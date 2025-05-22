package com.example.myapplicationhw1
import android.content.Context
import android.content.Intent
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.Sensor
import android.os.*
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.button.MaterialButton
import com.example.myapplicationhw1.Logic.GameManager
class MainActivity : AppCompatActivity() {

    private lateinit var main_BTN_Left: MaterialButton
    private lateinit var main_BTN_Right: MaterialButton
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_grid_matrix: GridLayout
    private lateinit var matrixImages: Array<Array<AppCompatImageView>>
    private lateinit var scoreTextView: TextView
    private lateinit var distanceTextView: TextView
    private var distanceCounter: Int = 0

    private var sensorMode: Boolean = false
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                val x = event.values[0]
                if (x > 4) {
                    gameManager.moveLeft()
                    refreshMatrixUI()
                } else if (x < -4) {
                    gameManager.moveRight()
                    refreshMatrixUI()
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }


    private lateinit var gameManager: GameManager
    private val handler = Handler(Looper.getMainLooper())
    private var tickInterval = 300L

    private val gameTickRunnable = object : Runnable {
        override fun run() {
            gameManager.gameTick()
            refreshMatrixUI()
            refreshUI()
            distanceCounter += 1
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

        val isFast = intent.getBooleanExtra("EXTRA_FAST", false)
        sensorMode = intent.getBooleanExtra("EXTRA_SENSOR", false)
        if (sensorMode) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            accelerometer?.let {
                sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }


        tickInterval = if (isFast) 300L else 550L
        handler.postDelayed(gameTickRunnable, tickInterval)

        findViews()

        val numCols = 5
        val numRows = 10

        main_grid_matrix.columnCount = numCols
        gameManager = GameManager(numRows, numCols, 3)

        main_grid_matrix.removeAllViews()
        initMatrixUI()
        initViews()

        gameManager.initializeGame()
        refreshMatrixUI()

        handler.postDelayed(gameTickRunnable, tickInterval)
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

    private fun initMatrixUI() {
        val numRows = gameManager.getMatrix().size
        val numCols = gameManager.getMatrix()[0].size

        matrixImages = Array(numRows) { row ->
            Array(numCols) { col ->
                val img = AppCompatImageView(this)
                img.layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    columnSpec = GridLayout.spec(col, 1f)
                    rowSpec = GridLayout.spec(row, 1f)
                    setMargins(4, 4, 4, 4)
                }
                img.scaleType = ImageView.ScaleType.FIT_CENTER
                img.adjustViewBounds = true
                img.setImageResource(0)
                main_grid_matrix.addView(img)
                img
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
                        imageView.setImageResource(R.drawable.star_svgrepo_com) // the new star icon
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
            scoreTextView.text = "Score: ${gameManager.score}"

        }
    }

    private fun changeActivity() {
        val intent = Intent(this, GameOverActivity::class.java)
        intent.putExtra("EXTRA_SCORE", gameManager.score)
        intent.putExtra("EXTRA_DISTANCE", distanceCounter) // send real distance
        startActivity(intent)
        finish()
    }



    override fun onDestroy() {
        handler.removeCallbacks(gameTickRunnable)
        if (sensorMode) {
            sensorManager.unregisterListener(sensorListener)
        }
        super.onDestroy()
    }
}
