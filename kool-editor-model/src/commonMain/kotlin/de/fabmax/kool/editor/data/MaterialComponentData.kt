package de.fabmax.kool.editor.data

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.modules.ksl.*
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class MaterialComponentData(var materialId: Long) : ComponentData

@Serializable
data class MaterialData(
    val id: Long,
    var name: String,
    var shaderData: MaterialShaderData
) {
    @Transient
    val nameState = mutableStateOf(name).onChange { name = it }
    @Transient
    val shaderDataState = mutableStateOf(shaderData).onChange { shaderData = it }

    suspend fun createShader(sceneShaderData: SceneModel.SceneShaderData): KslShader {
        return shaderData.createShader(sceneShaderData)
    }

    suspend fun updateShader(shader: DrawShader?, sceneShaderData: SceneModel.SceneShaderData): Boolean {
        return shaderData.updateShader(shader, sceneShaderData)
    }

    fun matchesShader(shader: DrawShader?): Boolean = shaderData.matchesShader(shader)

}

@Serializable
sealed interface MaterialShaderData {
    val genericSettings: GenericMaterialSettings

    fun matchesShader(shader: DrawShader?): Boolean
    suspend fun createShader(sceneShaderData: SceneModel.SceneShaderData): KslShader
    suspend fun updateShader(shader: DrawShader?, sceneShaderData: SceneModel.SceneShaderData): Boolean

    fun copy(genericSettings: GenericMaterialSettings = this.genericSettings): MaterialShaderData {
        return when (this) {
            is BlinnPhongShaderData -> copy(baseColor = baseColor, genericSettings = genericSettings)
            is PbrShaderData -> copy(baseColor = baseColor, genericSettings = genericSettings)
            is UnlitShaderData -> copy(baseColor = baseColor, genericSettings = genericSettings)
        }
    }
}

