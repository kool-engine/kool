package de.fabmax.kool.platform

import de.fabmax.kool.pipeline.TextureData
import org.w3c.dom.HTMLImageElement

class ImageTextureData(image: HTMLImageElement) : TextureData() {
    override val data = image

    init {
        if (!image.complete) {
            throw IllegalStateException("Image must be compülete")
        }
        width = image.width
        height = image.height
    }
}