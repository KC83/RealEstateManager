package com.openclassrooms.realestatemanager.ui.form

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import java.util.*


class ImageViewPagerAdapter(context: Context, images: IntArray?) : PagerAdapter() {
    var context: Context? = null
    var images: IntArray = intArrayOf()
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    init {
        this.context = context
        this.images = images!!
        inflater = LayoutInflater.from(context);
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = inflater.inflate(com.openclassrooms.realestatemanager.R.layout.image_view_pager, container, false)
        val imageView: ImageView = itemView.findViewById<View>(com.openclassrooms.realestatemanager.R.id.image_view_pager_image_view) as ImageView
        imageView.setImageResource(images[position])

        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}