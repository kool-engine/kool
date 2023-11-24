package de.fabmax.kool.modules.ksl.lang

// Uint to * cast extension functions - defined in a separate file to avoid JVM signature clashes

fun KslScalarExpression<KslUint1>.toBool1() = KslExpressionCastScalar(this, KslBool1)
fun KslScalarExpression<KslUint1>.toFloat1() = KslExpressionCastScalar(this, KslFloat1)
fun KslScalarExpression<KslUint1>.toInt1() = KslExpressionCastScalar(this, KslInt1)

fun KslVectorExpression<KslUint2, KslUint1>.toBool2() = KslExpressionCastVector(this, KslBool2)
fun KslVectorExpression<KslUint2, KslUint1>.toFloat2() = KslExpressionCastVector(this, KslFloat2)
fun KslVectorExpression<KslUint2, KslUint1>.toInt2() = KslExpressionCastVector(this, KslInt2)

fun KslVectorExpression<KslUint3, KslUint1>.toBool3() = KslExpressionCastVector(this, KslBool3)
fun KslVectorExpression<KslUint3, KslUint1>.toFloat3() = KslExpressionCastVector(this, KslFloat3)
fun KslVectorExpression<KslUint3, KslUint1>.toInt3() = KslExpressionCastVector(this, KslInt3)

fun KslVectorExpression<KslUint4, KslUint1>.toBool4() = KslExpressionCastVector(this, KslBool4)
fun KslVectorExpression<KslUint4, KslUint1>.toFloat4() = KslExpressionCastVector(this, KslFloat4)
fun KslVectorExpression<KslUint4, KslUint1>.toInt4() = KslExpressionCastVector(this, KslInt4)