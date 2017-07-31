package de.fabmax.kool.demo.earth

import de.fabmax.kool.InputManager
import de.fabmax.kool.httpTexture
import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.*
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.*

/**
 * Mouse controlled transform group for earth rotation / translation
 */
class Earth(name: String? = null) : TransformGroup(name), InputManager.DragHandler {

    var tileRes = 256
    var meterPerPxLvl0 = 156e3f

    private val zoomGroups = mutableListOf<Group>()

    private val tiles = mutableMapOf<Long, TileMesh>()
    private val removableTiles = mutableMapOf<Long, TileMesh>()
    private val loadingTiles = mutableSetOf<Long>()
    private val removeTiles = mutableListOf<TileMesh>()

    private val rotAxis = MutableVec3f()
    private val camPosition = MutableVec3f()
    private val camDirection = MutableVec3f()

    private val startTransform = Mat4f()
    private val ptOrientation = Mat4f()
    private val mouseRotationStart = Mat4f()
    private val pickRay = Ray()
    private var isDragging = false

    private val tmpVec = MutableVec3f()
    private val tmpVecRt = MutableVec3f()
    private val tmpVecUp = MutableVec3f()
    private val tmpVecY = MutableVec3f()

    private var steadyScreenPt = MutableVec2f()
    private var steadyScreenPtMode = STEADY_SCREEN_PT_OFF

    private var center = TileName(0, 0, 1)
    private var prevCamHeight = 0f
    private var prevLat = 0.0
    private var prevLon = 0.0

    init {
        for (i in MIN_ZOOM_LEVEL..MAX_ZOOM_LEVEL) {
            val zoomGroup = Group("${name ?: "earth"}-zoom-$i")
            zoomGroups += zoomGroup
            +zoomGroup
        }
    }

    private fun getZoomGroup(level: Int): Group = zoomGroups[level - MIN_ZOOM_LEVEL]

    fun setSteadyPoint(screenX: Float, screenY: Float) {
        steadyScreenPt.set(screenX, screenY)
        steadyScreenPtMode = STEADY_SCREEN_PT_INIT
    }

    override fun render(ctx: RenderContext) {
        val cam = scene?.camera
        if (cam != null && cam is PerspectiveCamera) {
            // determine lat/lng of camera center
            //camPosition.set(cam.globalPos)
            val height = cam.globalPos.length()
            camPosition.set(Vec3f.Z_AXIS).scale(height)
            toLocalCoords(camPosition)
            camPosition.norm(camDirection)

            cam.clipNear = height * 0.05f
            cam.clipFar = height * 10f

            val dh = if (height > prevCamHeight) { prevCamHeight / height } else { height / prevCamHeight }
            prevCamHeight = height

            val lat = Math.clamp(Math.PI * 0.5 - Math.acos(camDirection.y.toDouble()), -RAD_85, RAD_85)
            val lon = Math.atan2(camDirection.x.toDouble(), camDirection.z.toDouble())

            val isMoving = dh < 0.99f || Math.abs(lat - prevLat) > 1e-5 || Math.abs(lon - prevLon) > 1e-5
            prevLat = lat
            prevLon = lon

            // determine best zoom level
            camDirection.scale(EARTH_R.toFloat())
            val camHeight = camDirection.distance(camPosition)
            val meterPerPx = camHeight * Math.tan(Math.toRad(cam.fovy) * 0.5f) * 2f / ctx.viewportHeight
            val centerZoom = getBestZoom(meterPerPx, lat)

            val newCenter = TileName.forLatLng(Math.toDeg(lat), Math.toDeg(lon), centerZoom)
            if (newCenter != center && (tiles.size < 300 || !isMoving)) {
                //println("$newCenter ${tiles.size}")
                center = newCenter
                rebuildMesh(ctx)
            }
        }

        if (steadyScreenPtMode == STEADY_SCREEN_PT_INIT &&
                computePointOrientation(steadyScreenPt.x, steadyScreenPt.y, ctx)) {
            steadyScreenPtMode = STEADY_SCREEN_PT_HOLD
            ptOrientation.transpose(mouseRotationStart)
            startTransform.set(transform)

        } else if (steadyScreenPtMode == STEADY_SCREEN_PT_HOLD) {
            set(startTransform)
            if (computePointOrientation(steadyScreenPt.x, steadyScreenPt.y, ctx)) {
                ptOrientation.mul(mouseRotationStart)
            } else {
                steadyScreenPtMode = STEADY_SCREEN_PT_OFF
            }
            mul(ptOrientation)
        }

        super.render(ctx)

        if (!removeTiles.isEmpty()) {
            removeTiles.forEach {
                loadingTiles.remove(it.key)
                tiles.remove(it.key)
                removableTiles.remove(it.key)
                getZoomGroup(it.tz).removeNode(it)
                it.dispose(ctx)
            }
            removeTiles.clear()
        }
    }

