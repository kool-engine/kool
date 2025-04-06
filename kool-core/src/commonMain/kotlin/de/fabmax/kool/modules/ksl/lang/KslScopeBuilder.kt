package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslScope
import de.fabmax.kool.pipeline.backend.NdcYDirection
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Struct
import kotlin.jvm.JvmName

class KslScopeBuilder(parentOp: KslOp?, parentScope: KslScopeBuilder?, parentStage: KslShaderStage) :
    KslScope(parentOp, parentScope, parentStage)
{

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

    internal fun copyFrom(source: KslScope) {
        dependencies.clear()
        dependencies.putAll(source.dependencies)
        mutations.clear()
        mutations.putAll(source.mutations)
        definedStates.clear()
        definedStates.addAll(source.definedStates)
        ops.addAll(source.ops)
    }

    val Double.const: KslValueFloat1
        get() = KslValueFloat1(this.toFloat())
    val Float.const: KslValueFloat1
        get() = KslValueFloat1(this)
    val Float.const2: KslValueFloat2
        get() = KslValueFloat2(this, this)
    val Float.const3: KslValueFloat3
        get() = KslValueFloat3(this, this, this)
    val Float.const4: KslValueFloat4
        get() = KslValueFloat4(this, this, this, this)

    val Int.const: KslValueInt1
        get() = KslValueInt1(this)
    val Int.const2: KslValueInt2
        get() = KslValueInt2(this, this)
    val Int.const3: KslValueInt3
        get() = KslValueInt3(this, this, this)
    val Int.const4: KslValueInt4
        get() = KslValueInt4(this, this, this, this)

    val UInt.const: KslValueUint1
        get() = KslValueUint1(this)
    val UInt.const2: KslValueUint2
        get() = KslValueUint2(this, this)
    val UInt.const3: KslValueUint3
        get() = KslValueUint3(this, this, this)
    val UInt.const4: KslValueUint4
        get() = KslValueUint4(this, this, this, this)
    val Int.uconst: KslValueUint1
        get() = KslValueUint1(this.toUInt())
    val Int.uconst2: KslValueUint2
        get() = KslValueUint2(this.toUInt(), this.toUInt())
    val Int.uconst3: KslValueUint3
        get() = KslValueUint3(this.toUInt(), this.toUInt(), this.toUInt())
    val Int.uconst4: KslValueUint4
        get() = KslValueUint4(this.toUInt(), this.toUInt(), this.toUInt(), this.toUInt())

    val Boolean.const: KslValueBool1
        get() = KslValueBool1(this)
    val Boolean.const2: KslValueBool2
        get() = KslValueBool2(this, this)
    val Boolean.const3: KslValueBool3
        get() = KslValueBool3(this, this, this)
    val Boolean.const4: KslValueBool4
        get() = KslValueBool4(this, this, this, this)

    val Vec2f.const: KslValueFloat2
        get() = float2Value(x, y)
    val Vec3f.const: KslValueFloat3
        get() = float3Value(x, y, z)
    val Vec4f.const: KslValueFloat4
        get() = float4Value(x, y, z, w)
    val Mat3f.const: KslValueMat3
        get() = mat3Value(get(0).const, get(1).const, get(2).const)
    val Mat4f.const: KslValueMat4
        get() = mat4Value(get(0).const, get(1).const, get(2).const, get(3).const)
    val Color.const: KslValueFloat4
        get() = float4Value(r, g, b, a)
    val QuatF.const: KslValueFloat4
        get() = float4Value(x, y, z, w)

    val Vec2i.const: KslValueInt2
        get() = int2Value(x, y)
    val Vec3i.const: KslValueInt3
        get() = int3Value(x, y, z)
    val Vec4i.const: KslValueInt4
        get() = int4Value(x, y, z, w)

    fun float2Value(x: Float, y: Float) = KslValueFloat2(x, y)
    fun float2Value(x: KslExprFloat1, y: KslExprFloat1) = KslValueFloat2(x, y)

    fun float3Value(x: Float, y: Float, z: Float) = KslValueFloat3(x, y, z)
    fun float3Value(x: KslExprFloat1, y: KslExprFloat1, z: KslExprFloat1) = KslValueFloat3(x, y, z)
    fun float3Value(xy: KslExprFloat2, z: KslExprFloat1) = KslValueFloat3(xy.x, xy.y, z)

    fun float4Value(x: Float, y: Float, z: Float, w: Float) = KslValueFloat4(x, y, z, w)
    fun float4Value(x: KslExprFloat1, y: KslExprFloat1, z: KslExprFloat1, w: KslExprFloat1) = KslValueFloat4(x, y, z, w)
    fun float4Value(xy: KslExprFloat2, z: Float, w: Float) = float4Value(xy, z.const, w.const)
    fun float4Value(xy: KslExprFloat2, z: KslExprFloat1, w: KslExprFloat1) = KslValueFloat4(xy.x, xy.y, z, w)
    fun float4Value(xy: KslExprFloat2, zw: KslExprFloat2) = KslValueFloat4(xy.x, xy.y, zw.x, zw.y)
    fun float4Value(xyz: KslExprFloat3, w: Float) = float4Value(xyz, w.const)
    fun float4Value(xyz: KslExprFloat3, w: KslExprFloat1) = KslValueFloat4(xyz.x, xyz.y, xyz.z, w)

    fun int2Value(x: Int, y: Int) = KslValueInt2(x, y)
    fun int2Value(x: KslExprInt1, y: KslExprInt1) = KslValueInt2(x, y)

    fun int3Value(x: Int, y: Int, z: Int) = KslValueInt3(x, y, z)
    fun int3Value(x: KslExprInt1, y: KslExprInt1, z: KslExprInt1) = KslValueInt3(x, y, z)
    fun int3Value(xy: KslExprInt2, z: KslExprInt1) = KslValueInt3(xy.x, xy.y, z)

    fun int4Value(x: Int, y: Int, z: Int, w: Int) = KslValueInt4(x, y, z, w)
    fun int4Value(x: KslExprInt1, y: KslExprInt1, z: KslExprInt1, w: KslExprInt1) = KslValueInt4(x, y, z, w)
    fun int4Value(xy: KslExprInt2, z: Int, w: Int) = int4Value(xy, z.const, w.const)
    fun int4Value(xy: KslExprInt2, z: KslExprInt1, w: KslExprInt1) = KslValueInt4(xy.x, xy.y, z, w)
    fun int4Value(xy: KslExprInt2, zw: KslExprInt2) = KslValueInt4(xy.x, xy.y, zw.x, zw.y)
    fun int4Value(xyz: KslExprInt3, w: Int) = int4Value(xyz, w.const)
    fun int4Value(xyz: KslExprInt3, w: KslExprInt1) = KslValueInt4(xyz.x, xyz.y, xyz.z, w)

    fun uint2Value(x: UInt, y: UInt) = KslValueUint2(x, y)
    fun uint2Value(x: KslExprUint1, y: KslExprUint1) = KslValueUint2(x, y)

    fun uint3Value(x: UInt, y: UInt, z: UInt) = KslValueUint3(x, y, z)
    fun uint3Value(x: KslExprUint1, y: KslExprUint1, z: KslExprUint1) = KslValueUint3(x, y, z)
    fun uint3Value(xy: KslExprUint2, z: KslExprUint1) = KslValueUint3(xy.x, xy.y, z)

    fun uint4Value(x: UInt, y: UInt, z: UInt, w: UInt) = KslValueUint4(x, y, z, w)
    fun uint4Value(x: KslExprUint1, y: KslExprUint1, z: KslExprUint1, w: KslExprUint1) = KslValueUint4(x, y, z, w)
    fun uint4Value(xy: KslExprUint2, z: UInt, w: UInt) = uint4Value(xy, z.const, w.const)
    fun uint4Value(xy: KslExprUint2, z: KslExprUint1, w: KslExprUint1) = KslValueUint4(xy.x, xy.y, z, w)
    fun uint4Value(xy: KslExprUint2, zw: KslExprUint2) = KslValueUint4(xy.x, xy.y, zw.x, zw.y)
    fun uint4Value(xyz: KslExprUint3, w: UInt) = uint4Value(xyz, w.const)
    fun uint4Value(xyz: KslExprUint3, w: KslExprUint1) = KslValueUint4(xyz.x, xyz.y, xyz.z, w)

    fun bool2Value(x: Boolean, y: Boolean) = KslValueBool2(x, y)
    fun bool2Value(x: KslExprBool1, y: KslExprBool1) = KslValueBool2(x, y)

    fun bool3Value(x: Boolean, y: Boolean, z: Boolean) = KslValueBool3(x, y, z)
    fun bool3Value(x: KslExprBool1, y: KslExprBool1, z: KslExprBool1) = KslValueBool3(x, y, z)

    fun bool4Value(x: Boolean, y: Boolean, z: Boolean, w: Boolean) = KslValueBool4(x, y, z, w)
    fun bool4Value(x: KslExprBool1, y: KslExprBool1, z: KslExprBool1, w: KslExprBool1) = KslValueBool4(x, y, z, w)

    fun mat2Value(col0: KslExprFloat2, col1: KslExprFloat2) = KslValueMat2(col0, col1)
    fun mat3Value(col0: KslExprFloat3, col1: KslExprFloat3, col2: KslExprFloat3) = KslValueMat3(col0, col1, col2)
    fun mat4Value(col0: KslExprFloat4, col1: KslExprFloat4, col2: KslExprFloat4, col3: KslExprFloat4) = KslValueMat4(col0, col1, col2, col3)


    fun float2Var(x: Float, y: Float) = float2Var(float2Value(x, y))
    fun float2Var(x: KslExprFloat1, y: KslExprFloat1) = float2Var(float2Value(x, y))

    fun float3Var(x: Float, y: Float, z: Float) = float3Var(float3Value(x, y, z))
    fun float3Var(x: KslExprFloat1, y: KslExprFloat1, z: KslExprFloat1) = float3Var(float3Value(x, y, z))
    fun float3Var(xy: KslExprFloat2, z: KslExprFloat1) = float3Var(float3Value(xy.x, xy.y, z))

    fun float4Var(x: Float, y: Float, z: Float, w: Float) = float4Var(float4Value(x, y, z, w))
    fun float4Var(x: KslExprFloat1, y: KslExprFloat1, z: KslExprFloat1, w: KslExprFloat1) = float4Var(float4Value(x, y, z, w))
    fun float4Var(xy: KslExprFloat2, z: Float, w: Float) = float4Var(float4Value(xy, z.const, w.const))
    fun float4Var(xy: KslExprFloat2, z: KslExprFloat1, w: KslExprFloat1) = float4Var(float4Value(xy.x, xy.y, z, w))
    fun float4Var(xy: KslExprFloat2, zw: KslExprFloat2) = float4Var(float4Value(xy.x, xy.y, zw.x, zw.y))
    fun float4Var(xyz: KslExprFloat3, w: Float) = float4Var(float4Value(xyz, w.const))
    fun float4Var(xyz: KslExprFloat3, w: KslExprFloat1) = float4Var(float4Value(xyz.x, xyz.y, xyz.z, w))

    fun int2Var(x: Int, y: Int) = int2Var(int2Value(x, y))
    fun int2Var(x: KslExprInt1, y: KslExprInt1) = int2Var(int2Value(x, y))

    fun int3Var(x: Int, y: Int, z: Int) = int3Var(int3Value(x, y, z))
    fun int3Var(x: KslExprInt1, y: KslExprInt1, z: KslExprInt1) = int3Var(int3Value(x, y, z))
    fun int3Var(xy: KslExprInt2, z: KslExprInt1) = int3Var(int3Value(xy.x, xy.y, z))

    fun int4Var(x: Int, y: Int, z: Int, w: Int) = int4Var(int4Value(x, y, z, w))
    fun int4Var(x: KslExprInt1, y: KslExprInt1, z: KslExprInt1, w: KslExprInt1) = int4Var(int4Value(x, y, z, w))
    fun int4Var(xy: KslExprInt2, z: Int, w: Int) = int4Var(int4Value(xy, z.const, w.const))
    fun int4Var(xy: KslExprInt2, z: KslExprInt1, w: KslExprInt1) = int4Var(int4Value(xy.x, xy.y, z, w))
    fun int4Var(xy: KslExprInt2, zw: KslExprInt2) = int4Var(int4Value(xy.x, xy.y, zw.x, zw.y))
    fun int4Var(xyz: KslExprInt3, w: Int) = int4Var(int4Value(xyz, w.const))
    fun int4Var(xyz: KslExprInt3, w: KslExprInt1) = int4Var(int4Value(xyz.x, xyz.y, xyz.z, w))

    fun uint2Var(x: UInt, y: UInt) = uint2Var(uint2Value(x, y))
    fun uint2Var(x: KslExprUint1, y: KslExprUint1) = uint2Var(uint2Value(x, y))

    fun uint3Var(x: UInt, y: UInt, z: UInt) = uint3Var(uint3Value(x, y, z))
    fun uint3Var(x: KslExprUint1, y: KslExprUint1, z: KslExprUint1) = uint3Var(uint3Value(x, y, z))
    fun uint3Var(xy: KslExprUint2, z: KslExprUint1) = uint3Var(uint3Value(xy.x, xy.y, z))

    fun uint4Var(x: UInt, y: UInt, z: UInt, w: UInt) = uint4Var(uint4Value(x, y, z, w))
    fun uint4Var(x: KslExprUint1, y: KslExprUint1, z: KslExprUint1, w: KslExprUint1) = uint4Var(uint4Value(x, y, z, w))
    fun uint4Var(xy: KslExprUint2, z: UInt, w: UInt) = uint4Var(uint4Value(xy, z.const, w.const))
    fun uint4Var(xy: KslExprUint2, z: KslExprUint1, w: KslExprUint1) = uint4Var(uint4Value(xy.x, xy.y, z, w))
    fun uint4Var(xy: KslExprUint2, zw: KslExprUint2) = uint4Var(uint4Value(xy.x, xy.y, zw.x, zw.y))
    fun uint4Var(xyz: KslExprUint3, w: UInt) = uint4Var(uint4Value(xyz, w.const))
    fun uint4Var(xyz: KslExprUint3, w: KslExprUint1) = uint4Var(uint4Value(xyz.x, xyz.y, xyz.z, w))

    fun bool2Var(x: Boolean, y: Boolean) = bool2Var(bool2Value(x, y))
    fun bool2Var(x: KslExprBool1, y: KslExprBool1) = bool2Var(bool2Value(x, y))

    fun bool3Var(x: Boolean, y: Boolean, z: Boolean) = bool3Var(bool3Value(x, y, z))
    fun bool3Var(x: KslExprBool1, y: KslExprBool1, z: KslExprBool1) = bool3Var(bool3Value(x, y, z))

    fun bool4Var(x: Boolean, y: Boolean, z: Boolean, w: Boolean) = bool4Var(bool4Value(x, y, z, w))
    fun bool4Var(x: KslExprBool1, y: KslExprBool1, z: KslExprBool1, w: KslExprBool1) = bool4Var(bool4Value(x, y, z, w))

    fun mat2Var(col0: KslExprFloat2, col1: KslExprFloat2) = mat2Var(mat2Value(col0, col1))
    fun mat3Var(col0: KslExprFloat3, col1: KslExprFloat3, col2: KslExprFloat3) = mat3Var(mat3Value(col0, col1, col2))
    fun mat4Var(col0: KslExprFloat4, col1: KslExprFloat4, col2: KslExprFloat4, col3: KslExprFloat4) = mat4Var(mat4Value(col0, col1, col2, col3))

    fun float1Var(initValue: KslExpression<KslFloat1>? = null, name: String? = null) =
        KslVarScalar(name ?: nextName("f1"), KslFloat1, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun float2Var(initValue: KslExpression<KslFloat2>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("f2"), KslFloat2, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun float3Var(initValue: KslExpression<KslFloat3>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("f3"), KslFloat3, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun float4Var(initValue: KslExpression<KslFloat4>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("f4"), KslFloat4, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }


    fun int1Var(initValue: KslExpression<KslInt1>? = null, name: String? = null) =
        KslVarScalar(name ?: nextName("i1"), KslInt1, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun int2Var(initValue: KslExpression<KslInt2>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("i2"), KslInt2, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun int3Var(initValue: KslExpression<KslInt3>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("i3"), KslInt3, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun int4Var(initValue: KslExpression<KslInt4>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("i4"), KslInt4, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }


    fun uint1Var(initValue: KslExpression<KslUint1>? = null, name: String? = null) =
        KslVarScalar(name ?: nextName("u1"), KslUint1, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun uint2Var(initValue: KslExpression<KslUint2>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("u2"), KslUint2, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun uint3Var(initValue: KslExpression<KslUint3>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("u3"), KslUint3, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun uint4Var(initValue: KslExpression<KslUint4>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("u4"), KslUint4, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }


    fun bool1Var(initValue: KslExpression<KslBool1>? = null, name: String? = null) =
        KslVarScalar(name ?: nextName("b1"), KslBool1, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun bool2Var(initValue: KslExpression<KslBool2>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("b2"), KslInt2, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun bool3Var(initValue: KslExpression<KslBool3>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("b3"), KslInt3, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun bool4Var(initValue: KslExpression<KslBool4>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("b4"), KslInt4, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }


    fun mat2Var(initValue: KslExpression<KslMat2>? = null, name: String? = null) =
        KslVarMatrix(name ?: nextName("m2"), KslMat2, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun mat3Var(initValue: KslExpression<KslMat3>? = null, name: String? = null) =
        KslVarMatrix(name ?: nextName("m3"), KslMat3, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun mat4Var(initValue: KslExpression<KslMat4>? = null, name: String? = null) =
        KslVarMatrix(name ?: nextName("m4"), KslMat4, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }


    fun <S: Struct> structVar(initValue: KslExprStruct<S>, name: String? = null) =
        KslVarStruct(name ?: nextName("struct"), initValue.expressionType, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }

    fun <S: Struct> structVar(structType: KslStruct<S>, name: String? = null) =
        KslVarStruct(name ?: nextName("struct"), structType, true).also {
            ops += KslDeclareVar(it, null, this)
        }


    fun float1Array(arraySize: Int, initExpr: KslExprFloat1, name: String? = null) =
        KslArrayScalar(name ?: nextName("f1Array"), KslFloat1, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun float2Array(arraySize: Int, initExpr: KslExprFloat2, name: String? = null) =
        KslArrayVector(name ?: nextName("f2Array"), KslFloat2, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun float3Array(arraySize: Int, initExpr: KslExprFloat3, name: String? = null) =
        KslArrayVector(name ?: nextName("f3Array"), KslFloat3, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun float4Array(arraySize: Int, initExpr: KslExprFloat4, name: String? = null) =
        KslArrayVector(name ?: nextName("f4Array"), KslFloat4, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }

    fun float1Array(initExpr: KslExprFloat1Array, name: String? = null) =
        KslArrayScalar(name ?: nextName("f1Array"), KslFloat1, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun float2Array(initExpr: KslExprFloat2Array, name: String? = null) =
        KslArrayVector(name ?: nextName("f2Array"), KslFloat2, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun float3Array(initExpr: KslExprFloat3Array, name: String? = null) =
        KslArrayVector(name ?: nextName("f3Array"), KslFloat3, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun float4Array(initExpr: KslExprFloat4Array, name: String? = null) =
        KslArrayVector(name ?: nextName("f4Array"), KslFloat4, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }


    fun int1Array(arraySize: Int, initExpr: KslExprInt1, name: String? = null) =
        KslArrayScalar(name ?: nextName("i1Array"), KslInt1, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun int2Array(arraySize: Int, initExpr: KslExprInt2, name: String? = null) =
        KslArrayVector(name ?: nextName("i2Array"), KslInt2, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun int3Array(arraySize: Int, initExpr: KslExprInt3, name: String? = null) =
        KslArrayVector(name ?: nextName("i3Array"), KslInt3, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun int4Array(arraySize: Int, initExpr: KslExprInt4, name: String? = null) =
        KslArrayVector(name ?: nextName("i4Array"), KslInt4, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }

    fun int1Array(initExpr: KslExprInt1Array, name: String? = null) =
        KslArrayScalar(name ?: nextName("i1Array"), KslInt1, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun int2Array(initExpr: KslExprInt2Array, name: String? = null) =
        KslArrayVector(name ?: nextName("i2Array"), KslInt2, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun int3Array(initExpr: KslExprInt3Array, name: String? = null) =
        KslArrayVector(name ?: nextName("i3Array"), KslInt3, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun int4Array(initExpr: KslExprInt4Array, name: String? = null) =
        KslArrayVector(name ?: nextName("i4Array"), KslInt4, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }


    fun uint1Array(arraySize: Int, initExpr: KslExprUint1, name: String? = null) =
        KslArrayScalar(name ?: nextName("u1Array"), KslInt1, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun uint2Array(arraySize: Int, initExpr: KslExprUint2, name: String? = null) =
        KslArrayVector(name ?: nextName("u2Array"), KslInt2, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun uint3Array(arraySize: Int, initExpr: KslExprUint3, name: String? = null) =
        KslArrayVector(name ?: nextName("u3Array"), KslInt3, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun uint4Array(arraySize: Int, initExpr: KslExprUint4, name: String? = null) =
        KslArrayVector(name ?: nextName("u4Array"), KslInt4, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }

    fun uint1Array(initExpr: KslExprUint1Array, name: String? = null) =
        KslArrayScalar(name ?: nextName("u1Array"), KslInt1, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun uint2Array(initExpr: KslExprUint2Array, name: String? = null) =
        KslArrayVector(name ?: nextName("u2Array"), KslInt2, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun uint3Array(initExpr: KslExprUint3Array, name: String? = null) =
        KslArrayVector(name ?: nextName("u3Array"), KslInt3, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun uint4Array(initExpr: KslExprUint4Array, name: String? = null) =
        KslArrayVector(name ?: nextName("u4Array"), KslInt4, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }


    fun bool1Array(arraySize: Int, initExpr: KslExprBool1, name: String? = null) =
        KslArrayScalar(name ?: nextName("b1Array"), KslBool1, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun bool2Array(arraySize: Int, initExpr: KslExprBool2, name: String? = null) =
        KslArrayVector(name ?: nextName("b2Array"), KslBool2, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun bool3Array(arraySize: Int, initExpr: KslExprBool3, name: String? = null) =
        KslArrayVector(name ?: nextName("b3Array"), KslBool3, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun bool4Array(arraySize: Int, initExpr: KslExprBool4, name: String? = null) =
        KslArrayVector(name ?: nextName("b4Array"), KslBool4, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }

    fun bool1Array(initExpr: KslExprBool1Array, name: String? = null) =
        KslArrayScalar(name ?: nextName("b1Array"), KslBool1, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun bool2Array(initExpr: KslExprBool2Array, name: String? = null) =
        KslArrayVector(name ?: nextName("b2Array"), KslBool2, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun bool3Array(initExpr: KslExprBool3Array, name: String? = null) =
        KslArrayVector(name ?: nextName("b3Array"), KslBool3, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun bool4Array(initExpr: KslExprBool4Array, name: String? = null) =
        KslArrayVector(name ?: nextName("b4Array"), KslBool4, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }


    fun mat2Array(arraySize: Int, initExpr: KslExprMat2, name: String? = null) =
        KslArrayMatrix(name ?: nextName("i2Array"), KslMat2, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun mat3Array(arraySize: Int, initExpr: KslExprMat3, name: String? = null) =
        KslArrayMatrix(name ?: nextName("i3Array"), KslMat3, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun mat4Array(arraySize: Int, initExpr: KslExprMat4, name: String? = null) =
        KslArrayMatrix(name ?: nextName("i4Array"), KslMat4, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }

    fun mat2Array(initExpr: KslExprMat2Array, name: String? = null) =
        KslArrayMatrix(name ?: nextName("i2Array"), KslMat2, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun mat3Array(initExpr: KslExprMat3Array, name: String? = null) =
        KslArrayMatrix(name ?: nextName("i3Array"), KslMat3, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun mat4Array(initExpr: KslExprMat4Array, name: String? = null) =
        KslArrayMatrix(name ?: nextName("i4Array"), KslMat4, initExpr.expressionType.arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }

    fun KslVarVector<KslFloat2, KslFloat1>.flipUvByDeviceCoords(onClipY: NdcYDirection = NdcYDirection.TOP_TO_BOTTOM) {
        if (KoolSystem.requireContext().backend.deviceCoordinates.ndcYDirection == onClipY) {
            y set 1f.const - y
        }
    }

    infix fun <T: KslType> KslAssignable<T>.set(expression: KslExpression<T>) {
        ops += KslAssign(this, expression, this@KslScopeBuilder)
    }

    fun `if`(condition: KslExpression<KslBool1>, block: KslScopeBuilder.() -> Unit): KslIf {
        val stmt = KslIf(condition, this).apply { body.block() }
        ops += stmt
        return stmt
    }

    fun fori(fromInclusive: KslScalarExpression<KslInt1>, toExclusive: KslScalarExpression<KslInt1>,
             block: KslScopeBuilder.(KslScalarExpression<KslInt1>) -> Unit) {
        val i = int1Var(fromInclusive)
        `for`(i, i lt toExclusive, 1.const, block)
    }

    fun <T> `for`(loopVar: KslVarScalar<T>, whileExpr: KslScalarExpression<KslBool1>,
                  incExpr: KslScalarExpression<T>, block: KslScopeBuilder.(KslScalarExpression<T>) -> Unit)
            where T: KslNumericType, T: KslScalar {
        val loop = KslLoopFor(loopVar, whileExpr, incExpr, this).apply { body.block(loopVar) }
        ops += loop
    }

    fun repeat(times: KslScalarExpression<KslInt1>, block: KslScopeBuilder.(KslScalarExpression<KslInt1>) -> Unit) {
        val i = int1Var(0.const)
        `for`(i, i lt times, 1.const, block)
    }

    fun `while`(whileExpr: KslScalarExpression<KslBool1>, block: KslScopeBuilder.() -> Unit) {
        val loop = KslLoopWhile(whileExpr, this).apply { body.block() }
        ops += loop
    }

    fun `break`() {
        ops += KslLoopBreak(this)
    }

    fun `continue`() {
        ops += KslLoopContinue(this)
    }

    fun discard() {
        ops += KslDiscard(this)
    }

    fun <T> any(boolVec: KslVectorExpression<T, KslBool1>) where T: KslBoolType, T: KslVector<KslBool1> =
        KslBoolVectorExpr(boolVec, KslBoolVecOperator.Any)
    fun <T> all(boolVec: KslVectorExpression<T, KslBool1>) where T: KslBoolType, T: KslVector<KslBool1> =
        KslBoolVectorExpr(boolVec, KslBoolVecOperator.All)
    fun <T> none(boolVec: KslVectorExpression<T, KslBool1>) where T: KslBoolType, T: KslVector<KslBool1> =
        KslBoolVectorExpr(boolVec, KslBoolVecOperator.None)

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

    fun inlineCode(code: String): KslInlineCode {
        val op = KslInlineCode(code, this)
        ops += op
        return op
    }

    // function invocation
    operator fun <S> KslFunction<S>.invoke(vararg args: KslExpression<*>): KslScalarExpression<S> where S: KslType, S: KslScalar {
        return KslInvokeFunctionScalar(this, this@KslScopeBuilder, returnType, *args)
    }
    operator fun <V, S> KslFunction<V>.invoke(vararg args: KslExpression<*>): KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar {
        return KslInvokeFunctionVector(this, this@KslScopeBuilder, returnType, *args)
    }
    operator fun <M, V> KslFunction<M>.invoke(vararg args: KslExpression<*>): KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*> {
        return KslInvokeFunctionMatrix(this, this@KslScopeBuilder, returnType, *args)
    }
    operator fun <S> KslFunction<KslStruct<S>>.invoke(vararg args: KslExpression<*>): KslExprStruct<S> where S: Struct {
        return KslInvokeFunctionStruct(this, this@KslScopeBuilder, returnType, *args)
    }
    operator fun <S> KslFunction<KslArrayType<S>>.invoke(vararg args: KslExpression<*>): KslScalarArrayExpression<S> where S: KslType, S: KslScalar {
        return KslInvokeFunctionScalarArray(this, this@KslScopeBuilder, returnType, *args)
    }
    operator fun <V, S> KslFunction<KslArrayType<V>>.invoke(vararg args: KslExpression<*>): KslVectorArrayExpression<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar {
        return KslInvokeFunctionVectorArray(this, this@KslScopeBuilder, returnType, *args)
    }
    operator fun <M, V> KslFunction<KslArrayType<M>>.invoke(vararg args: KslExpression<*>): KslMatrixArrayExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*> {
        return KslInvokeFunctionMatrixArray(this, this@KslScopeBuilder, returnType, *args)
    }
    @JvmName("invokeVoid")
    operator fun KslFunction<KslTypeVoid>.invoke(vararg args: KslExpression<*>): KslExpression<KslTypeVoid> {
        return KslInvokeFunctionVoid(this, this@KslScopeBuilder, *args)
    }

    // builtin general functions
    fun <S> abs(value: KslScalarExpression<S>) where S: KslNumericType, S: KslScalar = KslBuiltinAbsScalar(value)
    fun <V, S> abs(vec: KslVectorExpression<V, S>)
            where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslBuiltinAbsVector(vec)

    fun atan2(y: KslScalarExpression<KslFloat1>, x: KslScalarExpression<KslFloat1>) = KslBuiltinAtan2Scalar(y, x)
    fun <V> atan2(y: KslVectorExpression<V, KslFloat1>, x: KslVectorExpression<V, KslFloat1>)
            where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinAtan2Vector(y, x)

    fun ceil(value: KslScalarExpression<KslFloat1>) = KslBuiltinCeilScalar(value)
    fun <V> ceil(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinCeilVector(vec)

    fun <S> clamp(value: KslScalarExpression<S>, min: KslScalarExpression<S>, max: KslScalarExpression<S>)
        where S: KslNumericType, S: KslScalar = KslBuiltinClampScalar(value, min, max)
    fun <V, S> clamp(vec: KslVectorExpression<V, S>, min: KslVectorExpression<V, S>, max: KslVectorExpression<V, S>)
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslBuiltinClampVector(vec, min, max)

    fun saturate(value: KslExprFloat1) = clamp(value, 0f.const, 1f.const)
    @JvmName("saturate2f")
    fun saturate(value: KslExprFloat2) = clamp(value, Vec2f.ZERO.const, Vec2f.ONES.const)
    @JvmName("saturate3f")
    fun saturate(value: KslExprFloat3) = clamp(value, Vec3f.ZERO.const, Vec3f.ONES.const)
    @JvmName("saturate4f")
    fun saturate(value: KslExprFloat4) = clamp(value, Vec4f.ZERO.const, Vec4f.ONES.const)

    fun cross(a: KslVectorExpression<KslFloat3, KslFloat1>, b: KslVectorExpression<KslFloat3, KslFloat1>) = KslBuiltinCross(a, b)

    fun degrees(value: KslScalarExpression<KslFloat1>) = KslBuiltinDegreesScalar(value)
    fun <V> degrees(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinDegreesVector(vec)

    fun <T: KslFloatType> distance(a: KslExpression<T>, b: KslExpression<T>) = KslBuiltinDistanceScalar(a, b)

    fun <V> dot(a: KslVectorExpression<V, KslFloat1>, b: KslVectorExpression<V, KslFloat1>)
        where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinDot(a, b)

    fun dpdx(value: KslScalarExpression<KslFloat1>) = KslBuiltinDpdxScalar(value)
    fun <V> dpdx(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinDpdxVector(vec)
    fun dpdy(value: KslScalarExpression<KslFloat1>) = KslBuiltinDpdyScalar(value)
    fun <V> dpdy(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinDpdyVector(vec)

    fun exp(value: KslScalarExpression<KslFloat1>) = KslBuiltinExpScalar(value)
    fun <V> exp(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinExpVector(vec)
    fun exp2(value: KslScalarExpression<KslFloat1>) = KslBuiltinExpScalar(value)
    fun <V> exp2(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinExpVector(vec)

    fun <V> faceForward(a: KslVectorExpression<V, KslFloat1>, b: KslVectorExpression<V, KslFloat1>, c: KslVectorExpression<V, KslFloat1>)
        where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinFaceForward(a, b, c)

    fun floor(value: KslScalarExpression<KslFloat1>) = KslBuiltinFloorScalar(value)
    fun <V> floor(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinFloorVector(vec)

    fun fma(a: KslScalarExpression<KslFloat1>, b: KslScalarExpression<KslFloat1>, c: KslScalarExpression<KslFloat1>) =
        KslBuiltinFmaScalar(a, b, c)
    fun <V> fma(a: KslVectorExpression<V, KslFloat1>, b: KslVectorExpression<V, KslFloat1>, c: KslVectorExpression<V, KslFloat1>)
        where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinFmaVector(a, b, c)

    fun fract(value: KslScalarExpression<KslFloat1>) = KslBuiltinFractScalar(value)
    fun <V> fract(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinFractVector(vec)

    fun inverseSqrt(value: KslScalarExpression<KslFloat1>) = KslBuiltinInverseSqrtScalar(value)
    fun <V> inverseSqrt(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinInverseSqrtVector(vec)

    fun isNan(value: KslScalarExpression<KslFloat1>) = KslBuiltinIsNanScalar(value)
    fun isNan(vec: KslVectorExpression<KslFloat2, KslFloat1>) = KslBuiltinIsNanVector2(vec)
    fun isNan(vec: KslVectorExpression<KslFloat3, KslFloat1>) = KslBuiltinIsNanVector3(vec)
    fun isNan(vec: KslVectorExpression<KslFloat4, KslFloat1>) = KslBuiltinIsNanVector4(vec)

    fun isInf(value: KslScalarExpression<KslFloat1>) = KslBuiltinIsNanScalar(value)
    fun isInf(vec: KslVectorExpression<KslFloat2, KslFloat1>) = KslBuiltinIsInfVector2(vec)
    fun isInf(vec: KslVectorExpression<KslFloat3, KslFloat1>) = KslBuiltinIsInfVector3(vec)
    fun isInf(vec: KslVectorExpression<KslFloat4, KslFloat1>) = KslBuiltinIsInfVector4(vec)

    fun <V> length(arg: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinLength(arg)

    fun log(value: KslScalarExpression<KslFloat1>) = KslBuiltinLogScalar(value)
    fun <V> log(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinLogVector(vec)
    fun log2(value: KslScalarExpression<KslFloat1>) = KslBuiltinLog2Scalar(value)
    fun <V> log2(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinLog2Vector(vec)

    fun <S> max(a: KslScalarExpression<S>, b: KslScalarExpression<S>) where S: KslNumericType, S: KslScalar = KslBuiltinMaxScalar(a, b)
    fun <V, S> max(a: KslVectorExpression<V, S>, b: KslVectorExpression<V, S>)
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslBuiltinMaxVector(a, b)

    fun <S> min(a: KslScalarExpression<S>, b: KslScalarExpression<S>) where S: KslNumericType, S: KslScalar = KslBuiltinMinScalar(a, b)
    fun <V, S> min(a: KslVectorExpression<V, S>, b: KslVectorExpression<V, S>)
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslBuiltinMinVector(a, b)

    fun mix(x: KslScalarExpression<KslFloat1>, y: KslScalarExpression<KslFloat1>, a: KslScalarExpression<KslFloat1>) =
        KslBuiltinMixScalar(x, y, a)
    fun <V> mix(x: KslVectorExpression<V, KslFloat1>, y: KslVectorExpression<V, KslFloat1>, a: KslVectorExpression<V, KslFloat1>)
        where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinMixVector(x, y, a)
    fun <V> mix(x: KslVectorExpression<V, KslFloat1>, y: KslVectorExpression<V, KslFloat1>, a: KslScalarExpression<KslFloat1>)
        where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinMixVector(x, y, a)

    fun <V> normalize(arg: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinNormalize(arg)

    fun pow(value: KslScalarExpression<KslFloat1>, power: KslScalarExpression<KslFloat1>) = KslBuiltinPowScalar(value, power)
    fun <V> pow(vec: KslVectorExpression<V, KslFloat1>, power: KslVectorExpression<V, KslFloat1>)
        where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinPowVector(vec, power)

    fun radians(value: KslScalarExpression<KslFloat1>) = KslBuiltinRadiansScalar(value)
    fun <V> radians(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinRadiansVector(vec)

    fun <V> reflect(a: KslVectorExpression<V, KslFloat1>, b: KslVectorExpression<V, KslFloat1>)
        where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinReflect(a, b)
    fun <V> refract(a: KslVectorExpression<V, KslFloat1>, b: KslVectorExpression<V, KslFloat1>, i: KslScalarExpression<KslFloat1>)
        where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinRefract(a, b, i)

    fun round(value: KslScalarExpression<KslFloat1>) = KslBuiltinRoundScalar(value)
    fun <V> round(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinRoundVector(vec)

    fun <S> sign(value: KslScalarExpression<S>) where S: KslNumericType, S: KslScalar = KslBuiltinSignScalar(value)
    fun <V, S> sign(vec: KslVectorExpression<V, S>)
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslBuiltinSignVector(vec)

    fun smoothStep(low: KslScalarExpression<KslFloat1>, high: KslScalarExpression<KslFloat1>, x: KslScalarExpression<KslFloat1>) =
        KslBuiltinSmoothStepScalar(low, high, x)
    fun <V> smoothStep(low: KslVectorExpression<V, KslFloat1>, high: KslVectorExpression<V, KslFloat1>, x: KslVectorExpression<V, KslFloat1>)
        where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinSmoothStepVector(low, high, x)

    fun sqrt(value: KslScalarExpression<KslFloat1>) = KslBuiltinSqrtScalar(value)
    fun <V> sqrt(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinSqrtVector(vec)

    fun step(edge: KslScalarExpression<KslFloat1>, x: KslScalarExpression<KslFloat1>) = KslBuiltinStepScalar(edge, x)
    fun <V> step(edge: KslVectorExpression<V, KslFloat1>, x: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinStepVector(edge, x)

    fun trunc(value: KslScalarExpression<KslFloat1>) = KslBuiltinTruncScalar(value)
    fun <V> trunc(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTruncVector(vec)

    // builtin trigonometry functions
    fun cos(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "cos")
    fun sin(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "sin")
    fun tan(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "tan")
    fun cosh(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "cosh")
    fun sinh(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "sinh")
    fun tanh(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "tanh")

    fun <V> cos(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "cos")
    fun <V> sin(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "sin")
    fun <V> tan(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "tan")
    fun <V> cosh(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "cosh")
    fun <V> sinh(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "sinh")
    fun <V> tanh(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "tanh")

    fun acos(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "acos")
    fun asin(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "asin")
    fun atan(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "atan")
    fun acosh(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "acosh")
    fun asinh(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "asinh")
    fun atanh(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "atanh")

    fun <V> acos(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "acos")
    fun <V> asin(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "asin")
    fun <V> atan(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "atan")
    fun <V> acosh(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "acosh")
    fun <V> asinh(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "asinh")
    fun <V> atanh(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "atanh")

    // builtin matrix functions
    fun <M, V> determinant(matrix: KslMatrixExpression<M, V>) where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<*> =
        KslBuiltinDeterminant(matrix)
    fun <M, V> transpose(matrix: KslMatrixExpression<M, V>) where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<*> =
        KslBuiltinTranspose(matrix)

    // builtin texture functions
    fun <T: KslColorSampler<C>, C: KslFloatType> sampleTexture(
        sampler: KslExpression<T>,
        coord: KslExpression<C>,
        lod: KslScalarExpression<KslFloat1>? = null
    ) = KslSampleColorTexture(sampler, coord, lod)

    fun <T: KslColorSampler<C>, C: KslFloatType> sampleTextureGrad(
        sampler: KslExpression<T>,
        coord: KslExpression<C>,
        ddx: KslExpression<C>,
        ddy: KslExpression<C>,
    ) = KslSampleColorTextureGrad(sampler, coord, ddx, ddy)

    fun <T: KslDepthSampler<C>, C: KslFloatType> sampleDepthTexture(
        sampler: KslExpression<T>,
        coord: KslExpression<C>,
        depthRef: KslExprFloat1
    ) = KslSampleDepthTexture(sampler, coord, depthRef)

    fun <T, C: KslFloatType> sampleTextureArray(
        sampler: KslExpression<T>,
        arrayIndex: KslExprInt1,
        coord: KslExpression<C>,
        lod: KslExprFloat1? = null
    )  where T: KslColorSampler<C>, T: KslSamplerArrayType = KslSampleColorTextureArray(sampler, arrayIndex, coord, lod)

    fun <T, C: KslFloatType> sampleTextureArrayGrad(
        sampler: KslExpression<T>,
        arrayIndex: KslExprInt1,
        coord: KslExpression<C>,
        ddx: KslExpression<C>,
        ddy: KslExpression<C>,
    )  where T: KslColorSampler<C>, T: KslSamplerArrayType = KslSampleColorTextureArrayGrad(sampler, arrayIndex, coord, ddx, ddy)

    fun <T: KslColorSampler<C>, C: KslFloatType> KslExpression<T>.sample(coord: KslExpression<C>, lod: KslScalarExpression<KslFloat1>? = null) =
        KslSampleColorTexture(this, coord, lod)
    fun <T: KslColorSampler<C>, C: KslFloatType> KslExpression<T>.sample(coord: KslExpression<C>, ddx: KslExpression<C>, ddy: KslExpression<C>,) =
        KslSampleColorTextureGrad(this, coord, ddx, ddy)
    fun <T: KslDepthSampler<C>, C: KslFloatType> KslExpression<T>.sample(coord: KslExpression<C>, depthRef: KslExprFloat1) =
        KslSampleDepthTexture(this, coord, depthRef)
    fun <T, C: KslFloatType> KslExpression<T>.sample(arrayIndex: KslExprInt1, coord: KslExpression<C>, lod: KslExprFloat1? = null)
        where T: KslColorSampler<C>, T: KslSamplerArrayType = KslSampleColorTextureArray(this, arrayIndex, coord, lod)
    fun <T, C: KslFloatType> KslExpression<T>.sample(arrayIndex: KslExprInt1, coord: KslExpression<C>, ddx: KslExpression<C>, ddy: KslExpression<C>)
        where T: KslColorSampler<C>, T: KslSamplerArrayType = KslSampleColorTextureArrayGrad(this, arrayIndex, coord, ddx, ddy)

    /**
     * texelFetch — perform a lookup of a single texel within a texture
     * @param sampler Specifies the sampler to which the texture from which texels will be retrieved is bound.
     * @param coord Specifies the integer texture coordinates at which texture will be sampled.
     * @param lod If present, specifies the level-of-detail within the texture from which the texel will be fetched.
     */
    @Deprecated("use texture.load instead")
    fun <T: KslColorSampler<R>, R : KslFloatType> texelFetch(
        sampler: KslExpression<T>,
        coord: KslExpression<*>,
        lod: KslScalarExpression<KslInt1>? = null
    ) = KslImageTextureLoad(sampler, coord, lod)

    fun <T: KslColorSampler<R>, R : KslFloatType, C : KslIntType> KslExpression<T>.load(
        coord: KslExpression<C>,
        lod: KslScalarExpression<KslInt1>? = null
    ) = KslImageTextureLoad(this, coord, lod)

    fun <T> textureSize1d(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1> = 0.const)
        where T: KslSamplerType<*>, T: KslSampler1dType = KslTextureSize1d(sampler, lod)
    fun <T> textureSize2d(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1> = 0.const)
        where T: KslSamplerType<*>, T: KslSampler2dType = KslTextureSize2d(sampler, lod)
    fun <T> textureSize3d(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1> = 0.const)
        where T: KslSamplerType<*>, T: KslSampler3dType = KslTextureSize3d(sampler, lod)
    fun <T> textureSizeCube(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1> = 0.const)
        where T: KslSamplerType<*>, T: KslSamplerCubeType = KslTextureSizeCube(sampler, lod)
    fun <T> textureSize2dArray(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1> = 0.const)
        where T: KslSamplerType<*>, T: KslSampler2dType, T: KslSamplerArrayType = KslTextureSize2dArray(sampler, lod)
    fun <T> textureSizeCubeArray(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1> = 0.const)
        where T: KslSamplerType<*>, T: KslSamplerCubeType, T: KslSamplerArrayType = KslTextureSizeCubeArray(sampler, lod)

    fun <T> KslExpression<T>.size(lod: KslScalarExpression<KslInt1> = 0.const)
            where T: KslSamplerType<*>, T: KslSampler1dType = KslTextureSize1d(this, lod)
    fun <T> KslExpression<T>.size(lod: KslScalarExpression<KslInt1> = 0.const)
            where T: KslSamplerType<*>, T: KslSampler2dType = KslTextureSize2d(this, lod)
    fun <T> KslExpression<T>.size(lod: KslScalarExpression<KslInt1> = 0.const)
            where T: KslSamplerType<*>, T: KslSampler3dType = KslTextureSize3d(this, lod)
    fun <T> KslExpression<T>.size(lod: KslScalarExpression<KslInt1> = 0.const)
            where T: KslSamplerType<*>, T: KslSamplerCubeType = KslTextureSizeCube(this, lod)
    fun <T> KslExpression<T>.size(lod: KslScalarExpression<KslInt1> = 0.const)
            where T: KslSamplerType<*>, T: KslSampler2dType, T: KslSamplerArrayType = KslTextureSize2dArray(this, lod)
    fun <T> KslExpression<T>.size(lod: KslScalarExpression<KslInt1> = 0.const)
            where T: KslSamplerType<*>, T: KslSamplerCubeType, T: KslSamplerArrayType = KslTextureSizeCubeArray(this, lod)

    @JvmName("storageTexSize1d")
    fun <T: KslNumericType> textureSize1d(storageTex: KslStorageTexture1d<KslStorageTexture1dType<T>, T>) = KslStorageTextureSize1d(storageTex)
    @JvmName("storageTexSize2d")
    fun <T: KslNumericType> textureSize2d(storageTex: KslStorageTexture2d<KslStorageTexture2dType<T>, T>) = KslStorageTextureSize2d(storageTex)
    @JvmName("storageTexSize3d")
    fun <T: KslNumericType> textureSize3d(storageTex: KslStorageTexture3d<KslStorageTexture3dType<T>, T>) = KslStorageTextureSize3d(storageTex)

    fun <R: KslNumericType> KslStorageTexture1d<KslStorageTexture1dType<R>, R>.size(): KslExprInt1 = textureSize1d(this)
    fun <R: KslNumericType> KslStorageTexture2d<KslStorageTexture2dType<R>, R>.size(): KslExprInt2 = textureSize2d(this)
    fun <R: KslNumericType> KslStorageTexture3d<KslStorageTexture3dType<R>, R>.size(): KslExprInt3 = textureSize3d(this)

    // builtin storage functions
    operator fun <T: KslStorageType<R>, R: KslType> KslStorage<T>.get(index: KslExprInt1): KslExpression<R> {
        return KslStorageRead(this, index, expressionType.elemType)
    }

    @JvmName("getScalar")
    operator fun <T: KslPrimitiveStorageType<S>, S> KslStorage<T>.get(index: KslExprInt1): KslScalarExpression<S> where S : KslScalar, S : KslNumericType {
        return KslStorageReadScalar(this, index, expressionType.elemType)
    }

    @JvmName("getVector")
    operator fun <T: KslPrimitiveStorageType<V>, V, S> KslStorage<T>.get(index: KslExprInt1): KslVectorExpression<V, S>
        where V : KslNumericType, V : KslVector <S>, S : KslScalar
    {
        return KslStorageReadVector(this, index, expressionType.elemType)
    }

    fun <T: KslStorageType<R>, R: KslType> KslStorage<T>.load(index: KslExprInt1): KslExpression<R> {
        return KslStorageRead(this, index, expressionType.elemType)
    }

    @JvmName("loadScalar")
    fun <T: KslStorageType<S>, S> KslStorage<T>.load(index: KslExprInt1): KslScalarExpression<S> where S : KslScalar, S : KslNumericType {
        return KslStorageReadScalar(this, index, expressionType.elemType)
    }

    @JvmName("loadVector")
    fun <T: KslStorageType<V>, V, S> KslStorage<T>.load(index: KslExprInt1): KslVectorExpression<V, S> where V: KslNumericType, V : KslVector<S>, S : KslScalar {
        return KslStorageReadVector(this, index, expressionType.elemType)
    }

    operator fun <T: KslStorageType<R>, R: KslType> KslStorage<T>.set(index: KslExprInt1, data: KslExpression<R>) {
        ops += KslStorageWrite(this, index, data, this@KslScopeBuilder)
    }

    fun <T: KslStorageType<R>, R: KslType> KslStorage<T>.store(index: KslExprInt1, data: KslExpression<R>) {
        ops += KslStorageWrite(this, index, data, this@KslScopeBuilder)
    }

    fun <T: KslStorageType<R>, R> KslStorage<T>.atomicSwap(
        index: KslExprInt1,
        data: KslExpression<R>
    ): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
        KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.Swap, storageType.elemType)

    fun <T: KslStorageType<R>, R> KslStorage<T>.atomicAdd(
        index: KslExprInt1,
        data: KslExpression<R>
    ): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
        KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.Add, storageType.elemType)

    fun <T: KslStorageType<R>, R> KslStorage<T>.atomicAnd(
        index: KslExprInt1,
        data: KslExpression<R>
    ): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
        KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.And, storageType.elemType)

    fun <T: KslStorageType<R>, R> KslStorage<T>.atomicOr(
        index: KslExprInt1,
        data: KslExpression<R>
    ): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
        KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.Or, storageType.elemType)

    fun <T: KslStorageType<R>, R> KslStorage<T>.atomicXor(
        index: KslExprInt1,
        data: KslExpression<R>
    ): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
        KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.Xor, storageType.elemType)

    fun <T: KslStorageType<R>, R> KslStorage<T>.atomicMin(
        index: KslExprInt1,
        data: KslExpression<R>
    ): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
        KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.Min, storageType.elemType)

    fun <T: KslStorageType<R>, R> KslStorage<T>.atomicMax(
        index: KslExprInt1,
        data: KslExpression<R>
    ): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
        KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.Max, storageType.elemType)

    fun <T: KslStorageType<R>, R> KslStorage<T>.atomicCompareSwap(
        index: KslExprInt1,
        compare: KslExpression<R>,
        data: KslExpression<R>
    ): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
        KslStorageAtomicCompareSwap(this, index, data, compare, storageType.elemType)

    // builtin storage texture functions
    operator fun <T: KslStorageTextureType<R, C>, R: KslNumericType, C: KslIntType> KslStorageTexture<T, R, C>.get(coord: KslExpression<C>): KslExprFloat4 =
        KslStorageTextureLoad(this, coord)

    @JvmName("getTex2d")
    operator fun <T: KslStorageTexture2dType<R>, R: KslNumericType> KslStorageTexture2d<T, R>.get(x: KslExprInt1, y: KslExprInt1): KslExprFloat4 =
        KslStorageTextureLoad(this, int2Value(x, y))

    @JvmName("getTex3d")
    operator fun <T: KslStorageTexture3dType<R>, R: KslNumericType> KslStorageTexture3d<T, R>.get(x: KslExprInt1, y: KslExprInt1, z: KslExprInt1): KslExprFloat4 =
        KslStorageTextureLoad(this, int3Value(x, y, z))

    operator fun <T: KslStorageTextureType<R, C>, R: KslNumericType, C: KslIntType> KslStorageTexture<T, R, C>.set(
        coord: KslExpression<C>,
        data: KslExprFloat4
    ) {
        ops += KslStorageTextureStore(this, coord, data, this@KslScopeBuilder)
    }

    @JvmName("setTex2d")
    operator fun <T: KslStorageTexture2dType<R>, R: KslNumericType> KslStorageTexture2d<T, R>.set(
        x: KslExprInt1,
        y: KslExprInt1,
        data: KslExprFloat4
    ) {
        ops += KslStorageTextureStore(this, int2Value(x, y), data, this@KslScopeBuilder)
    }

    @JvmName("setTex3d")
    operator fun <T: KslStorageTexture3dType<R>, R: KslNumericType> KslStorageTexture3d<T, R>.set(
        x: KslExprInt1,
        y: KslExprInt1,
        z: KslExprInt1,
        data: KslExprFloat4
    ) {
        ops += KslStorageTextureStore(this, int3Value(x, y, z), data, this@KslScopeBuilder)
    }

    fun <T: KslStorageTextureType<R, C>, R: KslNumericType, C: KslIntType> KslStorageTexture<T, R, C>.load(
        coord: KslExpression<C>
    ): KslExprFloat4 = KslStorageTextureLoad(this, coord)

    fun <T: KslStorageTextureType<R, C>, R: KslNumericType, C: KslIntType> KslStorageTexture<T, R, C>.store(
        coord: KslExpression<C>,
        data: KslExprFloat4
    ) {
        ops += KslStorageTextureStore(this, coord, data, this@KslScopeBuilder)
    }

}