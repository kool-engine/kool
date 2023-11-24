package de.fabmax.kool.modules.ksl.lang

// all possible uvec2 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslTypeUint2>.x get() = uint1("x")
val KslExpression<KslTypeUint2>.y get() = uint1("y")

val KslExpression<KslTypeUint2>.r get() = uint1("r")
val KslExpression<KslTypeUint2>.g get() = uint1("g")

val KslExpression<KslTypeUint2>.xx get() = uint2("xx")
val KslExpression<KslTypeUint2>.yy get() = uint2("yy")
val KslExpression<KslTypeUint2>.yx get() = uint2("yx")

val KslExpression<KslTypeUint2>.rr get() = uint2("rr")
val KslExpression<KslTypeUint2>.gg get() = uint2("gg")
val KslExpression<KslTypeUint2>.gr get() = uint2("gr")
