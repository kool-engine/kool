package de.fabmax.kool.demo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.scale
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.gltf.GltfFile
import de.fabmax.kool.util.gltf.loadGltfModel
import de.fabmax.kool.util.ibl.EnvironmentHelper
import de.fabmax.kool.util.ibl.EnvironmentMaps
import kotlin.math.*

class AoDemo : DemoScene("Ambient Occlusion") {

    private var autoRotate = true
    private var spotLight = true

    private lateinit var aoPipeline: AoPipeline
    private val shadows = mutableListOf<ShadowMap>()

    private lateinit var ibl: EnvironmentMaps
    private lateinit var teapotMesh: Mesh

    override fun lateInit(ctx: KoolContext) {
        updateLighting()
    }

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        showLoadText("Loading IBL Maps")
        ibl = EnvironmentHelper.hdriEnvironment(mainScene, "${Demo.envMapBasePath}/mossy_forest_1k.rgbe.png", this)

        showLoadText("Loading IBL Maps")
        val modelCfg = GltfFile.ModelGenerateConfig(generateNormals = true, applyMaterials = false)
        val model = loadGltfModel("${Demo.modelBasePath}/teapot.gltf.gz", modelCfg)!!
        teapotMesh = model.meshes.values.first()
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        +orbitInputTransform {
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(0f, -20f)
            // Add camera to the transform group
            +camera
            zoom = 8.0

            onUpdate += {
                if (autoRotate) {
                    verticalRotation += ctx.deltaT * 3f
                }
            }
        }

        shadows.add(SimpleShadowMap(this, 0, 2048))
        aoPipeline = AoPipeline.createForward(this)

        +colorMesh("teapots") {
            generate {
                for (x in -3..3) {
                    for (y in -3..3) {
                        val h = atan2(y.toFloat(), x.toFloat()).toDeg()
                        val s = max(abs(x), abs(y)) / 5f
                        color = Color.fromHsv(h, s, 0.75f, 1f).toLinear()

                        withTransform {
                            translate(x.toFloat(), 0f, y.toFloat())
                            scale(0.25f, 0.25f, 0.25f)
                            rotate(-37.5f, Vec3f.Y_AXIS)
                            geometry(teapotMesh.geometry)
                        }
                    }
                }
            }
            val shader = pbrShader {
                albedoSource = Albedo.VERTEX_ALBEDO
                shadowMaps += shadows
                roughness = 0.1f

                useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                useImageBasedLighting(ibl)
            }
            this.shader = shader
        }

        +textureMesh("ground", isNormalMapped = true) {
            isCastingShadow = false
            generate {
                // generate a cube (as set of rects for better control over tex coords)
                val texScale = 0.1955f

                // top
                withTransform {
                    rotate(90f, Vec3f.NEG_X_AXIS)
                    rect {
                        size.set(12f, 12f)
                        origin.set(size.x, size.y, 0f).scale(-0.5f)
                        setUvs(0.06f, 0f, size.x * texScale, size.y * texScale)
                    }
                }

                // bottom
                withTransform {
                    translate(0f, -0.25f, 0f)
                    rotate(90f, Vec3f.X_AXIS)
                    rect {
                        size.set(12f, 12f)
                        origin.set(size.x, size.y, 0f).scale(-0.5f)
                        setUvs(0.06f, 0f, size.x * texScale, size.y * texScale)
                    }
                }

                // left
                withTransform {
                    translate(-6f, -0.125f, 0f)
                    rotate(90f, Vec3f.NEG_Y_AXIS)
                    rotate(90f, Vec3f.Z_AXIS)
                    rect {
                        size.set(0.25f, 12f)
                        origin.set(size.x, size.y, 0f).scale(-0.5f)
                        setUvs(0.06f - size.x * texScale, 0f, size.x * texScale, size.y * texScale)
                    }
                }

                // right
                withTransform {
                    translate(6f, -0.125f, 0f)
                    rotate(90f, Vec3f.Y_AXIS)
                    rotate(-90f, Vec3f.Z_AXIS)
                    rect {
                        size.set(0.25f, 12f)
                        origin.set(size.x, size.y, 0f).scale(-0.5f)
                        setUvs(0.06f + 12 * texScale, 0f, size.x * texScale, size.y * texScale)
                    }
                }

                // front
                withTransform {
                    translate(0f, -0.125f, 6f)
                    rect {
                        size.set(12f, 0.25f)
                        origin.set(size.x, size.y, 0f).scale(-0.5f)
                        setUvs(0.06f, 12f * texScale, size.x * texScale, size.y * texScale)
                    }
                }

                // back
                withTransform {
                    translate(0f, -0.125f, -6f)
                    rotate(180f, Vec3f.X_AXIS)
                    rect {
                        size.set(12f, 0.25f)
                        origin.set(size.x, size.y, 0f).scale(-0.5f)
                        setUvs(0.06f, -0.25f * texScale, size.x * texScale, size.y * texScale)
                    }
                }
            }

            val shader = pbrShader {
                useAlbedoMap("${Demo.pbrBasePath}/brown_planks_03/brown_planks_03_diff_2k.jpg")
                useAmbientOcclusionMap("${Demo.pbrBasePath}/brown_planks_03/brown_planks_03_AO_2k.jpg")
                useNormalMap("${Demo.pbrBasePath}/brown_planks_03/brown_planks_03_Nor_2k.jpg")
                useRoughnessMap("${Demo.pbrBasePath}/brown_planks_03/brown_planks_03_rough_2k.jpg")

                useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                useImageBasedLighting(ibl)
                shadowMaps += shadows

                onDispose += {
                    albedoMap?.dispose()
                    aoMap?.dispose()
                    normalMap?.dispose()
                    roughnessMap?.dispose()
                }
            }
            this.shader = shader
        }

