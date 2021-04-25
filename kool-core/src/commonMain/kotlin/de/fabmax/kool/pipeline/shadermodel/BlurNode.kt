package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec2f
import kotlin.math.exp

class BlurNode(shaderGraph: ShaderGraph) : ShaderNode("blurNd_${shaderGraph.nextNodeId}", shaderGraph) {

    var inRadiusFac = ShaderNodeIoVar(ModelVar2fConst(Vec2f(1f / 800f, 1f / 450f)))
    var inTexCoord = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO))
    var inDirection = ShaderNodeIoVar(ModelVar1iConst(DIRECTION_HORIZONTAL))
    var minBrightness: ShaderNodeIoVar? = null
    lateinit var inTexture: Texture2dNode

    val outColor = ShaderNodeIoVar(ModelVar4f("${name}_outColor"), this)

    var kernel = blurKernel(4)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inTexture)
        dependsOn(inRadiusFac, inTexCoord, inDirection)
        minBrightness?.let { dependsOn(it) }
    }

    override fun generateCode(generator: CodeGenerator) {
        if (minBrightness != null) {
            generator.appendFunction("sampleBlurInTex", """
                vec4 sampleBlurInTex(vec2 texCoord, float minBrightness) {
                    vec4 color = ${generator.sampleTexture2d(inTexture.name, "texCoord")};
                    float b = dot(color.rgb, vec3(1.0, 1.0, 1.0));
                    if (b > minBrightness) {
                        return color;
                    } else {
                        return vec4(0.0);
                    }
                }
            """.trimIndent())

        } else {
            generator.appendFunction("sampleBlurInTex", """
                vec4 sampleBlurInTex(vec2 texCoord) {
                    return ${generator.sampleTexture2d(inTexture.name, "texCoord")};
                }
            """.trimIndent())
        }

        fun sampleFunc(coord: String): String {
            return if (minBrightness != null) {
                "sampleBlurInTex($coord, ${minBrightness!!.ref1f()})"
            } else {
                generator.sampleTexture2d(inTexture.name, coord)
            }
        }


        generator.appendMain("${outColor.declare()} = ${sampleFunc(inTexCoord.ref2f())} * ${kernel[0]};")
        generator.appendMain("if (${inDirection.ref1i()} == $DIRECTION_HORIZONTAL) {")
        for (i in 1 until kernel.size) {
            val coordRt = "${inTexCoord.ref2f()} + vec2($i.0 * ${inRadiusFac}.x, 0.0)"
            generator.appendMain("    $outColor += ${sampleFunc(coordRt)} * ${kernel[i]};")
            val coordLt = "${inTexCoord.ref2f()} - vec2($i.0 * ${inRadiusFac}.x, 0.0)"
            generator.appendMain("    $outColor += ${sampleFunc(coordLt)} * ${kernel[i]};")
        }
        generator.appendMain("} else {")
        for (i in 1 until kernel.size) {
            val coordDn = "${inTexCoord.ref2f()} + vec2(0.0, $i.0 * ${inRadiusFac}.y)"
            generator.appendMain("    $outColor += ${sampleFunc(coordDn)} * ${kernel[i]};")
            val coordUp = "${inTexCoord.ref2f()} - vec2(0.0, $i.0 * ${inRadiusFac}.y)"
            generator.appendMain("    $outColor += ${sampleFunc(coordUp)} * ${kernel[i]};")
        }
        generator.appendMain("}")
    }

    companion object {
        const val DIRECTION_HORIZONTAL = 0
        const val DIRECTION_VERTICAL = 1

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