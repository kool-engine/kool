package de.fabmax.kool.modules.ksl.lang

// Uint to * cast extension functions - defined in a separate file to avoid JVM signature clashes

fun KslExpression<KslBool1>.toFloat1() = KslExpressionCastScalar(this, KslFloat1)
fun KslExpression<KslBool1>.toInt1() = KslExpressionCastScalar(this, KslInt1)
fun KslExpression<KslBool1>.toUint1() = KslExpressionCastScalar(this, KslUint1)

fun KslExpression<KslBool2>.toFloat2() = KslExpressionCastVector(this, KslFloat2)
fun KslExpression<KslBool2>.toInt2() = KslExpressionCastVector(this, KslInt2)
fun KslExpression<KslBool2>.toUint2() = KslExpressionCastVector(this, KslUint2)

fun KslExpression<KslBool3>.toFloat3() = KslExpressionCastVector(this, KslFloat3)
fun KslExpression<KslBool3>.toInt3() = KslExpressionCastVector(this, KslInt3)
fun KslExpression<KslBool3>.toUint3() = KslExpressionCastVector(this, KslUint3)

fun KslExpression<KslBool4>.toFloat4() = KslExpressionCastVector(this, KslFloat4)
fun KslExpression<KslBool4>.toInt4() = KslExpressionCastVector(this, KslInt4)
fun KslExpression<KslBool4>.toUint4() = KslExpressionCastVector(this, KslUint4)