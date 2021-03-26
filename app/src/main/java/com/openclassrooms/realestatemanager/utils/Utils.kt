package com.openclassrooms.realestatemanager.utils

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.EstateImage
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
        const val EXTRA_ESTATE_IMAGE = "EXTRA_ESTATE_IMAGE"

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
        fun setImageBottomSheet(images: MutableList<EstateImage>, viewPager: ViewPager, activity: EstateFormActivity, view: View, contentResolver: ContentResolver, supportFragmentManager: FragmentManager) {
            val imageBottomSheetDialogFragment = ImageBottomSheetDialogFragment(viewPager, contentResolver,activity,view,images)
            imageBottomSheetDialogFragment.show(supportFragmentManager, "ImageBottomSheetDialogFragment")
        }
    }
}