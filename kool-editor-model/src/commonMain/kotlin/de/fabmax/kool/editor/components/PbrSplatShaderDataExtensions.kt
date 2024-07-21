package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.SceneShaderData
import de.fabmax.kool.editor.api.loadTexture2d
import de.fabmax.kool.editor.data.PbrSplatShaderData
import de.fabmax.kool.modules.ksl.KslPbrSplatShader
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.DrawShader

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

        val color1 = AppAssets.loadTexture2d("/textures/materials/brown_mud_leaves_01/brown_mud_leaves_01_diff_2k.jpg")
        val disp1 = AppAssets.loadTexture2d("/textures/materials/brown_mud_leaves_01/brown_mud_leaves_01_disp_2k.jpg")
        val normal1 = AppAssets.loadTexture2d("/textures/materials/brown_mud_leaves_01/brown_mud_leaves_01_nor_gl_2k.jpg")
        val arm1 = AppAssets.loadTexture2d("/textures/materials/brown_mud_leaves_01/brown_mud_leaves_01_arm_2k.jpg")

        val color2 = AppAssets.loadTexture2d("/textures/materials/lichen_rock_2k/lichen_rock_diff_2k.jpg")
        val disp2 = AppAssets.loadTexture2d("/textures/materials/lichen_rock_2k/lichen_rock_disp_2k.jpg")
        val normal2 = AppAssets.loadTexture2d("/textures/materials/lichen_rock_2k/lichen_rock_nor_gl_2k.jpg")
        val arm2 = AppAssets.loadTexture2d("/textures/materials/lichen_rock_2k/lichen_rock_arm_2k.jpg")

        addSplatMaterial {
            displacement(disp1)
            color { textureColor(color1) }
            normalMap(normal1)
            ao { textureProperty(arm1, 0, "arm1") }
            roughness { textureProperty(arm1, 1, "arm1") }
            metallic { textureProperty(arm1, 2, "arm1") }
            uvScale = 150f
        }
        addSplatMaterial {
            displacement(disp2)
            color { textureColor(color2) }
            normalMap(normal2)
            ao { textureProperty(arm2, 0, "arm2") }
            roughness { textureProperty(arm2, 1, "arm2") }
            metallic { textureProperty(arm2, 2, "arm2") }
            uvScale = 75f
            //stochasticTileSize = 1f
            //stochasticTileRotation = 0f.deg
        }

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
    shader.splatMap = splatMap?.let { AppAssets.loadTexture2d(it.mapPath) }
    return true
}

fun PbrSplatShaderData.matchesPbrSplatShaderConfig(shader: DrawShader?): Boolean {
    return shader is KslPbrSplatShader
}