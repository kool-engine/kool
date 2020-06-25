package de.fabmax.kool.util.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Texture(
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