package de.fabmax.kool.modules.ksl.lang

// common vec4 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslExpression<KslFloat4>.x get() = float1("x")
val KslExpression<KslFloat4>.y get() = float1("y")
val KslExpression<KslFloat4>.z get() = float1("z")
val KslExpression<KslFloat4>.w get() = float1("w")

val KslExpression<KslFloat4>.r get() = float1("r")
val KslExpression<KslFloat4>.g get() = float1("g")
val KslExpression<KslFloat4>.b get() = float1("b")
val KslExpression<KslFloat4>.a get() = float1("a")

val KslExpression<KslFloat4>.xy get() = float2("xy")
val KslExpression<KslFloat4>.xz get() = float2("xz")
val KslExpression<KslFloat4>.yz get() = float2("yz")
val KslExpression<KslFloat4>.xw get() = float2("xw")
val KslExpression<KslFloat4>.yw get() = float2("yw")
val KslExpression<KslFloat4>.zw get() = float2("zw")

val KslExpression<KslFloat4>.rg get() = float2("rg")
val KslExpression<KslFloat4>.rb get() = float2("rb")
val KslExpression<KslFloat4>.gb get() = float2("gb")

val KslExpression<KslFloat4>.xyz get() = float3("xyz")
val KslExpression<KslFloat4>.rgb get() = float3("rgb")
