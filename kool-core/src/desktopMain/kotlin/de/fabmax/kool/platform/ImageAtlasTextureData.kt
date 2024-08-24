package de.fabmax.kool.platform

import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.BufferedImageData3d
import de.fabmax.kool.pipeline.ImageData
import de.fabmax.kool.pipeline.isByte
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl

fun imageAtlasTextureData(image: BufferedImageData2d, tilesX: Int, tilesY: Int): BufferedImageData3d {
    check(image.format.isByte) { "Texture atlas only supported for byte formats right now" }

    val width = image.width / tilesX
    val height = image.height / tilesY
    val depth = tilesX * tilesY
    val buffer = ImageData.createBuffer(image.format, width, height, depth) as Uint8Buffer
    val data = BufferedImageData3d(buffer, width, height, depth, image.format)
    val lineBuf = ByteArray(width * image.format.channels)

    val data2d = image.data as Uint8BufferImpl
    for (tileY in 0 until tilesY) {
        for (tileX in 0 until tilesX) {
            val srcX = tileX * width
            val srcY = tileY * height

            for (l in 0 until height) {
                data2d.useRaw {
                    it.position(((srcY + l) * image.width + srcX) * image.format.channels)
                    it.get(lineBuf)
                }
                buffer.put(lineBuf)
            }
        }
    }
    return data
}
