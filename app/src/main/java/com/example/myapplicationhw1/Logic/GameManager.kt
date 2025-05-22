package com.example.myapplicationhw1.Logic

import kotlin.random.Random
import com.example.myapplicationhw1.Utilities.SignalManager


class GameManager(
    private val numRows: Int = 10,
    private val numColumns: Int = 5,
    private val lifeCount: Int = 3
) {
    private val matrix = Array(numRows) { IntArray(numColumns) { 0 } }
    private var firstTick = true

    var spaceshipColumn: Int = numColumns / 2
        private set

    var wrongAnswers: Int = 0
        private set

    var wasHitThisTick: Boolean = false
        private set

    val isGameOver: Boolean
        get() = wrongAnswers >= lifeCount

   var score: Int = 0
         private set


    fun moveLeft() {
        if (spaceshipColumn > 0) spaceshipColumn--
    }

    fun moveRight() {
        if (spaceshipColumn < numColumns - 1) spaceshipColumn++
    }

    fun gameTick() {
        wasHitThisTick = false  // Reset flag every tick

        if (!firstTick) {
            moveObjectsDown()
            checkCollision()
        } else {
            // Clear the top row to avoid double asteroid on first tick
            for (col in 0 until numColumns) {
                matrix[0][col] = 0
            }
        }

        spawnObject()
        firstTick = false
    }

    private fun moveObjectsDown() {
        for (row in numRows - 1 downTo 1) {
            for (col in 0 until numColumns) {
                matrix[row][col] = matrix[row - 1][col]
            }
        }

        for (col in 0 until numColumns) {
            matrix[0][col] = 0
        }
    }

    fun spawnObject() {
        val randomCol = Random.nextInt(numColumns)
        when (Random.nextInt(3)) {
            0 -> matrix[0][randomCol] = 1 // asteroid
            1 -> matrix[0][randomCol] = 2 // star
            2 -> matrix[0][randomCol] = 0 // nothing
        }
    }


    private fun checkCollision() {
        val objectAtPlayer = matrix[numRows - 1][spaceshipColumn]

        when (objectAtPlayer) {
            1 -> { // asteroid
                wrongAnswers++
                SignalManager.getInstance().vibrate()
                SignalManager.getInstance().toast("BOOM!")
                wasHitThisTick = true
            }
            2 -> { // star
                score += 10
                SignalManager.getInstance().toast("⭐ +10 Points!")
            }
        }

        matrix[numRows - 1][spaceshipColumn] = 0
    }


    fun getMatrix(): Array<IntArray> = matrix

    fun resetGame() {
        for (row in 0 until numRows) {
            for (col in 0 until numColumns) {
                matrix[row][col] = 0
            }
        }

        wrongAnswers = 0
        spaceshipColumn = numColumns / 2
        firstTick = true
        wasHitThisTick = false
    }

    fun initializeGame() {
        resetGame()
        val randomCol = Random.nextInt(numColumns)
        matrix[0][randomCol] = 1
    }
}