package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.editor.api.SceneShaderData
import de.fabmax.kool.editor.api.loadTexture2d
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrSplatShader
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Color

suspend fun PbrSplatShaderData.createPbrSplatShader(sceneShaderData: SceneShaderData, modelMats: List<ModelMatrixComposition>): KslPbrSplatShader {
    val shader = KslPbrSplatShader {
        pipeline {
            if (genericSettings.isTwoSided) {
                cullMethod = CullMethod.NO_CULLING
            }
        }
        vertices {
            isInstanced = true
            modelMatrixComposition = modelMats
        }
        lighting {
            maxNumberOfLights = sceneShaderData.maxNumberOfLights
            addShadowMaps(sceneShaderData.shadowMaps)
            sceneShaderData.ssaoMap?.let {
                enableSsao(it)
            }
        }

        val defaultDispTex = SingleColorTexture(Color.GRAY)
        materialMaps.forEachIndexed { i, mat ->
            addMaterial {
                displacement(defaultDispTex)

                color {
                    when (val color = mat.baseColor) {
                        is ConstColorAttribute -> uniformColor()
                        is ConstValueAttribute -> uniformColor()
                        is MapAttribute -> textureColor()
                        is VertexAttribute -> vertexColor(Attribute(color.attribName, GpuType.FLOAT4))
                    }
                }
                emission {
                    when (val color = mat.emission) {
                        is ConstColorAttribute -> uniformColor()
                        is ConstValueAttribute -> uniformColor()
                        is MapAttribute -> textureColor()
                        is VertexAttribute -> vertexColor(Attribute(color.attribName, GpuType.FLOAT4))
                    }
                }
                mat.normalMap?.let {
                    normalMapping { setNormalMap() }
                }

                val armTexNames = PbrArmTexNames.getForConfigs(mat.aoMap, mat.roughness, mat.metallic, "$i")
                mat.aoMap?.let {
                    ao { textureProperty(channel = it.singleChannelIndex, textureName = armTexNames.ao) }
                }
                roughness {
                    when (val rough = mat.roughness) {
                        is ConstColorAttribute -> uniformProperty()
                        is ConstValueAttribute -> uniformProperty()
                        is MapAttribute -> textureProperty(channel = rough.singleChannelIndex, textureName = armTexNames.roughness)
                        is VertexAttribute -> vertexProperty(Attribute(rough.attribName, GpuType.FLOAT1))
                    }
                }
                metallic {
                    when (val metal = mat.metallic) {
                        is ConstColorAttribute -> uniformProperty()
                        is ConstValueAttribute -> uniformProperty()
                        is MapAttribute -> textureProperty(channel = metal.singleChannelIndex, textureName = armTexNames.metallic)
                        is VertexAttribute -> vertexProperty(Attribute(metal.attribName, GpuType.FLOAT1))
                    }
                }
            }
        }

        isWithDebugOptions = debugMode != KslPbrSplatShader.DEBUG_MODE_OFF
        //isContinuousHeight = true
        //isParallax = true

        colorSpaceConversion = ColorSpaceConversion.LinearToSrgbHdr(sceneShaderData.toneMapping)
        sceneShaderData.environmentMaps?.let {
            enableImageBasedLighting(it)
        }
    }
    updatePbrSplatShader(shader, sceneShaderData)
    return shader
}

