package de.fabmax.kool.modules.ksl.generator

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslScope
import de.fabmax.kool.modules.ksl.model.KslState
import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.DrawPipeline

abstract class KslGenerator(val generatorExpressions: Map<KslExpression<*>, KslExpression<*>>) {

    abstract fun generateProgram(program: KslProgram, pipeline: DrawPipeline): GeneratorOutput
    abstract fun generateComputeProgram(program: KslProgram, pipeline: ComputePipeline): GeneratorOutput

    fun generateScope(scope: KslScope, indent: String): String {
        return generateOps(scope.ops, indent)
    }

    fun generateOps(ops: List<KslOp>, indent: String): String {
        return ops.joinToString("\n") { generateOp(it).prependIndent(indent) }
    }

    fun generateOp(op: KslOp): String {
        return when (op) {
            is KslDeclareVar -> opDeclareVar(op)
            is KslDeclareArray -> opDeclareArray(op)
            is KslAssign<*> -> opAssign(op)
            is KslAugmentedAssign<*> -> opAugmentedAssign(op)
            is KslIf -> opIf(op)
            is KslLoopFor<*> -> opFor(op)
            is KslLoopWhile -> opWhile(op)
            is KslLoopDoWhile -> opDoWhile(op)
            is KslLoopBreak -> opBreak(op)
            is KslLoopContinue -> opContinue(op)
            is KslDiscard -> opDiscard(op)
            is KslReturn -> opReturn(op)
            is KslBlock -> opBlock(op)
            is KslInlineCode -> opInlineCode(op)
            is KslStorageWrite<*, *> -> opStorageWrite(op)
            is KslStorageTextureStore<*, *, *> -> opStorageTextureWrite(op)
            is KslFunction<*>.FunctionRoot -> opFunctionBody(op)
            else -> throw IllegalArgumentException("Unsupported op: ${op.toPseudoCode()}")
        }
    }

    abstract fun opFunctionBody(op: KslFunction<*>.FunctionRoot): String
    abstract fun opDeclareVar(op: KslDeclareVar): String
    abstract fun opDeclareArray(op: KslDeclareArray): String
    abstract fun opAssign(op: KslAssign<*>): String
    abstract fun opAugmentedAssign(op: KslAugmentedAssign<*>): String
    abstract fun opIf(op: KslIf): String
    abstract fun opFor(op: KslLoopFor<*>): String
    abstract fun opWhile(op: KslLoopWhile): String
    abstract fun opDoWhile(op: KslLoopDoWhile): String
    abstract fun opBreak(op: KslLoopBreak): String
    abstract fun opContinue(op: KslLoopContinue): String
    abstract fun opDiscard(op: KslDiscard): String
    abstract fun opReturn(op: KslReturn): String
    abstract fun opBlock(op: KslBlock): String
    abstract fun opInlineCode(op: KslInlineCode): String
    abstract fun opStorageWrite(op: KslStorageWrite<*, *>): String
    abstract fun opStorageTextureWrite(op: KslStorageTextureStore<*, *, *>): String

