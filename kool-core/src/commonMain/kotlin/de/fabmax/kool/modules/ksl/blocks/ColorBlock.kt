package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.Texture2dArray
import de.fabmax.kool.util.Color

fun KslScopeBuilder.vertexColorBlock(cfg: ColorBlockConfig): ColorBlockVertexStage {
    val colorBlock = ColorBlockVertexStage(cfg, this)
    ops += colorBlock
    return colorBlock
}

fun KslScopeBuilder.fragmentColorBlock(
    cfg: ColorBlockConfig,
    ddx: KslExprFloat2? = null,
    ddy: KslExprFloat2? = null,
    uv: KslExprFloat2? = null,
    vertexStage: ColorBlockVertexStage? = null
): ColorBlockFragmentStage {
    val colorBlock = ColorBlockFragmentStage(cfg, vertexStage, uv, ddx, ddy, this)
    ops += colorBlock
    return colorBlock
}

class ColorBlockVertexStage(cfg: ColorBlockConfig, parentScope: KslScopeBuilder) : KslBlock(cfg.colorName, parentScope) {
    val vertexColors = mutableMapOf<ColorBlockConfig.VertexColor, KslInterStageVector<KslFloat4, KslFloat1>>()
    val instanceColors = mutableMapOf<ColorBlockConfig.InstanceColor, KslInterStageVector<KslFloat4, KslFloat1>>()

    init {
        body.apply {
            check(parentStage is KslVertexStage) { "ColorBlockVertexStage can only be added to KslVertexStage" }

            cfg.colorSources.filterIsInstance<ColorBlockConfig.VertexColor>().mapIndexed { i, source ->
                vertexColors[source] = parentStage.program.interStageFloat4(name = nextName("${opName}_vertexColor_$i")).apply {
                    input set parentStage.vertexAttribFloat4(source.colorAttrib.name)
                }
            }
            cfg.colorSources.filterIsInstance<ColorBlockConfig.InstanceColor>().mapIndexed { i, source ->
                instanceColors[source] = parentStage.program.interStageFloat4(name = nextName("${opName}_instanceColor_$i")).apply {
                    input set parentStage.instanceAttribFloat4(source.colorAttrib.name)
                }
            }
        }
    }
}

class ColorBlockFragmentStage(
    private val cfg: ColorBlockConfig,
    private val vertexColorBlock: ColorBlockVertexStage?,
    private val inUv: KslExprFloat2?,
    private val inDdx: KslExprFloat2?,
    private val inDdy: KslExprFloat2?,
    parentScope: KslScopeBuilder
) : KslBlock(cfg.colorName, parentScope) {

    val outColor = outFloat4(parentScope.nextName("${opName}_outColor"))

    init {
        body.apply {
            check(parentStage is KslFragmentStage) { "ColorBlockFragmentStage can only be added to KslFragmentStage" }

            if (cfg.colorSources.isEmpty() || cfg.colorSources.first().blendMode != ColorBlockConfig.BlendMode.Set) {
                outColor set Color.BLACK.const
            }

            cfg.colorSources.forEach { source ->
                val colorValue: KslVectorExpression<KslFloat4, KslFloat1> = when (source) {
                    is ColorBlockConfig.ConstColor -> source.constColor.const
                    is ColorBlockConfig.UniformColor -> parentStage.program.uniformFloat4(source.uniformName)
                    is ColorBlockConfig.VertexColor -> vertexBlock(parentStage).vertexColors[source]?.output ?: Vec4f.ZERO.const
                    is ColorBlockConfig.InstanceColor -> vertexBlock(parentStage).instanceColors[source]?.output ?: Vec4f.ZERO.const
                    is ColorBlockConfig.TextureColor -> {
                        val tex = parentStage.program.texture2d(source.textureName)
                        val texCoords = inUv ?: texCoordBlock(parentStage).getTextureCoords()
                        val texColor = if (inDdx != null && inDdy != null) {
                            float4Var(sampleTextureGrad(tex, texCoords, inDdx, inDdy))
                        } else {
                            float4Var(sampleTexture(tex, texCoords))
                        }
                        if (source.gamma != 1f) {
                            texColor.rgb set pow(texColor.rgb, Vec3f(source.gamma).const)
                        }
                        texColor
                    }
                    is ColorBlockConfig.TextureArrayColor -> {
                        val tex = parentStage.program.texture2dArray(source.textureName)
                        val texCoords = inUv ?: texCoordBlock(parentStage).getTextureCoords()
                        val texColor = if (inDdx != null && inDdy != null) {
                            float4Var(sampleTextureArrayGrad(tex, source.arrayIndex.const, texCoords, inDdx, inDdy))
                        } else {
                            float4Var(sampleTextureArray(tex, source.arrayIndex.const, texCoords))
                        }
                        if (source.gamma != 1f) {
                            texColor.rgb set pow(texColor.rgb, Vec3f(source.gamma).const)
                        }
                        texColor
                    }
                }
                blendColor(source.blendMode, colorValue)
            }
        }
    }

    private fun texCoordBlock(parentStage: KslShaderStage): TexCoordAttributeBlock {
        var block: TexCoordAttributeBlock? = parentStage.program.vertexStage?.findBlock()
        if (block == null) {
            parentStage.program.vertexStage {
                main { block = texCoordAttributeBlock() }
            }
        }
        return block!!
    }

    private fun vertexBlock(parentStage: KslShaderStage): ColorBlockVertexStage {
        var block: ColorBlockVertexStage? = vertexColorBlock ?: parentStage.program.vertexStage?.findBlock(cfg.colorName)
        if (block == null) {
            parentStage.program.vertexStage {
                main { block = vertexColorBlock(cfg) }
            }
        }
        return block!!
    }

    private fun KslScopeBuilder.blendColor(blendMode: ColorBlockConfig.BlendMode, value: KslExprFloat4) {
        when (blendMode) {
            ColorBlockConfig.BlendMode.Set -> outColor set value
            ColorBlockConfig.BlendMode.Multiply -> outColor *= value
            ColorBlockConfig.BlendMode.Add -> outColor += value
            ColorBlockConfig.BlendMode.Subtract -> outColor -= value
        }
    }
}

