package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.*
import kotlin.math.abs
import kotlin.math.sign

class SphereGridSystem : Mesh(IndexedVertexList(listOf(
        Attribute.POSITIONS, Attribute.NORMALS, Attribute.TANGENTS, Attribute.TEXTURE_COORDS, ATTRIB_EDGE_FLAG
))) {
    private val tiles: MeshInstanceList

    private val zoomColors = listOf(
            MutableColor(MdColor.PURPLE).mix(Color.WHITE, 0.6f).toLinear(),
            MutableColor(MdColor.RED).mix(Color.WHITE, 0.6f).toLinear(),
            MutableColor(MdColor.AMBER).mix(Color.WHITE, 0.6f).toLinear(),
            MutableColor(MdColor.LIGHT_GREEN).mix(Color.WHITE, 0.6f).toLinear(),
            MutableColor(MdColor.BLUE).mix(Color.WHITE, 0.6f).toLinear(),
            MutableColor(MdColor.DEEP_PURPLE).mix(Color.WHITE, 0.6f).toLinear(),
            MutableColor(MdColor.PINK).mix(Color.WHITE, 0.6f).toLinear(),
    )

    private val front = Face(MutableColor(MdColor.RED))
    private val back = Face(MutableColor(MdColor.GREEN))
    private val right = Face(MutableColor(MdColor.BLUE))
    private val left = Face(MutableColor(MdColor.YELLOW))
    private val top = Face(MutableColor(MdColor.CYAN))
    private val bottom = Face(MutableColor(MdColor.PURPLE))

    private val faces = listOf(front, back, left, right, top, bottom)
    private var centerFace: Face? = null

    private val centerTileName = MutableVec4f()
    private val oldCenterTileName = MutableVec4f()
    private val tmpTileName = MutableVec4f()

    init {
        isFrustumChecked = false

        front.up = top
        front.right = right
        front.down = bottom
        front.left = left

        right.up = top
        right.right = back
        right.down = bottom
        right.left = front

        back.up = top
        back.right = left
        back.down = bottom
        back.left = right

        left.up = top
        left.right = front
        left.down = bottom
        left.left = back

        top.up = back
        top.right = right
        top.down = front
        top.left = left

        bottom.up = front
        bottom.right = right
        bottom.down = back
        bottom.left = left

        generate {
            tileGrid(TILE_GRID_SIZE)
        }

        tiles = MeshInstanceList(INSTANCE_ATTRIBUTES)
        instances = tiles
    }

    private fun MeshBuilder.tileGrid(gridSz: Int) {
        val nx = gridSz + 1

        for (y in 0..gridSz) {
            for (x in 0..gridSz) {
                val idx = vertex {
                    // most vertex attributes are derived from texture coordinate by the vertex shader
                    texCoord.set(x / gridSz.toFloat(), y / gridSz.toFloat())

                    getIntAttribute(ATTRIB_EDGE_FLAG)!!.i = when {
                        x == 0 && y % 2 == 1 -> EDGE_RIGHT
                        x == gridSz && y % 2 == 1 -> EDGE_LEFT
                        y == 0 && x % 2 == 1 -> EDGE_TOP
                        y == gridSz && x % 2 == 1 -> EDGE_BOTTOM
                        else -> 0
                    }
                }

                if (x > 0 && y > 0) {
                    if (x % 2 == y % 2) {
                        geometry.addTriIndices(idx - nx - 1, idx, idx - 1)
                        geometry.addTriIndices(idx - nx, idx, idx - nx - 1)
                    } else {
                        geometry.addTriIndices(idx - nx, idx, idx - 1)
                        geometry.addTriIndices(idx - nx, idx - 1, idx - nx - 1)
                    }
                }
            }
        }
    }

    fun updateTiles(camPos: Vec3f) {
        val localCamPos = toLocalCoords(MutableVec3f(camPos), 1f)

        val d = localCamPos.length()
        if (d == 0f) {
            return
        }
        val nrmPos = MutableVec3f(localCamPos).scale(1f / d)

        //val zoom = (5f / (d - 1).clamp(1f, 5f)).roundToInt() - 1
        val zoom = 6
        val sz = 1 shl zoom

        val (uv, face) = cubeFaceUv(nrmPos)
        val camX = (uv.x * sz).toInt()
        val camY = (uv.y * sz).toInt()

        encodeTileName(camX, camY, zoom, centerTileName)
        if (centerTileName != oldCenterTileName) {
            oldCenterTileName.set(centerTileName)

            if (face !== centerFace) {
                centerFace = face
                applyRotations()
            }

            tiles.clear()
            for (i in faces.indices) {
                faces[i].tileCnt = 0
            }

            face.addTile(camX, camY, zoom, ZoomLevelEdge.NO_EDGE)
            face.buildGrid(camX, camY, 1, 1, zoom)

            for (i in faces.indices) {
                if (faces[i].tileCnt == 0) {
                    faces[i].addTile(0, 0, 0, ZoomLevelEdge.NO_EDGE)
                }
            }
        }
    }

    private fun applyRotations() {
        front.mat.setIdentity()
        back.mat.setIdentity()
        left.mat.setIdentity()
        right.mat.setIdentity()
        top.mat.setIdentity()
        bottom.mat.setIdentity()

        when {
            centerFace === right -> {
                top.mat.rotate(90f, Vec3f.Y_AXIS)
                bottom.mat.rotate(90f, Vec3f.Y_AXIS)
            }
            centerFace === left -> {
                top.mat.rotate(-90f, Vec3f.Y_AXIS)
                bottom.mat.rotate(-90f, Vec3f.Y_AXIS)
            }
            centerFace === back -> {
                top.mat.rotate(180f, Vec3f.Y_AXIS)
                bottom.mat.rotate(180f, Vec3f.Y_AXIS)
            }
            centerFace === top -> {
                right.mat.rotate(90f, Vec3f.NEG_X_AXIS)
                back.mat.rotate(180f, Vec3f.Z_AXIS)
                left.mat.rotate(90f, Vec3f.NEG_X_AXIS)
            }
            centerFace === bottom -> {
                right.mat.rotate(90f, Vec3f.X_AXIS)
                back.mat.rotate(180f, Vec3f.Z_AXIS)
                left.mat.rotate(90f, Vec3f.X_AXIS)
            }
        }

        right.mat.rotate(90f, Vec3f.Y_AXIS)
        back.mat.rotate(180f, Vec3f.Y_AXIS)
        left.mat.rotate(270f, Vec3f.Y_AXIS)
        top.mat.rotate(90f, Vec3f.NEG_X_AXIS)
        bottom.mat.rotate(-90f, Vec3f.NEG_X_AXIS)
    }

    private class ZoomLevelEdge(val left: Int, val right: Int, val top: Int, val bottom: Int) {
        fun getEdgeMask(x: Int, y: Int): Int {
            var mask = 0
            if (x == left) {
                mask = mask or EDGE_LEFT
            }
            if (x == right) {
                mask = mask or EDGE_RIGHT
            }
            if (y == top) {
                mask = mask or EDGE_TOP
            }
            if (y == bottom) {
                mask = mask or EDGE_BOTTOM
            }
            return mask
        }

        companion object {
            val NO_EDGE = ZoomLevelEdge(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
        }
    }

    private inner class Face(val faceColor: MutableColor) {
        var up: Face? = null
        var right: Face? = null
        var down: Face? = null
        var left: Face? = null

        val mat = Mat4f()
        var tileCnt = 0

        fun buildGrid(innerX: Int, innerY: Int, innerW: Int, innerH: Int, zoom: Int) {
            val left = innerX - 1
            val right = innerX + innerW
            val top = innerY - 1
            val bottom = innerY + innerH

            val leftEd = if (left % 2 != 0) { left - 1 } else left
            val rightEd = if (right % 2 == 0) { right + 1 } else right
            val topEd = if (top % 2 != 0) { top - 1 } else top
            val bottomEd = if (bottom % 2 == 0) { bottom + 1 } else bottom
            val zoomEdge = ZoomLevelEdge(leftEd, rightEd, topEd, bottomEd)

            addTileRow(left, right, top, zoom, zoomEdge)
            addTileRow(left, right, bottom, zoom, zoomEdge)
            addTileCol(top, bottom, left, zoom, zoomEdge)
            addTileCol(top, bottom, right, zoom, zoomEdge)

            if (left != leftEd) {
                addTileCol(top, bottom, leftEd, zoom, zoomEdge)
            }
            if (right != rightEd) {
                addTileCol(top, bottom, rightEd, zoom, zoomEdge)
            }
            if (top != topEd) {
                addTileRow(leftEd, rightEd, topEd, zoom, zoomEdge)
            }
            if (bottom != bottomEd) {
                addTileRow(leftEd, rightEd, bottomEd, zoom, zoomEdge)
            }

            if (zoom > 1) {
                buildGrid(leftEd / 2, topEd / 2, (rightEd - leftEd + 1) / 2, (bottomEd - topEd + 1) / 2, zoom - 1)
            }
        }

        fun addTileRow(fromX: Int, toX: Int, y: Int, zoom: Int, edge: ZoomLevelEdge) {
            for (x in fromX..toX) {
                addTile(x, y, zoom, edge)
            }
        }

        fun addTileCol(fromY: Int, toY: Int, x: Int, zoom: Int, edge: ZoomLevelEdge) {
            for (y in fromY..toY) {
                addTile(x, y, zoom, edge)
            }
        }

        fun addTile(x: Int, y: Int, zoom: Int, edge: ZoomLevelEdge, xOffset: Int = 0, yOffset: Int = 0) {
            val edgeMask = edge.getEdgeMask(x, y).toFloat()

            val ox = x + xOffset
            val oy = y + yOffset
            val max = 1 shl zoom
            if (ox in 0 until max && oy in 0 until max) {
                tileCnt++
                tiles.addInstance {
                    put(mat.matrix)
                    put(encodeTileName(ox, oy, zoom, tmpTileName).array)
                    put(edgeMask)
                    put(zoomColors[zoom].array)
//                    put(faceColor.array)
                }

            } else {
                if (x in -max until 0 && y in 0 until max) {
                    left?.addTile(x, y, zoom, edge, xOffset = max)
                } else if (x in max until max * 2 && y in 0 until max) {
                    right?.addTile(x, y, zoom, edge, xOffset = -max)

                } else if (x in 0 until max && y in -max until 0) {
                    up?.addTile(x, y, zoom, edge, yOffset = max)
                } else if (x in 0 until max && y in max until max * 2) {
                    down?.addTile(x, y, zoom, edge, yOffset = -max)
                }
            }
        }
    }

    private fun encodeTileName(x: Int, y: Int, zoom: Int, result: MutableVec4f): MutableVec4f {
        val sz = 1 shl zoom
        val uvRange = if (x == sz / 2 - 1) 0.99999f else 1f
        return result.set(x.toFloat(), y.toFloat(), sz.toFloat(), uvRange)
    }

    private fun cubeFaceUv(normalPos: Vec3f): Pair<Vec2f, Face> {
        val absX = abs(normalPos.x)
        val absY = abs(normalPos.y)
        val absZ = abs(normalPos.z)

        val maxLen: Float
        val uvx: Float
        val uvy: Float
        val face: Face

        if (absX > absY && absX > absZ) {
            maxLen = absX
            uvx = -normalPos.z * sign(normalPos.x)
            uvy = -normalPos.y
            face = if (normalPos.x > 0) right else left

        } else if (absY > absX && absY > absZ) {
            maxLen = absY
            uvx = normalPos.x
            uvy = normalPos.z * sign(normalPos.y)
            face = if (normalPos.y > 0) top else bottom

        } else {
            maxLen = absZ
            uvx = normalPos.x * sign(normalPos.z)
            uvy = -normalPos.y
            face = if (normalPos.z > 0) front else back
        }

        return Vec2f(0.5f * (uvx / maxLen + 1f), 0.5f * (uvy / maxLen + 1f)) to face
    }

    companion object {
        const val TILE_GRID_SIZE = 40

        const val EDGE_LEFT = 1
        const val EDGE_RIGHT = 2
        const val EDGE_TOP = 4
        const val EDGE_BOTTOM = 8

        val ATTRIB_EDGE_FLAG = Attribute("aEdgeFlag", GlslType.INT)
        val ATTRIB_EDGE_MASK = Attribute("aEdgeMask", GlslType.FLOAT)
        val ATTRIB_TILE_NAME = Attribute("aTileName", GlslType.VEC_4F)

        private val INSTANCE_ATTRIBUTES = listOf(MeshInstanceList.MODEL_MAT, ATTRIB_TILE_NAME, ATTRIB_EDGE_MASK, Attribute.COLORS)
    }
}