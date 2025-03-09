package de.fabmax.kool.modules.ksl.lang

class KslStorageAtomicOp<T: KslStorageType<R>, R>(
    val storage: KslStorage<T>,
    val index: KslExprInt1,
    val data: KslExpression<R>,
    val op: Op,
    override val expressionType: R
) : KslScalarExpression<R> where R: KslIntType, R: KslScalar {

    init {
        storage.isWritten = true
        storage.isAccessedAtomically = true
    }

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(storage, index, data)
    override fun toPseudoCode(): String = "storageAtomic$op(${storage.toPseudoCode()}, ${index.toPseudoCode()}, ${data.toPseudoCode()})"

    enum class Op {
        Swap,
        Add,
        And,
        Or,
        Xor,
        Min,
        Max
    }
}

class KslStorageAtomicCompareSwap<T: KslStorageType<R>, R>(
    val storage: KslStorage<T>,
    val index: KslExprInt1,
    val compare: KslExpression<R>,
    val data: KslExpression<R>,
    override val expressionType: R
) : KslScalarExpression<R> where R: KslIntType, R: KslScalar {

    init {
        storage.isWritten = true
        storage.isAccessedAtomically = true
    }

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(storage, index, compare, data)
    override fun toPseudoCode(): String = "storageAtomicCondSet(${storage.toPseudoCode()}, ${index.toPseudoCode()}, ${compare.toPseudoCode()}, ${data.toPseudoCode()})"
}
