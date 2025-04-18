package de.fabmax.kool.modules.ksl.lang

// common vec3 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslBool3>.x get() = bool1("x")
val KslExpression<KslBool3>.y get() = bool1("y")
val KslExpression<KslBool3>.z get() = bool1("z")

val KslExpression<KslBool3>.r get() = bool1("r")
val KslExpression<KslBool3>.g get() = bool1("g")
val KslExpression<KslBool3>.b get() = bool1("b")

val KslExpression<KslBool3>.xy get() = bool2("xy")
val KslExpression<KslBool3>.xz get() = bool2("xz")
val KslExpression<KslBool3>.yz get() = bool2("yz")

val KslExpression<KslBool3>.rg get() = bool2("rg")
val KslExpression<KslBool3>.rb get() = bool2("rb")
val KslExpression<KslBool3>.gb get() = bool2("gb")
