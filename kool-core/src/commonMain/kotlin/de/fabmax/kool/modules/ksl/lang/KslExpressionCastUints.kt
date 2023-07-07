package de.fabmax.kool.modules.ksl.lang

// Uint to * cast extension functions - defined in a separate file to avoid JVM signature clashes

fun KslScalarExpression<KslTypeUint1>.toBool1() = KslExpressionCastScalar(this, KslTypeBool1)
fun KslScalarExpression<KslTypeUint1>.toFloat1() = KslExpressionCastScalar(this, KslTypeFloat1)
fun KslScalarExpression<KslTypeUint1>.toInt1() = KslExpressionCastScalar(this, KslTypeInt1)

fun KslVectorExpression<KslTypeUint2, KslTypeUint1>.toBool2() = KslExpressionCastVector(this, KslTypeBool2)
fun KslVectorExpression<KslTypeUint2, KslTypeUint1>.toFloat2() = KslExpressionCastVector(this, KslTypeFloat2)
fun KslVectorExpression<KslTypeUint2, KslTypeUint1>.toInt2() = KslExpressionCastVector(this, KslTypeInt2)

fun KslVectorExpression<KslTypeUint3, KslTypeUint1>.toBool3() = KslExpressionCastVector(this, KslTypeBool3)
fun KslVectorExpression<KslTypeUint3, KslTypeUint1>.toFloat3() = KslExpressionCastVector(this, KslTypeFloat3)
fun KslVectorExpression<KslTypeUint3, KslTypeUint1>.toInt3() = KslExpressionCastVector(this, KslTypeInt3)

fun KslVectorExpression<KslTypeUint4, KslTypeUint1>.toBool4() = KslExpressionCastVector(this, KslTypeBool4)
fun KslVectorExpression<KslTypeUint4, KslTypeUint1>.toFloat4() = KslExpressionCastVector(this, KslTypeFloat4)
fun KslVectorExpression<KslTypeUint4, KslTypeUint1>.toInt4() = KslExpressionCastVector(this, KslTypeInt4)