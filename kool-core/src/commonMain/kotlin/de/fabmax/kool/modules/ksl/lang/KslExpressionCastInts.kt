package de.fabmax.kool.modules.ksl.lang

// Int to * cast extension functions - defined in a separate file to avoid JVM signature clashes

fun KslScalarExpression<KslTypeInt1>.toBool1() = KslExpressionCastScalar(this, KslTypeBool1)
fun KslScalarExpression<KslTypeInt1>.toFloat1() = KslExpressionCastScalar(this, KslTypeFloat1)
fun KslScalarExpression<KslTypeInt1>.toUint1() = KslExpressionCastScalar(this, KslTypeUint1)

fun KslVectorExpression<KslTypeInt2, KslTypeInt1>.toBool2() = KslExpressionCastVector(this, KslTypeBool2)
fun KslVectorExpression<KslTypeInt2, KslTypeInt1>.toFloat2() = KslExpressionCastVector(this, KslTypeFloat2)
fun KslVectorExpression<KslTypeInt2, KslTypeInt1>.toUint2() = KslExpressionCastVector(this, KslTypeUint2)

fun KslVectorExpression<KslTypeInt3, KslTypeInt1>.toBool3() = KslExpressionCastVector(this, KslTypeBool3)
fun KslVectorExpression<KslTypeInt3, KslTypeInt1>.toFloat3() = KslExpressionCastVector(this, KslTypeFloat3)
fun KslVectorExpression<KslTypeInt3, KslTypeInt1>.toUint3() = KslExpressionCastVector(this, KslTypeUint3)

fun KslVectorExpression<KslTypeInt4, KslTypeInt1>.toBool4() = KslExpressionCastVector(this, KslTypeBool4)
fun KslVectorExpression<KslTypeInt4, KslTypeInt1>.toFloat4() = KslExpressionCastVector(this, KslTypeFloat4)
fun KslVectorExpression<KslTypeInt2, KslTypeInt1>.toUint4() = KslExpressionCastVector(this, KslTypeUint4)