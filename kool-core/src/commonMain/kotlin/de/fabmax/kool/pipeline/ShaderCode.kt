package de.fabmax.kool.pipeline

expect class ShaderCode {
    val longHash: ULong
}

expect fun shaderCodeFromSource(vertexShaderSource: String, fraqmentShaderSource: String): ShaderCode