    fun KslExpression<*>.generateExpression(): String {
        val expr = (this as? KslInjectedExpression)?.expr ?: generatorExpressions.getOrElse(this) { this }
        return when (expr) {
            is KslArrayAccessor<*> -> generateArrayValueExpression(expr)
            is KslBlock.BlockInput<*, *> -> (expr.input as KslExpression<*>).generateExpression()
            is KslBuiltinFunction<*> -> generateBuiltinFunctionExpression(expr)
            is KslExpressionBit<*> -> generateBitExpression(expr)
            is KslExpressionCast<*> -> generateCastExpression(expr)
            is KslExpressionCompare<*> -> compareExpression(expr)
            is KslExpressionMath<*> -> generateMathExpression(expr)
            is KslInvokeFunction<*> -> generateInvokeFunction(expr)
            is KslVectorAccessor -> generateVectorSwizzleExpression(expr)
            is KslMatrixAccessor<*> -> generateMatrixColExpression(expr)
            is KslValue<*> -> generateValueExpression(expr)
            is KslValueExpression<*> -> generateValueExpression(expr)
            is KslUniform<*> -> generateValueExpression(expr.value)
            is KslVertexAttribute<*> -> generateValueExpression(expr.value)
            is KslStageInput<*> -> generateValueExpression(expr.value)
            is KslStageOutput<*> -> generateValueExpression(expr.value)
            is KslStructMemberExpression -> structMemberExpression(expr)
            is KslNumericScalarUnaryMinus<*> -> numericUnaryMinusExpression(expr)
            is KslNumericVectorUnaryMinus<*, *> -> numericUnaryMinusExpression(expr)
            is KslIntScalarComplement -> intComplementExpression(expr)
            is KslIntVectorComplement<*, *> -> intComplementExpression(expr)
            is KslBoolNotExpr -> boolNotExpression(expr)
            is KslBoolScalarExpr -> boolScalarExpression(expr)
            is KslBoolVectorExpr<*> -> boolVecExpression(expr)
            is KslSampleColorTexture<*> -> sampleColorTexture(expr)
            is KslSampleColorTextureGrad<*> -> sampleColorTextureGrad(expr)
            is KslSampleColorTextureArray<*> -> sampleColorTextureArray(expr)
            is KslSampleColorTextureArrayGrad<*> -> sampleColorTextureArrayGrad(expr)
            is KslSampleDepthTexture<*> -> sampleDepthTexture(expr)
            is KslSampleDepthTextureArray<*> -> sampleDepthTextureArray(expr)
            is KslImageTextureLoad<*> -> imageTextureRead(expr)
            is KslStorageTextureLoad<*, *, *> -> storageTextureRead(expr)
            is KslStorageRead<*, *> -> generateStorageRead(expr)
            is KslStorageTextureSize<*, *, *> -> generateTextureSize(expr)
            is KslTextureSize<*, *> -> generateTextureSize(expr)
            is KslStorageAtomicOp<*, *> -> storageAtomicOp(expr)
            is KslStorageAtomicCompareSwap<*, *> -> storageAtomicCompareSwap(expr)
            is KslExpressionBitcast -> generateBitcastExpression(expr)
            else -> error("expression type not implemented: $expr")
        }
    }

    abstract fun getStateName(state: KslState): String

    open fun varAssignable(assignable: KslVar<*>): String = getStateName(assignable)
    open fun structMemberAssignable(structMember: KslStructMemberExpression<*>): String = structMember.generateExpression()
    open fun arrayValueAssignable(arrayAccessor: KslArrayAccessor<*>): String =
        "${arrayAccessor.array.generateExpression()}[${arrayAccessor.index.generateExpression()}]"
    open fun matrixColAssignable(matrixAccessor: KslMatrixAccessor<*>): String =
        "${matrixAccessor.matrix.generateExpression()}[${matrixAccessor.colIndex.generateExpression()}]"
    open fun vectorSwizzleAssignable(swizzleAssignable: KslVectorAccessor<*>): String =
        "${swizzleAssignable.vector.generateExpression()}.${swizzleAssignable.components}"

    abstract fun generateCastExpression(castExpr: KslExpressionCast<*>): String
    abstract fun generateBitcastExpression(expr: KslExpressionBitcast<*>): String

    open fun <T: KslNumericType> generateMathExpression(expression: KslExpressionMath<T>): String =
        "(${expression.left.generateExpression()} ${expression.operator.opChar} ${expression.right.generateExpression()})"
    open fun <T: KslNumericType> generateBitExpression(expression: KslExpressionBit<T>): String =
        "(${expression.left.generateExpression()} ${expression.operator.opString} ${expression.right.generateExpression()})"

    open fun generateValueExpression(value: KslValue<*>): String = getStateName(value)
    open fun generateArrayValueExpression(arrayAccessor: KslArrayAccessor<*>): String =
        "${arrayAccessor.array.generateExpression()}[${arrayAccessor.index.generateExpression()}]"
    open fun generateMatrixColExpression(matrixAccessor: KslMatrixAccessor<*>): String =
        "${matrixAccessor.matrix.generateExpression()}[${matrixAccessor.colIndex.generateExpression()}]"
    open fun generateVectorSwizzleExpression(swizzleExpr: KslVectorAccessor<*>): String = "${swizzleExpr.vector.generateExpression()}.${swizzleExpr.components}"

    open fun structMemberExpression(expression: KslStructMemberExpression<*>): String =
        "${expression.struct.generateExpression()}${expression.member.qualifiedName}"

