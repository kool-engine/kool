@file:Suppress("UNCHECKED_CAST")

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

abstract class KslStructMemberExpression<T: KslType>(val struct: KslExpression<KslStruct<*>>, val member: StructMember) : KslExpression<T>

class KslStructMemberExpressionScalar<S>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: S
) : KslStructMemberExpression<S>(struct, member), KslScalarExpression<S>, KslAssignable<S> where S : KslScalar, S : KslType {
    override val assignType: S get() = expressionType
    override val mutatingState: KslValue<*>? = struct.asAssignable()

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun generateAssignable(generator: KslGenerator): String = generator.structMemberAssignable(this)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionVector<V, S>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: V
) : KslStructMemberExpression<V>(struct, member), KslVectorExpression<V, S>, KslAssignable<V> where V : KslType, V : KslVector<S>, S : KslScalar {
    override val assignType: V get() = expressionType
    override val mutatingState: KslValue<*>? = struct.asAssignable()

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun generateAssignable(generator: KslGenerator): String = generator.structMemberAssignable(this)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionMatrix<M, V>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: M
) : KslStructMemberExpression<M>(struct, member), KslMatrixExpression<M, V>, KslAssignable<M> where M : KslNumericType, M : KslMatrix<V>, V : KslVector<*> {
    override val assignType: M get() = expressionType
    override val mutatingState: KslValue<*>? = struct.asAssignable()

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun generateAssignable(generator: KslGenerator): String = generator.structMemberAssignable(this)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionScalarArray<S>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: KslArrayType<S>
) : KslStructMemberExpression<KslArrayType<S>>(struct, member), KslScalarArrayExpression<S> where S : KslScalar, S : KslType {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionVectorArray<V, S>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: KslArrayType<V>
) : KslStructMemberExpression<KslArrayType<V>>(struct, member), KslVectorArrayExpression<V, S> where V : KslType, V : KslVector<S>, S : KslScalar {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionMatrixArray<M, V>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: KslArrayType<M>
) : KslStructMemberExpression<KslArrayType<M>>(struct, member), KslMatrixArrayExpression<M, V> where M : KslNumericType, M : KslMatrix<V>, V : KslVector<*> {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionStruct<S: Struct>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: KslStruct<S>
) : KslStructMemberExpression<KslStruct<S>>(struct, member), KslAssignable<KslStruct<S>>, KslExpression<KslStruct<S>> {
    override val assignType get() = expressionType
    override val mutatingState: KslValue<*>? = struct.asAssignable()

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun generateAssignable(generator: KslGenerator): String = generator.structMemberAssignable(this)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

class KslStructMemberExpressionStructArray<S: Struct>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: KslStructArray<S>
) : KslStructMemberExpression<KslArrayType<KslStruct<S>>>(struct, member), KslArrayExpression<KslStruct<S>> {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.name}"
}

operator fun <S: Struct> KslExprStruct<S>.get(member: Float1Member): KslStructMemberExpressionScalar<KslFloat1> =
    KslStructMemberExpressionScalar(this as KslExprStruct<*>, member, KslFloat1)
operator fun <S: Struct> KslExprStruct<S>.get(member: Float2Member): KslStructMemberExpressionVector<KslFloat2, KslFloat1> =
    KslStructMemberExpressionVector(this as KslExprStruct<*>, member, KslFloat2)
operator fun <S: Struct> KslExprStruct<S>.get(member: Float3Member): KslStructMemberExpressionVector<KslFloat3, KslFloat1> =
    KslStructMemberExpressionVector(this as KslExprStruct<*>, member, KslFloat3)
operator fun <S: Struct> KslExprStruct<S>.get(member: Float4Member): KslStructMemberExpressionVector<KslFloat4, KslFloat1> =
    KslStructMemberExpressionVector(this as KslExprStruct<*>, member, KslFloat4)

operator fun <S: Struct> KslExprStruct<S>.get(member: Int1Member): KslStructMemberExpressionScalar<KslInt1> =
    KslStructMemberExpressionScalar(this as KslExprStruct<*>, member, KslInt1)
