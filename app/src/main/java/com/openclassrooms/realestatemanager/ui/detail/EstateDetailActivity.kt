package com.openclassrooms.realestatemanager.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.domain.RealEstateApplication
import com.openclassrooms.realestatemanager.ui.form.EstateFormActivity
import com.openclassrooms.realestatemanager.ui.list.EstateListActivity
import com.openclassrooms.realestatemanager.ui.viewmodel.EstateViewModel
import com.openclassrooms.realestatemanager.ui.viewmodel.EstateViewModelFactory
import com.openclassrooms.realestatemanager.utils.Utils
import java.io.Serializable

class EstateDetailActivity : AppCompatActivity() {
    lateinit var estateModel: EstateModel
    private var comeFromMaps: Boolean = false

    private val estateViewModel: EstateViewModel by viewModels {
        val app = application as RealEstateApplication
        EstateViewModelFactory(app.estateRepository, app.estateImageRepository, app.estatePlaceRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estate_detail)

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            estateModel = intent.getSerializableExtra(Utils.EXTRA_ESTATE_MODEL) as EstateModel
            comeFromMaps = intent.getBooleanExtra(Utils.EXTRA_COME_FROM_MAP, false)
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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            navigateUpTo(Intent(this, EstateListActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Utils.FORM_ACTIVITY_REQUEST && resultCode == Activity.RESULT_OK) {
            estateViewModel.estateId.observe(this, {
                if (it > 0) {
                    // Toast when the estate is saved
                    Toast.makeText(this, R.string.form_save, Toast.LENGTH_LONG).show()
                }
            })
            estateViewModel.saveEstate(data)
        }
    }

    private fun showDetail(estateModel: EstateModel) {
        val fragment = EstateDetailFragment().apply {
            arguments = Bundle().apply {
                putSerializable(Utils.EXTRA_ESTATE_MODEL, estateModel)
                putBoolean(Utils.EXTRA_COME_FROM_MAP,comeFromMaps)

                if (findViewById<RelativeLayout>(R.id.estate_detail_container) != null) {
                    putBoolean(Utils.EXTRA_TWO_PANE,true)
                }
            }
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.estate_detail_container, fragment)
                .commit()

        findViewById<FloatingActionButton>(R.id.estate_detail_button_form).setOnClickListener {
            val formIntent = Intent(this, EstateFormActivity::class.java)
            formIntent.putExtra(Utils.EXTRA_ESTATE_MODEL, estateModel)
            startActivityForResult(formIntent,Utils.FORM_ACTIVITY_REQUEST)
        }
    }
}