package de.fabmax.kool.modules.ksl.lang

// common vec4 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslVectorExpression<KslTypeFloat4, KslTypeFloat1>.xy get() = float2("xy")
val KslVectorExpression<KslTypeFloat4, KslTypeFloat1>.xz get() = float2("xz")
val KslVectorExpression<KslTypeFloat4, KslTypeFloat1>.yz get() = float2("xz")

val KslVectorExpression<KslTypeFloat4, KslTypeFloat1>.rg get() = float2("rg")
val KslVectorExpression<KslTypeFloat4, KslTypeFloat1>.rb get() = float2("rb")
val KslVectorExpression<KslTypeFloat4, KslTypeFloat1>.gb get() = float2("gb")

val KslVectorExpression<KslTypeFloat4, KslTypeFloat1>.xyz get() = float3("xyz")
val KslVectorExpression<KslTypeFloat4, KslTypeFloat1>.rgb get() = float3("rgb")
