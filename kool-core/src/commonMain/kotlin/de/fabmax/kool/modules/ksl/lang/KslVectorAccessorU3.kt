package de.fabmax.kool.modules.ksl.lang

// common uvec3 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslUint3>.x get() = uint1("x")
val KslExpression<KslUint3>.y get() = uint1("y")
val KslExpression<KslUint3>.z get() = uint1("z")

val KslExpression<KslUint3>.r get() = uint1("r")
val KslExpression<KslUint3>.g get() = uint1("g")
val KslExpression<KslUint3>.b get() = uint1("b")

val KslExpression<KslUint3>.xy get() = uint2("xy")
val KslExpression<KslUint3>.xz get() = uint2("xz")
val KslExpression<KslUint3>.yz get() = uint2("yz")

val KslExpression<KslUint3>.rg get() = uint2("rg")
val KslExpression<KslUint3>.rb get() = uint2("rb")
val KslExpression<KslUint3>.gb get() = uint2("gb")
