package de.fabmax.kool.platform

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.ShaderCode
import de.fabmax.kool.pipeline.shadermodel.BaseAlbedo
import de.fabmax.kool.pipeline.shadermodel.ShaderGenerator
import de.fabmax.kool.pipeline.shadermodel.ShaderModel

class ShaderGeneratorImplVk : ShaderGenerator() {

    private val shaderCodes = mutableMapOf<BaseAlbedo, ShaderCode>()

    override fun generateShader(model: ShaderModel, ctx: KoolContext): ShaderCode {
        // fixme: as a placeholder load shaders from resources...
        return shaderCodes.computeIfAbsent(model.baseAlbedo) {
            when (it) {
                BaseAlbedo.MASKED -> ShaderCode.codeFromResources("masked.vert", "masked.frag")
                BaseAlbedo.VERTEX -> ShaderCode.codeFromResources("colorShader.vert", "colorShader.frag")
                BaseAlbedo.STATIC -> ShaderCode.codeFromResources("staticCol.vert", "staticCol.frag")
                BaseAlbedo.TEXTURE -> ShaderCode.codeFromResources("tex.vert", "tex.frag")
                else -> TODO("not implemented")
            }
        }
    }
}