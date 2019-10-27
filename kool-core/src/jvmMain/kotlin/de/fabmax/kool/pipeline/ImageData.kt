package de.fabmax.kool.pipeline

import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.platform.vk.VkSystem
import de.fabmax.kool.platform.vk.VkTexture
import kotlinx.coroutines.Deferred

actual class ImageData(val image: ImageTextureData)

actual class ImageSampler(val data: Deferred<ImageData>, val sys: VkSystem) {

    private var imageData: ImageTextureData? = null
    var texture: VkTexture? = null
        private set

    init {
        data.invokeOnCompletion { cause ->
            if (cause == null) {
                imageData = data.getCompleted().image
            } else {
                // todo: use fallback texture?
                throw cause
            }
        }
    }

    fun checkComplete(): Boolean {
        if (texture == null) {
            imageData?.let { img ->
                texture = VkTexture(sys, img).also { sys.device.addDependingResource(it) }
            }
        }
        return texture != null
    }
}