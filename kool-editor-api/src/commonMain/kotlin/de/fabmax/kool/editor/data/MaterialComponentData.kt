package de.fabmax.kool.editor.data

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlinx.serialization.Serializable


@Serializable
data class MaterialComponentData(var materialId: Long) : ComponentData

@Serializable
data class MaterialData(
    val id: Long,
    val name: String,
    var shaderData: MaterialShaderData
) {

    suspend fun createShader(ibl: EnvironmentMaps?): KslShader {
        return when (val data = shaderData) {
            is BlinnPhongShaderData -> TODO()
            is PbrShaderData -> createPbrShader(data, ibl)
            is UnlitShaderData -> TODO()
        }
    }

    private suspend fun createPbrShader(pbrData: PbrShaderData, ibl: EnvironmentMaps?): KslPbrShader {
        val colorMap = (pbrData.baseColor as? MapAttribute)?.let { AppAssets.loadTexture2d(it.mapPath) }
        val roughnessMap = (pbrData.roughness as? MapAttribute)?.let { AppAssets.loadTexture2d(it.mapPath) }
        val metallicMap = (pbrData.metallic as? MapAttribute)?.let { AppAssets.loadTexture2d(it.mapPath) }
        val emissionMap = (pbrData.emission as? MapAttribute)?.let { AppAssets.loadTexture2d(it.mapPath) }
        val normalMap = pbrData.normalMap?.let { AppAssets.loadTexture2d(it.mapPath) }
        val aoMap = pbrData.aoMap?.let { AppAssets.loadTexture2d(it.mapPath) }
        val displacementMap = pbrData.displacementMap?.let { AppAssets.loadTexture2d(it.mapPath) }

        return KslPbrShader {
            color {
                when (val color = pbrData.baseColor) {
                    is ConstColorAttribute -> uniformColor(color.color.toColor())
                    is ConstValueAttribute -> uniformColor(Color(color.value, color.value, color.value).toLinear())
                    is MapAttribute -> textureColor(colorMap)
                    is VertexAttribute -> vertexColor(Attribute(color.attribName, GlslType.VEC_4F))
                }
            }
            emission {
                when (val color = pbrData.emission) {
                    is ConstColorAttribute -> uniformColor(color.color.toColor())
                    is ConstValueAttribute -> uniformColor(Color(color.value, color.value, color.value).toLinear())
                    is MapAttribute -> textureColor(emissionMap)
                    is VertexAttribute -> vertexColor(Attribute(color.attribName, GlslType.VEC_4F))
                }
            }
            roughness {
                when (val rough = pbrData.roughness) {
                    is ConstColorAttribute -> uniformProperty(rough.color.r)
                    is ConstValueAttribute -> uniformProperty(rough.value)
                    is MapAttribute -> textureProperty(roughnessMap, rough.singleChannelIndex)
                    is VertexAttribute -> vertexProperty(Attribute(rough.attribName, GlslType.FLOAT))
                }
            }
            metallic {
                when (val metal = pbrData.metallic) {
                    is ConstColorAttribute -> uniformProperty(metal.color.r)
                    is ConstValueAttribute -> uniformProperty(metal.value)
                    is MapAttribute -> textureProperty(metallicMap, metal.singleChannelIndex)
                    is VertexAttribute -> vertexProperty(Attribute(metal.attribName, GlslType.FLOAT))
                }
            }
            pbrData.aoMap?.let {
                ao {
                    materialAo {
                        textureProperty(aoMap, it.singleChannelIndex)
                    }
                }
            }
            pbrData.displacementMap?.let {
                vertices {
                    displacement {
                        textureProperty(displacementMap, it.singleChannelIndex)
                    }
                }
            }
            normalMap?.let {
                normalMapping {
                    setNormalMap(normalMap)
                }
            }
            ibl?.let {
                enableImageBasedLighting(ibl)
            }
        }
    }
}

@Serializable
sealed interface MaterialShaderData

@Serializable
data class PbrShaderData(
    val baseColor: MaterialAttribute = ConstColorAttribute(ColorData(MdColor.GREY)),
    val roughness: MaterialAttribute = ConstValueAttribute(0.5f),
    val metallic: MaterialAttribute = ConstValueAttribute(0f),
    val emission: MaterialAttribute = ConstColorAttribute(ColorData(Color.BLACK)),
    val normalMap: MapAttribute? = null,
    val aoMap: MapAttribute? = null,
    val displacementMap: MapAttribute? = null
) : MaterialShaderData

@Serializable
data class BlinnPhongShaderData(
    val baseColor: MaterialAttribute = ConstColorAttribute(ColorData(MdColor.GREY)),
    val specularColor: MaterialAttribute = ConstColorAttribute(ColorData(Color.WHITE)),
    val shininess: MaterialAttribute = ConstValueAttribute(16f),
    val specularStrength: MaterialAttribute = ConstValueAttribute(1f),
) : MaterialShaderData

@Serializable
data class UnlitShaderData(
    val baseColor: MaterialAttribute = ConstColorAttribute(ColorData(MdColor.GREY))
) : MaterialShaderData

@Serializable
sealed interface MaterialAttribute

@Serializable
class ConstColorAttribute(val color: ColorData) : MaterialAttribute

@Serializable
class ConstValueAttribute(val value: Float) : MaterialAttribute

@Serializable
class VertexAttribute(val attribName: String) : MaterialAttribute

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
}
