package com.openclassrooms.realestatemanager.ui.list

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.EstateModel
import com.openclassrooms.realestatemanager.ui.detail.EstateDetailActivity
import com.openclassrooms.realestatemanager.utils.Utils
import java.io.Serializable

class MapFragment : Fragment() {
    private var items: List<EstateModel>? = null
    private var currentLatLng: LatLng = LatLng(48.8,2.35)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(Utils.EXTRA_ESTATE_MODEL)) {
                items = it.getSerializable(Utils.EXTRA_ESTATE_MODEL) as List<EstateModel>
            }
            if (it.containsKey(Utils.EXTRA_LAT) && it.containsKey(Utils.EXTRA_LNG)) {
                currentLatLng = LatLng(it.getDouble(Utils.EXTRA_LAT), it.getDouble(Utils.EXTRA_LNG))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        var latLng: LatLng?
        val markerOptions = MarkerOptions()

        // Set markers
        if (items?.isNotEmpty() == true) {
            items!!.forEach { item ->
                if (item.estate.lat != 0.0 && item.estate.lng != 0.0) {
                    latLng = LatLng(item.estate.lat, item.estate.lng)

                    // Set position to the marker
                    markerOptions.position(latLng!!)
                    // Set title to the marker
                    markerOptions.title(item.estate.city)

                    googleMap.setOnMarkerClickListener {
                        val intent = Intent(this.requireContext(), EstateDetailActivity::class.java).apply {
                            putExtra(Utils.EXTRA_ESTATE_MODEL, item as Serializable)
                        }
                        this.requireContext().startActivity(intent)
                        return@setOnMarkerClickListener true
                    }

                    // Add marker
                    googleMap.addMarker(markerOptions)
                }
            }
        }

        // Check permission for the location of the user
        if (ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        }

        // Set current location
        val currentLocation = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15F)
        googleMap.moveCamera(currentLocation)
    }

}