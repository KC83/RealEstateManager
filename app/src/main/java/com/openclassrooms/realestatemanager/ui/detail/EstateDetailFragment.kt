package com.openclassrooms.realestatemanager.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.ui.form.EstateFormActivity
import com.openclassrooms.realestatemanager.ui.form.ImageViewPagerAdapter
import com.openclassrooms.realestatemanager.utils.ChipItem
import com.openclassrooms.realestatemanager.utils.tools.InternetManager
import com.openclassrooms.realestatemanager.utils.tools.InternetManagerImpl
import com.openclassrooms.realestatemanager.utils.Utils
import com.squareup.picasso.Picasso
import java.io.File

class EstateDetailFragment : Fragment() {
    private var item: EstateModel? = null
    private var comeFromMaps: Boolean = false
    private val internetManager: InternetManager by lazy {
        InternetManagerImpl(this.requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(Utils.EXTRA_ESTATE_MODEL)) {
                item = it.getSerializable(Utils.EXTRA_ESTATE_MODEL) as EstateModel
                comeFromMaps = it.getBoolean(Utils.EXTRA_COME_FROM_MAP,false)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.estate_detail, container, false)
        item?.let {
            rootView.findViewById<TextView>(R.id.detail_status).text = it.status.name
            rootView.findViewById<TextView>(R.id.detail_price).text = it.estate.price.toString().plus(" ").plus(getString(R.string.dollar))
            rootView.findViewById<TextView>(R.id.detail_type).text = it.type.name
            rootView.findViewById<TextView>(R.id.detail_surface).text = it.estate.surface.toString().plus(" ").plus(getString(R.string.meter))
            rootView.findViewById<TextView>(R.id.detail_rooms).text = it.estate.numberRooms.toString().plus(" ").plus(getString(R.string.rooms))
            rootView.findViewById<TextView>(R.id.detail_bedrooms).text = it.estate.numberBedrooms.toString().plus(" ").plus(getString(R.string.bedrooms))
            rootView.findViewById<TextView>(R.id.detail_bathrooms).text = it.estate.numberBathrooms.toString().plus(" ").plus(getString(R.string.bathrooms))
            rootView.findViewById<TextView>(R.id.detail_description).text = it.estate.description
            rootView.findViewById<TextView>(R.id.detail_location).text = it.estate.location
            rootView.findViewById<TextView>(R.id.detail_zip_code).text = it.estate.zipCode
            rootView.findViewById<TextView>(R.id.detail_city).text = it.estate.city
            rootView.findViewById<TextView>(R.id.detail_country).text = it.estate.country
            rootView.findViewById<TextView>(R.id.detail_insert_date).text =  String().plus(getString(R.string.add_the)).plus(" ").plus(it.estate.insertDate)
            if (it.estate.saleDate.isNotEmpty()) {
                rootView.findViewById<TextView>(R.id.detail_sale_date).text =  String().plus(getString(R.string.sale_the)).plus(" ").plus(it.estate.saleDate)
            } else {
                rootView.findViewById<TextView>(R.id.detail_sale_date).text = String().plus(getString(R.string.not_sold))
            }
            rootView.findViewById<TextView>(R.id.detail_agent).text = String().plus(getString(R.string.agent_fragment)).plus(" ").plus(it.agent.fullName)

            // Maps
            if (!internetManager.isConnected()) {
                // If Internet is not available, we get saved map
                if (it.estate.map_uri.isNotEmpty()) {
                    val file = File(it.estate.map_uri)
                    if (file.exists()) {
                        Picasso.get().load(file).into(rootView.findViewById<ImageView>(R.id.detail_map_image))
                    } else {
                        Picasso.get().load(R.drawable.ic_no_location).into(rootView.findViewById<ImageView>(R.id.detail_map_image))
                    }
                } else {
                    Picasso.get().load(R.drawable.ic_no_location).into(rootView.findViewById<ImageView>(R.id.detail_map_image))
                }
            }

            // Setup images
            if (it.images.size == 0) {
                it.images.add(EstateImage(estateId = it.estate.id, uri = Utils.getUriAddImage(this.requireContext()).toString(), name = getString(R.string.no_image)))
                rootView.findViewById<TextView>(R.id.detail_image_description).text = ""
            } else {
                rootView.findViewById<TextView>(R.id.detail_image_description).text = it.images[0].name
            }

            val pager = rootView.findViewById<ViewPager>(R.id.detail_image_view_pager)
            val imageViewPagerAdapter = ImageViewPagerAdapter(this.requireContext(), it.images, null, rootView, pager, false)

            pager.adapter = imageViewPagerAdapter

            val tabLayout = rootView.findViewById<TabLayout>(R.id.detail_tab_layout)
            tabLayout.setupWithViewPager(pager, true)
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab?.position != null) {
                        rootView.findViewById<TextView>(R.id.detail_image_description).text = it.images[tab.position].name
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}

            })

            // Setup places
            // Get chipGroup
            val titleChipGroup: TextView = rootView.findViewById(R.id.detail_title_chip_group)
            val chipGroup: ChipGroup = rootView.findViewById(R.id.detail_chip_group)

            if (it.places.isEmpty()) {
                titleChipGroup.visibility = View.INVISIBLE
            } else if(it.places.isNotEmpty()) {
                it.places.map { place ->
                    val chipItem = ChipItem(place.id.toInt(), place.name, place.logo)
                    Utils.addChip(this.requireContext(), chipGroup, chipItem, true)
                }
            }

            if (comeFromMaps) {
                rootView.findViewById<FloatingActionButton>(R.id.estate_detail_button_form).visibility = View.INVISIBLE
            } else {
                val estateModel = it
                rootView.findViewById<FloatingActionButton>(R.id.estate_detail_button_form).setOnClickListener {
                    val formIntent = Intent(this.context,EstateFormActivity::class.java)
                    formIntent.putExtra(Utils.EXTRA_ESTATE_MODEL, estateModel)
                    startActivityForResult(formIntent, Utils.FORM_ACTIVITY_REQUEST)
                }
            }
        }
        return rootView
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if(isVisibleToUser && isResumed) {
            onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!userVisibleHint) {
            return
        }

        item?.let {
            if (internetManager.isConnected()) {
                // If Internet is available, get the map
                val src: String = Utils.getMapsURL(this.requireContext(), it.estate, getString(R.string.GOOGLE_API_KEY))
                Picasso.get().load(src).into(activity?.findViewById(R.id.detail_map_image))
            }
        }
    }
}