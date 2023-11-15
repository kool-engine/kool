package de.fabmax.kool.pipeline

import de.fabmax.kool.Assets
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.util.*
import kotlinx.coroutines.CoroutineScope
import kotlin.math.roundToInt

/**
 * Describes a texture by its properties and a loader function which is called once the texture is used.
 */
abstract class Texture(val props: TextureProps, val name: String, val loader: TextureLoader? = null): BaseReleasable() {

    /**
     * Contains the platform specific handle to the loaded texture. It is available after the loader function was
     * called.
     */
    var loadedTexture: LoadedTexture? = null

    var loadingState = LoadingState.NOT_LOADED

    /**
     * Disposes the underlying texture memory and resets the [loadingState] to [LoadingState.NOT_LOADED]. In contrast
     * to [release], the texture can still be used after disposing it - the texture will be reloaded. Disposing
     * textures is useful to update changed texture data.
     *
     * @see release
     */
    open fun dispose() {
        loadedTexture?.release()
        loadedTexture = null
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

    constructor(
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("Texture1d"),
        loader: (suspend CoroutineScope.() -> TextureData1d)? = null
    ) : this(props, name, loader?.let { AsyncTextureLoader(it) })
}

open class Texture2d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture2d"),
    loader: TextureLoader?
) : Texture(props, name, loader) {

    constructor(
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("Texture2d"),
        loader: (suspend CoroutineScope.() -> TextureData)? = null
    ) : this(props, name, loader?.let { AsyncTextureLoader(it) })

    constructor(
        props: TextureProps = TextureProps(),
        data: TextureData2d,
        name: String = UniqueId.nextId("Texture2d")
    ) : this(props, name, BufferedTextureLoader(data))

    constructor(
        assetPath: String,
        name: String = UniqueId.nextId("Texture2d"),
        props: TextureProps = TextureProps()
    ) : this(props, name, AsyncTextureLoader { Assets.loadTextureData(assetPath, props) })

    fun readTexturePixels(): TextureData2d? {
        val tex = loadedTexture ?: return null
        val bufferSize = tex.width * tex.height * props.format.channels
        val buffer = if (props.format.isFloat) {
            Float32Buffer(bufferSize)
        } else {
            Uint8Buffer(bufferSize)
        }
        val data = TextureData2d(buffer, tex.width, tex.height, props.format)
        tex.readTexturePixels(data)
        return data
    }
}

open class Texture3d(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("Texture3d"),
    loader: TextureLoader?
) : Texture(props, name, loader) {

    constructor(
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("Texture3d"),
        loader: (suspend CoroutineScope.() -> TextureData)? = null
    ) : this(props, name, loader?.let { AsyncTextureLoader(it) })

    constructor(
        props: TextureProps = TextureProps(),
        data: TextureData3d,
        name: String = UniqueId.nextId("Texture3d")
    ) : this(props, name, BufferedTextureLoader(data))

}