    open fun <B: KslBoolType> compareExpression(expression: KslExpressionCompare<B>): String =
        "(${expression.left.generateExpression()} ${expression.operator.opString} ${expression.right.generateExpression()})"
    open fun numericUnaryMinusExpression(expression: KslNumericScalarUnaryMinus<*>): String =
        "-(${expression.expr.generateExpression()})"
    open fun numericUnaryMinusExpression(expression: KslNumericVectorUnaryMinus<*, *>): String =
        "-(${expression.expr.generateExpression()})"
    open fun intComplementExpression(expression: KslIntScalarComplement<*>): String =
        "~(${expression.expr.generateExpression()})"
    open fun intComplementExpression(expression: KslIntVectorComplement<*, *>): String =
        "~(${expression.expr.generateExpression()})"

    open fun boolVecExpression(expression: KslBoolVectorExpr<*>): String =
        "${expression.op.opString}(${expression.boolVec.generateExpression()})"
    open fun boolScalarExpression(expression: KslBoolScalarExpr): String =
        "(${expression.left.generateExpression()} ${expression.op.opString} ${expression.right.generateExpression()})"
    open fun boolNotExpression(expression: KslBoolNotExpr): String =
        "!(${expression.expr.generateExpression()})"

    private fun generateValueExpression(expr: KslValueExpression<*>): String {
        return when (expr) {
            is KslValueBool1 -> constBoolExpression(expr.value)
            is KslValueBool2 -> constBoolVecExpression(expr.x, expr.y)
            is KslValueBool3 -> constBoolVecExpression(expr.x, expr.y, expr.z)
            is KslValueBool4 -> constBoolVecExpression(expr.x, expr.y, expr.z, expr.w)
            is KslValueFloat1 -> constFloatExpression(expr.value)
            is KslValueFloat2 -> constFloatVecExpression(expr.x, expr.y)
            is KslValueFloat3 -> constFloatVecExpression(expr.x, expr.y, expr.z)
            is KslValueFloat4 -> constFloatVecExpression(expr.x, expr.y, expr.z, expr.w)
            is KslValueInt1 -> constIntExpression(expr.value)
            is KslValueInt2 -> constIntVecExpression(expr.x, expr.y)
            is KslValueInt3 -> constIntVecExpression(expr.x, expr.y, expr.z)
            is KslValueInt4 -> constIntVecExpression(expr.x, expr.y, expr.z, expr.w)
            is KslValueMat2 -> constMatExpression(expr.col0, expr.col1)
            is KslValueMat3 -> constMatExpression(expr.col0, expr.col1, expr.col2)
            is KslValueMat4 -> constMatExpression(expr.col0, expr.col1, expr.col2, expr.col3)
            is KslValueUint1 -> constUintExpression(expr.value)
            is KslValueUint2 -> constUintVecExpression(expr.x, expr.y)
            is KslValueUint3 -> constUintVecExpression(expr.x, expr.y, expr.z)
            is KslValueUint4 -> constUintVecExpression(expr.x, expr.y, expr.z, expr.w)
            KslValueVoid -> error("void has no value that could be generated")
        }
    }

    open fun constBoolExpression(value: Boolean) = "$value"
    open fun constIntExpression(value: Int) = "$value"
    open fun constUintExpression(value: UInt) = "${value}u"
    open fun constFloatExpression(value: Float): String {
        var str = "$value"
        if (!str.contains('.') && !str.lowercase().contains("e")) {
            // make sure string is a proper floating point value (not always the case with javascript)
            str += ".0"
        }
        return str
    }

    abstract fun constFloatVecExpression(vararg values: KslExpression<KslFloat1>): String
    abstract fun constIntVecExpression(vararg values: KslExpression<KslInt1>): String
    abstract fun constUintVecExpression(vararg values: KslExpression<KslUint1>): String
    abstract fun constBoolVecExpression(vararg values: KslExpression<KslBool1>): String
    abstract fun constMatExpression(vararg columns: KslVectorExpression<*, KslFloat1>): String

