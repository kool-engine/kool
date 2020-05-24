package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.GlslType

class Vec3TransformNode(graph: ShaderGraph, var w: Float = 1.0f, var invert: Boolean = false) : ShaderNode("vec3MatTransform", graph) {
    var inMat: ShaderNodeIoVar? = null
    var inVec: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    val outVec3 = ShaderNodeIoVar(ModelVar3f("vec3MatTransform${nodeId}_out"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inVec, inMat ?: throw KoolException("Matrix input not set"))
    }

    override fun generateCode(generator: CodeGenerator) {
        val mat = inMat ?: throw KoolException("Matrix input not set")
        val input = if (inVec.variable.type == GlslType.VEC_4F) { inVec.ref4f() } else { "vec4(${inVec.ref3f()}, $w)" }
        val sign = if (invert) { "-" } else { "" }
        generator.appendMain("${outVec3.declare()} = $sign(${mat.refAsType(GlslType.MAT_4F)} * $input).xyz;")
    }
}

class Vec4TransformNode(graph: ShaderGraph, var w: Float = 1.0f) : ShaderNode("vec4MatTransform", graph) {
    var inMat: ShaderNodeIoVar? = null
    var inVec: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    val outVec4 = ShaderNodeIoVar(ModelVar4f("vec4MatTransform${nodeId}_out"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inVec, inMat ?: throw KoolException("Matrix input not set"))
    }

    override fun generateCode(generator: CodeGenerator) {
        val mvp = inMat?.variable ?: throw KoolException("Matrix input not set")
        val input = if (inVec.variable.type == GlslType.VEC_4F) { inVec.ref4f() } else { "vec4(${inVec.ref3f()}, $w)" }
        generator.appendMain("${outVec4.declare()} = ${mvp.refAsType(GlslType.MAT_4F)} * $input;")
    }
}
