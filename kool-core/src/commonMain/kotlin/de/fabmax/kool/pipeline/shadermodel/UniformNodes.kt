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

                    uCamPos.value.set(cmd.scene?.camera?.globalPos ?: Vec3f.ZERO, 1f)
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

class TextureNode(val texName: String, graph: ShaderGraph) : ShaderNode("Texture $texName", graph) {
    val visibleIn = mutableSetOf(graph.stage)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.descriptorSet.apply {
            +TextureSampler.Builder().apply {
                stages += visibleIn
                name = texName
                stages += shaderGraph.stage
            }
        }
    }
}

abstract class PushConstantNode<T: Uniform<*>>(name: String, graph: ShaderGraph) : ShaderNode(name, graph) {
    abstract val output: ShaderNodeIoVar
    lateinit var uniform: T

    val visibleIn = mutableSetOf(graph.stage)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        uniform = createUniform()
        shaderGraph.pushConstants.apply {
            stages += visibleIn
            +{ uniform }
        }
    }

    protected abstract fun createUniform(): T
}

class PushConstantNode1f(name: String, graph: ShaderGraph) : PushConstantNode<Uniform1f>(name, graph) {
    override val output = ShaderNodeIoVar(ModelVar1f(name), this)
    override fun createUniform(): Uniform1f = Uniform1f(output.variable.name)
}

class PushConstantNode2f(name: String, graph: ShaderGraph) : PushConstantNode<Uniform2f>(name, graph) {
    override val output = ShaderNodeIoVar(ModelVar2f(name), this)
    override fun createUniform(): Uniform2f = Uniform2f(output.variable.name)
}

class PushConstantNode3f(name: String, graph: ShaderGraph) : PushConstantNode<Uniform3f>(name, graph) {
    override val output = ShaderNodeIoVar(ModelVar3f(name), this)
    override fun createUniform(): Uniform3f = Uniform3f(output.variable.name)
}

class PushConstantNode4f(name: String, graph: ShaderGraph) : PushConstantNode<Uniform4f>(name, graph) {
    override val output = ShaderNodeIoVar(ModelVar4f(name), this)
    override fun createUniform(): Uniform4f = Uniform4f(output.variable.name)
}