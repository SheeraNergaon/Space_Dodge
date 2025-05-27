package com.example.spacedodge

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.google.android.material.materialswitch.MaterialSwitch
import androidx.appcompat.app.AppCompatActivity



class MenuActivity : AppCompatActivity() {

    private lateinit var btnStartGame: Button
    private lateinit var btnViewScores: Button
    private lateinit var switchFastMode: MaterialSwitch
    private lateinit var switchSensorMode: MaterialSwitch



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        btnStartGame = findViewById(R.id.menu_BTN_start_game)
        switchFastMode = findViewById(R.id.switch_fast_mode)
        switchSensorMode = findViewById(R.id.switch_sensor_mode)
        btnViewScores = findViewById(R.id.menu_BTN_view_scores)

        btnStartGame.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("EXTRA_FAST", switchFastMode.isChecked)
            intent.putExtra("EXTRA_SENSOR", switchSensorMode.isChecked)
            startActivity(intent)
        }
        btnViewScores.setOnClickListener {
            val intent = Intent(this, ScoreActivity::class.java)
            startActivity(intent)
        }

    }
}
