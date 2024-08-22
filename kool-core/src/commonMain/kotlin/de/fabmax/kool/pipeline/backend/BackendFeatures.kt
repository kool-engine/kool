package de.fabmax.kool.pipeline.backend

data class BackendFeatures(
    val computeShaders: Boolean,
    val cubeMapArrays: Boolean,
    val reversedDepth: Boolean
)
