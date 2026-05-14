package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslDomain
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Struct

context(_: KslDomain)
val Double.const: KslValueFloat1 get() = KslValueFloat1(this.toFloat())
context(_: KslDomain)
val Float.const: KslValueFloat1 get() = KslValueFloat1(this)
context(_: KslDomain)
val Vec2f.const: KslValueFloat2 get() = float2Value(x, y)
context(_: KslDomain)
val Vec3f.const: KslValueFloat3 get() = float3Value(x, y, z)
context(_: KslDomain)
val Vec4f.const: KslValueFloat4 get() = float4Value(x, y, z, w)
context(_: KslDomain)
val Color.const: KslValueFloat4 get() = float4Value(r, g, b, a)
context(_: KslDomain)
val QuatF.const: KslValueFloat4 get() = float4Value(x, y, z, w)
context(_: KslDomain)
val Mat3f.const: KslValueMat3 get() = mat3Value(get(0).const, get(1).const, get(2).const)
context(_: KslDomain)
val Mat4f.const: KslValueMat4 get() = mat4Value(get(0).const, get(1).const, get(2).const, get(3).const)

context(_: KslDomain)
val Int.const: KslValueInt1 get() = KslValueInt1(this)
context(_: KslDomain)
val Int.uconst: KslValueUint1 get() = KslValueUint1(this.toUInt())
context(_: KslDomain)
val UInt.const: KslValueUint1 get() = KslValueUint1(this)
context(_: KslDomain)
val Vec2i.const: KslValueInt2 get() = int2Value(x, y)
context(_: KslDomain)
val Vec3i.const: KslValueInt3 get() = int3Value(x, y, z)
context(_: KslDomain)
val Vec4i.const: KslValueInt4 get() = int4Value(x, y, z, w)

context(_: KslDomain)
val Boolean.const: KslValueBool1 get() = KslValueBool1(this)

context(_: KslDomain)
fun float2Value(x: KslExprFloat1, y: KslExprFloat1) = KslValueFloat2(x, y)
context(_: KslDomain)
fun float3Value(x: KslExprFloat1, y: KslExprFloat1, z: KslExprFloat1) = KslValueFloat3(x, y, z)
context(_: KslDomain)
fun float4Value(x: KslExprFloat1, y: KslExprFloat1, z: KslExprFloat1, w: KslExprFloat1) = KslValueFloat4(x, y, z, w)

context(_: KslDomain)
fun int2Value(x: KslExprInt1, y: KslExprInt1) = KslValueInt2(x, y)
context(_: KslDomain)
fun int3Value(x: KslExprInt1, y: KslExprInt1, z: KslExprInt1) = KslValueInt3(x, y, z)
context(_: KslDomain)
fun int4Value(x: KslExprInt1, y: KslExprInt1, z: KslExprInt1, w: KslExprInt1) = KslValueInt4(x, y, z, w)

context(_: KslDomain)
fun uint2Value(x: KslExprUint1, y: KslExprUint1) = KslValueUint2(x, y)
context(_: KslDomain)
fun uint3Value(x: KslExprUint1, y: KslExprUint1, z: KslExprUint1) = KslValueUint3(x, y, z)
context(_: KslDomain)
fun uint4Value(x: KslExprUint1, y: KslExprUint1, z: KslExprUint1, w: KslExprUint1) = KslValueUint4(x, y, z, w)

context(_: KslDomain)
fun bool2Value(x: KslExprBool1, y: KslExprBool1) = KslValueBool2(x, y)
context(_: KslDomain)
fun bool3Value(x: KslExprBool1, y: KslExprBool1, z: KslExprBool1) = KslValueBool3(x, y, z)
context(_: KslDomain)
fun bool4Value(x: KslExprBool1, y: KslExprBool1, z: KslExprBool1, w: KslExprBool1) = KslValueBool4(x, y, z, w)

context(_: KslDomain)
fun mat2Value(col0: KslExprFloat2, col1: KslExprFloat2) = KslValueMat2(col0, col1)
context(_: KslDomain)
fun mat3Value(col0: KslExprFloat3, col1: KslExprFloat3, col2: KslExprFloat3) = KslValueMat3(col0, col1, col2)
context(_: KslDomain)
fun mat4Value(col0: KslExprFloat4, col1: KslExprFloat4, col2: KslExprFloat4, col3: KslExprFloat4) = KslValueMat4(col0, col1, col2, col3)


