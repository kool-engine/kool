package de.fabmax.kool.pipeline

import de.fabmax.kool.AssetManager
import de.fabmax.kool.util.*
import kotlinx.coroutines.CoroutineScope
import kotlin.math.roundToInt

/**
 * Describes a texture by it's properties and a loader function which is called once the texture is used.
 */
abstract class Texture(val props: TextureProps, val name: String?, val loader: (suspend CoroutineScope.(AssetManager) -> TextureData)?) {

    /**
     * Contains the platform specific handle to the loaded texture. It is available after the loader function was
     * called.
     */
    var loadedTexture: LoadedTexture? = null

    var loadingState = LoadingState.NOT_LOADED

    protected abstract val type: String

    fun dispose() {
        loadedTexture?.dispose()
        loadedTexture = null
        loadingState = LoadingState.NOT_LOADED
    }

    override fun toString(): String {
        return "Texture$type(name: $name)"
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

open class Texture1d(props: TextureProps = TextureProps(), name: String? = null, loader: (suspend CoroutineScope.(AssetManager) -> TextureData1d)? = null) :
        Texture(props, name, loader) {

    override val type = "1D"
}

open class Texture2d(props: TextureProps = TextureProps(), name: String? = null, loader: (suspend CoroutineScope.(AssetManager) -> TextureData)? = null) :
        Texture(props, name, loader) {

    override val type = "2D"

    constructor(assetPath: String, name: String? = null, props: TextureProps = TextureProps()) :
            this(props, name, { it.loadTextureData(assetPath, props.format) })
}

open class Texture3d(props: TextureProps = TextureProps(), name: String? = null, loader: (suspend CoroutineScope.(AssetManager) -> TextureData)? = null) :
        Texture(props, name, loader) {

    override val type = "3D"
}

open class TextureCube(props: TextureProps = TextureProps(), name: String? = null, loader: (suspend CoroutineScope.(AssetManager) -> TextureDataCube)? = null) :
        Texture(props, name, loader) {

    override val type = "Cube"
}

class SingleColorTexture(color: Color) : Texture2d(
        TextureProps(
                minFilter = FilterMethod.NEAREST,
                magFilter = FilterMethod.NEAREST,
                mipMapping = false,
                maxAnisotropy = 1),
        color.toString(),
        loader = { getColorTextureData(color) }) {

    override val type = "SingleColor"

    companion object {
        private val colorData = mutableMapOf<Color, TextureData2d>()

        private fun getColorTextureData(color: Color): TextureData2d {
            return colorData.getOrPut(color) { TextureData2d.singleColor(color) }
        }
    }
}

class GradientTexture(gradient: ColorGradient, size: Int = 256) : Texture1d(
        TextureProps(
                addressModeU = AddressMode.CLAMP_TO_EDGE,
                addressModeV = AddressMode.CLAMP_TO_EDGE,
                minFilter = FilterMethod.LINEAR,
                magFilter = FilterMethod.LINEAR,
                mipMapping = false,
                maxAnisotropy = 1),
        "gradientTex-$size",
        loader = { TextureData1d.gradient(gradient, size) }) {

    override val type = "Gradient"

}

data class TextureProps(
        val format: TexFormat = TexFormat.RGBA,
        val addressModeU: AddressMode = AddressMode.REPEAT,
        val addressModeV: AddressMode = AddressMode.REPEAT,
        val addressModeW: AddressMode = AddressMode.REPEAT,
        val minFilter: FilterMethod = FilterMethod.LINEAR,
        val magFilter: FilterMethod = FilterMethod.LINEAR,
        val mipMapping: Boolean = true,
        val maxAnisotropy: Int = 4)

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
            val buf = createUint8Buffer(4 * size)
            val color = MutableColor()
            for (i in 0 until size) {
                gradient.getColorInterpolated(i / (size - 1f), color)
                buf[i * 4 + 0] = (color.r * 255f).roundToInt().toByte()
                buf[i * 4 + 1] = (color.g * 255f).roundToInt().toByte()
                buf[i * 4 + 2] = (color.b * 255f).roundToInt().toByte()
                buf[i * 4 + 3] = (color.a * 255f).roundToInt().toByte()
            }
            return TextureData1d(buf, size, TexFormat.RGBA)
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
            val buf = createUint8Buffer(4)
            buf[0] = (color.r * 255f).roundToInt().toByte()
            buf[1] = (color.g * 255f).roundToInt().toByte()
            buf[2] = (color.b * 255f).roundToInt().toByte()
            buf[3] = (color.a * 255f).roundToInt().toByte()
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