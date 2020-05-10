package de.fabmax.kool.platform.webgl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.CodeGenerator
import de.fabmax.kool.pipeline.shadermodel.ShaderGenerator
import de.fabmax.kool.pipeline.shadermodel.ShaderGraph
import de.fabmax.kool.pipeline.shadermodel.ShaderModel

class ShaderGeneratorImplWebGl : ShaderGenerator() {
    override fun generateShader(model: ShaderModel, pipeline: Pipeline, ctx: KoolContext): ShaderCode {
        val (vs, fs) = generateCode(model, pipeline)
        return ShaderCode(vs, fs)
    }

    private fun generateCode(model: ShaderModel, pipeline: Pipeline): Pair<String, String> {
        val vertShader = generateVertexShaderCode(model, pipeline)
        val fragShader = generateFragmentShaderCode(model, pipeline)

        if (model.dumpCode) {
            println("Vertex shader:\n$vertShader")
            println("Fragment shader:\n$fragShader")
        }

        return vertShader to fragShader
    }

    private fun generateVertexShaderCode(model: ShaderModel, pipeline: Pipeline): String {
        val codeGen = CodeGen()
        model.vertexStageGraph.generateCode(codeGen)
        return """
            #version 300 es
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
            #version 300 es
            precision highp float;
            precision highp sampler2DShadow;
            ${model.infoStr()}

            // descriptor layout / uniforms ${generateDescriptorBindings(pipeline, ShaderStage.FRAGMENT_SHADER)}
            // inputs ${model.fragmentStageGraph.generateStageInputs()}
            // outputs
            out vec4 fragColor;
            // functions
            ${codeGen.generateFunctions()}
            
            void main() {
                ${codeGen.generateMain()}
                fragColor = ${model.fragmentStageGraph.colorOutput.variable.ref4f()};
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
                        is UniformBuffer -> srcBuilder.append(generateUniformBuffer(desc))
                        is TextureSampler -> srcBuilder.append(generateTextureSampler(desc))
                        is CubeMapSampler -> srcBuilder.append(generateCubeMapSampler(desc))
                        else -> TODO("Descriptor type not implemented: $desc")
                    }
                }
            }
        }

        // WebGL doesn't have an equivalent for push constants, generate standard uniforms instead
        val pushConstants = pipeline.pushConstantRanges.filter { it.stages.contains(stage) }
        if (pushConstants.isNotEmpty()) {
            pipeline.pushConstantRanges.forEach { pcr ->
                pcr.pushConstants.forEach { u ->
                    srcBuilder.appendln("uniform ${u.declare()};")
                }
            }
        }
        return srcBuilder.toString()
    }

    private fun generateUniformBuffer(desc: UniformBuffer): String {
        // fixme: implement support for UBOs (supported by WebGL2), for now individual uniforms are used
        val srcBuilder = StringBuilder()
        desc.uniforms.forEach { u ->
            srcBuilder.appendln("uniform ${u.declare()};")
        }
        return srcBuilder.toString()
    }

    private fun generateTextureSampler(desc: TextureSampler): String {
        val samplerType = if (desc.isDepthSampler) "sampler2DShadow" else "sampler2D"
        val arraySuffix = if (desc.arraySize > 1) { "[${desc.arraySize}]" } else { "" }
        return "uniform $samplerType ${desc.name}$arraySuffix;\n"
    }

    private fun generateCubeMapSampler(desc: CubeMapSampler): String {
        val samplerType = if (desc.isDepthSampler) "samplerCubeShadow" else "samplerCube"
        val arraySuffix = if (desc.arraySize > 1) { "[${desc.arraySize}]" } else { "" }
        return "uniform $samplerType ${desc.name}$arraySuffix;\n"
    }

    private fun generateAttributeBindings(pipeline: Pipeline): String {
        val srcBuilder = StringBuilder("\n")
        pipeline.vertexLayout.bindings.forEach { binding ->
            binding.vertexAttributes.forEach { attr ->
                srcBuilder.appendln("layout(location=${attr.location}) in ${attr.type.glslType} ${attr.name};")
            }
        }
        return srcBuilder.toString()
    }

    private fun ShaderGraph.generateStageInputs(): String {
        val srcBuilder = StringBuilder("\n")
        inputs.forEach {
            srcBuilder.appendln("in ${it.variable.glslType()} ${it.variable.name};")
        }
        return srcBuilder.toString()
    }

    private fun ShaderGraph.generateStageOutputs(): String {
        val srcBuilder = StringBuilder("\n")
        outputs.forEach {
            srcBuilder.appendln("out ${it.variable.glslType()} ${it.variable.name};")
        }
        return srcBuilder.toString()
    }

    private fun StringBuilder.appendln(line: String) = append("$line\n")

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
            else -> TODO("Uniform type name not implemented: $this")
        }
    }

    private class CodeGen : CodeGenerator {
        val functions = mutableMapOf<String, String>()
        val mainCode = mutableListOf<String>()

        override val clipSpaceOrientation = CodeGenerator.ClipSpaceOrientation.Y_UP

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

        override fun sampleTexture2dDepth(texName: String, texCoords: String): String {
            return "textureProj($texName, $texCoords)"
        }

        override fun sampleTextureCube(texName: String, texCoords: String, lod: String?) =
                sampleTexture2d(texName, texCoords, lod)

        fun generateFunctions(): String = functions.values.joinToString("\n")

        fun generateMain(): String = mainCode.joinToString("\n")
    }
}