context(b: KslScopeBuilder)
fun float1Var(initValue: KslExpression<KslFloat1>? = null, name: String? = null) =
    KslVarScalar(name ?: b.nextName("f1"), KslFloat1, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun float2Var(initValue: KslExpression<KslFloat2>? = null, name: String? = null) =
    KslVarVector(name ?: b.nextName("f2"), KslFloat2, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun float3Var(initValue: KslExpression<KslFloat3>? = null, name: String? = null) =
    KslVarVector(name ?: b.nextName("f3"), KslFloat3, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun float4Var(initValue: KslExpression<KslFloat4>? = null, name: String? = null) =
    KslVarVector(name ?: b.nextName("f4"), KslFloat4, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }


context(b: KslScopeBuilder)
fun int1Var(initValue: KslExpression<KslInt1>? = null, name: String? = null) =
    KslVarScalar(name ?: b.nextName("i1"), KslInt1, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun int2Var(initValue: KslExpression<KslInt2>? = null, name: String? = null) =
    KslVarVector(name ?: b.nextName("i2"), KslInt2, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun int3Var(initValue: KslExpression<KslInt3>? = null, name: String? = null) =
    KslVarVector(name ?: b.nextName("i3"), KslInt3, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun int4Var(initValue: KslExpression<KslInt4>? = null, name: String? = null) =
    KslVarVector(name ?: b.nextName("i4"), KslInt4, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }


context(b: KslScopeBuilder)
fun uint1Var(initValue: KslExpression<KslUint1>? = null, name: String? = null) =
    KslVarScalar(name ?: b.nextName("u1"), KslUint1, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun uint2Var(initValue: KslExpression<KslUint2>? = null, name: String? = null) =
    KslVarVector(name ?: b.nextName("u2"), KslUint2, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun uint3Var(initValue: KslExpression<KslUint3>? = null, name: String? = null) =
    KslVarVector(name ?: b.nextName("u3"), KslUint3, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun uint4Var(initValue: KslExpression<KslUint4>? = null, name: String? = null) =
    KslVarVector(name ?: b.nextName("u4"), KslUint4, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }


context(b: KslScopeBuilder)
fun bool1Var(initValue: KslExpression<KslBool1>? = null, name: String? = null) =
    KslVarScalar(name ?: b.nextName("b1"), KslBool1, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun bool2Var(initValue: KslExpression<KslBool2>? = null, name: String? = null) =
    KslVarVector(name ?: b.nextName("b2"), KslBool2, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun bool3Var(initValue: KslExpression<KslBool3>? = null, name: String? = null) =
    KslVarVector(name ?: b.nextName("b3"), KslBool3, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun bool4Var(initValue: KslExpression<KslBool4>? = null, name: String? = null) =
    KslVarVector(name ?: b.nextName("b4"), KslBool4, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }


context(b: KslScopeBuilder)
fun mat2Var(initValue: KslExpression<KslMat2>? = null, name: String? = null) =
    KslVarMatrix(name ?: b.nextName("m2"), KslMat2, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun mat3Var(initValue: KslExpression<KslMat3>? = null, name: String? = null) =
    KslVarMatrix(name ?: b.nextName("m3"), KslMat3, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun mat4Var(initValue: KslExpression<KslMat4>? = null, name: String? = null) =
    KslVarMatrix(name ?: b.nextName("m4"), KslMat4, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }

context(_: KslScopeBuilder)
fun float2Var(x: KslExprFloat1, y: KslExprFloat1) = float2Var(float2Value(x, y))
context(_: KslScopeBuilder)
fun float3Var(x: KslExprFloat1, y: KslExprFloat1, z: KslExprFloat1) = float3Var(float3Value(x, y, z))
context(_: KslScopeBuilder)
fun float4Var(x: KslExprFloat1, y: KslExprFloat1, z: KslExprFloat1, w: KslExprFloat1) = float4Var(float4Value(x, y, z, w))

context(_: KslScopeBuilder)
fun int2Var(x: KslExprInt1, y: KslExprInt1) = int2Var(int2Value(x, y))
context(_: KslScopeBuilder)
fun int3Var(x: KslExprInt1, y: KslExprInt1, z: KslExprInt1) = int3Var(int3Value(x, y, z))
context(_: KslScopeBuilder)
fun int4Var(x: KslExprInt1, y: KslExprInt1, z: KslExprInt1, w: KslExprInt1) = int4Var(int4Value(x, y, z, w))

context(_: KslScopeBuilder)
fun uint2Var(x: KslExprUint1, y: KslExprUint1) = uint2Var(uint2Value(x, y))
context(_: KslScopeBuilder)
fun uint3Var(x: KslExprUint1, y: KslExprUint1, z: KslExprUint1) = uint3Var(uint3Value(x, y, z))
context(_: KslScopeBuilder)
fun uint4Var(x: KslExprUint1, y: KslExprUint1, z: KslExprUint1, w: KslExprUint1) = uint4Var(uint4Value(x, y, z, w))

context(_: KslScopeBuilder)
fun bool2Var(x: KslExprBool1, y: KslExprBool1) = bool2Var(bool2Value(x, y))
context(_: KslScopeBuilder)
fun bool3Var(x: KslExprBool1, y: KslExprBool1, z: KslExprBool1) = bool3Var(bool3Value(x, y, z))
context(_: KslScopeBuilder)
fun bool4Var(x: KslExprBool1, y: KslExprBool1, z: KslExprBool1, w: KslExprBool1) = bool4Var(bool4Value(x, y, z, w))

context(_: KslScopeBuilder)
fun mat2Var(col0: KslExprFloat2, col1: KslExprFloat2) = mat2Var(mat2Value(col0, col1))
context(_: KslScopeBuilder)
fun mat3Var(col0: KslExprFloat3, col1: KslExprFloat3, col2: KslExprFloat3) = mat3Var(mat3Value(col0, col1, col2))
context(_: KslScopeBuilder)
fun mat4Var(col0: KslExprFloat4, col1: KslExprFloat4, col2: KslExprFloat4, col3: KslExprFloat4) = mat4Var(mat4Value(col0, col1, col2, col3))


context(b: KslScopeBuilder)
fun <S: Struct> structVar(initValue: KslExprStruct<S>, name: String? = null) =
    KslVarStruct(name ?: b.nextName("struct"), initValue.expressionType, true).also {
        b.ops += KslDeclareVar(it, initValue, b)
    }
context(b: KslScopeBuilder)
fun <S: Struct> structVar(structType: KslStruct<S>, name: String? = null) =
    KslVarStruct(name ?: b.nextName("struct"), structType, true).also {
        b.ops += KslDeclareVar(it, null, b)
    }


context(b: KslScopeBuilder)
fun float1Array(arraySize: Int, initExpr: KslExprFloat1, name: String? = null) =
    KslArrayScalar(name ?: b.nextName("f1Array"), KslFloat1, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun float2Array(arraySize: Int, initExpr: KslExprFloat2, name: String? = null) =
    KslArrayVector(name ?: b.nextName("f2Array"), KslFloat2, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun float3Array(arraySize: Int, initExpr: KslExprFloat3, name: String? = null) =
    KslArrayVector(name ?: b.nextName("f3Array"), KslFloat3, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun float4Array(arraySize: Int, initExpr: KslExprFloat4, name: String? = null) =
    KslArrayVector(name ?: b.nextName("f4Array"), KslFloat4, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }

context(b: KslScopeBuilder)
fun float1Array(initExpr: KslExprFloat1Array, name: String? = null) =
    KslArrayScalar(name ?: b.nextName("f1Array"), KslFloat1, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun float2Array(initExpr: KslExprFloat2Array, name: String? = null) =
    KslArrayVector(name ?: b.nextName("f2Array"), KslFloat2, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun float3Array(initExpr: KslExprFloat3Array, name: String? = null) =
    KslArrayVector(name ?: b.nextName("f3Array"), KslFloat3, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun float4Array(initExpr: KslExprFloat4Array, name: String? = null) =
    KslArrayVector(name ?: b.nextName("f4Array"), KslFloat4, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }


context(b: KslScopeBuilder)
fun int1Array(arraySize: Int, initExpr: KslExprInt1, name: String? = null) =
    KslArrayScalar(name ?: b.nextName("i1Array"), KslInt1, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun int2Array(arraySize: Int, initExpr: KslExprInt2, name: String? = null) =
    KslArrayVector(name ?: b.nextName("i2Array"), KslInt2, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun int3Array(arraySize: Int, initExpr: KslExprInt3, name: String? = null) =
    KslArrayVector(name ?: b.nextName("i3Array"), KslInt3, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun int4Array(arraySize: Int, initExpr: KslExprInt4, name: String? = null) =
    KslArrayVector(name ?: b.nextName("i4Array"), KslInt4, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }

context(b: KslScopeBuilder)
fun int1Array(initExpr: KslExprInt1Array, name: String? = null) =
    KslArrayScalar(name ?: b.nextName("i1Array"), KslInt1, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun int2Array(initExpr: KslExprInt2Array, name: String? = null) =
    KslArrayVector(name ?: b.nextName("i2Array"), KslInt2, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun int3Array(initExpr: KslExprInt3Array, name: String? = null) =
    KslArrayVector(name ?: b.nextName("i3Array"), KslInt3, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun int4Array(initExpr: KslExprInt4Array, name: String? = null) =
    KslArrayVector(name ?: b.nextName("i4Array"), KslInt4, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }


context(b: KslScopeBuilder)
fun uint1Array(arraySize: Int, initExpr: KslExprUint1, name: String? = null) =
    KslArrayScalar(name ?: b.nextName("u1Array"), KslUint1, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun uint2Array(arraySize: Int, initExpr: KslExprUint2, name: String? = null) =
    KslArrayVector(name ?: b.nextName("u2Array"), KslUint2, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun uint3Array(arraySize: Int, initExpr: KslExprUint3, name: String? = null) =
    KslArrayVector(name ?: b.nextName("u3Array"), KslUint3, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun uint4Array(arraySize: Int, initExpr: KslExprUint4, name: String? = null) =
    KslArrayVector(name ?: b.nextName("u4Array"), KslUint4, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }

context(b: KslScopeBuilder)
fun uint1Array(initExpr: KslExprUint1Array, name: String? = null) =
    KslArrayScalar(name ?: b.nextName("u1Array"), KslUint1, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun uint2Array(initExpr: KslExprUint2Array, name: String? = null) =
    KslArrayVector(name ?: b.nextName("u2Array"), KslUint2, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun uint3Array(initExpr: KslExprUint3Array, name: String? = null) =
    KslArrayVector(name ?: b.nextName("u3Array"), KslUint3, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun uint4Array(initExpr: KslExprUint4Array, name: String? = null) =
    KslArrayVector(name ?: b.nextName("u4Array"), KslUint4, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }


context(b: KslScopeBuilder)
fun bool1Array(arraySize: Int, initExpr: KslExprBool1, name: String? = null) =
    KslArrayScalar(name ?: b.nextName("b1Array"), KslBool1, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun bool2Array(arraySize: Int, initExpr: KslExprBool2, name: String? = null) =
    KslArrayVector(name ?: b.nextName("b2Array"), KslBool2, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun bool3Array(arraySize: Int, initExpr: KslExprBool3, name: String? = null) =
    KslArrayVector(name ?: b.nextName("b3Array"), KslBool3, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun bool4Array(arraySize: Int, initExpr: KslExprBool4, name: String? = null) =
    KslArrayVector(name ?: b.nextName("b4Array"), KslBool4, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }

context(b: KslScopeBuilder)
fun bool1Array(initExpr: KslExprBool1Array, name: String? = null) =
    KslArrayScalar(name ?: b.nextName("b1Array"), KslBool1, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun bool2Array(initExpr: KslExprBool2Array, name: String? = null) =
    KslArrayVector(name ?: b.nextName("b2Array"), KslBool2, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun bool3Array(initExpr: KslExprBool3Array, name: String? = null) =
    KslArrayVector(name ?: b.nextName("b3Array"), KslBool3, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun bool4Array(initExpr: KslExprBool4Array, name: String? = null) =
    KslArrayVector(name ?: b.nextName("b4Array"), KslBool4, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }


context(b: KslScopeBuilder)
fun mat2Array(arraySize: Int, initExpr: KslExprMat2, name: String? = null) =
    KslArrayMatrix(name ?: b.nextName("i2Array"), KslMat2, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun mat3Array(arraySize: Int, initExpr: KslExprMat3, name: String? = null) =
    KslArrayMatrix(name ?: b.nextName("i3Array"), KslMat3, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun mat4Array(arraySize: Int, initExpr: KslExprMat4, name: String? = null) =
    KslArrayMatrix(name ?: b.nextName("i4Array"), KslMat4, arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }

context(b: KslScopeBuilder)
fun mat2Array(initExpr: KslExprMat2Array, name: String? = null) =
    KslArrayMatrix(name ?: b.nextName("i2Array"), KslMat2, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun mat3Array(initExpr: KslExprMat3Array, name: String? = null) =
    KslArrayMatrix(name ?: b.nextName("i3Array"), KslMat3, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }
context(b: KslScopeBuilder)
fun mat4Array(initExpr: KslExprMat4Array, name: String? = null) =
    KslArrayMatrix(name ?: b.nextName("i4Array"), KslMat4, initExpr.expressionType.arraySize, true).also { b.definedStates += it }.also {
        b.ops += KslDeclareArray(it, initExpr, b)
    }


//
// swizzles
//

// floats
context(_: KslDomain)
fun float3Value(xy: KslExprFloat2, z: KslExprFloat1) = KslValueFloat3(xy.x, xy.y, z)

context(_: KslScopeBuilder)
fun float3Var(xy: KslExprFloat2, z: KslExprFloat1) = float3Var(float3Value(xy.x, xy.y, z))

context(_: KslDomain)
fun float4Value(xy: KslExprFloat2, z: KslExprFloat1, w: KslExprFloat1) = KslValueFloat4(xy.x, xy.y, z, w)
context(_: KslDomain)
fun float4Value(xy: KslExprFloat2, zw: KslExprFloat2) = KslValueFloat4(xy.x, xy.y, zw.x, zw.y)
context(_: KslDomain)
fun float4Value(xyz: KslExprFloat3, w: Float) = float4Value(xyz, w.const)
context(_: KslDomain)
fun float4Value(xyz: KslExprFloat3, w: KslExprFloat1) = KslValueFloat4(xyz.x, xyz.y, xyz.z, w)

context(_: KslScopeBuilder)
fun float4Var(xy: KslExprFloat2, z: KslExprFloat1, w: KslExprFloat1) = float4Var(float4Value(xy.x, xy.y, z, w))
context(_: KslScopeBuilder)
fun float4Var(xy: KslExprFloat2, zw: KslExprFloat2) = float4Var(float4Value(xy.x, xy.y, zw.x, zw.y))
context(_: KslScopeBuilder)
fun float4Var(xyz: KslExprFloat3, w: Float) = float4Var(float4Value(xyz, w.const))
context(_: KslScopeBuilder)
fun float4Var(xyz: KslExprFloat3, w: KslExprFloat1) = float4Var(float4Value(xyz.x, xyz.y, xyz.z, w))

// ints
context(_: KslDomain)
fun int3Value(xy: KslExprInt2, z: KslExprInt1) = KslValueInt3(xy.x, xy.y, z)

context(_: KslScopeBuilder)
fun int3Var(xy: KslExprInt2, z: KslExprInt1) = int3Var(int3Value(xy.x, xy.y, z))

context(_: KslDomain)
fun int4Value(xy: KslExprInt2, z: KslExprInt1, w: KslExprInt1) = KslValueInt4(xy.x, xy.y, z, w)
context(_: KslDomain)
fun int4Value(xy: KslExprInt2, zw: KslExprInt2) = KslValueInt4(xy.x, xy.y, zw.x, zw.y)
context(_: KslDomain)
fun int4Value(xyz: KslExprInt3, w: Int) = int4Value(xyz, w.const)
context(_: KslDomain)
fun int4Value(xyz: KslExprInt3, w: KslExprInt1) = KslValueInt4(xyz.x, xyz.y, xyz.z, w)

context(_: KslScopeBuilder)
fun int4Var(xy: KslExprInt2, z: KslExprInt1, w: KslExprInt1) = int4Var(int4Value(xy.x, xy.y, z, w))
context(_: KslScopeBuilder)
fun int4Var(xy: KslExprInt2, zw: KslExprInt2) = int4Var(int4Value(xy.x, xy.y, zw.x, zw.y))
context(_: KslScopeBuilder)
fun int4Var(xyz: KslExprInt3, w: Int) = int4Var(int4Value(xyz, w.const))
context(_: KslScopeBuilder)
fun int4Var(xyz: KslExprInt3, w: KslExprInt1) = int4Var(int4Value(xyz.x, xyz.y, xyz.z, w))

// uints
context(_: KslDomain)
fun uint3Value(xy: KslExprUint2, z: KslExprUint1) = KslValueUint3(xy.x, xy.y, z)

context(_: KslScopeBuilder)
fun uint3Var(xy: KslExprUint2, z: KslExprUint1) = uint3Var(uint3Value(xy.x, xy.y, z))

context(_: KslDomain)
fun uint4Value(xy: KslExprUint2, z: KslExprUint1, w: KslExprUint1) = KslValueUint4(xy.x, xy.y, z, w)
context(_: KslDomain)
fun uint4Value(xy: KslExprUint2, zw: KslExprUint2) = KslValueUint4(xy.x, xy.y, zw.x, zw.y)
context(_: KslDomain)
fun uint4Value(xyz: KslExprUint3, w: UInt) = uint4Value(xyz, w.const)
context(_: KslDomain)
fun uint4Value(xyz: KslExprUint3, w: KslExprUint1) = KslValueUint4(xyz.x, xyz.y, xyz.z, w)

context(_: KslScopeBuilder)
fun uint4Var(xy: KslExprUint2, z: KslExprUint1, w: KslExprUint1) = uint4Var(uint4Value(xy.x, xy.y, z, w))
context(_: KslScopeBuilder)
fun uint4Var(xy: KslExprUint2, zw: KslExprUint2) = uint4Var(uint4Value(xy.x, xy.y, zw.x, zw.y))
context(_: KslScopeBuilder)
fun uint4Var(xyz: KslExprUint3, w: UInt) = uint4Var(uint4Value(xyz, w.const))
context(_: KslScopeBuilder)
fun uint4Var(xyz: KslExprUint3, w: KslExprUint1) = uint4Var(uint4Value(xyz.x, xyz.y, xyz.z, w))

//
// primitive conversions
//

// floats
context(_: KslDomain)
val Float.const2: KslValueFloat2 get() = KslValueFloat2(this.const, this.const)
context(_: KslDomain)
val Float.const3: KslValueFloat3 get() = KslValueFloat3(this.const, this.const, this.const)
context(_: KslDomain)
val Float.const4: KslValueFloat4 get() = KslValueFloat4(this.const, this.const, this.const, this.const)

context(_: KslDomain)
fun float1Value(x: Float) = x.const
context(_: KslDomain)
fun float2Value(x: Float, y: Float) = KslValueFloat2(x.const, y.const)
context(_: KslDomain)
fun float3Value(x: Float, y: Float, z: Float) = KslValueFloat3(x.const, y.const, z.const)
context(_: KslDomain)
fun float4Value(x: Float, y: Float, z: Float, w: Float) = KslValueFloat4(x.const, y.const, z.const, w.const)

context(_: KslScopeBuilder)
fun float2Var(x: Float, y: Float) = float2Var(float2Value(x, y))
context(_: KslScopeBuilder)
fun float3Var(x: Float, y: Float, z: Float) = float3Var(float3Value(x, y, z))
context(_: KslScopeBuilder)
fun float4Var(x: Float, y: Float, z: Float, w: Float) = float4Var(float4Value(x, y, z, w))

// ints
context(_: KslDomain)
val Int.const2: KslValueInt2 get() = KslValueInt2(this.const, this.const)
context(_: KslDomain)
val Int.const3: KslValueInt3 get() = KslValueInt3(this.const, this.const, this.const)
context(_: KslDomain)
val Int.const4: KslValueInt4 get() = KslValueInt4(this.const, this.const, this.const, this.const)

context(_: KslDomain)
val Int.uconst2: KslValueUint2 get() = KslValueUint2(this.uconst, this.uconst)
context(_: KslDomain)
val Int.uconst3: KslValueUint3 get() = KslValueUint3(this.uconst, this.uconst, this.uconst)
context(_: KslDomain)
val Int.uconst4: KslValueUint4 get() = KslValueUint4(this.uconst, this.uconst, this.uconst, this.uconst)

context(_: KslDomain)
fun int1Value(x: Int) = x.const
context(_: KslDomain)
fun int2Value(x: Int, y: Int) = KslValueInt2(x.const, y.const)
context(_: KslDomain)
fun int3Value(x: Int, y: Int, z: Int) = KslValueInt3(x.const, y.const, z.const)
context(_: KslDomain)
fun int4Value(x: Int, y: Int, z: Int, w: Int) = KslValueInt4(x.const, y.const, z.const, w.const)

context(_: KslScopeBuilder)
fun int2Var(x: Int, y: Int) = int2Var(int2Value(x, y))
context(_: KslScopeBuilder)
fun int3Var(x: Int, y: Int, z: Int) = int3Var(int3Value(x, y, z))
context(_: KslScopeBuilder)
fun int4Var(x: Int, y: Int, z: Int, w: Int) = int4Var(int4Value(x, y, z, w))

// uints
context(_: KslDomain)
val UInt.const2: KslValueUint2 get() = KslValueUint2(this.const, this.const)
context(_: KslDomain)
val UInt.const3: KslValueUint3 get() = KslValueUint3(this.const, this.const, this.const)
context(_: KslDomain)
val UInt.const4: KslValueUint4 get() = KslValueUint4(this.const, this.const, this.const, this.const)

context(_: KslDomain)
fun uint1Value(x: UInt) = x.const
context(_: KslDomain)
fun uint2Value(x: UInt, y: UInt) = KslValueUint2(x.const, y.const)
context(_: KslDomain)
fun uint3Value(x: UInt, y: UInt, z: UInt) = KslValueUint3(x.const, y.const, z.const)
context(_: KslDomain)
fun uint4Value(x: UInt, y: UInt, z: UInt, w: UInt) = KslValueUint4(x.const, y.const, z.const, w.const)

context(_: KslScopeBuilder)
fun uint2Var(x: UInt, y: UInt) = uint2Var(uint2Value(x, y))
context(_: KslScopeBuilder)
fun uint3Var(x: UInt, y: UInt, z: UInt) = uint3Var(uint3Value(x, y, z))
context(_: KslScopeBuilder)
fun uint4Var(x: UInt, y: UInt, z: UInt, w: UInt) = uint4Var(uint4Value(x, y, z, w))

// bools
context(_: KslDomain)
val Boolean.const2: KslValueBool2 get() = KslValueBool2(this.const, this.const)
context(_: KslDomain)
val Boolean.const3: KslValueBool3 get() = KslValueBool3(this.const, this.const, this.const)
context(_: KslDomain)
val Boolean.const4: KslValueBool4 get() = KslValueBool4(this.const, this.const, this.const, this.const)

context(_: KslDomain)
fun bool1Value(x: Boolean) = x.const
context(_: KslDomain)
fun bool2Value(x: Boolean, y: Boolean) = KslValueBool2(x.const, y.const)
context(_: KslDomain)
fun bool3Value(x: Boolean, y: Boolean, z: Boolean) = KslValueBool3(x.const, y.const, z.const)
context(_: KslDomain)
fun bool4Value(x: Boolean, y: Boolean, z: Boolean, w: Boolean) = KslValueBool4(x.const, y.const, z.const, w.const)

context(_: KslScopeBuilder)
fun bool2Var(x: Boolean, y: Boolean) = bool2Var(bool2Value(x, y))
context(_: KslScopeBuilder)
fun bool3Var(x: Boolean, y: Boolean, z: Boolean) = bool3Var(bool3Value(x, y, z))
context(_: KslScopeBuilder)
fun bool4Var(x: Boolean, y: Boolean, z: Boolean, w: Boolean) = bool4Var(bool4Value(x, y, z, w))


