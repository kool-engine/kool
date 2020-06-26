package de.fabmax.kool.util.gltf

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
data class Texture(
        val sampler: Int = -1,
        val source: Int = 0,
        val name: String? = null
) {
    @Transient
    lateinit var imageRef: Image

    @Transient
    private var createdTex: de.fabmax.kool.pipeline.Texture? = null

    fun makeTexture(): de.fabmax.kool.pipeline.Texture {
        if (createdTex == null) {
            val uri = imageRef.uri
            val name = if (uri != null && !uri.startsWith("data:", true)) {
                uri
            } else {
                "gltf_tex_$source"
            }
            createdTex = de.fabmax.kool.pipeline.Texture(name) { assetMgr ->
                if (uri != null) {
                    assetMgr.loadTextureData(uri)
                } else {
                    assetMgr.createTextureData(imageRef.bufferViewRef!!.getData(), imageRef.mimeType!!)
                }
            }
        }
        return createdTex!!
    }
}