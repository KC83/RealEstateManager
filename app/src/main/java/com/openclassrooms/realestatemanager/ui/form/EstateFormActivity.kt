package com.openclassrooms.realestatemanager.ui.form

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.dummy.DropdownItem
import com.openclassrooms.realestatemanager.utils.Utils
import java.util.*
import kotlin.math.roundToInt


class EstateFormActivity : AppCompatActivity() {
    private val CAMERA_REQUEST = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estate_form)

        // Set dropdown
        setDropdown(this, R.id.form_autocomplete_status, Utils.getStatus())
        setDropdown(this, R.id.form_autocomplete_agent, Utils.getAgent())
        setDropdown(this, R.id.form_autocomplete_type, Utils.getType())

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
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), CAMERA_REQUEST)
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
            if (checkInput()) {
                val replyIntent= Intent()
                val estate = Estate(0,0,0,0,"","", 0F,0F,0,0,0,"","","","","")
                replyIntent.putExtra(EXTRA_REPLY, estate)
            } else {
                Toast.makeText(this, "Vous devez remplir tous les champs pour enregistrer ce bien.",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setDropdown(context: Context, id: Int, values: List<DropdownItem>) {
        val adapter = DropdownAdapter(context, values)
        val dropdown = findViewById<AutoCompleteTextView>(id)
        dropdown.setAdapter(adapter)
    }
    private fun setImageBottomSheet() {
        val imageView = findViewById<ImageView>(R.id.form_image_view_image)
        val imageBottomSheetDialogFragment = ImageBottomSheetDialogFragment(imageView)
        imageBottomSheetDialogFragment.show(supportFragmentManager, "ImageBottomSheetDialogFragment")
    }
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

    private fun checkInput(): Boolean {
        return true
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}