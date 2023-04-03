package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslScope

class KslScopeBuilder(parentOp: KslOp?, val parentScope: KslScopeBuilder?, val parentStage: KslShaderStage) : KslScope(parentOp) {

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

    val Vec2i.const: KslValueInt2
        get() = int2Value(x, y)
    val Vec3i.const: KslValueInt3
        get() = int3Value(x, y, z)
    val Vec4i.const: KslValueInt4
        get() = int4Value(x, y, z, w)

    fun float2Value(x: Float, y: Float) = KslValueFloat2(x, y)
    fun float2Value(x: KslExpression<KslTypeFloat1>, y: KslExpression<KslTypeFloat1>) = KslValueFloat2(x, y)

    fun float3Value(x: Float, y: Float, z: Float) = KslValueFloat3(x, y, z)
    fun float3Value(x: KslExpression<KslTypeFloat1>, y: KslExpression<KslTypeFloat1>, z: KslExpression<KslTypeFloat1>) =
        KslValueFloat3(x, y, z)

    fun float4Value(x: Float, y: Float, z: Float, w: Float) = KslValueFloat4(x, y, z, w)
    fun float4Value(x: KslExpression<KslTypeFloat1>, y: KslExpression<KslTypeFloat1>, z: KslExpression<KslTypeFloat1>, w: KslExpression<KslTypeFloat1>) =
        KslValueFloat4(x, y, z, w)
    fun float4Value(xyz: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>, w: Float) =
        float4Value(xyz, w.const)
    fun float4Value(xyz: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>, w: KslExpression<KslTypeFloat1>) =
        KslValueFloat4(xyz.x, xyz.y, xyz.z, w)

    fun int2Value(x: Int, y: Int) = KslValueInt2(x, y)
    fun int2Value(x: KslExpression<KslTypeInt1>, y: KslExpression<KslTypeInt1>) = KslValueInt2(x, y)

    fun int3Value(x: Int, y: Int, z: Int) = KslValueInt3(x, y, z)
    fun int3Value(x: KslExpression<KslTypeInt1>, y: KslExpression<KslTypeInt1>, z: KslExpression<KslTypeInt1>) =
        KslValueInt3(x, y, z)

    fun int4Value(x: Int, y: Int, z: Int, w: Int) = KslValueInt4(x, y, z, w)
    fun int4Value(x: KslExpression<KslTypeInt1>, y: KslExpression<KslTypeInt1>, z: KslExpression<KslTypeInt1>, w: KslExpression<KslTypeInt1>) =
        KslValueInt4(x, y, z, w)
    fun int4Value(xyz: KslVectorExpression<KslTypeInt3, KslTypeInt1>, w: Int) =
        int4Value(xyz, w.const)
    fun int4Value(xyz: KslVectorExpression<KslTypeInt3, KslTypeInt1>, w: KslExpression<KslTypeInt1>) =
        KslValueInt4(xyz.x, xyz.y, xyz.z, w)

    fun bool2Value(x: Boolean, y: Boolean) = KslValueBool2(x, y)
    fun bool2Value(x: KslExpression<KslTypeBool1>, y: KslExpression<KslTypeBool1>) = KslValueBool2(x, y)

    fun bool3Value(x: Boolean, y: Boolean, z: Boolean) = KslValueBool3(x, y, z)
    fun bool3Value(x: KslExpression<KslTypeBool1>, y: KslExpression<KslTypeBool1>, z: KslExpression<KslTypeBool1>) =
        KslValueBool3(x, y, z)

    fun bool4Value(x: Boolean, y: Boolean, z: Boolean, w: Boolean) = KslValueBool4(x, y, z, w)
    fun bool4Value(x: KslExpression<KslTypeBool1>, y: KslExpression<KslTypeBool1>, z: KslExpression<KslTypeBool1>, w: KslExpression<KslTypeBool1>) =
        KslValueBool4(x, y, z, w)

