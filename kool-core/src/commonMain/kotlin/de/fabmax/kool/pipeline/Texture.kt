package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.backend.GpuTexture
import de.fabmax.kool.util.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * Describes a texture by its properties and a loader function which is called once the texture is used.
 */
abstract class Texture(
    val props: TextureProps,
    val name: String,
    val loader: TextureLoader? = null
): BaseReleasable() {
    /**
     * Contains the platform specific handle to the loaded texture. It is available after the loader function was
     * called.
     */
    var gpuTexture: GpuTexture? = null

    var loadingState = LoadingState.NOT_LOADED

    val width: Int
        get() = gpuTexture?.width ?: 0
    val height: Int
        get() = gpuTexture?.height ?: 0
    val depth: Int
        get() = gpuTexture?.depth ?: 0

    /**
     * Disposes the underlying texture memory and resets the [loadingState] to [LoadingState.NOT_LOADED]. In contrast
     * to [release], the texture can still be used after disposing it - the texture will be reloaded. Disposing
     * textures is useful to update changed texture data.
     *
     * @see release
     */
    open fun dispose() {
        gpuTexture?.release()
        gpuTexture = null
        loadingState = LoadingState.NOT_LOADED
    }

    /**
     * Releases this texture, making it unusable. Use [dispose] instead if you only want to free the underlying
     * texture memory and reload new texture data.
     *
     * @see dispose
     */
    override fun release() {
        super.release()
        dispose()
    }

    override fun toString(): String {
        return "${this::class.simpleName}:$name"
    }

    enum class LoadingState {
        NOT_LOADED,
        LOADING,
        LOADED,
        LOADING_FAILED
    }

    suspend fun upload(texData: ImageData) {
        withContext(Dispatchers.RenderLoop) {
            KoolSystem.requireContext().backend.uploadTextureData(this@Texture, texData)
        }
    }

    companion object {
        fun estimatedTexSize(width: Int, height: Int, layers: Int, mipLevels: Int, bytesPerPx: Int): Int {
            var mipFac = 1.0
            var mipAdd = 0.25
            for (i in 2..mipLevels) {
                mipFac += mipAdd
                mipAdd *= 0.25
            }
            return (width * height * layers * bytesPerPx * mipFac).roundToInt()
        }
    }
}

open class Texture1d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture1d"),
    loader: TextureLoader?
) : Texture(props, name, loader) {
    suspend inline fun readbackTextureData(): BufferedImageData1d {
        val deferred = CompletableDeferred<ImageData>()
        KoolSystem.requireContext().backend.downloadTextureData(this, deferred)

        val buffer = deferred.await()
        check(buffer is BufferedImageData1d)
        return buffer
    }
}

fun Texture1d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture1d"),
    loader: (suspend CoroutineScope.() -> BufferedImageData1d)? = null
): Texture1d = Texture1d(props, name, loader?.let { DeferredTextureLoader(it) })

fun Texture1d(
    props: TextureProps = TextureProps(),
    data: BufferedImageData1d,
    name: String = UniqueId.nextId("Texture2d")
): Texture1d = Texture1d(props, name, ImageTextureLoader(data))

open class Texture2d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture2d"),
    loader: TextureLoader?
) : Texture(props, name, loader) {
    suspend inline fun readbackTextureData(): BufferedImageData2d {
        val deferred = CompletableDeferred<ImageData>()
        KoolSystem.requireContext().backend.downloadTextureData(this, deferred)

        val buffer = deferred.await()
        check(buffer is BufferedImageData2d)
        return buffer
    }
}

fun Texture2d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture2d"),
    loader: (suspend CoroutineScope.() -> ImageData)? = null
): Texture2d = Texture2d(props, name, loader?.let { DeferredTextureLoader(it) })

fun Texture2d(
    props: TextureProps = TextureProps(),
    data: BufferedImageData2d,
    name: String = UniqueId.nextId("Texture2d")
): Texture2d = Texture2d(props, name, ImageTextureLoader(data))

open class Texture3d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture3d"),
    loader: TextureLoader?
) : Texture(props, name, loader) {
    suspend inline fun readbackTextureData(): BufferedImageData3d {
        val deferred = CompletableDeferred<ImageData>()
        KoolSystem.requireContext().backend.downloadTextureData(this, deferred)

        val buffer = deferred.await()
        check(buffer is BufferedImageData3d)
        return buffer
    }
}

fun Texture3d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture3d"),
    loader: (suspend CoroutineScope.() -> ImageData)? = null
): Texture3d = Texture3d(props, name, loader?.let { DeferredTextureLoader(it) })

fun Texture3d(
    props: TextureProps = TextureProps(),
    data: BufferedImageData3d,
    name: String = UniqueId.nextId("Texture3d")
): Texture3d = Texture3d(props, name, ImageTextureLoader(data))

open class TextureCube(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("TextureCube"),
    loader: TextureLoader?
) : Texture(props, name, loader)

fun TextureCube(
    props: TextureProps = TextureProps(),
    data: ImageDataCube,
    name: String = UniqueId.nextId("Texture3d")
): TextureCube = TextureCube(props, name, ImageTextureLoader(data))

fun TextureCube(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("TextureCube"),
    loader: (suspend CoroutineScope.() -> ImageDataCube)? = null
): TextureCube = TextureCube(props, name, loader?.let { DeferredTextureLoader(it) })

class BufferedTexture2d(
    data: ImageData,
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("BufferedTexture2d")
) : Texture2d(props, name, ImageTextureLoader(data)) {
    fun updateTextureData(data: ImageData) {
        (loader as ImageTextureLoader).data = data
        if (loadingState == LoadingState.LOADED) {
            dispose()
        }
    }
}

class SingleColorTexture(color: Color) : Texture2d(
    props = TextureProps(generateMipMaps = false, defaultSamplerSettings = DEFAULT_SAMPLER_SETTINGS),
    name = "SingleColorTex:${color}",
    loader = ImageTextureLoader(getColorTextureData(color))
) {
    companion object {
        val DEFAULT_SAMPLER_SETTINGS = SamplerSettings(
            minFilter = FilterMethod.NEAREST,
            magFilter = FilterMethod.NEAREST,
            maxAnisotropy = 1,
        )

        private val colorData = mutableMapOf<Color, BufferedImageData2d>()

        fun getColorTextureData(color: Color): BufferedImageData2d {
            return colorData.getOrPut(color) { BufferedImageData2d.singleColor(color) }
        }
    }
}

class GradientTexture(
    gradient: ColorGradient,
    size: Int = 256,
    isClamped: Boolean = true,
    name: String = "gradientTex-$size"
) : Texture1d(
    props = TextureProps(
        format = TexFormat.RGBA_F16,    // f16 format yields much better results with gradients in linear color space
        generateMipMaps = false,
        defaultSamplerSettings = if (isClamped) DEFAULT_SAMPLER_SETTINGS_CLAMPED else DEFAULT_SAMPLER_SETTINGS_REPEATING
    ),
    name = name,
    loader = ImageTextureLoader(BufferedImageData1d.gradientF16(gradient, size))
) {
    companion object {
        val DEFAULT_SAMPLER_SETTINGS_CLAMPED = SamplerSettings(
            addressModeU = AddressMode.CLAMP_TO_EDGE,
            maxAnisotropy = 1,
        )
        val DEFAULT_SAMPLER_SETTINGS_REPEATING = DEFAULT_SAMPLER_SETTINGS_CLAMPED.copy(addressModeU = AddressMode.REPEAT)
    }
}