open class TextureCube(
    props: TextureProps = TextureProps(),
    name: String = UniqueId.nextId("TextureCube"),
    loader: TextureLoader?
) : Texture(props, name, loader) {

    constructor(
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("TextureCube"),
        loader: (suspend CoroutineScope.() -> TextureDataCube)? = null
    ) : this(props, name, loader?.let { AsyncTextureLoader(it) })

}

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
    TextureProps(
        minFilter = FilterMethod.NEAREST,
        magFilter = FilterMethod.NEAREST,
        mipMapping = false,
        maxAnisotropy = 1
    ),
    name = "SingleColorTex:${color}",
    loader = BufferedTextureLoader(getColorTextureData(color))
) {

    companion object {
        private val colorData = mutableMapOf<Color, TextureData2d>()

        private fun getColorTextureData(color: Color): TextureData2d {
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
    TextureProps(
        format = TexFormat.RGBA_F16,    // use f16 texture for better results together with linear color gradients
        addressModeU = if (isClamped) AddressMode.CLAMP_TO_EDGE else AddressMode.REPEAT,
        addressModeV = if (isClamped) AddressMode.CLAMP_TO_EDGE else AddressMode.REPEAT,
        minFilter = FilterMethod.LINEAR,
        magFilter = FilterMethod.LINEAR,
        mipMapping = false,
        maxAnisotropy = 1),
    name = name,
    loader = BufferedTextureLoader(TextureData1d.gradientF16(gradient, size))
)

data class TextureProps(
    val format: TexFormat = TexFormat.RGBA,
    val addressModeU: AddressMode = AddressMode.REPEAT,
    val addressModeV: AddressMode = AddressMode.REPEAT,
    val addressModeW: AddressMode = AddressMode.REPEAT,
    val minFilter: FilterMethod = FilterMethod.LINEAR,
    val magFilter: FilterMethod = FilterMethod.LINEAR,
    val mipMapping: Boolean = true,
    val maxAnisotropy: Int = 4,

    /**
     * If non-null, the loader implementation will try to scale the loaded texture image to the given size in pixels.
     * This is particular useful to scale vector (SVG) images to a desired resolution on load.
     */
    val preferredSize: Vec2i? = null
)

enum class FilterMethod {
    NEAREST,
    LINEAR
}

enum class AddressMode {
    CLAMP_TO_EDGE,
    MIRRORED_REPEAT,
    REPEAT
}

abstract class TextureData {
    var width = 0
        protected set
    var height = 0
        protected set
    var depth = 0
        protected set
    var format = TexFormat.RGBA
        protected set

    abstract val data: Any
}

class TextureData1d(override val data: Buffer, width: Int, format: TexFormat) : TextureData() {
    init {
        this.width = width
        this.height = 1
        this.depth = 1
        this.format = format
    }

    companion object {
        fun gradient(gradient: ColorGradient, size: Int): TextureData1d {
            val buf = Uint8Buffer(4 * size)
            val color = MutableColor()
            for (i in 0 until size) {
                gradient.getColorInterpolated(i / (size - 1f), color)
                buf[i * 4 + 0] = (color.r * 255f).roundToInt().toUByte()
                buf[i * 4 + 1] = (color.g * 255f).roundToInt().toUByte()
                buf[i * 4 + 2] = (color.b * 255f).roundToInt().toUByte()
                buf[i * 4 + 3] = (color.a * 255f).roundToInt().toUByte()
            }
            return TextureData1d(buf, size, TexFormat.RGBA)
        }

        fun gradientF16(gradient: ColorGradient, size: Int): TextureData1d {
            val buf = Float32Buffer(4 * size)
            val color = MutableColor()
            for (i in 0 until size) {
                gradient.getColorInterpolated(i / (size - 1f), color)
                buf[i * 4 + 0] = color.r
                buf[i * 4 + 1] = color.g
                buf[i * 4 + 2] = color.b
                buf[i * 4 + 3] = color.a
            }
            return TextureData1d(buf, size, TexFormat.RGBA_F16)
        }
    }
}

/**
 * Buffer based 2d texture data. Texture data can be generated and edited procedurally. Layout and format of data
 * is specified by the format parameter. The buffer size must match the texture size and data format.
 *
 * @param data   texture data buffer, must have a size of width * height * bytes-per-pixel
 * @param width  width of texture in pixels
 * @param height height of texture in pixels
 * @param format texture data format
 */
open class TextureData2d(override val data: Buffer, width: Int, height: Int, format: TexFormat) : TextureData() {
    init {
        this.width = width
        this.height = height
        this.depth = 1
        this.format = format
    }

    companion object {
        fun singleColor(color: Color): TextureData2d {
            val buf = Uint8Buffer(4)
            buf[0] = (color.r * 255f).roundToInt().toUByte()
            buf[1] = (color.g * 255f).roundToInt().toUByte()
            buf[2] = (color.b * 255f).roundToInt().toUByte()
            buf[3] = (color.a * 255f).roundToInt().toUByte()
            return TextureData2d(buf, 1, 1, TexFormat.RGBA)
        }
    }
}

open class TextureData3d(override val data: Buffer, width: Int, height: Int, depth: Int, format: TexFormat) : TextureData() {
    init {
        this.width = width
        this.height = height
        this.depth = depth
        this.format = format
    }
}

class TextureDataCube(val front: TextureData, val back: TextureData, val left: TextureData,
                      val right: TextureData, val up: TextureData, val down: TextureData) : TextureData() {
    init {
        width = front.width
        height = front.height
        depth = 1
        format = front.format
    }

    override val data: Any
        get() = front.data
}