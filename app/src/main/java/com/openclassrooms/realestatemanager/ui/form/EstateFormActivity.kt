package com.openclassrooms.realestatemanager.ui.form

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Agent
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.data.model.Type
import com.openclassrooms.realestatemanager.domain.repository.RealEstateApplication
import com.openclassrooms.realestatemanager.utils.DropdownItem
import com.openclassrooms.realestatemanager.ui.viewmodel.*
import com.openclassrooms.realestatemanager.utils.Utils
import java.io.Serializable
import java.util.*
import kotlin.math.roundToInt


class EstateFormActivity : AppCompatActivity() {
    private val statusViewModel: StatusViewModel by viewModels {
        StatusViewModelFactory((application as RealEstateApplication).statusRepository)
    }
    private val agentViewModel: AgentViewModel by viewModels {
        AgentViewModelFactory((application as RealEstateApplication).agentRepository)
    }
    private val typeViewModel: TypeViewModel by viewModels {
        TypeViewModelFactory((application as RealEstateApplication).typeRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estate_form)

        // Set dropdown
        setDropdown(this, R.id.form_autocomplete_status, getListDropdownItem(Utils.DROPDOWN_STATUS))
        setDropdown(this, R.id.form_autocomplete_agent, getListDropdownItem(Utils.DROPDOWN_AGENT))
        setDropdown(this, R.id.form_autocomplete_type, getListDropdownItem(Utils.DROPDOWN_TYPE))

        // Add the sign "$" to the price
        val priceSlider = findViewById<Slider>(R.id.form_slider_price)
        priceSlider.setLabelFormatter { value: Float ->
            return@setLabelFormatter "$${value.roundToInt()}"
        }

        // Add action when add image on click
        val image = findViewById<ImageView>(R.id.form_image_view_image)
        image.setOnClickListener { view ->
            if (ContextCompat.checkSelfPermission(view.context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(view.context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(view.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    setImageBottomSheet()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), Utils.CAMERA_REQUEST)
                }
            } else {
                setImageBottomSheet()
            }
        }

        // Set datePicker for the insert date
        setDate(R.id.form_text_input_edit_insert_date)
        // Set datePicker for the sale date
        setDate(R.id.form_text_input_edit_sale_date)

        val btnSave = findViewById<Button>(R.id.form_btn_save)
        btnSave.setOnClickListener { view ->
            val estate = Estate(
                    status = getStringValue(Utils.AUTOCOMPLETE_TEXT_VIEW,R.id.form_autocomplete_status),
                    type = getStringValue(Utils.AUTOCOMPLETE_TEXT_VIEW,R.id.form_autocomplete_type),
                    agent = getStringValue(Utils.AUTOCOMPLETE_TEXT_VIEW,R.id.form_autocomplete_agent),
                    insertDate = getStringValue(Utils.TEXT_INPUT_EDIT_TEXT,R.id.form_text_input_edit_insert_date),
                    saleDate = getStringValue(Utils.TEXT_INPUT_EDIT_TEXT,R.id.form_text_input_edit_sale_date),
                    price = getFloatValue(Utils.SLIDER, R.id.form_slider_price),
                    surface = getFloatValue(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_surface),
                    numberRooms = getIntValue(R.id.form_text_input_edit_rooms),
                    numberBathrooms = getIntValue(R.id.form_text_input_edit_bathrooms),
                    numberBedrooms = getIntValue(R.id.form_text_input_edit_bedrooms),
                    description = getStringValue(Utils.TEXT_INPUT_EDIT_TEXT,R.id.form_text_input_edit_description),
                    location = getStringValue(Utils.TEXT_INPUT_EDIT_TEXT,R.id.form_text_input_edit_location),
                    zipCode = getStringValue(Utils.TEXT_INPUT_EDIT_TEXT,R.id.form_text_input_edit_zip_code),
                    city = getStringValue(Utils.TEXT_INPUT_EDIT_TEXT,R.id.form_text_input_edit_city),
                    country = getStringValue(Utils.TEXT_INPUT_EDIT_TEXT,R.id.form_text_input_edit_country)
            )

            // Check if inputs are not empty
            if (checkInput(estate)) {
                // Check if image is not empty
                if (image.tag.toString().isNotEmpty()) {
                    val replyIntent= Intent()
                    replyIntent.putExtra(Utils.EXTRA_ESTATE, estate as Serializable)
                    replyIntent.putExtra(Utils.EXTRA_ESTATE_IMAGE, image.tag.toString())
                    setResult(Activity.RESULT_OK,replyIntent)
                    finish()
                } else {
                    Toast.makeText(this, getString(R.string.error_image),Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.error_input),Toast.LENGTH_LONG).show()
            }
        }
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
        if (inputType == Utils.DROPDOWN_AGENT) {
            agentViewModel.allAgents.observe(this, { allAgents ->
                allAgents.forEach { agent ->
                    list.add(DropdownItem(agent.fullName))
                }
            })
        } else if (inputType == Utils.DROPDOWN_STATUS) {
            statusViewModel.allStatus.observe(this, { allStatus ->
                allStatus.forEach { status ->
                    list.add(DropdownItem(status.name))
                }
            })
        } else if (inputType == Utils.DROPDOWN_TYPE) {
            typeViewModel.allTypes.observe(this, { allTypes ->
                allTypes.forEach { type ->
                    list.add(DropdownItem(type.name))
                }
            })
        }
        return list
    }

