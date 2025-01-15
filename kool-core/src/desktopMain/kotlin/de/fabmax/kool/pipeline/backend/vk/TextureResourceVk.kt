package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.GpuTexture
import de.fabmax.kool.util.BaseReleasable

class TextureResourceVk(val image: Image) : BaseReleasable(), GpuTexture {
    override val width: Int get() = image.width
    override val height: Int get() = image.height
    override val depth: Int get() = image.depth

    override fun release() {
        super.release()
        image.release()
    }
}