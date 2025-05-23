package com.example.myapplicationhw1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    private lateinit var btnStartGame: Button
    private lateinit var switchFastMode: Switch
    private lateinit var switchSensorMode: Switch


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Find views by their IDs matching XML
        btnStartGame = findViewById(R.id.menu_BTN_start_game)
        switchFastMode = findViewById(R.id.switch_fast_mode)        // NEW
        switchSensorMode = findViewById(R.id.switch_sensor_mode)    // NEW

        // Optional: you can update switch text dynamically if you want
        switchFastMode.setOnCheckedChangeListener { _, isChecked ->
            switchFastMode.text = if (isChecked) "Speed: Fast" else "Speed: Normal"
        }
        switchSensorMode.setOnCheckedChangeListener { _, isChecked ->
            switchSensorMode.text = if (isChecked) "Sensor: On" else "Sensor: Off"
        }

        btnStartGame.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Pass the current state of the switches to the game activity
            intent.putExtra("EXTRA_FAST", switchFastMode.isChecked)
            intent.putExtra("EXTRA_SENSOR", switchSensorMode.isChecked)
            startActivity(intent)
        }
    }
}
