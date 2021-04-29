package com.openclassrooms.realestatemanager.utils

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.data.model.Place
import com.openclassrooms.realestatemanager.ui.form.EstateFormActivity
import com.openclassrooms.realestatemanager.ui.form.ImageBottomSheetDialogFragment
import java.io.*
import java.util.*

class Utils {
    companion object {
        /**
         * ACTIVITY REQUEST
         */
        const val FORM_ACTIVITY_REQUEST = 100

        /**
         * PERMISSION REQUEST
         */
        const val CAMERA_REQUEST = 100
        const val GALLERY_REQUEST = 200

        /**
         * ACTIVITY EXTRA
         */
        const val EXTRA_ESTATE = "EXTRA_ESTATE"
        const val EXTRA_ESTATE_MODEL = "EXTRA_ESTATE_MODEL"
        const val EXTRA_ESTATE_IMAGE = "EXTRA_ESTATE_IMAGE"
        const val EXTRA_IMAGE = "EXTRA_IMAGE"
        const val EXTRA_PLACE = "EXTRA_PLACE"
        const val EXTRA_ESTATE_PLACE = "EXTRA_ESTATE_PLACE"

        /**
         * FORM CONSTANTS
         */
        const val TEXT_INPUT_EDIT_TEXT = "TEXT_INPUT_EDIT_TEXT"
        const val AUTOCOMPLETE_TEXT_VIEW = "AUTOCOMPLETE_TEXT_VIEW"
        const val SLIDER = "SLIDER"

        const val DROPDOWN_AGENT = "AGENT"
        const val DROPDOWN_STATUS = "STATUS"
        const val DROPDOWN_TYPE = "TYPE"

        // Method to save an image to internal storage
        fun saveImageToInternalStorage(bitmap: Bitmap, applicationContext: Context?): Uri {
            // Get the context wrapper instance
            val wrapper = ContextWrapper(applicationContext)

            // Initializing a new file
            // The bellow line return a directory in internal storage
            var file = wrapper.getDir("images", Context.MODE_PRIVATE)

            // Create a file to save the image
            file = File(file, "${UUID.randomUUID()}.jpg")

            try {
                // Get the file output stream
                val stream: OutputStream = FileOutputStream(file)

                // Compress bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

                // Flush the stream
                stream.flush()

                // Close stream
                stream.close()
            } catch (e: IOException){ // Catch the exception
                e.printStackTrace()
            }

            // Return the saved image uri
            return Uri.parse(file.absolutePath)
        }

        // Uri for a drawable (button add image)
        fun getUriAddImage(context: Context): Uri {
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://"
                    + context.resources.getResourcePackageName(R.drawable.ic_add_photo)
                    + '/'
                    + context.resources.getResourceTypeName(R.drawable.ic_add_photo)
                    + '/'
                    + context.resources.getResourceEntryName(R.drawable.ic_add_photo))
        }

        // Set the image bottom sheet
        fun setImageBottomSheet(images: MutableList<EstateImage>, viewPager: ViewPager, estateFormActivity: EstateFormActivity?, view: View, contentResolver: ContentResolver, supportFragmentManager: FragmentManager) {
            val imageBottomSheetDialogFragment = ImageBottomSheetDialogFragment(viewPager, contentResolver, estateFormActivity, view, images)
            imageBottomSheetDialogFragment.show(supportFragmentManager, "ImageBottomSheetDialogFragment")
        }

        // Add programmatically chip
        fun addChip(context: Context, chipGroup: ChipGroup, place: Place, isEstateDetailActivity: Boolean = false, isChecked: Boolean = false) {
            val chip = Chip(context)
            chip.id = place.id.toInt()
            chip.text = place.name
            if (place.logo != 0) {
                chip.chipIcon = ContextCompat.getDrawable(context, place.logo)
            }
            chip.isClickable = true
            chip.isCheckable = true
            chip.isCheckedIconVisible = true
            chip.isFocusable = true
            chip.isCheckedIconVisible = false
            chip.chipBackgroundColor = ColorStateList.valueOf(Color.LTGRAY)

            if (!isEstateDetailActivity) {
                // If it's not estateDetailActivity, set onChecked action
                chip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorSecondary))
                        chip.isCloseIconVisible = true
                    } else {
                        chip.chipBackgroundColor = ColorStateList.valueOf(Color.LTGRAY)
                        chip.isCloseIconVisible = false
                    }
                }
            } else {
                // If estateDetailActivity, set background color
                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorSecondary))
                chip.isCloseIconVisible = false
            }

            if (isChecked) {
                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorSecondary))
                chip.isCloseIconVisible = true
                chip.isChecked = true
            }

            chipGroup.addView(chip)
        }

        fun getMapsURL(context: Context, estate: Estate, googleApi: String): String {
            var address: String = ""
            if (estate.location.isNotEmpty()) {
                address += estate.location.replace(' ', '+')
            }
            if (estate.zipCode.isNotEmpty()) {
                if(address.isNotEmpty()) {
                    address += "+";
                }
                address += estate.zipCode.replace(' ', '+')
            }
            if (estate.city.isNotEmpty()) {
                if(address.isNotEmpty()) {
                    address += "+";
                }
                address += estate.city.replace(' ', '+')
            }
            if (estate.country.isNotEmpty()) {
                if(address.isNotEmpty()) {
                    address += "+";
                }
                address += estate.country.replace(' ', '+')
            }

            var latLong = ""

            val coder = Geocoder(context)
            try {
                val addresses: ArrayList<Address> = coder.getFromLocationName(address, 50) as ArrayList<Address>
                for (add in addresses) {
                    val longitude: Double = add.longitude
                    val latitude: Double = add.latitude

                    val color = "green"
                    latLong = "&markers=color:"+color+"%7C"+latitude+","+longitude
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return "https://maps.googleapis.com/maps/api/staticmap?center=" + address + "&zoom=18&size=600x300&maptype=roadmap&key=" + googleApi+latLong
        }
    }
}