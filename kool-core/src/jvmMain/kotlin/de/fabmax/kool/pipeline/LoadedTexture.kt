package de.fabmax.kool.pipeline

import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.platform.vk.util.vkBytesPerPx
import de.fabmax.kool.util.logD
import org.lwjgl.vulkan.VK10.vkDestroySampler
import java.io.FileInputStream
import java.util.concurrent.atomic.AtomicLong
import javax.imageio.ImageIO

actual class LoadedTexture(val sys: VkSystem, val format: TexFormat, val textureImage: Image,
                           val textureImageView: ImageView, val sampler: Long,
                           private val isSharedRes: Boolean = false) : VkResource() {

    val texId = nextTexId.getAndIncrement()

    init {
        if (!isSharedRes) {
            addDependingResource(textureImage)
            addDependingResource(textureImageView)

            sys.ctx.engineStats.textureAllocated(texId, Texture.estimatedTexSize(
                    textureImage.width, textureImage.height, format.vkBytesPerPx, textureImage.arrayLayers, textureImage.mipLevels))
        }
        logD { "Created texture: Image: ${textureImage.vkImage}, view: ${textureImageView.vkImageView}, sampler: $sampler" }
    }

    override fun freeResources() {
        if (!isSharedRes) {
            vkDestroySampler(sys.device.vkDevice, sampler, null)
            sys.ctx.engineStats.textureDeleted(texId)
        }
        logD { "Destroyed texture" }
    }

    actual fun dispose() {
        // fixme: kinda hacky... also might be depending resource of something else than sys.device
        sys.ctx.runDelayed(sys.swapChain?.nImages ?: 3) {
            sys.device.removeDependingResource(this)
            destroy()
        }
    }

    companion object {
        private val nextTexId = AtomicLong(1L)

        fun fromFile(sys: VkSystem, path: String, texProps: TextureProps = TextureProps()): LoadedTexture {
            return fromTexData(sys, texProps, ImageTextureData(ImageIO.read(FileInputStream(path))))
        }

        fun fromTexData(sys: VkSystem, texProps: TextureProps, data: TextureData): LoadedTexture {
            return when(data) {
                is BufferedTextureData -> TextureLoader.loadTexture(sys, texProps, data)
                is CubeMapTextureData -> TextureLoader.loadCubeMap(sys, texProps, data)
                else -> TODO("texture data not implemented: ${data::class.java.name}")
            }
        }
    }
}
