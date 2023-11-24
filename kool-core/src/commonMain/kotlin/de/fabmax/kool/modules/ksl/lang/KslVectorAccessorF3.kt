package de.fabmax.kool.modules.ksl.lang

// common vec3 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslTypeFloat3>.x get() = float1("x")
val KslExpression<KslTypeFloat3>.y get() = float1("y")
val KslExpression<KslTypeFloat3>.z get() = float1("z")

val KslExpression<KslTypeFloat3>.r get() = float1("r")
val KslExpression<KslTypeFloat3>.g get() = float1("g")
val KslExpression<KslTypeFloat3>.b get() = float1("b")

val KslExpression<KslTypeFloat3>.xy get() = float2("xy")
val KslExpression<KslTypeFloat3>.xz get() = float2("xz")
val KslExpression<KslTypeFloat3>.yz get() = float2("xz")

val KslExpression<KslTypeFloat3>.rg get() = float2("rg")
val KslExpression<KslTypeFloat3>.rb get() = float2("rb")
val KslExpression<KslTypeFloat3>.gb get() = float2("gb")
