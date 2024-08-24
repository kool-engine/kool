package de.fabmax.kool.editor.api

import de.fabmax.kool.pipeline.TexFormat

sealed interface AssetReference {
    data class Texture(
        val path: String,
        val texFormat: TexFormat = TexFormat.RGBA
    ) : AssetReference

    data class TextureArray(
        val paths: List<String>,
        val texFormat: TexFormat = TexFormat.RGBA
    ) : AssetReference

    data class Hdri(val path: String) : AssetReference

    data class Model(val path: String) : AssetReference

    data class Blob(val path: String) : AssetReference

    data class Heightmap(
        val path: String,
        val heightScale: Float = 1f,
        val heightOffset: Float = 0f,
        val rows: Int = 0,
        val columns: Int = 0
    ) : AssetReference
}