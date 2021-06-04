package com.openclassrooms.realestatemanager.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.ui.form.ImageViewPagerAdapter
import com.openclassrooms.realestatemanager.utils.ChipItem
import com.openclassrooms.realestatemanager.utils.InternetManager
import com.openclassrooms.realestatemanager.utils.InternetManagerImpl
import com.openclassrooms.realestatemanager.utils.Utils
import com.squareup.picasso.Picasso
import java.io.File

class EstateDetailFragment : Fragment() {
    private var item: EstateModel? = null
    private val internetManager: InternetManager by lazy {
        InternetManagerImpl(this.requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(Utils.EXTRA_ESTATE_MODEL)) {
                item = it.getSerializable(Utils.EXTRA_ESTATE_MODEL) as EstateModel
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.estate_detail, container, false)
        item?.let {
            rootView.findViewById<TextView>(R.id.detail_status).text = it.status.name
            rootView.findViewById<TextView>(R.id.detail_price).text = it.estate.price.toString().plus("$")
            rootView.findViewById<TextView>(R.id.detail_type).text = it.type.name
            rootView.findViewById<TextView>(R.id.detail_surface).text = it.estate.surface.toString().plus(" m²")
            rootView.findViewById<TextView>(R.id.detail_rooms).text = it.estate.numberRooms.toString().plus(" pièce(s)")
            rootView.findViewById<TextView>(R.id.detail_bedrooms).text = it.estate.numberBedrooms.toString().plus(" chambre(s)")
            rootView.findViewById<TextView>(R.id.detail_bathrooms).text = it.estate.numberBathrooms.toString().plus(" salle(s) de bain")
            rootView.findViewById<TextView>(R.id.detail_description).text = it.estate.description
            rootView.findViewById<TextView>(R.id.detail_location).text = it.estate.location
            rootView.findViewById<TextView>(R.id.detail_zip_code).text = it.estate.zipCode
            rootView.findViewById<TextView>(R.id.detail_city).text = it.estate.city
            rootView.findViewById<TextView>(R.id.detail_country).text = it.estate.country
            rootView.findViewById<TextView>(R.id.detail_insert_date).text =  String().plus("Ajouté le ").plus(it.estate.insertDate)
            if (it.estate.saleDate.isNotEmpty()) {
                rootView.findViewById<TextView>(R.id.detail_sale_date).text =  String().plus("Vendu le ").plus(it.estate.saleDate)
            } else {
                rootView.findViewById<TextView>(R.id.detail_sale_date).text = String().plus("Non vendu")
            }
            rootView.findViewById<TextView>(R.id.detail_agent).text = String().plus("Agent : ").plus(it.agent.fullName)

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
                it.images.add(EstateImage(estateId = it.estate.id, uri = Utils.getUriAddImage(this.requireContext()).toString(), name = "Pas d'image"))
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