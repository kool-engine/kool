package de.fabmax.kool.pipeline

actual class ShaderCode(val vertexSrc: String, val fragmentSrc: String) {
    actual val longHash = (vertexSrc.hashCode().toULong() shl 32) + fragmentSrc.hashCode().toULong()
}