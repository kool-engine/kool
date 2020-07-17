package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.util.Color

class FragmentColorOutNode(graph: ShaderGraph, val channels: Int = 1) : ShaderNode("fragmentColorOut", graph, ShaderStage.FRAGMENT_SHADER.mask) {
    var alpha: ShaderNodeIoVar? = null
    var inColors = Array(channels) { ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA)) }

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(*inColors)
        alpha?.let { dependsOn(it) }
    }

    override fun generateCode(generator: CodeGenerator) {
        val outCode = StringBuilder()
        for (i in 0 until channels) {
            outCode.append("layout(location=$i) out vec4 fragOutColor_$i;\n")
        }
        generator.appendFunction("fragOut", outCode.toString())

        for (i in 0 until channels) {
            val a = alpha
            if (a != null) {
                generator.appendMain("fragOutColor_$i = vec4(${inColors[i].ref3f()}, ${a.ref1f()});")
            } else {
                generator.appendMain("fragOutColor_$i = ${inColors[i].ref4f()};")
            }

        }
    }
}

class FragmentDepthOutNode(graph: ShaderGraph) : ShaderNode("fragmentDepthOut", graph, ShaderStage.FRAGMENT_SHADER.mask) {
    var inDepth = ShaderNodeIoVar(ModelVar1fConst(0.5f))

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inDepth)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("gl_FragDepth = ${inDepth.ref1f()};")
    }
}

class DiscardAlphaNode(graph: ShaderGraph) : ShaderNode("discardAlpha_${graph.nextNodeId}", graph, ShaderStage.FRAGMENT_SHADER.mask) {
    var inAlpha = ShaderNodeIoVar(ModelVar1fConst(1f))
    var inAlphaCutoff = ShaderNodeIoVar(ModelVar1fConst(0f))

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inAlpha, inAlphaCutoff)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("if (${inAlpha.ref1f()} <= ${inAlphaCutoff.ref1f()}) { discard; }")
    }
}