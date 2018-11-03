package de.fabmax.kool.modules.globe.elevation

import de.fabmax.kool.AssetManager
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.platform.JvmAssetManager
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.DataBufferInt
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import javax.imageio.ImageIO

actual fun loadPngS16ElevationMap(basePath: String, meta: ElevationMapMeta, assetMgr: AssetManager): BoundedElevationMap {
    return DelayedElevationMap(basePath, meta, assetMgr as JvmAssetManager)
}

fun loadPngS16ElevationMap(basePath: String, meta: ElevationMapMeta): BoundedElevationMap {
    val data = loadElevationMapData(FileInputStream(File(basePath, meta.name)), meta)
    return ElevationMapS16(data, meta)
}

private class DelayedElevationMap(basePath: String, override val meta: ElevationMapMeta, assetMgr: JvmAssetManager) : BoundedElevationMap {
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
        assetMgr.loadAssetAsStream("$basePath/${meta.name}") {
            if (it != null) {
                loadedHeightMap = ElevationMapS16(loadElevationMapData(it, meta), meta)
            }
            isAvailable = true
        }
    }

    override fun getElevationAt(lat: Double, lon: Double): Double = loadedHeightMap?.getElevationAt(lat, lon) ?: 0.0

    override fun getNormalAt(lat: Double, lon: Double, result: MutableVec3f): MutableVec3f =
            loadedHeightMap?.getNormalAt(lat, lon, result) ?: result.set(Vec3f.Z_AXIS)

}

private fun loadElevationMapData(inputStream: InputStream, meta: ElevationMapMeta): ShortArray {
    val img = ImageIO.read(inputStream)
    if (img.width != meta.width || img.height != meta.height) {
        throw IllegalStateException("Image size differs from meta info: img: ${img.width} x ${img.height}, meta: ${meta.width} x ${meta.height} [${meta.name}]")
    }

    val data = ShortArray(meta.width * meta.height)
    var i = 0

    if (img.type == BufferedImage.TYPE_INT_ARGB || img.type == BufferedImage.TYPE_INT_RGB) {
        val buf = (img.raster.dataBuffer as DataBufferInt).data
        for (y in 0 until meta.height) {
            for (x in 0 until meta.width) {
                data[i] = (buf[i] shr 8).toShort()
                i++
            }
        }

    } else if (img.type == BufferedImage.TYPE_3BYTE_BGR) {
        val buf = (img.raster.dataBuffer as DataBufferByte).data
        for (y in 0 until meta.height) {
            for (x in 0 until meta.width) {
                val r = buf[i*3+2].toInt() and 0xff
                val g = buf[i*3+1].toInt() and 0xff
                data[i] = ((r shl 8) or g).toShort()
                i++
            }
        }

    } else {
        throw NotImplementedError("Unsupported image type: ${img.type} [${meta.name}]")
    }

    return data
}