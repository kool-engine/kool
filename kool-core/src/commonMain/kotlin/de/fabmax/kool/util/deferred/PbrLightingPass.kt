package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.mesh

class PbrLightingPass(scene: Scene, val mrtPass: DeferredMrtPass, cfg: PbrSceneShader.DeferredPbrConfig = PbrSceneShader.DeferredPbrConfig()) :
        OffscreenRenderPass2d(Group(), mrtPass.texWidth, mrtPass.texHeight, TexFormat.RGBA_F16) {

    val dynamicPointLights: DeferredPointLights = DeferredPointLights(mrtPass)
    val staticPointLights: DeferredPointLights = DeferredPointLights(mrtPass)

    init {
        dynamicPointLights.isDynamic = true
        staticPointLights.isDynamic = false
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
        lighting = scene.lighting

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

                pipelineLoader = PbrSceneShader(cfg)
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
