package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.util.*

class KslStruct<T: Struct>(val struct: T) : KslType(struct.name) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as KslStruct<*>
        return struct::class == other.struct::class
    }

    override fun hashCode(): Int {
        return struct::class.hashCode()
    }
}

abstract class KslStructMemberExpression<T: KslType, P: Struct>(val struct: KslExpression<KslStruct<P>>, val member: StructMember<P>) : KslExpression<T>

class KslStructMemberExpressionScalar<S, P>(
    struct: KslExpression<KslStruct<P>>,
    member: StructMember<P>,
    override val expressionType: S
) : KslStructMemberExpression<S, P>(struct, member), KslScalarExpression<S>, KslAssignable<S> where S : KslScalar, S : KslType, P : Struct {
    override val assignType: S get() = expressionType
    override val mutatingState: KslValue<*>? = struct.asAssignable()

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun generateAssignable(generator: KslGenerator): String = generator.structMemberAssignable(this)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionVector<V, S, P>(
    struct: KslExpression<KslStruct<P>>,
    member: StructMember<P>,
    override val expressionType: V
) : KslStructMemberExpression<V, P>(struct, member), KslVectorExpression<V, S>, KslAssignable<V> where V : KslType, V : KslVector<S>, S : KslScalar, P : Struct {
    override val assignType: V get() = expressionType
    override val mutatingState: KslValue<*>? = struct.asAssignable()

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun generateAssignable(generator: KslGenerator): String = generator.structMemberAssignable(this)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionMatrix<M, V, P>(
    struct: KslExpression<KslStruct<P>>,
    member: StructMember<P>,
    override val expressionType: M
) : KslStructMemberExpression<M, P>(struct, member), KslMatrixExpression<M, V>, KslAssignable<M> where M : KslNumericType, M : KslMatrix<V>, V : KslVector<*>, P : Struct {
    override val assignType: M get() = expressionType
    override val mutatingState: KslValue<*>? = struct.asAssignable()

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun generateAssignable(generator: KslGenerator): String = generator.structMemberAssignable(this)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionScalarArray<S, P>(
    struct: KslExpression<KslStruct<P>>,
    member: StructMember<P>,
    override val expressionType: KslArrayType<S>
) : KslStructMemberExpression<KslArrayType<S>, P>(struct, member), KslScalarArrayExpression<S> where S : KslScalar, S : KslType, P : Struct {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionVectorArray<V, S, P>(
    struct: KslExpression<KslStruct<P>>,
    member: StructMember<P>,
    override val expressionType: KslArrayType<V>
) : KslStructMemberExpression<KslArrayType<V>, P>(struct, member), KslVectorArrayExpression<V, S> where V : KslType, V : KslVector<S>, S : KslScalar, P : Struct {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionMatrixArray<M, V, P>(
    struct: KslExpression<KslStruct<P>>,
    member: StructMember<P>,
    override val expressionType: KslArrayType<M>
) : KslStructMemberExpression<KslArrayType<M>, P>(struct, member), KslMatrixArrayExpression<M, V> where M : KslNumericType, M : KslMatrix<V>, V : KslVector<*>, P : Struct {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionStruct<S: Struct, P: Struct>(
    struct: KslExpression<KslStruct<P>>,
    member: StructMember<P>,
    override val expressionType: KslStruct<S>
) : KslStructMemberExpression<KslStruct<S>, P>(struct, member), KslAssignable<KslStruct<S>>, KslExpression<KslStruct<S>> {
    override val assignType get() = expressionType
    override val mutatingState: KslValue<*>? = struct.asAssignable()

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun generateAssignable(generator: KslGenerator): String = generator.structMemberAssignable(this)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionStructArray<S: Struct, P: Struct>(
    struct: KslExpression<KslStruct<P>>,
    member: StructMember<P>,
    override val expressionType: KslStructArray<S>
) : KslStructMemberExpression<KslArrayType<KslStruct<S>>, P>(struct, member), KslArrayExpression<KslStruct<S>> {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

operator fun <S: Struct> KslExprStruct<S>.get(member: Float1Member<S>): KslStructMemberExpressionScalar<KslFloat1, S> =
    KslStructMemberExpressionScalar(this, member, KslFloat1)
operator fun <S: Struct> KslExprStruct<S>.get(member: Float2Member<S>): KslStructMemberExpressionVector<KslFloat2, KslFloat1, S> =
    KslStructMemberExpressionVector(this, member, KslFloat2)
operator fun <S: Struct> KslExprStruct<S>.get(member: Float3Member<S>): KslStructMemberExpressionVector<KslFloat3, KslFloat1, S> =
    KslStructMemberExpressionVector(this, member, KslFloat3)
operator fun <S: Struct> KslExprStruct<S>.get(member: Float4Member<S>): KslStructMemberExpressionVector<KslFloat4, KslFloat1, S> =
    KslStructMemberExpressionVector(this, member, KslFloat4)

operator fun <S: Struct> KslExprStruct<S>.get(member: Int1Member<S>): KslStructMemberExpressionScalar<KslInt1, S> =
    KslStructMemberExpressionScalar(this, member, KslInt1)
operator fun <S: Struct> KslExprStruct<S>.get(member: Int2Member<S>): KslStructMemberExpressionVector<KslInt2, KslInt1, S> =
    KslStructMemberExpressionVector(this, member, KslInt2)
operator fun <S: Struct> KslExprStruct<S>.get(member: Int3Member<S>): KslStructMemberExpressionVector<KslInt3, KslInt1, S> =
    KslStructMemberExpressionVector(this, member, KslInt3)
operator fun <S: Struct> KslExprStruct<S>.get(member: Int4Member<S>): KslStructMemberExpressionVector<KslInt4, KslInt1, S> =
    KslStructMemberExpressionVector(this, member, KslInt4)

operator fun <S: Struct> KslExprStruct<S>.get(member: Uint1Member<S>): KslStructMemberExpressionScalar<KslUint1, S> =
    KslStructMemberExpressionScalar(this, member, KslUint1)
operator fun <S: Struct> KslExprStruct<S>.get(member: Uint2Member<S>): KslStructMemberExpressionVector<KslUint2, KslUint1, S> =
    KslStructMemberExpressionVector(this, member, KslUint2)
operator fun <S: Struct> KslExprStruct<S>.get(member: Uint3Member<S>): KslStructMemberExpressionVector<KslUint3, KslUint1, S> =
    KslStructMemberExpressionVector(this, member, KslUint3)
operator fun <S: Struct> KslExprStruct<S>.get(member: Uint4Member<S>): KslStructMemberExpressionVector<KslUint4, KslUint1, S> =
    KslStructMemberExpressionVector(this, member, KslUint4)

operator fun <S: Struct> KslExprStruct<S>.get(member: Bool1Member<S>): KslStructMemberExpressionScalar<KslBool1, S> =
    KslStructMemberExpressionScalar(this, member, KslBool1)
operator fun <S: Struct> KslExprStruct<S>.get(member: Bool2Member<S>): KslStructMemberExpressionVector<KslBool2, KslBool1, S> =
    KslStructMemberExpressionVector(this, member, KslBool2)
operator fun <S: Struct> KslExprStruct<S>.get(member: Bool3Member<S>): KslStructMemberExpressionVector<KslBool3, KslBool1, S> =
    KslStructMemberExpressionVector(this, member, KslBool3)
operator fun <S: Struct> KslExprStruct<S>.get(member: Bool4Member<S>): KslStructMemberExpressionVector<KslBool4, KslBool1, S> =
    KslStructMemberExpressionVector(this, member, KslBool4)

operator fun <S: Struct> KslExprStruct<S>.get(member: Mat2Member<S>): KslStructMemberExpressionMatrix<KslMat2, KslFloat2, S> =
    KslStructMemberExpressionMatrix(this, member, KslMat2)
operator fun <S: Struct> KslExprStruct<S>.get(member: Mat3Member<S>): KslStructMemberExpressionMatrix<KslMat3, KslFloat3, S> =
    KslStructMemberExpressionMatrix(this, member, KslMat3)
operator fun <S: Struct> KslExprStruct<S>.get(member: Mat4Member<S>): KslStructMemberExpressionMatrix<KslMat4, KslFloat4, S> =
    KslStructMemberExpressionMatrix(this, member, KslMat4)

operator fun <S: Struct> KslExprStruct<S>.get(member: Float1ArrayMember<S>): KslStructMemberExpressionScalarArray<KslFloat1, S> =
    KslStructMemberExpressionScalarArray(this, member, KslFloat1Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Float2ArrayMember<S>): KslStructMemberExpressionVectorArray<KslFloat2, KslFloat1, S> =
    KslStructMemberExpressionVectorArray(this, member, KslFloat2Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Float3ArrayMember<S>): KslStructMemberExpressionVectorArray<KslFloat3, KslFloat1, S> =
    KslStructMemberExpressionVectorArray(this, member, KslFloat3Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Float4ArrayMember<S>): KslStructMemberExpressionVectorArray<KslFloat4, KslFloat1, S> =
    KslStructMemberExpressionVectorArray(this, member, KslFloat4Array(member.arraySize))

operator fun <S: Struct> KslExprStruct<S>.get(member: Int1ArrayMember<S>): KslStructMemberExpressionScalarArray<KslInt1, S> =
    KslStructMemberExpressionScalarArray(this, member, KslInt1Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Int2ArrayMember<S>): KslStructMemberExpressionVectorArray<KslInt2, KslInt1, S> =
    KslStructMemberExpressionVectorArray(this, member, KslInt2Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Int3ArrayMember<S>): KslStructMemberExpressionVectorArray<KslInt3, KslInt1, S> =
    KslStructMemberExpressionVectorArray(this, member, KslInt3Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Int4ArrayMember<S>): KslStructMemberExpressionVectorArray<KslInt4, KslInt1, S> =
    KslStructMemberExpressionVectorArray(this, member, KslInt4Array(member.arraySize))

operator fun <S: Struct> KslExprStruct<S>.get(member: Uint1ArrayMember<S>): KslStructMemberExpressionScalarArray<KslUint1, S> =
    KslStructMemberExpressionScalarArray(this, member, KslUint1Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Uint2ArrayMember<S>): KslStructMemberExpressionVectorArray<KslUint2, KslUint1, S> =
    KslStructMemberExpressionVectorArray(this, member, KslUint2Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Uint3ArrayMember<S>): KslStructMemberExpressionVectorArray<KslUint3, KslUint1, S> =
    KslStructMemberExpressionVectorArray(this, member, KslUint3Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Uint4ArrayMember<S>): KslStructMemberExpressionVectorArray<KslUint4, KslUint1, S> =
    KslStructMemberExpressionVectorArray(this, member, KslUint4Array(member.arraySize))

operator fun <S: Struct> KslExprStruct<S>.get(member: Bool1ArrayMember<S>): KslStructMemberExpressionScalarArray<KslBool1, S> =
    KslStructMemberExpressionScalarArray(this, member, KslBool1Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Bool2ArrayMember<S>): KslStructMemberExpressionVectorArray<KslBool2, KslBool1, S> =
    KslStructMemberExpressionVectorArray(this, member, KslBool2Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Bool3ArrayMember<S>): KslStructMemberExpressionVectorArray<KslBool3, KslBool1, S> =
    KslStructMemberExpressionVectorArray(this, member, KslBool3Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Bool4ArrayMember<S>): KslStructMemberExpressionVectorArray<KslBool4, KslBool1, S> =
    KslStructMemberExpressionVectorArray(this, member, KslBool4Array(member.arraySize))

operator fun <S: Struct> KslExprStruct<S>.get(member: Mat2ArrayMember<S>): KslStructMemberExpressionMatrixArray<KslMat2, KslFloat2, S> =
    KslStructMemberExpressionMatrixArray(this, member, KslMat2Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Mat3ArrayMember<S>): KslStructMemberExpressionMatrixArray<KslMat3, KslFloat3, S> =
    KslStructMemberExpressionMatrixArray(this, member, KslMat3Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Mat4ArrayMember<S>): KslStructMemberExpressionMatrixArray<KslMat4, KslFloat4, S> =
    KslStructMemberExpressionMatrixArray(this, member, KslMat4Array(member.arraySize))

operator fun <S: Struct, N: Struct> KslExprStruct<S>.get(member: NestedStructMember<S, N>): KslStructMemberExpressionStruct<N, S> =
    KslStructMemberExpressionStruct(this, member, KslStruct(member.struct))
operator fun <S: Struct, N: Struct> KslExprStruct<S>.get(member: NestedStructArrayMember<S, N>): KslStructMemberExpressionStructArray<N, S> =
    KslStructMemberExpressionStructArray(this, member, KslStructArray(KslStruct(member.struct), member.arraySize))
