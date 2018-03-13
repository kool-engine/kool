package de.fabmax.kool.platform

import de.fabmax.kool.JsImpl
import de.fabmax.kool.KoolContext
import de.fabmax.kool.Texture
import de.fabmax.kool.TextureData
import de.fabmax.kool.gl.GL_RGBA
import de.fabmax.kool.gl.GL_TEXTURE_2D
import de.fabmax.kool.gl.GL_UNSIGNED_BYTE
import org.w3c.dom.HTMLImageElement

class ImageTextureData(val image: HTMLImageElement) : TextureData() {
    override var isAvailable: Boolean
        get() = image.complete
        set(value) {}

    override fun onLoad(texture: Texture, ctx: KoolContext) {
        // fixme: is there a way to find out if the image has an alpha channel and set the GL format accordingly?
        JsImpl.gl.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE, image)
        width = image.width
        height = image.height
        val size = width * height * 4
        ctx.memoryMgr.memoryAllocated(texture.res!!, size)
    }
}