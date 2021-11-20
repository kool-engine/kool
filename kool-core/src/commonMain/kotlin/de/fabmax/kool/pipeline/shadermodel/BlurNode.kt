package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec2f
import kotlin.math.exp

class BlurNode(shaderGraph: ShaderGraph) : ShaderNode("blurNd_${shaderGraph.nextNodeId}", shaderGraph) {

    var inTexCoord = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO))
    var inDirection = ShaderNodeIoVar(ModelVar2fConst(Vec2f(0.001f, 0f)))

    lateinit var inTexture: Texture2dNode

    val outColor = ShaderNodeIoVar(ModelVar4f("${name}_outColor"), this)

    var kernel = blurKernel(4)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inTexture)
        dependsOn(inTexCoord, inDirection)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("""
            ${outColor.declare()} = ${generator.sampleTexture2d(inTexture.name, inTexCoord.ref2f())} * ${kernel[0]};
        """)
        for (i in 1 until kernel.size) {
            val coordRt = "${inTexCoord.ref2f()} + ${inDirection.ref2f()} * float($i)"
            val coordLt = "${inTexCoord.ref2f()} - ${inDirection.ref2f()} * float($i)"
            generator.appendMain("    $outColor += ${generator.sampleTexture2d(inTexture.name, coordLt)}  * ${kernel[i]};")
            generator.appendMain("    $outColor += ${generator.sampleTexture2d(inTexture.name, coordRt)}  * ${kernel[i]};")
        }
    }

    companion object {
        fun blurKernel(radius: Int, sigma: Float = radius / 2.5f): FloatArray {
            val size = radius + 1
            val values = FloatArray(size)
            var sum = 0f
            for (i in 0 until size) {
                val a = i / sigma
                values[i] = exp(-0.5f * a * a)
                sum += values[i] * if (i == 0) 1f else 2f
            }
            for (i in 0 until size) {
                values[i] /= sum
            }
            return values
        }
    }
}