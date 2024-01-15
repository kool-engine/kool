package de.fabmax.kool.pipeline

import de.fabmax.kool.util.LongHash

interface ShaderCode {
    val hash: LongHash
}

interface ComputeShaderCode : ShaderCode
