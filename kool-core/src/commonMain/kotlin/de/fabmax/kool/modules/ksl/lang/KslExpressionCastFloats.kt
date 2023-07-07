package de.fabmax.kool.modules.ksl.lang

// Float to * cast extension functions - defined in a separate file to avoid JVM signature clashes

fun KslScalarExpression<KslTypeFloat1>.toBool1() = KslExpressionCastScalar(this, KslTypeBool1)
fun KslScalarExpression<KslTypeFloat1>.toInt1() = KslExpressionCastScalar(this, KslTypeInt1)
fun KslScalarExpression<KslTypeFloat1>.toUint1() = KslExpressionCastScalar(this, KslTypeUint1)

fun KslVectorExpression<KslTypeFloat2, KslTypeFloat1>.toBool2() = KslExpressionCastVector(this, KslTypeBool2)
fun KslVectorExpression<KslTypeFloat2, KslTypeFloat1>.toInt2() = KslExpressionCastVector(this, KslTypeInt2)
fun KslVectorExpression<KslTypeFloat2, KslTypeFloat1>.toUint2() = KslExpressionCastVector(this, KslTypeUint2)

fun KslVectorExpression<KslTypeFloat3, KslTypeFloat1>.toBool3() = KslExpressionCastVector(this, KslTypeBool3)
fun KslVectorExpression<KslTypeFloat3, KslTypeFloat1>.toInt3() = KslExpressionCastVector(this, KslTypeInt3)
fun KslVectorExpression<KslTypeFloat3, KslTypeFloat1>.toUint3() = KslExpressionCastVector(this, KslTypeUint3)

fun KslVectorExpression<KslTypeFloat4, KslTypeFloat1>.toBool4() = KslExpressionCastVector(this, KslTypeBool4)
fun KslVectorExpression<KslTypeFloat4, KslTypeFloat1>.toInt4() = KslExpressionCastVector(this, KslTypeInt4)
fun KslVectorExpression<KslTypeFloat2, KslTypeFloat1>.toUint4() = KslExpressionCastVector(this, KslTypeUint4)
