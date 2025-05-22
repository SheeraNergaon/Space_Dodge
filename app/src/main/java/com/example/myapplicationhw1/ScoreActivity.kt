package com.example.myapplicationhw1

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ScoreActivity : AppCompatActivity(), OnHighscoreClickListener {

    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        resetButton = findViewById(R.id.button_reset)

        val highscores = HighscoreStorage.load(this)
        val latestEntry = highscores.lastOrNull()

        val mapFragment = HighscoreMapFragment()
        val listFragment = HighscoreListFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.highscore_list_container, listFragment)
            .replace(R.id.highscore_map_container, mapFragment)
            .commit()

        // Button to clear highscores
        resetButton.setOnClickListener {
            HighscoreStorage.clear(this)
            Toast.makeText(this, "Highscores reset", Toast.LENGTH_SHORT).show()

            val emptyList = emptyList<HighscoreEntry>()
            listFragment.updateHighscores(emptyList)
            mapFragment.setHighscores(emptyList)
        }

        // Update fragments after they are attached
        window.decorView.post {
            mapFragment.setHighscores(highscores)
            latestEntry?.let { mapFragment.updateLocation(it) }
            listFragment.updateHighscores(highscores)
        }
    }

    override fun onHighscoreClicked(highscore: HighscoreEntry) {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.highscore_map_container) as? HighscoreMapFragment
        mapFragment?.updateLocation(highscore)
    }
}
