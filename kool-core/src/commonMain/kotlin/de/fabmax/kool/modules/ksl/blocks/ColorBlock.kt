package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.util.Color

fun KslScopeBuilder.vertexColorBlock(cfg: ColorBlockConfig): ColorBlockVertexStage {
    val colorBlock = ColorBlockVertexStage(cfg, "colorBlock", this)
    ops += colorBlock
    return colorBlock
}

fun KslScopeBuilder.fragmentColorBlock(cfg: ColorBlockConfig, vertexStage: ColorBlockVertexStage? = null): ColorBlockFragmentStage {
    val colorBlock = ColorBlockFragmentStage(cfg, vertexStage, "colorBlock", this)
    ops += colorBlock
    return colorBlock
}

class ColorBlockVertexStage(cfg: ColorBlockConfig, name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val texCoords = mutableMapOf<ColorBlockConfig.TextureColor, KslInterStageVar<KslTypeFloat2>>()
    val vertexColors = mutableMapOf<ColorBlockConfig.VertexColor, KslInterStageVector<KslTypeFloat4, KslTypeFloat1>>()
    val instanceColors = mutableMapOf<ColorBlockConfig.InstanceColor, KslInterStageVector<KslTypeFloat4, KslTypeFloat1>>()

    init {
        body.apply {
            check(parentStage is KslVertexStage) { "ColorBlockVertexStage can only be added to KslVertexStage" }

            cfg.colorSources.filterIsInstance<ColorBlockConfig.TextureColor>().mapIndexed { i, source ->
                texCoords[source] = parentStage.program.interStageFloat2(name = nextName("${name}_texUv_$i")).apply {
                    input set parentStage.vertexAttribFloat2(source.coordAttribute.name)
                }
            }
            cfg.colorSources.filterIsInstance<ColorBlockConfig.VertexColor>().mapIndexed { i, source ->
                vertexColors[source] = parentStage.program.interStageFloat4(name = nextName("${name}_vertexColor_$i")).apply {
                    input set parentStage.vertexAttribFloat4(source.colorAttrib.name)
                }
            }
            cfg.colorSources.filterIsInstance<ColorBlockConfig.InstanceColor>().mapIndexed { i, source ->
                instanceColors[source] = parentStage.program.interStageFloat4(name = nextName("${name}_instanceColor_$i")).apply {
                    input set parentStage.instanceAttribFloat4(source.colorAttrib.name)
                }
            }
        }
    }
}

class ColorBlockFragmentStage(cfg: ColorBlockConfig, vertexColorBlock: ColorBlockVertexStage?, name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val outColor = outFloat4(parentScope.nextName("${name}_outColor"))

    val textures = mutableMapOf<ColorBlockConfig.TextureColor, KslUniform<KslTypeColorSampler2d>>()

    init {
        body.apply {
            check(parentStage is KslFragmentStage) { "ColorBlockFragmentStage can only be added to KslFragmentStage" }

            val vertexBlock = vertexColorBlock
                ?: parentStage.program.vertexStage.findBlock()
                ?: parentStage.program.vertexStage.main.run { vertexColorBlock(cfg) }

            outColor set cfg.staticColor.const

            cfg.colorSources.forEach { source ->
                val colorValue: KslVectorExpression<KslTypeFloat4, KslTypeFloat1> = when (source) {
                    is ColorBlockConfig.UniformColor -> parentStage.program.uniformFloat4(source.uniformName)
                    is ColorBlockConfig.VertexColor -> vertexBlock.vertexColors[source]?.output ?: Vec4f.ZERO.const
                    is ColorBlockConfig.InstanceColor -> vertexBlock.instanceColors[source]?.output ?: Vec4f.ZERO.const
                    is ColorBlockConfig.TextureColor ->  {
                        val tex = parentStage.program.texture2d(source.textureName).also { textures[source] = it }
                        sampleTexture(tex, vertexBlock.texCoords[source]?.output ?: Vec2f.ZERO.const)
                    }
                }
                mixColor(source.mixMode, colorValue)
            }
        }
    }

    private fun KslScopeBuilder.mixColor(mixMode: ColorBlockConfig.MixMode, value: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>) {
        when (mixMode) {
            ColorBlockConfig.MixMode.Set -> outColor set value
            ColorBlockConfig.MixMode.Multiply -> outColor *= value
            ColorBlockConfig.MixMode.Add -> outColor += value
            ColorBlockConfig.MixMode.Subtract -> outColor -= value
        }
    }
}

class ColorBlockConfig {
    var staticColor = Color.BLACK
    val colorSources = mutableListOf<ColorSource>()

    fun addUniformColor(defaultColor: Color? = null, uniformName: String = "uColor", mixMode: MixMode = MixMode.Set) {
        colorSources += UniformColor(defaultColor, uniformName, mixMode)
    }

    fun addVertexColor(attribute: Attribute = Attribute.COLORS, mixMode: MixMode = MixMode.Set) {
        colorSources += VertexColor(attribute, mixMode)
    }

    fun addTextureColor(defaultTexture: Texture2d? = null,
                        textureName: String = "tColor",
                        coordAttribute: Attribute = Attribute.TEXTURE_COORDS,
                        mixMode: MixMode = MixMode.Set) {
        colorSources += TextureColor(defaultTexture, textureName, coordAttribute, mixMode)
    }

    fun addInstanceColor(attribute: Attribute = Attribute.INSTANCE_COLOR, mixMode: MixMode = MixMode.Set) {
        colorSources += InstanceColor(attribute, mixMode)
    }

    val primaryUniformColor: UniformColor?
        get() = colorSources.find { it is UniformColor } as? UniformColor

    val primaryTextureColor: TextureColor?
        get() = colorSources.find { it is TextureColor } as? TextureColor

    sealed class ColorSource(val mixMode: MixMode)
    class UniformColor(val defaultColor: Color?, val uniformName: String, mixMode: MixMode) : ColorSource(mixMode)
    class VertexColor(val colorAttrib: Attribute, mixMode: MixMode) : ColorSource(mixMode)
    class TextureColor(val defaultTexture: Texture2d?, val textureName: String, val coordAttribute: Attribute, mixMode: MixMode) : ColorSource(mixMode)
    class InstanceColor(val colorAttrib: Attribute, mixMode: MixMode) : ColorSource(mixMode)

    enum class MixMode {
        Set,
        Multiply,
        Add,
        Subtract
    }
}
