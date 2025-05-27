package com.example.spacedodge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HighscoreMapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var highscoreList: List<HighscoreEntry> = listOf()
    private var pendingZoom: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_highscore_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        plotMarkers()

        pendingZoom?.let {
            zoom(it.latitude, it.longitude)
            pendingZoom = null
        }
    }

    fun setHighscores(list: List<HighscoreEntry>) {
        highscoreList = list
        plotMarkers()
    }

    fun zoom(lat: Double, lon: Double) {
        val location = LatLng(lat, lon)
        googleMap?.let {
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f)) // higher zoom
        } ?: run {
            pendingZoom = location
        }
    }

    private fun plotMarkers() {
        googleMap?.let { map ->
            map.clear()
            for (entry in highscoreList) {
                val location = LatLng(entry.latitude, entry.longitude)
                map.addMarker(MarkerOptions().position(location).title(entry.name))
            }

            highscoreList.lastOrNull()?.let { last ->
                val lastLocation = LatLng(last.latitude, last.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 12f))
            }
        }
    }
}
