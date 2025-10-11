package de.fabmax.kool.demo.tetris

import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.random.Random

internal class TetrisGame {
    // game state
    val board = Array(HEIGHT) { Array<Color?>(WIDTH) { null } }
    var currentPiece: Piece? = null

    private val pieceQueue = mutableStateListOf<Tetromino>()
    val numPreviews = mutableStateOf(1)
    val nextPiece: Tetromino get() = pieceQueue.first()
    val previewPieces: List<Tetromino>
        get() {
            val numPreviews = numPreviews.value
            return if (numPreviews <= 1) {
                emptyList()
            } else {
                pieceQueue.subList(1, numPreviews.coerceAtMost(pieceQueue.size))
            }
        }

    val isPaused = mutableStateOf(false)
    val isGameOver = mutableStateOf(false)

    // Score
    val score = mutableStateOf(0)
    val lines = mutableStateOf(0)
    val level = mutableStateOf(1)

    // Timers
    private var gravityTimer = 0f
    private var gravitySpeed = 1f
    private var softDropScore = 0

    val blockSize = 1f

    init {
        reset()
    }

    fun reset() {
        for (y in 0 until HEIGHT) {
            for (x in 0 until WIDTH) {
                board[y][x] = null
            }
        }
        score.set(0)
        lines.set(0)
        level.set(1)
        isPaused.set(false)
        gravitySpeed = 1f

        pieceQueue.clear()
        repeat(MAX_PREVIEWS) {
            pieceQueue.add(Tetromino.random())
        }
        spawnNewPiece()
    }

    fun update(deltaT: Float) {
        if (isPaused.value || isGameOver.value) return

        gravityTimer += deltaT
        if (gravityTimer >= gravitySpeed) {
            gravityTimer = 0f
            if (!move(0, -1)) {
                lockPiece()
            }
        }
    }

    fun togglePause() {
        if (!isGameOver.value) {
            isPaused.set(!isPaused.value)
        }
    }

    fun getGhostPiece(): Piece? {
        currentPiece?.let {
            var ghost = it
            while (true) {
                val next = ghost.copy(pos = MutableVec2i(ghost.pos.x, ghost.pos.y - 1))
                if (checkCollision(next)) {
                    return ghost
                }
                ghost = next
            }
        }
        return null
    }

    private fun spawnNewPiece() {
        currentPiece = Piece(pieceQueue.removeFirst(), MutableVec2i(WIDTH / 2, HEIGHT - 2))
        while (pieceQueue.size < MAX_PREVIEWS) {
            pieceQueue.add(Tetromino.random())
        }
        softDropScore = 0

        if (checkCollision(currentPiece!!)) {
            isGameOver.set(true)
        }
    }

    fun move(dx: Int, dy: Int = 0): Boolean {
        if (isPaused.value || isGameOver.value) return false
        val moved = currentPiece?.copy(pos = MutableVec2i(currentPiece!!.pos.x + dx, currentPiece!!.pos.y + dy))
        if (moved != null && !checkCollision(moved)) {
            currentPiece = moved
            return true
        }
        return false
    }

    fun softDrop() {
        if (isPaused.value || isGameOver.value) return
        if (!move(0, -1)) {
            lockPiece()
        } else {
            softDropScore++
        }
    }

    fun hardDrop() {
        if (isPaused.value || isGameOver.value) return
        var dropped = 0
        while (move(0, -1)) {
            dropped++
        }
        score.set(score.value + dropped * 2)
        lockPiece()
    }

    fun rotate() {
        if (isPaused.value || isGameOver.value) return
        val piece = currentPiece ?: return
        if (piece.tetromino == Tetromino.O) {
            return
        }

        val rotated = piece.copy(rotation = piece.rotation + 1)
        val fromRotation = piece.rotation % piece.tetromino.shapes.size
        val toRotation = rotated.rotation % piece.tetromino.shapes.size

        val kickData = if (piece.tetromino == Tetromino.I) iKicks else jlstzKicks
        val kicks = kickData[fromRotation to toRotation] ?: emptyList()

        for (kick in kicks) {
            val testPiece = rotated.copy(pos = MutableVec2i(rotated.pos.x + kick.x, rotated.pos.y + kick.y))
            if (!checkCollision(testPiece)) {
                currentPiece = testPiece
                return
            }
        }
    }

