package com.openclassrooms.realestatemanager.ui.form

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.utils.Utils
import java.util.*
import kotlin.collections.ArrayList


class ImageViewPagerAdapter(var context: Context, var images: MutableList<EstateImage>, private val activity: EstateFormActivity, private val formView: View, private val viewPager: ViewPager) : PagerAdapter() {
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    init {
        inflater = LayoutInflater.from(context);
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = inflater.inflate(R.layout.image_view_pager, container, false)
        val imageView: ImageView = itemView.findViewById<View>(R.id.image_view_pager_image_view) as ImageView
        imageView.setImageURI(Uri.parse(images[position].uri))

        if (position == 0) {
            imageView.scaleType = ImageView.ScaleType.CENTER
        } else {
            imageView.scaleType = ImageView.ScaleType.FIT_XY
        }

        imageView.setOnClickListener { v ->
            if (ContextCompat.checkSelfPermission(v.context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(v.context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(v.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Utils.setImageBottomSheet(images,viewPager,activity,formView,activity.contentResolver,activity.supportFragmentManager)
                } else {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), Utils.CAMERA_REQUEST)
                }
            } else {
                Utils.setImageBottomSheet(images,viewPager,activity,formView,activity.contentResolver,activity.supportFragmentManager)
            }
        }

        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}