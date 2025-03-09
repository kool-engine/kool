package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator

interface KslAssignable<T: KslType> {
    val assignType: T
    val mutatingState: KslValue<*>?

    fun generateAssignable(generator: KslGenerator): String
    fun toPseudoCode(): String

    fun checkIsAssignable(scopeBuilder: KslScopeBuilder) {
        val mutState = mutatingState ?: throw IllegalArgumentException("Assignable has no mutable state")
        if (!mutState.isMutable) {
            throw IllegalArgumentException("Provided assign target is not mutable")
        }
    }
}

fun KslExpression<*>.asAssignable(): KslValue<*>? = when (this) {
    is KslValue<*> -> this
    is KslAssignable<*> -> mutatingState
    is KslStructMemberExpression<*> -> struct.asAssignable()
    else -> null
}
