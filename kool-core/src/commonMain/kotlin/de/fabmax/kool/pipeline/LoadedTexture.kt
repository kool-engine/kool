package de.fabmax.kool.pipeline

import de.fabmax.kool.util.Releasable

interface LoadedTexture : Releasable {
    val width: Int
    val height: Int
    val depth: Int

    fun readTexturePixels(targetData: TextureData)
}