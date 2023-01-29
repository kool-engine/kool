package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d

fun KslScopeBuilder.vertexPropertyBlock(cfg: PropertyBlockConfig): PropertyBlockVertexStage {
    val propertyBlock = PropertyBlockVertexStage(cfg, this)
    ops += propertyBlock
    return propertyBlock
}

fun KslScopeBuilder.fragmentPropertyBlock(cfg: PropertyBlockConfig, vertexStage: PropertyBlockVertexStage? = null): PropertyBlockFragmentStage {
    val propertyBlock = PropertyBlockFragmentStage(cfg, vertexStage, this)
    ops += propertyBlock
    return propertyBlock
}

class PropertyBlockVertexStage(cfg: PropertyBlockConfig, parentScope: KslScopeBuilder) : KslBlock(cfg.propertyName, parentScope) {
    val vertexProperties = mutableMapOf<PropertyBlockConfig.VertexProperty, KslInterStageScalar<KslTypeFloat1>>()
    val instanceProperties = mutableMapOf<PropertyBlockConfig.InstanceProperty, KslInterStageScalar<KslTypeFloat1>>()

    init {
        body.apply {
            check(parentStage is KslVertexStage) { "PropertyBlockVertexStage can only be added to KslVertexStage" }

            cfg.propertySources.filterIsInstance<PropertyBlockConfig.VertexProperty>().mapIndexed { i, source ->
                vertexProperties[source] = parentStage.program.interStageFloat1(name = nextName("${opName}_vertexProp_$i")).apply {
                    val prop = if (source.propertyAttrib.type.channels == 1) {
                        parentStage.vertexAttribFloat1(source.propertyAttrib.name)
                    } else {
                        val attrib = when (source.propertyAttrib.type.channels) {
                            2 -> parentStage.vertexAttribFloat2(source.propertyAttrib.name)
                            3 -> parentStage.vertexAttribFloat3(source.propertyAttrib.name)
                            else -> parentStage.vertexAttribFloat4(source.propertyAttrib.name)
                        }
                        when (source.channel) {
                            0 -> attrib.x
                            1 -> attrib.y
                            2 -> attrib.z
                            else -> attrib.w
                        }
                    }
                    input set prop
                }
            }
            cfg.propertySources.filterIsInstance<PropertyBlockConfig.InstanceProperty>().mapIndexed { i, source ->
                instanceProperties[source] = parentStage.program.interStageFloat1(name = nextName("${opName}_instanceProp_$i")).apply {
                    val prop = if (source.propertyAttrib.type.channels == 1) {
                        parentStage.instanceAttribFloat1(source.propertyAttrib.name)
                    } else {
                        val attrib = when (source.propertyAttrib.type.channels) {
                            2 -> parentStage.instanceAttribFloat2(source.propertyAttrib.name)
                            3 -> parentStage.instanceAttribFloat3(source.propertyAttrib.name)
                            else -> parentStage.instanceAttribFloat4(source.propertyAttrib.name)
                        }
                        when (source.channel) {
                            0 -> attrib.x
                            1 -> attrib.y
                            2 -> attrib.z
                            else -> attrib.w
                        }
                    }
                    input set prop
                }
            }
        }
    }
}

