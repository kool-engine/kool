package de.fabmax.kool.demo.globe

import de.fabmax.kool.KoolContext
import de.fabmax.kool.gl.GL_ALWAYS
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.doubleprec.TransformGroupDp
import de.fabmax.kool.util.logD
import kotlin.math.*

class Globe(val radius: Double, name: String? = null) : TransformGroupDp(name) {

    var meterPerPxLvl0: Double = 156e3
    var maxTiles = 300

    val minZoomLvl = 3
    val maxZoomLvl = 19
    val frameZoomLvl = 11
    val frameZoomThresh = 14

    var centerLat = 0.0
        private set
    var centerLon = 0.0
        private set
    var cameraHeight = 0.0
        private set

    private val meshGenerator = FlatTileMeshGenerator()
    val tileShaderProvider = OsmTexImageTileShaderProvider()

    private val tileFrames = mutableMapOf<Long, TileFrame>()
    private val zoomGroups = mutableListOf<Group>()

    private val tiles = mutableMapOf<Long, TileMesh>()
    private val loadingTiles = mutableSetOf<Long>()
    private val removableTiles = mutableMapOf<Long, TileMesh>()
    private val removeTiles = mutableListOf<TileMesh>()

    private val camPosition = MutableVec3f()
    private val camDirection = MutableVec3f()
    private var center = TileName(0, 0, 1)

    private var prevCamHeight = 0f
    private var prevLat = 0.0
    private var prevLon = 0.0

    private val tmpVec = MutableVec3f()

    init {
        translate(0.0, 0.0, -radius)

        for (i in minZoomLvl..frameZoomThresh) {
            val grp = Group()
            zoomGroups += grp
            +grp
        }
    }

    override fun preRenderDp(ctx: KoolContext, modelMatDp: Mat4dStack) {
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
            if (newCenter != center && (tiles.size < maxTiles || !isMoving)) {
                //logD { "new center: $newCenter ${tiles.size}" }
                center = newCenter
                rebuildMesh(ctx)
            }
        }

        super.preRenderDp(ctx, modelMatDp)

