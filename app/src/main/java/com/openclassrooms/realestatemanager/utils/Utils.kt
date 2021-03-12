package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.openclassrooms.realestatemanager.R
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
    }
}