package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.pipeline.renderPassConfig
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.Time

class HelloWorldDemo : DemoScene("Hello World") {
    override fun Scene.setupMainScene(ctx: KoolContext) = helloWorldScene()
}
class HelloRenderToTextureDemo : DemoScene("Hello RenderToTexture") {
    override fun Scene.setupMainScene(ctx: KoolContext) = helloRenderToTexture()
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

fun Scene.helloRenderToTexture() {
    // create offscreen content
    val backgroundGroup = group {
        +colorMesh {
            generate {
                cube {
                    colored()
                    centered()
                }
            }
            shader = KslUnlitShader { color { vertexColor() } }
        }
        onUpdate += { rotate(it.deltaT * 30f, Vec3f.Y_AXIS) }
    }

    // setup offscreen pass
    val off = OffscreenRenderPass2d(backgroundGroup, renderPassConfig {
        width = 512
        height = 512
        addColorTexture {
            colorFormat = TexFormat.RGBA
        }
    }).apply {
        clearColor = Color.BLACK
        camera.position.set(0f, 1f, 2f)
    }

    // create main scene and draw offscreen result on a quad
    defaultCamTransform()
    addOffscreenPass(off)

    +textureMesh {
        generate {
            rect {
                size.set(2f, 2f)
                origin.set(-1f, -1f, 0f)
            }
        }
        shader = KslUnlitShader {
            color { textureColor(off.colorTexture) }
            pipeline { cullMethod = CullMethod.NO_CULLING }
        }
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
        loadGltfModel("${DemoLoader.modelPath}/BoxAnimated.gltf", modelCfg)?.let { model ->
            +model
            model.translate(0f, 0.5f, 0f)

            if (model.animations.isNotEmpty()) {
                model.enableAnimation(0)
                model.onUpdate += {
                    model.applyAnimation(Time.deltaT)
                }
            }
        }
    }
}