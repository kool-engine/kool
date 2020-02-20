package de.fabmax.kool.platform

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.CodeGenerator
import de.fabmax.kool.pipeline.shadermodel.ShaderGenerator
import de.fabmax.kool.pipeline.shadermodel.ShaderGraph
import de.fabmax.kool.pipeline.shadermodel.ShaderModel
import de.fabmax.kool.util.logE

class ShaderGeneratorImplVk : ShaderGenerator() {

    private val shaderCodes = mutableMapOf<String, ShaderCode>()

    override fun generateShader(model: ShaderModel, pipeline: Pipeline, ctx: KoolContext): ShaderCode {
        val (vertShader, fragShader) = generateCode(model, pipeline)
        //return shaderCodes.computeIfAbsent(vertShader + fragShader) {  }

        val codeKey = vertShader + fragShader
        var code = shaderCodes[codeKey]
        if (code == null) {
            try {
                code = ShaderCode.codeFromSource(vertShader, fragShader)
                shaderCodes[codeKey] = code

                if (model.dumpCode) {
                    printCode(vertShader, fragShader)
                }

            } catch (e: Exception) {
                logE { "Compilation failed: $e" }
                printCode(vertShader, fragShader)
                throw RuntimeException(e)
            }
        }
        return code
    }

    private fun printCode(vertShader: String, fragShader: String) {
        println("Vertex shader:\n\n")
        vertShader.lines().forEachIndexed { i, l ->
            println(String.format("%3d: %s", i+1, l))
        }
        println("Fragment shader:\n\n")
        fragShader.lines().forEachIndexed { i, l ->
            println(String.format("%3d: %s", i+1, l))
        }
    }

    private fun generateCode(model: ShaderModel, pipeline: Pipeline): Pair<String, String> {
        val vertShader = generateVertexShaderCode(model, pipeline)
        val fragShader = generateFragmentShaderCode(model, pipeline)
        return vertShader to fragShader
    }

    private fun generateVertexShaderCode(model: ShaderModel, pipeline: Pipeline): String {
        val codeGen = CodeGen()
        model.vertexStageGraph.generateCode(codeGen)
        return """
            #version 450
            ${model.infoStr()}
            
            // descriptor layout / uniforms ${generateDescriptorBindings(pipeline, ShaderStage.VERTEX_SHADER)}
            // vertex attributes ${generateAttributeBindings(pipeline)}
            // outputs ${model.vertexStageGraph.generateStageOutputs()}
            // functions
            ${codeGen.generateFunctions()}
            
            void main() {
                ${codeGen.generateMain()}
                gl_Position = ${model.vertexStageGraph.positionOutput.variable.ref4f()};
            }
        """.trimIndent()
    }

    private fun generateFragmentShaderCode(model: ShaderModel, pipeline: Pipeline): String {
        val codeGen = CodeGen()
        model.fragmentStageGraph.generateCode(codeGen)
        return """
            #version 450
            precision highp float;
            ${model.infoStr()}
            
            // descriptor layout / uniforms ${generateDescriptorBindings(pipeline, ShaderStage.FRAGMENT_SHADER)}
            // inputs ${model.fragmentStageGraph.generateStageInputs()}
            // outputs
            layout(location=0) out vec4 fragStage_outColor;
            // functions
            ${codeGen.generateFunctions()}
            
            void main() {
                ${codeGen.generateMain()}
                fragStage_outColor = ${model.fragmentStageGraph.colorOutput.variable.ref4f()};
            }
        """.trimIndent()
    }

    private fun ShaderModel.infoStr(): String {
        return modelInfo.lines().joinToString { "// $it\n"}
    }

    private fun generateDescriptorBindings(pipeline: Pipeline, stage: ShaderStage): String {
        val srcBuilder = StringBuilder("\n")
        pipeline.descriptorSetLayouts.forEach { set ->
            set.descriptors.forEach { desc ->
                if (desc.stages.contains(stage)) {
                    when (desc) {
                        is UniformBuffer -> srcBuilder.append(generateUniformBuffer(set, desc))
                        is TextureSampler -> srcBuilder.append(generateTextureSampler(set, desc))
                        is CubeMapSampler -> srcBuilder.append(generateCubeMapSampler(set, desc))
                        else -> TODO("Descriptor type not implemented: ${desc::class.java.name}")
                    }
                }
            }
        }

        val pushConstants = pipeline.pushConstantRanges.filter { it.stages.contains(stage) }
        if (pushConstants.isNotEmpty()) {
            pipeline.pushConstantRanges.forEach { pcr ->
                srcBuilder.appendln(8, "layout(push_constant) uniform ${pcr.name} {")
                pcr.pushConstants.forEach { u ->
                    srcBuilder.appendln(12, "${u.declare()};")
                }
                srcBuilder.appendln(8, "}${pcr.instanceName ?: ""};")
            }
        }
        return srcBuilder.toString()
    }

