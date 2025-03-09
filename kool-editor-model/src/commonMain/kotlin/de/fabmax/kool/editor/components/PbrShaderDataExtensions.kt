package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.*
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logW

suspend fun PbrShaderData.createPbrShader(
    meshLayoutInfo: MeshLayoutInfo,
    modelMats: List<ModelMatrixComposition>,
    sceneShaderData: SceneShaderData
): KslPbrShader {
    val shader = KslPbrShader {
        pipeline {
            if (genericSettings.isTwoSided) {
                cullMethod = CullMethod.NO_CULLING
            }
        }
        vertices {
            isInstanced = true
            modelMatrixComposition = modelMats
            if (meshLayoutInfo.numJoints > 0) {
                enableArmature(meshLayoutInfo.numJoints)
            }
        }
        color {
            when (val color = baseColor) {
                is ConstColorAttribute -> uniformColor()
                is ConstValueAttribute -> uniformColor()
                is MapAttribute -> textureColor()
                is VertexAttribute -> vertexColor(Attribute(color.attribName, GpuType.Float4))
            }
        }
        emission {
            when (val color = emission) {
                is ConstColorAttribute -> uniformColor()
                is ConstValueAttribute -> uniformColor()
                is MapAttribute -> textureColor()
                is VertexAttribute -> vertexColor(Attribute(color.attribName, GpuType.Float4))
            }
        }

        val armTexNames = PbrArmTexNames.getForConfigs(aoMap, roughness, metallic)
        roughness {
            when (val rough = roughness) {
                is ConstColorAttribute -> uniformProperty()
                is ConstValueAttribute -> uniformProperty()
                is MapAttribute -> textureProperty(channel = rough.singleChannelIndex, textureName = armTexNames.roughness)
                is VertexAttribute -> vertexProperty(Attribute(rough.attribName, GpuType.Float1))
            }
        }
        metallic {
            when (val metal = metallic) {
                is ConstColorAttribute -> uniformProperty()
                is ConstValueAttribute -> uniformProperty()
                is MapAttribute -> textureProperty(channel = metal.singleChannelIndex, textureName = armTexNames.metallic)
                is VertexAttribute -> vertexProperty(Attribute(metal.attribName, GpuType.Float1))
            }
        }
        aoMap?.let {
            ao { textureProperty(channel = it.singleChannelIndex, textureName = armTexNames.ao) }
        }
        displacementMap?.let {
            vertices {
                displacement {
                    uniformProperty(parallaxOffset)
                }
            }
            parallaxMapping {
                useParallaxMap(null, parallaxStrength, maxSteps = parallaxSteps, textureChannel = it.singleChannelIndex)
            }
        }
        normalMap?.let {
            normalMapping {
                useNormalMap()
            }
        }

        lighting {
            maxNumberOfLights = sceneShaderData.maxNumberOfLights
            addShadowMaps(sceneShaderData.shadowMaps)
            sceneShaderData.ssaoMap?.let {
                enableSsao(it)
            }
        }
        sceneShaderData.environmentMap?.let {
            enableImageBasedLighting(it)
        }
        colorSpaceConversion = ColorSpaceConversion.LinearToSrgbHdr(sceneShaderData.toneMapping)
    }
    updatePbrShader(shader, sceneShaderData)
    return shader
}

