package de.fabmax.kool.demo.fluidsim

import de.fabmax.kool.modules.ksl.lang.*

fun KslExpression<KslInt1>.toFloating(): KslScalarExpression<KslFloat1> {
    return toFloat1() * KslValueFloat1(1f / 65536f)
}

fun KslScalarExpression<KslFloat1>.toFixed(): KslScalarExpression<KslInt1> {
    return (this * KslValueFloat1(65536f)).toInt1()
}