    private fun checkCollision(piece: Piece): Boolean {
        return piece.getBlocks().any { p ->
            p.x !in 0 ..< WIDTH || p.y < 0 || (p.y < HEIGHT && board[p.y][p.x] != null)
        }
    }

    private fun lockPiece() {
        score.set(score.value + softDropScore)
        currentPiece?.getBlocks()?.forEach { p ->
            if (p.y in 0 until HEIGHT) {
                board[p.y][p.x] = currentPiece!!.tetromino.color
            }
        }
        clearLines()
        spawnNewPiece()
    }

    private fun clearLines() {
        var y = HEIGHT - 1
        var clearedCount = 0
        while (y >= 0) {
            if (board[y].all { it != null }) {
                clearedCount++
                // Shift all rows above this one down by one
                for (j in y until HEIGHT - 1) {
                    board[j] = board[j + 1]
                }
                // Clear the top row
                board[HEIGHT - 1] = Array(WIDTH) { null }
            } else {
                y--
            }
        }

        if (clearedCount > 0) {
            lines.set(lines.value + clearedCount)
            score.set(score.value + when (clearedCount) {
                1 -> 100
                2 -> 300
                3 -> 500
                4 -> 800 // Tetris!
                else -> 1200 // it's impossible, but miracles happen :>
            } * level.value)

            if (lines.value / 10 >= level.value) {
                level.set(level.value + 1)
                gravitySpeed = (1.0 - (level.value - 1) * 0.05).toFloat().coerceAtLeast(0.1f)
            }
        }
    }

    companion object {
        const val WIDTH = 10
        const val HEIGHT = 20
        const val MAX_PREVIEWS = 5

        private val jlstzKicks = mapOf(
            (0 to 1) to listOf(Vec2i(0, 0), Vec2i(-1, 0), Vec2i(-1, 1), Vec2i(0, -2), Vec2i(-1, -2)),
            (1 to 0) to listOf(Vec2i(0, 0), Vec2i(1, 0), Vec2i(1, -1), Vec2i(0, 2), Vec2i(1, 2)),
            (1 to 2) to listOf(Vec2i(0, 0), Vec2i(1, 0), Vec2i(1, -1), Vec2i(0, 2), Vec2i(1, 2)),
            (2 to 1) to listOf(Vec2i(0, 0), Vec2i(-1, 0), Vec2i(-1, 1), Vec2i(0, -2), Vec2i(-1, -2)),
            (2 to 3) to listOf(Vec2i(0, 0), Vec2i(1, 0), Vec2i(1, 1), Vec2i(0, -2), Vec2i(1, -2)),
            (3 to 2) to listOf(Vec2i(0, 0), Vec2i(-1, 0), Vec2i(-1, -1), Vec2i(0, 2), Vec2i(-1, 2)),
            (3 to 0) to listOf(Vec2i(0, 0), Vec2i(-1, 0), Vec2i(-1, -1), Vec2i(0, 2), Vec2i(-1, 2)),
            (0 to 3) to listOf(Vec2i(0, 0), Vec2i(1, 0), Vec2i(1, 1), Vec2i(0, -2), Vec2i(1, -2))
        )

        private val iKicks = mapOf(
            (0 to 1) to listOf(Vec2i(0, 0), Vec2i(-2, 0), Vec2i(1, 0), Vec2i(-2, 1), Vec2i(1, -2)),
            (1 to 0) to listOf(Vec2i(0, 0), Vec2i(2, 0), Vec2i(-1, 0), Vec2i(2, -1), Vec2i(-1, 2)),
            (1 to 2) to listOf(Vec2i(0, 0), Vec2i(-1, 0), Vec2i(2, 0), Vec2i(-1, 2), Vec2i(2, -1)),
            (2 to 1) to listOf(Vec2i(0, 0), Vec2i(1, 0), Vec2i(-2, 0), Vec2i(1, -2), Vec2i(-2, 1)),
            (2 to 3) to listOf(Vec2i(0, 0), Vec2i(2, 0), Vec2i(-1, 0), Vec2i(2, 1), Vec2i(-1, -2)),
            (3 to 2) to listOf(Vec2i(0, 0), Vec2i(-2, 0), Vec2i(1, 0), Vec2i(-2, -1), Vec2i(1, 2)),
            (3 to 0) to listOf(Vec2i(0, 0), Vec2i(1, 0), Vec2i(-2, 0), Vec2i(1, -2), Vec2i(-2, 1)),
            (0 to 3) to listOf(Vec2i(0, 0), Vec2i(-1, 0), Vec2i(2, 0), Vec2i(-1, 2), Vec2i(2, -1))
        )
    }
}

