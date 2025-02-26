package de.fabmax.kool.modules.ksl.lang

class KslStorageAtomicOp<T: KslStorageType<R, C>, C: KslIntType, R>(
    val storage: KslStorage<T, *>,
    val coord: KslExpression<C>,
    val data: KslExpression<R>,
    val op: Op,
    override val expressionType: R
) : KslScalarExpression<R> where R: KslIntType, R: KslScalar {

    init {
        storage.isWritten = true
        storage.isAccessedAtomically = true
    }

    override fun collectSubExpressions(): List<KslExpression<*>> = storage.collectSubExpressions() + this
    override fun toPseudoCode(): String = "storageAtomic$op(${storage.toPseudoCode()}, ${coord.toPseudoCode()}, ${data.toPseudoCode()})"

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

class KslStorageAtomicCompareSwap<T: KslStorageType<R, C>, C: KslIntType, R>(
    val storage: KslStorage<T, *>,
    val coord: KslExpression<C>,
    val compare: KslExpression<R>,
    val data: KslExpression<R>,
    override val expressionType: R
) : KslScalarExpression<R> where R: KslIntType, R: KslScalar {

    init {
        storage.isWritten = true
        storage.isAccessedAtomically = true
    }

    override fun collectSubExpressions(): List<KslExpression<*>> = storage.collectSubExpressions() + this
    override fun toPseudoCode(): String = "storageAtomicCondSet(${storage.toPseudoCode()}, ${coord.toPseudoCode()}, ${compare.toPseudoCode()}, ${data.toPseudoCode()})"
}
