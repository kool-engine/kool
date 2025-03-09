package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator

open class KslArrayAccessor<T: KslType>(
    val array: KslExpression<KslArrayType<T>>,
    val index: KslExpression<KslInt1>
) : KslExpression<T>, KslAssignable<T> {

    override val expressionType = array.expressionType.elemType
    override val assignType = array.expressionType.elemType
    override val mutatingState: KslValue<*>? get() = array.asAssignable()

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(array, index)

    override fun generateAssignable(generator: KslGenerator) = generator.arrayValueAssignable(this)
    override fun toPseudoCode() = "${array.toPseudoCode()}[${index.toPseudoCode()}]"
}

class KslArrayScalarAccessor<S>(array: KslScalarArrayExpression<S>, index: KslExpression<KslInt1>) :
    KslArrayAccessor<S>(array, index), KslScalarExpression<S> where S: KslType, S: KslScalar
class KslArrayVectorAccessor<V, S>(array: KslVectorArrayExpression<V, S>, index: KslExpression<KslInt1>) :
    KslArrayAccessor<V>(array, index), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar
class KslArrayMatrixAccessor<M, V>(array: KslMatrixArrayExpression<M, V>, index: KslExpression<KslInt1>) :
    KslArrayAccessor<M>(array, index), KslMatrixExpression<M, V> where M: KslType, M : KslMatrix<V>, V: KslVector<*>
class KslArrayGenericAccessor<T: KslType>(array: KslGenericArrayExpression<T>, index: KslExpression<KslInt1>) :
    KslArrayAccessor<T>(array, index)

operator fun <T: KslType> KslArrayExpression<T>.get(index: Int) =
    KslArrayAccessor(this, KslValueInt1(index))
operator fun <T: KslType> KslArrayExpression<T>.get(index: KslExpression<KslInt1>) =
    KslArrayAccessor(this, index)

operator fun <S> KslScalarArrayExpression<S>.get(index: Int) where S: KslType, S: KslScalar =
    KslArrayScalarAccessor(this, KslValueInt1(index))
operator fun <S> KslScalarArrayExpression<S>.get(index: KslExpression<KslInt1>) where S: KslType, S: KslScalar =
    KslArrayScalarAccessor(this, index)

operator fun <V, S> KslVectorArrayExpression<V, S>.get(index: Int) where V: KslType, V: KslVector<S>, S: KslScalar =
    KslArrayVectorAccessor(this, KslValueInt1(index))
operator fun <V, S> KslVectorArrayExpression<V, S>.get(index: KslExpression<KslInt1>) where V: KslType, V: KslVector<S>, S: KslScalar =
    KslArrayVectorAccessor(this, index)

operator fun <M, V> KslMatrixArrayExpression<M, V>.get(index: Int) where M: KslType, M : KslMatrix<V>, V: KslVector<*> =
    KslArrayMatrixAccessor(this, KslValueInt1(index))
operator fun <M, V> KslMatrixArrayExpression<M, V>.get(index: KslExpression<KslInt1>) where M: KslType, M : KslMatrix<V>, V: KslVector<*> =
    KslArrayMatrixAccessor(this, index)

operator fun <T: KslType> KslGenericArrayExpression<T>.get(index: Int) = KslArrayGenericAccessor(this, KslValueInt1(index))
operator fun <T: KslType> KslGenericArrayExpression<T>.get(index: KslExpression<KslInt1>) = KslArrayGenericAccessor(this, index)