    private fun getBestZoom(meterPerPx: Float, lat: Double): Int =
            Math.clamp(Math.round(0.2f + Math.log2(meterPerPxLvl0 / meterPerPx * Math.cos(lat))),
                    MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL)

    private fun rebuildMesh(ctx: RenderContext) {
        removableTiles.putAll(tiles)

        val rng = 5

        var zoom = center.zoom
        var size = 1 shl zoom
        var xStart = (center.x - rng + 1) and 1.inv()
        var xEnd = ((center.x + rng + 1) and 1.inv()) - 1
        var yStart = (center.y - rng + 1) and 1.inv()
        var yEnd = ((center.y + rng + 1) and 1.inv()) - 1

        addMeshesWrappingX(xStart, xEnd, yStart, yEnd, zoom)

        for (i in 1..4) {
            zoom--
            if (zoom >= MIN_ZOOM_LEVEL) {
                size = size shr 1
                val xStShf = xStart shr 1
                val xEdShf = (xEnd + 1) shr 1
                val yStShf = yStart shr 1
                val yEdShf = (yEnd + 1) shr 1

                xStart = (xStShf - 1) and 1.inv()
                xEnd = (xEdShf and 1.inv()) + 1
                yStart = (yStShf - 1) and 1.inv()
                yEnd = (yEdShf and 1.inv()) + 1

                addMeshesWrappingX(xStart, xStShf-1, yStart, yEnd, zoom)
                addMeshesWrappingX(xEdShf, xEnd, yStart, yEnd, zoom)

                addMeshesWrappingX(xStShf, xEdShf-1, yStart, yStShf-1, zoom)
                addMeshesWrappingX(xStShf, xEdShf-1, yEdShf, yEnd, zoom)
            } else {
                break
            }
        }

        if (tiles.size > 400) {
            // queue is getting too large, remove stuff even though it might be visible
            val rmQueue = mutableListOf<TileMesh>().apply { addAll(removableTiles.values) }
            rmQueue.sortBy { m -> if (!m.isTexLoaded) { Int.MIN_VALUE } else { -Math.abs(m.tz - center.zoom) } }
            for (i in 0..(tiles.size-400)) {
                removeTileMesh(rmQueue[i], false)
            }
        }
    }

    private fun addMeshesWrappingX(xStart: Int, xEnd: Int, yStart: Int, yEnd: Int, zoom: Int) {
        val size = 1 shl zoom
        val ys = Math.max(0, yStart)
        val ye = Math.min(size - 1, yEnd)

        addMeshes(Math.max(0, xStart)..Math.min(size - 1, xEnd), ys..ye, zoom)
        if (xStart < 0 && xEnd < size - 1) {
            // wrap around 180° long
            addMeshes(Math.max(size + xStart, xEnd)..size - 1, ys..ye, zoom)
        } else if (xStart > 0 && xEnd > size - 1) {
            // wrap around 180° long
            addMeshes(0..Math.min(xStart, xEnd - (size - 1)), ys..ye, zoom)
        }
    }

