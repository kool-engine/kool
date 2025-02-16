package de.fabmax.kool.modules.ksl.generator

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslScope
import de.fabmax.kool.modules.ksl.model.KslState
import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.DrawPipeline

abstract class KslGenerator {

    abstract fun getStateName(stae: KslState): String

    open fun constBoolExpression(value: Boolean) = "$value"
    open fun constIntExpression(value: Int) = "$value"
    open fun constUintExpression(value: UInt) = "${value}u"
    open fun constFloatExpression(value: Float): String {
        var str = "$value"
        if (!str.contains('.')) {
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

    open fun valueExpression(value: KslValue<*>): String = getStateName(value)
    open fun arrayValueExpression(arrayAccessor: KslArrayAccessor<*>): String =
        "${arrayAccessor.array.generateExpression(this)}[${arrayAccessor.index.generateExpression(this)}]"
    open fun matrixColExpression(matrixAccessor: KslMatrixAccessor<*>): String =
        "${matrixAccessor.matrix.generateExpression(this)}[${matrixAccessor.colIndex.generateExpression(this)}]"
    open fun vectorSwizzleExpression(swizzleExpr: KslVectorAccessor<*>): String = "${swizzleExpr.vector.generateExpression(this)}.${swizzleExpr.components}"

    abstract fun castExpression(castExpr: KslExpressionCast<*>): String

    open fun <T: KslNumericType> mathExpression(expression: KslExpressionMath<T>): String =
        "(${expression.left.generateExpression(this)} ${expression.operator.opChar} ${expression.right.generateExpression(this)})"
    open fun <T: KslNumericType> bitExpression(expression: KslExpressionBit<T>): String =
        "(${expression.left.generateExpression(this)} ${expression.operator.opString} ${expression.right.generateExpression(this)})"
    open fun <B: KslBoolType> compareExpression(expression: KslExpressionCompare<B>): String =
        "(${expression.left.generateExpression(this)} ${expression.operator.opString} ${expression.right.generateExpression(this)})"
    open fun numericUnaryMinusExpression(expression: KslNumericScalarUnaryMinus<*>): String =
        "-(${expression.expr.generateExpression(this)})"
    open fun numericUnaryMinusExpression(expression: KslNumericVectorUnaryMinus<*, *>): String =
        "-(${expression.expr.generateExpression(this)})"
    open fun intComplementExpression(expression: KslIntScalarComplement<*>): String =
        "~(${expression.expr.generateExpression(this)})"
    open fun intComplementExpression(expression: KslIntVectorComplement<*, *>): String =
        "~(${expression.expr.generateExpression(this)})"

    open fun boolVecExpression(expression: KslBoolVectorExpr<*>): String =
        "${expression.op.opString}(${expression.boolVec.generateExpression(this)})"
    open fun boolScalarExpression(expression: KslBoolScalarExpr): String =
        "(${expression.left.generateExpression(this)} ${expression.op.opString} ${expression.right.generateExpression(this)})"
    open fun boolNotExpression(expression: KslBoolNotExpr): String =
        "!(${expression.expr.generateExpression(this)})"

    abstract fun sampleColorTexture(sampleTexture: KslSampleColorTexture<*>): String
    abstract fun sampleColorTextureGrad(sampleTextureGrad: KslSampleColorTextureGrad<*>): String
    abstract fun sampleDepthTexture(sampleTexture: KslSampleDepthTexture<*>): String
    abstract fun sampleColorTextureArray(sampleTexture: KslSampleColorTextureArray<*>): String
    abstract fun sampleColorTextureArrayGrad(sampleTextureGrad: KslSampleColorTextureArrayGrad<*>): String
    abstract fun sampleDepthTextureArray(sampleTexture: KslSampleDepthTextureArray<*>): String
    abstract fun textureSize(textureSize: KslTextureSize<*, *>): String
    abstract fun textureSize(textureSize: KslStorageTextureSize<*, *, *>): String

    abstract fun storageTextureRead(storageTextureRead: KslStorageTextureLoad<*, *, *>): String
    abstract fun imageTextureRead(expression: KslImageTextureLoad<*>): String

    abstract fun storageRead(storageRead: KslStorageRead<*, *, *>): String
    abstract fun storageAtomicOp(atomicOp: KslStorageAtomicOp<*, *, *>): String
    abstract fun storageAtomicCompareSwap(atomicCompSwap: KslStorageAtomicCompareSwap<*, *, *>): String

    open fun varAssignable(assignable: KslVar<*>): String = getStateName(assignable)
    open fun arrayValueAssignable(arrayAccessor: KslArrayAccessor<*>): String =
        "${arrayAccessor.array.generateExpression(this)}[${arrayAccessor.index.generateExpression(this)}]"
    open fun matrixColAssignable(matrixAccessor: KslMatrixAccessor<*>): String =
        "${matrixAccessor.matrix.generateExpression(this)}[${matrixAccessor.colIndex.generateExpression(this)}]"
    open fun vectorSwizzleAssignable(swizzleAssignable: KslVectorAccessor<*>): String =
        "${swizzleAssignable.vector.generateExpression(this)}.${swizzleAssignable.components}"

    abstract fun generateProgram(program: KslProgram, pipeline: DrawPipeline): GeneratorOutput
    abstract fun generateComputeProgram(program: KslProgram, pipeline: ComputePipeline): GeneratorOutput

    open fun generateScope(scope: KslScope, indent: String): String {
        return scope.ops.asSequence().map { generateOp(it).prependIndent(indent) }.joinToString("\n")
    }

    open fun generateOp(op: KslOp): String {
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
            is KslStorageWrite<*, *, *> -> opStorageWrite(op)
            is KslStorageTextureStore<*, *, *> -> opStorageTextureWrite(op)
            else -> throw IllegalArgumentException("Unsupported op: ${op.toPseudoCode()}")
        }
    }

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
    abstract fun opStorageWrite(op: KslStorageWrite<*, *, *>): String
    abstract fun opStorageTextureWrite(op: KslStorageTextureStore<*, *, *>): String

    abstract fun invokeFunction(func: KslInvokeFunction<*>): String

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

    protected fun sortFunctions(functions: MutableList<KslFunction<*>>) {
        val closed = mutableSetOf<KslFunction<*>>()
        val open = mutableSetOf<KslFunction<*>>()
        open += functions
        functions.clear()

        while (open.isNotEmpty()) {
            val next = open.find { it.functionDependencies.all { dep -> dep in closed } }
                ?: throw IllegalStateException("Unable to sort functions, circular dependencies?")
            open -= next
            closed += next
            functions += next
        }
    }

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