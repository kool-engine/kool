package de.fabmax.kool.pipeline.backend

import de.fabmax.kool.util.Color

data class BackendFeatures(
    val computeShaders: Boolean,
    val cubeMapArrays: Boolean,
    val reversedDepth: Boolean,
    val depthOnlyShaderColorOutput: Color?,
)
