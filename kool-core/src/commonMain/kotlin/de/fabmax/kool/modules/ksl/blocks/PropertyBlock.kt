package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d

fun KslScopeBuilder.vertexPropertyBlock(cfg: PropertyBlockConfig): PropertyBlockVertexStage {
    val colorBlock = PropertyBlockVertexStage(cfg, this)
    ops += colorBlock
    return colorBlock
}

fun KslScopeBuilder.fragmentPropertyBlock(cfg: PropertyBlockConfig, vertexStage: PropertyBlockVertexStage? = null): PropertyBlockFragmentStage {
    val colorBlock = PropertyBlockFragmentStage(cfg, vertexStage, this)
    ops += colorBlock
    return colorBlock
}

class PropertyBlockVertexStage(cfg: PropertyBlockConfig, parentScope: KslScopeBuilder) : KslBlock(cfg.propertyName, parentScope) {
    val vertexProperties = mutableMapOf<PropertyBlockConfig.VertexProperty, KslInterStageScalar<KslTypeFloat1>>()
    val instanceProperties = mutableMapOf<PropertyBlockConfig.InstanceProperty, KslInterStageScalar<KslTypeFloat1>>()

    init {
        body.apply {
            check(parentStage is KslVertexStage) { "PropertyBlockVertexStage can only be added to KslVertexStage" }

            cfg.propertySources.filterIsInstance<PropertyBlockConfig.VertexProperty>().mapIndexed { i, source ->
                vertexProperties[source] = parentStage.program.interStageFloat1(name = nextName("${opName}_vertexProp_$i")).apply {
                    input set parentStage.vertexAttribFloat1(source.propertyAttrib.name)
                }
            }
            cfg.propertySources.filterIsInstance<PropertyBlockConfig.InstanceProperty>().mapIndexed { i, source ->
                instanceProperties[source] = parentStage.program.interStageFloat1(name = nextName("${opName}_instanceProp_$i")).apply {
                    input set parentStage.instanceAttribFloat1(source.propertyAttrib.name)
                }
            }
        }
    }
}

class PropertyBlockFragmentStage(cfg: PropertyBlockConfig, vertexPropertyBlock: PropertyBlockVertexStage?, parentScope: KslScopeBuilder) : KslBlock(cfg.propertyName, parentScope) {
    val outProperty = outFloat1(parentScope.nextName("${opName}_outProperty"))
    val outSamplerValues = mutableMapOf<Pair<String, Attribute>, KslVectorExpression<KslTypeFloat4, KslTypeFloat1>>()

    val textures = mutableMapOf<PropertyBlockConfig.TextureProperty, KslUniform<KslTypeColorSampler2d>>()

    init {
        body.apply {
            check(parentStage is KslFragmentStage) { "PropertyBlockFragmentStage can only be added to KslFragmentStage" }

            val texCoordBlock: TexCoordAttributeBlock = parentStage.program.vertexStage.findBlock()
                ?: parentStage.program.vertexStage.main.run { texCoordAttributeBlock() }

            val vertexBlock: PropertyBlockVertexStage = vertexPropertyBlock
                ?: parentStage.program.vertexStage.findBlock(cfg.propertyName)
                ?: parentStage.program.vertexStage.main.run { vertexPropertyBlock(cfg) }

            if (cfg.propertySources.isEmpty() || cfg.propertySources.first().mixMode != PropertyBlockConfig.MixMode.Set) {
                outProperty set 0f.const
            }

            cfg.propertySources.forEach { source ->
                val propertyValue: KslScalarExpression<KslTypeFloat1> = when (source) {
                    is PropertyBlockConfig.ConstProperty -> source.value.const
                    is PropertyBlockConfig.UniformProperty -> parentStage.program.uniformFloat1(source.uniformName)
                    is PropertyBlockConfig.VertexProperty -> vertexBlock.vertexProperties[source]?.output ?: 0f.const
                    is PropertyBlockConfig.InstanceProperty -> vertexBlock.instanceProperties[source]?.output ?: 0f.const
                    is PropertyBlockConfig.TextureProperty ->  {
                        var sampleValue = findExistingSampleValue(source.textureName, source.coordAttribute, parentStage)
                        if (sampleValue == null) {
                            val tex = parentStage.program.texture2d(source.textureName).also { textures[source] = it }
                            val texCoords = texCoordBlock.getAttributeCoords(source.coordAttribute)
                            sampleValue = float4Var(sampleTexture(tex, texCoords)).also {
                                outSamplerValues[source.textureName to source.coordAttribute] = it
                            }
                        }
                        when (source.channel) {
                            0 -> sampleValue.r
                            1 -> sampleValue.g
                            2 -> sampleValue.b
                            3 -> sampleValue.a
                            else -> throw IllegalArgumentException("Invalid TextureProperty channel: ${source.channel}")
                        }
                    }
                }
                mixValue(source.mixMode, propertyValue)
            }
        }
    }

