package de.fabmax.kool.demo.tetris

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.isFuzzyZero
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.MdColor
import kotlin.math.max
import kotlin.math.min

internal enum class BlockStyle(val displayName: String) {
    BEVELED_CUBE("Simple Cubes"),
    BEVELED_SPHERE("Rounded Cubes")
}

internal class TetrisRenderer(private val game: TetrisGame) {
    val blockStyle = mutableStateOf(BlockStyle.BEVELED_SPHERE)

    fun MeshBuilder<*>.render() {
        drawBoardBackground()
        drawBorder()
        drawBoard()
        drawCurrentPiece()
        drawNextPiece()
    }

    private fun MeshBuilder<*>.drawBorder() {
        val boardW = TetrisGame.WIDTH * game.blockSize
        val boardH = TetrisGame.HEIGHT * game.blockSize
        val thickness = game.blockSize * 0.5f
        color = MdColor.GREY tone 800
        withTransform {
            translate(0f, 0f, -thickness / 2f)
            // left border
            cube { size.set(thickness, boardH + thickness, thickness); origin.set(-thickness / 2f, boardH / 2f, 0f) }
            // right border
            cube { size.set(thickness, boardH + thickness, thickness); origin.set(boardW + thickness / 2f, boardH / 2f, 0f) }
            // bottom border
            cube { size.set(boardW + 1, thickness, thickness); origin.set(boardW / 2f, -thickness / 2f, 0f) }
        }
    }

    private fun MeshBuilder<*>.drawBoardBackground() {
        val boardW = TetrisGame.WIDTH * game.blockSize
        val boardH = TetrisGame.HEIGHT * game.blockSize
        color = MdColor.GREY tone 900
        // main background
        rect {
            origin.set(boardW / 2f, boardH / 2f, -game.blockSize / 2f)
            size.set(boardW, boardH)
        }

        //grid lines
        color = (MdColor.GREY tone 800).withAlpha(0.5f)
        for (x in 1 until TetrisGame.WIDTH) {
            line3d(
                Vec3f(x * game.blockSize, 0f, -game.blockSize/2f + 0.01f),
                Vec3f(x * game.blockSize, boardH, -game.blockSize/2f + 0.01f),
                Vec3f.Z_AXIS,
                0.02f
            )
        }
        for (y in 1 until TetrisGame.HEIGHT) {
            line3d(
                Vec3f(0f, y * game.blockSize, -game.blockSize/2f + 0.01f),
                Vec3f(boardW, y * game.blockSize, -game.blockSize/2f + 0.01f),
                Vec3f.Z_AXIS,
                0.02f
            )
        }
    }

    private fun MeshBuilder<*>.drawBoard() {
        for (y in 0 until TetrisGame.HEIGHT) {
            for (x in 0 until TetrisGame.WIDTH) {
                game.board[y][x]?.let {
                    color = it
                    renderBlock(x, y)
                }
            }
        }
    }

    private fun MeshBuilder<*>.drawCurrentPiece() {
        if (game.isGameOver.value) return

        game.getGhostPiece()?.let { ghost ->
            color = ghost.tetromino.color.withAlpha(0.25f)
            ghost.getBlocks().forEach { p ->
                if (p.y < TetrisGame.HEIGHT) {
                    renderBlock(p.x, p.y)
                }
            }
        }

        game.currentPiece?.let { piece ->
            color = piece.tetromino.color
            piece.getBlocks().forEach { p ->
                if (p.y < TetrisGame.HEIGHT) {
                    renderBlock(p.x, p.y)
                }
            }
        }
    }

    private fun MeshBuilder<*>.drawNextPiece() {
        if (game.isGameOver.value) return

        val piecesToDraw = (listOf(game.nextPiece) + game.previewPieces).take(game.numPreviews.value)
        piecesToDraw.forEachIndexed { i, tetromino ->
            color = tetromino.color

            val shape = tetromino.shapes[0]
            var minX = 100; var maxX = -100; var minY = 100; var maxY = -100
            shape.forEach {
                minX = min(minX, it.x); maxX = max(maxX, it.x)
                minY = min(minY, it.y); maxY = max(maxY, it.y)
            }
            val offX = (minX + maxX + 1) / 2f
            val offY = (minY + maxY + 1) / 2f

            withTransform {
                val previewX = (TetrisGame.WIDTH + 2) * game.blockSize
                val previewY = (TetrisGame.HEIGHT - 2) * game.blockSize - (i * 4f * game.blockSize * 0.6f)
                translate(previewX, previewY, 0f)
                scale(0.6f)

                shape.forEach { p ->
                    renderBlock(p.x - offX + 0.5f, p.y - offY + 0.5f, isCentered = true)
                }
            }
        }
    }

    private fun MeshBuilder<*>.renderBlock(x: Int, y: Int) {
        withTransform {
            translate(
                x * game.blockSize + game.blockSize / 2f,
                y * game.blockSize + game.blockSize / 2f,
                0f
            )
            renderBlockGeometry()
        }
    }

    private fun MeshBuilder<*>.renderBlock(x: Float, y: Float, isCentered: Boolean) {
        withTransform {
            val blockOffset = if (isCentered) 0f else game.blockSize / 2f
            translate(
                x * game.blockSize + blockOffset,
                y * game.blockSize + blockOffset,
                0f
            )
            renderBlockGeometry()
        }
    }

    private fun MeshBuilder<*>.renderBlockGeometry() {
        when (blockStyle.value) {
            BlockStyle.BEVELED_CUBE -> beveledCube(game.blockSize)
            BlockStyle.BEVELED_SPHERE -> beveledCubeWithSphere(game.blockSize, 0.15f)
        }
    }
}

private fun MeshBuilder<*>.beveledCube(size: Float = 1f) {
    cube {
        this.size.set(size, size, size)
    }
}

private fun MeshBuilder<*>.beveledCubeWithSphere(size: Float = 1f, bevel: Float = 0.1f) {
    val cornerR = size * bevel
    cube {
        this.size.set(size, size, size)
        if (!cornerR.isFuzzyZero()) {
            this.size.set(size - cornerR * 2, size - cornerR * 2, size - cornerR * 2)
            icoSphere {
                steps = 1
                radius = cornerR
            }
        }
    }
}