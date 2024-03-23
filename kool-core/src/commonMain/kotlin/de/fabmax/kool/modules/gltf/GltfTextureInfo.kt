package de.fabmax.kool.modules.gltf

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.pipeline.Texture2d
import kotlinx.serialization.Serializable

/**
 * Reference to a texture (color, emissive, normal, occlusion).
 *
 * @param index    The index of the texture.
 * @param strength A scalar multiplier controlling the amount of occlusion applied (for occlusion textures only).
 * @param texCoord The set index of texture's TEXCOORD attribute used for texture coordinate mapping.
 * @param scale    The scalar multiplier applied to each normal vector of the normal texture.
 */
@Serializable
data class GltfTextureInfo(
    val index: Int,
    val strength: Float = 1f,
    val texCoord: Int = 0,
    val scale: Float = 1f
) {
    fun getTexture(gltfFile: GltfFile, assetLoader: AssetLoader): Texture2d {
        return gltfFile.textures[index].makeTexture(assetLoader)
    }
}