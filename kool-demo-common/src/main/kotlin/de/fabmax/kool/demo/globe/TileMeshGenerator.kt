package de.fabmax.kool.demo.globe

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.sinh

interface TileMeshGenerator {
    fun generateMesh(globe: Globe, tileMesh: TileMesh): TileFrame?
}

open class FlatTileMeshGenerator : TileMeshGenerator {

    override fun generateMesh(globe: Globe, tileMesh: TileMesh): TileFrame? {
        return if (tileMesh.tileName.zoom < globe.frameZoomThresh) {
            generateMesh(globe, tileMesh, null)
            null

        } else {
            val div = 1 shl (tileMesh.tileName.zoom - globe.frameZoomLvl)
            val frameTile = TileName(tileMesh.tileName.x / div, tileMesh.tileName.y / div, globe.frameZoomLvl)
            val frame = globe.getTileFrame(frameTile)
            generateMesh(globe, tileMesh, frame)
            frame
        }
    }

    private fun generateMesh(globe: Globe, tileMesh: TileMesh, frame: TileFrame?) {
        tileMesh.generator = {
            val uvScale = 255f / 256f
            val uvOff = 0.5f / 256f

            val stepsExp = 4
            val steps = 1 shl stepsExp
            val zoomDiv = 2 * PI / (1 shl (tileMesh.tileName.zoom + stepsExp)).toDouble()

            val pos = MutableVec3d()
            val nrm = MutableVec3d()
            val posf = MutableVec3f()
            val nrmf = MutableVec3f()

            for (row in 0..steps) {
                val tys = (tileMesh.tileName.y+1) * steps - row
                val lat = atan(sinh(PI - tys * zoomDiv))
                for (i in 0..steps) {
                    val lon = (tileMesh.tileName.x * steps + i) * zoomDiv - PI

                    TileFrame.latLonToCartesian(lat, lon, globe.radius, pos)
                    pos.norm(nrm)
                    if (frame != null) {
                        frame.transformToLocal.transform(pos, 1.0)
                        frame.transformToLocal.transform(nrm, 0.0)
                    }

                    val uv = Vec2f((i.toFloat() / steps) * uvScale + uvOff, 1f - ((row.toFloat() / steps) * uvScale + uvOff))
                    val iv = vertex(pos.toMutableVec3f(posf), nrm.toMutableVec3f(nrmf), uv)

                    if (i > 0 && row > 0) {
                        meshData.addTriIndices(iv - steps - 2, iv, iv - 1)
                        meshData.addTriIndices(iv - steps - 2, iv - steps - 1, iv)

                        if (row == steps / 2 && i == steps / 2) {
                            // normal of center vertex == center normal of tile
                            nrm.toMutableVec3f(tileMesh.centerNormal)
                        }
                    }
                }
            }
        }
        tileMesh.generateGeometry()
    }
}
