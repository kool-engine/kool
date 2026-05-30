package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.KslDomain
import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslScope
import kotlin.jvm.JvmName
import kotlin.reflect.KProperty

class KslScopeBuilder(
    parentOp: KslOp?,
    parentScope: KslScopeBuilder?,
    parentStage: KslShaderStage
) : KslScope(parentOp, parentScope, parentStage), KslDomain {

    inline fun <reified T: Any> findParentOpByType(): T? {
        var parent = parentOp
        while (parent !is T && parent != null) {
            parent = parent.parentScope.parentOp
        }
        return parent as? T
    }

    val isInLoop: Boolean
        get() = findParentOpByType<KslLoop>() != null

    val parentFunction: KslFunction<*>?
        get() = findParentOpByType<KslFunction<*>.FunctionRoot>()?.function

    val isInFunction: Boolean
        get() = parentFunction != null

    fun nextName(prefix: String): String = parentStage.program.nextName(prefix)

    fun getBlocks(name: String?, result: MutableList<KslBlock>): MutableList<KslBlock> {
        ops.forEach { op ->
            if (op is KslBlock && (name == null || op.opName == name)) {
                result += op
            }
            op.childScopes.asSequence().filterIsInstance<KslScopeBuilder>().forEach {
                it.getBlocks(name, result)
            }
        }
        return result
    }

    // todo: would be nice to move the delegate operators to context functions as well but that isn't supported yet
    @JvmName("provideDelegateFloat1")
    operator fun KslExprFloat1.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarFloat1 = float1Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateFloat2")
    operator fun KslExprFloat2.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarFloat2 = float2Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateFloat3")
    operator fun KslExprFloat3.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarFloat3 = float3Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateFloat4")
    operator fun KslExprFloat4.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarFloat4 = float4Var(this@provideDelegate, property.name)

    @JvmName("provideDelegateInt1")
    operator fun KslExprInt1.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarInt1 = int1Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateInt2")
    operator fun KslExprInt2.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarInt2 = int2Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateInt3")
    operator fun KslExprInt3.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarInt3 = int3Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateInt4")
    operator fun KslExprInt4.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarInt4 = int4Var(this@provideDelegate, property.name)

    @JvmName("provideDelegateUint1")
    operator fun KslExprUint1.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarUint1 = uint1Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateUint2")
    operator fun KslExprUint2.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarUint2 = uint2Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateUint3")
    operator fun KslExprUint3.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarUint3 = uint3Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateUint4")
    operator fun KslExprUint4.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarUint4 = uint4Var(this@provideDelegate, property.name)

    @JvmName("provideDelegateBool1")
    operator fun KslExprBool1.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarBool1 = bool1Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateBool2")
    operator fun KslExprBool2.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarBool2 = bool2Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateBool3")
    operator fun KslExprBool3.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarBool3 = bool3Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateBool4")
    operator fun KslExprBool4.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarBool4 = bool4Var(this@provideDelegate, property.name)

    @JvmName("provideDelegateMat2")
    operator fun KslExprMat2.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarMat2 = mat2Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateMat3")
    operator fun KslExprMat3.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarMat3 = mat3Var(this@provideDelegate, property.name)
    @JvmName("provideDelegateMat4")
    operator fun KslExprMat4.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarMat4 = mat4Var(this@provideDelegate, property.name)

    @JvmName("provideDelegateFloat1Array")
    operator fun KslExprFloat1Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarFloat1Array = float1Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateFloat2Array")
    operator fun KslExprFloat2Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarFloat2Array = float2Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateFloat3Array")
    operator fun KslExprFloat3Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarFloat3Array = float3Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateFloat4Array")
    operator fun KslExprFloat4Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarFloat4Array = float4Array(this@provideDelegate, property.name)

    @JvmName("provideDelegateInt1Array")
    operator fun KslExprInt1Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarInt1Array = int1Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateInt2Array")
    operator fun KslExprInt2Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarInt2Array = int2Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateInt3Array")
    operator fun KslExprInt3Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarInt3Array = int3Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateInt4Array")
    operator fun KslExprInt4Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarInt4Array = int4Array(this@provideDelegate, property.name)

    @JvmName("provideDelegateUint1Array")
    operator fun KslExprUint1Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarUint1Array = uint1Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateUint2Array")
    operator fun KslExprUint2Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarUint2Array = uint2Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateUint3Array")
    operator fun KslExprUint3Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarUint3Array = uint3Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateUint4Array")
    operator fun KslExprUint4Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarUint4Array = uint4Array(this@provideDelegate, property.name)

    @JvmName("provideDelegateBool1Array")
    operator fun KslExprBool1Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarBool1Array = bool1Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateBool2Array")
    operator fun KslExprBool2Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarBool2Array = bool2Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateBool3Array")
    operator fun KslExprBool3Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarBool3Array = bool3Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateBool4Array")
    operator fun KslExprBool4Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarBool4Array = bool4Array(this@provideDelegate, property.name)

    @JvmName("provideDelegateMat2Array")
    operator fun KslExprMat2Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarMat2Array = mat2Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateMat3Array")
    operator fun KslExprMat3Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarMat3Array = mat3Array(this@provideDelegate, property.name)
    @JvmName("provideDelegateMat4Array")
    operator fun KslExprMat4Array.provideDelegate(thisRef: Any?, property: KProperty<*>): KslVarMat4Array = mat4Array(this@provideDelegate, property.name)

    @JvmName("getValueFloat1")
    operator fun KslVarFloat1.getValue(thisRef: Any?, property: KProperty<*>?): KslVarFloat1 = this
    @JvmName("getValueFloat2")
    operator fun KslVarFloat2.getValue(thisRef: Any?, property: KProperty<*>?): KslVarFloat2 = this
    @JvmName("getValueFloat3")
    operator fun KslVarFloat3.getValue(thisRef: Any?, property: KProperty<*>?): KslVarFloat3 = this
    @JvmName("getValueFloat4")
    operator fun KslVarFloat4.getValue(thisRef: Any?, property: KProperty<*>?): KslVarFloat4 = this

    @JvmName("getValueInt1")
    operator fun KslVarInt1.getValue(thisRef: Any?, property: KProperty<*>?): KslVarInt1 = this
    @JvmName("getValueInt2")
    operator fun KslVarInt2.getValue(thisRef: Any?, property: KProperty<*>?): KslVarInt2 = this
    @JvmName("getValueInt3")
    operator fun KslVarInt3.getValue(thisRef: Any?, property: KProperty<*>?): KslVarInt3 = this
    @JvmName("getValueInt4")
    operator fun KslVarInt4.getValue(thisRef: Any?, property: KProperty<*>?): KslVarInt4 = this

    @JvmName("getValueUint1")
    operator fun KslVarUint1.getValue(thisRef: Any?, property: KProperty<*>?): KslVarUint1 = this
    @JvmName("getValueUint2")
    operator fun KslVarUint2.getValue(thisRef: Any?, property: KProperty<*>?): KslVarUint2 = this
    @JvmName("getValueUint3")
    operator fun KslVarUint3.getValue(thisRef: Any?, property: KProperty<*>?): KslVarUint3 = this
    @JvmName("getValueUint4")
    operator fun KslVarUint4.getValue(thisRef: Any?, property: KProperty<*>?): KslVarUint4 = this

    @JvmName("getValueBool1")
    operator fun KslVarBool1.getValue(thisRef: Any?, property: KProperty<*>?): KslVarBool1 = this
    @JvmName("getValueBool2")
    operator fun KslVarBool2.getValue(thisRef: Any?, property: KProperty<*>?): KslVarBool2 = this
    @JvmName("getValueBool3")
    operator fun KslVarBool3.getValue(thisRef: Any?, property: KProperty<*>?): KslVarBool3 = this
    @JvmName("getValueBool4")
    operator fun KslVarBool4.getValue(thisRef: Any?, property: KProperty<*>?): KslVarBool4 = this

    @JvmName("getValueMat2")
    operator fun KslVarMat2.getValue(thisRef: Any?, property: KProperty<*>?): KslVarMat2 = this
    @JvmName("getValueMat3")
    operator fun KslVarMat3.getValue(thisRef: Any?, property: KProperty<*>?): KslVarMat3 = this
    @JvmName("getValueMat4")
    operator fun KslVarMat4.getValue(thisRef: Any?, property: KProperty<*>?): KslVarMat4 = this

    @JvmName("getValueFloat1Array")
    operator fun KslVarFloat1Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarFloat1Array = this
    @JvmName("getValueFloat2Array")
    operator fun KslVarFloat2Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarFloat2Array = this
    @JvmName("getValueFloat3Array")
    operator fun KslVarFloat3Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarFloat3Array = this
    @JvmName("getValueFloat4Array")
    operator fun KslVarFloat4Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarFloat4Array = this

    @JvmName("getValueInt1Array")
    operator fun KslVarInt1Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarInt1Array = this
    @JvmName("getValueInt2Array")
    operator fun KslVarInt2Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarInt2Array = this
    @JvmName("getValueInt3Array")
    operator fun KslVarInt3Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarInt3Array = this
    @JvmName("getValueInt4Array")
    operator fun KslVarInt4Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarInt4Array = this

    @JvmName("getValueUint1Array")
    operator fun KslVarUint1Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarUint1Array = this
    @JvmName("getValueUint2Array")
    operator fun KslVarUint2Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarUint2Array = this
    @JvmName("getValueUint3Array")
    operator fun KslVarUint3Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarUint3Array = this
    @JvmName("getValueUint4Array")
    operator fun KslVarUint4Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarUint4Array = this

    @JvmName("getValueBool1Array")
    operator fun KslVarBool1Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarBool1Array = this
    @JvmName("getValueBool2Array")
    operator fun KslVarBool2Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarBool2Array = this
    @JvmName("getValueBool3Array")
    operator fun KslVarBool3Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarBool3Array = this
    @JvmName("getValueBool4Array")
    operator fun KslVarBool4Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarBool4Array = this

    @JvmName("getValueMat2Array")
    operator fun KslVarMat2Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarMat2Array = this
    @JvmName("getValueMat3Array")
    operator fun KslVarMat3Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarMat3Array = this
    @JvmName("getValueMat4Array")
    operator fun KslVarMat4Array.getValue(thisRef: Any?, property: KProperty<*>?): KslVarMat4Array = this
}

