package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.smoothStep
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.UniformBufferMvp
import de.fabmax.kool.pipeline.shadermodel.UnlitMaterialNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.AlphaModeBlend
import de.fabmax.kool.pipeline.shading.UnlitMaterialConfig
import de.fabmax.kool.pipeline.shading.UnlitShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color

class SkyPass(val atmosphereDemo: AtmosphereDemo) :
        OffscreenRenderPass2d(Group(), renderPassConfig {
            name = "SkyPass"
            setDynamicSize()
            addColorTexture {
                colorFormat = TexFormat.RGB_F16
                minFilter = FilterMethod.NEAREST
                magFilter = FilterMethod.NEAREST
            }
            clearDepthTexture()
        }) {

    val content = drawNode as Group

    init {
        val scene = atmosphereDemo.mainScene
        val proxyCamera = PerspectiveCamera.Proxy(scene.camera as PerspectiveCamera)
        camera = proxyCamera
        onBeforeCollectDrawCommands += { ctx ->
            proxyCamera.sync(scene.mainRenderPass.viewport, ctx)
        }

        lighting = scene.lighting

        scene.addOffscreenPass(this)
        //clearColor = Color.RED

        scene.onRenderScene += { ctx ->
            val vpW = mainRenderPass.viewport.width
            val vpH = mainRenderPass.viewport.height
            if (vpW > 0 && vpH > 0 && (vpW != width || vpH != height)) {
                resize(vpW, vpH, ctx)
            }
        }

        content.apply {
            isFrustumChecked = false
            setupSky()
        }
    }

    private fun Group.setupSky() {
        val textures = atmosphereDemo.textures
        var starShader: UnlitShader? = null
        var sunBgShader: UnlitShader? = null

        val stars = textureMesh {
            isFrustumChecked = false
            generate {
                vertexModFun = {
                    texCoord.x = 1f - texCoord.x
                }
                uvSphere {
                    steps = 10
                }
            }
            starShader = skyboxShader(textures[AtmosphereDemo.texMilkyway])
            shader = starShader
        }
        val sunBg = textureMesh {
            isFrustumChecked = false
            generate {
                rect {
                    size.set(3f, 3f)
                    origin.set(size.x, size.y, 0f).scale(-0.5f)
                    origin.z = -10f
                }
            }
            sunBgShader = skyboxShader(textures[AtmosphereDemo.texSunBg])
            shader = sunBgShader
        }
        val sun = textureMesh {
            isFrustumChecked = false
            generate {
                rect {
                    size.set(1f, 1f)
                    origin.set(size.x, size.y, 0f).scale(-0.5f)
                    origin.z = -10f
                }

            }
            shader = skyboxShader(textures[AtmosphereDemo.texSun])
        }

        +group {
            isFrustumChecked = false
            +stars
            // milky way is wildly tilted (no idea in which direction...)
            rotate(-60f, Vec3f.X_AXIS)

            onUpdate += {
                val atmoThickness = atmosphereDemo.atmoShader.atmosphereRadius - atmosphereDemo.atmoShader.surfaceRadius
                val skyHeightAlpha = ((atmosphereDemo.cameraHeight / atmoThickness).clamp(0.2f, 1f) - 0.2f) * (1f / 0.8f)

                val posNormal = MutableVec3f(atmosphereDemo.mainScene.camera.globalPos).norm()
                val dayNightAlpha = smoothStep(0.1f, 0.3f, posNormal * atmosphereDemo.sun.direction)

                val w = 1f - ((1f - skyHeightAlpha) * (1f - dayNightAlpha))
                val mulColor = Color(w, w, w, 1f)
                starShader!!.color = mulColor
                sunBgShader!!.color = mulColor.withAlpha(0.75f)
            }
        }
        +sunBg
        +sun

        +atmosphereDemo.moonGroup
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    private fun skyboxShader(texture: Texture?): UnlitShader {
        val unlitCfg = UnlitMaterialConfig().apply {
            alphaMode = AlphaModeBlend()
            useColorMap(texture, true)
            color = Color.WHITE
        }
        val unlitModel = UnlitShader.defaultUnlitModel(unlitCfg).apply {
            vertexStage {
                val mvp = findNodeByType<UniformBufferMvp>()!!
                positionOutput = addNode(Skybox.SkyboxPosNode(mvp, attrPositions().output, stage)).outPosition
            }
            fragmentStage {
                val unlitMat = findNodeByType<UnlitMaterialNode>()!!
                colorOutput(gammaNode(unlitMat.outColor).outColor)
            }
        }
        return UnlitShader(unlitCfg, unlitModel).apply {
            onPipelineSetup += { builder, _, _ ->
                builder.cullMethod = CullMethod.NO_CULLING
                builder.depthTest = DepthCompareOp.DISABLED
            }
        }
    }
}