package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.*
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrSplatShader
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW

suspend fun PbrSplatShaderData.createPbrSplatShader(
    meshLayoutInfo: MeshLayoutInfo,
    modelMats: List<ModelMatrixComposition>,
    sceneShaderData: SceneShaderData
): KslPbrSplatShader {
    val shader = KslPbrSplatShader {
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
        lighting {
            maxNumberOfLights = sceneShaderData.maxNumberOfLights
            addShadowMaps(sceneShaderData.shadowMaps)
            sceneShaderData.ssaoMap?.let {
                enableSsao(it)
            }
        }

        val arrayTexName = "pbr_material_maps"
        var arrayIndex = 0
        materialMaps.forEach { mat ->
            addMaterial {
                color {
                    when (val color = mat.baseColor) {
                        is ConstColorAttribute -> uniformColor()
                        is ConstValueAttribute -> uniformColor()
                        is MapAttribute -> textureColor(arrayIndex++, arrayTexName)
                        is VertexAttribute -> vertexColor(Attribute(color.attribName, GpuType.Float4))
                    }
                }
                emission {
                    when (val color = mat.emission) {
                        is ConstColorAttribute -> uniformColor()
                        is ConstValueAttribute -> uniformColor()
                        is MapAttribute -> textureColor(arrayIndex++, arrayTexName)
                        is VertexAttribute -> vertexColor(Attribute(color.attribName, GpuType.Float4))
                    }
                }
                mat.normalMap?.let {
                    normalMapping { useNormalMapFromArray(arrayIndex++, arrayTexName) }
                }

                val armTexNames = PbrArmTexNames.getForConfigs(mat.aoMap, mat.roughness, mat.metallic)
                mat.aoMap?.let {
                    ao {
                        textureProperty(
                            arrayIndex = arrayIndex + armTexNames.aoIndex,
                            textureName = arrayTexName,
                            channel = it.singleChannelIndex
                        )
                    }
                }
                roughness {
                    when (val rough = mat.roughness) {
                        is ConstColorAttribute -> uniformProperty()
                        is ConstValueAttribute -> uniformProperty()
                        is VertexAttribute -> vertexProperty(Attribute(rough.attribName, GpuType.Float1))
                        is MapAttribute -> textureProperty(
                            arrayIndex = arrayIndex + armTexNames.roughnessIndex,
                            textureName = arrayTexName,
                            channel = rough.singleChannelIndex
                        )
                    }
                }
                metallic {
                    when (val metal = mat.metallic) {
                        is ConstColorAttribute -> uniformProperty()
                        is ConstValueAttribute -> uniformProperty()
                        is VertexAttribute -> vertexProperty(Attribute(metal.attribName, GpuType.Float1))
                        is MapAttribute -> textureProperty(
                            arrayIndex = arrayIndex + armTexNames.roughnessIndex,
                            textureName = arrayTexName,
                            channel = metal.singleChannelIndex
                        )
                    }
                }
                arrayIndex += maxOf(armTexNames.aoIndex, armTexNames.roughnessIndex, armTexNames.metallicIndex) + 1
            }
        }

        isWithDebugOptions = debugMode != KslPbrSplatShader.DEBUG_MODE_OFF
        //isContinuousHeight = true
        //isParallax = true

        colorSpaceConversion = ColorSpaceConversion.LinearToSrgbHdr(sceneShaderData.toneMapping)
        sceneShaderData.environmentMap?.let {
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

    val ibl = sceneShaderData.environmentMap
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

    val mapPaths = mutableListOf<String>()
    materialMaps.forEachIndexed { i, mat ->
        val matBinding = shader.materials[i]

        // collect texture paths, order matters as it will determine the resulting index in the array texture
        (mat.baseColor as? MapAttribute)?.let { mapPaths += it.mapPath }
        (mat.emission as? MapAttribute)?.let { mapPaths += it.mapPath }
        mat.normalMap?.let { mapPaths += it.mapPath }

        val armTexNames = PbrArmTexNames.getForConfigs(mat.aoMap, mat.roughness, mat.metallic)
        mat.aoMap?.let { mapPaths += it.mapPath }
        if (armTexNames.roughnessIndex != armTexNames.aoIndex) {
            (mat.roughness as? MapAttribute)?.let { mapPaths += it.mapPath }
        }
        if (armTexNames.metallicIndex != armTexNames.aoIndex && armTexNames.metallicIndex != armTexNames.roughnessIndex) {
            (mat.metallic as? MapAttribute)?.let { mapPaths += it.mapPath }
        }

        when (val color = mat.baseColor) {
            is ConstColorAttribute -> matBinding.color = color.color.toColorLinear()
            is ConstValueAttribute -> matBinding.color = Color(color.value, color.value, color.value)
            else -> { }
        }
        when (val color = mat.emission) {
            is ConstColorAttribute -> matBinding.emission = color.color.toColorLinear()
            is ConstValueAttribute -> matBinding.emission = Color(color.value, color.value, color.value)
            else -> { }
        }
        when (val rough = mat.roughness) {
            is ConstColorAttribute -> matBinding.roughness = rough.color.r
            is ConstValueAttribute -> matBinding.roughness = rough.value
            else -> { }
        }
        when (val metal = mat.metallic) {
            is ConstColorAttribute -> matBinding.metallic = metal.color.r
            is ConstValueAttribute -> matBinding.metallic = metal.value
            else -> { }
        }

        matBinding.textureScale = mat.textureScale
        matBinding.textureRotation = mat.textureRotation.deg
        matBinding.tileSize = mat.stochasticTileSize
        matBinding.tileRotation = mat.stochasticRotation.deg
    }

    shader.debugMode = debugMode
    shader.splatMap = splatMap?.let { AppAssets.loadTexture2dOrNull(it.mapPath) } ?: SingleColorTexture(Color.BLACK)

    val oldMatMaps = shader.textureArrays["pbr_material_maps"]?.get()
    val oldDispMaps = shader.textureArrays[KslPbrSplatShader.DISPLACEMENTS_TEX_NAME]?.get()

    val mapArray = AppAssets.loadTexture2dArray(AssetReference.TextureArray(mapPaths))
    if (mapArray.isFailure) {
        logE { "Failed loading splat material maps: ${mapArray.exceptionOrNull()}" }
    }
    val newMatMaps = mapArray.getOrNull()

    val dispMaps = materialMaps.map { it.displacementMap }
    val nonNullDisp = dispMaps.find { it != null }
    var newDispMaps = nonNullDisp?.let {
        if (dispMaps.any { it == null }) {
            logW { "PbrSplatShaderData contains materials without displacement map, material blending won't work as expected" }
        }
        val nonNullDispMaps = dispMaps.mapNotNull { it ?: nonNullDisp }
        if (nonNullDispMaps.any { it.channels != null && it.singleChannelIndex != 0 }) {
            logE { "PbrSplatShaderData contains displacement maps, with invalid channels. Displacement maps must use the first (red) channel" }
        }
        AppAssets.loadTexture2dArray(AssetReference.TextureArray(nonNullDispMaps.map { it.mapPath }, TexFormat.R)).getOrNull()
    }
    if (newDispMaps == null) {
        logW { "PbrSplatShaderData contains no displacement maps, material blending won't work as expected" }
        val fakeDisps = ImageData2dArray(materialMaps.map { BufferedImageData2d.singleColor(Color.GRAY) })
        newDispMaps = Texture2dArray(fakeDisps)
    }

    // fixme: releasing the old maps just like that is very hacky, it should be done via AppAssets
    //  also old textures do not get released if the shader is recreated instead of just updated
    if (newMatMaps != oldMatMaps) oldMatMaps?.release()
    if (newDispMaps != oldDispMaps) oldDispMaps?.release()

    shader.textureArrays["pbr_material_maps"]?.set(newMatMaps)
    shader.textureArrays[KslPbrSplatShader.DISPLACEMENTS_TEX_NAME]?.set(newDispMaps)


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