package com.openclassrooms.realestatemanager.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.data.model.EstatePlace
import com.openclassrooms.realestatemanager.domain.RealEstateApplication
import com.openclassrooms.realestatemanager.ui.form.EstateFormActivity
import com.openclassrooms.realestatemanager.ui.list.EstateListActivity
import com.openclassrooms.realestatemanager.ui.viewmodel.*
import com.openclassrooms.realestatemanager.utils.Utils

class EstateDetailActivity : AppCompatActivity() {
    lateinit var estateModel: EstateModel

    private val estateViewModel: EstateViewModel by viewModels {
        val app = application as RealEstateApplication
        EstateViewModelFactory(app.estateRepository)
    }
    private val estateImageViewModel: EstateImageViewModel by viewModels {
        EstateImageViewModelFactory((application as RealEstateApplication).estateImageRepository)
    }
    private val estatePlaceViewModel: EstatePlaceViewModel by viewModels {
        EstatePlaceViewModelFactory((application as RealEstateApplication).estatePlaceRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estate_detail)
        //setSupportActionBar(findViewById(R.id.detail_toolbar))

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            estateModel = intent.getSerializableExtra(Utils.EXTRA_ESTATE_MODEL) as EstateModel
            showDetail(estateModel)
        }

        // Reset estates
        estateViewModel.allEstates.observe(this, { estatesModel ->
            estatesModel.forEach {
                if (it.estate.id == estateModel.estate.id) {
                    showDetail(it)
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    navigateUpTo(Intent(this, EstateListActivity::class.java))
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Utils.FORM_ACTIVITY_REQUEST && resultCode == Activity.RESULT_OK) {
            val estate: Estate = data?.getSerializableExtra(Utils.EXTRA_ESTATE) as Estate
            estateViewModel.estateId.observe(this, { estateId ->
                // Images
                val estateImages: MutableList<EstateImage> = data.getSerializableExtra(Utils.EXTRA_ESTATE_IMAGE) as MutableList<EstateImage>
                val images: MutableList<EstateImage> = data.getSerializableExtra(Utils.EXTRA_IMAGE) as MutableList<EstateImage>
                val imagesToKeep: MutableList<Long> = mutableListOf()
                var idx = 0
                estateImages.forEach { image ->
                    if(idx > 0) {
                        estateImageViewModel.insert(EstateImage(id = image.id, estateId = estateId, uri = image.uri, name = image.name))
                        if (image.id > 0) {
                            imagesToKeep.add(image.id)
                        }
                    }
                    idx++
                }
                // Delete estateImage if removed
                if (images.size > 0) {
                    images.forEach { estateImage ->
                        if (!imagesToKeep.contains(estateImage.id)) {
                            estateImageViewModel.delete(estateImage)
                        }
                    }
                }

                // Places
                val placeIds: MutableList<Int> = data.getSerializableExtra(Utils.EXTRA_PLACE) as MutableList<Int>
                val estatePlaces: MutableList<EstatePlace> = data.getSerializableExtra(Utils.EXTRA_ESTATE_PLACE) as MutableList<EstatePlace>
                val estatePlacesToKeep: MutableList<EstatePlace> = mutableListOf()
                placeIds.forEach { placeId ->
                    // Get estate place already saved
                    val estatePlace: EstatePlace? = estatePlaces.firstOrNull { estatePlace -> estatePlace.placeId == placeId.toLong() }
                    if (estatePlace != null) {
                        estatePlacesToKeep.add(estatePlace)
                    }

                    estatePlaceViewModel.insert(EstatePlace(id = estatePlace?.id ?: 0L, estateId = estateId, placeId = placeId.toLong()))
                }

                // Delete estatePlace if not checked
                estatePlaces.forEach { estatePlace ->
                    if (!estatePlacesToKeep.contains(estatePlace)) {
                        estatePlaceViewModel.delete(estatePlace)
                    }
                }
            })
            estateViewModel.insert(estate)
        }
    }


    private fun showDetail(estateModel: EstateModel) {
        val fragment = EstateDetailFragment().apply {
            arguments = Bundle().apply {
                putSerializable(Utils.EXTRA_ESTATE_MODEL, estateModel)
            }
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.estate_detail_container, fragment)
                .commit()
        
        findViewById<FloatingActionButton>(R.id.estate_detail_button_form).setOnClickListener {
            val formIntent = Intent(this, EstateFormActivity::class.java)
            formIntent.putExtra(Utils.EXTRA_ESTATE_MODEL, estateModel)
            startActivityForResult(formIntent, Utils.FORM_ACTIVITY_REQUEST)
        }
    }
}