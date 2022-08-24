package de.fabmax.kool.modules.ksl.generator

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslState

/**
 * Default GLSL shader code generator, generates glsl in version 300 es, which works for WebGL and OpenGL 3.3+
 */
open class GlslGenerator : KslGenerator() {

    protected var glslVersionStr = "#version 300 es"

    var blockIndent = "  "

    override fun generateProgram(program: KslProgram): GlslGeneratorOutput {
        return GlslGeneratorOutput(generateVertexSrc(program.vertexStage), generateFragmentSrc(program.fragmentStage))
    }

    override fun constFloatVecExpression(vararg values: KslExpression<KslTypeFloat1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 2 and 4)")
        }
        return "vec${values.size}(${values.joinToString { it.generateExpression(this) }})"
    }

    override fun constIntVecExpression(vararg values: KslExpression<KslTypeInt1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 2 and 4)")
        }
        return "ivec${values.size}(${values.joinToString { it.generateExpression(this) }})"
    }

    override fun constUintVecExpression(vararg values: KslExpression<KslTypeUint1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 2 and 4)")
        }
        return "uvec${values.size}(${values.joinToString { it.generateExpression(this) }})"
    }

    override fun constBoolVecExpression(vararg values: KslExpression<KslTypeBool1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 2 and 4)")
        }
        return "bvec${values.size}(${values.joinToString { it.generateExpression(this) }})"
    }

    override fun constMatExpression(vararg columns: KslVectorExpression<*, KslTypeFloat1>): String {
        if (columns.size !in 2..4) {
            throw IllegalArgumentException("invalid number of columns: ${columns.size} (must be between 2 and 4)")
        }
        return "mat${columns.size}(${columns.joinToString { it.generateExpression(this) }})"
    }

    override fun castExpression(castExpr: KslExpressionCast<*>): String {
        return "${glslTypeName(castExpr.expressionType)}(${castExpr.value.generateExpression(this)})"
    }

    override fun <B: KslBoolType> compareExpression(expression: KslExpressionCompare<B>): String {
        val lt = expression.left.generateExpression(this)
        val rt = expression.right.generateExpression(this)
        return if (expression.left.expressionType is KslVector<*>) {
            when (expression.operator) {
                KslCompareOperator.Equal -> "equal($lt, $rt)"
                KslCompareOperator.NotEqual -> "notEqual($lt, $rt)"
                KslCompareOperator.Less -> "lessThan($lt, $rt)"
                KslCompareOperator.LessEqual -> "lessThanEqual($lt, $rt)"
                KslCompareOperator.Greater -> "greaterThan($lt, $rt)"
                KslCompareOperator.GreaterEqual -> "greaterThanEqual($lt, $rt)"
            }
        } else {
            "($lt ${expression.operator.opString} $rt)"
        }
    }

    override fun sampleColorTexture(sampleTexture: KslSampleColorTexture<*>): String {
        val sampler = sampleTexture.sampler.generateExpression(this)
        val coord = if (sampleTexture.sampler.expressionType is KslTypeSampler1d && sampleTexture.coord.expressionType is KslTypeFloat1) {
            // for better OpenGL ES compatibility 1d textures actually are 2d textures...
            "vec2(${sampleTexture.coord.generateExpression(this)}, 0.5)"
        } else {
            sampleTexture.coord.generateExpression(this)
        }

        return if (sampleTexture.lod != null) {
            "textureLod(${sampler}, ${coord}, ${sampleTexture.lod.generateExpression(this)})"
        } else {
            "texture(${sampler}, ${coord})"
        }
    }

    override fun sampleDepthTexture(sampleTexture: KslSampleDepthTexture<*>): String {
        return "texture(${sampleTexture.sampler.generateExpression(this)}, ${sampleTexture.coord.generateExpression(this)})"
    }

    override fun textureSize(textureSize: KslTextureSize<*, *>): String {
        return "textureSize(${textureSize.sampler.generateExpression(this)}, ${textureSize.lod.generateExpression(this)})"
    }

    override fun texelFetch(expression: KslTexelFetch<*>): String {
        val sampler = expression.sampler.generateExpression(this)
        val coords = expression.coord.generateExpression(this)
        val lod = expression.lod?.generateExpression(this)
        return "texelFetch($sampler, $coords, ${lod ?: 0})"
    }

    private fun generateVertexSrc(vertexStage: KslVertexStage): String {
        val src = StringBuilder()
        src.appendLine("""
            $glslVersionStr
            precision highp sampler3D;
            
            /* 
             * ${vertexStage.program.name} - generated vertex shader
             */ 
        """.trimIndent())
        src.appendLine()

        src.generateUbos(vertexStage)
        src.generateUniformSamplers(vertexStage)
        src.generateAttributes(vertexStage.attributes.values.filter { it.inputRate == KslInputRate.Instance }, "instance attributes")
        src.generateAttributes(vertexStage.attributes.values.filter { it.inputRate == KslInputRate.Vertex }, "vertex attributes")
        src.generateInterStageOutputs(vertexStage)
        src.generateFunctions(vertexStage)

        src.appendLine("void main() {")
        src.appendLine(generateScope(vertexStage.main, blockIndent))
        src.appendLine("}")
        return src.toString()
    }

    private fun generateFragmentSrc(fragmentStage: KslFragmentStage): String {
        val src = StringBuilder()
        src.appendLine("""
            $glslVersionStr
            precision highp float;
            precision highp sampler2DShadow;
            precision highp sampler3D;
            
            /* 
             * ${fragmentStage.program.name} - generated fragment shader
             */
        """.trimIndent())
        src.appendLine()

        src.generateUbos(fragmentStage)
        src.generateUniformSamplers(fragmentStage)
        src.generateInterStageInputs(fragmentStage)
        src.generateOutputs(fragmentStage.outColors)
        src.generateFunctions(fragmentStage)

        src.appendLine("void main() {")
        src.appendLine(generateScope(fragmentStage.main, blockIndent))
        src.appendLine("}")
        return src.toString()
    }

    protected open fun StringBuilder.generateUniformSamplers(stage: KslShaderStage) {
        val samplers = stage.getUsedSamplers()
        if (samplers.isNotEmpty()) {
            appendLine("// texture samplers")
            for (u in samplers) {
                val arraySuffix = if (u.value is KslArray<*>) { "[${u.arraySize}]" } else { "" }
                appendLine("uniform ${glslTypeName(u.expressionType)} ${u.value.name()}${arraySuffix};")
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateUbos(stage: KslShaderStage) {
        val ubos = stage.getUsedUbos()
        if (ubos.isNotEmpty()) {
            appendLine("// uniform buffer objects")
            for (ubo in ubos) {
                // if isShared is true, the underlying buffer is externally provided without the buffer layout
                // being queried via OpenGL API -> use standardized std140 layout
                val layoutPrefix = if (ubo.isShared) { "layout(std140) " } else { "" }

                appendLine("${layoutPrefix}uniform ${ubo.name} {")
                for (u in ubo.uniforms.values) {
                    val arraySuffix = if (u.value is KslArray<*>) { "[${u.arraySize}]" } else { "" }
                    appendLine("    highp ${glslTypeName(u.expressionType)} ${u.value.name()}${arraySuffix};")
                }
                appendLine("};")
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateAttributes(attribs: List<KslVertexAttribute<*>>, info: String) {
        if (attribs.isNotEmpty()) {
            appendLine("// $info")
            attribs.forEach { a ->
                appendLine("layout(location=${a.location}) in ${glslTypeName(a.expressionType)} ${a.value.name()};")
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateInterStageOutputs(vertexStage: KslVertexStage) {
        if (vertexStage.interStageVars.isNotEmpty()) {
            appendLine("// custom vertex stage outputs")
            vertexStage.interStageVars.forEach { interStage ->
                val value = interStage.input
                val arraySuffix = if (value is KslArray<*>) { "[${value.arraySize}]" } else { "" }
                appendLine("${interStage.interpolation.glsl()} out ${glslTypeName(value.expressionType)} ${value.name()}${arraySuffix};")
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateInterStageInputs(fragmentStage: KslFragmentStage) {
        if (fragmentStage.interStageVars.isNotEmpty()) {
            appendLine("// custom fragment stage inputs")
            fragmentStage.interStageVars.forEach { interStage ->
                val value = interStage.output
                val arraySuffix = if (value is KslArray<*>) { "[${value.arraySize}]" } else { "" }
                appendLine("${interStage.interpolation.glsl()} in ${glslTypeName(value.expressionType)} ${value.name()}${arraySuffix};")
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateOutputs(outputs: List<KslStageOutput<*>>) {
        if (outputs.isNotEmpty()) {
            appendLine("// stage outputs")
            outputs.forEach { output ->
                val loc = if (output.location >= 0) "layout(location=${output.location}) " else ""
                appendLine("${loc}out ${glslTypeName(output.expressionType)} ${output.value.name()};")
            }
            appendLine()
        }
    }

    private fun StringBuilder.generateFunctions(stage: KslShaderStage) {
        if (stage.functions.isNotEmpty()) {
            val funcList = stage.functions.values.toMutableList()
            sortFunctions(funcList)
            funcList.forEach { func ->
                appendLine("${glslTypeName(func.returnType)} ${func.name}(${func.parameters.joinToString { p -> "${glslTypeName(p.expressionType)} ${p.stateName}" }}) {")
                appendLine(generateScope(func.body, blockIndent))
                appendLine("}")
                appendLine()
            }
        }
    }

    override fun opDeclareVar(op: KslDeclareVar): String {
        val initExpr = op.initExpression?.let { " = ${it.generateExpression(this)}" } ?: ""
        val state = op.declareVar
        return "${glslTypeName(state.expressionType)} ${state.name()}${initExpr};"
    }

    override fun opDeclareArray(op: KslDeclareArray): String {
        val initExpr = op.elements.joinToString { it.generateExpression(this) }
        val array = op.declareVar
        val typeName = glslTypeName(array.expressionType.elemType)
        return "$typeName ${array.name()}[${array.arraySize}] = ${typeName}[](${initExpr});"
    }

    override fun opAssign(op: KslAssign<*>): String {
        return "${op.assignTarget.generateAssignable(this)} = ${op.assignExpression.generateExpression(this)};"
    }

    override fun opAugmentedAssign(op: KslAugmentedAssign<*>): String {
        return "${op.assignTarget.generateAssignable(this)} ${op.augmentationMode.opChar}= ${op.assignExpression.generateExpression(this)};"
    }

    override fun opIf(op: KslIf): String {
        val txt = StringBuilder("if (${op.condition.generateExpression(this)}) {\n")
        txt.appendLine(generateScope(op.body, blockIndent))
        txt.append("}")
        op.elseIfs.forEach { elseIf ->
            txt.appendLine(" else if (${elseIf.first.generateExpression(this)}) {")
            txt.appendLine(generateScope(elseIf.second, blockIndent))
            txt.append("}")
        }
        if (op.elseBody.isNotEmpty()) {
            txt.appendLine(" else {")
            txt.appendLine(generateScope(op.elseBody, blockIndent))
            txt.append("}")
        }
        return txt.toString()
    }

    override fun opFor(op: KslLoopFor<*>): String {
        return StringBuilder("for (; ")
            .append(op.whileExpression.generateExpression(this)).append("; ")
            .append(op.loopVar.generateAssignable(this)).append(" += ").append(op.incExpr.generateExpression(this))
            .appendLine(") {")
            .appendLine(generateScope(op.body, blockIndent))
            .append("}")
            .toString()
    }

    override fun opWhile(op: KslLoopWhile): String {
        return StringBuilder("while (${op.whileExpression.generateExpression(this)}) {\n")
            .appendLine(generateScope(op.body, blockIndent))
            .append("}")
            .toString()
    }

    override fun opDoWhile(op: KslLoopDoWhile): String {
        return StringBuilder("do {\n")
            .appendLine(generateScope(op.body, blockIndent))
            .append("} while (${op.whileExpression.generateExpression(this)});")
            .toString()
    }

    override fun opBreak(op: KslLoopBreak) = "break;"

    override fun opContinue(op: KslLoopContinue) = "continue;"

    override fun opDiscard(op: KslDiscard): String = "discard;"

    override fun opReturn(op: KslReturn): String = "return ${op.returnValue.generateExpression(this)};"

    override fun opBlock(op: KslBlock): String {
        val txt = StringBuilder("{ // block: ${op.opName}\n")
        txt.appendLine(generateScope(op.body, blockIndent))
        txt.append("}")
        return txt.toString()
    }

    private fun generateArgs(args: List<KslExpression<*>>, expectedArgs: Int): String {
        check(args.size == expectedArgs)
        return args.joinToString { it.generateExpression(this) }
    }

    override fun invokeFunction(func: KslInvokeFunction<*>) = "${func.function.name}(${generateArgs(func.args, func.args.size)})"

    override fun builtinAbs(func: KslBuiltinAbsScalar<*>) = "abs(${generateArgs(func.args, 1)})"
    override fun builtinAbs(func: KslBuiltinAbsVector<*, *>) = "abs(${generateArgs(func.args, 1)})"
    override fun builtinAtan2(func: KslBuiltinAtan2Scalar) = "atan(${generateArgs(func.args, 2)})"
    override fun builtinAtan2(func: KslBuiltinAtan2Vector<*>) = "atan(${generateArgs(func.args, 2)})"
    override fun builtinCeil(func: KslBuiltinCeilScalar) = "ceil(${generateArgs(func.args, 1)})"
    override fun builtinCeil(func: KslBuiltinCeilVector<*>) = "ceil(${generateArgs(func.args, 1)})"
    override fun builtinClamp(func: KslBuiltinClampScalar<*>) = "clamp(${generateArgs(func.args, 3)})"
    override fun builtinClamp(func: KslBuiltinClampVector<*, *>) = "clamp(${generateArgs(func.args, 3)})"
    override fun builtinCross(func: KslBuiltinCross) = "cross(${generateArgs(func.args, 2)})"
    override fun builtinDegrees(func: KslBuiltinDegreesScalar) = "degrees(${generateArgs(func.args, 1)})"
    override fun builtinDegrees(func: KslBuiltinDegreesVector<*>) = "degrees(${generateArgs(func.args, 1)})"
    override fun builtinDistance(func: KslBuiltinDistanceScalar<*>) = "distance(${generateArgs(func.args, 2)})"
    override fun builtinDot(func: KslBuiltinDot<*>) = "dot(${generateArgs(func.args, 2)})"
    override fun builtinExp(func: KslBuiltinExpScalar) = "exp(${generateArgs(func.args, 1)})"
    override fun builtinExp(func: KslBuiltinExpVector<*>) = "exp(${generateArgs(func.args, 1)})"
    override fun builtinExp2(func: KslBuiltinExp2Scalar) = "exp2(${generateArgs(func.args, 1)})"
    override fun builtinExp2(func: KslBuiltinExp2Vector<*>) = "exp2(${generateArgs(func.args, 1)})"
    override fun builtinFaceForward(func: KslBuiltinFaceForward<*>) = "faceforward(${generateArgs(func.args, 3)})"
    override fun builtinFloor(func: KslBuiltinFloorScalar) = "floor(${generateArgs(func.args, 1)})"
    override fun builtinFloor(func: KslBuiltinFloorVector<*>) = "floor(${generateArgs(func.args, 1)})"
    override fun builtinFma(func: KslBuiltinFmaScalar) = "fma(${generateArgs(func.args, 3)})"
    override fun builtinFma(func: KslBuiltinFmaVector<*>) = "fma(${generateArgs(func.args, 3)})"
    override fun builtinFract(func: KslBuiltinFractScalar) = "fract(${generateArgs(func.args, 1)})"
    override fun builtinFract(func: KslBuiltinFractVector<*>) = "fract(${generateArgs(func.args, 1)})"
    override fun builtinInverseSqrt(func: KslBuiltinInverseSqrtScalar) = "inversesqrt(${generateArgs(func.args, 1)})"
    override fun builtinInverseSqrt(func: KslBuiltinInverseSqrtVector<*>) = "inversesqrt(${generateArgs(func.args, 1)})"
    override fun builtinLength(func: KslBuiltinLength<*>) = "length(${generateArgs(func.args, 1)})"
    override fun builtinLog(func: KslBuiltinLogScalar) = "log(${generateArgs(func.args, 1)})"
    override fun builtinLog(func: KslBuiltinLogVector<*>) = "log(${generateArgs(func.args, 1)})"
    override fun builtinLog2(func: KslBuiltinLog2Scalar) = "log2(${generateArgs(func.args, 1)})"
    override fun builtinLog2(func: KslBuiltinLog2Vector<*>) = "log2(${generateArgs(func.args, 1)})"
    override fun builtinMax(func: KslBuiltinMaxScalar<*>) = "max(${generateArgs(func.args, 2)})"
    override fun builtinMax(func: KslBuiltinMaxVector<*, *>) = "max(${generateArgs(func.args, 2)})"
    override fun builtinMin(func: KslBuiltinMinScalar<*>) = "min(${generateArgs(func.args, 2)})"
    override fun builtinMin(func: KslBuiltinMinVector<*, *>) = "min(${generateArgs(func.args, 2)})"
    override fun builtinMix(func: KslBuiltinMixScalar) = "mix(${generateArgs(func.args, 3)})"
    override fun builtinMix(func: KslBuiltinMixVector<*>) = "mix(${generateArgs(func.args, 3)})"
    override fun builtinNormalize(func: KslBuiltinNormalize<*>) = "normalize(${generateArgs(func.args, 1)})"
    override fun builtinReflect(func: KslBuiltinReflect<*>) = "reflect(${generateArgs(func.args, 2)})"
    override fun builtinRefract(func: KslBuiltinRefract<*>) = "refract(${generateArgs(func.args, 3)})"
    override fun builtinRound(func: KslBuiltinRoundScalar) = "round(${generateArgs(func.args, 1)})"
    override fun builtinRound(func: KslBuiltinRoundVector<*>) = "round(${generateArgs(func.args, 1)})"
    override fun builtinSign(func: KslBuiltinSignScalar<*>) = "sign(${generateArgs(func.args, 1)})"
    override fun builtinSign(func: KslBuiltinSignVector<*, *>) = "sign(${generateArgs(func.args, 1)})"
    override fun builtinPow(func: KslBuiltinPowScalar) = "pow(${generateArgs(func.args, 2)})"
    override fun builtinPow(func: KslBuiltinPowVector<*>) = "pow(${generateArgs(func.args, 2)})"
    override fun builtinRadians(func: KslBuiltinRadiansScalar) = "radians(${generateArgs(func.args, 1)})"
    override fun builtinRadians(func: KslBuiltinRadiansVector<*>) = "radians(${generateArgs(func.args, 1)})"
    override fun builtinSmoothStep(func: KslBuiltinSmoothStepScalar) = "smoothstep(${generateArgs(func.args, 3)})"
    override fun builtinSmoothStep(func: KslBuiltinSmoothStepVector<*>) = "smoothstep(${generateArgs(func.args, 3)})"
    override fun builtinSqrt(func: KslBuiltinSqrtScalar) = "sqrt(${generateArgs(func.args, 1)})"
    override fun builtinSqrt(func: KslBuiltinSqrtVector<*>) = "sqrt(${generateArgs(func.args, 1)})"
    override fun builtinStep(func: KslBuiltinStepScalar) = "step(${generateArgs(func.args, 2)})"
    override fun builtinStep(func: KslBuiltinStepVector<*>) = "step(${generateArgs(func.args, 2)})"
    override fun builtinTrigonometry(func: KslBuiltinTrigonometryScalar) = "${func.name}(${generateArgs(func.args, 1)})"
    override fun builtinTrigonometry(func: KslBuiltinTrigonometryVector<*>) = "${func.name}(${generateArgs(func.args, 1)})"
    override fun builtinTrunc(func: KslBuiltinTruncScalar) = "trunc(${generateArgs(func.args, 1)})"
    override fun builtinTrunc(func: KslBuiltinTruncVector<*>) = "trunc(${generateArgs(func.args, 1)})"

    override fun builtinDeterminant(func: KslBuiltinDeterminant<*, *>) = "determinant(${generateArgs(func.args, 1)})"
    override fun builtinTranspose(func: KslBuiltinTranspose<*, *>) = "transpose(${generateArgs(func.args, 1)})"

    protected fun KslInterStageInterpolation.glsl(): String {
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
            KslVertexStage.NAME_OUT_POINT_SIZE -> "gl_PointSize"

            KslFragmentStage.NAME_IN_FRAG_POSITION -> "gl_FragCoord"
            KslFragmentStage.NAME_IN_IS_FRONT_FACING -> "gl_FrontFacing"
            KslFragmentStage.NAME_OUT_DEPTH -> "gl_FragDepth"

            else -> stateName
        }
    }

    protected fun glslTypeName(type: KslType): String {
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
            KslTypeUint1 -> "uint"
            KslTypeUint2 -> "uvec2"
            KslTypeUint3 -> "uvec3"
            KslTypeUint4 -> "uvec4"
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