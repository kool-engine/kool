package de.fabmax.kool.modules.ksl.lang

// common vec4 component swizzles - defined in a separate file to avoid JVM signature clashes

val KslVectorExpression<KslTypeInt4, KslTypeInt1>.xy get() = int2("xy")
val KslVectorExpression<KslTypeInt4, KslTypeInt1>.xz get() = int2("xz")
val KslVectorExpression<KslTypeInt4, KslTypeInt1>.yz get() = int2("xz")

val KslVectorExpression<KslTypeInt4, KslTypeInt1>.rg get() = int2("rg")
val KslVectorExpression<KslTypeInt4, KslTypeInt1>.rb get() = int2("rb")
val KslVectorExpression<KslTypeInt4, KslTypeInt1>.gb get() = int2("gb")

val KslVectorExpression<KslTypeInt4, KslTypeInt1>.xyz get() = int3("xyz")
val KslVectorExpression<KslTypeInt4, KslTypeInt1>.rgb get() = int3("rgb")