@Serializable
data class PbrShaderData(
    val baseColor: MaterialAttribute = ConstColorAttribute(ColorData(MdColor.GREY toneLin 500)),
    val roughness: MaterialAttribute = ConstValueAttribute(0.5f),
    val metallic: MaterialAttribute = ConstValueAttribute(0f),
    val emission: MaterialAttribute = ConstColorAttribute(ColorData(Color.BLACK.toLinear())),
    val normalMap: MapAttribute? = null,
    val aoMap: MapAttribute? = null,
    val displacementMap: MapAttribute? = null,
    override val genericSettings: GenericMaterialSettings = GenericMaterialSettings()
) : MaterialShaderData {

    override fun matchesShader(shader: DrawShader?): Boolean {
        if (shader !is KslPbrShader) {
            return false
        }
        return baseColor.matchesCfg(shader.colorCfg)
                && roughness.matchesCfg(shader.roughnessCfg)
                && metallic.matchesCfg(shader.metallicCfg)
                && emission.matchesCfg(shader.emissionCfg)
                && aoMap?.matchesCfg(shader.materialAoCfg) != false
                && displacementMap?.matchesCfg(shader.displacementCfg) != false
                && shader.isNormalMapped == (normalMap != null)
                && genericSettings.matchesPipelineConfig(shader.pipelineConfig)
    }

    override suspend fun createShader(sceneShaderData: SceneModel.SceneShaderData): KslPbrShader {
        val shader = KslPbrShader {
            pipeline {
                if (genericSettings.isTwoSided) {
                    cullMethod = CullMethod.NO_CULLING
                }
            }
            color {
                when (val color = baseColor) {
                    is ConstColorAttribute -> uniformColor()
                    is ConstValueAttribute -> uniformColor()
                    is MapAttribute -> textureColor()
                    is VertexAttribute -> vertexColor(Attribute(color.attribName, GpuType.FLOAT4))
                }
            }
            emission {
                when (val color = emission) {
                    is ConstColorAttribute -> uniformColor()
                    is ConstValueAttribute -> uniformColor()
                    is MapAttribute -> textureColor()
                    is VertexAttribute -> vertexColor(Attribute(color.attribName, GpuType.FLOAT4))
                }
            }
            roughness {
                when (val rough = roughness) {
                    is ConstColorAttribute -> uniformProperty()
                    is ConstValueAttribute -> uniformProperty()
                    is MapAttribute -> textureProperty()
                    is VertexAttribute -> vertexProperty(Attribute(rough.attribName, GpuType.FLOAT1))
                }
            }
            metallic {
                when (val metal = metallic) {
                    is ConstColorAttribute -> uniformProperty()
                    is ConstValueAttribute -> uniformProperty()
                    is MapAttribute -> textureProperty()
                    is VertexAttribute -> vertexProperty(Attribute(metal.attribName, GpuType.FLOAT1))
                }
            }
            this@PbrShaderData.aoMap?.let {
                ao {
                    materialAo {
                        textureProperty(null, it.singleChannelIndex)
                    }
                }
            }
            this@PbrShaderData.displacementMap?.let {
                vertices {
                    displacement {
                        textureProperty(null, it.singleChannelIndex)
                    }
                }
            }
            this@PbrShaderData.normalMap?.let {
                normalMapping {
                    setNormalMap()
                }
            }

            shadow { addShadowMaps(sceneShaderData.shadowMaps) }
            maxNumberOfLights = sceneShaderData.maxNumberOfLights
            sceneShaderData.environmentMaps?.let {
                enableImageBasedLighting(it)
            }
            sceneShaderData.ssaoMap?.let {
                ao { enableSsao(it) }
            }
        }
        updateShader(shader, sceneShaderData)
        return shader
    }

    override suspend fun updateShader(shader: DrawShader?, sceneShaderData: SceneModel.SceneShaderData): Boolean {
        if (!matchesShader(shader)) {
            return false
        }
        val pbrShader = shader as? KslPbrShader ?: return false

        val ibl = sceneShaderData.environmentMaps
        if (ibl != null && shader.ambientCfg is KslLitShader.AmbientColor.Uniform) {
            return false
        }

        val colorMap = (baseColor as? MapAttribute)?.let { AppAssets.loadTexture2d(it.mapPath) }
        val roughnessMap = (roughness as? MapAttribute)?.let { AppAssets.loadTexture2d(it.mapPath) }
        val metallicMap = (metallic as? MapAttribute)?.let { AppAssets.loadTexture2d(it.mapPath) }
        val emissionMap = (emission as? MapAttribute)?.let { AppAssets.loadTexture2d(it.mapPath) }
        val normalMap = normalMap?.let { AppAssets.loadTexture2d(it.mapPath) }
        val aoMap = aoMap?.let { AppAssets.loadTexture2d(it.mapPath) }
        val displacementMap = displacementMap?.let { AppAssets.loadTexture2d(it.mapPath) }

        when (val color = baseColor) {
            is ConstColorAttribute -> pbrShader.color = color.color.toColorLinear()
            is ConstValueAttribute -> pbrShader.color = Color(color.value, color.value, color.value)
            is MapAttribute -> pbrShader.colorMap = colorMap
            is VertexAttribute -> { }
        }
        when (val color = emission) {
            is ConstColorAttribute -> pbrShader.emission = color.color.toColorLinear()
            is ConstValueAttribute -> pbrShader.emission = Color(color.value, color.value, color.value)
            is MapAttribute -> pbrShader.emissionMap = emissionMap
            is VertexAttribute -> { }
        }
        when (val rough = roughness) {
            is ConstColorAttribute -> pbrShader.roughness = rough.color.r
            is ConstValueAttribute -> pbrShader.roughness = rough.value
            is MapAttribute -> pbrShader.roughnessMap = roughnessMap
            is VertexAttribute -> { }
        }
        when (val metal = metallic) {
            is ConstColorAttribute -> pbrShader.metallic = metal.color.r
            is ConstValueAttribute -> pbrShader.metallic = metal.value
            is MapAttribute -> pbrShader.metallicMap = metallicMap
            is VertexAttribute -> { }
        }
        pbrShader.normalMap = normalMap
        pbrShader.materialAoMap = aoMap
        pbrShader.displacementMap = displacementMap
        ibl?.let {
            pbrShader.ambientMap = ibl.irradianceMap
            pbrShader.reflectionMap = ibl.reflectionMap
        }
        return true
    }
}

