package de.fabmax.kool.modules.ksl.lang

// common vec4 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslTypeFloat4>.x get() = float1("x")
val KslExpression<KslTypeFloat4>.y get() = float1("y")
val KslExpression<KslTypeFloat4>.z get() = float1("z")
val KslExpression<KslTypeFloat4>.w get() = float1("w")

val KslExpression<KslTypeFloat4>.r get() = float1("r")
val KslExpression<KslTypeFloat4>.g get() = float1("g")
val KslExpression<KslTypeFloat4>.b get() = float1("b")
val KslExpression<KslTypeFloat4>.a get() = float1("a")

val KslExpression<KslTypeFloat4>.xy get() = float2("xy")
val KslExpression<KslTypeFloat4>.xz get() = float2("xz")
val KslExpression<KslTypeFloat4>.yz get() = float2("xz")
val KslExpression<KslTypeFloat4>.xw get() = float2("xw")
val KslExpression<KslTypeFloat4>.yw get() = float2("yw")
val KslExpression<KslTypeFloat4>.zw get() = float2("zw")

val KslExpression<KslTypeFloat4>.rg get() = float2("rg")
val KslExpression<KslTypeFloat4>.rb get() = float2("rb")
val KslExpression<KslTypeFloat4>.gb get() = float2("gb")

val KslExpression<KslTypeFloat4>.xyz get() = float3("xyz")
val KslExpression<KslTypeFloat4>.rgb get() = float3("rgb")
