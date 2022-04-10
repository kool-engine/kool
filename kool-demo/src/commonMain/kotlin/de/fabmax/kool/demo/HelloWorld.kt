package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.defaultCamTransform
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.SimpleShadowMap

class HelloWorldDemo : DemoScene("Hello World") {
    override fun Scene.setupMainScene(ctx: KoolContext) = helloWorldScene()
}

class HelloGltfDemo : DemoScene("Hello glTF") {
    override fun Scene.setupMainScene(ctx: KoolContext) = helloGltfScene(ctx)
}

fun Scene.helloWorldScene() {
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

fun Scene.helloGltfScene(ctx: KoolContext) {
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
        loadGltfModel("${Demo.modelPath}/BoxAnimated.gltf", modelCfg)?.let { model ->
            +model
            model.translate(0f, 0.5f, 0f)

            if (model.animations.isNotEmpty()) {
                model.enableAnimation(0)
                model.onUpdate += { updateEvt ->
                    model.applyAnimation(updateEvt.deltaT)
                }
            }
        }
    }
}