    fun mat2Value(col0: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>,
                  col1: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>) = KslValueMat2(col0, col1)
    fun mat3Value(col0: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
                  col1: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
                  col2: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>) = KslValueMat3(col0, col1, col2)
    fun mat4Value(col0: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>,
                  col1: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>,
                  col2: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>,
                  col3: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>) = KslValueMat4(col0, col1, col2, col3)

    @Deprecated("Use float1Var instead", replaceWith = ReplaceWith("float1Var(initValue, name)"))
    fun floatVar(initValue: KslScalarExpression<KslTypeFloat1>? = null, name: String? = null) = float1Var(initValue, name)
    @Deprecated("Use float1Array instead", replaceWith = ReplaceWith("float1Array(arraySize, initExpr, name)"))
    fun floatArray(arraySize: Int, initExpr: KslScalarExpression<KslTypeFloat1>, name: String? = null) = float1Array(arraySize, initExpr, name)
    @Deprecated("Use int1Var instead", replaceWith = ReplaceWith("int1Var(initValue, name)"))
    fun intVar(initValue: KslScalarExpression<KslTypeInt1>? = null, name: String? = null) = int1Var(initValue, name)
    @Deprecated("Use int1Array instead", replaceWith = ReplaceWith("int1Array(arraySize, initExpr, name)"))
    fun intArray(arraySize: Int, initExpr: KslScalarExpression<KslTypeInt1>, name: String? = null) = int1Array(arraySize, initExpr, name)
    @Deprecated("Use bool1Var instead", replaceWith = ReplaceWith("bool1Var(initValue, name)"))
    fun boolVar(initValue: KslScalarExpression<KslTypeBool1>? = null, name: String? = null) = bool1Var(initValue, name)