@Serializable
data class BlinnPhongShaderData(
    val baseColor: MaterialAttribute = ConstColorAttribute(ColorData(MdColor.GREY toneLin 500)),
    val specularColor: MaterialAttribute = ConstColorAttribute(ColorData(Color.WHITE.toLinear())),
    val shininess: MaterialAttribute = ConstValueAttribute(16f),
    val specularStrength: MaterialAttribute = ConstValueAttribute(1f),
    override val genericSettings: GenericMaterialSettings = GenericMaterialSettings()
) : MaterialShaderData {

    override fun matchesShader(shader: DrawShader?): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun createShader(sceneShaderData: SceneModel.SceneShaderData): KslShader {
        TODO("Not yet implemented")
    }

    override suspend fun updateShader(shader: DrawShader?, sceneShaderData: SceneModel.SceneShaderData): Boolean {
        TODO("Not yet implemented")
    }
}

@Serializable
data class UnlitShaderData(
    val baseColor: MaterialAttribute = ConstColorAttribute(ColorData(MdColor.GREY toneLin 500)),
    override val genericSettings: GenericMaterialSettings = GenericMaterialSettings()
) : MaterialShaderData {

    override fun matchesShader(shader: DrawShader?): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun createShader(sceneShaderData: SceneModel.SceneShaderData): KslShader {
        TODO("Not yet implemented")
    }

    override suspend fun updateShader(shader: DrawShader?, sceneShaderData: SceneModel.SceneShaderData): Boolean {
        TODO("Not yet implemented")
    }
}

@Serializable
sealed interface MaterialAttribute {
    fun matchesCfg(cfg: ColorBlockConfig): Boolean {
        return false
    }
    fun matchesCfg(cfg: PropertyBlockConfig): Boolean {
        return false
    }
}

@Serializable
class ConstColorAttribute(val color: ColorData) : MaterialAttribute {
    override fun matchesCfg(cfg: ColorBlockConfig): Boolean {
        return cfg.colorSources.any { it is ColorBlockConfig.UniformColor }
    }
}

@Serializable
class ConstValueAttribute(val value: Float) : MaterialAttribute {
    override fun matchesCfg(cfg: PropertyBlockConfig): Boolean {
        return cfg.propertySources.any { it is PropertyBlockConfig.UniformProperty }
    }
}

@Serializable
class VertexAttribute(val attribName: String) : MaterialAttribute {
    override fun matchesCfg(cfg: PropertyBlockConfig): Boolean {
        return cfg.propertySources.any { it is PropertyBlockConfig.VertexProperty }
    }
}

@Serializable
class MapAttribute(val mapPath: String, val channels: String? = null) : MaterialAttribute {
    val singleChannelIndex: Int
        get() {
            return when (channels) {
                "r" -> 0
                "g" -> 1
                "b" -> 2
                "a" -> 3
                else -> 0
            }
        }

    val mapName: String
        get() = mapPath.replaceBeforeLast("/", "").removePrefix("/")

    override fun matchesCfg(cfg: ColorBlockConfig): Boolean {
        return cfg.colorSources.any { it is ColorBlockConfig.TextureColor }
    }

    override fun matchesCfg(cfg: PropertyBlockConfig): Boolean {
        return cfg.propertySources.any { it is PropertyBlockConfig.TextureProperty }
    }
}

@Serializable
data class GenericMaterialSettings(val isTwoSided: Boolean = false) {

    fun matchesPipelineConfig(cfg: PipelineConfig): Boolean {
        return isTwoSided == (cfg.cullMethod == CullMethod.NO_CULLING)
    }

}
