package de.fabmax.kool.modules.ksl.lang

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

abstract class KslStructMemberExpression(val struct: KslExpression<KslStruct<*>>, val member: StructMember)

class KslStructMemberExpressionScalar<S>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: S
) : KslStructMemberExpression(struct, member), KslScalarExpression<S> where S : KslScalar, S : KslNumericType {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.memberName}"
}

class KslStructMemberExpressionVector<V, S>(
    struct: KslExpression<KslStruct<*>>,
    member: StructMember,
    override val expressionType: V
) : KslStructMemberExpression(struct, member), KslVectorExpression<V, S> where V : KslNumericType, V : KslVector<S>, S : KslScalar {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(struct)
    override fun toPseudoCode(): String = "${struct.toPseudoCode()}.${member.memberName}"
}

val <T: KslStruct<S>, S: Struct<S>> KslExpression<T>.struct: S get() = expressionType.provider().also {
    @Suppress("UNCHECKED_CAST")
    it.setupKslAccess(this as KslExpression<KslStruct<*>>)
}

val Struct<*>.Float1Member.ksl: KslExprFloat1 get() = KslStructMemberExpressionScalar(parent.kslAccess, this, KslFloat1)
val Struct<*>.Float2Member.ksl: KslExprFloat2 get() = KslStructMemberExpressionVector(parent.kslAccess, this, KslFloat2)
val Struct<*>.Float3Member.ksl: KslExprFloat3 get() = KslStructMemberExpressionVector(parent.kslAccess, this, KslFloat3)
val Struct<*>.Float4Member.ksl: KslExprFloat4 get() = KslStructMemberExpressionVector(parent.kslAccess, this, KslFloat4)
