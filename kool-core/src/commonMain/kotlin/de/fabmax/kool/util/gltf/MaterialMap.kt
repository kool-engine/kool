package de.fabmax.kool.util.gltf

import kotlinx.serialization.Serializable

@Serializable
data class MaterialMap(
        val index: Int,
        val texCoord: Int = 0,
        val scale: Float = 1f
) {
    fun getTexture(gltfFile: GltfFile): de.fabmax.kool.pipeline.Texture {
        return gltfFile.textures[index].makeTexture()
    }
}