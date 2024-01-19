package de.fabmax.kool.platform

import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.TextureData
import org.w3c.dom.ImageBitmap

class ImageTextureData(override val data: ImageBitmap, fmt: TexFormat?) : TextureData() {
    init {
        width = data.width
        height = data.height
        depth = 1
        fmt?.let { format = it }
    }
}