package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*

fun KslScopeBuilder.float1Port(name: String, input: KslScalarExpression<KslTypeFloat1>? = null): PortFloat1 =
    PortFloat1(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.float2Port(name: String, input: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>? = null): PortFloat2 =
    PortFloat2(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.float3Port(name: String, input: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>? = null): PortFloat3 =
    PortFloat3(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.float4Port(name: String, input: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>? = null): PortFloat4 =
    PortFloat4(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.getFloat1Port(name: String) = getBlocks(name, mutableListOf())[0] as PortFloat1
fun KslScopeBuilder.getFloat2Port(name: String) = getBlocks(name, mutableListOf())[0] as PortFloat2
fun KslScopeBuilder.getFloat3Port(name: String) = getBlocks(name, mutableListOf())[0] as PortFloat3
fun KslScopeBuilder.getFloat4Port(name: String) = getBlocks(name, mutableListOf())[0] as PortFloat4

class PortFloat1(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val input = inFloat1("input")
    val output = outFloat1("output")
    init { body.apply { output set input } }
}

class PortFloat2(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val input = inFloat2("input")
    val output = outFloat2("output")
    init { body.apply { output set input } }
}

class PortFloat3(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val input = inFloat3("input")
    val output = outFloat3("output")
    init { body.apply { output set input } }
}

class PortFloat4(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val input = inFloat4("input")
    val output = outFloat4("output")
    init { body.apply { output set input } }
}


fun KslScopeBuilder.int1Port(name: String, input: KslScalarExpression<KslTypeInt1>? = null): PortInt1 =
    PortInt1(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.int2Port(name: String, input: KslVectorExpression<KslTypeInt2, KslTypeInt1>? = null): PortInt2 =
    PortInt2(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.int3Port(name: String, input: KslVectorExpression<KslTypeInt3, KslTypeInt1>? = null): PortInt3 =
    PortInt3(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.int4Port(name: String, input: KslVectorExpression<KslTypeInt4, KslTypeInt1>? = null): PortInt4 =
    PortInt4(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.getInt1Port(name: String) = getBlocks(name, mutableListOf())[0] as PortInt1
fun KslScopeBuilder.getInt2Port(name: String) = getBlocks(name, mutableListOf())[0] as PortInt2
fun KslScopeBuilder.getInt3Port(name: String) = getBlocks(name, mutableListOf())[0] as PortInt3
fun KslScopeBuilder.getInt4Port(name: String) = getBlocks(name, mutableListOf())[0] as PortInt4

class PortInt1(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val input = inInt1("input")
    val output = outInt1("output")
    init { body.apply { output set input } }
}

class PortInt2(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val input = inInt2("input")
    val output = outInt2("output")
    init { body.apply { output set input } }
}

class PortInt3(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val input = inInt3("input")
    val output = outInt3("output")
    init { body.apply { output set input } }
}

class PortInt4(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val input = inInt4("input")
    val output = outInt4("output")
    init { body.apply { output set input } }
}


fun KslScopeBuilder.mat2Port(name: String, input: KslMatrixExpression<KslTypeMat2, KslTypeFloat2>? = null): PortMat2 =
    PortMat2(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.mat3Port(name: String, input: KslMatrixExpression<KslTypeMat3, KslTypeFloat3>? = null): PortMat3 =
    PortMat3(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.mat4Port(name: String, input: KslMatrixExpression<KslTypeMat4, KslTypeFloat4>? = null): PortMat4 =
    PortMat4(name, this).also { port ->
        input?.let { port.input(it) }
        ops += port
    }

fun KslScopeBuilder.getMat2Port(name: String) = getBlocks(name, mutableListOf())[0] as PortMat2
fun KslScopeBuilder.getMat3Port(name: String) = getBlocks(name, mutableListOf())[0] as PortMat3
fun KslScopeBuilder.getMat4Port(name: String) = getBlocks(name, mutableListOf())[0] as PortMat4

class PortMat2(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val input = inMat2("input")
    val output = outMat2("output")
    init { body.apply { output set input } }
}

class PortMat3(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val input = inMat3("input")
    val output = outMat3("output")
    init { body.apply { output set input } }
}

class PortMat4(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val input = inMat4("input")
    val output = outMat4("output")
    init { body.apply { output set input } }
}