    fun float1Var(initValue: KslScalarExpression<KslTypeFloat1>? = null, name: String? = null) =
        KslVarScalar(name ?: nextName("f1"), KslTypeFloat1, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun float2Var(initValue: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("f2"), KslTypeFloat2, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun float3Var(initValue: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("f3"), KslTypeFloat3, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun float4Var(initValue: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("f4"), KslTypeFloat4, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }

    fun float1Array(arraySize: Int, initExpr: KslScalarExpression<KslTypeFloat1>, name: String? = null) =
        KslArrayScalar(name ?: nextName("f1Array"), KslTypeFloat1, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun float2Array(arraySize: Int, initExpr: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>, name: String? = null) =
        KslArrayVector(name ?: nextName("f2Array"), KslTypeFloat2, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun float3Array(arraySize: Int, initExpr: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>, name: String? = null) =
        KslArrayVector(name ?: nextName("f3Array"), KslTypeFloat3, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun float4Array(arraySize: Int, initExpr: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>, name: String? = null) =
        KslArrayVector(name ?: nextName("f4Array"), KslTypeFloat4, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }

    fun int1Var(initValue: KslScalarExpression<KslTypeInt1>? = null, name: String? = null) =
        KslVarScalar(name ?: nextName("i1"), KslTypeInt1, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun int2Var(initValue: KslVectorExpression<KslTypeInt2, KslTypeInt1>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("i2"), KslTypeInt2, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun int3Var(initValue: KslVectorExpression<KslTypeInt3, KslTypeInt1>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("i3"), KslTypeInt3, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun int4Var(initValue: KslVectorExpression<KslTypeInt4, KslTypeInt1>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("i4"), KslTypeInt4, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }

    fun uint1Var(initValue: KslScalarExpression<KslTypeUint1>? = null, name: String? = null) =
        KslVarScalar(name ?: nextName("u1"), KslTypeUint1, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun uint2Var(initValue: KslVectorExpression<KslTypeUint2, KslTypeUint1>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("u2"), KslTypeUint2, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun uint3Var(initValue: KslVectorExpression<KslTypeUint3, KslTypeUint1>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("u3"), KslTypeUint3, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun uint4Var(initValue: KslVectorExpression<KslTypeUint4, KslTypeUint1>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("u4"), KslTypeUint4, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }

    fun int1Array(arraySize: Int, initExpr: KslScalarExpression<KslTypeInt1>, name: String? = null) =
        KslArrayScalar(name ?: nextName("i1Array"), KslTypeInt1, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun int2Array(arraySize: Int, initExpr: KslVectorExpression<KslTypeInt2, KslTypeInt1>, name: String? = null) =
        KslArrayVector(name ?: nextName("i2Array"), KslTypeInt2, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun int3Array(arraySize: Int, initExpr: KslVectorExpression<KslTypeInt3, KslTypeInt1>, name: String? = null) =
        KslArrayVector(name ?: nextName("i3Array"), KslTypeInt3, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }
    fun int4Array(arraySize: Int, initExpr: KslVectorExpression<KslTypeInt4, KslTypeInt1>, name: String? = null) =
        KslArrayVector(name ?: nextName("i4Array"), KslTypeInt4, arraySize, true).also { definedStates += it }.also {
            ops += KslDeclareArray(it, initExpr, this)
        }

    fun bool1Var(initValue: KslScalarExpression<KslTypeBool1>? = null, name: String? = null) =
        KslVarScalar(name ?: nextName("b1"), KslTypeBool1, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun bool2Var(initValue: KslVectorExpression<KslTypeBool2, KslTypeBool1>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("b2"), KslTypeInt2, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun bool3Var(initValue: KslVectorExpression<KslTypeBool3, KslTypeBool1>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("b3"), KslTypeInt3, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun bool4Var(initValue: KslVectorExpression<KslTypeBool4, KslTypeBool1>? = null, name: String? = null) =
        KslVarVector(name ?: nextName("b4"), KslTypeInt4, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }

    fun mat2Var(initValue: KslMatrixExpression<KslTypeMat2, KslTypeFloat2>? = null, name: String? = null) =
        KslVarMatrix(name ?: nextName("m2"), KslTypeMat2, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun mat3Var(initValue: KslMatrixExpression<KslTypeMat3, KslTypeFloat3>? = null, name: String? = null) =
        KslVarMatrix(name ?: nextName("m3"), KslTypeMat3, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }
    fun mat4Var(initValue: KslMatrixExpression<KslTypeMat4, KslTypeFloat4>? = null, name: String? = null) =
        KslVarMatrix(name ?: nextName("m4"), KslTypeMat4, true).also {
            ops += KslDeclareVar(it, initValue, this)
        }

    infix fun <T: KslType> KslAssignable<T>.set(expression: KslExpression<T>) {
        ops += KslAssign(this, expression, this@KslScopeBuilder)
    }

    fun `if`(condition: KslExpression<KslTypeBool1>, block: KslScopeBuilder.() -> Unit): KslIf {
        val stmt = KslIf(condition, this).apply { body.block() }
        ops += stmt
        return stmt
    }

    fun fori(fromInclusive: KslScalarExpression<KslTypeInt1>, toExclusive: KslScalarExpression<KslTypeInt1>,
             block: KslScopeBuilder.(KslScalarExpression<KslTypeInt1>) -> Unit) {
        val i = int1Var(fromInclusive)
        `for`(i, i lt toExclusive, 1.const, block)
    }

    fun <T> `for`(loopVar: KslVarScalar<T>, whileExpr: KslScalarExpression<KslTypeBool1>,
                  incExpr: KslScalarExpression<T>, block: KslScopeBuilder.(KslScalarExpression<T>) -> Unit)
            where T: KslNumericType, T: KslScalar {
        val loop = KslLoopFor(loopVar, whileExpr, incExpr, this).apply { body.block(loopVar) }
        ops += loop
    }

    fun `while`(whileExpr: KslScalarExpression<KslTypeBool1>, block: KslScopeBuilder.() -> Unit) {
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

    // builtin general functions
    fun <S> abs(value: KslScalarExpression<S>) where S: KslNumericType, S: KslScalar = KslBuiltinAbsScalar(value)
    fun <V, S> abs(vec: KslVectorExpression<V, S>)
            where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslBuiltinAbsVector(vec)

    fun atan2(y: KslScalarExpression<KslTypeFloat1>, x: KslScalarExpression<KslTypeFloat1>) = KslBuiltinAtan2Scalar(y, x)
    fun <V> atan2(y: KslVectorExpression<V, KslTypeFloat1>, x: KslVectorExpression<V, KslTypeFloat1>)
            where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinAtan2Vector(y, x)

    fun ceil(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinCeilScalar(value)
    fun <V> ceil(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinCeilVector(vec)

    fun <S> clamp(value: KslScalarExpression<S>, min: KslScalarExpression<S>, max: KslScalarExpression<S>)
        where S: KslNumericType, S: KslScalar = KslBuiltinClampScalar(value, min, max)
    fun <V, S> clamp(vec: KslVectorExpression<V, S>, min: KslVectorExpression<V, S>, max: KslVectorExpression<V, S>)
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslBuiltinClampVector(vec, min, max)

    fun cross(a: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>, b: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>) = KslBuiltinCross(a, b)

    fun degrees(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinDegreesScalar(value)
    fun <V> degrees(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinDegreesVector(vec)

    fun <T: KslFloatType> distance(a: KslExpression<T>, b: KslExpression<T>) = KslBuiltinDistanceScalar(a, b)

    fun <V> dot(a: KslVectorExpression<V, KslTypeFloat1>, b: KslVectorExpression<V, KslTypeFloat1>)
        where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinDot(a, b)

    fun exp(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinExpScalar(value)
    fun <V> exp(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinExpVector(vec)
    fun exp2(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinExpScalar(value)
    fun <V> exp2(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinExpVector(vec)

    fun <V> faceForward(a: KslVectorExpression<V, KslTypeFloat1>, b: KslVectorExpression<V, KslTypeFloat1>, c: KslVectorExpression<V, KslTypeFloat1>)
            where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinFaceForward(a, b, c)

    fun floor(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinFloorScalar(value)
    fun <V> floor(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinFloorVector(vec)

    fun fma(a: KslScalarExpression<KslTypeFloat1>, b: KslScalarExpression<KslTypeFloat1>, c: KslScalarExpression<KslTypeFloat1>) =
        KslBuiltinFmaScalar(a, b, c)
    fun <V> fma(a: KslVectorExpression<V, KslTypeFloat1>, b: KslVectorExpression<V, KslTypeFloat1>, c: KslVectorExpression<V, KslTypeFloat1>)
        where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinFmaVector(a, b, c)

    fun fract(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinFractScalar(value)
    fun <V> fract(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinFractVector(vec)

    fun inverseSqrt(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinInverseSqrtScalar(value)
    fun <V> inverseSqrt(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinInverseSqrtVector(vec)

    fun isNan(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinIsNanScalar(value)
    fun isNan(vec: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>) = KslBuiltinIsNanVector2(vec)
    fun isNan(vec: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>) = KslBuiltinIsNanVector3(vec)
    fun isNan(vec: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>) = KslBuiltinIsNanVector4(vec)

    fun isInf(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinIsNanScalar(value)
    fun isInf(vec: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>) = KslBuiltinIsInfVector2(vec)
    fun isInf(vec: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>) = KslBuiltinIsInfVector3(vec)
    fun isInf(vec: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>) = KslBuiltinIsInfVector4(vec)

    fun <V> length(arg: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinLength(arg)

    fun log(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinLogScalar(value)
    fun <V> log(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinLogVector(vec)
    fun log2(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinLog2Scalar(value)
    fun <V> log2(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinLog2Vector(vec)

    fun <S> max(a: KslScalarExpression<S>, b: KslScalarExpression<S>) where S: KslFloatType, S: KslScalar = KslBuiltinMaxScalar(a, b)
    fun <V, S> max(a: KslVectorExpression<V, S>, b: KslVectorExpression<V, S>)
        where V: KslFloatType, V: KslVector<S>, S: KslFloatType, S: KslScalar = KslBuiltinMaxVector(a, b)

    fun <S> min(a: KslScalarExpression<S>, b: KslScalarExpression<S>) where S: KslFloatType, S: KslScalar = KslBuiltinMinScalar(a, b)
    fun <V, S> min(a: KslVectorExpression<V, S>, b: KslVectorExpression<V, S>)
        where V: KslFloatType, V: KslVector<S>, S: KslFloatType, S: KslScalar = KslBuiltinMinVector(a, b)

    fun mix(x: KslScalarExpression<KslTypeFloat1>, y: KslScalarExpression<KslTypeFloat1>, a: KslScalarExpression<KslTypeFloat1>) =
        KslBuiltinMixScalar(x, y, a)
    fun <V> mix(x: KslVectorExpression<V, KslTypeFloat1>, y: KslVectorExpression<V, KslTypeFloat1>, a: KslVectorExpression<V, KslTypeFloat1>)
        where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinMixVector(x, y, a)
    fun <V> mix(x: KslVectorExpression<V, KslTypeFloat1>, y: KslVectorExpression<V, KslTypeFloat1>, a: KslScalarExpression<KslTypeFloat1>)
        where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinMixVector(x, y, a)

    fun <V> normalize(arg: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinNormalize(arg)

    fun pow(value: KslScalarExpression<KslTypeFloat1>, power: KslScalarExpression<KslTypeFloat1>) = KslBuiltinPowScalar(value, power)
    fun <V> pow(vec: KslVectorExpression<V, KslTypeFloat1>, power: KslVectorExpression<V, KslTypeFloat1>)
        where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinPowVector(vec, power)

    fun radians(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinRadiansScalar(value)
    fun <V> radians(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinRadiansVector(vec)

    fun <V> reflect(a: KslVectorExpression<V, KslTypeFloat1>, b: KslVectorExpression<V, KslTypeFloat1>)
            where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinReflect(a, b)
    fun <V> refract(a: KslVectorExpression<V, KslTypeFloat1>, b: KslVectorExpression<V, KslTypeFloat1>, i: KslScalarExpression<KslTypeFloat1>)
            where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinRefract(a, b, i)

    fun round(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinRoundScalar(value)
    fun <V> round(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinRoundVector(vec)

    fun <S> sign(value: KslScalarExpression<S>) where S: KslNumericType, S: KslScalar = KslBuiltinSignScalar(value)
    fun <V, S> sign(vec: KslVectorExpression<V, S>)
            where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslBuiltinSignVector(vec)

    fun smoothStep(low: KslScalarExpression<KslTypeFloat1>, high: KslScalarExpression<KslTypeFloat1>, x: KslScalarExpression<KslTypeFloat1>) =
        KslBuiltinSmoothStepScalar(low, high, x)
    fun <V> smoothStep(low: KslVectorExpression<V, KslTypeFloat1>, high: KslVectorExpression<V, KslTypeFloat1>, x: KslVectorExpression<V, KslTypeFloat1>)
            where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinSmoothStepVector(low, high, x)

    fun sqrt(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinSqrtScalar(value)
    fun <V> sqrt(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinSqrtVector(vec)

    fun step(edge: KslScalarExpression<KslTypeFloat1>, x: KslScalarExpression<KslTypeFloat1>) = KslBuiltinStepScalar(edge, x)
    fun <V> step(edge: KslVectorExpression<V, KslTypeFloat1>, x: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinStepVector(edge, x)

    fun trunc(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinTruncScalar(value)
    fun <V> trunc(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinTruncVector(vec)

    // builtin trigonometry functions
    fun cos(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinTrigonometryScalar(value, "cos")
    fun sin(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinTrigonometryScalar(value, "sin")
    fun tan(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinTrigonometryScalar(value, "tan")
    fun cosh(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinTrigonometryScalar(value, "cosh")
    fun sinh(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinTrigonometryScalar(value, "sinh")
    fun tanh(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinTrigonometryScalar(value, "tanh")

    fun <V> cos(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinTrigonometryVector(vec, "cos")
    fun <V> sin(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinTrigonometryVector(vec, "sin")
    fun <V> tan(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinTrigonometryVector(vec, "tan")
    fun <V> cosh(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinTrigonometryVector(vec, "cosh")
    fun <V> sinh(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinTrigonometryVector(vec, "sinh")
    fun <V> tanh(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinTrigonometryVector(vec, "tanh")

    fun acos(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinTrigonometryScalar(value, "acos")
    fun asin(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinTrigonometryScalar(value, "asin")
    fun atan(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinTrigonometryScalar(value, "atan")
    fun acosh(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinTrigonometryScalar(value, "acosh")
    fun asinh(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinTrigonometryScalar(value, "asinh")
    fun atanh(value: KslScalarExpression<KslTypeFloat1>) = KslBuiltinTrigonometryScalar(value, "atanh")

    fun <V> acos(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinTrigonometryVector(vec, "acos")
    fun <V> asin(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinTrigonometryVector(vec, "asin")
    fun <V> atan(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinTrigonometryVector(vec, "atan")
    fun <V> acosh(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinTrigonometryVector(vec, "acosh")
    fun <V> asinh(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinTrigonometryVector(vec, "asinh")
    fun <V> atanh(vec: KslVectorExpression<V, KslTypeFloat1>) where V: KslFloatType, V: KslVector<KslTypeFloat1> = KslBuiltinTrigonometryVector(vec, "atanh")

    // builtin matrix functions
    fun <M, V> determinant(matrix: KslMatrixExpression<M, V>) where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<*> =
        KslBuiltinDeterminant(matrix)
    fun <M, V> transpose(matrix: KslMatrixExpression<M, V>) where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<*> =
        KslBuiltinTranspose(matrix)

    // builtin texture functions
    fun <T: KslTypeColorSampler<C>, C: KslFloatType> sampleTexture(sampler: KslExpression<T>, coord: KslExpression<C>,
                                                                   lod: KslScalarExpression<KslTypeFloat1>? = null) =
        KslSampleColorTexture(sampler, coord, lod)

    fun <T: KslTypeDepthSampler<C>, C: KslFloatType> sampleDepthTexture(sampler: KslExpression<T>, coord: KslExpression<C>) =
        KslSampleDepthTexture(sampler, coord)

    /**
     * texelFetch — perform a lookup of a single texel within a texture
     * @param sampler Specifies the sampler to which the texture from which texels will be retrieved is bound.
     * @param coord Specifies the texture coordinates at which texture will be sampled.
     * @param lod If present, specifies the level-of-detail within the texture from which the texel will be fetched.
     */
    fun <T: KslTypeColorSampler<R>, R : KslFloatType> texelFetch(sampler: KslExpression<T>, coord: KslExpression<*>,
                                                  lod: KslScalarExpression<KslTypeInt1>? = null) =
        KslTexelFetch(sampler, coord, lod)

    fun <T> textureSize1d(sampler: KslExpression<T>, lod: KslScalarExpression<KslTypeInt1> = 0.const)
        where T: KslTypeSampler<*>, T: KslTypeSampler1d = KslTextureSize1d(sampler, lod)
    fun <T> textureSize2d(sampler: KslExpression<T>, lod: KslScalarExpression<KslTypeInt1> = 0.const)
            where T: KslTypeSampler<*>, T: KslTypeSampler2d = KslTextureSize2d(sampler, lod)
    fun <T> textureSize3d(sampler: KslExpression<T>, lod: KslScalarExpression<KslTypeInt1> = 0.const)
            where T: KslTypeSampler<*>, T: KslTypeSampler3d = KslTextureSize3d(sampler, lod)
    fun <T> textureSizeCube(sampler: KslExpression<T>, lod: KslScalarExpression<KslTypeInt1> = 0.const)
            where T: KslTypeSampler<*>, T: KslTypeSamplerCube = KslTextureSizeCube(sampler, lod)
    fun <T> textureSize2dArray(sampler: KslExpression<T>, lod: KslScalarExpression<KslTypeInt1> = 0.const)
            where T: KslTypeSampler<*>, T: KslTypeSampler2dArray = KslTextureSize2dArray(sampler, lod)
    fun <T> textureSizeCubeArray(sampler: KslExpression<T>, lod: KslScalarExpression<KslTypeInt1> = 0.const)
            where T: KslTypeSampler<*>, T: KslTypeSamplerCubeArray = KslTextureSizeCubeArray(sampler, lod)
}