    private fun generateBuiltinFunctionExpression(expr: KslBuiltinFunction<*>): String {
        return when (expr) {
            is KslBuiltinTranspose<*, *> -> builtinTranspose(expr)
            is KslBuiltinAbsScalar<*> -> builtinAbs(expr)
            is KslBuiltinAtan2Scalar -> builtinAtan2(expr)
            is KslBuiltinCeilScalar -> builtinCeil(expr)
            is KslBuiltinClampScalar<*> -> builtinClamp(expr)
            is KslBuiltinDegreesScalar -> builtinDegrees(expr)
            is KslBuiltinDeterminant<*, *> -> builtinDeterminant(expr)
            is KslBuiltinDistanceScalar<*> -> builtinDistance(expr)
            is KslBuiltinDot<*> -> builtinDot(expr)
            is KslBuiltinDpdxScalar -> builtinDpdx(expr)
            is KslBuiltinDpdyScalar -> builtinDpdy(expr)
            is KslBuiltinExp2Scalar -> builtinExp2(expr)
            is KslBuiltinExpScalar -> builtinExp(expr)
            is KslBuiltinFloorScalar -> builtinFloor(expr)
            is KslBuiltinFmaScalar -> builtinFma(expr)
            is KslBuiltinFractScalar -> builtinFract(expr)
            is KslBuiltinInverseSqrtScalar -> builtinInverseSqrt(expr)
            is KslBuiltinIsInfScalar -> builtinIsInf(expr)
            is KslBuiltinIsNanScalar -> builtinIsNan(expr)
            is KslBuiltinLength<*> -> builtinLength(expr)
            is KslBuiltinLog2Scalar -> builtinLog2(expr)
            is KslBuiltinLogScalar -> builtinLog(expr)
            is KslBuiltinMaxScalar<*> -> builtinMax(expr)
            is KslBuiltinMinScalar<*> -> builtinMin(expr)
            is KslBuiltinMixScalar -> builtinMix(expr)
            is KslBuiltinPowScalar -> builtinPow(expr)
            is KslBuiltinRadiansScalar -> builtinRadians(expr)
            is KslBuiltinRoundScalar -> builtinRound(expr)
            is KslBuiltinSignScalar<*> -> builtinSign(expr)
            is KslBuiltinSmoothStepScalar -> builtinSmoothStep(expr)
            is KslBuiltinSqrtScalar -> builtinSqrt(expr)
            is KslBuiltinStepScalar -> builtinStep(expr)
            is KslBuiltinTrigonometryScalar -> builtinTrigonometry(expr)
            is KslBuiltinTruncScalar -> builtinTrunc(expr)
            is KslBuiltinAbsVector<*, *> -> builtinAbs(expr)
            is KslBuiltinAtan2Vector<*> -> builtinAtan2(expr)
            is KslBuiltinCeilVector<*> -> builtinCeil(expr)
            is KslBuiltinClampVector<*, *> -> builtinClamp(expr)
            is KslBuiltinCross -> builtinCross(expr)
            is KslBuiltinDegreesVector<*> -> builtinDegrees(expr)
            is KslBuiltinDpdxVector<*> -> builtinDpdx(expr)
            is KslBuiltinDpdyVector<*> -> builtinDpdy(expr)
            is KslBuiltinExp2Vector<*> -> builtinExp2(expr)
            is KslBuiltinExpVector<*> -> builtinExp(expr)
            is KslBuiltinFaceForward<*> -> builtinFaceForward(expr)
            is KslBuiltinFloorVector<*> -> builtinFloor(expr)
            is KslBuiltinFmaVector<*> -> builtinFma(expr)
            is KslBuiltinFractVector<*> -> builtinFract(expr)
            is KslBuiltinInverseSqrtVector<*> -> builtinInverseSqrt(expr)
            is KslBuiltinIsInfVector<*, *> -> builtinIsInf(expr)
            is KslBuiltinIsNanVector<*, *> -> builtinIsNan(expr)
            is KslBuiltinLog2Vector<*> -> builtinLog2(expr)
            is KslBuiltinLogVector<*> -> builtinLog(expr)
            is KslBuiltinMaxVector<*, *> -> builtinMax(expr)
            is KslBuiltinMinVector<*, *> -> builtinMin(expr)
            is KslBuiltinMixVector<*> -> builtinMix(expr)
            is KslBuiltinNormalize<*> -> builtinNormalize(expr)
            is KslBuiltinPowVector<*> -> builtinPow(expr)
            is KslBuiltinRadiansVector<*> -> builtinRadians(expr)
            is KslBuiltinReflect<*> -> builtinReflect(expr)
            is KslBuiltinRefract<*> -> builtinRefract(expr)
            is KslBuiltinRoundVector<*> -> builtinRound(expr)
            is KslBuiltinSignVector<*, *> -> builtinSign(expr)
            is KslBuiltinSmoothStepVector<*> -> builtinSmoothStep(expr)
            is KslBuiltinSqrtVector<*> -> builtinSqrt(expr)
            is KslBuiltinStepVector<*> -> builtinStep(expr)
            is KslBuiltinTrigonometryVector<*> -> builtinTrigonometry(expr)
            is KslBuiltinTruncVector<*> -> builtinTrunc(expr)
        }
    }

