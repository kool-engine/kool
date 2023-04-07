package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.Time

class HelloGltfDemo : DemoScene("Hello glTF") {
    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera()

        // Light setup
        lighting.singleLight {
            setSpot(Vec3f(5f, 6.25f, 7.5f), Vec3f(-1f, -1.25f, -1.5f), 45f)
            setColor(Color.WHITE, 300f)
        }
        val shadowMap = SimpleShadowMap(this, lightIndex = 0)
        val aoPipeline = AoPipeline.createForward(this)

        // Add a ground plane
        colorMesh {
            generate {
                grid { }
            }
            shader = KslPbrShader {
                color { constColor(Color.WHITE) }
                shadow { addShadowMap(shadowMap) }
                enableSsao(aoPipeline.aoMap)
            }
        }

        // Load a glTF 2.0 model
        ctx.assetMgr.launch {
            val materialCfg = GltfFile.ModelMaterialConfig(
                shadowMaps = listOf(shadowMap),
                scrSpcAmbientOcclusionMap = aoPipeline.aoMap
            )
            val modelCfg = GltfFile.ModelGenerateConfig(materialConfig = materialCfg)
            loadGltfModel("${DemoLoader.modelPath}/BoxAnimated.gltf", modelCfg)?.let { model ->
                addNode(model)
                model.transform.translate(0f, 0.5f, 0f)

                if (model.animations.isNotEmpty()) {
                    model.enableAnimation(0)
                    model.onUpdate {
                        model.applyAnimation(Time.deltaT)
                    }
                }
            }
        }
    }
}