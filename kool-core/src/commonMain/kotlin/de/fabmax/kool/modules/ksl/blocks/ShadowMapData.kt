package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.modules.ksl.lang.KslUniformBuffer
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.UniformMat4f
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.util.SimpleShadowMap

class ShadowMapData(val shadowMap: SimpleShadowMap, program: KslProgram) : KslUniformBuffer(), KslShader.KslShaderListener {

    val shadowMapViewProj = program.uniformMat4("uShadowMapViewProj")
    val depthMap = program.depthTexture2d("tDepthMap")

    private lateinit var uShadowMapViewProj: UniformMat4f

    override fun onShaderCreated(shader: KslShader, pipeline: Pipeline, ctx: KoolContext) {
        uShadowMapViewProj = shader.uniforms["uShadowMapViewProj"] as UniformMat4f
        shader.texSamplers2d["tDepthMap"]!!.texture = shadowMap.depthTexture
    }

    override fun onUpdate(cmd: DrawCommand) {
        uShadowMapViewProj.value.set(shadowMap.lightViewProjMat)
    }

}