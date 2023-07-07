package de.fabmax.kool.modules.ksl.lang

// Uint to * cast extension functions - defined in a separate file to avoid JVM signature clashes

fun KslScalarExpression<KslTypeBool1>.toFloat1() = KslExpressionCastScalar(this, KslTypeFloat1)
fun KslScalarExpression<KslTypeBool1>.toInt1() = KslExpressionCastScalar(this, KslTypeInt1)
fun KslScalarExpression<KslTypeBool1>.toUint1() = KslExpressionCastScalar(this, KslTypeUint1)

fun KslVectorExpression<KslTypeBool2, KslTypeBool1>.toFloat2() = KslExpressionCastVector(this, KslTypeFloat2)
fun KslVectorExpression<KslTypeBool2, KslTypeBool1>.toInt2() = KslExpressionCastVector(this, KslTypeInt2)
fun KslVectorExpression<KslTypeBool2, KslTypeBool1>.toUint2() = KslExpressionCastVector(this, KslTypeUint2)

fun KslVectorExpression<KslTypeBool3, KslTypeBool1>.toFloat3() = KslExpressionCastVector(this, KslTypeFloat3)
fun KslVectorExpression<KslTypeBool3, KslTypeBool1>.toInt3() = KslExpressionCastVector(this, KslTypeInt3)
fun KslVectorExpression<KslTypeBool3, KslTypeBool1>.toUint3() = KslExpressionCastVector(this, KslTypeUint3)

fun KslVectorExpression<KslTypeBool4, KslTypeBool1>.toFloat4() = KslExpressionCastVector(this, KslTypeFloat4)
fun KslVectorExpression<KslTypeBool4, KslTypeBool1>.toInt4() = KslExpressionCastVector(this, KslTypeInt4)
fun KslVectorExpression<KslTypeBool4, KslTypeBool1>.toUint4() = KslExpressionCastVector(this, KslTypeUint4)