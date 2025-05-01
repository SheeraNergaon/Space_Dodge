package com.example.myapplicationhw1
import android.content.Intent
import android.os.*
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.example.myapplicationhw1.Utilities.Constants
import com.google.android.material.button.MaterialButton
import com.example.myapplicationhw1.Logic.GameManager
class MainActivity : AppCompatActivity() {

    private lateinit var main_BTN_Left: MaterialButton
    private lateinit var main_BTN_Right: MaterialButton
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_grid_matrix: GridLayout
    private lateinit var matrixImages: Array<Array<AppCompatImageView>>

    private lateinit var gameManager: GameManager
    private val handler = Handler(Looper.getMainLooper())
    private val gameTickRunnable = object : Runnable {
        override fun run() {
            gameManager.gameTick()
            refreshMatrixUI()
            refreshUI()

            if (gameManager.isGameOver) {
                changeActivity("Game Over!")
            } else {
                handler.postDelayed(this, 800)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()

        val numCols = 3
        val numRows = 6

        main_grid_matrix.columnCount = numCols
        gameManager = GameManager(numRows, numCols, 3)

        main_grid_matrix.removeAllViews()
        initMatrixUI()
        initViews()

        gameManager.initializeGame()
        refreshMatrixUI()

        handler.postDelayed(gameTickRunnable, 1000)
    }

    private fun findViews() {
        main_BTN_Left = findViewById(R.id.main_BTN_Left)
        main_BTN_Right = findViewById(R.id.main_BTN_Right)
        main_grid_matrix = findViewById(R.id.main_grid_matrix)

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
                if (row == matrix.lastIndex && col == gameManager.spaceshipColumn) {
                    imageView.setImageResource(R.drawable.space_rocket)
                    imageView.visibility = View.VISIBLE
                } else if (matrix[row][col] == 1) {
                    imageView.setImageResource(R.drawable.asteroid)
                    imageView.visibility = View.VISIBLE
                } else {
                    imageView.setImageResource(0)
                    imageView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun refreshUI() {
        for (i in main_IMG_hearts.indices) {
            main_IMG_hearts[i].visibility = if (i < 3 - gameManager.wrongAnswers) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun changeActivity(message: String) {
        val intent = Intent(this, ScoreActivity::class.java)
        intent.putExtra(Constants.BundleKeys.MESSAGE_KEY, message)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacks(gameTickRunnable)
        super.onDestroy()
    }
}
