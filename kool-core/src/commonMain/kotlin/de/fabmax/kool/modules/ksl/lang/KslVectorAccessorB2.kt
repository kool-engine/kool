package de.fabmax.kool.modules.ksl.lang

// all possible bvec2 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslTypeBool2>.x get() = bool1("x")
val KslExpression<KslTypeBool2>.y get() = bool1("y")

val KslExpression<KslTypeBool2>.r get() = bool1("r")
val KslExpression<KslTypeBool2>.g get() = bool1("g")

val KslExpression<KslTypeBool2>.xx get() = bool2("xx")
val KslExpression<KslTypeBool2>.yy get() = bool2("yy")
val KslExpression<KslTypeBool2>.yx get() = bool2("yx")

val KslExpression<KslTypeBool2>.rr get() = bool2("rr")
val KslExpression<KslTypeBool2>.gg get() = bool2("gg")
val KslExpression<KslTypeBool2>.gr get() = bool2("gr")
