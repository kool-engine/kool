package de.fabmax.kool.modules.ksl.lang

// all possible vec2 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslVectorExpression<KslTypeFloat2, KslTypeFloat1>.xx get() = float2("xx")
val KslVectorExpression<KslTypeFloat2, KslTypeFloat1>.yy get() = float2("yy")
val KslVectorExpression<KslTypeFloat2, KslTypeFloat1>.yx get() = float2("yx")

val KslVectorExpression<KslTypeFloat2, KslTypeFloat1>.rr get() = float2("rr")
val KslVectorExpression<KslTypeFloat2, KslTypeFloat1>.gg get() = float2("gg")
val KslVectorExpression<KslTypeFloat2, KslTypeFloat1>.gr get() = float2("gr")
