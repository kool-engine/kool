package de.fabmax.kool.demo.globe

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.doubleprec.TransformGroupDp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class TileFrame(val tileName: TileName, private val globe: Globe) : TransformGroupDp() {

    val transformToLocal: Mat4d get() = invTransform
    val transformToGlobal: Mat4d get() = transform

    val zoomGroups = mutableListOf<Group>()
    var tileCount = 0
        private set

    init {
        rotate(tileName.center.lon, 0.0, 1.0, 0.0)
        rotate(90.0 - tileName.center.lat, 1.0, 0.0, 0.0)
        translate(0.0, globe.radius, 0.0)
        checkInverse()

        for (i in tileName.zoom..globe.maxZoomLvl) {
            val grp = Group()
            zoomGroups += grp
            +grp
        }
    }

    fun addTile(tile: TileMesh) {
        getZoomGroup(tile.tileName.zoom) += tile
        tileCount++
    }


    fun removeTile(tile: TileMesh) {
        getZoomGroup(tile.tileName.zoom) -= tile
        tileCount--
    }

    private fun getZoomGroup(level: Int) = zoomGroups[level - tileName.zoom]

    fun toLocalPosition(latRad: Double, lonRad: Double, result: MutableVec3d): MutableVec3d {
        return transformToLocal.transform(latLonToCartesian(latRad, lonRad, globe.radius, result))
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