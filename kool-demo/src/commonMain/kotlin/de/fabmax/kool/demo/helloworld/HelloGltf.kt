package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.GltfMaterialConfig
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addColorMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.launchOnMainThread

class HelloGltf : DemoScene("Hello glTF") {
    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera()

        // Light setup
        lighting.singleSpotLight {
            setup(Vec3f(5f, 6.25f, 7.5f), Vec3f(-1f, -1.25f, -1.5f), 45f.deg)
            setColor(Color.WHITE, 300f)
        }
        val shadowMap = SimpleShadowMap(this, lighting.lights[0])
        val aoPipeline = AoPipeline.createForward(this)

        // Add a ground plane
        addColorMesh {
            generate {
                grid { }
            }
            shader = KslPbrShader {
                color { constColor(Color.WHITE) }
                lighting { addShadowMap(shadowMap) }
                enableSsao(aoPipeline.aoMap)
            }
        }

        // Load a glTF 2.0 model
        launchOnMainThread {
            val materialCfg = GltfMaterialConfig(
                shadowMaps = listOf(shadowMap),
                scrSpcAmbientOcclusionMap = aoPipeline.aoMap
            )
            val modelCfg = GltfLoadConfig(materialConfig = materialCfg)
            val model = Assets.loadGltfModel("${DemoLoader.modelPath}/BoxAnimated.gltf", modelCfg)

            model.transform.translate(0f, 0.5f, 0f)
            if (model.animations.isNotEmpty()) {
                model.enableAnimation(0)
                model.onUpdate {
                    model.applyAnimation(Time.deltaT)
                }
            }

            // Add loaded model to scene
            addNode(model)
        }
    }
}