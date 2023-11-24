package de.fabmax.kool.modules.ksl.lang

// all possible uvec2 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslUint2>.x get() = uint1("x")
val KslExpression<KslUint2>.y get() = uint1("y")

val KslExpression<KslUint2>.r get() = uint1("r")
val KslExpression<KslUint2>.g get() = uint1("g")

val KslExpression<KslUint2>.xx get() = uint2("xx")
val KslExpression<KslUint2>.yy get() = uint2("yy")
val KslExpression<KslUint2>.yx get() = uint2("yx")

val KslExpression<KslUint2>.rr get() = uint2("rr")
val KslExpression<KslUint2>.gg get() = uint2("gg")
val KslExpression<KslUint2>.gr get() = uint2("gr")