operator fun <S: Struct> KslExprStruct<S>.get(member: Int2Member): KslStructMemberExpressionVector<KslInt2, KslInt1> =
    KslStructMemberExpressionVector(this as KslExprStruct<*>, member, KslInt2)
operator fun <S: Struct> KslExprStruct<S>.get(member: Int3Member): KslStructMemberExpressionVector<KslInt3, KslInt1> =
    KslStructMemberExpressionVector(this as KslExprStruct<*>, member, KslInt3)
operator fun <S: Struct> KslExprStruct<S>.get(member: Int4Member): KslStructMemberExpressionVector<KslInt4, KslInt1> =
    KslStructMemberExpressionVector(this as KslExprStruct<*>, member, KslInt4)

operator fun <S: Struct> KslExprStruct<S>.get(member: Uint1Member): KslStructMemberExpressionScalar<KslUint1> =
    KslStructMemberExpressionScalar(this as KslExprStruct<*>, member, KslUint1)
operator fun <S: Struct> KslExprStruct<S>.get(member: Uint2Member): KslStructMemberExpressionVector<KslUint2, KslUint1> =
    KslStructMemberExpressionVector(this as KslExprStruct<*>, member, KslUint2)
operator fun <S: Struct> KslExprStruct<S>.get(member: Uint3Member): KslStructMemberExpressionVector<KslUint3, KslUint1> =
    KslStructMemberExpressionVector(this as KslExprStruct<*>, member, KslUint3)
operator fun <S: Struct> KslExprStruct<S>.get(member: Uint4Member): KslStructMemberExpressionVector<KslUint4, KslUint1> =
    KslStructMemberExpressionVector(this as KslExprStruct<*>, member, KslUint4)

operator fun <S: Struct> KslExprStruct<S>.get(member: Bool1Member): KslStructMemberExpressionScalar<KslBool1> =
    KslStructMemberExpressionScalar(this as KslExprStruct<*>, member, KslBool1)
operator fun <S: Struct> KslExprStruct<S>.get(member: Bool2Member): KslStructMemberExpressionVector<KslBool2, KslBool1> =
    KslStructMemberExpressionVector(this as KslExprStruct<*>, member, KslBool2)
operator fun <S: Struct> KslExprStruct<S>.get(member: Bool3Member): KslStructMemberExpressionVector<KslBool3, KslBool1> =
    KslStructMemberExpressionVector(this as KslExprStruct<*>, member, KslBool3)
operator fun <S: Struct> KslExprStruct<S>.get(member: Bool4Member): KslStructMemberExpressionVector<KslBool4, KslBool1> =
    KslStructMemberExpressionVector(this as KslExprStruct<*>, member, KslBool4)

operator fun <S: Struct> KslExprStruct<S>.get(member: Mat2Member): KslStructMemberExpressionMatrix<KslMat2, KslFloat2> =
    KslStructMemberExpressionMatrix(this as KslExprStruct<*>, member, KslMat2)
operator fun <S: Struct> KslExprStruct<S>.get(member: Mat3Member): KslStructMemberExpressionMatrix<KslMat3, KslFloat3> =
    KslStructMemberExpressionMatrix(this as KslExprStruct<*>, member, KslMat3)
operator fun <S: Struct> KslExprStruct<S>.get(member: Mat4Member): KslStructMemberExpressionMatrix<KslMat4, KslFloat4> =
    KslStructMemberExpressionMatrix(this as KslExprStruct<*>, member, KslMat4)

operator fun <S: Struct> KslExprStruct<S>.get(member: Float1ArrayMember): KslStructMemberExpressionScalarArray<KslFloat1> =
    KslStructMemberExpressionScalarArray(this as KslExprStruct<*>, member, KslFloat1Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Float2ArrayMember): KslStructMemberExpressionVectorArray<KslFloat2, KslFloat1> =
    KslStructMemberExpressionVectorArray(this as KslExprStruct<*>, member, KslFloat2Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Float3ArrayMember): KslStructMemberExpressionVectorArray<KslFloat3, KslFloat1> =
    KslStructMemberExpressionVectorArray(this as KslExprStruct<*>, member, KslFloat3Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Float4ArrayMember): KslStructMemberExpressionVectorArray<KslFloat4, KslFloat1> =
    KslStructMemberExpressionVectorArray(this as KslExprStruct<*>, member, KslFloat4Array(member.arraySize))

