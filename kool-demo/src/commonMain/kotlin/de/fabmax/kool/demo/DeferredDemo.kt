package de.fabmax.kool.demo

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Random
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.shadermodel.ShaderModel
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.deferred.*

fun deferredScene() = scene {
    val autoRotate = false
    val rand = Random(1337)

//    val colorMap = listOf(Color.MD_RED, Color.MD_PINK, Color.MD_PURPLE, Color.MD_DEEP_PURPLE, Color.MD_INDIGO,
//            Color.MD_BLUE, Color.MD_LIGHT_BLUE, Color.MD_CYAN, Color.MD_TEAL, Color.MD_GREEN, Color.MD_LIGHT_GREEN,
//            Color.MD_LIME, Color.MD_YELLOW, Color.MD_AMBER, Color.MD_ORANGE, Color.MD_DEEP_ORANGE)

    //val colorMap = listOf(Color.MD_PINK, Color.MD_YELLOW, Color.MD_BLUE, Color.MD_GREEN)
    //val colorMap = listOf(Color.MD_PURPLE, Color.MD_PINK, Color.MD_RED, Color.MD_DEEP_ORANGE, Color.MD_ORANGE)
    val colorMap = listOf(Color.MD_PINK, Color.MD_CYAN)
//    val colorMap = listOf(Color.MD_ORANGE, Color.MD_GREEN)

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
            maxZoom = 50.0

            translation.set(0.0, -3.0, 0.0)
            onUpdate += { _, ctx ->
                if (autoRotate) {
                    verticalRotation += ctx.deltaT * 3f
                }
            }
        }

        +colorMesh {
            generate {
                val sphereProtos = mutableListOf<IndexedVertexList>()
                for (i in 0..10) {
                    val builder = MeshBuilder(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS))
                    sphereProtos += builder.geometry
                    builder.apply {
                        icoSphere {
                            steps = 3
                            radius = rand.randomF(0.3f, 0.4f)
                            center.set(0f, 0.1f + radius, 0f)
                        }
                    }
                }

                for (x in -19..19) {
                    for (y in -19..19) {
                        color = Color.WHITE
                        withTransform {
                            translate(x.toFloat(), 0f, y.toFloat())
                            if ((x + 100) % 2 == (y + 100) % 2) {
                                cube {
                                    size.set(rand.randomF(0.6f, 0.8f), rand.randomF(0.6f, 0.95f), rand.randomF(0.6f, 0.8f))
                                    origin.set(-size.x / 2, 0.1f, -size.z / 2 )
                                }
                            } else {
                                geometry(sphereProtos[rand.randomI(sphereProtos.indices)])
                            }
                        }
                    }
                }
            }
            val mrtCfg = DeferredMrtShader.MrtPbrConfig().apply {
                roughness = 0.15f
            }
            pipelineLoader = DeferredMrtShader(mrtCfg)
        }

        +textureMesh(isNormalMapped = true) {
            generate {
                rotate(90f, Vec3f.NEG_X_AXIS)
                color = Color.WHITE
                rect {
                    size.set(40f, 40f)
                    origin.set(size.x, size.y, 0f).scale(-0.5f)
                    generateTexCoords(30f)
                }
            }
            val mrtCfg = DeferredMrtShader.MrtPbrConfig().apply {
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

    // add some lights
    val lights = mutableListOf<AnimatedLight>()
    val travel = 41f
    val start = travel / 2
    val minSat = 1f
    val maxSat = 1f
    for (x in 0..40) {
        val ro = rand.randomF()
        for (l in 0 until 30) {
            var light = pbrPass.dynamicPointLights.addPointLight {
                color = Color.WHITE.mix(colorMap[rand.randomI(colorMap.indices)].toLinear(), rand.randomF(minSat, maxSat))
                intensity = 1.0f
            }
            lights += AnimatedLight(light).apply {
                startPos.set(x - travel / 2, rand.randomF(0.3f, 0.6f), -start)
                dir.set(0f, 0f, 1f)
                travelDist = travel
                travelPos = l / 10f * travelDist + ro
                speed = rand.randomF(1f, 3f) * 0.25f
            }

            light = pbrPass.dynamicPointLights.addPointLight {
                color = Color.WHITE.mix(colorMap[rand.randomI(colorMap.indices)].toLinear(), rand.randomF(minSat, maxSat))
                intensity = 1.0f
            }
            lights += AnimatedLight(light).apply {
                startPos.set(-start, rand.randomF(0.3f, 0.6f), x - travel / 2)
                dir.set(1f, 0f, 0f)
                travelDist = travel
                travelPos = l / 10f * travelDist + ro
                speed = rand.randomF(1f, 3f) * 0.25f
            }

            light = pbrPass.dynamicPointLights.addPointLight {
                color = Color.WHITE.mix(colorMap[rand.randomI(colorMap.indices)].toLinear(), rand.randomF(minSat, maxSat))
                intensity = 1.0f
            }
            lights += AnimatedLight(light).apply {
                startPos.set(x - travel / 2 + 0.5f, rand.randomF(1f, 1.5f), start)
                dir.set(0f, 0f, -1f)
                travelDist = travel
                travelPos = l / 10f * travelDist + ro
                speed = rand.randomF(1f, 3f) * 0.25f
            }

            light = pbrPass.dynamicPointLights.addPointLight {
                color = Color.WHITE.mix(colorMap[rand.randomI(colorMap.indices)].toLinear(), rand.randomF(minSat, maxSat))
                intensity = 1.0f
            }
            lights += AnimatedLight(light).apply {
                startPos.set(start, rand.randomF(1f, 1.5f), x - travel / 2 + 0.5f)
                dir.set(-1f, 0f, 0f)
                travelDist = travel
                travelPos = l / 10f * travelDist + ro
                speed = rand.randomF(1f, 3f) * 0.25f
            }
        }
    }
    lights.forEach { it.light.intensity = 1f }
    //println("Added ${lights.size} lights")

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

class AnimatedLight(val light: DeferredPointLights.PointLight) {

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
