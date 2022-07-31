package de.fabmax.kool.modules.ksl.lang

// all possible ivec2 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslVectorExpression<KslTypeInt2, KslTypeInt1>.xx get() = int2("xx")
val KslVectorExpression<KslTypeInt2, KslTypeInt1>.yy get() = int2("yy")
val KslVectorExpression<KslTypeInt2, KslTypeInt1>.yx get() = int2("yx")

val KslVectorExpression<KslTypeInt2, KslTypeInt1>.rr get() = int2("rr")
val KslVectorExpression<KslTypeInt2, KslTypeInt1>.gg get() = int2("gg")
val KslVectorExpression<KslTypeInt2, KslTypeInt1>.gr get() = int2("gr")
