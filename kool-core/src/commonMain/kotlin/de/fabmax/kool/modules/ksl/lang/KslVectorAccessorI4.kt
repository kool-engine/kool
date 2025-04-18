package de.fabmax.kool.modules.ksl.lang

// common ivec4 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslInt4>.x get() = int1("x")
val KslExpression<KslInt4>.y get() = int1("y")
val KslExpression<KslInt4>.z get() = int1("z")
val KslExpression<KslInt4>.w get() = int1("w")

val KslExpression<KslInt4>.r get() = int1("r")
val KslExpression<KslInt4>.g get() = int1("g")
val KslExpression<KslInt4>.b get() = int1("b")
val KslExpression<KslInt4>.a get() = int1("a")

val KslExpression<KslInt4>.xy get() = int2("xy")
val KslExpression<KslInt4>.xz get() = int2("xz")
val KslExpression<KslInt4>.yz get() = int2("yz")

val KslExpression<KslInt4>.rg get() = int2("rg")
val KslExpression<KslInt4>.rb get() = int2("rb")
val KslExpression<KslInt4>.gb get() = int2("gb")

val KslExpression<KslInt4>.xyz get() = int3("xyz")
val KslExpression<KslInt4>.rgb get() = int3("rgb")
