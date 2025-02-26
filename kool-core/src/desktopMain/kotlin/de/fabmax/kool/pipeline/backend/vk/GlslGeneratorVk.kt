package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslState
import de.fabmax.kool.pipeline.PipelineBase
import de.fabmax.kool.pipeline.StorageAccessType
import de.fabmax.kool.pipeline.StorageTextureLayout
import de.fabmax.kool.pipeline.backend.gl.GlslGenerator

class GlslGeneratorVk : GlslGenerator(
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
                val (set, desc) = pipeline.findBindGroupItemByName(u.name)!!
                appendLine("layout(set=${set.group}, binding=${desc.bindingIndex}) uniform ${glslTypeName(u.expressionType)} ${getStateName(u.value)};")
            }
            appendLine()
        }
    }

    override fun StringBuilder.generateStorageBuffers(stage: KslShaderStage, pipeline: PipelineBase) {
        val storages = stage.getUsedStorage()
        if (storages.isNotEmpty()) {
            appendLine("// storage buffers")
            val readonly = if (stage.type == KslShaderStageType.ComputeShader) "" else "readonly"
            for (storage in storages) {
                val (set, desc) = pipeline.findBindGroupItemByName(storage.name)!!
                appendLine("""
                    layout(std430, set=${set.group}, binding=${desc.bindingIndex}) $readonly buffer ssboLayout_${storage.name} {
                        ${glslTypeName(storage.storageType.elemType)} ${storage.name}[];
                    };
                """.trimIndent())
            }
            appendLine()
        }
    }

    override fun StringBuilder.generateStorageTextures(stage: KslShaderStage, pipeline: PipelineBase) {
        val storages = stage.getUsedStorageTextures()
        if (storages.isNotEmpty()) {
            appendLine("// storage textures")
            for (storage in storages) {
                val (set, desc) = pipeline.findBindGroupItemByName(storage.name)!!
                val layout = "${storageTextureFormatQualifier(storage.texFormat)}, set=${set.group}, binding=${desc.bindingIndex}"
                val access = when ((desc as StorageTextureLayout).accessType) {
                    StorageAccessType.READ_ONLY -> "readonly"
                    StorageAccessType.WRITE_ONLY -> "writeonly"
                    StorageAccessType.READ_WRITE -> ""
                }
                appendLine("layout($layout) uniform restrict $access ${glslTypeName(storage.expressionType)} ${storage.name};")
            }
            appendLine()
        }
    }

    override fun StringBuilder.generateUbos(stage: KslShaderStage, pipeline: PipelineBase) {
        val ubos = stage.getUsedUbos()
        if (ubos.isNotEmpty()) {
            appendLine("// uniform buffer objects")
            for (ubo in ubos) {
                val (set, desc) = pipeline.findBindGroupItemByName(ubo.name)!!
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