suspend fun PbrShaderData.updatePbrShader(shader: KslPbrShader, sceneShaderData: SceneShaderData): Boolean {
    if (!matchesPbrShaderConfig(shader)) {
        return false
    }

    val colorConv = shader.cfg.colorSpaceConversion
    if (colorConv is ColorSpaceConversion.LinearToSrgbHdr && colorConv.toneMapping != sceneShaderData.toneMapping) {
        return false
    }

    val ibl = sceneShaderData.environmentMap
    val isIbl = ibl != null
    val isSsao = sceneShaderData.ssaoMap != null
    val isMaterialAo = aoMap != null

    when {
        (shader.ambientCfg is KslLitShader.AmbientLight.ImageBased) != isIbl -> return false
        shader.isSsao != isSsao -> return false
        shader.cfg.lightingCfg.maxNumberOfLights != sceneShaderData.maxNumberOfLights -> return false
        shader.shadowMaps != sceneShaderData.shadowMaps -> return false
        (shader.aoCfg.primaryTexture != null) != isMaterialAo -> return false
    }

    val colorMap = (baseColor as? MapAttribute)?.let { AppAssets.loadTexture2dOrNull(it.mapPath) }
    val roughnessMap = (roughness as? MapAttribute)?.let { AppAssets.loadTexture2dOrNull(it.mapPath) }
    val metallicMap = (metallic as? MapAttribute)?.let { AppAssets.loadTexture2dOrNull(it.mapPath) }
    val emissionMap = (emission as? MapAttribute)?.let { AppAssets.loadTexture2dOrNull(it.mapPath) }
    val normalMap = normalMap?.let { AppAssets.loadTexture2dOrNull(it.mapPath) }
    val aoMap = aoMap?.let { AppAssets.loadTexture2dOrNull(it.mapPath) }
    val displacementMap = displacementMap?.let { AppAssets.loadTexture2dOrNull(AssetReference.Texture(it.mapPath, TexFormat.R)) }

    when (val color = baseColor) {
        is ConstColorAttribute -> shader.color = color.color.toColorLinear()
        is ConstValueAttribute -> shader.color = Color(color.value, color.value, color.value)
        is MapAttribute -> shader.colorMap = colorMap
        is VertexAttribute -> { }
    }
    when (val color = emission) {
        is ConstColorAttribute -> shader.emission = color.color.toColorLinear()
        is ConstValueAttribute -> shader.emission = Color(color.value, color.value, color.value)
        is MapAttribute -> shader.emissionMap = emissionMap
        is VertexAttribute -> { }
    }
    when (val rough = roughness) {
        is ConstColorAttribute -> shader.roughness = rough.color.r
        is ConstValueAttribute -> shader.roughness = rough.value
        is MapAttribute -> shader.roughnessMap = roughnessMap
        is VertexAttribute -> { }
    }
    when (val metal = metallic) {
        is ConstColorAttribute -> shader.metallic = metal.color.r
        is ConstValueAttribute -> shader.metallic = metal.value
        is MapAttribute -> shader.metallicMap = metallicMap
        is VertexAttribute -> { }
    }
    shader.normalMap = normalMap
    shader.aoMap = aoMap
    shader.parallaxMap = displacementMap
    shader.parallaxMapSteps = parallaxSteps
    shader.parallaxStrength = parallaxStrength
    shader.vertexDisplacementStrength = parallaxOffset

    if (ibl != null) {
        shader.ambientFactor = Color.WHITE
        shader.ambientMap = ibl.irradianceMap
        shader.reflectionMap = ibl.reflectionMap
    } else {
        shader.ambientFactor = sceneShaderData.ambientColorLinear
    }
    return true
}

fun PbrShaderData.matchesPbrShaderConfig(shader: DrawShader?): Boolean {
    if (shader !is KslPbrShader) {
        return false
    }
    return baseColor.matchesCfg(shader.colorCfg)
            && roughness.matchesCfg(shader.roughnessCfg)
            && metallic.matchesCfg(shader.metallicCfg)
            && emission.matchesCfg(shader.emissionCfg)
            && aoMap?.matchesCfg(shader.aoCfg) != false
            && shader.isParallaxMapped == (displacementMap != null)
            && shader.isNormalMapped == (normalMap != null)
            && genericSettings.matchesPipelineConfig(shader.pipelineConfig)
}

data class PbrArmTexNames(val ao: String, val roughness: String, val metallic: String, val aoIndex: Int, val roughnessIndex: Int, val metallicIndex: Int) {
    companion object {
        fun getForConfigs(aoCfg: MapAttribute?, roughCfg: MaterialAttribute, metalCfg: MaterialAttribute, postfix: String = ""): PbrArmTexNames {
            val texNames = mutableMapOf<String, Pair<String, Int>>()

            var mapIdx = 0
            val ao = aoCfg?.let { texNames.getOrPut(it.mapPath) { "tAo$postfix" to mapIdx++ } }
            val roughness = if (roughCfg !is MapAttribute) null else {
                texNames.getOrPut(roughCfg.mapPath) { "tRoughness$postfix" to mapIdx++ }
            }
            val metallic = if (metalCfg !is MapAttribute) null else {
                texNames.getOrPut(metalCfg.mapPath) { "tMetal$postfix" to mapIdx++ }
            }

            if (ao != null && ao == roughness && aoCfg.channels == (roughCfg as MapAttribute).channels) {
                logW { "AO and roughness attribute use the same texture channel, which is most-likely a mistake, texture: ${aoCfg.mapPath}" }
            }
            if (ao != null && ao == metallic && aoCfg.channels == (metalCfg as MapAttribute).channels) {
                logW { "AO and roughness attribute use the same texture channel, which is most-likely a mistake, texture: ${aoCfg.mapPath}" }
            }
            if (roughness != null && roughness == metallic && (roughCfg as MapAttribute).channels == (metalCfg as MapAttribute).channels) {
                logW { "Roughness and metallic attribute use the same texture channel, which is most-likely a mistake, texture: ${roughCfg.mapPath}" }
            }

            return PbrArmTexNames(
                ao = ao?.first ?: "tAo$postfix",
                roughness = roughness?.first ?: "tRoughness$postfix",
                metallic = metallic?.first ?: "tMetallic$postfix",
                aoIndex = ao?.second ?: -1,
                roughnessIndex = roughness?.second ?: -1,
                metallicIndex = metallic?.second ?: -1,
            )
        }
    }
}
