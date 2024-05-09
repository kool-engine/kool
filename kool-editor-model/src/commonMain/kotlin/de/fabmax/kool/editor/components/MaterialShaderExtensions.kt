package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.loadTexture2d
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.DrawShader
import de.fabmax.kool.pipeline.GpuType
import de.fabmax.kool.util.Color

suspend fun MaterialData.createShader(sceneShaderData: SceneModel.SceneShaderData): DrawShader {
    return shaderData.createShader(sceneShaderData)
}

suspend fun MaterialData.updateShader(shader: DrawShader?, sceneShaderData: SceneModel.SceneShaderData): Boolean {
    return shaderData.updateShader(shader, sceneShaderData)
}

fun MaterialData.matchesShader(shader: DrawShader?): Boolean = shaderData.matchesShader(shader)

suspend fun MaterialShaderData.createShader(sceneShaderData: SceneModel.SceneShaderData): DrawShader = when (this) {
    is PbrShaderData -> createShader(sceneShaderData)
    is BlinnPhongShaderData -> TODO()
    is UnlitShaderData -> TODO()
}

suspend fun MaterialShaderData.updateShader(shader: DrawShader?, sceneShaderData: SceneModel.SceneShaderData): Boolean = when (this) {
    is PbrShaderData -> (shader as? KslPbrShader)?.let { updateShader(it, sceneShaderData) } ?: false
    is BlinnPhongShaderData -> TODO()
    is UnlitShaderData -> TODO()
}

fun MaterialShaderData.matchesShader(shader: DrawShader?): Boolean = when (this) {
    is PbrShaderData -> matchesShader(shader)
    is BlinnPhongShaderData -> TODO()
    is UnlitShaderData -> TODO()
}

suspend fun PbrShaderData.createShader(sceneShaderData: SceneModel.SceneShaderData): KslPbrShader {
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
        this@createShader.aoMap?.let {
            ao {
                materialAo {
                    textureProperty(null, it.singleChannelIndex)
                }
            }
        }
        this@createShader.displacementMap?.let {
            vertices {
                displacement {
                    textureProperty(null, it.singleChannelIndex)
                }
            }
        }
        this@createShader.normalMap?.let {
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

suspend fun PbrShaderData.updateShader(shader: KslPbrShader, sceneShaderData: SceneModel.SceneShaderData): Boolean {
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

fun PbrShaderData.matchesShader(shader: DrawShader?): Boolean {
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
