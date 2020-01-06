package de.fabmax.kool.pipeline

import de.fabmax.kool.AssetManager
import de.fabmax.kool.CubeMapTextureData
import de.fabmax.kool.TextureData
import kotlinx.coroutines.CoroutineScope

/**
 * Describes a texture by it's properties and a loader function which is called once the texture is used.
 */
open class Texture(
        val addressModeU: AddressMode = AddressMode.REPEAT,
        val addressModeV: AddressMode = AddressMode.REPEAT,
        val addressModeW: AddressMode = AddressMode.REPEAT,
        val minFilter: FilterMethod = FilterMethod.LINEAR,
        val magFilter: FilterMethod = FilterMethod.LINEAR,
        val maxAnisotropy: Int = 16,
        val loader: (suspend CoroutineScope.(AssetManager) -> TextureData)?) {

    /**
     * Contains the platform specific handle to the loaded texture. It is available after the loader function was
     * called by the texture manager.
     */
    var loadedTexture: LoadedTexture? = null

    var loadingState = LoadingState.NOT_LOADED

    enum class LoadingState {
        NOT_LOADED,
        LOADING,
        LOADED,
        LOADING_FAILED
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
}

open class CubeMapTexture(
        addressModeU: AddressMode = AddressMode.CLAMP_TO_EDGE,
        addressModeV: AddressMode = AddressMode.CLAMP_TO_EDGE,
        addressModeW: AddressMode = AddressMode.CLAMP_TO_EDGE,
        minFilter: FilterMethod = FilterMethod.LINEAR,
        magFilter: FilterMethod = FilterMethod.LINEAR,
        maxAnisotropy: Int = 16,
        loader: (suspend CoroutineScope.(AssetManager) -> CubeMapTextureData)?) :

        Texture(addressModeU, addressModeV, addressModeW, minFilter, magFilter, maxAnisotropy, loader)