class PropertyBlockFragmentStage(
    private val cfg: PropertyBlockConfig,
    private val vertexPropertyBlock: PropertyBlockVertexStage?,
    parentScope: KslScopeBuilder
) : KslBlock(cfg.propertyName, parentScope) {

    val outProperty = outFloat1(parentScope.nextName("${opName}_outProperty"))
    val outSamplerValues = mutableMapOf<Pair<String, Attribute>, KslVectorExpression<KslTypeFloat4, KslTypeFloat1>>()

    val textures = mutableMapOf<PropertyBlockConfig.TextureProperty, KslUniform<KslTypeColorSampler2d>>()

    init {
        body.apply {
            check(parentStage is KslFragmentStage) { "PropertyBlockFragmentStage can only be added to KslFragmentStage" }

            if (cfg.propertySources.isEmpty() || cfg.propertySources.first().mixMode != PropertyBlockConfig.MixMode.Set) {
                outProperty set 0f.const
            }

            cfg.propertySources.forEach { source ->
                val propertyValue: KslScalarExpression<KslTypeFloat1> = when (source) {
                    is PropertyBlockConfig.ConstProperty -> source.value.const
                    is PropertyBlockConfig.UniformProperty -> parentStage.program.uniformFloat1(source.uniformName)
                    is PropertyBlockConfig.VertexProperty -> vertexBlock(parentStage).vertexProperties[source]?.output ?: 0f.const
                    is PropertyBlockConfig.InstanceProperty -> vertexBlock(parentStage).instanceProperties[source]?.output ?: 0f.const
                    is PropertyBlockConfig.TextureProperty ->  {
                        var sampleValue = findExistingSampleValue(source.textureName, source.coordAttribute, parentStage)
                        if (sampleValue == null) {
                            val tex = parentStage.program.texture2d(source.textureName).also { textures[source] = it }
                            sampleValue = parentScope.run {
                                val texCoords = texCoordBlock(parentStage).getAttributeCoords(source.coordAttribute)
                                float4Var(sampleTexture(tex, texCoords)).also {
                                    outSamplerValues[source.textureName to source.coordAttribute] = it
                                }
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

    private fun texCoordBlock(parentStage: KslShaderStage): TexCoordAttributeBlock {
        return parentStage.program.vertexStage.findBlock()
            ?: parentStage.program.vertexStage.main.run { texCoordAttributeBlock() }
    }

    private fun vertexBlock(parentStage: KslShaderStage): PropertyBlockVertexStage {
        return vertexPropertyBlock
            ?: parentStage.program.vertexStage.findBlock(cfg.propertyName)
            ?: parentStage.program.vertexStage.main.run { vertexPropertyBlock(cfg) }
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

data class PropertyBlockConfig(
    val propertyName: String,
    val propertySources: MutableList<PropertySource> = mutableListOf()
) {

    fun constProperty(value: Float, mixMode: MixMode = MixMode.Set) {
        propertySources += ConstProperty(value, mixMode)
    }

    fun uniformProperty(defaultValue: Float = 0f, uniformName: String = "u${propertyName}", mixMode: MixMode = MixMode.Set) {
        propertySources += UniformProperty(defaultValue, uniformName, mixMode)
    }

    fun vertexProperty(attribute: Attribute, channel: Int = 0, mixMode: MixMode = MixMode.Set) {
        propertySources += VertexProperty(attribute, channel, mixMode)
    }

    fun textureProperty(defaultTexture: Texture2d? = null,
                        channel: Int = 0,
                        textureName: String = "t${propertyName}",
                        coordAttribute: Attribute = Attribute.TEXTURE_COORDS,
                        mixMode: MixMode = MixMode.Set) {
        propertySources += TextureProperty(defaultTexture, channel, textureName, coordAttribute, mixMode)
    }

    fun instanceProperty(attribute: Attribute, channel: Int = 0, mixMode: MixMode = MixMode.Set) {
        propertySources += InstanceProperty(attribute, channel, mixMode)
    }

    val primaryUniform: UniformProperty?
        get() = propertySources.find { it is UniformProperty } as? UniformProperty

    val primaryTexture: TextureProperty?
        get() = propertySources.find { it is TextureProperty } as? TextureProperty

    sealed class PropertySource(val mixMode: MixMode)
    class ConstProperty(val value: Float, mixMode: MixMode) : PropertySource(mixMode)
    class UniformProperty(val defaultValue: Float?, val uniformName: String, mixMode: MixMode) : PropertySource(mixMode)
    class VertexProperty(val propertyAttrib: Attribute, val channel: Int, mixMode: MixMode) : PropertySource(mixMode)
    class TextureProperty(val defaultTexture: Texture2d?, val channel: Int, val textureName: String, val coordAttribute: Attribute, mixMode: MixMode) : PropertySource(mixMode)
    class InstanceProperty(val propertyAttrib: Attribute, val channel: Int, mixMode: MixMode) : PropertySource(mixMode)

    fun isEmptyOrConst(constValue: Float): Boolean {
        if (propertySources.size == 1) {
            val ps = propertySources[0]
            if (ps is ConstProperty) {
                return ps.value == constValue
            }
        }
        return propertySources.isEmpty()
    }

    enum class MixMode {
        Set,
        Multiply,
        Add,
        Subtract
    }
}
