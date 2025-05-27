package com.example.spacedodge

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class GameOverActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var saveButton: Button
    private lateinit var messageTextView: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var score: Int = 0
    private var distance: Int = 0

    companion object {
        private const val LOCATION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        nameInput = findViewById(R.id.input_name)
        saveButton = findViewById(R.id.button_save)
        messageTextView = findViewById(R.id.game_over_message)

        score = intent.getIntExtra("EXTRA_SCORE", 0)
        distance = intent.getIntExtra("EXTRA_DISTANCE", 0)

        val qualifies = HighscoreStorage.qualifiesForHighscore(this, score)
        messageTextView.text = if (qualifies && score > 0) {
            "🎉 New High Score Set!"
        } else {
            "💪 Keep playing to qualify for the Top 10!"
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        saveButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            } else {
                saveHighscoreWithLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun saveHighscoreWithLocation() {
        val name = nameInput.text.toString().ifBlank { "Anonymous" }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                val lat = location?.latitude ?: 0.0
                val lng = location?.longitude ?: 0.0

                val highscore = HighscoreEntry(name, score, distance, lat, lng)
                HighscoreStorage.save(this, highscore)

                startActivity(Intent(this, ScoreActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
                val highscore = HighscoreEntry(name, score, distance, 0.0, 0.0)
                HighscoreStorage.save(this, highscore)

                startActivity(Intent(this, ScoreActivity::class.java))
                finish()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            saveHighscoreWithLocation()
        } else {
            Toast.makeText(this, "Location permission is required to save your score location.", Toast.LENGTH_LONG).show()
        }
    }
}
