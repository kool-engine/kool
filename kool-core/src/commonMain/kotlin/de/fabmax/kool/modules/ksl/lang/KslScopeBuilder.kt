package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslScope

class KslScopeBuilder(parentOp: KslOp?, val parentScope: KslScopeBuilder?, val parentStage: KslShaderStage) : KslScope(parentOp) {

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

    val Float.const: KslConstFloat1
        get() = KslConstFloat1(this)
    val Int.const: KslConstInt1
        get() = KslConstInt1(this)
    val Boolean.const: KslConstBool1
        get() = KslConstBool1(this)

    val Vec2f.const: KslConstFloat2
        get() = constFloat2(x, y)
    val Vec3f.const: KslConstFloat3
        get() = constFloat3(x, y, z)
    val Vec4f.const: KslConstFloat4
        get() = constFloat4(x, y, z, w)

    val Vec2i.const: KslConstInt2
        get() = constInt2(x, y)
    val Vec3i.const: KslConstInt3
        get() = constInt3(x, y, z)
    val Vec4i.const: KslConstInt4
        get() = constInt4(x, y, z, w)

    fun constFloat2(x: Float, y: Float) = KslConstFloat2(x, y)
    fun constFloat2(x: KslExpression<KslTypeFloat1>, y: KslExpression<KslTypeFloat1>) = KslConstFloat2(x, y)

    fun constFloat3(x: Float, y: Float, z: Float) = KslConstFloat3(x, y, z)
    fun constFloat3(x: KslExpression<KslTypeFloat1>, y: KslExpression<KslTypeFloat1>, z: KslExpression<KslTypeFloat1>) =
        KslConstFloat3(x, y, z)

    fun constFloat4(x: Float, y: Float, z: Float, w: Float) = KslConstFloat4(x, y, z, w)
    fun constFloat4(x: KslExpression<KslTypeFloat1>, y: KslExpression<KslTypeFloat1>, z: KslExpression<KslTypeFloat1>, w: KslExpression<KslTypeFloat1>) =
        KslConstFloat4(x, y, z, w)
    fun constFloat4(xyz: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>, w: Float) =
        constFloat4(xyz, w.const)
    fun constFloat4(xyz: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>, w: KslExpression<KslTypeFloat1>) =
        KslConstFloat4(xyz.x, xyz.y, xyz.z, w)

    fun constInt2(x: Int, y: Int) = KslConstInt2(x, y)
    fun constInt2(x: KslExpression<KslTypeInt1>, y: KslExpression<KslTypeInt1>) = KslConstInt2(x, y)

    fun constInt3(x: Int, y: Int, z: Int) = KslConstInt3(x, y, z)
    fun constInt3(x: KslExpression<KslTypeInt1>, y: KslExpression<KslTypeInt1>, z: KslExpression<KslTypeInt1>) =
        KslConstInt3(x, y, z)

    fun constInt4(x: Int, y: Int, z: Int, w: Int) = KslConstInt4(x, y, z, w)
    fun constInt4(x: KslExpression<KslTypeInt1>, y: KslExpression<KslTypeInt1>, z: KslExpression<KslTypeInt1>, w: KslExpression<KslTypeInt1>) =
        KslConstInt4(x, y, z, w)
    fun constInt4(xyz: KslVectorExpression<KslTypeInt3, KslTypeInt1>, w: Int) =
        constInt4(xyz, w.const)
    fun constInt4(xyz: KslVectorExpression<KslTypeInt3, KslTypeInt1>, w: KslExpression<KslTypeInt1>) =
        KslConstInt4(xyz.x, xyz.y, xyz.z, w)

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

    fun mat2Var(name: String? = null) = KslVarMatrix(name ?: nextName("m2"), KslTypeMat2, true).also { definedStates += it }
    fun mat3Var(name: String? = null) = KslVarMatrix(name ?: nextName("m3"), KslTypeMat3, true).also { definedStates += it }
    fun mat4Var(name: String? = null) = KslVarMatrix(name ?: nextName("m4"), KslTypeMat4, true).also { definedStates += it }

    fun <T: KslTypeColorSampler<C>, C: KslFloatType> sampleTexture(sampler: KslExpression<T>, coord: KslExpression<C>) =
        KslSampleColorTexture(sampler, coord)

    infix fun <T: KslType> KslAssignable<T>.set(expression: KslExpression<T>) {
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

    operator fun <T: KslType> KslAssignable<T>.plusAssign(expr: KslExpression<T>) {
        ops += KslAugmentedAssign(this, KslMathOperator.Plus, expr, this@KslScopeBuilder)
    }

    operator fun <T: KslType> KslAssignable<T>.minusAssign(expr: KslExpression<T>) {
        ops += KslAugmentedAssign(this, KslMathOperator.Minus, expr, this@KslScopeBuilder)
    }

    operator fun <T: KslType> KslAssignable<T>.timesAssign(expr: KslExpression<T>) {
        ops += KslAugmentedAssign(this, KslMathOperator.Times, expr, this@KslScopeBuilder)
    }

    operator fun <T: KslType> KslAssignable<T>.divAssign(expr: KslExpression<T>) {
        ops += KslAugmentedAssign(this, KslMathOperator.Divide, expr, this@KslScopeBuilder)
    }

    operator fun <T: KslType> KslAssignable<T>.remAssign(expr: KslExpression<T>) {
        ops += KslAugmentedAssign(this, KslMathOperator.Remainder, expr, this@KslScopeBuilder)
    }
}