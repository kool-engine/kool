package de.fabmax.kool.modules.ksl.lang

// Int to * cast extension functions - defined in a separate file to avoid JVM signature clashes

fun KslExpression<KslInt1>.toBool1() = KslExpressionCastScalar(this, KslBool1)
fun KslExpression<KslInt1>.toFloat1() = KslExpressionCastScalar(this, KslFloat1)
fun KslExpression<KslInt1>.toUint1() = KslExpressionCastScalar(this, KslUint1)

fun KslExpression<KslInt2>.toBool2() = KslExpressionCastVector(this, KslBool2)
fun KslExpression<KslInt2>.toFloat2() = KslExpressionCastVector(this, KslFloat2)
fun KslExpression<KslInt2>.toUint2() = KslExpressionCastVector(this, KslUint2)

fun KslExpression<KslInt3>.toBool3() = KslExpressionCastVector(this, KslBool3)
fun KslExpression<KslInt3>.toFloat3() = KslExpressionCastVector(this, KslFloat3)
fun KslExpression<KslInt3>.toUint3() = KslExpressionCastVector(this, KslUint3)

fun KslExpression<KslInt4>.toBool4() = KslExpressionCastVector(this, KslBool4)
fun KslExpression<KslInt4>.toFloat4() = KslExpressionCastVector(this, KslFloat4)
fun KslExpression<KslInt2>.toUint4() = KslExpressionCastVector(this, KslUint4)