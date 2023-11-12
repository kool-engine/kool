package de.fabmax.kool.pipeline

class ShaderCodeImpl(val vertexSrc: String, val fragmentSrc: String) : ShaderCode {
    override val longHash = (vertexSrc.hashCode().toLong() shl 32) + fragmentSrc.hashCode().toLong()
}