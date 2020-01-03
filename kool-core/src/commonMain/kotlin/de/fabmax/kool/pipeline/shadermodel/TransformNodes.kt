package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.ShaderStage

class TransformNode(graph: ShaderGraph, var w: Float = 1.0f, var invert: Boolean = false) : ShaderNode("Matrix Transform", graph) {
    var inMat: ShaderNodeIoVar? = null
    var input: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    val output = ShaderNodeIoVar(ModelVar3f("dirTrans${nodeId}_outDirection"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(input)
        dependsOn(inMat ?: throw KoolException("View matrix input not set"))
    }

    override fun generateCode(generator: CodeGenerator) {
        val mat = inMat ?: throw KoolException("View matrix input not set")
        val input = if (input.variable.type == GlslType.VEC_4F) { input.ref4f() } else { "vec4(${input.ref3f()}, $w)" }
        val sign = if (invert) { "-" } else { "" }
        generator.appendMain("${output.declare()} = $sign(${mat.refAsType(GlslType.MAT_4F)} * $input).xyz;")
    }
}

class VertexPosTransformNode(graph: ShaderGraph) : ShaderNode("Vertex Pos Transform", graph, ShaderStage.VERTEX_SHADER.mask) {
    var inMvp: ShaderNodeIoVar? = null
    var inPosition: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    val outPosition = ShaderNodeIoVar(ModelVar4f("vertPos_outPosition"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inPosition, inMvp ?: throw KoolException("MVP matrix input not set"))

        shaderGraph as VertexShaderGraph
        shaderGraph.positionOutput = outPosition
    }

    override fun generateCode(generator: CodeGenerator) {
        val mvp = inMvp?.variable ?: throw KoolException("MVP matrix input not set")
        generator.appendMain("${outPosition.declare()} = " +
                "${mvp.refAsType(GlslType.MAT_4F)} * vec4(${inPosition.ref3f()}, 1.0);")
    }
}
