package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.stats.TextureInfo
import kotlin.math.min

class LoadedTextureGl(
    val target: Int,
    val glTexture: GlTexture,
    val backend: RenderBackendGl,
    val texture: Texture,
    estimatedSize: Long
) : LoadedTexture {

    val texId = nextTexId++
    override var isReleased = false
        private set

    override var width = 0
    override var height = 0
    override var depth = 0

    private val gl = backend.gl

    private val allocationInfo = TextureInfo(texture, estimatedSize)

    private var currentSamplerSettings: SamplerSettings? = null

    fun setSize(width: Int, height: Int, depth: Int) {
        this.width = width
        this.height = height
        this.depth = depth
    }

    fun bind() {
        gl.bindTexture(target, glTexture)
    }

    fun applySamplerSettings(samplerSettings: SamplerSettings?) {
        val settings = samplerSettings ?: texture.props.defaultSamplerSettings
        if (settings == currentSamplerSettings) {
            return
        }

        val isMipMapped = texture.props.generateMipMaps
        currentSamplerSettings = settings

        gl.texParameteri(target, gl.TEXTURE_MIN_FILTER, settings.minFilter.glMinFilterMethod(isMipMapped))
        gl.texParameteri(target, gl.TEXTURE_MAG_FILTER, settings.magFilter.glMagFilterMethod())
        gl.texParameteri(target, gl.TEXTURE_WRAP_S, settings.addressModeU.glAddressMode())
        gl.texParameteri(target, gl.TEXTURE_WRAP_T, settings.addressModeV.glAddressMode())
        if (target == gl.TEXTURE_3D) {
            gl.texParameteri(target, gl.TEXTURE_WRAP_R, settings.addressModeW.glAddressMode())
        }

        val anisotropy = min(settings.maxAnisotropy, gl.capabilities.maxAnisotropy)
        if (anisotropy > 1 && isMipMapped &&
            settings.minFilter == FilterMethod.LINEAR &&
            settings.magFilter == FilterMethod.LINEAR
        ) {
            gl.texParameteri(target, gl.TEXTURE_MAX_ANISOTROPY_EXT, anisotropy)
        }
    }

    override fun readTexturePixels(targetData: TextureData) {
        if (target != gl.TEXTURE_2D) {
            throw IllegalStateException("readTexturePixels() is only supported for 2D textures")
        }
        if (targetData.width != width || targetData.height != height) {
            throw IllegalArgumentException("supplied targetData dimension does not match texture size " +
                    "(supplied: ${targetData.width} x ${targetData.height}, actual: $width x $height)")
        }
        gl.readTexturePixels(this, targetData)
    }

    override fun release() {
        if (!isReleased) {
            isReleased = true
            gl.deleteTexture(glTexture)
            allocationInfo.deleted()
        }
    }

    private fun FilterMethod.glMinFilterMethod(mipMapping: Boolean): Int {
        return when(this) {
            FilterMethod.NEAREST -> if (mipMapping) gl.NEAREST_MIPMAP_NEAREST else gl.NEAREST
            FilterMethod.LINEAR -> if (mipMapping) gl.LINEAR_MIPMAP_LINEAR else gl.LINEAR
        }
    }

    private fun FilterMethod.glMagFilterMethod(): Int {
        return when (this) {
            FilterMethod.NEAREST -> gl.NEAREST
            FilterMethod.LINEAR -> gl.LINEAR
        }
    }

    private fun AddressMode.glAddressMode(): Int {
        return when(this) {
            AddressMode.CLAMP_TO_EDGE -> gl.CLAMP_TO_EDGE
            AddressMode.MIRRORED_REPEAT -> gl.MIRRORED_REPEAT
            AddressMode.REPEAT -> gl.REPEAT
        }
    }

    companion object {
        private var nextTexId = 1L
    }
}