suspend fun PbrSplatShaderData.updatePbrSplatShader(shader: KslPbrSplatShader, sceneShaderData: SceneShaderData): Boolean {
    if (!matchesPbrSplatShaderConfig(shader)) {
        return false
    }

    val colorConv = shader.cfg.colorSpaceConversion
    if (colorConv is ColorSpaceConversion.LinearToSrgbHdr && colorConv.toneMapping != sceneShaderData.toneMapping) {
        return false
    }

    val ibl = sceneShaderData.environmentMaps
    val isIbl = ibl != null
    val isSsao = sceneShaderData.ssaoMap != null
    val isDebugMode = debugMode != KslPbrSplatShader.DEBUG_MODE_OFF

    when {
        (shader.ambientCfg is KslLitShader.AmbientLight.ImageBased) != isIbl -> return false
        shader.isSsao != isSsao -> return false
        shader.cfg.lightingCfg.maxNumberOfLights != sceneShaderData.maxNumberOfLights -> return false
        shader.shadowMaps != sceneShaderData.shadowMaps -> return false
        shader.cfg.isWithDebugOptions != isDebugMode -> return false
    }

    materialMaps.forEachIndexed { i, mat ->
        val matBinding = shader.materials[i]

        val colorMap = (mat.baseColor as? MapAttribute)?.let { AppAssets.loadTexture2d(it.mapPath) }
        val roughnessMap = (mat.roughness as? MapAttribute)?.let { AppAssets.loadTexture2d(it.mapPath) }
        val metallicMap = (mat.metallic as? MapAttribute)?.let { AppAssets.loadTexture2d(it.mapPath) }
        val emissionMap = (mat.emission as? MapAttribute)?.let { AppAssets.loadTexture2d(it.mapPath) }
        val normalMap = mat.normalMap?.let { AppAssets.loadTexture2d(it.mapPath) }
        val aoMap = mat.aoMap?.let { AppAssets.loadTexture2d(it.mapPath) }
        val displacementMap = mat.displacementMap?.let { AppAssets.loadTexture2d(AssetReference.Texture(it.mapPath, TexFormat.R)) }

        when (val color = mat.baseColor) {
            is ConstColorAttribute -> matBinding.color = color.color.toColorLinear()
            is ConstValueAttribute -> matBinding.color = Color(color.value, color.value, color.value)
            is MapAttribute -> matBinding.colorMap = colorMap
            is VertexAttribute -> { }
        }
        when (val color = mat.emission) {
            is ConstColorAttribute -> matBinding.emission = color.color.toColorLinear()
            is ConstValueAttribute -> matBinding.emission = Color(color.value, color.value, color.value)
            is MapAttribute -> matBinding.emissionMap = emissionMap
            is VertexAttribute -> { }
        }
        when (val rough = mat.roughness) {
            is ConstColorAttribute -> matBinding.roughness = rough.color.r
            is ConstValueAttribute -> matBinding.roughness = rough.value
            is MapAttribute -> matBinding.roughnessMap = roughnessMap
            is VertexAttribute -> { }
        }
        when (val metal = mat.metallic) {
            is ConstColorAttribute -> matBinding.metallic = metal.color.r
            is ConstValueAttribute -> matBinding.metallic = metal.value
            is MapAttribute -> matBinding.metallicMap = metallicMap
            is VertexAttribute -> { }
        }
        displacementMap?.let { matBinding.displacementMap = it }
        normalMap?.let { matBinding.normalMap = it }
        aoMap?.let { matBinding.aoMap = it }
        roughnessMap?.let { matBinding.roughnessMap = it }
        metallicMap?.let { matBinding.metallicMap = it }

        matBinding.textureScale = mat.textureScale
        matBinding.textureRotation = mat.textureRotation.deg
        matBinding.tileSize = mat.stochasticTileSize
        matBinding.tileRotation = mat.stochasticRotation.deg

        shader.debugMode = debugMode
    }

    shader.splatMap = splatMap?.let { AppAssets.loadTexture2d(it.mapPath) } ?: SingleColorTexture(Color.BLACK)

    if (ibl != null) {
        shader.ambientFactor = Color.WHITE
        shader.ambientMap = ibl.irradianceMap
        shader.reflectionMap = ibl.reflectionMap
    } else {
        shader.ambientFactor = sceneShaderData.ambientColorLinear
    }

    return true
}

fun PbrSplatShaderData.matchesPbrSplatShaderConfig(shader: DrawShader?): Boolean {
    if (shader !is KslPbrSplatShader) return false

    if (!genericSettings.matchesPipelineConfig(shader.pipelineConfig)) return false
    if (shader.materials.size != materialMaps.size) return false

    materialMaps.forEachIndexed { i, mat ->
        val shaderMat = shader.cfg.materials[i]
        val matOk = mat.baseColor.matchesCfg(shaderMat.colorCfg)
                && mat.roughness.matchesCfg(shaderMat.roughnessCfg)
                && mat.metallic.matchesCfg(shaderMat.metallicCfg)
                && mat.emission.matchesCfg(shaderMat.emissionCfg)
                && mat.aoMap?.matchesCfg(shaderMat.aoCfg) != false
                && shaderMat.normalMapCfg.isNormalMapped == (mat.normalMap != null)
        if (!matOk) {
            return false
        }
    }
    return true
}