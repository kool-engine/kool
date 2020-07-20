package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.scene.textureMesh

class PbrLightingPass(scene: Scene, val mrtPass: DeferredMrtPass, cfg: PbrSceneShader.DeferredPbrConfig = PbrSceneShader.DeferredPbrConfig()) :
        OffscreenRenderPass2d(Group(), mrtPass.texWidth, mrtPass.texHeight, pbrLightingSetup(mrtPass)) {

    val dynamicPointLights: DeferredPointLights = DeferredPointLights(mrtPass)
    val staticPointLights: DeferredPointLights = DeferredPointLights(mrtPass)

    private val mutSpotLights = mutableListOf<DeferredSpotLights>()
    val spotLights: List<DeferredSpotLights>
        get() = mutSpotLights

    val content = drawNode as Group

    val sceneShader: PbrSceneShader
    val prevColorTex = Texture("PbrLightingPass.prevColorTex", TextureProps(TexFormat.RGBA_F16, mipMapping = false, maxAnisotropy = 1))

    init {
        dynamicPointLights.isDynamic = true
        staticPointLights.isDynamic = false

        lighting = scene.lighting

        scene.onRenderScene += { ctx ->
            val mapW = mainRenderPass.viewport.width
            val mapH = mainRenderPass.viewport.height
            if (mapW > 0 && mapH > 0 && (mapW != texWidth || mapH != texHeight)) {
                mrtPass.resize(mapW, mapH, ctx)
                resize(mapW, mapH, ctx)
            }
        }
        scene.addOffscreenPass(this)
        dependsOn(mrtPass)

        clearColor = clearColor?.toLinear()
        clearDepth = false
        camera = mrtPass.camera

        cfg.useMrtPass(mrtPass)
        if (cfg.isScrSpcReflections) {
            cfg.useScreenSpaceReflections(prevColorTex, true)
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

        copyTargetsColor += prevColorTex
    }

    override fun afterCollectDrawCommands(ctx: KoolContext) {
        for (i in mrtPass.alphaMeshes.indices) {
            drawQueue.addMesh(mrtPass.alphaMeshes[i], ctx)
        }
        super.afterCollectDrawCommands(ctx)
    }

    fun addSpotLights(maxSpotAngle: Float): DeferredSpotLights {
        val lights = DeferredSpotLights(maxSpotAngle, mrtPass)
        content += lights.mesh
        mutSpotLights += lights
        return lights
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        sceneShader.scrSpcReflectionNoise?.dispose()
        super.dispose(ctx)
    }

    fun createOutputQuad() = textureMesh {
        isFrustumChecked = false
        generate {
            rect {
                mirrorTexCoordsY()
            }
        }
        shader = DeferredOutputShader(colorTexture, mrtPass.depthTexture)
    }

    companion object {
        private fun pbrLightingSetup(mrtPass: DeferredMrtPass) = Setup().apply {
            colorFormat = TexFormat.RGBA_F16
            colorRenderTarget = RENDER_TARGET_TEXTURE
            depthRenderTarget = RENDER_TARGET_TEXTURE

            extDepthTexture = mrtPass.depthTexture
        }
    }
}
