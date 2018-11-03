package de.fabmax.kool.modules.globe

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.globe.elevation.ElevationMapProvider
import de.fabmax.kool.modules.globe.elevation.NullElevationMap
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.doubleprec.TransformGroupDp
import kotlin.math.*

class Globe(val radius: Double, name: String? = null) : TransformGroupDp(name) {

    var meterPerPxLvl0: Double = 156e3
    val frameZoomLvl = 11
    val frameZoomThresh = 14
    val minZoomLvl: Int get() = tileManager.minZoomLvl
    val maxZoomLvl: Int get() = tileManager.maxZoomLvl

    var centerLat = 0.0
        private set
    var centerLon = 0.0
        private set
    var cameraHeight = 0.0
        private set

    val tileManager = TileManager(this)
    var elevationMapProvider: ElevationMapProvider = NullElevationMap()
    var meshGenerator = GridTileMeshGenerator()
    var meshDetailLevel = 5
    var tileShaderProvider = OsmTexImageTileShaderProvider()

    private val tileFrames = mutableMapOf<Long, TileFrame>()
    private val zoomGroups = mutableListOf<Group>()
    private val removeTiles = mutableListOf<TileMesh>()

    private val camPosition = MutableVec3f()
    private val camDirection = MutableVec3f()

    private var prevCamHeight = 0f
    private var prevLat = 0.0
    private var prevLon = 0.0

    private val tmpVec = MutableVec3f()

    init {
        translate(0.0, 0.0, -radius)

        for (i in tileManager.minZoomLvl..frameZoomThresh) {
            val grp = Group()
            zoomGroups += grp
            +grp
        }
    }

    override fun preRenderDp(ctx: KoolContext, modelMatDp: Mat4dStack) {
        // make sure default tile texture is loaded before other tiles block the texture loading queue
        TileMesh.prepareDefaultTex(ctx)

        val cam = scene?.camera
        if (cam != null && cam is PerspectiveCamera) {
            toGlobalCoords(tmpVec.set(Vec3f.ZERO))
            tmpVec.subtract(cam.globalPos)
            cameraHeight = tmpVec.length().toDouble() - radius

            // determine lat/lng of camera center
            val camDist = cam.globalPos.length()
            camPosition.set(Vec3f.Z_AXIS).scale(camDist)
            toLocalCoords(camPosition)
            camPosition.norm(camDirection)

            cam.clipNear = camDist * 0.05f
            cam.clipFar = camDist * 10f

            val dh = if (camDist > prevCamHeight) { prevCamHeight / camDist } else { camDist / prevCamHeight }
            prevCamHeight = camDist

            val lat = (PI * 0.5 - acos(camDirection.y.toDouble())).clamp(-RAD_85, RAD_85)
            val lon = atan2(camDirection.x.toDouble(), camDirection.z.toDouble())

            val isMoving = dh < 0.99f || abs(lat - prevLat) > 1e-5 || abs(lon - prevLon) > 1e-5
            prevLat = lat
            prevLon = lon
            centerLat = lat.toDeg()
            centerLon = lon.toDeg()

            // determine best zoom level
            camDirection.scale(radius.toFloat())
            val camHeight = camDirection.distance(camPosition)
            val meterPerPx = camHeight * tan(cam.fovy.toRad() * 0.5f) * 2f / (ctx.viewport.height * 96f / ctx.screenDpi)
            val centerZoom = getBestZoom(meterPerPx, lat)

            val newCenter = TileName.forLatLon(lat.toDeg(), lon.toDeg(), centerZoom)
            tileManager.updateCenter(newCenter, isMoving, ctx)
        }

        tileManager.onPreRender(ctx)

        // delete all tiles scheduled for removal
        if (!removeTiles.isEmpty()) {
            removeTiles.forEach {
                if (it.isRemovable) {
                    tileManager.onTileDeleted(it)
                    deleteTile(it)
                    it.dispose(ctx)
                }
//                else {
//                    logD { "tile is not removable anymore: ${it.tileName}" }
//                }
            }
            removeTiles.clear()
        }

        super.preRenderDp(ctx, modelMatDp)
    }

    fun addTile(mesh: TileMesh) {
        val parentFrame = getTileFrame(mesh.tileName)
        if (parentFrame != null) {
            parentFrame.addTile(mesh)
        } else {
            getZoomGroup(mesh.tileName.zoom) += mesh
        }
    }

    fun removeTile(mesh: TileMesh) {
        removeTiles += mesh
    }

    fun tileLoaded(tileMesh: TileMesh) {
        tileManager.onTileLoaded(tileMesh)
    }

    private fun deleteTile(tile: TileMesh) {
        val frame = getTileFrame(tile.tileName)
        if (frame != null) {
            frame.removeTile(tile)
            if (frame.tileCount == 0) {
                tileFrames.remove(frame.tileName.fusedKey)
                this -= frame
                //logD { "removed tile frame ${frame.tileName}, ${tileFrames.size} frames remaining" }
            }

        } else {
            getZoomGroup(tile.tileName.zoom).removeNode(tile)
        }
    }

    private fun getBestZoom(meterPerPx: Float, lat: Double): Int =
            round(0.2f + log2(meterPerPxLvl0 / meterPerPx * cos(lat)))
                    .clamp(minZoomLvl.toDouble(), maxZoomLvl.toDouble()).toInt()

    fun getZoomGroup(level: Int) = zoomGroups[level - minZoomLvl]

    fun getTileFrame(tileName: TileName): TileFrame? {
        return if (tileName.zoom < frameZoomThresh) { null } else {
            val div = 1 shl (tileName.zoom - frameZoomLvl)
            val frameX = tileName.x / div
            val frameY = tileName.y / div
            val frameKey = TileName.fuesdKey(frameX, frameY, frameZoomLvl)

            tileFrames.getOrPut(frameKey) {
                val frame = TileFrame(TileName(frameX, frameY, frameZoomLvl), this)
                this += frame
                //logD { "added tile frame ${frame.tileName}" }
                frame
            }
        }
    }

    companion object {
        private val RAD_85 = 85.0.toRad()
        const val ALLOWED_MESH_REFINEMENTS_PER_FRAME = 1
    }
}