data class ColorBlockConfig(
    val colorName: String,
    val colorSources: List<ColorSource>
) {
    val primaryUniform: UniformColor?
        get() = colorSources.find { it is UniformColor } as? UniformColor

    val primaryTexture: TextureColor?
        get() = colorSources.find { it is TextureColor } as? TextureColor

    val primaryArrayTexture: TextureArrayColor?
        get() = colorSources.find { it is TextureArrayColor } as? TextureArrayColor

    class Builder(val colorName: String) {
        val colorSources = mutableListOf<ColorSource>()

        val primaryUniform: UniformColor?
            get() = colorSources.find { it is UniformColor } as? UniformColor

        val primaryTexture: TextureColor?
            get() = colorSources.find { it is TextureColor } as? TextureColor

        fun constColor(constColor: Color, blendMode: BlendMode = BlendMode.Set): Builder {
            colorSources += ConstColor(constColor, blendMode)
            return this
        }

        fun uniformColor(defaultColor: Color? = null, uniformName: String = "u${colorName}", blendMode: BlendMode = BlendMode.Set): Builder {
            colorSources += UniformColor(defaultColor, uniformName, blendMode)
            return this
        }

        fun vertexColor(attribute: Attribute = Attribute.COLORS, blendMode: BlendMode = BlendMode.Set): Builder {
            colorSources += VertexColor(attribute, blendMode)
            return this
        }

        fun instanceColor(attribute: Attribute = Attribute.INSTANCE_COLOR, blendMode: BlendMode = BlendMode.Set): Builder {
            colorSources += InstanceColor(attribute, blendMode)
            return this
        }

        /**
         * Adds texture based color information. By default, texture color space is converted from sRGB to linear color
         * space (this is typically what you want). In case you don't want color space conversion, specify a gamma value
         * of 1.0 or use [textureData] instead.
         *
         * @param defaultTexture Texture to bind to this attribute
         * @param textureName Name of the texture used in the generated shader code
         * @param gamma Color space conversion gama value. Default is 2.2 (sRGB -> linear), use a value of 1.0 to disable
         *              color space conversion
         * @param blendMode Blend mode for this color value. This is useful to combine multiple color sources (e.g.
         *                texture color and an instance color based color tint)
         */
        fun textureColor(
            defaultTexture: Texture2d? = null,
            textureName: String = "t${colorName}",
            gamma: Float = Color.GAMMA_sRGB_TO_LINEAR,
            blendMode: BlendMode = BlendMode.Set
        ): Builder {
            colorSources += TextureColor(defaultTexture, textureName, gamma, blendMode)
            return this
        }

        fun textureColor(
            arrayIndex: Int,
            textureName: String,
            defaultTexture: Texture2dArray? = null,
            gamma: Float = Color.GAMMA_sRGB_TO_LINEAR,
            blendMode: BlendMode = BlendMode.Set
        ): Builder {
            colorSources += TextureArrayColor(arrayIndex, defaultTexture, textureName, gamma, blendMode)
            return this
        }

        /**
         * Adds texture based color information without applying any color space conversion. This is equivalent to calling
         * [textureColor] with a gamma value of 1.0.
         *
         * @param defaultTexture Texture to bind to this attribute
         * @param textureName Name of the texture used in the generated shader code
         * @param blendMode Blend mode for this color value. This is useful to combine multiple color sources (e.g.
         *                texture color and an instance color based color tint)
         */
        fun textureData(
            defaultTexture: Texture2d? = null,
            textureName: String = "t${colorName}",
            blendMode: BlendMode = BlendMode.Set
        ) = textureColor(defaultTexture, textureName, 1f, blendMode)

        fun textureData(
            arrayIndex: Int,
            textureName: String,
            defaultTexture: Texture2dArray? = null,
            blendMode: BlendMode = BlendMode.Set
        ): Builder {
            colorSources += TextureArrayColor(arrayIndex, defaultTexture, textureName, 1f, blendMode)
            return this
        }

        fun build() = ColorBlockConfig(colorName, colorSources)
    }

    sealed interface ColorSource {
        val blendMode: BlendMode
    }
    data class ConstColor(val constColor: Color, override val blendMode: BlendMode) : ColorSource
    data class UniformColor(val defaultColor: Color?, val uniformName: String, override val blendMode: BlendMode) : ColorSource
    data class VertexColor(val colorAttrib: Attribute, override val blendMode: BlendMode) : ColorSource
    data class TextureColor(val defaultTexture: Texture2d?, val textureName: String, val gamma: Float, override val blendMode: BlendMode) : ColorSource
    data class TextureArrayColor(val arrayIndex: Int, val defaultTexture: Texture2dArray?, val textureName: String, val gamma: Float, override val blendMode: BlendMode) : ColorSource
    data class InstanceColor(val colorAttrib: Attribute, override val blendMode: BlendMode) : ColorSource

    enum class BlendMode {
        Set,
        Multiply,
        Add,
        Subtract
    }
}
