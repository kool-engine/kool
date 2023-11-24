package de.fabmax.kool.modules.ksl.lang

// Uint to * cast extension functions - defined in a separate file to avoid JVM signature clashes

fun KslScalarExpression<KslBool1>.toFloat1() = KslExpressionCastScalar(this, KslFloat1)
fun KslScalarExpression<KslBool1>.toInt1() = KslExpressionCastScalar(this, KslInt1)
fun KslScalarExpression<KslBool1>.toUint1() = KslExpressionCastScalar(this, KslUint1)

fun KslVectorExpression<KslBool2, KslBool1>.toFloat2() = KslExpressionCastVector(this, KslFloat2)
fun KslVectorExpression<KslBool2, KslBool1>.toInt2() = KslExpressionCastVector(this, KslInt2)
fun KslVectorExpression<KslBool2, KslBool1>.toUint2() = KslExpressionCastVector(this, KslUint2)

fun KslVectorExpression<KslBool3, KslBool1>.toFloat3() = KslExpressionCastVector(this, KslFloat3)
fun KslVectorExpression<KslBool3, KslBool1>.toInt3() = KslExpressionCastVector(this, KslInt3)
fun KslVectorExpression<KslBool3, KslBool1>.toUint3() = KslExpressionCastVector(this, KslUint3)

fun KslVectorExpression<KslBool4, KslBool1>.toFloat4() = KslExpressionCastVector(this, KslFloat4)
fun KslVectorExpression<KslBool4, KslBool1>.toInt4() = KslExpressionCastVector(this, KslInt4)
fun KslVectorExpression<KslBool4, KslBool1>.toUint4() = KslExpressionCastVector(this, KslUint4)