package de.fabmax.kool.modules.ksl.lang

open class KslVertexAttribute<T: KslType>(val value: KslValue<T>, val inputRate: InputRate) : KslExpression<T> by value {
    var location = -1
}

class KslVertexAttributeScalar<S>(value: KslValue<S>, inputRate: InputRate)
    : KslVertexAttribute<S>(value, inputRate), KslScalarExpression<S> where S: KslType, S: KslScalar
class KslVertexAttributeVector<V, S>(value: KslValue<V>, inputRate: InputRate)
    : KslVertexAttribute<V>(value, inputRate), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar
class KslVertexAttributeMatrix<M, V>(value: KslValue<M>, inputRate: InputRate)
    : KslVertexAttribute<M>(value, inputRate), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslVector<*>

enum class InputRate {
    Vertex,
    Instance,
}
