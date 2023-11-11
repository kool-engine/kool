package de.fabmax.kool.pipeline

interface ShaderCode {
    val longHash: Long
}

data class ShaderCodeGl(
    val vertexSrc: String,
    val fragmentSrc: String
) : ShaderCode {

    override val longHash: Long = (vertexSrc.hashCode().toLong() shl 32) + fragmentSrc.hashCode()
}
