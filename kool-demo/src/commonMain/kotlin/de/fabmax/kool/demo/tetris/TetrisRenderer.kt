package de.fabmax.kool.demo.tetris

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.isFuzzyZero
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.MdColor

internal enum class BlockStyle(val displayName: String) {
    BEVELED_CUBE("Simple Cubes"),
    BEVELED_SPHERE("Rounded Cubes")
}

internal class TetrisRenderer(private val game: TetrisGame) {
    val blockStyle = mutableStateOf(BlockStyle.BEVELED_SPHERE)

    fun MeshBuilder.render() {
        drawBoardBackground()
        drawBorder()
        drawBoard()
        drawCurrentPiece()
    }

    private fun MeshBuilder.drawBorder() {
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

    private fun MeshBuilder.drawBoardBackground() {
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

    private fun MeshBuilder.drawBoard() {
        for (y in 0 until TetrisGame.HEIGHT) {
            for (x in 0 until TetrisGame.WIDTH) {
                game.board[y][x]?.let {
                    color = it
                    withTransform {
                        translate(
                            x * game.blockSize + game.blockSize / 2f,
                            y * game.blockSize + game.blockSize / 2f,
                            0f
                        )
                        when (blockStyle.value) {
                            BlockStyle.BEVELED_CUBE -> beveledCube(game.blockSize)
                            BlockStyle.BEVELED_SPHERE -> beveledCubeWithSphere(game.blockSize, 0.15f)
                        }
                    }
                }
            }
        }
    }

    private fun MeshBuilder.drawCurrentPiece() {
        if (game.isGameOver.value) return

        fun renderBlock(blocks: Piece) {
            blocks.getBlocks().forEach { p ->
                if (p.y < TetrisGame.HEIGHT) {
                    withTransform {
                        translate(
                            p.x * game.blockSize + game.blockSize / 2f,
                            p.y * game.blockSize + game.blockSize / 2f,
                            0f
                        )
                        when (blockStyle.value) {
                            BlockStyle.BEVELED_CUBE -> beveledCube(game.blockSize)
                            BlockStyle.BEVELED_SPHERE -> beveledCubeWithSphere(game.blockSize, 0.15f)
                        }
                    }
                }
            }
        }

        // drawing a ghostly figure
        game.getGhostPiece()?.let { ghost ->
            color = ghost.tetromino.color.withAlpha(0.25f)
            renderBlock(ghost)
        }

        // drawing the current shape
        game.currentPiece?.let { piece ->
            color = piece.tetromino.color
            renderBlock(piece)
        }
    }
}

private fun MeshBuilder.beveledCube(size: Float = 1f) {
    cube {
        this.size.set(size, size, size)
    }
}

private fun MeshBuilder.beveledCubeWithSphere(size: Float = 1f, bevel: Float = 0.1f) {
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