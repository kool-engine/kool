package de.fabmax.kool.pipeline

import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.util.logD
import org.lwjgl.vulkan.VK10.vkDestroySampler
import java.io.FileInputStream
import javax.imageio.ImageIO

actual class LoadedTexture(val sys: VkSystem, val format: TexFormat, val textureImage: Image,
                           val textureImageView: ImageView, val sampler: Long,
                           private val isSharedRes: Boolean = false) : VkResource() {
    init {
        if (!isSharedRes) {
            addDependingResource(textureImage)
            addDependingResource(textureImageView)
        }
        logD { "Created texture: Image: ${textureImage.vkImage}, view: ${textureImageView.vkImageView}, sampler: $sampler" }
    }

    override fun freeResources() {
        if (!isSharedRes) {
            vkDestroySampler(sys.device.vkDevice, sampler, null)
        }
        logD { "Destroyed texture" }
    }

    actual fun dispose() {
        // fixme: kinda hacky... also might me depending resource of something else than sys.device
        sys.renderLoop.runDelayed(sys.swapChain?.nImages ?: 3) {
            destroy()
            sys.device.removeDependingResource(this)
        }
    }

    companion object {
        fun fromFile(sys: VkSystem, path: String): LoadedTexture {
            return fromTexData(sys, ImageTextureData(ImageIO.read(FileInputStream(path))))
        }

        fun fromTexData(sys: VkSystem, data: TextureData): LoadedTexture {
            return when(data) {
                is BufferedTextureData -> TextureLoader.loadTexture(sys, data)
                is CubeMapTextureData -> TextureLoader.loadCubeMap(sys, data)
                else -> TODO("texture data not implemented: ${data::class.java.name}")
            }
        }
    }
}
