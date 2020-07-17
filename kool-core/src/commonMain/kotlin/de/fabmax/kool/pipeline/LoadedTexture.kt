package de.fabmax.kool.pipeline

interface LoadedTexture {
    val width: Int
    val height: Int

    fun dispose()
}