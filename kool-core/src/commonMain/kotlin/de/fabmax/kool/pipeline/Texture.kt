package de.fabmax.kool.pipeline

import de.fabmax.kool.AssetManager
import kotlinx.coroutines.CoroutineScope

class Texture(
        val addressModeU: AddressMode = AddressMode.REPEAT,
        val addressModeV: AddressMode = AddressMode.REPEAT,
        val addressModeW: AddressMode = AddressMode.REPEAT,
        val minFilter: FilterMethod = FilterMethod.LINEAR,
        val magFilter: FilterMethod = FilterMethod.LINEAR,
        val maxAnisotropy: Int = 16,
        val loader: suspend CoroutineScope.(AssetManager) -> ImageData) {

    var sampler: ImageSampler? = null

}

enum class FilterMethod {
    NEAREST,
    LINEAR
}

enum class AddressMode {
    CLAMP_TO_BORDER,
    CLAMP_TO_EDGE,
    MIRRORED_REPEAT,
    REPEAT
}