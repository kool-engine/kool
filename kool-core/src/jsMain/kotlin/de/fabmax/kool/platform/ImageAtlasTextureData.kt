package de.fabmax.kool.platform

import de.fabmax.kool.pipeline.ImageData3d
import de.fabmax.kool.pipeline.TexFormat
import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.ImageBitmap
import org.w3c.dom.ImageData

class ImageAtlasTextureData(
    image: ImageBitmap,
    tilesX: Int,
    tilesY: Int,
    override val id: String,
    override val format: TexFormat = TexFormat.RGBA,
) : ImageData3d {

    val data: Array<ImageData>

    override val width = image.width / tilesX
    override val height = image.height / tilesY
    override val depth = tilesX * tilesY

    init {
        val w = width.toDouble()
        val h = height.toDouble()

        val canvas = document.createElement("canvas") as HTMLCanvasElement
        canvas.width = width
        canvas.height = height
        val canvasCtx = canvas.getContext("2d") as CanvasRenderingContext2D

        data = Array(depth) { i ->
            val x = (i % tilesX).toDouble()
            val y = (i / tilesX).toDouble()

            canvasCtx.clearRect(0.0, 0.0, w, h)
            canvasCtx.drawImage(image, x * w, y * h, w, h, 0.0, 0.0, w, h)
            canvasCtx.getImageData(0.0, 0.0, w, h)
        }
    }
}