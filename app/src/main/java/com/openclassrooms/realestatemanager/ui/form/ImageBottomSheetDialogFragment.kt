package com.openclassrooms.realestatemanager.ui.form

import android.content.Intent
import android.graphics.Bitmap
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


class ImageBottomSheetDialogFragment(imageView: ImageView) : BottomSheetDialogFragment() {
    private val GALLERY_REQUEST = 1000
    private val CAMERA_REQUEST = 2000
    private val image: ImageView = imageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.image_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<AppCompatButton>(R.id.image_btn_camera).setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }
        view.findViewById<AppCompatButton>(R.id.image_btn_gallery).setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                val imageBitmap = data?.extras?.get("data") as Bitmap
                image.setImageBitmap(imageBitmap)
            } else if (requestCode == GALLERY_REQUEST) {
                val uri = data?.getData()
                image.setImageURI(uri)
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}