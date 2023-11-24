package de.fabmax.kool.modules.ksl.lang

// Int to * cast extension functions - defined in a separate file to avoid JVM signature clashes

fun KslScalarExpression<KslInt1>.toBool1() = KslExpressionCastScalar(this, KslBool1)
fun KslScalarExpression<KslInt1>.toFloat1() = KslExpressionCastScalar(this, KslFloat1)
fun KslScalarExpression<KslInt1>.toUint1() = KslExpressionCastScalar(this, KslUint1)

fun KslVectorExpression<KslInt2, KslInt1>.toBool2() = KslExpressionCastVector(this, KslBool2)
fun KslVectorExpression<KslInt2, KslInt1>.toFloat2() = KslExpressionCastVector(this, KslFloat2)
fun KslVectorExpression<KslInt2, KslInt1>.toUint2() = KslExpressionCastVector(this, KslUint2)

fun KslVectorExpression<KslInt3, KslInt1>.toBool3() = KslExpressionCastVector(this, KslBool3)
fun KslVectorExpression<KslInt3, KslInt1>.toFloat3() = KslExpressionCastVector(this, KslFloat3)
fun KslVectorExpression<KslInt3, KslInt1>.toUint3() = KslExpressionCastVector(this, KslUint3)

fun KslVectorExpression<KslInt4, KslInt1>.toBool4() = KslExpressionCastVector(this, KslBool4)
fun KslVectorExpression<KslInt4, KslInt1>.toFloat4() = KslExpressionCastVector(this, KslFloat4)
fun KslVectorExpression<KslInt2, KslInt1>.toUint4() = KslExpressionCastVector(this, KslUint4)