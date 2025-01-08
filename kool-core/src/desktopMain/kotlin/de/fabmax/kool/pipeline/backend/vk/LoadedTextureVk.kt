package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.GpuTexture
import de.fabmax.kool.util.*
import org.lwjgl.vulkan.VK10.vkDestroySampler
import java.util.concurrent.atomic.AtomicLong

class LoadedTextureVk(val backend: RenderBackendVk, val format: TexFormat, val textureImage: Image,
                      val textureImageView: ImageView, val sampler: Long,
                      private val isSharedRes: Boolean = false) : BaseReleasable(), GpuTexture {

    val texId = nextTexId.getAndIncrement()

    override var width = 0
    override var height = 0
    override var depth = 0

    init {
        if (!isSharedRes) {
            textureImage.releaseWith(this)
            textureImageView.releaseWith(this)

            // todo: add TextureInfo() to BackendStats
        }
        logD { "Created texture: Image: ${textureImage.vkImage}, view: ${textureImageView.vkImageView}, sampler: $sampler" }
    }

    fun setSize(width: Int, height: Int, depth: Int) {
        this.width = width
        this.height = height
        this.depth = depth
    }

    override fun release() {
        super.release()
        if (!isReleased) {
            // fixme: kinda hacky... also might be depending resource of something else than sys.device
            launchDelayed(backend.swapchain.nImages) {
                cancelReleaseWith(backend.device)

                if (!isSharedRes) {
                    vkDestroySampler(backend.device.vkDevice, sampler, null)
                    // todo: TextureInfo.deleted()
                }
                logD { "Destroyed texture" }
            }
        }
    }

    companion object {
        private val nextTexId = AtomicLong(1L)

        fun fromTexData(backend: RenderBackendVk, texProps: TextureProps, data: ImageData): LoadedTextureVk {
            return when(data) {
                is BufferedImageData1d -> TextureLoader.loadTexture1d(backend, texProps, data)
                is BufferedImageData2d -> TextureLoader.loadTexture2d(backend, texProps, data)
                is BufferedImageData3d -> TextureLoader.loadTexture3d(backend, texProps, data)
                is ImageDataCube -> TextureLoader.loadTextureCube(backend, texProps, data)
                else -> TODO("texture data not implemented: ${data::class.java.name}")
            }
        }
    }
}
