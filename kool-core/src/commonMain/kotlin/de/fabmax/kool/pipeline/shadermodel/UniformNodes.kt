package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*

class UniformBufferPremultipliedMvp(graph: ShaderGraph) : ShaderNode("UboPremultipliedMvp", graph) {
    val uMvp = UniformMat4f("modelViewProj")

    val outMvpMat = ShaderNodeIoVar(ModelVarMat4f(uMvp.name), this)

    private val visibleIn = mutableSetOf(graph.stage)

    fun addToStage(graph: ShaderGraph): StageCopy {
        val copy = StageCopy(graph)
        visibleIn.add(graph.stage)
        graph.addNode(copy)
        return copy
    }

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.descriptorSet.apply {
            uniformBuffer(name) {
                stages += visibleIn
                +{ uMvp }
                onUpdate = { _, cmd -> uMvp.value.set(cmd.mvpMat) }
            }
        }
    }

    inner class StageCopy(graph: ShaderGraph) : ShaderNode("UboPremultipliedMvp.copy", graph) {
        val outMvpMat = ShaderNodeIoVar(ModelVarMat4f(uMvp.name), this)
    }
}

class UniformBufferMvp(graph: ShaderGraph) : ShaderNode("UboMvp", graph) {
    val uModelMat = UniformMat4f("modelMat")
    val uViewMat = UniformMat4f("viewMat")
    val uProjMat = UniformMat4f("projMat")
    val uCamPos = Uniform4f("camPos")

    val outModelMat = ShaderNodeIoVar(ModelVarMat4f(uModelMat.name), this)
    val outViewMat = ShaderNodeIoVar(ModelVarMat4f(uViewMat.name), this)
    val outProjMat = ShaderNodeIoVar(ModelVarMat4f(uProjMat.name), this)
    val outCamPos = ShaderNodeIoVar(ModelVar4f(uCamPos.name), this)
    val outMvpMat = ShaderNodeIoVar(ModelVarMat4f("uMvp_outMvp"), this)

    private val visibleIn = mutableSetOf(graph.stage)

    fun addToStage(graph: ShaderGraph): StageCopy {
        val copy = StageCopy(graph)
        visibleIn.add(graph.stage)
        graph.addNode(copy)
        return copy
    }

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.descriptorSet.apply {
            uniformBuffer(name, shaderGraph.stage) {
                stages += visibleIn
                +{ uModelMat }
                +{ uViewMat }
                +{ uProjMat }
                +{ uCamPos }
                onUpdate = { _, cmd ->
                    uModelMat.value.set(cmd.modelMat)
                    uViewMat.value.set(cmd.viewMat)
                    uProjMat.value.set(cmd.projMat)

                    uCamPos.value.set(cmd.mesh.scene?.camera?.globalPos ?: Vec3f.ZERO, 1f)
                }
            }
        }
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${outMvpMat.declare()} = ${outProjMat.name} * ${outViewMat.name} * ${outModelMat.name};")
    }

    inner class StageCopy(graph: ShaderGraph) : ShaderNode("UboPremultipliedMvp.copy", graph) {
        val outModelMat = ShaderNodeIoVar(ModelVarMat4f(uModelMat.name), this)
        val outViewMat = ShaderNodeIoVar(ModelVarMat4f(uViewMat.name), this)
        val outProjMat = ShaderNodeIoVar(ModelVarMat4f(uProjMat.name), this)
        val outCamPos = ShaderNodeIoVar(ModelVar4f(uCamPos.name), this)
        val outMvpMat = ShaderNodeIoVar(ModelVarMat4f("uMvp_outMvp"), this)

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("${outMvpMat.declare()} = ${outProjMat.name} * ${outViewMat.name} * ${outModelMat.name};")
        }
    }
}

class TextureNode(graph: ShaderGraph, name: String = "tex2d_${graph.nextNodeId}") : ShaderNode(name, graph) {
    val visibleIn = mutableSetOf(graph.stage)
    lateinit var sampler: TextureSampler

    var arraySize = 1

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.descriptorSet.apply {
            +TextureSampler.Builder().apply {
                arraySize = this@TextureNode.arraySize
                stages += visibleIn
                name = this@TextureNode.name
                onCreate = { sampler = it }
            }
        }
    }
}

class CubeMapNode(graph: ShaderGraph, name: String = "texCube_${graph.nextNodeId}") : ShaderNode(name, graph) {
    val visibleIn = mutableSetOf(graph.stage)
    lateinit var sampler: CubeMapSampler

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.descriptorSet.apply {
            +CubeMapSampler.Builder().apply {
                stages += visibleIn
                name = this@CubeMapNode.name
                onCreate = { sampler = it }
            }
        }
    }
}

abstract class PushConstantNode<T: Uniform<*>>(name: String, graph: ShaderGraph) : ShaderNode(name, graph) {
    abstract val uniform: T
    abstract val output: ShaderNodeIoVar

    val visibleIn = mutableSetOf(graph.stage)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.pushConstants.apply {
            stages += visibleIn
            +{ uniform }
        }
    }
}

class PushConstantNode1f(override val uniform: Uniform1f, graph: ShaderGraph) : PushConstantNode<Uniform1f>(uniform.name, graph) {
    override val output = ShaderNodeIoVar(ModelVar1f(name), this)
}

class PushConstantNode2f(override val uniform: Uniform2f, graph: ShaderGraph) : PushConstantNode<Uniform2f>(uniform.name, graph) {
    override val output = ShaderNodeIoVar(ModelVar2f(name), this)
}

class PushConstantNode3f(override val uniform: Uniform3f, graph: ShaderGraph) : PushConstantNode<Uniform3f>(uniform.name, graph) {
    override val output = ShaderNodeIoVar(ModelVar3f(name), this)
}

class PushConstantNode4f(override val uniform: Uniform4f, graph: ShaderGraph) : PushConstantNode<Uniform4f>(uniform.name, graph) {
    override val output = ShaderNodeIoVar(ModelVar4f(name), this)
}

class PushConstantNodeColor(override val uniform: UniformColor, graph: ShaderGraph) : PushConstantNode<UniformColor>(uniform.name, graph) {
    override val output = ShaderNodeIoVar(ModelVar4f(name), this)
}