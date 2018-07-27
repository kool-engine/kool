package de.fabmax.kool.modules.globe.elevation

import de.fabmax.kool.AssetManager
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLImageElement
import kotlin.browser.document

actual fun loadPngS16ElevationMap(basePath: String, meta: ElevationMapMeta, assetMgr: AssetManager): BoundedElevationMap = DelayedElevationMap(basePath, meta)

private class DelayedElevationMap(basePath: String, override val meta: ElevationMapMeta) : BoundedElevationMap {
    override val west: Double
        get() = meta.west
    override val east: Double
        get() = meta.east
    override val south: Double
        get() = meta.south
    override val north: Double
        get() = meta.north

    override var isAvailable = false

    private var loadedHeightMap: ElevationMapS16? = null

    init {
        ElevationMapLoader.instance.loadHeightMap("$basePath/${meta.name}") { data ->
            if (data != null) {
                loadedHeightMap = ElevationMapS16(data, meta)
            }
            isAvailable = true
        }
    }

    override fun getElevationAt(lat: Double, lon: Double): Double = loadedHeightMap?.getElevationAt(lat, lon) ?: 0.0

    override fun getNormalAt(lat: Double, lon: Double, result: MutableVec3f): MutableVec3f =
            loadedHeightMap?.getNormalAt(lat, lon, result) ?: result.set(Vec3f.Z_AXIS)

}

private class ElevationMapLoader {
    private val canvas = document.createElement("canvas") as HTMLCanvasElement
    private val canvasCtx: CanvasRenderingContext2D

    init {
        canvas.width = MAP_WIDTH
        canvas.height = MAP_HEIGHT
        canvasCtx = canvas.getContext("2d") as CanvasRenderingContext2D
    }

    fun loadHeightMap(path: String, onLoaded: (ShortArray?) -> Unit) {
        val img = document.createElement("img") as HTMLImageElement
        img.src = path
        img.crossOrigin = ""
        img.onerror = { _,_,_,_,_ ->
            onLoaded(null)
            true
        }
        img.onload = {
            canvasCtx.drawImage(img, 0.0, 0.0)

            val array = ShortArray(900*900)
            val data = canvasCtx.getImageData(0.0, 0.0, 900.0, 900.0).data

            // rg encoding (works only with lossless / png)
            for (i in 0 until 900*900*4) {
                val r = data[i*4]
                val g = data[i*4+1]
                val h = ((r.toInt() shl 8) or g.toInt()).toShort()
                array[i] = h
            }
            onLoaded(array)
            true
        }
    }

    companion object {
        const val MAP_WIDTH = 900
        const val MAP_HEIGHT = 900

        val instance = ElevationMapLoader()
    }
}
