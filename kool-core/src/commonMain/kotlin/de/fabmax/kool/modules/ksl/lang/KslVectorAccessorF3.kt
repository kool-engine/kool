package de.fabmax.kool.modules.ksl.lang

// common vec3 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslFloat3>.x get() = float1("x")
val KslExpression<KslFloat3>.y get() = float1("y")
val KslExpression<KslFloat3>.z get() = float1("z")

val KslExpression<KslFloat3>.r get() = float1("r")
val KslExpression<KslFloat3>.g get() = float1("g")
val KslExpression<KslFloat3>.b get() = float1("b")

val KslExpression<KslFloat3>.xy get() = float2("xy")
val KslExpression<KslFloat3>.xz get() = float2("xz")
val KslExpression<KslFloat3>.yz get() = float2("yz")

val KslExpression<KslFloat3>.rg get() = float2("rg")
val KslExpression<KslFloat3>.rb get() = float2("rb")
val KslExpression<KslFloat3>.gb get() = float2("gb")
