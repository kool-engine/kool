package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator


abstract class KslMatrixAccessor<T>(
    val matrix: KslMatrixExpression<*,T>,
    val colIndex: KslExprInt1,
    val type: T
) :
    KslExpression<T>,
    KslAssignable<T>
    where T: KslType, T: KslVector<*>
{
    override val expressionType get() = type
    override val assignType get() = type
    override val mutatingState: KslValue<*>? get() = matrix.asAssignable()

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(matrix, colIndex)

    override fun generateAssignable(generator: KslGenerator) = generator.matrixColAssignable(this)
    override fun toPseudoCode() = "${matrix.toPseudoCode()}[${colIndex.toPseudoCode()}]"
}

class KslMatrix2Accessor(matrix: KslExprMat2, colIndex: KslExprInt1) : KslMatrixAccessor<KslFloat2>(matrix, colIndex, KslFloat2)
class KslMatrix3Accessor(matrix: KslExprMat3, colIndex: KslExprInt1) : KslMatrixAccessor<KslFloat3>(matrix, colIndex, KslFloat3)
class KslMatrix4Accessor(matrix: KslExprMat4, colIndex: KslExprInt1) : KslMatrixAccessor<KslFloat4>(matrix, colIndex, KslFloat4)

operator fun KslExprMat2.get(colIndex: Int) = KslMatrix2Accessor(this, KslValueInt1(colIndex))
operator fun KslExprMat2.get(colIndex: KslExprInt1) = KslMatrix2Accessor(this, colIndex)

operator fun KslExprMat3.get(colIndex: Int) = KslMatrix3Accessor(this, KslValueInt1(colIndex))
operator fun KslExprMat3.get(colIndex: KslExprInt1) = KslMatrix3Accessor(this, colIndex)

operator fun KslExprMat4.get(colIndex: Int) = KslMatrix4Accessor(this, KslValueInt1(colIndex))
operator fun KslExprMat4.get(colIndex: KslExprInt1) = KslMatrix4Accessor(this, colIndex)