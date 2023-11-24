package de.fabmax.kool.modules.ksl.lang

// common ivec4 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslTypeInt4>.x get() = int1("x")
val KslExpression<KslTypeInt4>.y get() = int1("y")
val KslExpression<KslTypeInt4>.z get() = int1("z")
val KslExpression<KslTypeInt4>.w get() = int1("w")

val KslExpression<KslTypeInt4>.r get() = int1("r")
val KslExpression<KslTypeInt4>.g get() = int1("g")
val KslExpression<KslTypeInt4>.b get() = int1("b")
val KslExpression<KslTypeInt4>.a get() = int1("a")

val KslExpression<KslTypeInt4>.xy get() = int2("xy")
val KslExpression<KslTypeInt4>.xz get() = int2("xz")
val KslExpression<KslTypeInt4>.yz get() = int2("xz")

val KslExpression<KslTypeInt4>.rg get() = int2("rg")
val KslExpression<KslTypeInt4>.rb get() = int2("rb")
val KslExpression<KslTypeInt4>.gb get() = int2("gb")

val KslExpression<KslTypeInt4>.xyz get() = int3("xyz")
val KslExpression<KslTypeInt4>.rgb get() = int3("rgb")
