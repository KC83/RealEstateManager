package com.openclassrooms.realestatemanager.ui.list

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.RealEstateRoomDatabase
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.EstateImage
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.data.model.EstatePlace
import com.openclassrooms.realestatemanager.domain.repository.RealEstateApplication
import com.openclassrooms.realestatemanager.ui.form.EstateFormActivity
import com.openclassrooms.realestatemanager.ui.viewmodel.*
import com.openclassrooms.realestatemanager.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class EstateListActivity : AppCompatActivity() {
    private var twoPane: Boolean = false

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
        setContentView(R.layout.activity_estate_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        toolbar.title = title

        if (findViewById<NestedScrollView>(R.id.estate_detail_container) != null) {
            twoPane = true
        }

        estateViewModel.allEstates.observe(this, { estatesModel ->
            setupRecyclerView(findViewById(R.id.estate_list), estatesModel)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem)= when (item.itemId) {
        R.id.action_add_estate -> {
            val intent = Intent(this, EstateFormActivity::class.java)
            startActivityForResult(intent, Utils.FORM_ACTIVITY_REQUEST)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Utils.FORM_ACTIVITY_REQUEST && resultCode == Activity.RESULT_OK) {
            val estate: Estate = data?.getSerializableExtra(Utils.EXTRA_ESTATE) as Estate
            estateViewModel.estateId.observe(this, { estateId ->
                // Images
                val images: MutableList<EstateImage> = data.getSerializableExtra(Utils.EXTRA_ESTATE_IMAGE) as MutableList<EstateImage>
                var idx = 0
                images.forEach { image ->
                    if(idx > 0) {
                        estateImageViewModel.insert(EstateImage(estateId = estateId, uri = image.uri, name = image.name))
                    }
                    idx++
                }

                // Places
                val placeIds: MutableList<Int> = data.getSerializableExtra(Utils.EXTRA_PLACE) as MutableList<Int>
                placeIds.forEach { placeId ->
                    estatePlaceViewModel.insert(EstatePlace(estateId = estateId, placeId = placeId.toLong()))
                }

                estateViewModel.allEstates.observe(this, { estatesModel ->
                    setupRecyclerView(findViewById(R.id.estate_list), estatesModel)
                })
            })
            estateViewModel.insert(estate)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, estates: List<EstateModel>) {
        recyclerView.adapter = EstateListAdapter(this, estates, twoPane)
    }
}