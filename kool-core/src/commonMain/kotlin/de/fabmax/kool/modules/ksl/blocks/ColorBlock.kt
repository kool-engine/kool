package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.util.Color

fun KslScopeBuilder.vertexColorBlock(cfg: ColorBlockConfig): ColorBlockVertexStage {
    val colorBlock = ColorBlockVertexStage(cfg, this)
    ops += colorBlock
    return colorBlock
}

fun KslScopeBuilder.fragmentColorBlock(cfg: ColorBlockConfig, vertexStage: ColorBlockVertexStage? = null): ColorBlockFragmentStage {
    val colorBlock = ColorBlockFragmentStage(cfg, vertexStage, this)
    ops += colorBlock
    return colorBlock
}

class ColorBlockVertexStage(cfg: ColorBlockConfig, parentScope: KslScopeBuilder) : KslBlock(cfg.colorName, parentScope) {
    val vertexColors = mutableMapOf<ColorBlockConfig.VertexColor, KslInterStageVector<KslTypeFloat4, KslTypeFloat1>>()
    val instanceColors = mutableMapOf<ColorBlockConfig.InstanceColor, KslInterStageVector<KslTypeFloat4, KslTypeFloat1>>()

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
    parentScope: KslScopeBuilder
) : KslBlock(cfg.colorName, parentScope) {

    val outColor = outFloat4(parentScope.nextName("${opName}_outColor"))

    val textures = mutableMapOf<ColorBlockConfig.TextureColor, KslUniform<KslTypeColorSampler2d>>()

    init {
        body.apply {
            check(parentStage is KslFragmentStage) { "ColorBlockFragmentStage can only be added to KslFragmentStage" }

            if (cfg.colorSources.isEmpty() || cfg.colorSources.first().blendMode != ColorBlockConfig.BlendMode.Set) {
                outColor set Color.BLACK.const
            }

            cfg.colorSources.forEach { source ->
                val colorValue: KslVectorExpression<KslTypeFloat4, KslTypeFloat1> = when (source) {
                    is ColorBlockConfig.ConstColor -> source.constColor.const
                    is ColorBlockConfig.UniformColor -> parentStage.program.uniformFloat4(source.uniformName)
                    is ColorBlockConfig.VertexColor -> vertexBlock(parentStage).vertexColors[source]?.output ?: Vec4f.ZERO.const
                    is ColorBlockConfig.InstanceColor -> vertexBlock(parentStage).instanceColors[source]?.output ?: Vec4f.ZERO.const
                    is ColorBlockConfig.TextureColor ->  {
                        val tex = parentStage.program.texture2d(source.textureName).also { textures[source] = it }
                        val texCoords = texCoordBlock(parentStage).getAttributeCoords(source.coordAttribute)
                        val texColor = float4Var(sampleTexture(tex, texCoords))
                        if (source.gamma != 1f) {
                            texColor.rgb set pow(texColor.rgb, Vec3f(source.gamma).const)
                        }
                        texColor
                    }
                }
                mixColor(source.blendMode, colorValue)
            }
        }
    }

    private fun texCoordBlock(parentStage: KslShaderStage): TexCoordAttributeBlock {
        return parentStage.program.vertexStage.findBlock()
            ?: parentStage.program.vertexStage.main.run { texCoordAttributeBlock() }
    }

    private fun vertexBlock(parentStage: KslShaderStage): ColorBlockVertexStage {
        return vertexColorBlock
            ?: parentStage.program.vertexStage.findBlock(cfg.colorName)
            ?: parentStage.program.vertexStage.main.run { vertexColorBlock(cfg) }
    }

    private fun KslScopeBuilder.mixColor(mixMode: ColorBlockConfig.BlendMode, value: KslExprFloat4) {
        when (mixMode) {
            ColorBlockConfig.BlendMode.Set -> outColor set value
            ColorBlockConfig.BlendMode.Multiply -> outColor *= value
            ColorBlockConfig.BlendMode.Add -> outColor += value
            ColorBlockConfig.BlendMode.Subtract -> outColor -= value
        }
    }
}

