package de.fabmax.kool.modules.ksl.lang

// all possible ivec2 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslTypeInt2>.x get() = int1("x")
val KslExpression<KslTypeInt2>.y get() = int1("y")

val KslExpression<KslTypeInt2>.r get() = int1("r")
val KslExpression<KslTypeInt2>.g get() = int1("g")

val KslExpression<KslTypeInt2>.xx get() = int2("xx")
val KslExpression<KslTypeInt2>.yy get() = int2("yy")
val KslExpression<KslTypeInt2>.yx get() = int2("yx")

val KslExpression<KslTypeInt2>.rr get() = int2("rr")
val KslExpression<KslTypeInt2>.gg get() = int2("gg")
val KslExpression<KslTypeInt2>.gr get() = int2("gr")
