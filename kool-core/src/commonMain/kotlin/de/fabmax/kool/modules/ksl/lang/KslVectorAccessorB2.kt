package de.fabmax.kool.modules.ksl.lang

// all possible bvec2 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslBool2>.x get() = bool1("x")
val KslExpression<KslBool2>.y get() = bool1("y")

val KslExpression<KslBool2>.r get() = bool1("r")
val KslExpression<KslBool2>.g get() = bool1("g")

val KslExpression<KslBool2>.xx get() = bool2("xx")
val KslExpression<KslBool2>.yy get() = bool2("yy")
val KslExpression<KslBool2>.yx get() = bool2("yx")

val KslExpression<KslBool2>.rr get() = bool2("rr")
val KslExpression<KslBool2>.gg get() = bool2("gg")
val KslExpression<KslBool2>.gr get() = bool2("gr")
