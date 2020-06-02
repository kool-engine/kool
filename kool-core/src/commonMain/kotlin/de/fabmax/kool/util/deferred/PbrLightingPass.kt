package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Lighting
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.mesh

class PbrLightingPass(scene: Scene, val mrtPass: DeferredMrtPass, cfg: PbrSceneShader.DeferredPbrConfig = PbrSceneShader.DeferredPbrConfig()) :
        OffscreenRenderPass2d(Group(), mrtPass.texWidth, mrtPass.texHeight, TexFormat.RGBA_F16) {

    val globalLighting = Lighting()

    val dynamicPointLights: DeferredPointLights = DeferredPointLights(mrtPass)
    val staticPointLights: DeferredPointLights = DeferredPointLights(mrtPass)

    lateinit var sceneShader: PbrSceneShader
        private set

    init {
        dynamicPointLights.isDynamic = true
        staticPointLights.isDynamic = false

        lighting = globalLighting
        globalLighting.lights.clear()
    }

    init {
        scene.onRenderScene += { ctx ->
            val mapW = mainRenderPass.viewport.width
            val mapH = mainRenderPass.viewport.height
            if (mapW > 0 && mapH > 0 && (mapW != texWidth || mapH != texHeight)) {
                resize(mapW, mapH, ctx)
                mrtPass.resize(mapW, mapH, ctx)
            }
        }
        scene.addOffscreenPass(this)

        clearColor = clearColor?.toLinear()
        dependsOn(mrtPass)

        camera = mrtPass.camera

        (drawNode as Group).apply {
            isFrustumChecked = false
            +mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
                isFrustumChecked = false
                generate {
                    rect {
                        size.set(1f, 1f)
                        mirrorTexCoordsY()
                    }
                }

                cfg.apply {
                    if (sceneCamera == null) { sceneCamera = mrtPass.camera }

                    if (positionAo == null) { positionAo = mrtPass.positionAo }
                    if (normalRoughness == null) { normalRoughness = mrtPass.normalRoughness }
                    if (albedoMetal == null) { albedoMetal = mrtPass.albedoMetal }
                }

                sceneShader = PbrSceneShader(cfg)
                pipelineLoader = sceneShader
            }

            +dynamicPointLights.mesh
            +staticPointLights.mesh
        }
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }
}
