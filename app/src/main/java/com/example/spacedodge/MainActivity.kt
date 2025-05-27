package com.example.spacedodge

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.*
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.example.spacedodge.Logic.GameManager
import com.google.android.material.button.MaterialButton
import android.media.MediaPlayer


class MainActivity : AppCompatActivity() {

    private lateinit var main_BTN_Left: MaterialButton
    private lateinit var main_BTN_Right: MaterialButton
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_grid_matrix: GridLayout
    private lateinit var matrixImages: Array<Array<AppCompatImageView>>
    private lateinit var scoreTextView: TextView
    private lateinit var distanceTextView: TextView
    private var distanceCounter: Int = 0
    private var mediaPlayer: MediaPlayer? = null


    private var sensorMode: Boolean = false
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var gameManager: GameManager

    private val handler = Handler(Looper.getMainLooper())
    private var tickInterval = 300L

    private lateinit var soundPool: SoundPool
    private var soundBoom = 0
    private var soundStar = 0

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.takeIf { it.sensor.type == Sensor.TYPE_ACCELEROMETER }?.let {
                when {
                    it.values[0] > 4 -> {
                        gameManager.moveLeft()
                        refreshMatrixUI()
                    }
                    it.values[0] < -4 -> {
                        gameManager.moveRight()
                        refreshMatrixUI()
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val gameTickRunnable = object : Runnable {
        override fun run() {
            gameManager.gameTick()
            refreshMatrixUI()
            refreshUI()
            distanceCounter++
            distanceTextView.text = "Distance: $distanceCounter"

            if (gameManager.isGameOver) {
                changeActivity()
            } else {
                handler.postDelayed(this, tickInterval)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(0.3f, 0.3f)
        mediaPlayer?.start()


        val isFast = intent.getBooleanExtra("EXTRA_FAST", false)
        sensorMode = intent.getBooleanExtra("EXTRA_SENSOR", false)

        tickInterval = if (isFast) 300L else 550L
        handler.postDelayed(gameTickRunnable, tickInterval)

        findViews()

        if (sensorMode) {
            main_BTN_Left.visibility = View.GONE
            main_BTN_Right.visibility = View.GONE
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            accelerometer?.let {
                sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        soundBoom = soundPool.load(this, R.raw.asteroid_hit, 1)
        soundStar = soundPool.load(this, R.raw.star_collect, 1)

        findViews()
        setupGame()
    }


    private fun findViews() {
        main_BTN_Left = findViewById(R.id.main_BTN_Left)
        main_BTN_Right = findViewById(R.id.main_BTN_Right)
        main_grid_matrix = findViewById(R.id.main_grid_matrix)
        scoreTextView = findViewById(R.id.main_LBL_score)
        distanceTextView = findViewById(R.id.main_LBL_distance)
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )
    }

    private fun setupGame() {
        val numCols = 5
        val numRows = 10
        main_grid_matrix.columnCount = numCols

        gameManager = GameManager(numRows, numCols, 3).apply {
            onAsteroidHit = {
                soundPool.play(soundBoom, 1f, 1f, 0, 0, 1f)
            }
            onStarCollected = {
                soundPool.play(soundStar, 1f, 1f, 0, 0, 1f)
            }
        }

        main_grid_matrix.removeAllViews()
        initMatrixUI()
        initViews()

        gameManager.initializeGame()
        refreshMatrixUI()
    }

    private fun initMatrixUI() {
        val numRows = gameManager.getMatrix().size
        val numCols = gameManager.getMatrix()[0].size

        matrixImages = Array(numRows) { row ->
            Array(numCols) { col ->
                AppCompatImageView(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(col, 1f)
                        rowSpec = GridLayout.spec(row, 1f)
                        setMargins(4, 4, 4, 4)
                    }
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    adjustViewBounds = true
                    setImageResource(0)
                    main_grid_matrix.addView(this)
                }
            }
        }
    }

    private fun initViews() {
        main_BTN_Left.setOnClickListener {
            gameManager.moveLeft()
            refreshMatrixUI()
        }
        main_BTN_Right.setOnClickListener {
            gameManager.moveRight()
            refreshMatrixUI()
        }
    }

    private fun refreshMatrixUI() {
        val matrix = gameManager.getMatrix()
        for (row in matrix.indices) {
            for (col in matrix[0].indices) {
                val imageView = matrixImages[row][col]
                when {
                    row == matrix.lastIndex && col == gameManager.spaceshipColumn -> {
                        imageView.setImageResource(R.drawable.space_rocket)
                        imageView.visibility = View.VISIBLE
                    }
                    matrix[row][col] == 1 -> {
                        imageView.setImageResource(R.drawable.asteroid)
                        imageView.visibility = View.VISIBLE
                    }
                    matrix[row][col] == 2 -> {
                        imageView.setImageResource(R.drawable.star_svgrepo_com)
                        imageView.visibility = View.VISIBLE
                    }
                    else -> {
                        imageView.setImageResource(0)
                        imageView.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun refreshUI() {
        for (i in main_IMG_hearts.indices) {
            main_IMG_hearts[i].visibility = if (i < 3 - gameManager.wrongAnswers) View.VISIBLE else View.INVISIBLE
        }
        scoreTextView.text = "Score: ${gameManager.score}"
    }

    private fun changeActivity() {
        val intent = Intent(this, GameOverActivity::class.java).apply {
            putExtra("EXTRA_SCORE", gameManager.score)
            putExtra("EXTRA_DISTANCE", distanceCounter)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacks(gameTickRunnable)

        if (sensorMode) {
            sensorManager.unregisterListener(sensorListener)
        }

        soundPool.release()

        mediaPlayer?.release()
        mediaPlayer = null

        super.onDestroy()
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(gameTickRunnable)
        mediaPlayer?.pause()

        if (sensorMode) {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(gameTickRunnable, tickInterval)
        mediaPlayer?.start()

        if (sensorMode) {
            accelerometer?.let {
                sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }


}
