package de.fabmax.kool.modules.globe.elevation

import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.DataBufferInt
import java.io.File
import javax.imageio.ImageIO

actual fun loadPngS16ElevationMap(basePath: String, meta: ElevationMapMeta): BoundedElevationMap {
    val img = ImageIO.read(File(basePath, meta.name))
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

    return ElevationMapS16(data, meta)
}