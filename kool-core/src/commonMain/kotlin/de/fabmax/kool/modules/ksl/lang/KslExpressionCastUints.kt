package de.fabmax.kool.modules.ksl.lang

// Uint to * cast extension functions - defined in a separate file to avoid JVM signature clashes

fun KslExpression<KslUint1>.toBool1() = KslExpressionCastScalar(this, KslBool1)
fun KslExpression<KslUint1>.toFloat1() = KslExpressionCastScalar(this, KslFloat1)
fun KslExpression<KslUint1>.toInt1() = KslExpressionCastScalar(this, KslInt1)

fun KslExpression<KslUint2>.toBool2() = KslExpressionCastVector(this, KslBool2)
fun KslExpression<KslUint2>.toFloat2() = KslExpressionCastVector(this, KslFloat2)
fun KslExpression<KslUint2>.toInt2() = KslExpressionCastVector(this, KslInt2)

fun KslExpression<KslUint3>.toBool3() = KslExpressionCastVector(this, KslBool3)
fun KslExpression<KslUint3>.toFloat3() = KslExpressionCastVector(this, KslFloat3)
fun KslExpression<KslUint3>.toInt3() = KslExpressionCastVector(this, KslInt3)

fun KslExpression<KslUint4>.toBool4() = KslExpressionCastVector(this, KslBool4)
fun KslExpression<KslUint4>.toFloat4() = KslExpressionCastVector(this, KslFloat4)
fun KslExpression<KslUint4>.toInt4() = KslExpressionCastVector(this, KslInt4)