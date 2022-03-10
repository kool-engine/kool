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

    override fun sampleColorTexture(sampleTexture: KslSampleColorTexture<*>): String {
        return "texture(${sampleTexture.sampler.generateExpression(this)}, ${sampleTexture.coord.generateExpression(this)})"
    }

    private fun generateVertexSrc(vertexStage: KslVertexStage): String {
        val src = StringBuilder()
        src.appendLine("""
            #version 300 es
            
            /* 
             * ${vertexStage.program.name} - generated vertex shader
             */ 
        """.trimIndent())
        src.appendLine()

        src.generateUniforms(vertexStage.uniforms)
        src.generateAttributes(vertexStage.attributes.values.filter { it.inputRate == KslInputRate.Instance }, "instance attributes")
        src.generateAttributes(vertexStage.attributes.values.filter { it.inputRate == KslInputRate.Vertex }, "vertex attributes")
        src.generateInterStageOutputs(vertexStage)

        src.appendLine("void main() {")
        src.appendLine(generateScope(vertexStage.main, "    "))
        src.appendLine("}")
        return src.toString()
    }

    private fun generateFragmentSrc(fragmentStage: KslFragmentStage): String {
        val src = StringBuilder()
        src.appendLine("""
            #version 300 es
            
            /* 
             * ${fragmentStage.program.name} - generated fragment shader
             */
             
            precision highp float;
            precision highp sampler2DShadow;
        """.trimIndent())
        src.appendLine()

        src.generateUniforms(fragmentStage.uniforms)
        src.generateInterStageInputs(fragmentStage)
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
                    "[${u.arraySize}]"
                } else {
                    ""
                }
                appendLine("uniform ${glslTypeName(u.expressionType)} ${u.value.name()}${arraySuffix};")
            }
            appendLine()
        }
    }

    private fun StringBuilder.generateAttributes(attribs: List<KslVertexAttribute<*>>, info: String) {
        if (attribs.isNotEmpty()) {
            appendLine("// $info")
            attribs.forEach { a ->
                appendLine("layout(location=${a.location}) in ${glslTypeName(a.expressionType)} ${a.value.name()};")
            }
            appendLine()
        }
    }

    private fun StringBuilder.generateInterStageOutputs(vertexStage: KslVertexStage) {
        if (vertexStage.interStageVars.isNotEmpty()) {
            appendLine("// custom vertex stage outputs")
            vertexStage.interStageVars.forEach { interStage ->
                appendLine("${interStage.interpolation.glsl()} out ${glslTypeName(interStage.input.assignType)} ${interStage.input.name()};")
            }
            appendLine()
        }
    }

    private fun StringBuilder.generateInterStageInputs(fragmentStage: KslFragmentStage) {
        if (fragmentStage.interStageVars.isNotEmpty()) {
            appendLine("// custom fragment stage inputs")
            fragmentStage.interStageVars.forEach { interStage ->
                appendLine("${interStage.interpolation.glsl()} in ${glslTypeName(interStage.output.expressionType)} ${interStage.output.name()};")
            }
            appendLine()
        }
    }

    private fun StringBuilder.generateOutputs(outputs: List<KslStageOutput<*>>) {
        if (outputs.isNotEmpty()) {
            appendLine("// stage outputs")
            outputs.forEach { output ->
                val loc = if (output.location >= 0) "layout(location=${output.location}) " else ""
                appendLine("${loc}out ${glslTypeName(output.expressionType)} ${output.value.name()};")
            }
            appendLine()
        }
    }

    override fun opDeclare(op: KslDeclareVar): String {
        val initExpr = op.initExpression?.let { " = ${it.generateExpression(this)}" } ?: ""
        val state = op.declareVar
        return "${glslTypeName(state.expressionType)} ${state.name()}${initExpr};"
    }

    override fun opAssign(op: KslAssign<*>): String {
        return "${op.assignTarget.generateAssignable(this)} = ${op.assignExpression.generateExpression(this)};"
    }

    override fun opAugmentedAssign(op: KslAugmentedAssign<*>): String {
        return "${op.assignTarget.generateAssignable(this)} ${op.augmentationMode.opChar}= ${op.assignExpression.generateExpression(this)};"
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

    override fun opBlock(op: KslBlock): String {
        val txt = StringBuilder("{ // block: ${op.opName}\n")
        txt.appendLine(generateScope(op.body, "    "))
        txt.append("}")
        return txt.toString()
    }

    private fun generateArgs(args: List<KslExpression<*>>, expectedCount: Int): String {
        check(args.size == expectedCount)
        return args.joinToString { it.generateExpression(this) }
    }

    override fun builtinClamp(func: KslBuiltinClampScalar<*>) = "clamp(${generateArgs(func.args, 3)})"
    override fun builtinClamp(func: KslBuiltinClampVector<*, *>) = "clamp(${generateArgs(func.args, 3)})"
    override fun builtinDot(func: KslBuiltinDot<*, *>) = "dot(${generateArgs(func.args, 2)})"
    override fun builtinLength(func: KslBuiltinLength<*, *>) = "length(${generateArgs(func.args, 1)})"
    override fun builtinMax(func: KslBuiltinMaxScalar<*>) = "max(${generateArgs(func.args, 2)})"
    override fun builtinMax(func: KslBuiltinMaxVector<*, *>) = "max(${generateArgs(func.args, 2)})"
    override fun builtinMin(func: KslBuiltinMinScalar<*>) = "min(${generateArgs(func.args, 2)})"
    override fun builtinMin(func: KslBuiltinMinVector<*, *>) = "min(${generateArgs(func.args, 2)})"
    override fun builtinNormalize(func: KslBuiltinNormalize<*, *>) = "normalize(${generateArgs(func.args, 1)})"
    override fun builtinReflect(func: KslBuiltinReflect<*, *>) = "reflect(${generateArgs(func.args, 2)})"
    override fun builtinPow(func: KslBuiltinPowScalar) = "pow(${generateArgs(func.args, 2)})"
    override fun builtinPow(func: KslBuiltinPowVector<*, *>) = "pow(${generateArgs(func.args, 2)})"

    private fun KslInterStageInterpolation.glsl(): String {
        return when (this) {
            KslInterStageInterpolation.Smooth -> "smooth"
            KslInterStageInterpolation.Flat -> "flat"
            KslInterStageInterpolation.NoPerspective -> "noperspective"
        }
    }

    override fun KslState.name(): String {
        return when (stateName) {
            KslVertexStage.NAME_IN_VERTEX_INDEX -> "gl_VertexID"
            KslVertexStage.NAME_IN_INSTANCE_INDEX -> "gl_InstanceID"
            KslVertexStage.NAME_OUT_POSITION -> "gl_Position"

            KslFragmentStage.NAME_IN_FRAG_POSITION -> "gl_FragCoord"
            KslFragmentStage.NAME_IN_IS_FRONT_FACING -> "gl_FrontFacing"
            KslFragmentStage.NAME_OUT_DEPTH -> "gl_FragDepth"

            else -> stateName
        }
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

            KslTypeColorSampler1d -> "sampler2D"    // in WebGL2, 1d textures are not supported, simply use 2d instead (with height = 1px)
            KslTypeColorSampler2d -> "sampler2D"
            KslTypeColorSampler3d -> "sampler3D"
            KslTypeColorSamplerCube -> "samplerCube"
            KslTypeColorSampler2dArray -> "sampler2DArray"
            KslTypeColorSamplerCubeArray -> "samplerCubeArray"

            KslTypeDepthSampler2d -> "sampler2DShadow"
            KslTypeDepthSamplerCube -> "samplerCubeShadow"
            KslTypeDepthSampler2dArray -> "sampler2DArrayShadow"
            KslTypeDepthSamplerCubeArray -> "samplerCubeArrayShadow"

            is KslTypeArray<*> -> glslTypeName(type.elemType)
        }
    }

    class GlslGeneratorOutput(val vertexSrc: String, val fragmentSrc: String) : GeneratorOutput {
        private fun linePrefix(line: Int): String {
            var num = "$line"
            while (num.length < 3) {
                num = " $num"
            }
            return "$num  "
        }

        fun dump() {
            println("###  vertex shader:")
            vertexSrc.lineSequence().forEachIndexed { i, line -> println("${linePrefix(i)}${line}") }
            println("###  fragment shader:")
            fragmentSrc.lineSequence().forEachIndexed { i, line -> println("${linePrefix(i)}${line}") }
        }
    }
}