    /**
     * Add image bottom sheet
     */
    private fun setImageBottomSheet() {
        val imageView = findViewById<ImageView>(R.id.form_image_view_image)
        val imageBottomSheetDialogFragment = ImageBottomSheetDialogFragment(imageView, this.contentResolver)
        imageBottomSheetDialogFragment.show(supportFragmentManager, "ImageBottomSheetDialogFragment")
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

                    val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
                    val month: Int = calendar.get(Calendar.MONTH)
                    val year: Int = calendar.get(Calendar.YEAR)

                    val datePickerDialog = DatePickerDialog(this@EstateFormActivity, { view, year, monthOfYear, dayOfMonth ->
                        print(monthOfYear)

                        var goodDay: String = dayOfMonth.toString()
                        if (dayOfMonth < 10) {
                            goodDay = "0" + goodDay
                        }

                        val goodMonth: Int = monthOfYear+1
                        var goodMonthStr: String = goodMonth.toString()
                        if (monthOfYear < 10) {
                            goodMonthStr = "0" + goodMonthStr
                        }

                        val date: String = String.format("%s/%s/%s", goodDay, goodMonthStr, year)
                        dateInputEditText.setText(date)
                    }, year, month, day)
                    datePickerDialog.show()
                }
            }

            v?.onTouchEvent(event) ?: true
        }
    }

    //### GET VALUES ###
    private fun getStringValue(inputType: String, @IdRes id: Int): String {
        var value = ""
        when (inputType) {
            Utils.AUTOCOMPLETE_TEXT_VIEW -> {
                val input = findViewById<AutoCompleteTextView>(id)
                value = input.text.toString()
            }
            Utils.TEXT_INPUT_EDIT_TEXT -> {
                val input = findViewById<TextInputEditText>(id)
                value = input.text.toString()
            }
        }

        return value
    }
    private fun getIntValue(@IdRes id: Int): Int {
        val input = findViewById<TextInputEditText>(id)
        val valueStr = input.text.toString()
        if (!valueStr.isEmpty()) {
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

    /**
     * Check if every input is not empty
     */
    private fun checkInput(estate: Estate): Boolean {
        // Remove all the errors
        setError(Utils.AUTOCOMPLETE_TEXT_VIEW, R.id.form_autocomplete_status, null)
        setError(Utils.AUTOCOMPLETE_TEXT_VIEW, R.id.form_autocomplete_type, null)
        setError(Utils.AUTOCOMPLETE_TEXT_VIEW, R.id.form_autocomplete_agent, null)
        setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_insert_date, null)
        setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_description, null)
        setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_location, null)
        setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_zip_code, null)
        setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_city, null)
        setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_country, null)

        when {
            estate.status.isEmpty() -> {
                setError(Utils.AUTOCOMPLETE_TEXT_VIEW, R.id.form_autocomplete_status, getString(R.string.form_error))
                return false
            }
            estate.agent.isEmpty() -> {
                setError(Utils.AUTOCOMPLETE_TEXT_VIEW, R.id.form_autocomplete_agent, getString(R.string.form_error))
                return false
            }
            estate.insertDate.isEmpty() -> {
                setError(Utils.TEXT_INPUT_EDIT_TEXT, R.id.form_text_input_edit_insert_date, getString(R.string.form_error))
                return false
            }
            estate.type.isEmpty() -> {
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
            else -> return true
        }
    }
    /**
     * Set error to input
     */
    private fun setError(inputType: String, id: Int, error: String?): Unit {
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
}