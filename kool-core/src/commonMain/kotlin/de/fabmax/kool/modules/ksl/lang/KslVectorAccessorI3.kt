package de.fabmax.kool.modules.ksl.lang

// common ivec3 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslInt3>.x get() = int1("x")
val KslExpression<KslInt3>.y get() = int1("y")
val KslExpression<KslInt3>.z get() = int1("z")

val KslExpression<KslInt3>.r get() = int1("r")
val KslExpression<KslInt3>.g get() = int1("g")
val KslExpression<KslInt3>.b get() = int1("b")

val KslExpression<KslInt3>.xy get() = int2("xy")
val KslExpression<KslInt3>.xz get() = int2("xz")
val KslExpression<KslInt3>.yz get() = int2("yz")

val KslExpression<KslInt3>.rg get() = int2("rg")
val KslExpression<KslInt3>.rb get() = int2("rb")
val KslExpression<KslInt3>.gb get() = int2("gb")
