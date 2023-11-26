package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.modules.ksl.lang.KslArray
import de.fabmax.kool.modules.ksl.lang.KslFragmentStage
import de.fabmax.kool.modules.ksl.lang.KslShaderStage
import de.fabmax.kool.modules.ksl.lang.KslVertexStage
import de.fabmax.kool.pipeline.PipelineBase
import de.fabmax.kool.pipeline.backend.gl.GlslGenerator

class KslGlslGeneratorVk(private val pipeline: PipelineBase) : GlslGenerator("#version 450") {

    override fun StringBuilder.generateUniformSamplers(stage: KslShaderStage) {
        val samplers = stage.getUsedSamplers()
        if (samplers.isNotEmpty()) {
            appendLine("// texture samplers")
            for (u in samplers) {
                val (set, desc) = pipeline.findBindGroupItemByName(u.name)!!
                appendLine("layout(set=${set.set}, binding=${desc.binding}) uniform ${glslTypeName(u.expressionType)} ${u.value.name()};")
            }
            appendLine()
        }
    }

    override fun StringBuilder.generateUbos(stage: KslShaderStage) {
        val ubos = stage.getUsedUbos()
        if (ubos.isNotEmpty()) {
            appendLine("// uniform buffer objects")
            for (ubo in ubos) {
                val (set, desc) = pipeline.findBindGroupItemByName(ubo.name)!!
                appendLine("layout(std140, set=${set.set}, binding=${desc.binding}) uniform ${ubo.name} {")
                for (u in ubo.uniforms.values) {
                    appendLine("    highp ${glslTypeName(u.expressionType)} ${u.value.name()};")
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

                appendLine("layout(location=${location}) ${interStage.interpolation.glsl()} out ${glslTypeName(value.expressionType)} ${value.name()};")
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

                appendLine("layout(location=${location}) ${interStage.interpolation.glsl()} in ${glslTypeName(value.expressionType)} ${value.name()};")
                location += locationIncrement
            }
            appendLine()
        }
    }
}