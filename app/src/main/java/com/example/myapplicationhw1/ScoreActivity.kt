package com.example.myapplicationhw1

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import java.util.Locale

class ScoreActivity : AppCompatActivity(), OnHighscoreClickListener {

    private lateinit var resetButton: Button
    private lateinit var mapFragment: HighscoreMapFragment
    private lateinit var listFragment: HighscoreListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        resetButton = findViewById(R.id.button_reset)

        val score = intent.getIntExtra("EXTRA_SCORE", 0)
        val distance = intent.getIntExtra("EXTRA_DISTANCE", 0)

        val highscores = HighscoreStorage.load(this)
        val lowestScore = highscores.minByOrNull { it.score }?.score ?: 0
        val shouldAdd = highscores.size < 10 || score > lowestScore

        mapFragment = HighscoreMapFragment()
        listFragment = HighscoreListFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.highscore_list_container, listFragment)
            .replace(R.id.highscore_map_container, mapFragment)
            .commit()

        if (shouldAdd && score > 0) {
            promptForHighscoreEntry(score, distance)
        } else {
            updateFragmentsWithHighscores(highscores)
        }

        resetButton.setOnClickListener {
            HighscoreStorage.clear(this)
            Toast.makeText(this, "Highscores reset", Toast.LENGTH_SHORT).show()
            val emptyList = emptyList<HighscoreEntry>()
            listFragment.updateHighscores(emptyList)
            mapFragment.setHighscores(emptyList)
        }
    }

    private fun promptForHighscoreEntry(score: Int, distance: Int) {
        val input = EditText(this)
        input.hint = "Enter your name"

        AlertDialog.Builder(this)
            .setTitle("New High Score!")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text.toString().ifBlank { "Unknown" }

                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        val lat = location?.latitude ?: 0.0
                        val lon = location?.longitude ?: 0.0
                        saveAndUpdate(name, score, distance, lat, lon)
                    }
                } else {
                    Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
                    saveAndUpdate(name, score, distance, 0.0, 0.0)
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun saveAndUpdate(name: String, score: Int, distance: Int, lat: Double, lon: Double) {
        val (city, country) = getCityAndCountry(lat, lon)
        val newEntry = HighscoreEntry(name, score, distance, lat, lon, city, country)
        HighscoreStorage.save(this, newEntry)

        val updatedList = HighscoreStorage.load(this)
        listFragment.updateHighscores(updatedList)
        mapFragment.setHighscores(updatedList)
        mapFragment.updateLocation(newEntry)
    }


    private fun updateFragmentsWithHighscores(highscores: List<HighscoreEntry>) {
        window.decorView.post {
            listFragment.updateHighscores(highscores)
            mapFragment.setHighscores(highscores)
            highscores.lastOrNull()?.let { mapFragment.updateLocation(it) }
        }
    }

    override fun onHighscoreClicked(highscore: HighscoreEntry) {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.highscore_map_container) as? HighscoreMapFragment
        mapFragment?.updateLocation(highscore)
    }

    private fun getCityAndCountry(latitude: Double, longitude: Double): Pair<String, String> {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val city = addresses[0].locality ?: "Unknown City"
                val country = addresses[0].countryName ?: "Unknown Country"
                Pair(city, country)
            } else {
                Pair("Unknown City", "Unknown Country")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Pair("Unknown City", "Unknown Country")
        }
    }

}