    abstract fun generateInvokeFunction(func: KslInvokeFunction<*>): String

    abstract fun builtinAbs(func: KslBuiltinAbsScalar<*>): String
    abstract fun builtinAbs(func: KslBuiltinAbsVector<*, *>): String
    abstract fun builtinAtan2(func: KslBuiltinAtan2Scalar): String
    abstract fun builtinAtan2(func: KslBuiltinAtan2Vector<*>): String
    abstract fun builtinCeil(func: KslBuiltinCeilScalar): String
    abstract fun builtinCeil(func: KslBuiltinCeilVector<*>): String
    abstract fun builtinClamp(func: KslBuiltinClampScalar<*>): String
    abstract fun builtinClamp(func: KslBuiltinClampVector<*, *>): String
    abstract fun builtinCross(func: KslBuiltinCross): String
    abstract fun builtinDegrees(func: KslBuiltinDegreesScalar): String
    abstract fun builtinDegrees(func: KslBuiltinDegreesVector<*>): String
    abstract fun builtinDistance(func: KslBuiltinDistanceScalar<*>): String
    abstract fun builtinDot(func: KslBuiltinDot<*>): String
    abstract fun builtinDpdx(func: KslBuiltinDpdxScalar): String
    abstract fun builtinDpdx(func: KslBuiltinDpdxVector<*>): String
    abstract fun builtinDpdy(func: KslBuiltinDpdyScalar): String
    abstract fun builtinDpdy(func: KslBuiltinDpdyVector<*>): String
    abstract fun builtinExp(func: KslBuiltinExpScalar): String
    abstract fun builtinExp(func: KslBuiltinExpVector<*>): String
    abstract fun builtinExp2(func: KslBuiltinExp2Scalar): String
    abstract fun builtinExp2(func: KslBuiltinExp2Vector<*>): String
    abstract fun builtinFaceForward(func: KslBuiltinFaceForward<*>): String
    abstract fun builtinFloor(func: KslBuiltinFloorScalar): String
    abstract fun builtinFloor(func: KslBuiltinFloorVector<*>): String
    abstract fun builtinFma(func: KslBuiltinFmaScalar): String
    abstract fun builtinFma(func: KslBuiltinFmaVector<*>): String
    abstract fun builtinFract(func: KslBuiltinFractScalar): String
    abstract fun builtinFract(func: KslBuiltinFractVector<*>): String
    abstract fun builtinInverseSqrt(func: KslBuiltinInverseSqrtScalar): String
    abstract fun builtinInverseSqrt(func: KslBuiltinInverseSqrtVector<*>): String
    abstract fun builtinIsInf(func: KslBuiltinIsInfScalar): String
    abstract fun builtinIsInf(func: KslBuiltinIsInfVector<*, *>): String
    abstract fun builtinIsNan(func: KslBuiltinIsNanScalar): String
    abstract fun builtinIsNan(func: KslBuiltinIsNanVector<*, *>): String
    abstract fun builtinLength(func: KslBuiltinLength<*>): String
    abstract fun builtinLog(func: KslBuiltinLogScalar): String
    abstract fun builtinLog(func: KslBuiltinLogVector<*>): String
    abstract fun builtinLog2(func: KslBuiltinLog2Scalar): String
    abstract fun builtinLog2(func: KslBuiltinLog2Vector<*>): String
    abstract fun builtinMax(func: KslBuiltinMaxScalar<*>): String
    abstract fun builtinMax(func: KslBuiltinMaxVector<*, *>): String
    abstract fun builtinMin(func: KslBuiltinMinScalar<*>): String
    abstract fun builtinMin(func: KslBuiltinMinVector<*, *>): String
    abstract fun builtinMix(func: KslBuiltinMixScalar): String
    abstract fun builtinMix(func: KslBuiltinMixVector<*>): String
    abstract fun builtinNormalize(func: KslBuiltinNormalize<*>): String
    abstract fun builtinPow(func: KslBuiltinPowScalar): String
    abstract fun builtinPow(func: KslBuiltinPowVector<*>): String
    abstract fun builtinRadians(func: KslBuiltinRadiansScalar): String
    abstract fun builtinRadians(func: KslBuiltinRadiansVector<*>): String
    abstract fun builtinReflect(func: KslBuiltinReflect<*>): String
    abstract fun builtinRefract(func: KslBuiltinRefract<*>): String
    abstract fun builtinRound(func: KslBuiltinRoundScalar): String
    abstract fun builtinRound(func: KslBuiltinRoundVector<*>): String
    abstract fun builtinSign(func: KslBuiltinSignScalar<*>): String
    abstract fun builtinSign(func: KslBuiltinSignVector<*, *>): String
    abstract fun builtinSmoothStep(func: KslBuiltinSmoothStepScalar): String
    abstract fun builtinSmoothStep(func: KslBuiltinSmoothStepVector<*>): String
    abstract fun builtinSqrt(func: KslBuiltinSqrtScalar): String
    abstract fun builtinSqrt(func: KslBuiltinSqrtVector<*>): String
    abstract fun builtinStep(func: KslBuiltinStepScalar): String
    abstract fun builtinStep(func: KslBuiltinStepVector<*>): String
    abstract fun builtinTrigonometry(func: KslBuiltinTrigonometryScalar): String
    abstract fun builtinTrigonometry(func: KslBuiltinTrigonometryVector<*>): String
    abstract fun builtinTrunc(func: KslBuiltinTruncScalar): String
    abstract fun builtinTrunc(func: KslBuiltinTruncVector<*>): String

