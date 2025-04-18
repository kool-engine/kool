package de.fabmax.kool.modules.ksl.lang

// common ivec4 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslBool4>.x get() = bool1("x")
val KslExpression<KslBool4>.y get() = bool1("y")
val KslExpression<KslBool4>.z get() = bool1("z")
val KslExpression<KslBool4>.w get() = bool1("w")

val KslExpression<KslBool4>.r get() = bool1("r")
val KslExpression<KslBool4>.g get() = bool1("g")
val KslExpression<KslBool4>.b get() = bool1("b")
val KslExpression<KslBool4>.a get() = bool1("a")

val KslExpression<KslBool4>.xy get() = bool2("xy")
val KslExpression<KslBool4>.xz get() = bool2("xz")
val KslExpression<KslBool4>.yz get() = bool2("yz")

val KslExpression<KslBool4>.rg get() = bool2("rg")
val KslExpression<KslBool4>.rb get() = bool2("rb")
val KslExpression<KslBool4>.gb get() = bool2("gb")

val KslExpression<KslBool4>.xyz get() = bool3("xyz")
val KslExpression<KslBool4>.rgb get() = bool3("rgb")
