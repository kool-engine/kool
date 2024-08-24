package de.fabmax.kool.editor.data

import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.PipelineConfig
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.logE
import kotlinx.serialization.Serializable


@Serializable
data class MaterialReferenceComponentData(val materialId: EntityId) : ComponentData

@Serializable
data class MaterialComponentData(val name: String, val shaderData: MaterialShaderData) : ComponentData

@Serializable
sealed interface MaterialShaderData {
    val genericSettings: GenericMaterialSettings

    fun copy(genericSettings: GenericMaterialSettings = this.genericSettings): MaterialShaderData {
        return when (this) {
            is BlinnPhongShaderData -> copy(baseColor = baseColor, genericSettings = genericSettings)
            is PbrShaderData -> copy(baseColor = baseColor, genericSettings = genericSettings)
            is UnlitShaderData -> copy(baseColor = baseColor, genericSettings = genericSettings)
            is PbrSplatShaderData -> copy(splatMap = splatMap, genericSettings = genericSettings)
        }
    }

    fun collectAttributes(): List<MaterialAttribute>
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
    val parallaxStrength: Float = 1f,
    val parallaxOffset: Float = 0f,
    val parallaxSteps: Int = 16,
    override val genericSettings: GenericMaterialSettings = GenericMaterialSettings()
) : MaterialShaderData {
    override fun collectAttributes(): List<MaterialAttribute> = listOfNotNull(
        baseColor,
        roughness,
        metallic,
        emission,
        normalMap,
        aoMap,
        displacementMap
    )
}

@Serializable
data class PbrSplatShaderData(
    val splatMap: MapAttribute? = null,
    val debugMode: Int = 0,
    val materialMaps: List<SplatMapData> = listOf(SplatMapData(), SplatMapData()),
    override val genericSettings: GenericMaterialSettings = GenericMaterialSettings()
) : MaterialShaderData {
    override fun collectAttributes(): List<MaterialAttribute> {
        val attribs = listOf(splatMap) + materialMaps.flatMap { it.collectAttributes() }
        return attribs.filterNotNull().distinct()
    }
}

@Serializable
data class SplatMapData(
    val baseColor: MaterialAttribute = ConstColorAttribute(ColorData(MdColor.GREY toneLin 500)),
    val roughness: MaterialAttribute = ConstValueAttribute(0.5f),
    val metallic: MaterialAttribute = ConstValueAttribute(0f),
    val emission: MaterialAttribute = ConstColorAttribute(ColorData(Color.BLACK.toLinear())),
    val normalMap: MapAttribute? = null,
    val aoMap: MapAttribute? = null,
    val displacementMap: MapAttribute? = null,
    val textureScale: Float = 10f,
    val textureRotation: Float = 0f,
    val stochasticTileSize: Float = 0.5f,
    val stochasticRotation: Float = 360f,
) {
    fun collectAttributes(): List<MaterialAttribute> = listOfNotNull(
        baseColor,
        roughness,
        metallic,
        emission,
        normalMap,
        aoMap,
        displacementMap
    )
}

@Serializable
data class BlinnPhongShaderData(
    val baseColor: MaterialAttribute = ConstColorAttribute(ColorData(MdColor.GREY toneLin 500)),
    val specularColor: MaterialAttribute = ConstColorAttribute(ColorData(Color.WHITE.toLinear())),
    val shininess: MaterialAttribute = ConstValueAttribute(16f),
    val specularStrength: MaterialAttribute = ConstValueAttribute(1f),
    override val genericSettings: GenericMaterialSettings = GenericMaterialSettings()
) : MaterialShaderData {
    override fun collectAttributes(): List<MaterialAttribute> = listOf(
        baseColor,
        specularColor,
        shininess,
        specularStrength
    )
}

@Serializable
data class UnlitShaderData(
    val baseColor: MaterialAttribute = ConstColorAttribute(ColorData(MdColor.GREY toneLin 500)),
    override val genericSettings: GenericMaterialSettings = GenericMaterialSettings()
) : MaterialShaderData {
    override fun collectAttributes(): List<MaterialAttribute> = listOf(baseColor)
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
data class ConstColorAttribute(val color: ColorData) : MaterialAttribute {
    override fun matchesCfg(cfg: ColorBlockConfig): Boolean {
        return cfg.colorSources.any { it is ColorBlockConfig.UniformColor }
    }
}

@Serializable
data class ConstValueAttribute(val value: Float) : MaterialAttribute {
    override fun matchesCfg(cfg: PropertyBlockConfig): Boolean {
        return cfg.propertySources.any { it is PropertyBlockConfig.UniformProperty }
    }
}

@Serializable
data class VertexAttribute(val attribName: String) : MaterialAttribute {
    override fun matchesCfg(cfg: PropertyBlockConfig): Boolean {
        return cfg.propertySources.any { it is PropertyBlockConfig.VertexProperty }
    }
}

@Serializable
data class MapAttribute(val mapPath: String, val channels: String? = null, val format: TexFormat? = null) : MaterialAttribute {
    val singleChannelIndex: Int
        get() {
            return when (channels?.lowercase()) {
                "r" -> 0
                "g" -> 1
                "b" -> 2
                "a" -> 3
                else -> {
                    logE { "Invalid MapAttribute single channel value: $channels (must be either of 'r', 'g', 'b' or 'a')" }
                    0
                }
            }
        }

    val mapName: String
        get() = mapPath.replaceBeforeLast("/", "").removePrefix("/")

    override fun matchesCfg(cfg: ColorBlockConfig): Boolean {
        return cfg.colorSources.any { it is ColorBlockConfig.TextureColor || it is ColorBlockConfig.TextureArrayColor }
    }

    override fun matchesCfg(cfg: PropertyBlockConfig): Boolean {
        return cfg.propertySources.any {
            (it is PropertyBlockConfig.TextureProperty && it.channel == singleChannelIndex) ||
            (it is PropertyBlockConfig.TextureArrayProperty && it.channel == singleChannelIndex)
        }
    }
}

@Serializable
data class GenericMaterialSettings(
    val isTwoSided: Boolean = false,
    val isCastingShadow: Boolean = true
) {
    fun matchesPipelineConfig(cfg: PipelineConfig): Boolean {
        return isTwoSided == (cfg.cullMethod == CullMethod.NO_CULLING)
    }
}
