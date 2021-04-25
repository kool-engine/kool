package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.renderPassConfig
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.Color

class PbrLightingPass(scene: Scene, val mrtPass: DeferredMrtPass, val sceneShader: PbrSceneShader) :
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

    init {
        dynamicPointLights.isDynamic = true
        staticPointLights.isDynamic = false

        lighting = scene.lighting
        clearColor = Color(0f, 0f, 0f, 0f)
        camera = mrtPass.camera

        scene.addOffscreenPass(this)

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
}
