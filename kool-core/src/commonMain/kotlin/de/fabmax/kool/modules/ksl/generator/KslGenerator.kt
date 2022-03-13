package de.fabmax.kool.modules.ksl.generator

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslScope
import de.fabmax.kool.modules.ksl.model.KslState

abstract class KslGenerator {

    abstract fun KslState.name(): String

    open fun constBoolExpression(value: Boolean) = "$value"
    open fun constIntExpression(value: Int) = "$value"
    open fun constFloatExpression(value: Float): String {
        var str = "$value"
        if (!str.contains('.')) {
            // make sure string is a proper floating point value (not always the case with javascript)
            str += ".0"
        }
        return str
    }

    abstract fun constFloatVecExpression(vararg values: KslExpression<KslTypeFloat1>): String
    abstract fun constIntVecExpression(vararg values: KslExpression<KslTypeInt1>): String
    abstract fun constBoolVecExpression(vararg values: KslExpression<KslTypeBool1>): String

    open fun valueExpression(value: KslValue<*>): String = value.name()
    open fun arrayValueExpression(arrayAccessor: KslArrayAccessor<*>): String =
        "${arrayAccessor.array.generateExpression(this)}[${arrayAccessor.index.generateExpression(this)}]"
    open fun vectorSwizzleExpression(swizzleExpr: KslVectorAccessor<*>): String = "${swizzleExpr.vector.generateExpression(this)}.${swizzleExpr.components}"

    abstract fun castExpression(castExpr: KslExpressionCast<*>): String

    open fun <T: KslNumericType> mathExpression(expression: KslExpressionMath<T>): String =
        "(${expression.left.generateExpression(this)} ${expression.operator.opChar} ${expression.right.generateExpression(this)})"
    open fun <B: KslBoolType> compareExpression(expression: KslExpressionCompare<B>): String =
        "(${expression.left.generateExpression(this)} ${expression.operator.opString} ${expression.right.generateExpression(this)})"
    open fun numericUnaryMinusExpression(expression: KslNumericScalarUnaryMinus<*>): String =
        "-(${expression.expr.generateExpression(this)})"
    open fun numericUnaryMinusExpression(expression: KslNumericVectorUnaryMinus<*, *>): String =
        "-(${expression.expr.generateExpression(this)})"

    open fun boolVecExpression(expression: KslBoolVectorExpr<*>): String =
        "${expression.op.opString}(${expression.boolVec.generateExpression(this)})"
    open fun boolScalarExpression(expression: KslBoolScalarExpr): String =
        "(${expression.left.generateExpression(this)} ${expression.op.opString} ${expression.right.generateExpression(this)})"
    open fun boolNotExpression(expression: KslBoolNotExpr): String =
        "!(${expression.expr.generateExpression(this)})"

    abstract fun sampleColorTexture(sampleTexture: KslSampleColorTexture<*>): String

    open fun varAssignable(assignable: KslVar<*>): String = assignable.name()
    open fun arrayValueAssignable(arrayAccessor: KslArrayAccessor<*>): String =
        "${arrayAccessor.array.generateExpression(this)}[${arrayAccessor.index.generateExpression(this)}]"
    open fun vectorSwizzleAssignable(swizzleAssignable: KslVectorAccessor<*>): String =
        "${swizzleAssignable.vector.generateExpression(this)}.${swizzleAssignable.components}"

    abstract fun generateProgram(program: KslProgram): GeneratorOutput

    open fun generateScope(scope: KslScope, indent: String): String {
        return scope.ops.asSequence().map { generateOp(it).prependIndent(indent) }.joinToString("\n")
    }

    open fun generateOp(op: KslOp): String {
        return when (op) {
            is KslDeclareVar -> opDeclare(op)
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
            else -> throw IllegalArgumentException("Unsupported op: ${op.toPseudoCode()}")
        }
    }

    abstract fun opDeclare(op: KslDeclareVar): String
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

    abstract fun invokeFunction(func: KslInvokeFunction<*>): String

    abstract fun builtinClamp(func: KslBuiltinClampScalar<*>): String
    abstract fun builtinClamp(func: KslBuiltinClampVector<*, *>): String
    abstract fun builtinCos(func: KslBuiltinCosScalar<*>): String
    abstract fun builtinCos(func: KslBuiltinCosVector<*, *>): String
    abstract fun builtinDot(func: KslBuiltinDot<*, *>): String
    abstract fun builtinLength(func: KslBuiltinLength<*, *>): String
    abstract fun builtinMax(func: KslBuiltinMaxScalar<*>): String
    abstract fun builtinMax(func: KslBuiltinMaxVector<*, *>): String
    abstract fun builtinMin(func: KslBuiltinMinScalar<*>): String
    abstract fun builtinMin(func: KslBuiltinMinVector<*, *>): String
    abstract fun builtinMix(func: KslBuiltinMixScalar): String
    abstract fun builtinMix(func: KslBuiltinMixVector<*, *>): String
    abstract fun builtinNormalize(func: KslBuiltinNormalize<*, *>): String
    abstract fun builtinPow(func: KslBuiltinPowScalar): String
    abstract fun builtinPow(func: KslBuiltinPowVector<*, *>): String
    abstract fun builtinReflect(func: KslBuiltinReflect<*, *>): String
    abstract fun builtinSin(func: KslBuiltinSinScalar<*>): String
    abstract fun builtinSin(func: KslBuiltinSinVector<*, *>): String
    abstract fun builtinSmoothStep(func: KslBuiltinSmoothStepScalar<*>): String
    abstract fun builtinSmoothStep(func: KslBuiltinSmoothStepVector<*, *>): String

    interface GeneratorOutput
}