package de.fabmax.kool.modules.ksl.lang

// all possible vec2 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslFloat2>.x get() = float1("x")
val KslExpression<KslFloat2>.y get() = float1("y")

val KslExpression<KslFloat2>.r get() = float1("r")
val KslExpression<KslFloat2>.g get() = float1("g")

val KslExpression<KslFloat2>.xx get() = float2("xx")
val KslExpression<KslFloat2>.yy get() = float2("yy")
val KslExpression<KslFloat2>.yx get() = float2("yx")

val KslExpression<KslFloat2>.rr get() = float2("rr")
val KslExpression<KslFloat2>.gg get() = float2("gg")
val KslExpression<KslFloat2>.gr get() = float2("gr")