    private fun addMeshes(xRng: IntRange, yRng: IntRange, zoom: Int) {
        if (xRng.last - xRng.first > 2 || yRng.last - yRng.first > 2) {
            addMeshesCircular(xRng, yRng, zoom)
        } else {
            addMeshesRectRange(xRng, yRng, zoom)
        }
    }

    private fun addMeshesRectRange(xRng: IntRange, yRng: IntRange, zoom: Int) {
        for (x in xRng) {
            for (y in yRng) {
                val key = TileMesh.tileKey(x, y, zoom)
                val existing = tiles[key]
                if (existing != null) {
                    removableTiles.remove(key)
                    existing.isFadingOut = false
                    if (!existing.isLoaded) {
                        loadingTiles += key
                    }
                } else {
                    val mesh = TileMesh(this, x, y, zoom)
                    tiles[key] = mesh
                    getZoomGroup(zoom) += mesh
                    loadingTiles += key
                }
            }
        }
    }

    private fun addMeshesCircular(xRng: IntRange, yRng: IntRange, zoom: Int) {
        fun addTile(x: Int, y: Int) {
            if (x in xRng && y in yRng) {
                val key = TileMesh.tileKey(x, y, zoom)
                val existing = tiles[key]
                if (existing != null) {
                    removableTiles.remove(key)
                    existing.isFadingOut = false
                    if (!existing.isLoaded) {
                        loadingTiles += key
                    }
                } else {
                    val mesh = TileMesh(this, x, y, zoom)
                    tiles[key] = mesh
                    getZoomGroup(zoom) += mesh
                    loadingTiles += key
                }
            }
        }

        val cx = xRng.first + (xRng.last - xRng.first) / 2
        val cy = yRng.first + (yRng.last - yRng.first) / 2
        val r = Math.max(Math.max(cx - xRng.first, xRng.last - cx), Math.max(cy - yRng.first, yRng.last - cy))

        for (i in 0..r) {
            for (x in cx - i..cx + i) {
                addTile(x, cy - i)
                if (i > 0) {
                    addTile(x, cy + i)
                }
            }
            if (i > 0) {
                for (y in cy - i + 1..cy + i - 1) {
                    addTile(cx - i, y)
                    addTile(cx + i, y)
                }
            }
        }
    }

    fun tileFadedOut(tileMesh: TileMesh) {
        removeTiles += tileMesh
    }

    fun tileLoaded(tileMesh: TileMesh) {
        removeObsoleteTilesBelow(tileMesh.tx, tileMesh.ty, tileMesh.tz)
        loadingTiles.remove(tileMesh.key)

        if (loadingTiles.isEmpty() && !removableTiles.isEmpty()) {
            //println("All loaded, remove obsolete")
            removableTiles.values.forEach { removeTileMesh(it, true) }
            removableTiles.clear()
        }
    }

    private fun removeTileMesh(mesh: TileMesh, fadeOut: Boolean) {
        if (mesh.isCurrentlyVisible && fadeOut) {
            mesh.isFadingOut = true
        } else {
            removeTiles += mesh
        }
    }

