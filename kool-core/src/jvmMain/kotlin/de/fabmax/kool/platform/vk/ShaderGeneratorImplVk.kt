package de.fabmax.kool.platform.vk

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.CodeGenerator
import de.fabmax.kool.pipeline.shadermodel.ShaderGenerator
import de.fabmax.kool.pipeline.shadermodel.ShaderGraph
import de.fabmax.kool.pipeline.shadermodel.ShaderModel
import de.fabmax.kool.util.logE

class ShaderGeneratorImplVk : ShaderGenerator() {

    private val shaderCodes = mutableMapOf<String, ShaderCode>()

    private fun compile(vertexShaderSrc: String, fragmentShaderSrc: String): ShaderCode {
        val codeKey = vertexShaderSrc + fragmentShaderSrc
        var code = shaderCodes[codeKey]
        if (code == null) {
            try {
                code = ShaderCode.vkCodeFromSource(vertexShaderSrc, fragmentShaderSrc)
                shaderCodes[codeKey] = code

            } catch (e: Exception) {
                logE { "Compilation failed: $e" }
                printCode(vertexShaderSrc, fragmentShaderSrc)
                throw RuntimeException(e)
            }
        }
        return code
    }

    fun generateKslShader(shader: KslShader, pipelineLayout: Pipeline.Layout): ShaderCode {
        val src = KslGlslGeneratorVk(pipelineLayout).generateProgram(shader.program)
        if (shader.program.dumpCode) {
            src.dump()
        }
        return compile(src.vertexSrc, src.fragmentSrc)
    }

