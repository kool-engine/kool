package de.fabmax.kool.modules.ksl.lang

// Float to * cast extension functions - defined in a separate file to avoid JVM signature clashes

fun KslScalarExpression<KslFloat1>.toBool1() = KslExpressionCastScalar(this, KslBool1)
fun KslScalarExpression<KslFloat1>.toInt1() = KslExpressionCastScalar(this, KslInt1)
fun KslScalarExpression<KslFloat1>.toUint1() = KslExpressionCastScalar(this, KslUint1)

fun KslVectorExpression<KslFloat2, KslFloat1>.toBool2() = KslExpressionCastVector(this, KslBool2)
fun KslVectorExpression<KslFloat2, KslFloat1>.toInt2() = KslExpressionCastVector(this, KslInt2)
fun KslVectorExpression<KslFloat2, KslFloat1>.toUint2() = KslExpressionCastVector(this, KslUint2)

fun KslVectorExpression<KslFloat3, KslFloat1>.toBool3() = KslExpressionCastVector(this, KslBool3)
fun KslVectorExpression<KslFloat3, KslFloat1>.toInt3() = KslExpressionCastVector(this, KslInt3)
fun KslVectorExpression<KslFloat3, KslFloat1>.toUint3() = KslExpressionCastVector(this, KslUint3)

fun KslVectorExpression<KslFloat4, KslFloat1>.toBool4() = KslExpressionCastVector(this, KslBool4)
fun KslVectorExpression<KslFloat4, KslFloat1>.toInt4() = KslExpressionCastVector(this, KslInt4)
fun KslVectorExpression<KslFloat2, KslFloat1>.toUint4() = KslExpressionCastVector(this, KslUint4)