    private fun findExistingSampleValue(texName: String, attrib: Attribute, parentStage: KslFragmentStage): KslExprFloat4? {
        return parentStage.main.getBlocks(null, mutableListOf())
            .filterIsInstance<PropertyBlockFragmentStage>()
            .map { it.outSamplerValues[texName to attrib] }
            .find { it != null }
    }

    private fun KslScopeBuilder.mixValue(mixMode: PropertyBlockConfig.MixMode, value: KslExprFloat1) {
        when (mixMode) {
            PropertyBlockConfig.MixMode.Set -> outProperty set value
            PropertyBlockConfig.MixMode.Multiply -> outProperty *= value
            PropertyBlockConfig.MixMode.Add -> outProperty += value
            PropertyBlockConfig.MixMode.Subtract -> outProperty -= value
        }
    }
}

class PropertyBlockConfig(val propertyName: String) {
    val propertySources = mutableListOf<PropertySource>()

    fun constProperty(value: Float, mixMode: MixMode = MixMode.Set) {
        propertySources += ConstProperty(value, mixMode)
    }

    fun uniformProperty(defaultValue: Float = 0f, uniformName: String = "u${propertyName}", mixMode: MixMode = MixMode.Set) {
        propertySources += UniformProperty(defaultValue, uniformName, mixMode)
    }

    fun vertexProperty(attribute: Attribute, mixMode: MixMode = MixMode.Set) {
        propertySources += VertexProperty(attribute, mixMode)
    }

    fun textureProperty(defaultTexture: Texture2d? = null,
                        channel: Int = 0,
                        textureName: String = "t${propertyName}",
                        coordAttribute: Attribute = Attribute.TEXTURE_COORDS,
                        mixMode: MixMode = MixMode.Set) {
        propertySources += TextureProperty(defaultTexture, channel, textureName, coordAttribute, mixMode)
    }

    fun instanceProperty(attribute: Attribute = Attribute.INSTANCE_COLOR, mixMode: MixMode = MixMode.Set) {
        propertySources += InstanceProperty(attribute, mixMode)
    }

    val primaryUniform: UniformProperty?
        get() = propertySources.find { it is UniformProperty } as? UniformProperty

    val primaryTexture: TextureProperty?
        get() = propertySources.find { it is TextureProperty } as? TextureProperty

    sealed class PropertySource(val mixMode: MixMode)
    class ConstProperty(val value: Float, mixMode: MixMode) : PropertySource(mixMode)
    class UniformProperty(val defaultValue: Float?, val uniformName: String, mixMode: MixMode) : PropertySource(mixMode)
    class VertexProperty(val propertyAttrib: Attribute, mixMode: MixMode) : PropertySource(mixMode)
    class TextureProperty(val defaultTexture: Texture2d?, val channel: Int, val textureName: String, val coordAttribute: Attribute, mixMode: MixMode) : PropertySource(mixMode)
    class InstanceProperty(val propertyAttrib: Attribute, mixMode: MixMode) : PropertySource(mixMode)

    enum class MixMode {
        Set,
        Multiply,
        Add,
        Subtract
    }
}
