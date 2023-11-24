package de.fabmax.kool.modules.ksl.lang

// common ivec4 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslTypeBool4>.x get() = bool1("x")
val KslExpression<KslTypeBool4>.y get() = bool1("y")
val KslExpression<KslTypeBool4>.z get() = bool1("z")
val KslExpression<KslTypeBool4>.w get() = bool1("w")

val KslExpression<KslTypeBool4>.r get() = bool1("r")
val KslExpression<KslTypeBool4>.g get() = bool1("g")
val KslExpression<KslTypeBool4>.b get() = bool1("b")
val KslExpression<KslTypeBool4>.a get() = bool1("a")

val KslExpression<KslTypeBool4>.xy get() = bool2("xy")
val KslExpression<KslTypeBool4>.xz get() = bool2("xz")
val KslExpression<KslTypeBool4>.yz get() = bool2("xz")

val KslExpression<KslTypeBool4>.rg get() = bool2("rg")
val KslExpression<KslTypeBool4>.rb get() = bool2("rb")
val KslExpression<KslTypeBool4>.gb get() = bool2("gb")

val KslExpression<KslTypeBool4>.xyz get() = bool3("xyz")
val KslExpression<KslTypeBool4>.rgb get() = bool3("rgb")
