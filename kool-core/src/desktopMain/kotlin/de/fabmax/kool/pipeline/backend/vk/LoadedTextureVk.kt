package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.GpuTexture

class LoadedTextureVk(val image: Image, val imageView: ImageView) : GpuTexture {
    override val width: Int get() = image.width
    override val height: Int get() = image.height
    override val depth: Int get() = image.depth

    override var isReleased: Boolean = false
        private set

    override fun release() {
        if (!isReleased) {
            isReleased = true
            image.release()
            imageView.release()
        }
    }
}