package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Shader
import de.fabmax.kool.pipeline.ShaderCode
import de.fabmax.kool.scene.Mesh

class CustomShader(val shaderCode: ShaderCode) : Shader() {
    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.shaderCodeGenerator = { shaderCode }
        super.onPipelineSetup(builder, mesh, ctx)
    }
}