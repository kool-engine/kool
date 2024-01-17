package de.fabmax.kool.modules.ksl.lang

open class KslVertexAttribute<T: KslType>(val value: KslValue<T>, val inputRate: KslInputRate) : KslExpression<T> by value {
    val name: String
        get() = value.stateName
}

class KslVertexAttributeScalar<S>(value: KslValue<S>, inputRate: KslInputRate)
    : KslVertexAttribute<S>(value, inputRate), KslScalarExpression<S> where S: KslType, S: KslScalar
class KslVertexAttributeVector<V, S>(value: KslValue<V>, inputRate: KslInputRate)
    : KslVertexAttribute<V>(value, inputRate), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar
class KslVertexAttributeMatrix<M, V>(value: KslValue<M>, inputRate: KslInputRate)
    : KslVertexAttribute<M>(value, inputRate), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslVector<*>

enum class KslInputRate {
    Vertex,
    Instance,
}
