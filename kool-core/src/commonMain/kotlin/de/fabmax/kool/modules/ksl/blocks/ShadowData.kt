package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.modules.ksl.lang.KslUniformBuffer
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.UniformMat4fv
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.util.SimpleShadowMap

class ShadowData(val shadowCfg: ShadowConfig, program: KslProgram) : KslUniformBuffer(), KslShader.KslShaderListener {

    // If shadowCfg is empty, uniforms are created with array size 0, which is kind of invalid. However, they are
    // also not referenced later on and therefore removed before shader is generated (again because shadowCfg is empty)
    val shadowMapViewProjMats = program.uniformMat4Array(UNIFORM_NAME_SHADOW_VP_MATS, shadowCfg.numShadowMaps)
    val depthMaps = program.depthTextureArray2d(SAMPLER_NAME_SHADOW_MAPS, shadowCfg.numShadowMaps)

    private var uShadowMapViewProjMats: UniformMat4fv? = null

    override fun onShaderCreated(shader: KslShader, pipeline: Pipeline, ctx: KoolContext) {
        uShadowMapViewProjMats = shader.uniforms[UNIFORM_NAME_SHADOW_VP_MATS] as? UniformMat4fv

        val sampler = shader.texSamplers2d[SAMPLER_NAME_SHADOW_MAPS]
        for (i in shadowCfg.shadowMaps.indices) {
            val shadowMap = shadowCfg.shadowMaps[i].shadowMap
            if (shadowMap is SimpleShadowMap) {
                sampler?.let { it.textures[i] = shadowMap.depthTexture }
            }
            // todo: else if shadowMap is CascadedShadowMap
        }
    }

    override fun onUpdate(cmd: DrawCommand) {
        uShadowMapViewProjMats?.let { mats ->
            for (i in shadowCfg.shadowMaps.indices) {
                val shadowMap = shadowCfg.shadowMaps[i].shadowMap
                if (shadowMap is SimpleShadowMap) {
                    mats.value[i].set(shadowMap.lightViewProjMat)
                }
                // todo: else if shadowMap is CascadedShadowMap
            }
        }
    }

    companion object {
        const val UNIFORM_NAME_SHADOW_VP_MATS = "uShadowMapViewProjMats"
        const val SAMPLER_NAME_SHADOW_MAPS = "tDepthMaps"
    }
}