        if (!removeTiles.isEmpty()) {
            removeTiles.forEach {
                loadingTiles.remove(it.key)
                tiles.remove(it.key)
                removableTiles.remove(it.key)

                deleteTile(it)
                it.dispose(ctx)
            }
            removeTiles.clear()
        }
    }

    override fun renderDp(ctx: KoolContext, modelMatDp: Mat4dStack) {
        ctx.pushAttributes()
        ctx.depthFunc = GL_ALWAYS
        ctx.applyAttributes()
        super.renderDp(ctx, modelMatDp)
        ctx.popAttributes()
    }

    private fun deleteTile(tile: TileMesh) {
        if (tile.tileName.zoom >= frameZoomThresh) {
            val frame = getTileFrame(tile.tileName)
            frame.removeTile(tile)

            if (frame.tileCount == 0) {
                tileFrames.remove(frame.tileName.fusedKey)
                this -= frame
                logD { "removed tile frame ${frame.tileName}, ${tileFrames.size} frames remaining" }
            }

        } else {
            getZoomGroup(tile.tileName.zoom).removeNode(tile)
        }
    }

    private fun getBestZoom(meterPerPx: Float, lat: Double): Int =
            round(0.2f + log2(meterPerPxLvl0 / meterPerPx * cos(lat)))
                    .clamp(minZoomLvl.toDouble(), maxZoomLvl.toDouble()).toInt()

    private fun rebuildMesh(ctx: KoolContext) {
        removableTiles.putAll(tiles)

        val rng = 5

        var zoom = center.zoom
        var xStart = (center.x - rng + 1) and 1.inv()
        var xEnd = ((center.x + rng + 1) and 1.inv()) - 1
        var yStart = (center.y - rng + 1) and 1.inv()
        var yEnd = ((center.y + rng + 1) and 1.inv()) - 1

        addMeshesWrappingX(xStart, xEnd, yStart, yEnd, zoom, ctx)

        for (i in 1..4) {
            zoom--
            if (zoom >= minZoomLvl) {
                val xStShf = xStart shr 1
                val xEdShf = (xEnd + 1) shr 1
                val yStShf = yStart shr 1
                val yEdShf = (yEnd + 1) shr 1

                xStart = (xStShf - 1) and 1.inv()
                xEnd = (xEdShf and 1.inv()) + 1
                yStart = (yStShf - 1) and 1.inv()
                yEnd = (yEdShf and 1.inv()) + 1

                addMeshesWrappingX(xStart, xStShf-1, yStart, yEnd, zoom, ctx)
                addMeshesWrappingX(xEdShf, xEnd, yStart, yEnd, zoom, ctx)

                addMeshesWrappingX(xStShf, xEdShf-1, yStart, yStShf-1, zoom, ctx)
                addMeshesWrappingX(xStShf, xEdShf-1, yEdShf, yEnd, zoom, ctx)
            } else {
                break
            }
        }

        val forceRemoveThresh = (maxTiles * 1.3).toInt()
        if (tiles.size > forceRemoveThresh) {
            // queue is getting too large, remove stuff even though it might be visible
            val rmQueue = mutableListOf<TileMesh>().apply { addAll(removableTiles.values) }
            rmQueue.sortBy { m -> if (!m.isTexLoaded) { Int.MIN_VALUE } else { -abs(m.tileName.zoom - center.zoom) } }
            for (i in 0..(tiles.size - forceRemoveThresh)) {
                removeTileMesh(rmQueue[i], true)
            }
        }
    }

    private fun addMeshesWrappingX(xStart: Int, xEnd: Int, yStart: Int, yEnd: Int, zoom: Int, ctx: KoolContext) {
        val size = 1 shl zoom
        val ys = max(0, yStart)
        val ye = min(size - 1, yEnd)

        addMeshes(max(0, xStart)..min(size - 1, xEnd), ys..ye, zoom, ctx)
        if (xStart < 0 && xEnd < size - 1) {
            // wrap around 180° long
            addMeshes(max(size + xStart, xEnd) until size, ys..ye, zoom, ctx)
        } else if (xStart > 0 && xEnd > size - 1) {
            // wrap around 180° long
            addMeshes(0..min(xStart, xEnd - (size - 1)), ys..ye, zoom, ctx)
        }
    }

    private fun addMeshes(xRng: IntRange, yRng: IntRange, zoom: Int, ctx: KoolContext) {
        if (xRng.last - xRng.first > 2 && yRng.last - yRng.first > 2) {
            addMeshesCircular(xRng, yRng, zoom, ctx)
        } else {
            addMeshesRectRange(xRng, yRng, zoom, ctx)
        }
    }

    private fun addMeshesRectRange(xRng: IntRange, yRng: IntRange, zoom: Int, ctx: KoolContext) {
        for (x in xRng) {
            for (y in yRng) {
                addTile(x, y, zoom, xRng, yRng, ctx)
            }
        }
    }

    private fun addMeshesCircular(xRng: IntRange, yRng: IntRange, zoom: Int, ctx: KoolContext) {
        val cx = xRng.first + (xRng.last - xRng.first) / 2
        val cy = yRng.first + (yRng.last - yRng.first) / 2
        val r = max(max(cx - xRng.first, xRng.last - cx), max(cy - yRng.first, yRng.last - cy))

        for (i in 0..r) {
            for (x in cx - i..cx + i) {
                addTile(x, cy - i, zoom, xRng, yRng, ctx)
                if (i > 0) {
                    addTile(x, cy + i, zoom, xRng, yRng, ctx)
                }
            }
            if (i > 0) {
                for (y in cy - i + 1..cy + i - 1) {
                    addTile(cx - i, y, zoom, xRng, yRng, ctx)
                    addTile(cx + i, y, zoom, xRng, yRng, ctx)
                }
            }
        }
    }

    private fun addTile(x: Int, y: Int, zoom: Int, xRng: IntRange, yRng: IntRange, ctx: KoolContext) {
        if (x in xRng && y in yRng) {
            val key = TileName.fuesdKey(x, y, zoom)
            val existing = tiles[key]
            if (existing != null) {
                removableTiles.remove(key)
                existing.isFadingOut = false
                if (!existing.isLoaded) {
                    loadingTiles += key
                }
            } else {
                val mesh = TileMesh(this, TileName(x, y, zoom), ctx)
                val parentFrame = meshGenerator.generateMesh(this, mesh)
                tiles[key] = mesh
                loadingTiles += key

                if (parentFrame != null) {
                    parentFrame.addTile(mesh)
                } else {
                    getZoomGroup(zoom) += mesh
                }
            }
        }
    }

    private fun removeTileMesh(mesh: TileMesh, forceRemove: Boolean) {
        if (mesh.isCurrentlyVisible && !forceRemove) {
            mesh.isFadingOut = true
        } else {
            removeTiles += mesh
        }
    }

    fun getZoomGroup(level: Int) = zoomGroups[level - minZoomLvl]

    fun getTileFrame(tileName: TileName): TileFrame {
        val div = 1 shl (tileName.zoom - frameZoomLvl)
        val frameX = tileName.x / div
        val frameY = tileName.y / div
        val frameKey = TileName.fuesdKey(frameX, frameY, frameZoomLvl)

        return tileFrames.getOrPut(frameKey) {
            val frame = TileFrame(TileName(frameX, frameY, frameZoomLvl), this)
            this += frame
            logD { "added tile frame ${frame.tileName}" }
            frame
        }
    }

    fun tileFadedOut(tileMesh: TileMesh) {
        removeTileMesh(tileMesh, true)
    }

    fun tileLoaded(tileMesh: TileMesh) {
        removeObsoleteTilesBelow(tileMesh.tileName)
        loadingTiles.remove(tileMesh.key)

        if (loadingTiles.isEmpty() && !removableTiles.isEmpty()) {
            //println("All loaded, remove obsolete")
            removableTiles.values.forEach { removeTileMesh(it, false) }
            removableTiles.clear()
        }
    }

    private fun removeObsoleteTilesBelow(tileName: TileName) {
        val it = removableTiles.values.iterator()
        for (mesh in it) {
            if (mesh.tileName.zoom > tileName.zoom) {
                val projX = mesh.tileName.x shr (mesh.tileName.zoom - tileName.zoom)
                val projY = mesh.tileName.y shr (mesh.tileName.zoom - tileName.zoom)
                if (projX == tileName.x && projY == tileName.y) {
                    removeTileMesh(mesh, false)
                    it.remove()
                }
            }
        }
    }

    companion object {
        private val RAD_85 = 85.0.toRad()
    }
}