internal data class Piece(val tetromino: Tetromino, val pos: MutableVec2i, val rotation: Int = 0) {
    fun getBlocks(): List<Vec2i> {
        return tetromino.shapes[rotation % tetromino.shapes.size].map { Vec2i(it.x + pos.x, it.y + pos.y) }
    }
}

internal data class Tetromino(val shapes: List<List<Vec2i>>, val color: Color) {
    companion object {
        val I = Tetromino(listOf(
            listOf(Vec2i(-1, 0), Vec2i(0, 0), Vec2i(1, 0), Vec2i(2, 0)),
            listOf(Vec2i(1, 1), Vec2i(1, 0), Vec2i(1, -1), Vec2i(1, -2)),
            listOf(Vec2i(-1, -1), Vec2i(0, -1), Vec2i(1, -1), Vec2i(2, -1)),
            listOf(Vec2i(0, 1), Vec2i(0, 0), Vec2i(0, -1), Vec2i(0, -2))
        ), MdColor.CYAN)
        val O = Tetromino(listOf(listOf(Vec2i(0, 0), Vec2i(1, 0), Vec2i(0, 1), Vec2i(1, 1))), MdColor.YELLOW)
        val T = Tetromino(listOf(
            listOf(Vec2i(-1, 0), Vec2i(0, 0), Vec2i(1, 0), Vec2i(0, 1)),
            listOf(Vec2i(0, 1), Vec2i(0, 0), Vec2i(0, -1), Vec2i(1, 0)),
            listOf(Vec2i(-1, 0), Vec2i(0, 0), Vec2i(1, 0), Vec2i(0, -1)),
            listOf(Vec2i(0, 1), Vec2i(0, 0), Vec2i(0, -1), Vec2i(-1, 0))
        ), MdColor.PURPLE)
        val L = Tetromino(listOf(
            listOf(Vec2i(-1, 0), Vec2i(0, 0), Vec2i(1, 0), Vec2i(1, 1)),
            listOf(Vec2i(0, 1), Vec2i(0, 0), Vec2i(0, -1), Vec2i(1, -1)),
            listOf(Vec2i(-1, 0), Vec2i(0, 0), Vec2i(1, 0), Vec2i(-1, -1)),
            listOf(Vec2i(0, 1), Vec2i(0, 0), Vec2i(0, -1), Vec2i(-1, 1))
        ), MdColor.ORANGE)
        val J = Tetromino(listOf(
            listOf(Vec2i(-1, 1), Vec2i(-1, 0), Vec2i(0, 0), Vec2i(1, 0)),
            listOf(Vec2i(0, 1), Vec2i(1, 1), Vec2i(0, 0), Vec2i(0, -1)),
            listOf(Vec2i(-1, 0), Vec2i(0, 0), Vec2i(1, 0), Vec2i(1, -1)),
            listOf(Vec2i(0, 1), Vec2i(-1, -1), Vec2i(0, -1), Vec2i(0, 0))
        ), MdColor.BLUE)
        val S = Tetromino(listOf(
            listOf(Vec2i(-1, 0), Vec2i(0, 0), Vec2i(0, 1), Vec2i(1, 1)),
            listOf(Vec2i(0, 1), Vec2i(0, 0), Vec2i(1, 0), Vec2i(1, -1))
        ), MdColor.GREEN)
        val Z = Tetromino(listOf(
            listOf(Vec2i(-1, 1), Vec2i(0, 1), Vec2i(0, 0), Vec2i(1, 0)),
            listOf(Vec2i(1, 1), Vec2i(1, 0), Vec2i(0, 0), Vec2i(0, -1))
        ), MdColor.RED)

        private val pieces = listOf(I, O, T, L, J, S, Z)
        fun random(): Tetromino = pieces[Random.nextInt(pieces.size)]
    }
}