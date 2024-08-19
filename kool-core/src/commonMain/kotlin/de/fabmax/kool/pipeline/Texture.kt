package de.fabmax.kool.pipeline

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.backend.GpuTexture
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.UniqueId
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
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
    suspend inline fun readbackTextureData(): TextureData1d {
        val deferred = CompletableDeferred<TextureData>()
        KoolSystem.requireContext().backend.readTextureData(this, deferred)

        val buffer = deferred.await()
        check(buffer is TextureData1d)
        return buffer
    }
}

fun Texture1d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture1d"),
    loader: (suspend CoroutineScope.() -> TextureData1d)? = null
): Texture1d = Texture1d(props, name, loader?.let { AsyncTextureLoader(it) })

fun Texture1d(
    props: TextureProps = TextureProps(),
    data: TextureData1d,
    name: String = UniqueId.nextId("Texture2d")
): Texture1d = Texture1d(props, name, BufferedTextureLoader(data))

open class Texture2d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture2d"),
    loader: TextureLoader?
) : Texture(props, name, loader) {
    suspend inline fun readbackTextureData(): TextureData2d {
        val deferred = CompletableDeferred<TextureData>()
        KoolSystem.requireContext().backend.readTextureData(this, deferred)

        val buffer = deferred.await()
        check(buffer is TextureData2d)
        return buffer
    }
}

fun Texture2d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture2d"),
    loader: (suspend CoroutineScope.() -> TextureData)? = null
): Texture2d = Texture2d(props, name, loader?.let { AsyncTextureLoader(it) })

fun Texture2d(
    props: TextureProps = TextureProps(),
    data: TextureData2d,
    name: String = UniqueId.nextId("Texture2d")
): Texture2d = Texture2d(props, name, BufferedTextureLoader(data))

@Deprecated("You should use Assets.loadTexture2d() instead", ReplaceWith("Assets.loadTexture2d(assetPath)"))
fun Texture2d(
    assetPath: String,
    name: String = UniqueId.nextId("Texture2d"),
    props: TextureProps = TextureProps()
): Texture2d = Texture2d(
    props,
    name,
    AsyncTextureLoader {
        Assets.loadTextureData(assetPath, props).getOrDefault(SingleColorTexture.getColorTextureData(Color.MAGENTA))
    }
)

open class Texture3d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture3d"),
    loader: TextureLoader?
) : Texture(props, name, loader) {
    suspend inline fun readbackTextureData(): TextureData3d {
        val deferred = CompletableDeferred<TextureData>()
        KoolSystem.requireContext().backend.readTextureData(this, deferred)

        val buffer = deferred.await()
        check(buffer is TextureData3d)
        return buffer
    }
}

fun Texture3d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture3d"),
    loader: (suspend CoroutineScope.() -> TextureData)? = null
): Texture3d = Texture3d(props, name, loader?.let { AsyncTextureLoader(it) })

fun Texture3d(
    props: TextureProps = TextureProps(),
    data: TextureData3d,
    name: String = UniqueId.nextId("Texture3d")
): Texture3d = Texture3d(props, name, BufferedTextureLoader(data))

open class TextureCube(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("TextureCube"),
    loader: TextureLoader?
) : Texture(props, name, loader)

fun TextureCube(
    props: TextureProps = TextureProps(),
    data: TextureDataCube,
    name: String = UniqueId.nextId("Texture3d")
): TextureCube = TextureCube(props, name, BufferedTextureLoader(data))

fun TextureCube(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("TextureCube"),
    loader: (suspend CoroutineScope.() -> TextureDataCube)? = null
): TextureCube = TextureCube(props, name, loader?.let { AsyncTextureLoader(it) })

class BufferedTexture2d(
    data: TextureData,
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("BufferedTexture2d")
) : Texture2d(props, name, BufferedTextureLoader(data)) {
    fun updateTextureData(data: TextureData) {
        (loader as BufferedTextureLoader).data = data
        if (loadingState == LoadingState.LOADED) {
            dispose()
        }
    }
}

class SingleColorTexture(color: Color) : Texture2d(
    props = TextureProps(generateMipMaps = false, defaultSamplerSettings = DEFAULT_SAMPLER_SETTINGS),
    name = "SingleColorTex:${color}",
    loader = BufferedTextureLoader(getColorTextureData(color))
) {
    companion object {
        val DEFAULT_SAMPLER_SETTINGS = SamplerSettings(
            minFilter = FilterMethod.NEAREST,
            magFilter = FilterMethod.NEAREST,
            maxAnisotropy = 1,
        )

        private val colorData = mutableMapOf<Color, TextureData2d>()

        fun getColorTextureData(color: Color): TextureData2d {
            return colorData.getOrPut(color) { TextureData2d.singleColor(color) }
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
    loader = BufferedTextureLoader(TextureData1d.gradientF16(gradient, size))
) {
    companion object {
        val DEFAULT_SAMPLER_SETTINGS_CLAMPED = SamplerSettings(
            addressModeU = AddressMode.CLAMP_TO_EDGE,
            maxAnisotropy = 1,
        )
        val DEFAULT_SAMPLER_SETTINGS_REPEATING = DEFAULT_SAMPLER_SETTINGS_CLAMPED.copy(addressModeU = AddressMode.REPEAT)
    }
}
