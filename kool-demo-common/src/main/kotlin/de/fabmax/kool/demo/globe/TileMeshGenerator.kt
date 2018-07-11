package de.fabmax.kool.demo.globe

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.util.MeshBuilder
import kotlinx.coroutines.experimental.yield
import kotlin.math.*

interface TileMeshGenerator {
    suspend fun generateMesh(globe: Globe, tileMesh: TileMesh, stepsLog2: Int)
}

open class GridTileMeshGenerator : TileMeshGenerator {

    private fun getFrame(globe: Globe, tileName: TileName): TileFrame? {
        return if (tileName.zoom < globe.frameZoomThresh) {
            null
        } else {
            globe.getTileFrame(tileName)
        }
    }

    override suspend fun generateMesh(globe: Globe, tileMesh: TileMesh, stepsLog2: Int) {
        val frame = getFrame(globe, tileMesh.tileName)
        val builder = MeshBuilder(tileMesh.meshData)

        tileMesh.meshData.isBatchUpdate = true

        val uvScale = 255f / 256f
        val uvOff = 0.5f / 256f

        val steps = 1 shl stepsLog2
        val zoomDiv = 2 * PI / (1 shl (tileMesh.tileName.zoom + stepsLog2)).toDouble()
        val heightResolution = (tileMesh.tileName.latN - tileMesh.tileName.latS) / steps * 3600 * 1000

        //println("${tileMesh.tileName} -> res = $heightResolution")

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
                val height = globe.heightMap.getHeightAt(latDeg, lonDeg, heightResolution)
                latLonToCartesian(lat, lon, globe.radius + height, pos)

                //globe.heightMap.getNormalAt(latDeg, lonDeg, heightResolution, nrm)
                //nrm.rotate(latDeg.toFloat(), Vec3f.NEG_X_AXIS).rotate(lonDeg.toFloat(), Vec3f.Y_AXIS)

                if (frame != null) {
                    frame.transformToLocal.transform(pos, 1.0)
                    //frame.transformToLocal.transform(nrm, 0f)
                }

                val uv = Vec2f((i.toFloat() / steps) * uvScale + uvOff, 1f - ((row.toFloat() / steps) * uvScale + uvOff))
                val iv = builder.vertex(pos.toMutableVec3f(posf), nrm, uv)
                if (i > 0 && row > 0) {
                    tileMesh.meshData.addTriIndices(iv - steps - 2, iv, iv - 1)
                    tileMesh.meshData.addTriIndices(iv - steps - 2, iv - steps - 1, iv)
                }
            }
            yield()
        }

        tileMesh.meshData.generateNormals()
        tileMesh.meshData.isBatchUpdate = false
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
