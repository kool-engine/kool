package de.fabmax.kool.modules.ksl.lang

// common vec3 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslVectorExpression<KslTypeFloat3, KslTypeFloat1>.xy get() = float2("xy")
val KslVectorExpression<KslTypeFloat3, KslTypeFloat1>.xz get() = float2("xz")
val KslVectorExpression<KslTypeFloat3, KslTypeFloat1>.yz get() = float2("xz")

val KslVectorExpression<KslTypeFloat3, KslTypeFloat1>.rg get() = float2("rg")
val KslVectorExpression<KslTypeFloat3, KslTypeFloat1>.rb get() = float2("rb")
val KslVectorExpression<KslTypeFloat3, KslTypeFloat1>.gb get() = float2("gb")
