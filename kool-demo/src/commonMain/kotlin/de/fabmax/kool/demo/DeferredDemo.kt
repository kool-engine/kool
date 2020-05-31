package de.fabmax.kool.demo

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Random
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.shadermodel.ShaderModel
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.deferred.*

fun deferredScene() = scene {
    val autoRotate = true
    val rand = Random(1337)
    val colorMap = listOf(Color.MD_RED, Color.MD_PINK, Color.MD_PURPLE, Color.MD_DEEP_PURPLE, Color.MD_INDIGO,
            Color.MD_BLUE, Color.MD_LIGHT_BLUE, Color.MD_CYAN, Color.MD_TEAL, Color.MD_GREEN, Color.MD_LIGHT_GREEN,
            Color.MD_LIME, Color.MD_YELLOW, Color.MD_AMBER, Color.MD_ORANGE, Color.MD_DEEP_ORANGE)

    // remove default lighting
    lighting.lights.clear()

    val mrtPass = DeferredMrtPass()
    addOffscreenPass(mrtPass)
    mrtPass.content.apply {
        +orbitInputTransform {
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(0f, -30f)
            // Add camera to the transform group
            +mrtPass.camera
            zoom = 13.0
            maxZoom = 30.0

            translation.set(0.0, -3.0, 0.0)
            onUpdate += { _, ctx ->
                if (autoRotate) {
                    verticalRotation += ctx.deltaT * 3f
                }
            }
        }

        +colorMesh {
            generate {
                for (x in -9..9) {
                    for (y in -9..9) {
                        color = Color.WHITE
                        //color = Color.WHITE.mix(colorMap[rand.randomI(colorMap.indices)], rand.randomF(0.1f, 0.3f))

                        withTransform {
                            translate(x.toFloat(), 0f, y.toFloat())
                            if ((x + 10) % 2 == (y + 10) % 2) {
                                cube {
                                    size.set(rand.randomF(0.6f, 0.8f), rand.randomF(0.6f, 0.95f), rand.randomF(0.6f, 0.8f))
                                    origin.set(-size.x / 2 + rand.randomF(-0.1f, 0.1f), 0.1f + rand.randomF(0f, 0.15f), -size.y / 2 + rand.randomF(-0.1f, 0.1f))
                                }
                            } else {
                                icoSphere {
                                    steps = 3
                                    radius = rand.randomF(0.3f, 0.4f)
                                    center.set(rand.randomF(-0.1f, 0.1f), 0.4f + rand.randomF(0f, 0.15f), rand.randomF(-0.1f, 0.1f))
                                }
                            }
                        }
                    }
                }
            }
            val mrtCfg = DeferredMrtShader.MrtPbrConfig().apply {
                roughness = 0.1f

//                albedoSource = Albedo.TEXTURE_ALBEDO
//                isNormalMapped = true
//                isRoughnessMapped = true
//                isMetallicMapped = true
//                isAmbientOcclusionMapped = true
//
//                albedoMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-albedo2.jpg") }
//                normalMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-normal-dx.jpg") }
//                roughnessMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-roughness.jpg") }
//                metallicMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-metallic.jpg") }
//                ambientOcclusionMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-ao.jpg") }
            }
            pipelineLoader = DeferredMrtShader(mrtCfg)
        }

        +textureMesh(isNormalMapped = true) {
            generate {
                rotate(90f, Vec3f.NEG_X_AXIS)
                color = Color.WHITE
                rect {
                    size.set(20f, 20f)
                    origin.set(size.x, size.y, 0f).scale(-0.5f)
                    generateTexCoords(15f)
                }
            }
            val mrtCfg = DeferredMrtShader.MrtPbrConfig().apply {
                //roughness = 0.5f

                albedoSource = Albedo.TEXTURE_ALBEDO
                isNormalMapped = true
                isRoughnessMapped = true
                isMetallicMapped = true
                isAmbientOcclusionMapped = true

                albedoMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-albedo1.jpg") }
                normalMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-normal-dx.jpg") }
                roughnessMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-roughness.jpg") }
                metallicMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-metallic.jpg") }
                ambientOcclusionMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-ao.jpg") }

//                albedoMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/concrete_floor_02/concrete_floor_02_diff1_1k.jpg") }
//                normalMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/concrete_floor_02/concrete_floor_02_Nor_1k.jpg") }
//                roughnessMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/concrete_floor_02/concrete_floor_02_rough_1k.jpg") }
//                ambientOcclusionMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/concrete_floor_02/concrete_floor_02_AO_1k.jpg") }
            }
            pipelineLoader = DeferredMrtShader(mrtCfg)
        }
    }

    // setup ambient occlusion pass
    val aoPipeline = AoPipeline.createDeferred(this, mrtPass)
    aoPipeline.intensity = 1.2f
    aoPipeline.kernelSz = 32

    // setup lighting pass
    val cfg = PbrSceneShader.DeferredPbrConfig().apply {
        isScrSpcAmbientOcclusion = true
        scrSpcAmbientOcclusionMap = aoPipeline.aoMap
    }
    val pbrPass = PbrLightingPass(this, mrtPass, cfg)
    addOffscreenPass(pbrPass)

    // add some lights
    val lights = mutableListOf<AnimatedLight>()
    val start = -10.5f
    val travel = 21f
    for (x in 0..20) {
        val ro = rand.randomF()
        for (l in 0 until 10) {
            var light = pbrPass.addPointLight {
                color = Color.WHITE.mix(colorMap[rand.randomI(colorMap.indices)].toLinear(), rand.randomF(0.5f, 1f))
                intensity = 1.0f
            }
            lights += AnimatedLight(light).apply {
                startPos.set(x - travel / 2, rand.randomF(0.3f, 0.6f), start)
                dir.set(0f, 0f, 1f)
                travelDist = travel
                travelPos = l / 10f * travelDist + ro
                speed = rand.randomF(1f, 3f) * 0.25f
            }

            light = pbrPass.addPointLight {
                color = Color.WHITE.mix(colorMap[rand.randomI(colorMap.indices)].toLinear(), rand.randomF(0.5f, 1f))
                intensity = 1.0f
            }
            lights += AnimatedLight(light).apply {
                startPos.set(start, rand.randomF(0.3f, 0.6f), x - travel / 2)
                dir.set(1f, 0f, 0f)
                travelDist = travel
                travelPos = l / 10f * travelDist + ro
                speed = rand.randomF(1f, 3f) * 0.25f
            }

            light = pbrPass.addPointLight {
                color = Color.WHITE.mix(colorMap[rand.randomI(colorMap.indices)].toLinear(), rand.randomF(0.5f, 1f))
                intensity = 1.0f
            }
            lights += AnimatedLight(light).apply {
                startPos.set(x - travel / 2 + 0.5f, rand.randomF(1f, 1.5f), start)
                dir.set(0f, 0f, 1f)
                travelDist = travel
                travelPos = l / 10f * travelDist + ro
                speed = rand.randomF(1f, 3f) * 0.25f
            }

            light = pbrPass.addPointLight {
                color = Color.WHITE.mix(colorMap[rand.randomI(colorMap.indices)].toLinear(), rand.randomF(0.5f, 1f))
                intensity = 1.0f
            }
            lights += AnimatedLight(light).apply {
                startPos.set(start, rand.randomF(1f, 1.5f), x - travel / 2 + 0.5f)
                dir.set(1f, 0f, 0f)
                travelDist = travel
                travelPos = l / 10f * travelDist + ro
                speed = rand.randomF(1f, 3f) * 0.25f
            }
        }
    }
    lights.forEach { it.light.intensity = 1f }
    println("Added ${lights.size} lights")

    onUpdate += { _, ctx ->
        lights.forEach { it.animate(ctx.deltaT) }
    }

    // main scene only contains a quad used to draw the deferred shading output
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
}

private fun textureColorModel(texName: String) = ShaderModel("ModeledShader.textureColor()").apply {
    val ifTexCoords: StageInterfaceNode

    vertexStage {
        ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
        positionOutput = simpleVertexPositionNode().outVec4
    }
    fragmentStage {
        val sampler = textureSamplerNode(textureNode(texName), ifTexCoords.output)
        colorOutput(hdrToLdrNode(sampler.outColor).outColor)
    }
}

class AnimatedLight(val light: DeferredPointLight) {

    val startPos = MutableVec3f()
    val dir = MutableVec3f()
    var speed = 1.5f
    var travelPos = 0f
    var travelDist = 10f

    fun animate(deltaT: Float) {
        travelPos += deltaT * speed
        if (travelPos > travelDist) {
            travelPos -= travelDist
        }
        light.position.set(dir).scale(travelPos).add(startPos)
    }

}
