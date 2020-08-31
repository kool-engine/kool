package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.smoothStep
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.AlphaModeBlend
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.UnlitMaterialConfig
import de.fabmax.kool.pipeline.shading.UnlitShader
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.scene.group
import de.fabmax.kool.scene.textureMesh
import de.fabmax.kool.util.Color

class AtmosphereSceneCompositing(val atmosphereDemo: AtmosphereDemo) {

    init {
        atmosphereDemo.mainScene.apply {
            //+atmosphereDemo.deferredPipeline.renderOutput
            compositeDepth()
            compositeSkybox()
            compositeColor()
        }
    }

    private fun Scene.compositeColor() {
        +textureMesh {
            isFrustumChecked = false
            generate {
                rect {
                    mirrorTexCoordsY()
                }
            }

            shader = atmosphereDemo.atmoShader
        }
    }

    private fun Scene.compositeDepth() {
        +textureMesh {
            isFrustumChecked = false
            generate {
                rect {
                    mirrorTexCoordsY()
                }
            }
            shader = ModeledShader(ShaderModel().apply {
                val ifTexCoords: StageInterfaceNode
                vertexStage {
                    ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                    positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
                }
                fragmentStage {
                    depthOutput(textureSamplerNode(textureNode("sceneDepth"), ifTexCoords.output).outColor)
                    colorOutput(constVec4f(Color.BLACK))
                }
            }).apply {
                onPipelineSetup += { builder, _, _ ->
                    builder.depthTest = DepthCompareOp.ALWAYS
                }
                onPipelineCreated += { _, _, _ ->
                    model.findNodeByType<TextureNode>()!!.sampler.texture = atmosphereDemo.deferredPipeline.mrtPass.depthTexture
                }
            }
        }
    }

    private fun Scene.compositeSkybox() {
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
            starShader = skyboxShader(atmosphereDemo.textures[AtmosphereDemo.texMilkyway])
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
            sunBgShader = skyboxShader(atmosphereDemo.textures[AtmosphereDemo.texSunBg])
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
            shader = skyboxShader(atmosphereDemo.textures[AtmosphereDemo.texSun])
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
        }
        return UnlitShader(unlitCfg, unlitModel).apply {
            onPipelineSetup += { builder, _, _ ->
                builder.cullMethod = CullMethod.NO_CULLING
                builder.depthTest = DepthCompareOp.LESS_EQUAL
            }
        }
    }

}