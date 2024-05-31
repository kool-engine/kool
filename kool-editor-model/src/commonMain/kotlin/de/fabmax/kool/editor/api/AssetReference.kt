package de.fabmax.kool.editor.api

import de.fabmax.kool.pipeline.TexFormat

sealed interface AssetReference {
    val path: String

    data class Texture(
        override val path: String,
        val texFormat: TexFormat = TexFormat.RGBA
    ) : AssetReference

    data class Hdri(override val path: String) : AssetReference

    data class Model(override val path: String) : AssetReference

    data class Blob(override val path: String) : AssetReference

    data class Heightmap(
        override val path: String,
        val heightScale: Float,
        val heightOffset: Float = 0f,
        val rows: Int = 0,
        val columns: Int = 0
    ) : AssetReference
}