    private fun removeObsoleteTilesBelow(x: Int, y: Int, zoom: Int) {
        val it = removableTiles.values.iterator()
        for (mesh in it) {
            if (mesh.tz > zoom) {
                val projX = mesh.tx shr (mesh.tz - zoom)
                val projY = mesh.ty shr (mesh.tz - zoom)
                if (projX == x && projY == y) {
                    removeTileMesh(mesh, true)
                    it.remove()
                }
            }
        }
    }

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        oldScene?.removeDragHandler(this)
        newScene?.registerDragHandler(this)
    }

    override fun handleDrag(dragPtrs: List<InputManager.Pointer>, ctx: RenderContext): Int {
        if (dragPtrs.size == 1 && dragPtrs[0].isInViewport(ctx)) {
            val ptrX = dragPtrs[0].x
            val ptrY = dragPtrs[0].y

            if (dragPtrs[0].isLeftButtonDown) {
                steadyScreenPtMode = STEADY_SCREEN_PT_OFF

                if (dragPtrs[0].isLeftButtonEvent) {
                    isDragging = computePointOrientation(ptrX, ptrY, ctx)
                    ptOrientation.transpose(mouseRotationStart)
                    startTransform.set(transform)

                } else if (isDragging) {
                    set(startTransform)
                    val valid = computePointOrientation(ptrX, ptrY, ctx)
                    if (valid) {
                        ptOrientation.mul(mouseRotationStart)
                    }
                    mul(ptOrientation)
                    isDragging = valid
                }
            } else if (dragPtrs[0].deltaScroll != 0f || (dragPtrs[0].isRightButtonEvent && dragPtrs[0].isRightButtonDown)) {
                if (steadyScreenPtMode == STEADY_SCREEN_PT_OFF || ptrX != steadyScreenPt.x || ptrY != steadyScreenPt.y) {
                    setSteadyPoint(ptrX, ptrY)
                }
            }
        }
        return 0
    }

    private fun computePointOrientation(screenX: Float, screenY: Float, ctx: RenderContext): Boolean {
        if (scene?.camera?.computePickRay(pickRay, screenX, screenY, ctx) ?: false) {
            val o = pickRay.origin
            val l = pickRay.direction
            toLocalCoords(pickRay.origin)
            toLocalCoords(pickRay.direction, 0f)
            toLocalCoords(tmpVecY.set(Vec3f.Y_AXIS), 0f)

            val ldo = l * o
            val sqr = ldo * ldo - o.sqrLength() + EARTH_R * EARTH_R
            if (sqr > 0) {
                val d = -ldo - Math.sqrt(sqr)
                l.scale(d.toFloat(), tmpVec).add(o)

                tmpVec.norm()
                if (tmpVec.isEqual(tmpVecY)) {
                    return false
                }
                tmpVecY.cross(tmpVec, tmpVecRt).norm()
                tmpVec.cross(tmpVecRt, tmpVecUp)

                ptOrientation.setColVec(0, tmpVec, 0f)
                ptOrientation.setColVec(1, tmpVecRt, 0f)
                ptOrientation.setColVec(2, tmpVecUp, 0f)
                ptOrientation.setColVec(3, Vec3f.ZERO, 1f)
                return true
            }
        }
        return false
    }

    companion object {
        // average radius in meters by WGS84
        const val EARTH_R = 6_371_000.8

        const val MIN_ZOOM_LEVEL = 3
        const val MAX_ZOOM_LEVEL = 19

        private val RAD_85 = Math.toRad(85.0)

        private val STEADY_SCREEN_PT_OFF = 0
        private val STEADY_SCREEN_PT_INIT = 1
        private val STEADY_SCREEN_PT_HOLD = 2
    }
}

