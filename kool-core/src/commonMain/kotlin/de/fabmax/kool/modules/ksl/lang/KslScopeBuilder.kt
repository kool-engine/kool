package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslScope

class KslScopeBuilder(parentOp: KslOp?, parentScope: KslScopeBuilder?, val parentStage: KslShaderStage) : KslScope(parentOp) {

    fun nextName(prefix: String): String = parentStage.program.nextName(prefix)

    val Float.const: KslConstFloat1
        get() = KslConstFloat1(this)
    val Int.const: KslConstInt1
        get() = KslConstInt1(this)
    val Boolean.const: KslConstBool1
        get() = KslConstBool1(this)

    fun constFloat2(x: Float, y: Float) = KslConstFloat2(x.const, y.const)
    fun constFloat2(x: KslExpression<KslTypeFloat1>, y: KslExpression<KslTypeFloat1>) = KslConstFloat2(x, y)

    fun constFloat3(x: Float, y: Float, z: Float) = KslConstFloat3(x.const, y.const, z.const)
    fun constFloat3(x: KslExpression<KslTypeFloat1>, y: KslExpression<KslTypeFloat1>, z: KslExpression<KslTypeFloat1>) =
        KslConstFloat3(x, y, z)

    fun constFloat4(x: Float, y: Float, z: Float, w: Float) = KslConstFloat4(x.const, y.const, z.const, w. const)
    fun constFloat4(x: KslExpression<KslTypeFloat1>, y: KslExpression<KslTypeFloat1>, z: KslExpression<KslTypeFloat1>, w: KslExpression<KslTypeFloat1>) =
        KslConstFloat4(x, y, z, w)

    fun floatVar(name: String? = null) = KslVarScalar(name ?: nextName("f1"), KslTypeFloat1, true).also { definedStates += it }
    fun float2Var(name: String? = null) = KslVarVector(name ?: nextName("f2"), KslTypeFloat2, true).also { definedStates += it }
    fun float3Var(name: String? = null) = KslVarVector(name ?: nextName("f3"), KslTypeFloat3, true).also { definedStates += it }
    fun float4Var(name: String? = null) = KslVarVector(name ?: nextName("f4"), KslTypeFloat4, true).also { definedStates += it }
    fun floatArray(arraySize: Int, name: String? = null) = floatArray(arraySize.const, name)
    fun floatArray(arraySize: KslExpression<KslTypeInt1>, name: String?) =
        KslArrayScalar(name ?: nextName("floatArray"), KslTypeFloat1, arraySize, true).also { definedStates += it }

    fun intVar(name: String? = null) = KslVarScalar(name ?: nextName("i1"), KslTypeInt1, true).also { definedStates += it }
    fun int2Var(name: String? = null) = KslVarVector(name ?: nextName("i2"), KslTypeInt2, true).also { definedStates += it }
    fun int3Var(name: String? = null) = KslVarVector(name ?: nextName("i3"), KslTypeInt3, true).also { definedStates += it }
    fun int4Var(name: String? = null) = KslVarVector(name ?: nextName("i4"), KslTypeInt4, true).also { definedStates += it }

    infix fun <T: KslType> KslAssignable<T>.`=`(expression: KslExpression<T>) {
        ops += KslAssign(this, expression, this@KslScopeBuilder)
    }

    fun `if`(condition: KslExpression<KslTypeBool1>, block: KslScopeBuilder.() -> Unit): KslIf {
        val stmt = KslIf(condition, this).apply { body.block() }
        ops += stmt
        return stmt
    }

    fun <T> any(boolVec: KslVectorExpression<T, KslTypeBool1>) where T: KslBoolType, T: KslVector<KslTypeBool1> =
        KslBoolVectorExpr(boolVec, KslBoolVecOperator.Any)
    fun <T> all(boolVec: KslVectorExpression<T, KslTypeBool1>) where T: KslBoolType, T: KslVector<KslTypeBool1> =
        KslBoolVectorExpr(boolVec, KslBoolVecOperator.All)
}