operator fun <S: Struct> KslExprStruct<S>.get(member: Int1ArrayMember): KslStructMemberExpressionScalarArray<KslInt1> =
    KslStructMemberExpressionScalarArray(this as KslExprStruct<*>, member, KslInt1Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Int2ArrayMember): KslStructMemberExpressionVectorArray<KslInt2, KslInt1> =
    KslStructMemberExpressionVectorArray(this as KslExprStruct<*>, member, KslInt2Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Int3ArrayMember): KslStructMemberExpressionVectorArray<KslInt3, KslInt1> =
    KslStructMemberExpressionVectorArray(this as KslExprStruct<*>, member, KslInt3Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Int4ArrayMember): KslStructMemberExpressionVectorArray<KslInt4, KslInt1> =
    KslStructMemberExpressionVectorArray(this as KslExprStruct<*>, member, KslInt4Array(member.arraySize))

operator fun <S: Struct> KslExprStruct<S>.get(member: Uint1ArrayMember): KslStructMemberExpressionScalarArray<KslUint1> =
    KslStructMemberExpressionScalarArray(this as KslExprStruct<*>, member, KslUint1Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Uint2ArrayMember): KslStructMemberExpressionVectorArray<KslUint2, KslUint1> =
    KslStructMemberExpressionVectorArray(this as KslExprStruct<*>, member, KslUint2Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Uint3ArrayMember): KslStructMemberExpressionVectorArray<KslUint3, KslUint1> =
    KslStructMemberExpressionVectorArray(this as KslExprStruct<*>, member, KslUint3Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Uint4ArrayMember): KslStructMemberExpressionVectorArray<KslUint4, KslUint1> =
    KslStructMemberExpressionVectorArray(this as KslExprStruct<*>, member, KslUint4Array(member.arraySize))

operator fun <S: Struct> KslExprStruct<S>.get(member: Bool1ArrayMember): KslStructMemberExpressionScalarArray<KslBool1> =
    KslStructMemberExpressionScalarArray(this as KslExprStruct<*>, member, KslBool1Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Bool2ArrayMember): KslStructMemberExpressionVectorArray<KslBool2, KslBool1> =
    KslStructMemberExpressionVectorArray(this as KslExprStruct<*>, member, KslBool2Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Bool3ArrayMember): KslStructMemberExpressionVectorArray<KslBool3, KslBool1> =
    KslStructMemberExpressionVectorArray(this as KslExprStruct<*>, member, KslBool3Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Bool4ArrayMember): KslStructMemberExpressionVectorArray<KslBool4, KslBool1> =
    KslStructMemberExpressionVectorArray(this as KslExprStruct<*>, member, KslBool4Array(member.arraySize))

operator fun <S: Struct> KslExprStruct<S>.get(member: Mat2ArrayMember): KslStructMemberExpressionMatrixArray<KslMat2, KslFloat2> =
    KslStructMemberExpressionMatrixArray(this as KslExprStruct<*>, member, KslMat2Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Mat3ArrayMember): KslStructMemberExpressionMatrixArray<KslMat3, KslFloat3> =
    KslStructMemberExpressionMatrixArray(this as KslExprStruct<*>, member, KslMat3Array(member.arraySize))
operator fun <S: Struct> KslExprStruct<S>.get(member: Mat4ArrayMember): KslStructMemberExpressionMatrixArray<KslMat4, KslFloat4> =
    KslStructMemberExpressionMatrixArray(this as KslExprStruct<*>, member, KslMat4Array(member.arraySize))

operator fun <S: Struct, N: Struct> KslExprStruct<S>.get(member: NestedStructMember<N>): KslStructMemberExpressionStruct<N> =
    KslStructMemberExpressionStruct(this as KslExprStruct<*>, member, KslStruct(member.struct))
operator fun <S: Struct, N: Struct> KslExprStruct<S>.get(member: NestedStructArrayMember<N>): KslStructMemberExpressionStructArray<N> =
    KslStructMemberExpressionStructArray(this as KslExprStruct<*>, member, KslStructArray(KslStruct(member.struct), member.arraySize))