    abstract fun builtinDeterminant(func: KslBuiltinDeterminant<*, *>): String
    abstract fun builtinTranspose(func: KslBuiltinTranspose<*, *>): String

    abstract fun sampleColorTexture(sampleTexture: KslSampleColorTexture<*>): String
    abstract fun sampleColorTextureGrad(sampleTextureGrad: KslSampleColorTextureGrad<*>): String
    abstract fun sampleDepthTexture(sampleTexture: KslSampleDepthTexture<*>): String
    abstract fun sampleColorTextureArray(sampleTexture: KslSampleColorTextureArray<*>): String
    abstract fun sampleColorTextureArrayGrad(sampleTextureGrad: KslSampleColorTextureArrayGrad<*>): String
    abstract fun sampleDepthTextureArray(sampleTexture: KslSampleDepthTextureArray<*>): String
    abstract fun generateTextureSize(textureSize: KslTextureSize<*, *>): String
    abstract fun generateTextureSize(textureSize: KslStorageTextureSize<*, *, *>): String

    abstract fun storageTextureRead(storageTextureRead: KslStorageTextureLoad<*, *, *>): String
    abstract fun imageTextureRead(expression: KslImageTextureLoad<*>): String

    abstract fun generateStorageRead(storageRead: KslStorageRead<*, *>): String
    abstract fun storageAtomicOp(atomicOp: KslStorageAtomicOp<*, *>): String
    abstract fun storageAtomicCompareSwap(atomicCompSwap: KslStorageAtomicCompareSwap<*, *>): String

    interface GeneratorOutput

    abstract class GeneratedSourceOutput : GeneratorOutput {
        val stages = mutableMapOf<KslShaderStageType, String>()

        val hasVertexSource: Boolean
            get() = KslShaderStageType.VertexShader in stages
        val hasFragmentSource: Boolean
            get() = KslShaderStageType.FragmentShader in stages
        val hasComputeSource: Boolean
            get() = KslShaderStageType.ComputeShader in stages

        val vertexSrc: String get() = checkNotNull(stages[KslShaderStageType.VertexShader]) {
            "Vertex shader source not defined"
        }

        val fragmentSrc: String get() = checkNotNull(stages[KslShaderStageType.FragmentShader]) {
            "Fragment shader source not defined"
        }

        val computeSrc: String get() = checkNotNull(stages[KslShaderStageType.ComputeShader]) {
            "Compute shader source not defined"
        }

        private fun linePrefix(line: Int): String {
            var num = "$line"
            while (num.length < 3) {
                num = " $num"
            }
            return "$num  "
        }

        fun dump() {
            stages.forEach { (type, src) ->
                println("### $type source:")
                src.lines().forEachIndexed { i, line -> println("${linePrefix(i+1)}${line}") }
            }
        }
    }
}