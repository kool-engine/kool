package de.fabmax.kool.editor.data

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.modules.ksl.*
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.Shader
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class MaterialComponentData(var materialId: Long) : ComponentData

@Serializable
data class MaterialData(
    val id: Long,
    val name: String,
    var shaderData: MaterialShaderData
) {
    @Transient
    val shaderDataState = mutableStateOf(shaderData).onChange { shaderData = it }

    suspend fun createShader(ibl: EnvironmentMaps?): KslShader = shaderData.createShader(ibl)
    suspend fun updateShader(shader: Shader?, ibl: EnvironmentMaps?): Boolean = shaderData.updateShader(shader, ibl)

    fun matchesShader(shader: Shader?): Boolean = shaderData.matchesShader(shader)

}

@Serializable
sealed interface MaterialShaderData {
    fun matchesShader(shader: Shader?): Boolean
    suspend fun createShader(ibl: EnvironmentMaps?): KslShader
    suspend fun updateShader(shader: Shader?, ibl: EnvironmentMaps?): Boolean
}

@Serializable
data class PbrShaderData(
    val baseColor: MaterialAttribute = ConstColorAttribute(ColorData(MdColor.GREY)),
    val roughness: MaterialAttribute = ConstValueAttribute(0.5f),
    val metallic: MaterialAttribute = ConstValueAttribute(0f),
    val emission: MaterialAttribute = ConstColorAttribute(ColorData(Color.BLACK)),
    val normalMap: MapAttribute? = null,
    val aoMap: MapAttribute? = null,
    val displacementMap: MapAttribute? = null
) : MaterialShaderData {

    override fun matchesShader(shader: Shader?): Boolean {
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
    }

    override suspend fun createShader(ibl: EnvironmentMaps?): KslPbrShader {
        val shader = KslPbrShader {
            color {
                when (val color = baseColor) {
                    is ConstColorAttribute -> uniformColor()
                    is ConstValueAttribute -> uniformColor()
                    is MapAttribute -> textureColor()
                    is VertexAttribute -> vertexColor(Attribute(color.attribName, GlslType.VEC_4F))
                }
            }
            emission {
                when (val color = emission) {
                    is ConstColorAttribute -> uniformColor()
                    is ConstValueAttribute -> uniformColor()
                    is MapAttribute -> textureColor()
                    is VertexAttribute -> vertexColor(Attribute(color.attribName, GlslType.VEC_4F))
                }
            }
            roughness {
                when (val rough = roughness) {
                    is ConstColorAttribute -> uniformProperty()
                    is ConstValueAttribute -> uniformProperty()
                    is MapAttribute -> textureProperty()
                    is VertexAttribute -> vertexProperty(Attribute(rough.attribName, GlslType.FLOAT))
                }
            }
            metallic {
                when (val metal = metallic) {
                    is ConstColorAttribute -> uniformProperty()
                    is ConstValueAttribute -> uniformProperty()
                    is MapAttribute -> textureProperty()
                    is VertexAttribute -> vertexProperty(Attribute(metal.attribName, GlslType.FLOAT))
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
            ibl?.let {
                enableImageBasedLighting(ibl)
            }
        }
        updateShader(shader, ibl)
        return shader
    }

    override suspend fun updateShader(shader: Shader?, ibl: EnvironmentMaps?): Boolean {
        if (!matchesShader(shader)) {
            return false
        }
        val pbrShader = shader as? KslPbrShader ?: return false

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
            is ConstColorAttribute -> pbrShader.color = color.color.toColor()
            is ConstValueAttribute -> pbrShader.color = Color(color.value, color.value, color.value)
            is MapAttribute -> pbrShader.colorMap = colorMap
            is VertexAttribute -> { }
        }
        when (val color = emission) {
            is ConstColorAttribute -> pbrShader.emission = color.color.toColor()
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
    val baseColor: MaterialAttribute = ConstColorAttribute(ColorData(MdColor.GREY)),
    val specularColor: MaterialAttribute = ConstColorAttribute(ColorData(Color.WHITE)),
    val shininess: MaterialAttribute = ConstValueAttribute(16f),
    val specularStrength: MaterialAttribute = ConstValueAttribute(1f),
) : MaterialShaderData {

    override fun matchesShader(shader: Shader?): Boolean {
        if (shader !is KslBlinnPhongShader) {
            return false
        }
        return true
    }

    override suspend fun createShader(ibl: EnvironmentMaps?): KslShader {
        TODO("Not yet implemented")
    }

    override suspend fun updateShader(shader: Shader?, ibl: EnvironmentMaps?): Boolean {
        TODO("Not yet implemented")
    }
}

@Serializable
data class UnlitShaderData(
    val baseColor: MaterialAttribute = ConstColorAttribute(ColorData(MdColor.GREY))
) : MaterialShaderData {

    override fun matchesShader(shader: Shader?): Boolean {
        if (shader !is KslUnlitShader) {
            return false
        }
        return true
    }

    override suspend fun createShader(ibl: EnvironmentMaps?): KslShader {
        TODO("Not yet implemented")
    }

    override suspend fun updateShader(shader: Shader?, ibl: EnvironmentMaps?): Boolean {
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
