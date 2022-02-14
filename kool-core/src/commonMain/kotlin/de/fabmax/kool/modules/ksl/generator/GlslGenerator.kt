package de.fabmax.kool.modules.ksl.generator

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslState

class GlslGenerator : KslGenerator() {

    override fun generateProgram(program: KslProgram): GlslGeneratorOutput {
        program.prepareGenerate()
        return GlslGeneratorOutput(generateVertexSrc(program.vertexStage), generateFragmentSrc(program.fragmentStage))
    }

    override fun constFloatVecExpression(vararg values: KslExpression<KslTypeFloat1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 1 and 4)")
        }
        return "vec${values.size}(${values.joinToString { it.generateExpression(this) }})"
    }

    override fun constIntVecExpression(vararg values: KslExpression<KslTypeInt1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 1 and 4)")
        }
        return "ivec${values.size}(${values.joinToString { it.generateExpression(this) }})"
    }

    override fun constBoolVecExpression(vararg values: KslExpression<KslTypeBool1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 1 and 4)")
        }
        return "bvec${values.size}(${values.joinToString { it.generateExpression(this) }})"
    }

    override fun castExpression(castExpr: KslExpressionCast<*>): String {
        return "${glslTypeName(castExpr.expressionType)}(${castExpr.value.generateExpression(this)})"
    }

    private fun generateVertexSrc(vertexStage: KslVertexStage): String {
        val src = StringBuilder()
        src.appendLine("#version 300 es")
        src.appendLine()

        src.generateUniforms(vertexStage.uniforms)
        src.generateAttributes(vertexStage.attributes.filter { it.inputRate == InputRate.Instance }, "instance attributes")
        src.generateAttributes(vertexStage.attributes.filter { it.inputRate == InputRate.Vertex }, "vertex attributes")
//        generateOutputs(shader)

        src.appendLine("void main() {")
        src.appendLine(generateScope(vertexStage.main, "    "))
        src.appendLine("}")
        return src.toString()
    }

    private fun generateFragmentSrc(fragmentStage: KslFragmentStage): String {
        val src = StringBuilder()
        src.appendLine("#version 300 es")
        src.appendLine("precision highp float;")
        src.appendLine("precision highp sampler2DShadow;")
        src.appendLine()

        src.generateUniforms(fragmentStage.uniforms)
        src.generateOutputs(fragmentStage.outColors)

        src.appendLine("void main() {")
        src.appendLine(generateScope(fragmentStage.main, "    "))
        src.appendLine("}")
        return src.toString()
    }

    private fun StringBuilder.generateUniforms(uniforms: List<KslUniform<*>>) {
        if (uniforms.isNotEmpty()) {
            appendLine("// uniforms")
            for (u in uniforms) {
                val arraySuffix = if (u.value is KslArray<*>) {
                    "[${u.value.arraySize.generateExpression(this@GlslGenerator)}]"
                } else {
                    ""
                }
                appendLine("uniform ${glslTypeName(u.expressionType)} ${u.value.stateName}${arraySuffix};")
            }
            appendLine()
        }
    }

    private fun StringBuilder.generateAttributes(attribs: List<KslVertexAttribute<*>>, info: String) {
        if (attribs.isNotEmpty()) {
            appendLine("// $info")
            attribs.forEach { a ->
                appendLine("layout(location=${a.location}) in ${glslTypeName(a.expressionType)} ${a.value.stateName};")
            }
            appendLine()
        }
    }

    private fun StringBuilder.generateInputs(inputs: List<KslStageInput<*>>) {
        if (inputs.isNotEmpty()) {
            appendLine("// stage inputs")
            inputs.forEach { input ->
                appendLine("in ${glslTypeName(input.expressionType)} ${input.value.stateName};")
            }
            appendLine()
        }
    }

    private fun StringBuilder.generateOutputs(outputs: List<KslStageOutput<*>>) {
        if (outputs.isNotEmpty()) {
            appendLine("// stage outputs")
            outputs.forEach { output ->
                val loc = if (output.location >= 0) "layout(location=${output.location}) " else ""
                appendLine("${loc}out ${glslTypeName(output.expressionType)} ${output.value.stateName};")
            }
            appendLine()
        }
    }

    override fun declareState(state: KslState): String {
        return when (state) {
            is KslVar<*> -> "${glslTypeName(state.expressionType)} ${state.stateName};"
            is KslArray<*> -> "${glslTypeName(state.expressionType.elemType)} ${state.stateName}[${state.arraySize.generateExpression(this)}];"
            else -> throw IllegalArgumentException("unsupported declare state: $state")
        }
    }

    override fun opAssign(op: KslAssign<*>): String {
        return "${op.assignTarget.generateAssignable(this)} = ${op.assignExpression.generateExpression(this)};"
    }

    override fun opIf(op: KslIf): String {
        val txt = StringBuilder("if (${op.condition.generateExpression(this)}) {\n")
        txt.appendLine(generateScope(op.body, "    "))
        txt.append("}")
        op.elseIfs.forEach { elseIf ->
            txt.appendLine(" else if (${elseIf.first.generateExpression(this)}) {")
            txt.appendLine(generateScope(elseIf.second, "    "))
            txt.append("}")
        }
        if (op.elseBody.isNotEmpty()) {
            txt.appendLine(" else {")
            txt.appendLine(generateScope(op.elseBody, "    "))
            txt.append("}")
        }
        return txt.toString()
    }

    private fun glslTypeName(type: KslType): String {
        return when (type) {
            KslTypeVoid -> "void"
            KslTypeBool1 -> "bool"
            KslTypeBool2 -> "bvec2"
            KslTypeBool3 -> "bvec3"
            KslTypeBool4 -> "bvec4"
            KslTypeFloat1 -> "float"
            KslTypeFloat2 -> "vec2"
            KslTypeFloat3 -> "vec3"
            KslTypeFloat4 -> "vec4"
            KslTypeInt1 -> "int"
            KslTypeInt2 -> "ivec2"
            KslTypeInt3 -> "ivec3"
            KslTypeInt4 -> "ivec4"
            KslTypeMat2 -> "mat2"
            KslTypeMat3 -> "mat3"
            KslTypeMat4 -> "mat4"

//            KslTypeSampler1d -> "sampler2D"    // in WebGL2, 1d textures are not supported, simply use 2d instead (with height = 1px)
//            KslTypeSampler2d -> "sampler2D"
//            KslTypeSampler3d -> "sampler3D"
//            KslTypeSamplerCube -> "samplerCube"
//            KslTypeSampler2dArray -> "sampler2DArray"
//            KslTypeSamplerCubeArray -> "samplerCubeArray"
//
//            KslTypeDepthSampler2d -> "sampler2DShadow"
//            KslTypeDepthSamplerCube -> "samplerCubeShadow"
//            KslTypeDepthSampler2dArray -> "sampler2DArrayShadow"
//            KslTypeDepthSamplerCubeArray -> "samplerCubeArrayShadow"

            is KslTypeArray<*> -> glslTypeName(type.elemType)
        }
    }

    class GlslGeneratorOutput(val vertexSrc: String, val fragmentSrc: String) : GeneratorOutput
}