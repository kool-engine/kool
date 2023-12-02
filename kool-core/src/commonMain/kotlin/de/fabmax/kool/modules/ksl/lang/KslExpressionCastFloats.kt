package de.fabmax.kool.modules.ksl.lang

// Float to * cast extension functions - defined in a separate file to avoid JVM signature clashes

fun KslExpression<KslFloat1>.toBool1() = KslExpressionCastScalar(this, KslBool1)
fun KslExpression<KslFloat1>.toInt1() = KslExpressionCastScalar(this, KslInt1)
fun KslExpression<KslFloat1>.toUint1() = KslExpressionCastScalar(this, KslUint1)

fun KslExpression<KslFloat2>.toBool2() = KslExpressionCastVector(this, KslBool2)
fun KslExpression<KslFloat2>.toInt2() = KslExpressionCastVector(this, KslInt2)
fun KslExpression<KslFloat2>.toUint2() = KslExpressionCastVector(this, KslUint2)

fun KslExpression<KslFloat3>.toBool3() = KslExpressionCastVector(this, KslBool3)
fun KslExpression<KslFloat3>.toInt3() = KslExpressionCastVector(this, KslInt3)
fun KslExpression<KslFloat3>.toUint3() = KslExpressionCastVector(this, KslUint3)

fun KslExpression<KslFloat4>.toBool4() = KslExpressionCastVector(this, KslBool4)
fun KslExpression<KslFloat4>.toInt4() = KslExpressionCastVector(this, KslInt4)
fun KslExpression<KslFloat2>.toUint4() = KslExpressionCastVector(this, KslUint4)
