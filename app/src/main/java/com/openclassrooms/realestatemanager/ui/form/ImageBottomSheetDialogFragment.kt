package com.openclassrooms.realestatemanager.ui.form

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.Utils


class ImageBottomSheetDialogFragment(private val image: ImageView, private var contentResolver: ContentResolver) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.image_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<AppCompatButton>(R.id.image_btn_camera).setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, Utils.CAMERA_REQUEST)
        }
        view.findViewById<AppCompatButton>(R.id.image_btn_gallery).setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, Utils.GALLERY_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == Utils.CAMERA_REQUEST) {
                val imageBitmap = data?.extras?.get("data") as Bitmap
                val uri: Uri = Utils.saveImageToInternalStorage(imageBitmap, context)
                image.setImageURI(uri)
                image.tag = uri
            } else if (requestCode == Utils.GALLERY_REQUEST) {
                var uri = data?.getData()
                if (uri != null) {
                    if(Build.VERSION.SDK_INT < 28) {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        uri = Utils.saveImageToInternalStorage(imageBitmap, context)
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, uri)
                        val imageBitmap = ImageDecoder.decodeBitmap(source)
                        uri = Utils.saveImageToInternalStorage(imageBitmap, context)
                    }
                }

                image.setImageURI(uri)
                image.tag = uri
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}