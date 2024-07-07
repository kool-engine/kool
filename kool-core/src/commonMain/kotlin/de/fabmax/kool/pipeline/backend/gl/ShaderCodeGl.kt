package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.ComputeShaderCode
import de.fabmax.kool.pipeline.ShaderCode
import de.fabmax.kool.util.LongHash

data class ShaderCodeGl(
    val vertexSrc: String,
    val fragmentSrc: String
) : ShaderCode {

    override val hash = LongHash {
        this += vertexSrc.hashCode().toLong() shl 32 or fragmentSrc.hashCode().toLong()
    }
}

data class ComputeShaderCodeGl(val computeSrc: String): ComputeShaderCode {
    override val hash = LongHash {
        this += computeSrc
    }
}
