package de.fabmax.kool.demo

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.scale
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.pipeline.shadermodel.ShaderModel
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.deferred.DeferredMrtPass
import de.fabmax.kool.util.deferred.DeferredMrtShader
import de.fabmax.kool.util.deferred.DeferredPbrPass
import de.fabmax.kool.util.deferred.DeferredPbrShader
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max

fun deferredScene() = scene {
    val isSpotLight = false

    lighting.singleLight {
        val p = MutableVec3f(6f, 10f, -7f)
        if (isSpotLight) {
            setSpot(p, scale(p, -1f).norm(), 45f)
            setColor(Color.WHITE.mix(Color.MD_AMBER, 0.2f).toLinear(), 750f)
        } else {
            setDirectional(MutableVec3f(p).scale(-1f).norm())
            setColor(Color.WHITE.mix(Color.MD_AMBER, 0.2f).toLinear(), 3f)
        }
    }

    val mrtPass = DeferredMrtPass()
    addOffscreenPass(mrtPass)
    mrtPass.colorBlend = false
    mrtPass.content.apply {
        +orbitInputTransform {
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(0f, -30f)
            // Add camera to the transform group
            +mrtPass.camera
            maxZoom = 30.0
        }

        +colorMesh {
            generate {
                for (x in -3..3) {
                    for (y in -3..3) {
                        val h = atan2(y.toFloat(), x.toFloat()).toDeg()
                        val s = max(abs(x), abs(y)) / 5f
                        color = Color.fromHsv(h, s, 0.75f, 1f)

                        withTransform {
                            translate(x.toFloat(), 0.5f, y.toFloat())
                            if ((x + 10) % 2 == (y + 10) % 2) {
                                cube {
                                    size.set(0.9f, 0.9f, 0.9f)
                                    centered()
                                }
                            } else {
                                icoSphere {
                                    steps = 3
                                    radius = 0.45f
                                }
                            }
                        }
                    }
                }
            }
            val mrtCfg = DeferredMrtShader.MrtPbrConfig().apply {
                roughness = 0.2f
            }
            pipelineLoader = DeferredMrtShader(mrtCfg)
        }

        +colorMesh {
            generate {
                rotate(90f, Vec3f.NEG_X_AXIS)
                color = Color.WHITE
                rect {
                    size.set(15f, 15f)
                    origin.set(size.x, size.y, 0f).scale(-0.5f)
                }
            }
            val mrtCfg = DeferredMrtShader.MrtPbrConfig().apply {
                roughness = 0.5f
            }
            pipelineLoader = DeferredMrtShader(mrtCfg)
        }
    }

    val aoPipeline = AoPipeline.createDeferred(this, mrtPass)
    aoPipeline.intensity = 1.2f
    aoPipeline.kernelSz = 64

    val shadows = mutableListOf<ShadowMap>()
    if (isSpotLight) {
        shadows += SimpleShadowMap(this, 0, 2048, drawNode = mrtPass.content)
    } else {
        shadows += CascadedShadowMap(this, 0, numCascades = 3, mapSize = 2048, drawNode = mrtPass.content).apply {
            println(mapRanges.map { "$it" }.joinToString())
            setMapRanges(0.2f, 0.6f, 1f)
            maxRange = 30f
            cascades.forEach { it.sceneCam = mrtPass.camera }
        }
    }

    val cfg = DeferredPbrShader.DeferredPbrConfig().apply {
        shadowMaps += shadows
        isScrSpcAmbientOcclusion = true
        scrSpcAmbientOcclusionMap = aoPipeline.aoMap
    }
    val pbrPass = DeferredPbrPass(this, mrtPass, cfg)
    addOffscreenPass(pbrPass)

    when (val shadowMap = shadows[0]) {
        is SimpleShadowMap -> pbrPass.dependsOn(shadowMap)
        is CascadedShadowMap -> shadowMap.cascades.forEach { pbrPass.dependsOn(it) }
    }

    camera = OrthographicCamera().apply {
        isKeepAspectRatio = false
        left = -0.5f
        right = 0.5f
        top = 0.5f
        bottom = -0.5f
    }

    +textureMesh {
        generate {
            rect {
                origin.set(-0.5f, -0.5f, 0f)
                mirrorTexCoordsY()
            }
        }
        pipelineLoader = ModeledShader.TextureColor(pbrPass.colorTexture, model = textureColorModel("colorTex"))
    }

    +textureMesh {
        isVisible = false
        generate {
            rect {
                origin.set(-0.5f, -0.5f, 0.1f)
                size.set(0.5f, 0.5f)
                mirrorTexCoordsY()
            }
        }
        pipelineLoader = ModeledShader.TextureColor(aoPipeline.aoMap)
    }
}

private fun textureColorModel(texName: String) = ShaderModel("ModeledShader.textureColor()").apply {
    val ifTexCoords: StageInterfaceNode

    vertexStage {
        ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
        positionOutput = simpleVertexPositionNode().outVec4
    }
    fragmentStage {
        val sampler = textureSamplerNode(textureNode(texName), ifTexCoords.output)
        colorOutput(sampler.outColor)
    }
}
