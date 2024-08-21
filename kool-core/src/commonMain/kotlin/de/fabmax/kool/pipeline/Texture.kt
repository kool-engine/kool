package de.fabmax.kool.pipeline

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.backend.GpuTexture
import de.fabmax.kool.util.*
import kotlinx.coroutines.*
import kotlin.math.roundToInt

/**
 * Describes a texture by its properties and a loader function which is called once the texture is used.
 */
abstract class Texture<T: ImageData>(
    val props: TextureProps,
    val name: String,
): BaseReleasable() {
    /**
     * Contains the platform specific handle to the loaded texture. It is available after the loader function was
     * called.
     */
    var gpuTexture: GpuTexture? = null
    internal var uploadData: T? = null
        set(value) {
            field = value
            value?.let { checkFormat(it.format) }
        }

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

    suspend fun upload(imageData: T) {
        checkFormat(imageData.format)
        withContext(Dispatchers.RenderLoop) {
            KoolSystem.requireContext().backend.uploadTextureData(this@Texture, imageData)
        }
    }

    fun uploadLazy(imageData: T) {
        uploadData = imageData
    }

    fun uploadLazy(provider: suspend CoroutineScope.() -> T) = Assets.launch {
        uploadData = provider()
    }

    private fun checkFormat(format: TexFormat) {
        check(format == props.format) {
            "Given image format doesn't match this texture: $format != ${props.format}"
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
    name: String = UniqueId.nextId("Texture1d")
) : Texture<ImageData1d>(props, name) {

    suspend fun download(): BufferedImageData1d {
        val deferred = CompletableDeferred<ImageData>()
        KoolSystem.requireContext().backend.downloadTextureData(this, deferred)
        val buffer = deferred.await()
        check(buffer is BufferedImageData1d)
        return buffer
    }
}

fun Texture1d(
    data: ImageData1d,
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture2d")
): Texture1d = Texture1d(props, name).apply { uploadLazy(data) }

fun Texture1d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture1d"),
    loader: (suspend CoroutineScope.() -> ImageData1d)
): Texture1d = Texture1d(props, name).apply { uploadLazy(loader) }


open class Texture2d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture2d"),
) : Texture<ImageData2d>(props, name) {

    suspend fun download(): BufferedImageData2d {
        val deferred = CompletableDeferred<ImageData>()
        KoolSystem.requireContext().backend.downloadTextureData(this, deferred)
        val buffer = deferred.await()
        check(buffer is BufferedImageData2d)
        return buffer
    }
}

fun Texture2d(
    data: ImageData2d,
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture2d")
): Texture2d = Texture2d(props, name).apply { uploadLazy(data) }

fun Texture2d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture2d"),
    loader: (suspend CoroutineScope.() -> ImageData2d)
): Texture2d = Texture2d(props, name).apply { uploadLazy(loader) }


open class Texture3d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture3d")
) : Texture<ImageData3d>(props, name) {

    suspend fun download(): BufferedImageData3d {
        val deferred = CompletableDeferred<ImageData>()
        KoolSystem.requireContext().backend.downloadTextureData(this, deferred)
        val buffer = deferred.await()
        check(buffer is BufferedImageData3d)
        return buffer
    }
}

fun Texture3d(
    data: ImageData3d,
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture3d")
): Texture3d = Texture3d(props, name).apply { uploadLazy(data) }

fun Texture3d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture3d"),
    loader: (suspend CoroutineScope.() -> ImageData3d)
): Texture3d = Texture3d(props, name).apply { uploadLazy(loader) }


open class TextureCube(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("TextureCube")
) : Texture<ImageDataCube>(props, name)

fun TextureCube(
    data: ImageDataCube,
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture3d")
): TextureCube = TextureCube(props, name).apply { uploadLazy(data) }

fun TextureCube(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("TextureCube"),
    loader: (suspend CoroutineScope.() -> ImageDataCube)
): TextureCube = TextureCube(props, name).apply { uploadLazy(loader) }


class SingleColorTexture(color: Color) : Texture2d(
    props = TextureProps(generateMipMaps = false, defaultSamplerSettings = DEFAULT_SAMPLER_SETTINGS),
    name = "SingleColorTex:${color}"
) {
    init {
        uploadLazy(getColorTextureData(color))
    }

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
    name = name
) {
    init {
        uploadLazy(BufferedImageData1d.gradientF16(gradient, size))
    }

    companion object {
        val DEFAULT_SAMPLER_SETTINGS_CLAMPED = SamplerSettings(
            addressModeU = AddressMode.CLAMP_TO_EDGE,
            maxAnisotropy = 1,
        )
        val DEFAULT_SAMPLER_SETTINGS_REPEATING = DEFAULT_SAMPLER_SETTINGS_CLAMPED.copy(addressModeU = AddressMode.REPEAT)
    }
}