        this@setupMainScene += Skybox.cube(ibl.reflectionMap, 1f)
    }

    private fun RectProps.setUvs(u: Float, v: Float, width: Float, height: Float) {
        texCoordUpperLeft.set(u, v)
        texCoordUpperRight.set(u + width, v)
        texCoordLowerLeft.set(u, v + height)
        texCoordLowerRight.set(u + width, v + height)
    }

    private fun updateLighting() {
        if (spotLight) {
            mainScene.lighting.singleLight {
                val p = Vec3f(6f, 10f, -6f)
                setSpot(p, scale(p, -1f).norm(), 40f)
                setColor(Color.WHITE.mix(MdColor.AMBER, 0.2f).toLinear(), 500f)
            }
        } else {
            mainScene.lighting.lights.clear()
        }
        shadows.forEach { it.isShadowMapEnabled = spotLight }
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
        val aoMap = image(imageShader = ModeledShader.TextureColor(aoPipeline.aoMap, model = aoMapColorModel())).apply {
            isVisible = false
        }

        section("Ambient Occlusion") {
            toggleButton("Enabled", aoPipeline.isEnabled) { aoPipeline.isEnabled = isEnabled }
            toggleButton("Show AO Map", aoMap.isVisible) { aoMap.isVisible = isEnabled }
            sliderWithValue("Radius:", aoPipeline.radius, 0.1f, 3f, 2) {
                aoPipeline.radius = value
            }
            sliderWithValue("Power:", log(aoPipeline.power, 10f), log(0.2f, 10f), log(5f, 10f), 2) {
                aoPipeline.power = 10f.pow(value)
            }
            sliderWithValue("Strength:", aoPipeline.strength, 0f, 5f, 2) {
                aoPipeline.strength = value
            }
            sliderWithValue("AO Samples:", aoPipeline.kernelSz.toFloat(), 4f, 64f, 0) {
                aoPipeline.kernelSz = value.roundToInt()
            }
            sliderWithValue("Map Size:", aoPipeline.mapSize, 0.1f, 1f, 1) {
                aoPipeline.mapSize = (value * 10).roundToInt() / 10f
            }
        }
        section("Scene") {
            toggleButton("Auto Rotate", autoRotate) { autoRotate = isEnabled }
            toggleButton("Spot Light", spotLight) {
                spotLight = isEnabled
                updateLighting()
            }
        }
    }

    companion object {
        fun aoMapColorModel() = ShaderModel("aoMap").apply {
            val ifTexCoords: StageInterfaceNode

            vertexStage {
                ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                positionOutput = simpleVertexPositionNode().outVec4
            }
            fragmentStage {
                val sampler = texture2dSamplerNode(texture2dNode("colorTex"), ifTexCoords.output)
                val gray = addNode(Red2GrayNode(sampler.outColor, stage)).outGray
                colorOutput(gray)
            }
        }

        private class Red2GrayNode(val inRed: ShaderNodeIoVar, graph: ShaderGraph) : ShaderNode("red2gray", graph) {
            val outGray = ShaderNodeIoVar(ModelVar4f("outGray"), this)

            override fun setup(shaderGraph: ShaderGraph) {
                super.setup(shaderGraph)
                dependsOn(inRed)
            }

            override fun generateCode(generator: CodeGenerator) {
                generator.appendMain("${outGray.declare()} = vec4(${inRed.ref1f()}, ${inRed.ref1f()}, ${inRed.ref1f()}, 1.0);")
            }
        }
    }
}