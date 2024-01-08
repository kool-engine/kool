package de.fabmax.kool.platform

import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.util.Buffer
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Uint8Buffer
import kotlinx.browser.document
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLImageElement

class BufferedImageTextureData(image: HTMLImageElement, texProps: TextureProps?) :
    TextureData2d(
        image.toBuffer(texProps),
        texProps?.resolveSize?.x ?: image.width,
        texProps?.resolveSize?.y ?: image.height,
        texProps?.format ?: TexFormat.RGBA
    )
{
    companion object {
        private fun HTMLImageElement.toBuffer(texProps: TextureProps?): Buffer {
            val fmt = texProps?.format
            val prefSize = texProps?.resolveSize
            val w = prefSize?.x ?: width
            val h = prefSize?.y ?: height

            // in order to get the pixel data, we must draw the image into a canvas and read the pixels
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            canvas.width = w
            canvas.height = h
            val canvasCtx = canvas.getContext("2d") as CanvasRenderingContext2D
            canvasCtx.drawImage(this, 0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
            val imageData = canvasCtx.getImageData(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())

            val dstFormat = fmt ?: TexFormat.RGBA
            val c = dstFormat.channels
            val buffer: Buffer

            if (dstFormat.isFloat) {
                buffer = Float32Buffer(w * h * c)
                for (i in 0 until w * h) {
                    buffer[i * c + 0] = (imageData.data[i * 4 + 0].toInt() and 0xff) / 255f
                    if (c > 1) buffer[i * c + 1] = (imageData.data[i * 4 + 1].toInt() and 0xff) / 255f
                    if (c > 2) buffer[i * c + 2] = (imageData.data[i * 4 + 2].toInt() and 0xff) / 255f
                    if (c > 3) buffer[i * c + 3] = (imageData.data[i * 4 + 3].toInt() and 0xff) / 255f
                }

            } else {
                buffer = Uint8Buffer(w * h * c)
                for (i in 0 until w * h) {
                    buffer[i * c + 0] = imageData.data[i * 4 + 0].toUByte()
                    if (c > 1) buffer[i * c + 1] = imageData.data[i * 4 + 1].toUByte()
                    if (c > 2) buffer[i * c + 2] = imageData.data[i * 4 + 2].toUByte()
                    if (c > 3) buffer[i * c + 3] = imageData.data[i * 4 + 3].toUByte()
                }
            }
            return buffer
        }
    }
}