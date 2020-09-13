package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.util.logE
import kotlin.math.min

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
    val uModelMat = UniformMat4f("uModelMat")
    val uViewMat = UniformMat4f("uViewMat")
    val uProjMat = UniformMat4f("uProjMat")
    val uCamPos = Uniform4f("uCamPos")
    val uViewport = Uniform4f("uViewport")

    val outModelMat = ShaderNodeIoVar(ModelVarMat4f(uModelMat.name), this)
    val outViewMat = ShaderNodeIoVar(ModelVarMat4f(uViewMat.name), this)
    val outProjMat = ShaderNodeIoVar(ModelVarMat4f(uProjMat.name), this)
    val outCamPos = ShaderNodeIoVar(ModelVar4f(uCamPos.name), this)
    val outViewport = ShaderNodeIoVar(ModelVar4f(uViewport.name), this)
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
                +{ uViewport }
                onUpdate = { _, cmd ->
                    uModelMat.value.set(cmd.modelMat)
                    uViewMat.value.set(cmd.viewMat)
                    uProjMat.value.set(cmd.projMat)

                    uCamPos.value.set(cmd.renderPass.camera.globalPos, 1f)
                    cmd.renderPass.viewport.let {
                        uViewport.value.set(it.x.toFloat(), it.ySigned.toFloat(), it.width.toFloat(), it.heightSigned.toFloat())
                    }
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
        val outViewport = ShaderNodeIoVar(ModelVar4f(uViewport.name), this)
        val outMvpMat = ShaderNodeIoVar(ModelVarMat4f("uMvp_outMvp"), this)

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("${outMvpMat.declare()} = ${outProjMat.name} * ${outViewMat.name} * ${outModelMat.name};")
        }
    }
}

class Texture2dNode(graph: ShaderGraph, name: String) : ShaderNode(name, graph) {
    val visibleIn = mutableSetOf(graph.stage)
    lateinit var sampler: TextureSampler2d

    var arraySize = 1
    var isDepthTexture = false

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.descriptorSet.apply {
            texture2d(name, shaderGraph.stage) {
                arraySize = this@Texture2dNode.arraySize
                isDepthSampler = isDepthTexture
                stages += visibleIn
                onCreate = { sampler = it }
            }
        }
    }
}

class Texture3dNode(graph: ShaderGraph, name: String) : ShaderNode(name, graph) {
    val visibleIn = mutableSetOf(graph.stage)
    lateinit var sampler: TextureSampler3d

    var arraySize = 1

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.descriptorSet.apply {
            texture3d(name, shaderGraph.stage) {
                arraySize = this@Texture3dNode.arraySize
                stages += visibleIn
                onCreate = { sampler = it }
            }
        }
    }
}

class TextureCubeNode(graph: ShaderGraph, name: String) : ShaderNode(name, graph) {
    val visibleIn = mutableSetOf(graph.stage)
    lateinit var sampler: TextureSamplerCube

    var arraySize = 1

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.descriptorSet.apply {
            textureCube(name, shaderGraph.stage) {
                arraySize = this@TextureCubeNode.arraySize
                stages += visibleIn
                onCreate = { sampler = it }
            }
        }
    }
}

class MorphWeightsNode(val nWeights: Int, graph: ShaderGraph) : ShaderNode("morphWeights_${graph.nextNodeId}", graph) {
    private val uWeights0 = Uniform4f("${name}_w0")
    private val uWeights1 = Uniform4f("${name}_w1")

    val outWeights0 = ShaderNodeIoVar(ModelVar4f(uWeights0.name))
    val outWeights1 = ShaderNodeIoVar(ModelVar4f(uWeights1.name))

    init {
        if (nWeights > 8) {
            logE { "Currently only up to 8 morph target attributes are supported" }
        }
    }

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        if (nWeights > 0) {
            shaderGraph.pushConstants.apply {
                stages += shaderGraph.stage
                +{ uWeights0 }
                if (nWeights > 4) {
                    +{ uWeights1 }
                }

                onUpdate = { _, cmd ->
                    cmd.mesh.morphWeights?.let { w ->
                        for (i in 0 until min(4, w.size)) {
                            uWeights0.value[i] = w[i]
                        }
                        for (i in 0 until min(4, w.size - 4)) {
                            uWeights1.value[i] = w[i + 4]
                        }
                    }
                }
            }
        }
    }
}

abstract class PushConstantNode<T: Uniform<*>>(name: String, graph: ShaderGraph) : ShaderNode(name, graph) {
    abstract val uniform: T
    abstract val output: ShaderNodeIoVar

    val visibleIn = mutableSetOf(graph.stage)

    var onUpdate: ((PushConstantRange, DrawCommand) -> Unit)? = null

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.pushConstants.apply {
            stages += visibleIn
            +{ uniform }
            onUpdate = this@PushConstantNode.onUpdate
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

class PushConstantNode1i(override val uniform: Uniform1i, graph: ShaderGraph) : PushConstantNode<Uniform1i>(uniform.name, graph) {
    override val output = ShaderNodeIoVar(ModelVar1i(name), this)
}

class PushConstantNode2i(override val uniform: Uniform2i, graph: ShaderGraph) : PushConstantNode<Uniform2i>(uniform.name, graph) {
    override val output = ShaderNodeIoVar(ModelVar2i(name), this)
}

class PushConstantNode3i(override val uniform: Uniform3i, graph: ShaderGraph) : PushConstantNode<Uniform3i>(uniform.name, graph) {
    override val output = ShaderNodeIoVar(ModelVar3i(name), this)
}

class PushConstantNode4i(override val uniform: Uniform4i, graph: ShaderGraph) : PushConstantNode<Uniform4i>(uniform.name, graph) {
    override val output = ShaderNodeIoVar(ModelVar4i(name), this)
}