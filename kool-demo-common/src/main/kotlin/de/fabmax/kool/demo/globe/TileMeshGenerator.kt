package de.fabmax.kool.demo.globe

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.toDeg
import kotlin.math.*

interface TileMeshGenerator {
    fun generateMesh(globe: Globe, tileMesh: TileMesh, stepsLog2: Int)
}

open class GridTileMeshGenerator : TileMeshGenerator {

    private fun getFrame(globe: Globe, tileName: TileName): TileFrame? {
        return if (tileName.zoom < globe.frameZoomThresh) {
            null
        } else {
            globe.getTileFrame(tileName)
        }
    }

    override fun generateMesh(globe: Globe, tileMesh: TileMesh, stepsLog2: Int) {
        val frame = getFrame(globe, tileMesh.tileName)
        tileMesh.generator = {
            val uvScale = 255f / 256f
            val uvOff = 0.5f / 256f

            val steps = 1 shl stepsLog2
            val zoomDiv = 2 * PI / (1 shl (tileMesh.tileName.zoom + stepsLog2)).toDouble()

            val pos = MutableVec3d()
            val nrm = MutableVec3f()
            val posf = MutableVec3f()

            for (row in 0..steps) {
                val tys = (tileMesh.tileName.y+1) * steps - row
                val lat = atan(sinh(PI - tys * zoomDiv))
                for (i in 0..steps) {
                    val lon = (tileMesh.tileName.x * steps + i) * zoomDiv - PI

                    val latDeg = lat.toDeg()
                    val lonDeg = lon.toDeg()
                    val height = globe.heightMap.getHeightAt(latDeg, lonDeg, tileMesh.tileName)
                    latLonToCartesian(lat, lon, globe.radius + height, pos)
                    //globe.heightMap.getNormalAt(latDeg, lonDeg, tileMesh.tileName, nrm)
                    //nrm.rotate(latDeg.toFloat(), Vec3f.NEG_X_AXIS).rotate(lonDeg.toFloat(), Vec3f.Y_AXIS)

                    if (frame != null) {
                        frame.transformToLocal.transform(pos, 1.0)
                        //frame.transformToLocal.transform(nrm, 0f)
                    }

                    val uv = Vec2f((i.toFloat() / steps) * uvScale + uvOff, 1f - ((row.toFloat() / steps) * uvScale + uvOff))
                    val iv = vertex(pos.toMutableVec3f(posf), nrm, uv)
                    if (i > 0 && row > 0) {
                        meshData.addTriIndices(iv - steps - 2, iv, iv - 1)
                        meshData.addTriIndices(iv - steps - 2, iv - steps - 1, iv)
                    }
                }
            }
            meshData.generateNormals()
        }
        tileMesh.generateGeometry()
    }

    companion object {
        fun latLonToCartesian(latRad: Double, lonRad: Double, radius: Double, result: MutableVec3d): MutableVec3d {
            val theta = PI * 0.5 - latRad
            result.x = sin(theta) * sin(lonRad) * radius
            result.z = sin(theta) * cos(lonRad) * radius
            result.y = cos(theta) * radius
            return result
        }
    }
}
