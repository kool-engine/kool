package de.fabmax.kool.platform

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*

class ShaderGeneratorImplVk : ShaderGenerator() {

    private val shaderCodes = mutableMapOf<String, ShaderCode>()

    override fun generateShader(model: ShaderModel, pipeline: Pipeline, ctx: KoolContext): ShaderCode {
        val (vertShader, fragShader) = generateCode(model, pipeline, ctx)
        //return shaderCodes.computeIfAbsent(vertShader + fragShader) {  }

        val codeKey = vertShader + fragShader
        var code = shaderCodes[codeKey]
        if (code == null) {
            println("Vertex shader:\n\n$vertShader\n\n")
            println("Fragment shader:\n\n$fragShader\n\n")
            code = ShaderCode.codeFromSource(vertShader, fragShader)
            shaderCodes[codeKey] = code
        }
        return code
    }

    private fun generateCode(model: ShaderModel, pipeline: Pipeline, ctx: KoolContext): Pair<String, String> {
        val vertShader = generateVertexShaderCode(model.vertexStage, pipeline, ctx)
        val fragShader = generateFragmentrShaderCode(model.fragmentStage, pipeline, ctx)
        return vertShader to fragShader
    }

    private fun generateVertexShaderCode(vertGraph: VertexShaderGraph, pipeline: Pipeline, ctx: KoolContext): String {
        val codeGen = CodeGen()
        vertGraph.generateCode(codeGen, pipeline, ctx)
        return """
            #version 450
            
            // descriptor layout / uniforms ${generateDescriptorBindings(pipeline, ShaderStage.VERTEX_SHADER)}
            // vertex attributes ${generateAttributeBindings(pipeline)}
            // outputs ${vertGraph.generateStageOutputs()}
            // functions
            ${codeGen.generateFunctions()}
            
            void main() {
                ${codeGen.generateMain()}
                gl_Position = ${vertGraph.positionOutput.variable.ref4f()};
            }
        """.trimIndent()
    }

    private fun generateFragmentrShaderCode(fragGraph: FragmentShaderGraph, pipeline: Pipeline, ctx: KoolContext): String {
        val codeGen = CodeGen()
        fragGraph.generateCode(codeGen, pipeline, ctx)
        return """
            #version 450
            
            // descriptor layout / uniforms ${generateDescriptorBindings(pipeline, ShaderStage.FRAGMENT_SHADER)}
            // inputs ${fragGraph.generateStageInputs()}
            // outputs
            layout(location=0) out vec4 fragStage_outColor;
            // functions
            ${codeGen.generateFunctions()}
            
            void main() {
                ${codeGen.generateMain()}
                fragStage_outColor = ${fragGraph.colorOutput.variable.ref4f()};
            }
        """.trimIndent()
    }

    private fun generateDescriptorBindings(pipeline: Pipeline, stage: ShaderStage): String {
        val srcBuilder = StringBuilder("\n")
        pipeline.descriptorSetLayouts.forEach { set ->
            set.descriptors.forEach { desc ->
                if (desc.stages.contains(stage)) {
                    when (desc) {
                        is UniformBuffer -> srcBuilder.append(generateUniformBuffer(set, desc))
                        is TextureSampler ->srcBuilder.append(generateTextureSampler(set, desc))
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
                    srcBuilder.appendln(12, "${u.type()} ${u.name};")
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
            srcBuilder.appendln(12, "${u.type()} ${u.name};")
        }

        srcBuilder.appendln(8, "}${desc.instanceName ?: ""};")
        return srcBuilder.toString()
    }

    private fun generateTextureSampler(set: DescriptorSetLayout, desc: TextureSampler): String {
        return "layout(set=${set.set}, binding=${desc.binding}) uniform sampler2D ${desc.name};\n"
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

    private fun Uniform<*>.type(): String {
        return when (this) {
            is Uniform1f -> "float"
            is Uniform2f -> "vec2"
            is Uniform3f -> "vec3"
            is Uniform4f -> "vec4"
            is UniformMat3f -> "mat3"
            is UniformMat4f -> "mat4"
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

        override fun sampleTexture2d(texName: String, texCoords: String) = "texture($texName, $texCoords)"

        fun generateFunctions(): String = functions.values.joinToString("\n")

        fun generateMain(): String = mainCode.joinToString("\n")
    }
}