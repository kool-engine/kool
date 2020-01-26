package de.fabmax.kool.platform

import de.fabmax.kool.TextureData
import org.w3c.dom.HTMLImageElement

class ImageTextureData(image: HTMLImageElement) : TextureData() {
//    override val isAvailable: Boolean get() = image.complete
//
//    override fun onLoad(texture: Texture, target: Int, ctx: KoolContext) {
//        // fixme: is there a way to find out if the image has an alpha channel and set the GL format accordingly?
//        JsImpl.gl.texImage2D(target, 0, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE, image)
//        width = image.width
//        height = image.height
//        val size = width * height * 4
//        ctx.memoryMgr.memoryAllocated(texture.res!!, size)
//    }

    override val isValid: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override val data = image
}