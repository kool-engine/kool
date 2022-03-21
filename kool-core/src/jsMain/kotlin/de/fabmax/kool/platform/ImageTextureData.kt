package de.fabmax.kool.platform

import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.TextureData
import org.w3c.dom.HTMLImageElement

class ImageTextureData(val image: HTMLImageElement, fmt: TexFormat?) : TextureData() {
    override val data = image

    init {
        if (!image.complete) {
            throw IllegalStateException("Image must be complete")
        }
        width = image.width
        height = image.height
        depth = 1

        fmt?.let { format = it }
    }
}