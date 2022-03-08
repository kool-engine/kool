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

    open fun boolVecExpression(expression: KslBoolVectorExpr<*>): String =
        "${expression.op.opString}(${expression.boolVec.generateExpression(this)})"
    open fun boolScalarExpression(expression: KslBoolScalarExpr): String =
        "(${expression.left.generateExpression(this)} ${expression.op.opString} ${expression.right.generateExpression(this)})"
    open fun boolNotExpression(expression: KslBoolNotExpr): String =
        "!${expression.expr.generateExpression(this)}"

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
            is KslDeclareValue -> opDeclare(op)
            is KslAssign<*> -> opAssign(op)
            is KslAugmentedAssign<*> -> opAugmentedAssign(op)
            is KslIf -> opIf(op)
            is KslBlock -> opBlock(op)
            else -> throw IllegalArgumentException("Unsupported op: ${op.toPseudoCode()}")
        }
    }

    abstract fun opDeclare(op: KslDeclareValue): String
    abstract fun opAssign(op: KslAssign<*>): String
    abstract fun opAugmentedAssign(op: KslAugmentedAssign<*>): String
    abstract fun opIf(op: KslIf): String
    abstract fun opBlock(op: KslBlock): String

    interface GeneratorOutput
}