class TileMesh(val earth: Earth, val tx: Int, val ty: Int, val tz: Int) :
        Mesh(MeshData(true, false, true), "$tz/$tx/$ty") {

    val key = tileKey(tx, ty, tz)

    val isCurrentlyVisible get() = isRendered

    private var tileShader: BasicShader

    private var centerNormal = Vec3f(0f)

    private var tmpVec = MutableVec3f()
    private var tmpBndsMin = MutableVec3f()
    private var tmpBndsMax = MutableVec3f()

    var isFadingOut = false
    var isLoaded = false
        private set
    var isTexLoaded = false
        private set

    init {
        generator = {
            val lonW = tx / (1 shl tz).toDouble() * 2 * Math.PI - Math.PI
            val lonE = (tx + 1) / (1 shl tz).toDouble() * 2 * Math.PI - Math.PI

            val stepsExp = 4
            val steps = 1 shl stepsExp
            val tysFac = 1.0 / (1 shl (tz + stepsExp)).toDouble() * 2 * Math.PI
            var prevIndices = IntArray(steps + 1)
            var rowIndices = IntArray(steps + 1)
            for (row in 0..steps) {
                val tmp = prevIndices
                prevIndices = rowIndices
                rowIndices = tmp

                val tys = (ty+1) * steps - row
                val lat = Math.PI * 0.5 - Math.atan(Math.sinh(Math.PI - tys * tysFac))
                val r = Math.sin(lat) * Earth.EARTH_R
                val y = Math.cos(lat) * Earth.EARTH_R
                for (i in 0..steps) {
                    val phi = lonW + (lonE - lonW) * i / steps
                    val x = Math.sin(phi) * r
                    val z = Math.cos(phi) * r
                    val uv = Vec2f(i.toFloat() / steps, 1f - row.toFloat() / steps)

                    val fx = x.toFloat()
                    val fy = y.toFloat()
                    val fz = z.toFloat()
                    val nrm = MutableVec3f(fx, fy, fz).norm()
                    rowIndices[i] = vertex(Vec3f(fx, fy, fz), nrm, uv)

                    if (row == steps / 2 && i == steps / 2) {
                        // center vertex
                        centerNormal = Vec3f(nrm)
                    }

                    if (i > 0 && row > 0) {
                        meshData.addTriIndices(prevIndices[i - 1], rowIndices[i], rowIndices[i - 1])
                        meshData.addTriIndices(prevIndices[i - 1], prevIndices[i], rowIndices[i])
                    }
                }
            }
        }
        tileShader = basicShader {
            colorModel = ColorModel.TEXTURE_COLOR
            //colorModel = if ((tx xor ty) % 2 == 0) ColorModel.STATIC_COLOR else ColorModel.TEXTURE_COLOR
            lightModel = LightModel.NO_LIGHTING
            isAlpha = true
        }
        tileShader.alpha = 0f
        shader = tileShader
        //tileShader.staticColor.set(Color(tz / 10f, 1 - tz / 10f, 0f))
        loadTileTex(tx, ty, tz)

        generateGeometry()
    }

    private fun loadTileTex(x: Int, y: Int, z:Int) {
        tileShader.texture = httpTexture("http://tile.openstreetmap.org/$z/$x/$y.png", "mapnik/$z/$x/$y.png")
    }

    override fun render(ctx: RenderContext) {
        ctx.pushAttributes()
        ctx.isDepthTest = false
        ctx.applyAttributes()
        super.render(ctx)
        ctx.popAttributes()

        // increase alpha as soon as texture is available (but mesh doesn't have to be visible)
        val targetAlpha = 1f
        if (isTexLoaded && !isFadingOut && tileShader.alpha < targetAlpha) {
            tileShader.alpha += ctx.deltaT * 2
            if (tileShader.alpha >= targetAlpha) {
                tileShader.alpha = targetAlpha
                isLoaded = true
                earth.tileLoaded(this)
            }
        } else if (isFadingOut && tileShader.alpha > 0f) {
            tileShader.alpha -= ctx.deltaT * 2
            if (tileShader.alpha <= 0f) {
                tileShader.alpha = 0f
                earth.tileFadedOut(this)
            }
        }
    }

    override fun checkIsVisible(ctx: RenderContext): Boolean {
        val tex = tileShader.texture ?: return false
        isTexLoaded = tex.res?.isLoaded ?: false
        val visible = isTexLoaded && super.checkIsVisible(ctx)
        if (visible) {
            toGlobalCoords(tmpVec.set(centerNormal), 0f)
            val cos = scene?.camera?.globalLookDir?.dot(tmpVec) ?: 0f
            return cos < 0.1f

        } else if (!isTexLoaded) {
            // trigger / poll texture loading
            ctx.textureMgr.bindTexture(tex, ctx)
        }
        return false
    }

    companion object {
        fun tileKey(tx: Int, ty: Int, tz: Int): Long = (tz.toLong() shl 58) or
                ((tx and 0x1fffffff).toLong().shl(29)) or
                (ty and 0x1fffffff).toLong()
    }
}