    private fun generateUniformBuffer(set: DescriptorSetLayout, desc: UniformBuffer): String {
        val srcBuilder = StringBuilder()
                .appendln(8, "layout(set=${set.set}, binding=${desc.binding}) uniform ${desc.name} {")

        desc.uniforms.forEach { u ->
            srcBuilder.appendln(12, "${u.declare()};")
        }

        srcBuilder.appendln(8, "}${desc.instanceName ?: ""};")
        return srcBuilder.toString()
    }

    private fun generateTextureSampler(set: DescriptorSetLayout, desc: TextureSampler): String {
        val arraySuffix = if (desc.arraySize > 1) { "[${desc.arraySize}]" } else { "" }
        return "layout(set=${set.set}, binding=${desc.binding}) uniform sampler2D ${desc.name}$arraySuffix;\n"
    }

    private fun generateCubeMapSampler(set: DescriptorSetLayout, desc: CubeMapSampler): String {
        val arraySuffix = if (desc.arraySize > 1) { "[${desc.arraySize}]" } else { "" }
        return "layout(set=${set.set}, binding=${desc.binding}) uniform samplerCube ${desc.name}$arraySuffix;\n"
    }

    private fun generateAttributeBindings(pipeline: Pipeline): String {
        val srcBuilder = StringBuilder("\n")
        pipeline.vertexLayout.bindings.forEach { binding ->
            binding.attributes.forEach { attr ->
                srcBuilder.appendln(8, "layout(location=${attr.location}) in ${attr.type.glslType} ${attr.name};")
            }
        }
        return srcBuilder.toString()
    }

    private fun ShaderGraph.generateStageInputs(): String {
        val srcBuilder = StringBuilder("\n")
        inputs.forEach {
            srcBuilder.appendln(8, "layout(location=${it.location}) in ${it.variable.glslType()} ${it.variable.name};")
        }
        return srcBuilder.toString()
    }

    private fun ShaderGraph.generateStageOutputs(): String {
        val srcBuilder = StringBuilder("\n")
        outputs.forEach {
            srcBuilder.appendln(8, "layout(location=${it.location}) out ${it.variable.glslType()} ${it.variable.name};")
        }
        return srcBuilder.toString()
    }

    private fun StringBuilder.appendln(indent: Int, line: String) = append(indent, "$line\n")
    private fun StringBuilder.append(indent: Int, line: String) = append(String.format("%${indent}s%s", "", line))

    private fun Uniform<*>.declare(): String {
        return when (this) {
            is Uniform1f -> "float $name"
            is Uniform2f -> "vec2 $name"
            is Uniform3f -> "vec3 $name"
            is Uniform4f -> "vec4 $name"
            is UniformColor -> "vec4 $name"
            is Uniform1fv -> "float $name[$length]"
            is Uniform2fv -> "vec2 $name[$length]"
            is Uniform3fv -> "vec3 $name[$length]"
            is Uniform4fv -> "vec4 $name[$length]"
            is UniformMat3f -> "mat3 $name"
            is UniformMat4f -> "mat4 $name"
            is UniformMat4fv -> "mat4 $name[$length]"
            is Uniform1i -> "int $name"
            else -> TODO("Uniform type name not implemented: ${this::class.java.name}")
        }
    }

    private class CodeGen : CodeGenerator {
        val functions = mutableMapOf<String, String>()
        val mainCode = mutableListOf<String>()

        override fun appendFunction(name: String, glslCode: String) {
            functions[name] = glslCode
        }

        override fun appendMain(glslCode: String) {
            mainCode += glslCode
        }

        override fun sampleTexture2d(texName: String, texCoords: String, lod: String?): String {
            return if (lod == null) {
                "texture($texName, $texCoords)"
            } else {
                "textureLod($texName, $texCoords, $lod)"
            }
        }

        override fun sampleTextureCube(texName: String, texCoords: String, lod: String?) = sampleTexture2d(texName, texCoords, lod)

        fun generateFunctions(): String = functions.values.joinToString("\n")

        fun generateMain(): String = mainCode.joinToString("\n")
    }
}