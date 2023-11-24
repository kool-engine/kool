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
                            0 -> attrib.float1("x")
                            1 -> attrib.float1("y")
                            2 -> attrib.float1("z")
                            else -> attrib.float1("w")
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
                            0 -> attrib.float1("x")
                            1 -> attrib.float1("y")
                            2 -> attrib.float1("z")
                            else -> attrib.float1("w")
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

            if (cfg.propertySources.isEmpty() || cfg.propertySources.first().blendMode != PropertyBlockConfig.BlendMode.Set) {
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
                mixValue(source.blendMode, propertyValue)
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

    private fun vertexBlock(parentStage: KslShaderStage): PropertyBlockVertexStage {
        var block: PropertyBlockVertexStage? = vertexPropertyBlock ?: parentStage.program.vertexStage?.findBlock(cfg.propertyName)
        if (block == null) {
            parentStage.program.vertexStage {
                main { block = vertexPropertyBlock(cfg) }
            }
        }
        return block!!
    }

    private fun findExistingSampleValue(texName: String, attrib: Attribute, parentStage: KslFragmentStage): KslExprFloat4? {
        return parentStage.main.getBlocks(null, mutableListOf())
            .filterIsInstance<PropertyBlockFragmentStage>()
            .map { it.outSamplerValues[texName to attrib] }
            .find { it != null }
    }

    private fun KslScopeBuilder.mixValue(blendMode: PropertyBlockConfig.BlendMode, value: KslExprFloat1) {
        when (blendMode) {
            PropertyBlockConfig.BlendMode.Set -> outProperty set value
            PropertyBlockConfig.BlendMode.Multiply -> outProperty *= value
            PropertyBlockConfig.BlendMode.Add -> outProperty += value
            PropertyBlockConfig.BlendMode.Subtract -> outProperty -= value
        }
    }
}

data class PropertyBlockConfig(
    val propertyName: String,
    val propertySources: MutableList<PropertySource> = mutableListOf()
) {

    fun constProperty(value: Float, blendMode: BlendMode = BlendMode.Set) {
        propertySources += ConstProperty(value, blendMode)
    }

    fun uniformProperty(defaultValue: Float = 0f, uniformName: String = "u${propertyName}", blendMode: BlendMode = BlendMode.Set) {
        propertySources += UniformProperty(defaultValue, uniformName, blendMode)
    }

    fun vertexProperty(attribute: Attribute, channel: Int = 0, blendMode: BlendMode = BlendMode.Set) {
        propertySources += VertexProperty(attribute, channel, blendMode)
    }

    fun textureProperty(defaultTexture: Texture2d? = null,
                        channel: Int = 0,
                        textureName: String = "t${propertyName}",
                        coordAttribute: Attribute = Attribute.TEXTURE_COORDS,
                        blendMode: BlendMode = BlendMode.Set) {
        propertySources += TextureProperty(defaultTexture, channel, textureName, coordAttribute, blendMode)
    }

    fun instanceProperty(attribute: Attribute, channel: Int = 0, blendMode: BlendMode = BlendMode.Set) {
        propertySources += InstanceProperty(attribute, channel, blendMode)
    }

    val primaryUniform: UniformProperty?
        get() = propertySources.find { it is UniformProperty } as? UniformProperty

    val primaryTexture: TextureProperty?
        get() = propertySources.find { it is TextureProperty } as? TextureProperty

    sealed class PropertySource(val blendMode: BlendMode)
    class ConstProperty(val value: Float, blendMode: BlendMode) : PropertySource(blendMode)
    class UniformProperty(val defaultValue: Float?, val uniformName: String, blendMode: BlendMode) : PropertySource(blendMode)
    class VertexProperty(val propertyAttrib: Attribute, val channel: Int, blendMode: BlendMode) : PropertySource(blendMode)
    class TextureProperty(val defaultTexture: Texture2d?, val channel: Int, val textureName: String, val coordAttribute: Attribute, blendMode: BlendMode) : PropertySource(blendMode)
    class InstanceProperty(val propertyAttrib: Attribute, val channel: Int, blendMode: BlendMode) : PropertySource(blendMode)

    fun isEmptyOrConst(constValue: Float): Boolean {
        if (propertySources.size == 1) {
            val ps = propertySources[0]
            if (ps is ConstProperty) {
                return ps.value == constValue
            }
        }
        return propertySources.isEmpty()
    }

    enum class BlendMode {
        Set,
        Multiply,
        Add,
        Subtract
    }
}
