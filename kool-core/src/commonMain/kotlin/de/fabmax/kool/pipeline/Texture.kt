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
    val format: TexFormat,
    open val mipMapping: MipMapping,
    val samplerSettings: SamplerSettings,
    val name: String,
): BaseReleasable() {
    /**
     * Contains the platform specific handle to the loaded texture. It is available after the loader function was
     * called.
     */
    var gpuTexture: GpuTexture? = null
    var uploadData: T? = null
        set(value) {
            field = value
            value?.let { checkFormat(it.format) }
        }

    val isLoaded: Boolean get() = gpuTexture != null

    val width: Int
        get() = gpuTexture?.width ?: 0
    val height: Int
        get() = gpuTexture?.height ?: 0
    val depth: Int
        get() = gpuTexture?.depth ?: 0

    /**
     * Releases this texture, making it unusable.
     */
    override fun release() {
        super.release()
        gpuTexture?.release()
        gpuTexture = null
    }

    override fun toString(): String {
        return "${this::class.simpleName}:$name"
    }

    suspend fun upload(imageData: T) {
        checkFormat(imageData.format)
        withContext(Dispatchers.RenderLoop) {
            uploadData = imageData
            KoolSystem.requireContext().backend.uploadTextureData(this@Texture)
        }
    }

    fun uploadLazy(imageData: T) {
        uploadData = imageData
    }

    fun uploadLazy(provider: suspend CoroutineScope.() -> T) = Assets.launch {
        uploadData = provider()
    }

    private fun checkFormat(format: TexFormat) {
        check(format == this.format) {
            "Given image format doesn't match this texture ($name): $format != ${this.format}"
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
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture1d")
) : Texture<ImageData1d>(format, mipMapping, samplerSettings, name) {

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
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture1d")
): Texture1d = Texture1d(data.format, mipMapping, samplerSettings, name).apply { uploadLazy(data) }

fun Texture1d(
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture1d"),
    loader: (suspend CoroutineScope.() -> ImageData1d)
): Texture1d = Texture1d(format, mipMapping, samplerSettings, name).apply { uploadLazy(loader) }


open class Texture2d(
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture2d"),
) : Texture<ImageData2d>(format, mipMapping, samplerSettings, name) {

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
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture2d")
): Texture2d = Texture2d(data.format, mipMapping, samplerSettings, name).apply { uploadLazy(data) }

fun Texture2d(
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture2d"),
    loader: (suspend CoroutineScope.() -> ImageData2d)
): Texture2d = Texture2d(format, mipMapping, samplerSettings, name).apply { uploadLazy(loader) }


open class Texture3d(
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture3d")
) : Texture<ImageData3d>(format, mipMapping, samplerSettings, name) {

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
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture3d")
): Texture3d = Texture3d(data.format, mipMapping, samplerSettings, name).apply { uploadLazy(data) }

fun Texture3d(
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture3d"),
    loader: (suspend CoroutineScope.() -> ImageData3d)
): Texture3d = Texture3d(format, mipMapping, samplerSettings, name).apply { uploadLazy(loader) }


open class TextureCube(
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("TextureCube")
) : Texture<ImageDataCube>(format, mipMapping, samplerSettings, name)

fun TextureCube(
    data: ImageDataCube,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("TextureCube")
): TextureCube = TextureCube(data.format, mipMapping, samplerSettings, name).apply { uploadLazy(data) }

fun TextureCube(
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("TextureCube"),
    loader: (suspend CoroutineScope.() -> ImageDataCube)
): TextureCube = TextureCube(format, mipMapping, samplerSettings, name).apply { uploadLazy(loader) }

open class Texture2dArray(
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture2dArray"),
) : Texture<ImageData3d>(format, mipMapping, samplerSettings, name)

fun Texture2dArray(
    data: ImageData3d,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture2dArray")
): Texture2dArray = Texture2dArray(data.format, mipMapping, samplerSettings, name).apply { uploadLazy(data) }

fun Texture2dArray(
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture2dArray"),
    loader: (suspend CoroutineScope.() -> ImageData3d)
): Texture2dArray = Texture2dArray(format, mipMapping, samplerSettings, name).apply { uploadLazy(loader) }

open class TextureCubeArray(
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("TextureCubeArray"),
) : Texture<ImageDataCubeArray>(format, mipMapping, samplerSettings, name)

fun TextureCubeArray(
    data: ImageDataCubeArray,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("TextureCubeArray")
): TextureCubeArray = TextureCubeArray(data.format, mipMapping, samplerSettings, name).apply { uploadLazy(data) }

fun TextureCubeArray(
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("TextureCubeArray"),
    loader: (suspend CoroutineScope.() -> ImageDataCubeArray)
): TextureCubeArray = TextureCubeArray(format, mipMapping, samplerSettings, name).apply { uploadLazy(loader) }

class SingleColorTexture(color: Color) : Texture2d(
    format = TexFormat.RGBA,
    mipMapping = MipMapping.Off,
    samplerSettings = DEFAULT_SAMPLER_SETTINGS,
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
    format = TexFormat.RGBA_F16,    // f16 format yields much better results with gradients in linear color space
    mipMapping = MipMapping.Off,
    samplerSettings = if (isClamped) DEFAULT_SAMPLER_SETTINGS_CLAMPED else DEFAULT_SAMPLER_SETTINGS_REPEATING,
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
