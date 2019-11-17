package de.fabmax.kool

import de.fabmax.kool.gl.*
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.Uint8Buffer
import kotlin.math.max

/**
 * Texture related classes. Texture stuff is split among several classes:
 *  - TextureProps define texture properties (filtering, etc)
 *  - TextureData wraps the actual image source which is loaded lazily
 *  - Texture wraps the OpenGL texture object and a TextureData generator, one object per texture instance
 *  - TextureResource represents the OpenGL resource object, single object per texture, can be shared between multiple
 *    Texture instances.
 *
 * @author fabmax
 */

fun defaultProps(id: String): TextureProps {
    return TextureProps(id, GL_LINEAR, GL_CLAMP_TO_EDGE)
}

fun defaultPropsRepeated(id: String): TextureProps {
    return TextureProps(id, GL_LINEAR, GL_REPEAT)
}

fun defaultPropsClamped(id: String): TextureProps {
    return TextureProps(id, GL_LINEAR, GL_REPEAT)
}

data class TextureProps(
        val id: String,
        val minFilter: Int,
        val magFilter: Int,
        val xWrapping: Int,
        val yWrapping: Int,
        val anisotropy: Int,
        val target: Int = GL_TEXTURE_2D) {

    constructor(id: String, filter: Int, wrapping: Int) :
            this(id, minFilter(filter), magFilter(filter), wrapping, wrapping, 16)

    constructor(id: String, filter: Int, wrapping: Int, anisotropy: Int) :
            this(id, minFilter(filter), magFilter(filter), wrapping, wrapping, anisotropy)

    companion object {
        val DEFAULT_MIN = GL_LINEAR_MIPMAP_LINEAR
        val DEFAULT_MAG = GL_LINEAR
        val DEFAULT_X_WRAP = GL_CLAMP_TO_EDGE
        val DEFAULT_Y_WRAP = GL_CLAMP_TO_EDGE

        private fun magFilter(filter: Int) = when (filter) {
            GL_NEAREST -> GL_NEAREST
            else -> DEFAULT_MAG
        }

        private fun minFilter(filter: Int) = when (filter) {
            GL_NEAREST -> GL_NEAREST
            else -> DEFAULT_MIN
        }
    }
}

abstract class TextureData {
    open var width = 0
        protected set
    open var height = 0
        protected set
    open var format = TexFormat.RGBA
        protected set

    abstract val isValid: Boolean

    abstract val data: Uint8Buffer?
}

/**
 * Byte buffer based texture data. Texture data can be generated and edited procedurally. Layout and format of data
 * is specified by the format parameter. The buffer size must match the texture size and data format.
 *
 * @param buffer texture data buffer, must have a size of width * height * bytes-per-pixel
 * @param width  width of texture in pixels
 * @param height height of texture in pixels
 * @param format texture data format
 */
class BufferedTextureData(buffer: Uint8Buffer, width: Int, height: Int, format: TexFormat) : TextureData() {

    init {
        this.width = width
        this.height = height
        this.format = format
    }

    override val data = buffer
    override val isValid = true
}

open class CubeMapTextureData(val front: TextureData, val back: TextureData, val left: TextureData,
                              val right: TextureData, val up: TextureData, val down: TextureData) : TextureData() {
//    override val isAvailable: Boolean get() = front.isAvailable && back.isAvailable && left.isAvailable &&
//            right.isAvailable && up.isAvailable && down.isAvailable
//
//    override fun onLoad(texture: Texture, target: Int, ctx: KoolContext) {
//        if (target != GL_TEXTURE_CUBE_MAP) {
//            throw KoolException("CubeMapTextureData can only be bound to target GL_TEXTURE_CUBE_MAP")
//        }
//
//        // load all cube map sides
//        front.onLoad(texture, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, ctx)
//        back.onLoad(texture, GL_TEXTURE_CUBE_MAP_POSITIVE_Z, ctx)
//        left.onLoad(texture, GL_TEXTURE_CUBE_MAP_NEGATIVE_X, ctx)
//        right.onLoad(texture, GL_TEXTURE_CUBE_MAP_POSITIVE_X, ctx)
//        up.onLoad(texture, GL_TEXTURE_CUBE_MAP_POSITIVE_Y, ctx)
//        down.onLoad(texture, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, ctx)
//
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)
//    }

    override val isValid: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val data: Uint8Buffer?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}

open class Texture(val props: TextureProps, val generator: Texture.(ctx: KoolContext) -> TextureData) :
        GlObject<TextureResource>() {

    var width = 0
        protected set
    var height = 0
        protected set

    var delayLoading = false

    override fun dispose(ctx: KoolContext) {
        // do not call super, as this will immediately delete the texture on the GPU. However, texture resource is
        // shared and might be used by other Texture objects...
        if (isValid) {
            ctx.textureMgr.deleteTexture(this, ctx)
            res = null
        }
    }

    /**
     * Called by [TextureManager] once texture data is available. This method is only called once per TextureResource
     * object; i.e. if TextureResource is shared between multiple Texture objects, this method is only called once.
     */
    open fun load(texData: TextureData, ctx: KoolContext) {
        if (!texData.isValid) {
            throw KoolException("Texture data is not available")
        }
        val res = res ?: throw KoolException("Texture wasn't created")

//        texData.loadData(this, ctx)
        width = texData.width
        height = texData.height

        // set filter properties
        glTexParameteri(res.target, GL_TEXTURE_MIN_FILTER, props.minFilter)
        glTexParameteri(res.target, GL_TEXTURE_MAG_FILTER, props.magFilter)
        glTexParameteri(res.target, GL_TEXTURE_WRAP_S, props.xWrapping)
        glTexParameteri(res.target, GL_TEXTURE_WRAP_T, props.yWrapping)
        if (props.anisotropy > 1 && ctx.glCapabilities.anisotropicTexFilterInfo.isSupported) {
            val anisotropy = max(ctx.glCapabilities.anisotropicTexFilterInfo.maxAnisotropy.toInt(), props.anisotropy)
            glTexParameteri(res.target, ctx.glCapabilities.anisotropicTexFilterInfo.TEXTURE_MAX_ANISOTROPY_EXT, anisotropy)
        }

        res.isLoaded = true
    }
}

open class CubeMapTexture(props: TextureProps, generator: Texture.(ctx: KoolContext) -> CubeMapTextureData) :
        Texture(props, generator) {
    init {
        if (props.target != GL_TEXTURE_CUBE_MAP) {
            throw KoolException("CubeMapTexture must be initialized with TextureProps.target = GL_TEXTURE_CUBE_MAP")
        }
    }
}
