package de.fabmax.kool.pipeline

import de.fabmax.kool.util.LongHash

interface ShaderCode {
    val hash: LongHash
}

data class ShaderCodeGl(
    val vertexSrc: String,
    val fragmentSrc: String
) : ShaderCode {

    override val hash = LongHash().apply {
        this += vertexSrc
        this += fragmentSrc
    }
}
