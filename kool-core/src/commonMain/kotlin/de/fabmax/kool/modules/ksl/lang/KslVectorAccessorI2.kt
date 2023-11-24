package de.fabmax.kool.modules.ksl.lang

// all possible ivec2 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslInt2>.x get() = int1("x")
val KslExpression<KslInt2>.y get() = int1("y")

val KslExpression<KslInt2>.r get() = int1("r")
val KslExpression<KslInt2>.g get() = int1("g")

val KslExpression<KslInt2>.xx get() = int2("xx")
val KslExpression<KslInt2>.yy get() = int2("yy")
val KslExpression<KslInt2>.yx get() = int2("yx")

val KslExpression<KslInt2>.rr get() = int2("rr")
val KslExpression<KslInt2>.gg get() = int2("gg")
val KslExpression<KslInt2>.gr get() = int2("gr")
