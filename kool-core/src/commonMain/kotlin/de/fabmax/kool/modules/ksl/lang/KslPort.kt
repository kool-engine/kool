package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator

abstract class KslPort<T: KslType>(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope), KslExpression<T> {
    abstract val output: KslValue<T>

    override val expressionType get() = output.expressionType
    override fun collectStateDependencies() = output.collectStateDependencies()
    override fun generateExpression(generator: KslGenerator) = output.generateExpression(generator)
    override fun toPseudoCode() = output.toPseudoCode()
}

fun KslScopeBuilder.float1Port(name: String, input: KslScalarExpression<KslFloat1>? = null): PortFloat1 =
    PortFloat1(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.float2Port(name: String, input: KslVectorExpression<KslFloat2, KslFloat1>? = null): PortFloat2 =
    PortFloat2(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.float3Port(name: String, input: KslVectorExpression<KslFloat3, KslFloat1>? = null): PortFloat3 =
    PortFloat3(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.float4Port(name: String, input: KslVectorExpression<KslFloat4, KslFloat1>? = null): PortFloat4 =
    PortFloat4(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.getFloat1Port(name: String) = getBlocks(name, mutableListOf()).filterIsInstance<PortFloat1>()[0]
fun KslScopeBuilder.getFloat2Port(name: String) = getBlocks(name, mutableListOf()).filterIsInstance<PortFloat2>()[0]
fun KslScopeBuilder.getFloat3Port(name: String) = getBlocks(name, mutableListOf()).filterIsInstance<PortFloat3>()[0]
fun KslScopeBuilder.getFloat4Port(name: String) = getBlocks(name, mutableListOf()).filterIsInstance<PortFloat4>()[0]

class PortFloat1(name: String, parentScope: KslScopeBuilder) :
    KslPort<KslFloat1>(name, parentScope), KslScalarExpression<KslFloat1> {
    val input = inFloat1("input")
    override val output = outFloat1("output")
    init { body.apply { output set input } }
}

class PortFloat2(name: String, parentScope: KslScopeBuilder) :
    KslPort<KslFloat2>(name, parentScope), KslVectorExpression<KslFloat2, KslFloat1> {
    val input = inFloat2("input")
    override val output = outFloat2("output")
    init { body.apply { output set input } }
}

class PortFloat3(name: String, parentScope: KslScopeBuilder) :
    KslPort<KslFloat3>(name, parentScope), KslVectorExpression<KslFloat3, KslFloat1> {
    val input = inFloat3("input")
    override val output = outFloat3("output")
    init { body.apply { output set input } }
}

class PortFloat4(name: String, parentScope: KslScopeBuilder) :
    KslPort<KslFloat4>(name, parentScope), KslVectorExpression<KslFloat4, KslFloat1> {
    val input = inFloat4("input")
    override val output = outFloat4("output")
    init { body.apply { output set input } }
}


fun KslScopeBuilder.int1Port(name: String, input: KslScalarExpression<KslInt1>? = null): PortInt1 =
    PortInt1(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.int2Port(name: String, input: KslVectorExpression<KslInt2, KslInt1>? = null): PortInt2 =
    PortInt2(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.int3Port(name: String, input: KslVectorExpression<KslInt3, KslInt1>? = null): PortInt3 =
    PortInt3(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.int4Port(name: String, input: KslVectorExpression<KslInt4, KslInt1>? = null): PortInt4 =
    PortInt4(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.getInt1Port(name: String) = getBlocks(name, mutableListOf())[0] as PortInt1
fun KslScopeBuilder.getInt2Port(name: String) = getBlocks(name, mutableListOf())[0] as PortInt2
fun KslScopeBuilder.getInt3Port(name: String) = getBlocks(name, mutableListOf())[0] as PortInt3
fun KslScopeBuilder.getInt4Port(name: String) = getBlocks(name, mutableListOf())[0] as PortInt4

class PortInt1(name: String, parentScope: KslScopeBuilder) :
    KslPort<KslInt1>(name, parentScope), KslScalarExpression<KslInt1> {
    val input = inInt1("input")
    override val output = outInt1("output")
    init { body.apply { output set input } }
}

class PortInt2(name: String, parentScope: KslScopeBuilder) :
    KslPort<KslInt2>(name, parentScope), KslVectorExpression<KslInt2, KslInt1> {
    val input = inInt2("input")
    override val output = outInt2("output")
    init { body.apply { output set input } }
}

class PortInt3(name: String, parentScope: KslScopeBuilder) :
    KslPort<KslInt3>(name, parentScope), KslVectorExpression<KslInt3, KslInt1> {
    val input = inInt3("input")
    override val output = outInt3("output")
    init { body.apply { output set input } }
}

class PortInt4(name: String, parentScope: KslScopeBuilder) :
    KslPort<KslInt4>(name, parentScope), KslVectorExpression<KslInt4, KslInt1> {
    val input = inInt4("input")
    override val output = outInt4("output")
    init { body.apply { output set input } }
}


fun KslScopeBuilder.mat2Port(name: String, input: KslMatrixExpression<KslMat2, KslFloat2>? = null): PortMat2 =
    PortMat2(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.mat3Port(name: String, input: KslMatrixExpression<KslMat3, KslFloat3>? = null): PortMat3 =
    PortMat3(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.mat4Port(name: String, input: KslMatrixExpression<KslMat4, KslFloat4>? = null): PortMat4 =
    PortMat4(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.getMat2Port(name: String) = getBlocks(name, mutableListOf())[0] as PortMat2
fun KslScopeBuilder.getMat3Port(name: String) = getBlocks(name, mutableListOf())[0] as PortMat3
fun KslScopeBuilder.getMat4Port(name: String) = getBlocks(name, mutableListOf())[0] as PortMat4

class PortMat2(name: String, parentScope: KslScopeBuilder) :
    KslPort<KslMat2>(name, parentScope), KslMatrixExpression<KslMat2, KslFloat2> {
    val input = inMat2("input")
    override val output = outMat2("output")
    init { body.apply { output set input } }
}

class PortMat3(name: String, parentScope: KslScopeBuilder) :
    KslPort<KslMat3>(name, parentScope), KslMatrixExpression<KslMat3, KslFloat3> {
    val input = inMat3("input")
    override val output = outMat3("output")
    init { body.apply { output set input } }
}

class PortMat4(name: String, parentScope: KslScopeBuilder) :
    KslPort<KslMat4>(name, parentScope), KslMatrixExpression<KslMat4, KslFloat4> {
    val input = inMat4("input")
    override val output = outMat4("output")
    init { body.apply { output set input } }
}