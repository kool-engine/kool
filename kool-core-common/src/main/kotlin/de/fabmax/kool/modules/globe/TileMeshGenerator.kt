package de.fabmax.kool.modules.globe

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.modules.globe.elevation.ElevationMap
import de.fabmax.kool.util.MeshBuilder
import kotlinx.coroutines.experimental.delay
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

        val steps = 1 shl stepsLog2
        val zoomDiv = 2 * PI / (1 shl (tileMesh.tileName.zoom + stepsLog2)).toDouble()
        val heightResolution = (tileMesh.tileName.latN - tileMesh.tileName.latS) / steps * 3600.0

        //println("${tileMesh.tileName} -> res = $heightResolution")

        val pos = MutableVec3d()
        val nrm = MutableVec3f()
        val posf = MutableVec3f()
        var map: ElevationMap? = null

        for (row in 0..steps) {
            val tys = (tileMesh.tileName.y+1) * steps - row
            val latRad = atan(sinh(PI - tys * zoomDiv))
            for (i in 0..steps) {
                val lonRad = (tileMesh.tileName.x * steps + i) * zoomDiv - PI

                val lat = latRad.toDeg()
                val lon = lonRad.toDeg()

                if (map == null || !map.contains(lat, lon)) {
                    map = globe.elevationMapProvider.getElevationMapAt(lat, lon, heightResolution)

                    map?.meta?.apply {
                        tileMesh.attributionInfo += TileMesh.AttributionInfo("Elevation-Data: $attr", null)
                    }
                }
                val height = map?.run {
                    while (!isAvailable) {
                        // wait a bit while height map data is loading
                        delay(50)
                    }
                    getElevationAt(lat, lon)
                } ?: 0.0

                latLonToCartesian(latRad, lonRad, globe.radius + height, pos)

                //globe.elevationMapProvider.getNormalAt(latDeg, lonDeg, heightResolution, nrm)
                //nrm.rotate(latDeg.toFloat(), Vec3f.NEG_X_AXIS).rotate(lonDeg.toFloat(), Vec3f.Y_AXIS)

                if (frame != null) {
                    frame.transformToLocal.transform(pos, 1.0)
                    //frame.transformToLocal.transform(nrm, 0f)
                }

                val uv = Vec2f(i.toFloat() / steps, 1f - row.toFloat() / steps)
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
