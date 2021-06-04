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
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.utils.Utils


class ImageBottomSheetDialogFragment(private val viewPager: ViewPager, private var contentResolver: ContentResolver, private val estateFormActivity: EstateFormActivity?, private val viewForm: View, private val images: MutableList<EstateImage>) : BottomSheetDialogFragment() {
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
            // Open AlertDialog to set a description to the image
            setAlertDialog(requestCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setAlertDialog(requestCode: Int, data: Intent?) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.add_description))
        builder.setMessage(getString(R.string.enter_description_image))

        val input = EditText(requireContext())
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = lp
        input.hint = "Description"
        builder.setView(input)

        builder.setPositiveButton("Ok",null)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (input.text.isNotEmpty()) {
                // Save image
                saveImage(input, requestCode, data)

                Toast.makeText(requireContext(), getString(R.string.image_saved), Toast.LENGTH_LONG).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), getString(R.string.enter_description), Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun saveImage(input: EditText, requestCode: Int, data: Intent?) {
        // Save image
        if (requestCode == Utils.CAMERA_REQUEST) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val uri: Uri = Utils.saveImageToInternalStorage(imageBitmap, requireContext())
            images.add(EstateImage(estateId = 0, uri = uri.toString(), name = input.text.toString()))
            val imageViewPagerAdapter = ImageViewPagerAdapter(requireContext(), images, estateFormActivity, viewForm, viewPager)
            viewPager.adapter = imageViewPagerAdapter
        } else if (requestCode == Utils.GALLERY_REQUEST) {
            val dataUri: Uri? = data?.data

            if (data?.data != null) {
                val uri = if(Build.VERSION.SDK_INT < 28) {
                    val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, dataUri)
                    Utils.saveImageToInternalStorage(imageBitmap, requireContext())
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, dataUri!!)
                    val imageBitmap = ImageDecoder.decodeBitmap(source)
                    Utils.saveImageToInternalStorage(imageBitmap, requireContext())
                }

                images.add(EstateImage(estateId = 0, uri = uri.toString(), name = input.text.toString()))
                val imageViewPagerAdapter = ImageViewPagerAdapter(requireContext(), images, estateFormActivity, viewForm, viewPager)
                viewPager.adapter = imageViewPagerAdapter
            }
        }
    }
}