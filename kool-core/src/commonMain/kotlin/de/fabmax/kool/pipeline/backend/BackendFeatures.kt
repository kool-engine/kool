package de.fabmax.kool.pipeline.backend

import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.util.Color

data class BackendFeatures(
    val computeShaders: Boolean,
    val cubeMapArrays: Boolean,
    val reversedDepth: Boolean,
    val maxSamples: Int,
    val readWriteStorageTextures: Boolean,

    /**
     * Color value to output from a shader that only produces depth values. If null, no color output is needed.
     */
    val depthOnlyShaderColorOutput: Color?,

    val maxComputeWorkGroupsPerDimension: Vec3i,
    val maxComputeWorkGroupSize: Vec3i,
    val maxComputeInvocationsPerWorkgroup: Int,
)
