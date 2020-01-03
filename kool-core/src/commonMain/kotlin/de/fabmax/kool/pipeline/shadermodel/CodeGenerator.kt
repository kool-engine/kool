package de.fabmax.kool.pipeline.shadermodel

interface CodeGenerator {

    fun sampleTexture2d(texName: String, texCoords: String): String

    fun sampleTextureCube(texName: String, texCoords: String): String

    fun appendFunction(name: String, glslCode: String)

    fun appendMain(glslCode: String)

}