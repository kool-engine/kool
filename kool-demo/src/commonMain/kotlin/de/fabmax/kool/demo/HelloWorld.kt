package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.defaultCamTransform
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.gltf.GltfFile
import de.fabmax.kool.util.gltf.loadGltfModel


fun helloWorldScene(): Scene = scene {
    defaultCamTransform()

    +colorMesh {
        generate {
            cube {
                colored()
                centered()
            }
        }
        shader = pbrShader {
            albedoSource = Albedo.VERTEX_ALBEDO
            metallic = 0.0f
            roughness = 0.25f
        }
    }

    lighting.singleLight {
        setDirectional(Vec3f(-1f, -1f, -1f))
        setColor(Color.WHITE, 5f)
    }
}

fun helloGltfScene(ctx: KoolContext): Scene = scene {
    defaultCamTransform()

    lighting.singleLight {
        setSpot(Vec3f(5f, 6.25f, 7.5f), Vec3f(-1f, -1.25f, -1.5f), 45f)
        setColor(Color.WHITE, 300f)
    }
    val shadows = listOf(SimpleShadowMap(this, lightIndex = 0))
    val aoPipeline = AoPipeline.createForward(this)

    +colorMesh {
        generate {
            grid {  }
        }
        shader = pbrShader {
            useStaticAlbedo(Color.WHITE)
            useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
            shadowMaps += shadows
        }
    }

    ctx.assetMgr.launch {
        val materialCfg = GltfFile.ModelMaterialConfig(
                shadowMaps = shadows,
                scrSpcAmbientOcclusionMap = aoPipeline.aoMap
        )
        val modelCfg = GltfFile.ModelGenerateConfig(materialConfig = materialCfg)
        loadGltfModel("${Demo.modelBasePath}/BoxAnimated.gltf", modelCfg)?.let { model ->
            +model
            model.translate(0f, 0.5f, 0f)

            if (model.animations.isNotEmpty()) {
                model.enableAnimation(0)
                model.onUpdate += { _, ctx ->
                    model.applyAnimation(ctx.time)
                }
            }
        }
    }
}