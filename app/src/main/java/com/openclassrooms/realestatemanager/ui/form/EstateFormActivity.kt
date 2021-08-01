package com.openclassrooms.realestatemanager.ui.form

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.Slider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.*
import com.openclassrooms.realestatemanager.domain.RealEstateApplication
import com.openclassrooms.realestatemanager.ui.viewmodel.*
import com.openclassrooms.realestatemanager.utils.ChipItem
import com.openclassrooms.realestatemanager.utils.DropdownItem
import com.openclassrooms.realestatemanager.utils.Utils
import com.squareup.picasso.Picasso
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class EstateFormActivity : AppCompatActivity() {
    private var setupForm = false
    private var estateModel: EstateModel? = null
    private val estateImages: MutableList<EstateImage> = mutableListOf()
    // Images already saved, use to know if a image is deleted
    private val images: MutableList<EstateImage> = mutableListOf()

    private val statusViewModel: StatusViewModel by viewModels {
        StatusViewModelFactory((application as RealEstateApplication).statusRepository)
        StatusViewModelFactory((application as RealEstateApplication).statusRepository)
    }
    private val agentViewModel: AgentViewModel by viewModels {
        AgentViewModelFactory((application as RealEstateApplication).agentRepository)
    }
    private val typeViewModel: TypeViewModel by viewModels {
        TypeViewModelFactory((application as RealEstateApplication).typeRepository)
    }
    private val placeViewModel: PlaceViewModel by viewModels {
        PlaceViewModelFactory((application as RealEstateApplication).placeRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estate_form)

        estateModel = intent.getSerializableExtra(Utils.EXTRA_ESTATE_MODEL) as EstateModel?

        // Add the sign "$" to the price
        val priceSlider = findViewById<Slider>(R.id.form_slider_price)
        priceSlider.setLabelFormatter { value: Float ->
            return@setLabelFormatter "$${value.roundToInt()}"
        }

        // Set datePicker for the insert date
        setDate(R.id.form_text_input_edit_insert_date)
        // Set datePicker for the sale date
        setDate(R.id.form_text_input_edit_sale_date)

        // Set images
        setImageRecyclerView(View(this))

        // Set form
        setupForm()
    }

    //### SETUP FORM ###
    /**
     * Add dropdown values
     */
    private fun setDropdown(context: Context, @IdRes id: Int, values: List<DropdownItem>) {
        val adapter = DropdownAdapter(context, values)
        val dropdown = findViewById<AutoCompleteTextView>(id)
        dropdown.setAdapter(adapter)
    }
    /**
     * Get the dropdown items
     */
    private fun getListDropdownItem(inputType: String): List<DropdownItem> {
        val list: MutableList<DropdownItem> = ArrayList()
        when (inputType) {
            Utils.DROPDOWN_AGENT -> {
                agentViewModel.allAgents.observe(this, { allAgents ->
                    allAgents.forEach { agent ->
                        list.add(DropdownItem(agent.fullName))
                    }
                })
            }
            Utils.DROPDOWN_STATUS -> {
                statusViewModel.allStatus.observe(this, { allStatus ->
                    allStatus.forEach { status ->
                        list.add(DropdownItem(status.name))
                    }
                })
            }
            Utils.DROPDOWN_TYPE -> {
                typeViewModel.allTypes.observe(this, { allTypes ->
                    allTypes.forEach { type ->
                        list.add(DropdownItem(type.name))
                    }
                })
            }
        }
        return list
    }
    /**
     * Setup date input
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setDate(id: Int) {
        val dateInputEditText = findViewById<TextInputEditText>(id)
        dateInputEditText.inputType = InputType.TYPE_NULL
        dateInputEditText.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    val calendar: Calendar = Calendar.getInstance()

                    var selectedDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
                    var selectedMonth: Int = calendar.get(Calendar.MONTH)
                    var selectedYear: Int = calendar.get(Calendar.YEAR)

                    val date = getStringValue(id)
                    if (date.isNotEmpty()) {
                        selectedDay = date.substring(0, 2).toInt()
                        selectedMonth = date.substring(3, 5).toInt() - 1
                        selectedYear = date.substring(6, 10).toInt()
                    }

                    val datePickerDialog = DatePickerDialog(this@EstateFormActivity, { _, year, monthOfYear, dayOfMonth ->
                        var goodDay: String = dayOfMonth.toString()
                        if (dayOfMonth < 10) {
                            goodDay = "0$goodDay"
                        }

                        val goodMonth: Int = monthOfYear + 1
                        var goodMonthStr: String = goodMonth.toString()
                        if (monthOfYear < 10) {
                            goodMonthStr = "0$goodMonthStr"
                        }

                        dateInputEditText.setText(String.format("%s/%s/%s", goodDay, goodMonthStr, year))
                    }, selectedYear, selectedMonth, selectedDay)
                    datePickerDialog.show()
                }
            }

            v?.onTouchEvent(event) ?: true
        }
    }
    /**
     * Setup image recycler view
     */
    private fun setImageRecyclerView(view: View) {
        if(estateImages.size == 0) {
            estateImages.add(EstateImage(estateId = 0, uri = Utils.getUriAddImage(this).toString(), name = ""))
        }

        val pager = this.findViewById<ViewPager>(R.id.form_image_view_pager)
        val imageViewPagerAdapter = ImageViewPagerAdapter(this, estateImages, this, view, pager)

        pager.adapter = imageViewPagerAdapter

        val tabLayout = this.findViewById<TabLayout>(R.id.form_tab_layout)
        tabLayout.setupWithViewPager(pager, true)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position != null) {
                    val imageDescription: TextView = findViewById(R.id.form_image_description)
                    val deleteImageButton: ImageButton = findViewById(R.id.form_delete_image)
                    val editImageButton: ImageButton = findViewById(R.id.form_edit_image)

                    if (estateImages[tab.position].name.isNotEmpty()) {
                        // Image description
                        imageDescription.text = estateImages[tab.position].name

                        // Delete button
                        deleteImageButton.visibility = View.VISIBLE
                        deleteImageButton.setOnClickListener {
                            deleteImage(tab.position)
                        }

                        // Edit button
                        editImageButton.visibility = View.VISIBLE
                        editImageButton.setOnClickListener {
                            setAlertDialog(tab.position)
                        }
                    } else {
                        imageDescription.text = ""
                        deleteImageButton.visibility = View.INVISIBLE
                        editImageButton.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    /**
     * Set values of the estate
     */
    private fun setValues(estateModel: EstateModel) {
        // Autocomplete
        findViewById<AutoCompleteTextView>(R.id.form_autocomplete_type).setText(estateModel.type.name, false)
        findViewById<AutoCompleteTextView>(R.id.form_autocomplete_agent).setText(estateModel.agent.fullName, false)
        findViewById<AutoCompleteTextView>(R.id.form_autocomplete_status).setText(estateModel.status.name, false)

        // TextInputEditText
        findViewById<TextInputEditText>(R.id.form_text_input_edit_insert_date).setText(estateModel.estate.insertDate)
        findViewById<TextInputEditText>(R.id.form_text_input_edit_sale_date).setText(estateModel.estate.saleDate)
        findViewById<TextInputEditText>(R.id.form_text_input_edit_surface).setText(estateModel.estate.surface.toString())
        findViewById<TextInputEditText>(R.id.form_text_input_edit_rooms).setText(estateModel.estate.numberRooms.toString())
        findViewById<TextInputEditText>(R.id.form_text_input_edit_bathrooms).setText(estateModel.estate.numberBathrooms.toString())
        findViewById<TextInputEditText>(R.id.form_text_input_edit_bedrooms).setText(estateModel.estate.numberBedrooms.toString())
        findViewById<TextInputEditText>(R.id.form_text_input_edit_description).setText(estateModel.estate.description)
        findViewById<TextInputEditText>(R.id.form_text_input_edit_location).setText(estateModel.estate.location)
        findViewById<TextInputEditText>(R.id.form_text_input_edit_zip_code).setText(estateModel.estate.zipCode)
        findViewById<TextInputEditText>(R.id.form_text_input_edit_city).setText(estateModel.estate.city)
        findViewById<TextInputEditText>(R.id.form_text_input_edit_country).setText(estateModel.estate.country)

        // Slider
        findViewById<Slider>(R.id.form_slider_price).value = estateModel.estate.price

        // Images
        if (estateModel.images.size > 0) {
            estateModel.images.forEach { image ->
                estateImages.add(image)
                images.add(image)
            }

            val pager = this.findViewById<ViewPager>(R.id.form_image_view_pager)
            val imageViewPagerAdapter = ImageViewPagerAdapter(this, estateImages, this, View(this), pager)
            pager.adapter = imageViewPagerAdapter
        }
    }
    /**
     * Set form
     */
    private fun setupForm() {
        if (!setupForm) {
            // Set dropdown
            setDropdown(this, R.id.form_autocomplete_status, getListDropdownItem(Utils.DROPDOWN_STATUS))
            setDropdown(this, R.id.form_autocomplete_agent, getListDropdownItem(Utils.DROPDOWN_AGENT))
            setDropdown(this, R.id.form_autocomplete_type, getListDropdownItem(Utils.DROPDOWN_TYPE))

            // Get chipGroup
            val chipGroup: ChipGroup = findViewById(R.id.form_chip_group)
            // Set chips for the points of interest
            placeViewModel.allPlaces.observe(this, { allPlaces ->
                allPlaces.forEach { place ->
                    var isChecked = false
                    if (estateModel != null) {
                        if (estateModel!!.estatePlaces.isNotEmpty()) {
                            estateModel!!.estatePlaces.forEach { estatePlace ->
                                if (estatePlace.placeId == place.id) {
                                    isChecked = true
                                }
                            }
                        }
                    }

                    val chipItem = ChipItem(place.id.toInt(), place.name, place.logo)
                    Utils.addChip(this, chipGroup, chipItem, false, isChecked)
                }
            })

            estateModel?.let { setValues(it) }

            val btnSave = findViewById<Button>(R.id.form_btn_save)
            btnSave.setOnClickListener {
                saveButton(estateModel, chipGroup)
            }

            setupForm = true
        }
    }

    //### GET VALUES ###
    private fun getStringValue(@IdRes id: Int): String {
        val input = findViewById<TextInputEditText>(id)
        return input.text.toString()
    }
    private fun getLongValue(@IdRes id: Int): Long {
        var value = 0L
        val input = findViewById<AutoCompleteTextView>(id)
        val inputValue = input.text.toString()

        when (id) {
            R.id.form_autocomplete_status -> {
                value = statusViewModel.getStatusByName(inputValue)?.id ?: 0L
            }
            R.id.form_autocomplete_agent -> {
                value = agentViewModel.getAgentByName(inputValue)?.id ?: 0L
            }
            R.id.form_autocomplete_type -> {
                value = typeViewModel.getTypeByName(inputValue)?.id ?: 0L
            }
        }

        return value
    }
    private fun getIntValue(@IdRes id: Int): Int {
        val input = findViewById<TextInputEditText>(id)
        val valueStr = input.text.toString()
        if (valueStr.isNotEmpty()) {
            return valueStr.toInt()
        }
        return 0
    }
    private fun getFloatValue(inputType: String, @IdRes id: Int): Float {
        var value = 0F
        when (inputType) {
            Utils.TEXT_INPUT_EDIT_TEXT -> {
                val input = findViewById<TextInputEditText>(id)
                val valueString = input.text.toString()
                if (valueString.isNotEmpty()) {
                    value = valueString.toFloat()
                }
            }
            Utils.SLIDER -> {
                val input = findViewById<Slider>(id)
                value = input.value
            }
        }

        return value
    }
    //##################

    /**
     * Action when save button is clicked
     */
    private fun saveButton(estateModel: EstateModel?, chipGroup: ChipGroup) {
        var estate = Estate(
                id = estateModel?.estate?.id ?: 0L,
                statusId = getLongValue(R.id.form_autocomplete_status),
                typeId = getLongValue(R.id.form_autocomplete_type),
                agentId = getLongValue(R.id.form_autocomplete_agent),
                insertDate = getStringValue(R.id.form_text_input_edit_insert_date),
                saleDate = getStringValue(R.id.form_text_input_edit_sale_date),
                price = getFloatValue(Utils.SLIDER, R.id.form_slider_price),
                surface = getFloatValue(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_surface),
                numberRooms = getIntValue(R.id.form_text_input_edit_rooms),
                numberBathrooms = getIntValue(R.id.form_text_input_edit_bathrooms),
                numberBedrooms = getIntValue(R.id.form_text_input_edit_bedrooms),
                description = getStringValue(R.id.form_text_input_edit_description),
                location = getStringValue(R.id.form_text_input_edit_location),
                zipCode = getStringValue(R.id.form_text_input_edit_zip_code),
                city = getStringValue(R.id.form_text_input_edit_city),
                country = getStringValue(R.id.form_text_input_edit_country)
        )

        // Check if inputs are not empty
        if (checkInput(estate)) {
            // Check if image is not empty
            if (estateImages.size > 1) {
                // Get mapUri
                var mapUriString = ""
                val src: String = Utils.getMapsURL(this, estate, getString(R.string.GOOGLE_API_KEY))
                Picasso.get()
                        .load(src)
                        .into(
                                object: com.squareup.picasso.Target {
                                    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                                        saveEstate(estateModel, estate, chipGroup)
                                    }
                                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                        if (bitmap != null) {
                                            val mapUri: Uri = Utils.saveImageToInternalStorage(bitmap,this@EstateFormActivity)
                                            mapUriString = mapUri.toString()
                                        }
                                        estate = estate.copy(map_uri = mapUriString)

                                        saveEstate(estateModel, estate, chipGroup)
                                    }
                                }
                        )
            } else {
                Toast.makeText(this, getString(R.string.error_image), Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.error_input), Toast.LENGTH_LONG).show()
        }
    }
    /**
     * Save estate
     */
    private fun saveEstate(estateModel: EstateModel?, estate: Estate, chipGroup: ChipGroup) {
        var estateToSave = estate

        val latLng: LatLng? = Utils.getLatLngFromAddress(this@EstateFormActivity, estateToSave)
        if (latLng != null) {
            estateToSave = estateToSave.copy(lat = latLng.latitude, lng = latLng.longitude)
        }

        val replyIntent= Intent()
        replyIntent.putExtra(Utils.EXTRA_ESTATE, estateToSave as Serializable)
        replyIntent.putExtra(Utils.EXTRA_ESTATE_IMAGE, estateImages as Serializable)
        replyIntent.putExtra(Utils.EXTRA_IMAGE, images as Serializable)
        replyIntent.putExtra(Utils.EXTRA_PLACE, chipGroup.checkedChipIds as Serializable)
        if (estateModel != null) {
            replyIntent.putExtra(Utils.EXTRA_ESTATE_PLACE, estateModel.estatePlaces as Serializable)
        }
        setResult(Activity.RESULT_OK, replyIntent)
        finish()
    }
    /**
     * Check if every input is not empty
     */
    private fun checkInput(estate: Estate): Boolean {
        // Remove all the errors
        setError(Utils.AUTOCOMPLETE_TEXT_VIEW, R.id.form_autocomplete_status, null)
        setError(Utils.AUTOCOMPLETE_TEXT_VIEW, R.id.form_autocomplete_type, null)
        setError(Utils.AUTOCOMPLETE_TEXT_VIEW, R.id.form_autocomplete_agent, null)
        setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_insert_date, null)
        setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_sale_date, null)
        setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_description, null)
        setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_location, null)
        setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_zip_code, null)
        setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_city, null)
        setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_country, null)

        val statusSale = statusViewModel.getStatusByName(getString(R.string.sale))
        val statusSaleId: Long = statusSale?.id ?: 0L

        when {
            estate.statusId == 0L-> {
                setError(Utils.AUTOCOMPLETE_TEXT_VIEW, R.id.form_autocomplete_status, getString(R.string.form_error))
                return false
            }
            estate.agentId == 0L -> {
                setError(Utils.AUTOCOMPLETE_TEXT_VIEW, R.id.form_autocomplete_agent, getString(R.string.form_error))
                return false
            }
            estate.insertDate.isEmpty() -> {
                setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_insert_date, getString(R.string.form_error))
                return false
            }
            estate.typeId == 0L -> {
                setError(Utils.AUTOCOMPLETE_TEXT_VIEW, R.id.form_autocomplete_type, getString(R.string.form_error))
                return false
            }
            estate.description.isEmpty() -> {
                setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_description, getString(R.string.form_error))
                return false
            }
            estate.location.isEmpty() -> {
                setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_location, getString(R.string.form_error))
                return false
            }
            estate.zipCode.isEmpty() -> {
                setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_zip_code, getString(R.string.form_error))
                return false
            }
            estate.city.isEmpty() -> {
                setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_city, getString(R.string.form_error))
                return false
            }
            estate.country.isEmpty() -> {
                setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_country, getString(R.string.form_error))
                return false
            }
            estate.statusId == statusSaleId && estate.saleDate.isEmpty() -> {
                setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_sale_date, getString(R.string.form_error))
                return false
            }
            else -> return true
        }
    }
    /**
     * Set error to input
     */
    private fun setError(inputType: String, id: Int, error: String?) {
        when (inputType) {
            Utils.AUTOCOMPLETE_TEXT_VIEW -> {
                val input = findViewById<AutoCompleteTextView>(id)
                input.error = error
            }
            Utils.TEXT_INPUT_EDIT_TEXT -> {
                val input = findViewById<TextInputEditText>(id)
                input.error = error
            }
        }
    }
    /**
     * Set AlertDialog to edit description of a image
     */
    private fun setAlertDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.add_description))
        builder.setMessage(getString(R.string.enter_description_image))

        val input = EditText(this)
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = lp
        input.hint = "Description"
        input.setText(estateImages[position].name)
        builder.setView(input)

        builder.setPositiveButton("Ok", null)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (input.text.isNotEmpty()) {
                estateImages[position].name = input.text.toString()
                setImageRecyclerView(View(this))

                Toast.makeText(this, getString(R.string.description_saved), Toast.LENGTH_LONG).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, getString(R.string.enter_description), Toast.LENGTH_LONG).show()
            }
        }
    }
    /**
     * Delete image
     */
    private fun deleteImage(position: Int) {
        estateImages.removeAt(position)
        setImageRecyclerView(View(this))
    }
}