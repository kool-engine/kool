package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.renderPassConfig
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.scene.textureMesh
import kotlin.math.roundToInt

class PbrLightingPass(scene: Scene, val mrtPass: DeferredMrtPass, cfg: PbrSceneShader.DeferredPbrConfig = PbrSceneShader.DeferredPbrConfig()) :
        OffscreenRenderPass2d(Group(), renderPassConfig {
            name = "PbrLightingPass"
            setSize(mrtPass.config.width, mrtPass.config.height)
            addColorTexture(TexFormat.RGBA_F16)
            clearDepthTexture()
        }) {

    val dynamicPointLights: DeferredPointLights = DeferredPointLights(mrtPass)
    val staticPointLights: DeferredPointLights = DeferredPointLights(mrtPass)

    private val mutSpotLights = mutableListOf<DeferredSpotLights>()
    val spotLights: List<DeferredSpotLights>
        get() = mutSpotLights

    val content = drawNode as Group

    val sceneShader: PbrSceneShader

    var reflectionMapSize = 0.5f
    val reflectionPass: ReflectionPass?
    val reflectionDenoisePass: ReflectionDenoisePass?

    init {
        dynamicPointLights.isDynamic = true
        staticPointLights.isDynamic = false

        lighting = scene.lighting
        clearColor = null
        camera = mrtPass.camera

        scene.addOffscreenPass(this)
        cfg.useMrtPass(mrtPass)
        if (cfg.isScrSpcReflections) {
            reflectionPass = ReflectionPass(mrtPass, this)
            reflectionDenoisePass = ReflectionDenoisePass(reflectionPass, mrtPass.positionAo)
            scene.addOffscreenPass(reflectionPass)
            scene.addOffscreenPass(reflectionDenoisePass)
            cfg.useScreenSpaceReflections(reflectionDenoisePass.colorTexture)
        } else {
            reflectionPass = null
            reflectionDenoisePass = null
        }
        sceneShader = PbrSceneShader(cfg)

        content.apply {
            isFrustumChecked = false
            +mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
                isFrustumChecked = false
                generate {
                    rect {
                        size.set(1f, 1f)
                        mirrorTexCoordsY()
                    }
                }
                shader = sceneShader
            }

            +dynamicPointLights.mesh
            +staticPointLights.mesh
        }

        dependsOn(mrtPass)

        scene.mainRenderPass.onAfterCollectDrawCommands += { ctx ->
            if (isEnabled) {
                for (i in mrtPass.alphaMeshes.indices) {
                    scene.mainRenderPass.drawQueue.addMesh(mrtPass.alphaMeshes[i], ctx)
                }
            }
        }
        scene.onRenderScene += { ctx ->
            val mapW = mainRenderPass.viewport.width
            val mapH = mainRenderPass.viewport.height
            if (mapW > 0 && mapH > 0 && (mapW != width || mapH != height)) {
                resize(mapW, mapH, ctx)
                mrtPass.resize(mapW, mapH, ctx)
            }

            reflectionPass?.let { rp ->
                val reflMapW = (mapW * reflectionMapSize).roundToInt()
                val reflMapH = (mapH * reflectionMapSize).roundToInt()
                if (reflMapW > 0 && reflMapH > 0 && (reflMapW != rp.width || reflMapH != rp.height)) {
                    rp.resize(reflMapW, reflMapH, ctx)
                    reflectionDenoisePass?.resize(reflMapW, reflMapH, ctx)
                }
            }
        }
    }

    fun addSpotLights(maxSpotAngle: Float): DeferredSpotLights {
        val lights = DeferredSpotLights(maxSpotAngle, mrtPass)
        content += lights.mesh
        mutSpotLights += lights
        return lights
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    fun createOutputQuad() = textureMesh {
        isFrustumChecked = false
        generate {
            rect {
                mirrorTexCoordsY()
            }
        }
        shader = DeferredOutputShader(colorTexture!!, mrtPass.depthTexture!!)
    }
}
