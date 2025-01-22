package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.modules.ksl.lang.KslArray
import de.fabmax.kool.modules.ksl.lang.KslFragmentStage
import de.fabmax.kool.modules.ksl.lang.KslShaderStage
import de.fabmax.kool.modules.ksl.lang.KslVertexStage
import de.fabmax.kool.modules.ksl.model.KslState
import de.fabmax.kool.pipeline.BindGroupScope
import de.fabmax.kool.pipeline.PipelineBase
import de.fabmax.kool.pipeline.backend.gl.GlslGenerator

class KslGlslGeneratorVk : GlslGenerator(
    Hints(
        glslVersionStr = "#version 450",
        compat1dSampler = false
    )
) {

    override fun StringBuilder.generateUniformSamplers(stage: KslShaderStage, pipeline: PipelineBase) {
        val samplers = stage.getUsedSamplers()
        if (samplers.isNotEmpty()) {
            appendLine("// texture samplers")
            for (u in samplers) {
                val set = pipeline.bindGroupLayouts[BindGroupScope.PIPELINE]
                val desc = pipeline.findBindGroupItemByName(u.name)!!
                appendLine("layout(set=${set.group}, binding=${desc.bindingIndex}) uniform ${glslTypeName(u.expressionType)} ${getStateName(u.value)};")
            }
            appendLine()
        }
    }

    override fun StringBuilder.generateUbos(stage: KslShaderStage, pipeline: PipelineBase) {
        val ubos = stage.getUsedUbos()
        if (ubos.isNotEmpty()) {
            appendLine("// uniform buffer objects")
            for (ubo in ubos) {
                val set = pipeline.bindGroupLayouts[ubo.scope]
                val desc = pipeline.findBindGroupItemByName(ubo.name)!!
                appendLine("layout(std140, set=${set.group}, binding=${desc.bindingIndex}) uniform ${ubo.name} {")
                for (u in ubo.uniforms.values) {
                    appendLine("    highp ${glslTypeName(u.expressionType)} ${getStateName(u.value)};")
                }
                appendLine("};")
            }
            appendLine()
        }
    }

    override fun StringBuilder.generateInterStageOutputs(vertexStage: KslVertexStage) {
        if (vertexStage.interStageVars.isNotEmpty()) {
            appendLine("// custom vertex stage outputs")
            var location = 0
            vertexStage.interStageVars.forEach { interStage ->
                val value = interStage.input
                val locationIncrement: Int = if (value is KslArray<*>) value.arraySize else 1

                appendLine("layout(location=${location}) ${interStage.interpolation.glsl()} out ${glslTypeName(value.expressionType)} ${getStateName(value)};")
                location += locationIncrement
            }
            appendLine()
        }
    }

    override fun StringBuilder.generateInterStageInputs(fragmentStage: KslFragmentStage) {
        if (fragmentStage.interStageVars.isNotEmpty()) {
            appendLine("// custom fragment stage inputs")
            var location = 0
            fragmentStage.interStageVars.forEach { interStage ->
                val value = interStage.input
                val locationIncrement: Int = if (value is KslArray<*>) value.arraySize else 1

                appendLine("layout(location=${location}) ${interStage.interpolation.glsl()} in ${glslTypeName(value.expressionType)} ${getStateName(value)};")
                location += locationIncrement
            }
            appendLine()
        }
    }

    override fun getStateName(state: KslState): String {
        return when (state.stateName) {
            KslVertexStage.NAME_IN_VERTEX_INDEX -> "gl_VertexIndex"
            KslVertexStage.NAME_IN_INSTANCE_INDEX -> "gl_InstanceIndex"
            else -> super.getStateName(state)
        }
    }
}