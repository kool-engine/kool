package de.fabmax.kool.modules.ksl.lang

// common uvec4 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslTypeUint4>.x get() = uint1("x")
val KslExpression<KslTypeUint4>.y get() = uint1("y")
val KslExpression<KslTypeUint4>.z get() = uint1("z")
val KslExpression<KslTypeUint4>.w get() = uint1("w")

val KslExpression<KslTypeUint4>.r get() = uint1("r")
val KslExpression<KslTypeUint4>.g get() = uint1("g")
val KslExpression<KslTypeUint4>.b get() = uint1("b")
val KslExpression<KslTypeUint4>.a get() = uint1("a")

val KslExpression<KslTypeUint4>.xy get() = uint2("xy")
val KslExpression<KslTypeUint4>.xz get() = uint2("xz")
val KslExpression<KslTypeUint4>.yz get() = uint2("xz")

val KslExpression<KslTypeUint4>.rg get() = uint2("rg")
val KslExpression<KslTypeUint4>.rb get() = uint2("rb")
val KslExpression<KslTypeUint4>.gb get() = uint2("gb")

val KslExpression<KslTypeUint4>.xyz get() = uint3("xyz")
val KslExpression<KslTypeUint4>.rgb get() = uint3("rgb")
