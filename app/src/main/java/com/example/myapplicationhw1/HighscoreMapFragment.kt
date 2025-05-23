package com.example.myapplicationhw1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HighscoreMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private var highscoreList: List<HighscoreEntry> = listOf()
    private var pendingLocation: HighscoreEntry? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_highscore_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        // Plot any previously saved list
        plotMarkers()

        // If a specific location was waiting
        pendingLocation?.let {
            updateLocation(it)
            pendingLocation = null
        }
    }

    fun setHighscores(list: List<HighscoreEntry>) {
        highscoreList = list
        plotMarkers()
    }

    fun updateLocation(entry: HighscoreEntry) {
        googleMap?.let { map ->
            val location = LatLng(entry.latitude, entry.longitude)
            map.clear()
            map.addMarker(MarkerOptions().position(location).title(entry.name))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
        } ?: run {
            pendingLocation = entry
        }
    }

    private fun plotMarkers() {
        googleMap?.let { map ->
            map.clear()
            for (entry in highscoreList) {
                val location = LatLng(entry.latitude, entry.longitude)
                map.addMarker(MarkerOptions().position(location).title(entry.name))
            }

            // Optional zoom to the last entry
            highscoreList.lastOrNull()?.let { last ->
                val lastLocation = LatLng(last.latitude, last.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 12f))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
