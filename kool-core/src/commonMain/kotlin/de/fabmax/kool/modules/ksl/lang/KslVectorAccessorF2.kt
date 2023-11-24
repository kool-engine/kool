package de.fabmax.kool.modules.ksl.lang

// all possible vec2 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslTypeFloat2>.x get() = float1("x")
val KslExpression<KslTypeFloat2>.y get() = float1("y")

val KslExpression<KslTypeFloat2>.r get() = float1("r")
val KslExpression<KslTypeFloat2>.g get() = float1("g")

val KslExpression<KslTypeFloat2>.xx get() = float2("xx")
val KslExpression<KslTypeFloat2>.yy get() = float2("yy")
val KslExpression<KslTypeFloat2>.yx get() = float2("yx")

val KslExpression<KslTypeFloat2>.rr get() = float2("rr")
val KslExpression<KslTypeFloat2>.gg get() = float2("gg")
val KslExpression<KslTypeFloat2>.gr get() = float2("gr")
