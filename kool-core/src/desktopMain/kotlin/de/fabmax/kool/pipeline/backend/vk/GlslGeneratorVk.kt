package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslState
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.gl.GlslGenerator
import de.fabmax.kool.util.MemoryLayout

class GlslGeneratorVk private constructor(generatorExpressions: Map<KslExpression<*>, KslExpression<*>>) : GlslGenerator(
    generatorExpressions,
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
            for (storage in storages) {
                val (set, desc) = pipeline.findBindGroupItemByName(storage.name)!!
                val rw = when ((desc as StorageBufferLayout).accessType) {
                    StorageAccessType.READ_ONLY -> "readonly"
                    StorageAccessType.WRITE_ONLY -> "writeonly"
                    StorageAccessType.READ_WRITE -> ""
                }
                val arrayDim = storage.size?.let { "[$it]" } ?: "[]"

                val type = storage.storageType.elemType
                val layout = if (type is KslStruct<*>) {
                    when (type.proto.layout) {
                        MemoryLayout.Std140 -> "std140"
                        MemoryLayout.Std430 -> "std430"
                        else -> error("layout of struct ${type.proto.structName} is ${type.proto.layout} but storage buffers only support std430 and std140")
                    }
                } else "std430"

                appendLine("""
                    layout($layout, set=${set.group}, binding=${desc.bindingIndex}) $rw buffer ssboLayout_${storage.name} {
                        ${glslTypeName(storage.storageType.elemType)} ${storage.name}$arrayDim;
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
        val uboStructs = stage.getUsedUboStructs().sortedBy { it.scope.ordinal }
        if (uboStructs.isNotEmpty()) {
            appendLine("// uniform structs")
            for (ubo in uboStructs) {
                val (set, desc) = pipeline.getBindGroupItemByName(ubo.name)
                appendLine("""
                    layout(std140, set=${set.group}, binding=${desc.bindingIndex}) uniform ${ubo.name}_ubo {
                        ${glslTypeName(ubo.expressionType)} ${ubo.name};
                    };
                    """.trimIndent()
                )
            }
        }

        val ubos = stage.getUsedUbos()
        if (ubos.isNotEmpty()) {
            appendLine("// uniform buffer objects")
            for (ubo in ubos) {
                val (set, desc) = pipeline.getBindGroupItemByName(ubo.name)
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

    companion object {
        fun generateProgram(program: KslProgram, pipeline: DrawPipeline): GlslGeneratorOutput {
            val vertexStage = checkNotNull(program.vertexStage) {
                "KslProgram vertexStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
            }
            val fragmentStage = checkNotNull(program.fragmentStage) {
                "KslProgram fragmentStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
            }
            val generatorExpressions = vertexStage.generatorExpressions + fragmentStage.generatorExpressions
            val generator = GlslGeneratorVk(generatorExpressions)
            return generator.generateProgram(program, pipeline)
        }

        fun generateComputeProgram(program: KslProgram, pipeline: ComputePipeline): GlslGeneratorOutput {
            val computeStage = checkNotNull(program.computeStage) {
                "KslProgram computeStage is missing"
            }
            val generator = GlslGeneratorVk(computeStage.generatorExpressions)
            return generator.generateComputeProgram(program, pipeline)
        }
    }
}