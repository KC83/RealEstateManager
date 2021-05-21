package com.openclassrooms.realestatemanager.ui.list

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.domain.RealEstateApplication
import com.openclassrooms.realestatemanager.ui.viewmodel.EstateViewModel
import com.openclassrooms.realestatemanager.ui.viewmodel.EstateViewModelFactory
import com.openclassrooms.realestatemanager.utils.Utils
import java.io.Serializable

class MapActivity : AppCompatActivity() {
    private var estatesModel: List<EstateModel>? = null
    private var currentLat: Double = 48.8
    private var currentLng: Double = 2.35

    private val estateViewModel: EstateViewModel by viewModels {
        val app = application as RealEstateApplication
        EstateViewModelFactory(app.estateRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        estateViewModel.allEstates.observe(this, {
            estatesModel = it
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    showMap(estatesModel)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), Utils.LOCATION_REQUEST)
                }
            } else {
                showMap(estatesModel)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            Utils.LOCATION_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted
                    showMap(estatesModel)
                }
                return
            }
        }
    }

    private fun showMap(estatesModel: List<EstateModel>?) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            if(locationManager != null) {
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) {
                    currentLat = location.latitude
                    currentLng = location.longitude
                }
            }
        }

        val fragment = MapFragment().apply {
            arguments = Bundle().apply {
                putSerializable(Utils.EXTRA_ESTATE_MODEL, estatesModel as Serializable)
                putSerializable(Utils.EXTRA_LAT, currentLat)
                putSerializable(Utils.EXTRA_LNG, currentLng)
            }
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.map_fragment, fragment)
                .commit()
    }
}