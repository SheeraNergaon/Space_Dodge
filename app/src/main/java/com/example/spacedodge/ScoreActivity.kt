package com.example.spacedodge

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices

class ScoreActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var resetButton: Button
    private lateinit var mapFragment: HighscoreMapFragment
    private lateinit var listFragment: HighscoreListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        backButton = findViewById(R.id.button_play_again)
        resetButton = findViewById(R.id.button_reset)

        val score = intent.getIntExtra("EXTRA_SCORE", 0)
        val distance = intent.getIntExtra("EXTRA_DISTANCE", 0)

        val highscores = HighscoreStorage.load(this)
        val lowestScore = highscores.minByOrNull { it.score }?.score ?: 0
        val shouldAdd = highscores.size < 10 || score > lowestScore

        if (shouldAdd && score > 0) {
            showSaveDialog(score, distance)
        } else {
            loadFragments(highscores)
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }

        resetButton.setOnClickListener {
            HighscoreStorage.clear(this)
            Toast.makeText(this, "Highscores reset", Toast.LENGTH_SHORT).show()
            loadFragments(emptyList())
        }
    }

    private fun showSaveDialog(score: Int, distance: Int) {
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
                        saveScoreAndLoad(name, score, distance, lat, lon)
                    }
                } else {
                    Toast.makeText(this, "No location permission", Toast.LENGTH_SHORT).show()
                    saveScoreAndLoad(name, score, distance, 0.0, 0.0)
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun saveScoreAndLoad(name: String, score: Int, distance: Int, lat: Double, lon: Double) {
        val entry = HighscoreEntry(name, score, distance, lat, lon)
        HighscoreStorage.save(this, entry)
        val updatedList = HighscoreStorage.load(this)
        loadFragments(updatedList)
    }

    private fun loadFragments(highscores: List<HighscoreEntry>) {
        mapFragment = HighscoreMapFragment()
        listFragment = HighscoreListFragment()

        listFragment.setHighscores(highscores)
        mapFragment.setHighscores(highscores)

        listFragment.setOnItemClickListener { entry ->
            mapFragment.zoom(entry.latitude, entry.longitude)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.highscore_list_container, listFragment)
            .replace(R.id.highscore_map_container, mapFragment)
            .commit()
    }
}
