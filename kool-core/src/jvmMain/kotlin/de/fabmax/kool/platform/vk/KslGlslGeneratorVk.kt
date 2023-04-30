package de.fabmax.kool.platform.vk

import de.fabmax.kool.modules.ksl.generator.GlslGenerator
import de.fabmax.kool.modules.ksl.lang.KslArray
import de.fabmax.kool.modules.ksl.lang.KslFragmentStage
import de.fabmax.kool.modules.ksl.lang.KslShaderStage
import de.fabmax.kool.modules.ksl.lang.KslVertexStage
import de.fabmax.kool.pipeline.Pipeline

class KslGlslGeneratorVk(private val pipelineLayout: Pipeline.Layout) : GlslGenerator() {

    init {
        glslVersionStr = "#version 450"
    }

    override fun StringBuilder.generateUniformSamplers(stage: KslShaderStage) {
        val samplers = stage.getUsedSamplers()
        if (samplers.isNotEmpty()) {
            appendLine("// texture samplers")
            for (u in samplers) {
                val (set, desc) = pipelineLayout.findDescriptorByName(u.name)!!
                val arraySuffix = if (u.value is KslArray<*>) { "[${u.arraySize}]" } else { "" }
                appendLine("layout(set=${set.set}, binding=${desc.binding}) uniform ${glslTypeName(u.expressionType)} ${u.value.name()}${arraySuffix};")
            }
            appendLine()
        }
    }

    override fun StringBuilder.generateUbos(stage: KslShaderStage) {
        val ubos = stage.getUsedUbos()
        if (ubos.isNotEmpty()) {
            appendLine("// uniform buffer objects")
            for (ubo in ubos) {
                val (set, desc) = pipelineLayout.findDescriptorByName(ubo.name)!!
                appendLine("layout(std140, set=${set.set}, binding=${desc.binding}) uniform ${ubo.name} {")
                for (u in ubo.uniforms.values) {
                    val arraySuffix = if (u.value is KslArray<*>) { "[${u.arraySize}]" } else { "" }
                    appendLine("    highp ${glslTypeName(u.expressionType)} ${u.value.name()}${arraySuffix};")
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
                val arraySuffix: String
                val locationIncrement: Int
                if (value is KslArray<*>) {
                    arraySuffix = "[${value.arraySize}]"
                    locationIncrement = value.arraySize
                } else {
                    arraySuffix = ""
                    locationIncrement = 1
                }

                appendLine("layout(location=${location}) ${interStage.interpolation.glsl()} out ${glslTypeName(value.expressionType)} ${value.name()}${arraySuffix};")
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
                val arraySuffix: String
                val locationIncrement: Int
                if (value is KslArray<*>) {
                    arraySuffix = "[${value.arraySize}]"
                    locationIncrement = value.arraySize
                } else {
                    arraySuffix = ""
                    locationIncrement = 1
                }

                appendLine("layout(location=${location}) ${interStage.interpolation.glsl()} in ${glslTypeName(value.expressionType)} ${value.name()}${arraySuffix};")
                location += locationIncrement
            }
            appendLine()
        }
    }
}