context(b: KslScopeBuilder)
infix fun <T: KslType> KslAssignable<T>.set(expression: KslExpression<T>) {
    b.ops += KslAssign(this, expression, b)
}

context(b: KslScopeBuilder)
fun `if`(condition: KslExpression<KslBool1>, block: KslScopeBuilder.() -> Unit): KslIf {
    val stmt = KslIf(condition, b).apply { body.block() }
    b.ops += stmt
    return stmt
}

context(b: KslScopeBuilder)
fun fori(fromInclusive: KslScalarExpression<KslInt1>, toExclusive: KslScalarExpression<KslInt1>,
         block: KslScopeBuilder.(KslScalarExpression<KslInt1>) -> Unit) {
    val i = int1Var(fromInclusive)
    `for`(i, i lt toExclusive, 1.const, block)
}

context(b: KslScopeBuilder)
fun <T> `for`(loopVar: KslVarScalar<T>, whileExpr: KslScalarExpression<KslBool1>,
              incExpr: KslScalarExpression<T>, block: KslScopeBuilder.(KslScalarExpression<T>) -> Unit)
        where T: KslNumericType, T: KslScalar {
    val loop = KslLoopFor(loopVar, whileExpr, incExpr, b).apply { body.block(loopVar) }
    b.ops += loop
}