    override fun generateShader(model: ShaderModel, pipelineLayout: Pipeline.Layout, ctx: KoolContext): ShaderCode {
        val (vertShader, fragShader) = generateCode(model, pipelineLayout)
        if (model.dumpCode) {
            printCode(vertShader, fragShader)
        }
        return compile(vertShader, fragShader)
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

    private fun generateCode(model: ShaderModel, pipelineLayout: Pipeline.Layout): Pair<String, String> {
        val vertShader = generateVertexShaderCode(model, pipelineLayout)
        val fragShader = generateFragmentShaderCode(model, pipelineLayout)
        return vertShader to fragShader
    }

    private fun generateVertexShaderCode(model: ShaderModel, pipelineLayout: Pipeline.Layout): String {
        val codeGen = CodeGen()
        model.vertexStageGraph.generateCode(codeGen)
        return """
            #version 450
            ${model.infoStr()}
            
            // descriptor layout / uniforms ${generateDescriptorBindings(pipelineLayout, ShaderStage.VERTEX_SHADER)}
            // vertex attributes ${generateAttributeBindings(pipelineLayout)}
            // outputs ${model.vertexStageGraph.generateStageOutputs()}
            // functions
            ${codeGen.generateFunctions()}
            
            void main() {
                ${codeGen.generateMain()}
                gl_Position = ${model.vertexStageGraph.positionOutput.variable.ref4f()};
            }
        """.trimIndent()
    }

    private fun generateFragmentShaderCode(model: ShaderModel, pipelineLayout: Pipeline.Layout): String {
        val codeGen = CodeGen()
        model.fragmentStageGraph.generateCode(codeGen)

        return """
            #version 450
            precision highp float;
            ${model.infoStr()}
            
            // descriptor layout / uniforms ${generateDescriptorBindings(pipelineLayout, ShaderStage.FRAGMENT_SHADER)}
            // inputs ${model.fragmentStageGraph.generateStageInputs()}
            // functions
            ${codeGen.generateFunctions()}

            void main() {
                ${codeGen.generateMain()}
            }
        """.trimIndent()
    }

    private fun ShaderModel.infoStr(): String {
        return modelName.lines().joinToString { "// $it\n"}
    }

    private fun generateDescriptorBindings(pipelineLayout: Pipeline.Layout, stage: ShaderStage): String {
        val srcBuilder = StringBuilder("\n")
        pipelineLayout.descriptorSets.forEach { set ->
            set.descriptors.forEach { desc ->
                if (desc.stages.contains(stage)) {
                    when (desc) {
                        is UniformBuffer -> srcBuilder.append(generateUniformBuffer(set, desc))
                        is TextureSampler1d -> srcBuilder.append(generateTextureSampler1d(set, desc))
                        is TextureSampler2d -> srcBuilder.append(generateTextureSampler2d(set, desc))
                        is TextureSampler3d -> srcBuilder.append(generateTextureSampler3d(set, desc))
                        is TextureSamplerCube -> srcBuilder.append(generateCubeMapSampler(set, desc))
                    }
                }
            }
        }

        var offset = 0
        pipelineLayout.pushConstantRanges.forEach { pcr ->
            if (pcr.stages.contains(stage)) {
                srcBuilder.appendln(8, "layout(push_constant) uniform ${pcr.name} {")
                pcr.pushConstants.forEach { u ->
                    srcBuilder.appendln(12, "${u.declare()};")
                }
                srcBuilder.appendln(8, "}${pcr.instanceName ?: ""};")
            }
            offset += pcr.size
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

    private fun generateTextureSampler1d(set: DescriptorSetLayout, desc: TextureSampler1d): String {
        val arraySuffix = if (desc.arraySize > 1) { "[${desc.arraySize}]" } else { "" }
        return "layout(set=${set.set}, binding=${desc.binding}) uniform sampler2D ${desc.name}$arraySuffix;\n"
    }

    private fun generateTextureSampler2d(set: DescriptorSetLayout, desc: TextureSampler2d): String {
        val samplerType = if (desc.isDepthSampler) "sampler2DShadow" else "sampler2D"
        val arraySuffix = if (desc.arraySize > 1) { "[${desc.arraySize}]" } else { "" }
        return "layout(set=${set.set}, binding=${desc.binding}) uniform $samplerType ${desc.name}$arraySuffix;\n"
    }

    private fun generateTextureSampler3d(set: DescriptorSetLayout, desc: TextureSampler3d): String {
        val arraySuffix = if (desc.arraySize > 1) { "[${desc.arraySize}]" } else { "" }
        return "layout(set=${set.set}, binding=${desc.binding}) uniform sampler3D ${desc.name}$arraySuffix;\n"
    }

    private fun generateCubeMapSampler(set: DescriptorSetLayout, desc: TextureSamplerCube): String {
        val samplerType = if (desc.isDepthSampler) "samplerCubeShadow" else "samplerCube"
        val arraySuffix = if (desc.arraySize > 1) { "[${desc.arraySize}]" } else { "" }
        return "layout(set=${set.set}, binding=${desc.binding}) uniform $samplerType ${desc.name}$arraySuffix;\n"
    }

    private fun generateAttributeBindings(pipelineLayout: Pipeline.Layout): String {
        val srcBuilder = StringBuilder("\n")
        pipelineLayout.vertices.bindings.forEach { binding ->
            binding.vertexAttributes.forEach { attr ->
                srcBuilder.appendln(8, "layout(location=${attr.location}) in ${attr.type.glslType} ${attr.name};")
            }
        }
        return srcBuilder.toString()
    }

    private fun ShaderGraph.generateStageInputs(): String {
        val srcBuilder = StringBuilder("\n")
        inputs.forEach {
            val flat = if (it.isFlat) "flat" else ""
            srcBuilder.appendln(8, "layout(location=${it.location}) $flat in ${it.variable.glslType()} ${it.variable.name};")
        }
        return srcBuilder.toString()
    }

    private fun ShaderGraph.generateStageOutputs(): String {
        val srcBuilder = StringBuilder("\n")
        outputs.forEach {
            val flat = if (it.isFlat) "flat" else ""
            srcBuilder.appendln(8, "layout(location=${it.location}) $flat out ${it.variable.glslType()} ${it.variable.name};")
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
            is UniformMat3fv -> "mat3 $name[$length"
            is UniformMat4f -> "mat4 $name"
            is UniformMat4fv -> "mat4 $name[$length]"
            is Uniform1i -> "int $name"
            is Uniform2i -> "ivec2 $name"
            is Uniform3i -> "ivec3 $name"
            is Uniform4i -> "ivec4 $name"
            is Uniform1iv -> "int $name[$length]"
            is Uniform2iv -> "ivec2 $name[$length]"
            is Uniform3iv -> "ivec3 $name[$length]"
            is Uniform4iv -> "ivec4 $name[$length]"
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

        override fun sampleTexture1d(texName: String, texCoords: String, lod: String?) =
                sampleTexture(texName, "vec2($texCoords, 0.0)", lod)

        override fun sampleTexture2d(texName: String, texCoords: String, lod: String?) =
                sampleTexture(texName, texCoords, lod)

        override fun sampleTexture2dDepth(texName: String, texCoords: String): String {
            return "textureProj($texName, $texCoords).x"
        }

        override fun sampleTexture3d(texName: String, texCoords: String, lod: String?) =
                sampleTexture(texName, texCoords, lod)

        override fun sampleTextureCube(texName: String, texCoords: String, lod: String?) =
                sampleTexture(texName, texCoords, lod)

        fun generateFunctions(): String = functions.values.joinToString("\n")

        fun generateMain(): String = mainCode.joinToString("\n")

        private fun sampleTexture(texName: String, texCoords: String, lod: String?): String {
            return if (lod == null) {
                "texture($texName, $texCoords)"
            } else {
                "textureLod($texName, $texCoords, $lod)"
            }
        }
    }
}