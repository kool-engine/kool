package de.fabmax.kool.modules.ksl.lang

// common vec3 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslTypeBool3>.x get() = bool1("x")
val KslExpression<KslTypeBool3>.y get() = bool1("y")
val KslExpression<KslTypeBool3>.z get() = bool1("z")

val KslExpression<KslTypeBool3>.r get() = bool1("r")
val KslExpression<KslTypeBool3>.g get() = bool1("g")
val KslExpression<KslTypeBool3>.b get() = bool1("b")

val KslExpression<KslTypeBool3>.xy get() = bool2("xy")
val KslExpression<KslTypeBool3>.xz get() = bool2("xz")
val KslExpression<KslTypeBool3>.yz get() = bool2("xz")

val KslExpression<KslTypeBool3>.rg get() = bool2("rg")
val KslExpression<KslTypeBool3>.rb get() = bool2("rb")
val KslExpression<KslTypeBool3>.gb get() = bool2("gb")
