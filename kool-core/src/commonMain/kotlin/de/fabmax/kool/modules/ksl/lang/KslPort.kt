package de.fabmax.kool.modules.ksl.lang

abstract class KslPort<T: KslType>(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    abstract val output: KslValue<T>
}

fun KslScopeBuilder.float1Port(name: String, input: KslScalarExpression<KslFloat1>? = null): KslExprFloat1 =
    PortFloat1(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }.output

fun KslScopeBuilder.float2Port(name: String, input: KslVectorExpression<KslFloat2, KslFloat1>? = null): KslExprFloat2 =
    PortFloat2(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }.output

fun KslScopeBuilder.float3Port(name: String, input: KslVectorExpression<KslFloat3, KslFloat1>? = null): KslExprFloat3 =
    PortFloat3(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }.output

fun KslScopeBuilder.float4Port(name: String, input: KslVectorExpression<KslFloat4, KslFloat1>? = null): KslExprFloat4 =
    PortFloat4(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }.output

fun KslScopeBuilder.getFloat1Port(name: String) = getBlocks(name, mutableListOf()).filterIsInstance<PortFloat1>()[0]
fun KslScopeBuilder.getFloat2Port(name: String) = getBlocks(name, mutableListOf()).filterIsInstance<PortFloat2>()[0]
fun KslScopeBuilder.getFloat3Port(name: String) = getBlocks(name, mutableListOf()).filterIsInstance<PortFloat3>()[0]
fun KslScopeBuilder.getFloat4Port(name: String) = getBlocks(name, mutableListOf()).filterIsInstance<PortFloat4>()[0]

class PortFloat1(name: String, parentScope: KslScopeBuilder) : KslPort<KslFloat1>(name, parentScope) {
    val input = inFloat1("input")
    override val output = outFloat1("output")
    init { body.apply { output set input } }
}

class PortFloat2(name: String, parentScope: KslScopeBuilder) : KslPort<KslFloat2>(name, parentScope) {
    val input = inFloat2("input")
    override val output = outFloat2("output")
    init { body.apply { output set input } }
}

class PortFloat3(name: String, parentScope: KslScopeBuilder) : KslPort<KslFloat3>(name, parentScope) {
    val input = inFloat3("input")
    override val output = outFloat3("output")
    init { body.apply { output set input } }
}

class PortFloat4(name: String, parentScope: KslScopeBuilder) : KslPort<KslFloat4>(name, parentScope) {
    val input = inFloat4("input")
    override val output = outFloat4("output")
    init { body.apply { output set input } }
}


fun KslScopeBuilder.int1Port(name: String, input: KslScalarExpression<KslInt1>? = null): KslExprInt1 =
    PortInt1(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }.output

fun KslScopeBuilder.int2Port(name: String, input: KslVectorExpression<KslInt2, KslInt1>? = null): KslExprInt2 =
    PortInt2(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }.output

fun KslScopeBuilder.int3Port(name: String, input: KslVectorExpression<KslInt3, KslInt1>? = null): KslExprInt3 =
    PortInt3(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }.output

fun KslScopeBuilder.int4Port(name: String, input: KslVectorExpression<KslInt4, KslInt1>? = null): KslExprInt4 =
    PortInt4(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }.output

fun KslScopeBuilder.getInt1Port(name: String) = getBlocks(name, mutableListOf())[0] as PortInt1
fun KslScopeBuilder.getInt2Port(name: String) = getBlocks(name, mutableListOf())[0] as PortInt2
fun KslScopeBuilder.getInt3Port(name: String) = getBlocks(name, mutableListOf())[0] as PortInt3
fun KslScopeBuilder.getInt4Port(name: String) = getBlocks(name, mutableListOf())[0] as PortInt4

class PortInt1(name: String, parentScope: KslScopeBuilder) : KslPort<KslInt1>(name, parentScope) {
    val input = inInt1("input")
    override val output = outInt1("output")
    init { body.apply { output set input } }
}

class PortInt2(name: String, parentScope: KslScopeBuilder) : KslPort<KslInt2>(name, parentScope) {
    val input = inInt2("input")
    override val output = outInt2("output")
    init { body.apply { output set input } }
}

class PortInt3(name: String, parentScope: KslScopeBuilder) : KslPort<KslInt3>(name, parentScope) {
    val input = inInt3("input")
    override val output = outInt3("output")
    init { body.apply { output set input } }
}

class PortInt4(name: String, parentScope: KslScopeBuilder) : KslPort<KslInt4>(name, parentScope) {
    val input = inInt4("input")
    override val output = outInt4("output")
    init { body.apply { output set input } }
}


fun KslScopeBuilder.mat2Port(name: String, input: KslMatrixExpression<KslMat2, KslFloat2>? = null): KslExprMat2 =
    PortMat2(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }.output

fun KslScopeBuilder.mat3Port(name: String, input: KslMatrixExpression<KslMat3, KslFloat3>? = null): KslExprMat3 =
    PortMat3(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }.output

fun KslScopeBuilder.mat4Port(name: String, input: KslMatrixExpression<KslMat4, KslFloat4>? = null): KslExprMat4 =
    PortMat4(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }.output

fun KslScopeBuilder.getMat2Port(name: String) = getBlocks(name, mutableListOf())[0] as PortMat2
fun KslScopeBuilder.getMat3Port(name: String) = getBlocks(name, mutableListOf())[0] as PortMat3
fun KslScopeBuilder.getMat4Port(name: String) = getBlocks(name, mutableListOf())[0] as PortMat4

class PortMat2(name: String, parentScope: KslScopeBuilder) : KslPort<KslMat2>(name, parentScope) {
    val input = inMat2("input")
    override val output = outMat2("output")
    init { body.apply { output set input } }
}

class PortMat3(name: String, parentScope: KslScopeBuilder) : KslPort<KslMat3>(name, parentScope) {
    val input = inMat3("input")
    override val output = outMat3("output")
    init { body.apply { output set input } }
}

class PortMat4(name: String, parentScope: KslScopeBuilder) : KslPort<KslMat4>(name, parentScope) {
    val input = inMat4("input")
    override val output = outMat4("output")
    init { body.apply { output set input } }
}