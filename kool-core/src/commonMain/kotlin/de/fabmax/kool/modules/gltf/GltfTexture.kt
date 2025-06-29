package de.fabmax.kool.modules.gltf

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.Assets
import de.fabmax.kool.MimeType
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A texture and its sampler.
 *
 * @param sampler The index of the sampler used by this texture. When undefined, a sampler with repeat wrapping and
 *                auto filtering should be used.
 * @param source  The index of the image used by this texture. When undefined, it is expected that an extension or
 *                other mechanism will supply an alternate texture source, otherwise behavior is undefined.
 * @param name    The user-defined name of this object.
 */
@Serializable
data class GltfTexture(
    val sampler: Int = -1,
    val source: Int = 0,
    val name: String? = null
) {
    @Transient
    lateinit var imageRef: GltfImage
    @Transient
    var samplerRef: GltfSampler? = null

    @Transient
    private var createdTex: Texture2d? = null

    fun makeTexture(assetLoader: AssetLoader): Texture2d {
        if (createdTex == null) {
            val uri = imageRef.uri
            val name = if (uri != null && !uri.startsWith("data:", true)) {
                uri
            } else {
                "gltf_tex_$source"
            }

            val sampler = samplerRef ?: GltfSampler()
            val samplerSettings = SamplerSettings(
                addressModeU = sampler.addressModeU,
                addressModeV = sampler.addressModeV,
                minFilter = sampler.minFilterKool,
                magFilter = sampler.magFilterKool,
            )
            val mipMapping = if (samplerSettings.minFilter != FilterMethod.NEAREST) MipMapping.Full else MipMapping.Off
            createdTex = Texture2d(
                format = TexFormat.RGBA,
                mipMapping = mipMapping,
                samplerSettings = samplerSettings,
                name
            ) {
                if (uri != null) {
                    assetLoader.loadImage2d(uri).getOrDefault(SingleColorTexture.getColorTextureData(Color.MAGENTA))
                } else {
                    Assets.loadImageFromBuffer(imageRef.bufferViewRef!!.getData(), MimeType(imageRef.mimeType!!))
                }
            }
        }
        return createdTex!!
    }
}