data class ColorBlockConfig(
    val colorName: String,
    val colorSources: MutableList<ColorSource> = mutableListOf()
) {
    fun constColor(constColor: Color, mixMode: BlendMode = BlendMode.Set) {
        colorSources += ConstColor(constColor, mixMode)
    }

    fun uniformColor(defaultColor: Color? = null, uniformName: String = "u${colorName}", mixMode: BlendMode = BlendMode.Set) {
        colorSources += UniformColor(defaultColor, uniformName, mixMode)
    }

    fun vertexColor(attribute: Attribute = Attribute.COLORS, mixMode: BlendMode = BlendMode.Set) {
        colorSources += VertexColor(attribute, mixMode)
    }

    /**
     * Adds texture based color information. By default, texture color space is converted from sRGB to linear color
     * space (this is typically what you want). In case you don't want color space conversion, specify a gamma value
     * of 1.0 or use [textureData] instead.
     *
     * @param defaultTexture Texture to bind to this attribute
     * @param textureName Name of the texture used in the generated shader code
     * @param coordAttribute Vertex attribute to use for the texture coordinates
     * @param gamma Color space conversion gama value. Default is 2.2 (sRGB -> linear), use a value of 1.0 to disable
     *              color space conversion
     * @param mixMode Mix mode for this color value. This is useful to combine multiple color sources (e.g.
     *                texture color and an instance color based color tint)
     */
    fun textureColor(defaultTexture: Texture2d? = null,
                     textureName: String = "t${colorName}",
                     coordAttribute: Attribute = Attribute.TEXTURE_COORDS,
                     gamma: Float = Color.GAMMA_sRGB_TO_LINEAR,
                     mixMode: BlendMode = BlendMode.Set) {
        colorSources += TextureColor(defaultTexture, textureName, coordAttribute, gamma, mixMode)
    }

    /**
     * Adds texture based color information without applying any color space conversion. This is equivalent to calling
     * [textureColor] with a gamma value of 1.0.
     *
     * @param defaultTexture Texture to bind to this attribute
     * @param textureName Name of the texture used in the generated shader code
     * @param coordAttribute Vertex attribute to use for the texture coordinates
     * @param mixMode Mix mode for this color value. This is useful to combine multiple color sources (e.g.
     *                texture color and an instance color based color tint)
     */
    fun textureData(defaultTexture: Texture2d? = null,
                    textureName: String = "tColor",
                    coordAttribute: Attribute = Attribute.TEXTURE_COORDS,
                    mixMode: BlendMode = BlendMode.Set) =
        textureColor(defaultTexture, textureName, coordAttribute, 1f, mixMode)

    fun instanceColor(attribute: Attribute = Attribute.INSTANCE_COLOR, blendMode: BlendMode = BlendMode.Set) {
        colorSources += InstanceColor(attribute, blendMode)
    }

    val primaryUniform: UniformColor?
        get() = colorSources.find { it is UniformColor } as? UniformColor

    val primaryTexture: TextureColor?
        get() = colorSources.find { it is TextureColor } as? TextureColor

    sealed class ColorSource(val blendMode: BlendMode)
    class ConstColor(val constColor: Color, blendMode: BlendMode) : ColorSource(blendMode)
    class UniformColor(val defaultColor: Color?, val uniformName: String, blendMode: BlendMode) : ColorSource(blendMode)
    class VertexColor(val colorAttrib: Attribute, blendMode: BlendMode) : ColorSource(blendMode)
    class TextureColor(val defaultTexture: Texture2d?, val textureName: String, val coordAttribute: Attribute, val gamma: Float, blendMode: BlendMode) : ColorSource(blendMode)
    class InstanceColor(val colorAttrib: Attribute, blendMode: BlendMode) : ColorSource(blendMode)

    enum class BlendMode {
        Set,
        Multiply,
        Add,
        Subtract
    }
}
