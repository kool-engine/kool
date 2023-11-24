package de.fabmax.kool.modules.ksl.lang

// common ivec3 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslTypeInt3>.x get() = int1("x")
val KslExpression<KslTypeInt3>.y get() = int1("y")
val KslExpression<KslTypeInt3>.z get() = int1("z")

val KslExpression<KslTypeInt3>.r get() = int1("r")
val KslExpression<KslTypeInt3>.g get() = int1("g")
val KslExpression<KslTypeInt3>.b get() = int1("b")

val KslExpression<KslTypeInt3>.xy get() = int2("xy")
val KslExpression<KslTypeInt3>.xz get() = int2("xz")
val KslExpression<KslTypeInt3>.yz get() = int2("xz")

val KslExpression<KslTypeInt3>.rg get() = int2("rg")
val KslExpression<KslTypeInt3>.rb get() = int2("rb")
val KslExpression<KslTypeInt3>.gb get() = int2("gb")
