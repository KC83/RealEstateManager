package com.openclassrooms.realestatemanager.utils

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.data.model.EstatePlace
import com.openclassrooms.realestatemanager.ui.detail.EstateDetailFragment
import com.openclassrooms.realestatemanager.ui.form.EstateFormActivity
import com.openclassrooms.realestatemanager.ui.form.ImageBottomSheetDialogFragment
import com.openclassrooms.realestatemanager.ui.list.EstateListActivity
import com.openclassrooms.realestatemanager.ui.viewmodel.EstateImageViewModel
import com.openclassrooms.realestatemanager.ui.viewmodel.EstatePlaceViewModel
import com.openclassrooms.realestatemanager.ui.viewmodel.EstateViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.Serializable
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
        const val LOCATION_REQUEST = 300

        /**
         * ACTIVITY EXTRA
         */
        const val EXTRA_ESTATE = "EXTRA_ESTATE"
        const val EXTRA_ESTATE_MODEL = "EXTRA_ESTATE_MODEL"
        const val EXTRA_ESTATE_IMAGE = "EXTRA_ESTATE_IMAGE"
        const val EXTRA_IMAGE = "EXTRA_IMAGE"
        const val EXTRA_PLACE = "EXTRA_PLACE"
        const val EXTRA_ESTATE_PLACE = "EXTRA_ESTATE_PLACE"
        const val EXTRA_LAT = "EXTRA_LAT"
        const val EXTRA_LNG = "EXTRA_LNG"
        const val EXTRA_COME_FROM_MAP = "EXTRA_COME_FROM_MAP"
        const val EXTRA_TWO_PANE = "EXTRA_TWO_PANE"

        /**
         * FORM CONSTANTS
         */
        const val TEXT_INPUT_EDIT_TEXT = "TEXT_INPUT_EDIT_TEXT"
        const val AUTOCOMPLETE_TEXT_VIEW = "AUTOCOMPLETE_TEXT_VIEW"
        const val SLIDER = "SLIDER"
        const val DROPDOWN_AGENT = "AGENT"
        const val DROPDOWN_STATUS = "STATUS"
        const val DROPDOWN_TYPE = "TYPE"

        //### UTILS FUNCTIONS ###//

        /**
         * Method to save an estate
         */
        fun saveEstate(data: Intent?, lifecycleOwner: LifecycleOwner, estateViewModel: EstateViewModel, estateImageViewModel: EstateImageViewModel, estatePlaceViewModel: EstatePlaceViewModel) {
            val estate: Estate = data?.getSerializableExtra(Utils.EXTRA_ESTATE) as Estate
            var saved = false

            estateViewModel.estateId.observe(lifecycleOwner, { estateId ->
                if (!saved) {
                    // Images
                    val estateImages: MutableList<EstateImage> = data.getSerializableExtra(EXTRA_ESTATE_IMAGE) as MutableList<EstateImage>
                    val images: MutableList<EstateImage> = data.getSerializableExtra(EXTRA_IMAGE) as MutableList<EstateImage>
                    val imagesToKeep: MutableList<Long> = mutableListOf()

                    // Remove first image, it's the image for adding new images
                    estateImages.drop(1).forEach { image ->
                        estateImageViewModel.insert(EstateImage(id = image.id, estateId = estateId, uri = image.uri, name = image.name))
                        if (image.id > 0) {
                            imagesToKeep.add(image.id)
                        }
                    }
                    // Delete estateImage if removed
                    images.forEach { estateImage ->
                        if (!imagesToKeep.contains(estateImage.id)) {
                            estateImageViewModel.delete(estateImage)
                        }
                    }

                    // Places
                    val placeIds: MutableList<Int>? = data.getSerializableExtra(EXTRA_PLACE) as MutableList<Int>?
                    val estatePlaces: MutableList<EstatePlace>? = data.getSerializableExtra(EXTRA_ESTATE_PLACE) as MutableList<EstatePlace>?
                    val estatePlacesToKeep: MutableList<EstatePlace> = mutableListOf()
                    placeIds?.forEach { placeId ->
                        // Get estate place already saved
                        val estatePlace: EstatePlace? = estatePlaces?.firstOrNull { estatePlace -> estatePlace.placeId == placeId.toLong() }
                        if (estatePlace != null) {
                            estatePlacesToKeep.add(estatePlace)
                        }

                        estatePlaceViewModel.insert(EstatePlace(id = estatePlace?.id ?: 0L, estateId = estateId, placeId = placeId.toLong()))
                    }

                    // Delete estatePlace if not checked
                    estatePlaces?.forEach { estatePlace ->
                        if (!estatePlacesToKeep.contains(estatePlace)) {
                            estatePlaceViewModel.delete(estatePlace)
                        }
                    }

                    saved = true

                    // Toast when the estate is saved
                    Toast.makeText(lifecycleOwner as Context, R.string.form_save, Toast.LENGTH_LONG).show()
                }
            })
            estateViewModel.insert(estate)
        }

        /**
         * Method to save an image to internal storage
         */
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

        /**
         * Uri for a drawable (button add image)
         */
        fun getUriAddImage(context: Context): Uri {
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://"
                    + context.resources.getResourcePackageName(R.drawable.ic_add_photo)
                    + '/'
                    + context.resources.getResourceTypeName(R.drawable.ic_add_photo)
                    + '/'
                    + context.resources.getResourceEntryName(R.drawable.ic_add_photo))
        }

        /**
         * Set the image bottom sheet
         */
        fun setImageBottomSheet(images: MutableList<EstateImage>, viewPager: ViewPager, estateFormActivity: EstateFormActivity?, view: View, contentResolver: ContentResolver, supportFragmentManager: FragmentManager) {
            val imageBottomSheetDialogFragment = ImageBottomSheetDialogFragment(viewPager, contentResolver, estateFormActivity, view, images)
            imageBottomSheetDialogFragment.show(supportFragmentManager, "ImageBottomSheetDialogFragment")
        }

        /**
         * Add programmatically chip
         */
        fun addChip(context: Context, chipGroup: ChipGroup, chipItem: ChipItem, isEstateDetailActivity: Boolean = false, isChecked: Boolean = false) {
            val chip = Chip(context)
            chip.id = chipItem.id
            chip.text = chipItem.name
            if (chipItem.logo != 0) {
                chip.chipIcon = ContextCompat.getDrawable(context, chipItem.logo)
            }
            chip.isClickable = true
            chip.isCheckable = true
            chip.isCheckedIconVisible = true
            chip.isFocusable = true
            chip.isCheckedIconVisible = false
            chip.chipBackgroundColor = ColorStateList.valueOf(Color.LTGRAY)

            if (!isEstateDetailActivity) {
                // If it's not estateDetailActivity, set onChecked action
                chip.setOnCheckedChangeListener { _, chipIsChecked ->
                    if (chipIsChecked) {
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

        /**
         * Get complete address of a estate
         */
        private fun getAddress(estate: Estate): String {
            var address = ""
            if (estate.location.isNotEmpty()) {
                address += estate.location.replace(' ', '+')
            }
            if (estate.zipCode.isNotEmpty()) {
                if(address.isNotEmpty()) {
                    address += "+"
                }
                address += estate.zipCode.replace(' ', '+')
            }
            if (estate.city.isNotEmpty()) {
                if(address.isNotEmpty()) {
                    address += "+"
                }
                address += estate.city.replace(' ', '+')
            }
            if (estate.country.isNotEmpty()) {
                if(address.isNotEmpty()) {
                    address += "+"
                }
                address += estate.country.replace(' ', '+')
            }

            return address
        }

        /**
         * Get the google Map of a estate
         */
        fun getMapsURL(context: Context, estate: Estate, googleApi: String): String {
            val address = getAddress(estate)
            var marker = ""
            val latLng: LatLng? = getLatLngFromAddress(context, estate)
            if (latLng != null) {
                marker = "&markers=color:green%7C"+latLng.latitude+","+latLng.longitude
            }

            return "https://maps.googleapis.com/maps/api/staticmap?center=$address&zoom=18&size=600x300&maptype=roadmap&key=$googleApi$marker"
        }

        /**
         * Get latitude an longitude from a address
         */
        fun getLatLngFromAddress(context: Context, estate: Estate): LatLng? {
            val address = getAddress(estate)
            var latLng: LatLng? = null
            val coder = Geocoder(context)
            try {
                val addresses: ArrayList<Address> = coder.getFromLocationName(address, 50) as ArrayList<Address>
                for (add in addresses) {
                    latLng = LatLng(add.latitude, add.longitude)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return latLng
        }
    }
}