context(b: KslScopeBuilder)
fun repeat(times: KslScalarExpression<KslInt1>, block: KslScopeBuilder.(KslScalarExpression<KslInt1>) -> Unit) {
    val i = int1Var(0.const)
    `for`(i, i lt times, 1.const, block)
}

context(b: KslScopeBuilder)
fun `while`(whileExpr: KslScalarExpression<KslBool1>, block: KslScopeBuilder.() -> Unit) {
    val loop = KslLoopWhile(whileExpr, b).apply { body.block() }
    b.ops += loop
}

context(b: KslScopeBuilder)
fun `break`() {
    b.ops += KslLoopBreak(b)
}

context(b: KslScopeBuilder)
fun `continue`() {
    b.ops += KslLoopContinue(b)
}

context(b: KslScopeBuilder)
fun discard() {
    b.ops += KslDiscard(b)
}

context(b: KslScopeBuilder)
operator fun <T: KslType> KslAssignable<T>.plusAssign(expr: KslExpression<T>) {
    b.ops += KslAugmentedAssign(this, KslMathOperator.Plus, expr, b)
}

context(b: KslScopeBuilder)
operator fun <T: KslType> KslAssignable<T>.minusAssign(expr: KslExpression<T>) {
    b.ops += KslAugmentedAssign(this, KslMathOperator.Minus, expr, b)
}

context(b: KslScopeBuilder)
operator fun <T: KslType> KslAssignable<T>.timesAssign(expr: KslExpression<T>) {
    b.ops += KslAugmentedAssign(this, KslMathOperator.Times, expr, b)
}

context(b: KslScopeBuilder)
operator fun <T: KslType> KslAssignable<T>.divAssign(expr: KslExpression<T>) {
    b.ops += KslAugmentedAssign(this, KslMathOperator.Divide, expr, b)
}

context(b: KslScopeBuilder)
operator fun <T: KslType> KslAssignable<T>.remAssign(expr: KslExpression<T>) {
    b.ops += KslAugmentedAssign(this, KslMathOperator.Remainder, expr, b)
}

/**
 * Inserts the given raw string into the generated shader code. Use with care, as it isn't checked in any way and
 * doesn't support different shading languages.
 */
context(b: KslScopeBuilder)
fun inlineCode(code: String): KslInlineCode {
    val op = KslInlineCode(code, b)
    b.ops += op
    return op
}
