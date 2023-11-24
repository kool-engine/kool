package de.fabmax.kool.modules.ksl.lang

// common uvec3 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslTypeUint3>.x get() = uint1("x")
val KslExpression<KslTypeUint3>.y get() = uint1("y")
val KslExpression<KslTypeUint3>.z get() = uint1("z")

val KslExpression<KslTypeUint3>.r get() = uint1("r")
val KslExpression<KslTypeUint3>.g get() = uint1("g")
val KslExpression<KslTypeUint3>.b get() = uint1("b")

val KslExpression<KslTypeUint3>.xy get() = uint2("xy")
val KslExpression<KslTypeUint3>.xz get() = uint2("xz")
val KslExpression<KslTypeUint3>.yz get() = uint2("xz")

val KslExpression<KslTypeUint3>.rg get() = uint2("rg")
val KslExpression<KslTypeUint3>.rb get() = uint2("rb")
val KslExpression<KslTypeUint3>.gb get() = uint2("gb")
