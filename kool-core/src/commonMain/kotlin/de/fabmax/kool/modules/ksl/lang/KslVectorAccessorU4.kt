package de.fabmax.kool.modules.ksl.lang

// common uvec4 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslUint4>.x get() = uint1("x")
val KslExpression<KslUint4>.y get() = uint1("y")
val KslExpression<KslUint4>.z get() = uint1("z")
val KslExpression<KslUint4>.w get() = uint1("w")

val KslExpression<KslUint4>.r get() = uint1("r")
val KslExpression<KslUint4>.g get() = uint1("g")
val KslExpression<KslUint4>.b get() = uint1("b")
val KslExpression<KslUint4>.a get() = uint1("a")

val KslExpression<KslUint4>.xy get() = uint2("xy")
val KslExpression<KslUint4>.xz get() = uint2("xz")
val KslExpression<KslUint4>.yz get() = uint2("yz")

val KslExpression<KslUint4>.rg get() = uint2("rg")
val KslExpression<KslUint4>.rb get() = uint2("rb")
val KslExpression<KslUint4>.gb get() = uint2("gb")

val KslExpression<KslUint4>.xyz get() = uint3("xyz")
val KslExpression<KslUint4>.rgb get() = uint3("rgb")
