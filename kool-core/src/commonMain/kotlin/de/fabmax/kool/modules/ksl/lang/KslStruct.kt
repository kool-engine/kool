package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.StructMember

class KslStruct<T: Struct<T>>(val provider: () -> T) : KslType(provider().structName) {
    val struct: T = provider()

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
) : KslStructMemberExpression<S>(struct, member), KslScalarExpression<S>, KslAssignable<S> where S : KslScalar, S : KslNumericType {
    override val assignType: S get() = expressionType
    override val mutatingState: KslValue<*>? = struct.asAssignable()

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun generateAssignable(generator: KslGenerator): String = generator.structMemberAssignable(this)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.memberName}"
}

class KslStructMemberExpressionVector<V, S>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: V
) : KslStructMemberExpression<V>(struct, member), KslVectorExpression<V, S>, KslAssignable<V> where V : KslNumericType, V : KslVector<S>, S : KslScalar {
    override val assignType: V get() = expressionType
    override val mutatingState: KslValue<*>? = struct.asAssignable()

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun generateAssignable(generator: KslGenerator): String = generator.structMemberAssignable(this)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.memberName}"
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
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.memberName}"
}

class KslStructMemberExpressionScalarArray<S>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: KslArrayType<S>
) : KslStructMemberExpression<KslArrayType<S>>(struct, member), KslScalarArrayExpression<S> where S : KslScalar, S : KslNumericType {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.memberName}"
}

class KslStructMemberExpressionVectorArray<V, S>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: KslArrayType<V>
) : KslStructMemberExpression<KslArrayType<V>>(struct, member), KslVectorArrayExpression<V, S> where V : KslNumericType, V : KslVector<S>, S : KslScalar {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.memberName}"
}

class KslStructMemberExpressionMatrixArray<M, V>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: KslArrayType<M>
) : KslStructMemberExpression<KslArrayType<M>>(struct, member), KslMatrixArrayExpression<M, V> where M : KslNumericType, M : KslMatrix<V>, V : KslVector<*> {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.memberName}"
}

class KslStructMemberExpressionStruct<S: Struct<S>>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: KslStruct<S>
) : KslStructMemberExpression<KslStruct<S>>(struct, member), KslAssignable<KslStruct<S>>, KslExpression<KslStruct<S>> {
    override val assignType get() = expressionType
    override val mutatingState: KslValue<*>? = struct.asAssignable()

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun generateAssignable(generator: KslGenerator): String = generator.structMemberAssignable(this)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.memberName}"
}

class KslStructMemberExpressionStructArray<S: Struct<S>>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: KslStructArray<S>
) : KslStructMemberExpression<KslArrayType<KslStruct<S>>>(struct, member), KslArrayExpression<KslStruct<S>> {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.memberName}"
}

val <T: KslStruct<S>, S: Struct<S>> KslExpression<T>.struct: S get() = expressionType.provider().also {
    @Suppress("UNCHECKED_CAST")
    it.setupKslAccess(this as KslExpression<KslStruct<*>>)
}

val Struct<*>.Float1Member.ksl: KslStructMemberExpressionScalar<KslFloat1> get() =
    KslStructMemberExpressionScalar(parent.kslAccess, this, KslFloat1)
val Struct<*>.Float2Member.ksl: KslStructMemberExpressionVector<KslFloat2, KslFloat1> get() =
    KslStructMemberExpressionVector(parent.kslAccess, this, KslFloat2)
val Struct<*>.Float3Member.ksl: KslStructMemberExpressionVector<KslFloat3, KslFloat1> get() =
    KslStructMemberExpressionVector(parent.kslAccess, this, KslFloat3)
val Struct<*>.Float4Member.ksl: KslStructMemberExpressionVector<KslFloat4, KslFloat1> get() =
    KslStructMemberExpressionVector(parent.kslAccess, this, KslFloat4)

val Struct<*>.Int1Member.ksl: KslExprInt1 get() = KslStructMemberExpressionScalar(parent.kslAccess, this, KslInt1)
val Struct<*>.Int2Member.ksl: KslExprInt2 get() = KslStructMemberExpressionVector(parent.kslAccess, this, KslInt2)
val Struct<*>.Int3Member.ksl: KslExprInt3 get() = KslStructMemberExpressionVector(parent.kslAccess, this, KslInt3)
val Struct<*>.Int4Member.ksl: KslExprInt4 get() = KslStructMemberExpressionVector(parent.kslAccess, this, KslInt4)

val Struct<*>.Mat2Member.ksl: KslExprMat2 get() = KslStructMemberExpressionMatrix(parent.kslAccess, this, KslMat2)
val Struct<*>.Mat3Member.ksl: KslExprMat3 get() = KslStructMemberExpressionMatrix(parent.kslAccess, this, KslMat3)
val Struct<*>.Mat4Member.ksl: KslExprMat4 get() = KslStructMemberExpressionMatrix(parent.kslAccess, this, KslMat4)

val Struct<*>.Float1ArrayMember.ksl: KslExprFloat1Array get() = KslStructMemberExpressionScalarArray(parent.kslAccess, this, KslFloat1Array(arraySize))
val Struct<*>.Float2ArrayMember.ksl: KslExprFloat2Array get() = KslStructMemberExpressionVectorArray(parent.kslAccess, this, KslFloat2Array(arraySize))
val Struct<*>.Float3ArrayMember.ksl: KslExprFloat3Array get() = KslStructMemberExpressionVectorArray(parent.kslAccess, this, KslFloat3Array(arraySize))
val Struct<*>.Float4ArrayMember.ksl: KslExprFloat4Array get() = KslStructMemberExpressionVectorArray(parent.kslAccess, this, KslFloat4Array(arraySize))

val Struct<*>.Int1ArrayMember.ksl: KslExprInt1Array get() = KslStructMemberExpressionScalarArray(parent.kslAccess, this, KslInt1Array(arraySize))
val Struct<*>.Int2ArrayMember.ksl: KslExprInt2Array get() = KslStructMemberExpressionVectorArray(parent.kslAccess, this, KslInt2Array(arraySize))
val Struct<*>.Int3ArrayMember.ksl: KslExprInt3Array get() = KslStructMemberExpressionVectorArray(parent.kslAccess, this, KslInt3Array(arraySize))
val Struct<*>.Int4ArrayMember.ksl: KslExprInt4Array get() = KslStructMemberExpressionVectorArray(parent.kslAccess, this, KslInt4Array(arraySize))

val Struct<*>.Mat2ArrayMember.ksl: KslExprMat2Array get() = KslStructMemberExpressionMatrixArray(parent.kslAccess, this, KslMat2Array(arraySize))
val Struct<*>.Mat3ArrayMember.ksl: KslExprMat3Array get() = KslStructMemberExpressionMatrixArray(parent.kslAccess, this, KslMat3Array(arraySize))
val Struct<*>.Mat4ArrayMember.ksl: KslExprMat4Array get() = KslStructMemberExpressionMatrixArray(parent.kslAccess, this, KslMat4Array(arraySize))

@Suppress("UNCHECKED_CAST")
val <S: Struct<S>> Struct<S>.ksl: KslStructMemberExpressionStruct<S> get() =
    KslStructMemberExpressionStruct(kslAccess, this, KslStruct { this as S })
val <S: Struct<S>> Struct<*>.NestedStructArrayMember<S>.ksl: KslArrayExpression<KslStruct<S>> get() =
    KslStructMemberExpressionStructArray(parent.kslAccess, this, KslStructArray(KslStruct(structProvider), arraySize))
