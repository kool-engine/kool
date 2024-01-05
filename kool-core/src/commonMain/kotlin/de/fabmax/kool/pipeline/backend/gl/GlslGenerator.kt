package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslState
import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.PipelineBase

/**
 * Default GLSL shader code generator.
 */
open class GlslGenerator(val hints: Hints) : KslGenerator() {

    var blockIndent = "  "

    override fun generateProgram(program: KslProgram, pipeline: Pipeline): GlslGeneratorOutput {
        val vertexStage = checkNotNull(program.vertexStage) {
            "KslProgram vertexStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
        }
        val fragmentStage = checkNotNull(program.fragmentStage) {
            "KslProgram fragmentStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
        }
        return GlslGeneratorOutput.shaderOutput(
            generateVertexSrc(vertexStage, pipeline),
            generateFragmentSrc(fragmentStage, pipeline)
        )
    }

    override fun generateComputeProgram(program: KslProgram, pipeline: ComputePipeline): GlslGeneratorOutput {
        val computeStage = checkNotNull(program.computeStage) {
            "KslProgram computeStage is missing"
        }
        return GlslGeneratorOutput.computeOutput(generateComputeSrc(computeStage, pipeline))
    }

    override fun constFloatVecExpression(vararg values: KslExpression<KslFloat1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 2 and 4)")
        }
        return "vec${values.size}(${values.joinToString { it.generateExpression(this) }})"
    }

    override fun constIntVecExpression(vararg values: KslExpression<KslInt1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 2 and 4)")
        }
        return "ivec${values.size}(${values.joinToString { it.generateExpression(this) }})"
    }

    override fun constUintVecExpression(vararg values: KslExpression<KslUint1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 2 and 4)")
        }
        return "uvec${values.size}(${values.joinToString { it.generateExpression(this) }})"
    }

    override fun constBoolVecExpression(vararg values: KslExpression<KslBool1>): String {
        if (values.size !in 2..4) {
            throw IllegalArgumentException("invalid number of values: ${values.size} (must be between 2 and 4)")
        }
        return "bvec${values.size}(${values.joinToString { it.generateExpression(this) }})"
    }

    override fun constMatExpression(vararg columns: KslVectorExpression<*, KslFloat1>): String {
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
        val coord = if (sampleTexture.sampler.expressionType is KslSampler1dType && sampleTexture.coord.expressionType is KslFloat1) {
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

    override fun storageSize(storageSize: KslStorageSize<*, *>): String {
        return "imageSize(${storageSize.storage.generateExpression(this)})"
    }

    override fun storageRead(storageRead: KslStorageRead<*, *, *>): String {
        val elemType = storageRead.storage.expressionType.elemType
        val channels = if (elemType is KslVector<*>) elemType.dimens else 1
        val suffix = when (channels) {
            1 -> ".x"
            2 -> ".xy"
            3 -> ".xyz"
            else -> ""
        }
        return "imageLoad(${storageRead.storage.generateExpression(this)}, ${storageRead.coord.generateExpression(this)})$suffix"
    }

    override fun opStorageWrite(op: KslStorageWrite<*, *, *>): String {
        val expr = op.data.generateExpression(this)
        val elemType = op.storage.expressionType.elemType
        val vec4 = when (elemType) {
            is KslFloat1 -> "vec4($expr, 0.0, 0.0, 0.0)"
            is KslFloat2 -> "vec4($expr, 0.0, 0.0)"
            is KslFloat3 -> "vec4($expr, 0.0)"
            is KslInt1 -> "ivec4($expr, 0, 0, 0)"
            is KslInt2 -> "ivec4($expr, 0, 0)"
            is KslInt3 -> "ivec4($expr, 0)"
            is KslUint1 -> "uvec4($expr, 0, 0, 0)"
            is KslUint2 -> "uvec4($expr, 0, 0)"
            is KslUint3 -> "uvec4($expr, 0)"
            else -> expr
        }
        return "imageStore(${op.storage.generateExpression(this)}, ${op.coord.generateExpression(this)}, $vec4);"
    }

    override fun storageAtomicOp(atomicOp: KslStorageAtomicOp<*, *, *>): String {
        val func = when(atomicOp.op) {
            KslStorageAtomicOp.Op.Swap -> "imageAtomicExchange"
            KslStorageAtomicOp.Op.Add -> "imageAtomicAdd"
            KslStorageAtomicOp.Op.And -> "imageAtomicAnd"
            KslStorageAtomicOp.Op.Or -> "imageAtomicOr"
            KslStorageAtomicOp.Op.Xor -> "imageAtomicXor"
            KslStorageAtomicOp.Op.Min -> "imageAtomicMin"
            KslStorageAtomicOp.Op.Max -> "imageAtomicMax"
        }

        return "$func(${atomicOp.storage.generateExpression(this)}, " +
                "${atomicOp.coord.generateExpression(this)}, " +
                "${atomicOp.data.generateExpression(this)})"
    }

    override fun storageAtomicCompareSwap(atomicCompSwap: KslStorageAtomicCompareSwap<*, *, *>): String {
        return "imageAtomicCompSwap(${atomicCompSwap.storage.generateExpression(this)}, " +
                "${atomicCompSwap.coord.generateExpression(this)}, " +
                "${atomicCompSwap.compare.generateExpression(this)}, " +
                "${atomicCompSwap.data.generateExpression(this)})"
    }

    private fun generateVertexSrc(vertexStage: KslVertexStage, pipeline: PipelineBase): String {
        val src = StringBuilder()
        src.appendLine("""
            ${hints.glslVersionStr}
            precision highp sampler3D;
            
            /*
             * ${vertexStage.program.name} - generated vertex shader
             */
        """.trimIndent())
        src.appendLine()

        src.generateUbos(vertexStage, pipeline)
        src.generateUniformSamplers(vertexStage, pipeline)
        src.generateUniformStorage(vertexStage, pipeline)
        src.generateAttributes(vertexStage.attributes.values.filter { it.inputRate == KslInputRate.Instance }, "instance attributes")
        src.generateAttributes(vertexStage.attributes.values.filter { it.inputRate == KslInputRate.Vertex }, "vertex attributes")
        src.generateInterStageOutputs(vertexStage)
        src.generateFunctions(vertexStage)

        src.appendLine("void main() {")
        src.appendLine(generateScope(vertexStage.main, blockIndent))
        src.appendLine("}")
        return src.toString()
    }

    private fun generateFragmentSrc(fragmentStage: KslFragmentStage, pipeline: PipelineBase): String {
        val src = StringBuilder()
        src.appendLine("""
            ${hints.glslVersionStr}
            precision highp float;
            precision highp sampler2DShadow;
            precision highp sampler3D;
            
            /*
             * ${fragmentStage.program.name} - generated fragment shader
             */
        """.trimIndent())
        src.appendLine()

        src.generateUbos(fragmentStage, pipeline)
        src.generateUniformSamplers(fragmentStage, pipeline)
        src.generateUniformStorage(fragmentStage, pipeline)
        src.generateInterStageInputs(fragmentStage)
        src.generateOutputs(fragmentStage.outColors)
        src.generateFunctions(fragmentStage)

        src.appendLine("void main() {")
        src.appendLine(generateScope(fragmentStage.main, blockIndent))
        src.appendLine("}")
        return src.toString()
    }

    private fun generateComputeSrc(computeStage: KslComputeStage, pipeline: PipelineBase): String {
        val src = StringBuilder()
        src.appendLine("""
            ${hints.glslVersionStr}
            
            /*
             * ${computeStage.program.name} - generated compute shader
             */
             
            layout(local_size_x = ${computeStage.workGroupSize.x}, local_size_y = ${computeStage.workGroupSize.y}, local_size_z = ${computeStage.workGroupSize.z}) in;
        """.trimIndent())
        src.appendLine()

        src.generateUbos(computeStage, pipeline)
        src.generateUniformSamplers(computeStage, pipeline)
        src.generateUniformStorage(computeStage, pipeline)
        src.generateFunctions(computeStage)

        src.appendLine("void main() {")
        src.appendLine(generateScope(computeStage.main, blockIndent))
        src.appendLine("}")
        return src.toString()
    }

    protected open fun StringBuilder.generateUniformSamplers(stage: KslShaderStage, pipeline: PipelineBase) {
        val samplers = stage.getUsedSamplers()
        if (samplers.isNotEmpty()) {
            appendLine("// texture samplers")
            for (u in samplers) {
                appendLine("uniform ${glslTypeName(u.expressionType)} ${u.value.name()};")
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateUniformStorage(stage: KslShaderStage, pipeline: PipelineBase) {
        val storage = stage.getUsedStorage()
        if (storage.isNotEmpty()) {
            appendLine("// image storage")
            storage.forEachIndexed { i, it ->
                appendLine("layout(${it.storageType.formatQualifier}, binding=$i) uniform ${glslTypeName(it.expressionType)} ${it.name};")
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateUbos(stage: KslShaderStage, pipeline: PipelineBase) {
        val ubos = stage.getUsedUbos()
        if (ubos.isNotEmpty()) {
            appendLine("// uniform buffer objects")
            for (ubo in ubos) {
                if (hints.replaceUbosByPlainUniforms) {
                    // compatibility fallback required in some scenarios
                    ubo.uniforms.values
                        .filter { it.expressionType !is KslArrayType<*> || it.arraySize > 0 }
                        .forEach {
                            appendLine("    uniform highp ${glslTypeName(it.expressionType)} ${it.value.name()};")
                        }

                } else {
                    // if isShared is true, the underlying buffer is externally provided without the buffer layout
                    // being queried via OpenGL API -> use standardized std140 layout
                    val layoutPrefix = if (hints.alwaysGenerateStd140Layout || ubo.isShared) "layout(std140) " else ""
                    appendLine("${layoutPrefix}uniform ${ubo.name} {")
                    ubo.uniforms.values
                        .filter { it.expressionType !is KslArrayType<*> || it.arraySize > 0 }
                        .forEach {
                            appendLine("    highp ${glslTypeName(it.expressionType)} ${it.value.name()};")
                        }
                    appendLine("};")
                }
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
                appendLine("${interStage.interpolation.glsl()} out ${glslTypeName(value.expressionType)} ${value.name()};")
            }
            appendLine()
        }
    }

    protected open fun StringBuilder.generateInterStageInputs(fragmentStage: KslFragmentStage) {
        if (fragmentStage.interStageVars.isNotEmpty()) {
            appendLine("// custom fragment stage inputs")
            fragmentStage.interStageVars.forEach { interStage ->
                val value = interStage.output
                appendLine("${interStage.interpolation.glsl()} in ${glslTypeName(value.expressionType)} ${value.name()};")
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
        val array = op.declareVar
        val typeName = glslTypeName(array.expressionType)

        return if (op.elements.size == 1 && op.elements[0].expressionType == array.expressionType) {
            "$typeName ${array.name()} = ${op.elements[0].generateExpression(this)};"
        } else {
            val initExpr = op.elements.joinToString { it.generateExpression(this) }
            "$typeName ${array.name()} = ${typeName}(${initExpr});"
        }
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

    override fun opInlineCode(op: KslInlineCode): String {
        return op.code
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
    override fun builtinIsInf(func: KslBuiltinIsInfScalar) = "isinf(${generateArgs(func.args, 1)})"
    override fun builtinIsInf(func: KslBuiltinIsInfVector<*, *>) = "isinf(${generateArgs(func.args, 1)})"
    override fun builtinIsNan(func: KslBuiltinIsNanScalar) = "isnan(${generateArgs(func.args, 1)})"
    override fun builtinIsNan(func: KslBuiltinIsNanVector<*, *>) = "isnan(${generateArgs(func.args, 1)})"
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

            KslComputeStage.NAME_IN_GLOBAL_INVOCATION_ID -> "gl_GlobalInvocationID"
            KslComputeStage.NAME_IN_LOCAL_INVOCATION_ID -> "gl_LocalInvocationID"
            KslComputeStage.NAME_IN_WORK_GROUP_ID -> "gl_WorkGroupID"
            KslComputeStage.NAME_IN_NUM_WORK_GROUPS -> "gl_NumWorkGroups"
            KslComputeStage.NAME_IN_WORK_GROUP_SIZE -> "gl_WorkGroupSize"

            else -> stateName
        }
    }

    protected fun glslTypeName(type: KslType): String {
        return when (type) {
            KslTypeVoid -> "void"
            KslBool1 -> "bool"
            KslBool2 -> "bvec2"
            KslBool3 -> "bvec3"
            KslBool4 -> "bvec4"
            KslFloat1 -> "float"
            KslFloat2 -> "vec2"
            KslFloat3 -> "vec3"
            KslFloat4 -> "vec4"
            KslInt1 -> "int"
            KslInt2 -> "ivec2"
            KslInt3 -> "ivec3"
            KslInt4 -> "ivec4"
            KslUint1 -> "uint"
            KslUint2 -> "uvec2"
            KslUint3 -> "uvec3"
            KslUint4 -> "uvec4"
            KslMat2 -> "mat2"
            KslMat3 -> "mat3"
            KslMat4 -> "mat4"

            KslColorSampler1d -> "sampler2D"    // in WebGL2, 1d textures are not supported, simply use 2d instead (with height = 1px)
            KslColorSampler2d -> "sampler2D"
            KslColorSampler3d -> "sampler3D"
            KslColorSamplerCube -> "samplerCube"
            KslColorSampler2dArray -> "sampler2DArray"
            KslColorSamplerCubeArray -> "samplerCubeArray"

            KslDepthSampler2D -> "sampler2DShadow"
            KslDepthSamplerCube -> "samplerCubeShadow"
            KslDepthSampler2DArray -> "sampler2DArrayShadow"
            KslDepthSamplerCubeArray -> "samplerCubeArrayShadow"

            is KslArrayType<*> -> "${glslTypeName(type.elemType)}[${type.arraySize}]"

            is KslStorage1dType<*> -> "${type.typePrefix}image1D"
            is KslStorage2dType<*> -> "${type.typePrefix}image2D"
            is KslStorage3dType<*> -> "${type.typePrefix}image3D"
        }
    }

    private val KslStorageType<*, *>.typePrefix: String
        get() = when (elemType) {
            is KslFloatType -> ""
            is KslInt1 -> "i"
            is KslInt2 -> "i"
            is KslInt3 -> "i"
            is KslInt4 -> "i"
            is KslUint1 -> "u"
            is KslUint2 -> "u"
            is KslUint3 -> "u"
            is KslUint4 -> "u"
        }

    private val KslStorageType<*, *>.formatQualifier: String
        get() = when (elemType) {
            is KslFloat1 -> "r32f"
            is KslFloat2 -> "rg32f"
            is KslFloat3 -> "rgb32f"
            is KslFloat4 -> "rgba32f"
            is KslInt1 -> "r32i"
            is KslInt2 -> "rg32i"
            is KslInt3 -> "rgb32i"
            is KslInt4 -> "rgba32i"
            is KslUint1 -> "r32ui"
            is KslUint2 -> "rg32ui"
            is KslUint3 -> "rgb32ui"
            is KslUint4 -> "rgba32ui"
            else -> throw IllegalStateException("Invalid storage element type $elemType")
        }

    data class Hints(
        val glslVersionStr: String,
        val alwaysGenerateStd140Layout: Boolean = true,
        val replaceUbosByPlainUniforms: Boolean = false
    )

    class GlslGeneratorOutput : GeneratedSourceOutput() {
        companion object {
            fun shaderOutput(vertexSrc: String, fragmentSrc: String) = GlslGeneratorOutput().apply {
                stages[KslShaderStageType.VertexShader] = vertexSrc
                stages[KslShaderStageType.FragmentShader] = fragmentSrc
            }

            fun computeOutput(computeSrc: String) = GlslGeneratorOutput().apply {
                stages[KslShaderStageType.ComputeShader] = computeSrc
            }
        }
    }
}