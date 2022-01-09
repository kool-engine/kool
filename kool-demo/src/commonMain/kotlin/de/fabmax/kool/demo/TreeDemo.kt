package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.Uniform1f
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.AlphaModeMask
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ibl.EnvironmentMaps
import de.fabmax.kool.util.ibl.SkyCubeIblSystem
import kotlin.math.cos
import kotlin.math.sqrt


/**
 * @author fabmax
 */

class TreeDemo : DemoScene("Procedural Tree") {

    val treeGen: TreeGenerator
    var trunkMesh: Mesh? = null
    var leafMesh: Mesh? = null

    var autoRotate = true
    var windSpeed = 2.5f
    var windAnimationPos = 0f
    var windStrength = 1f

    val lightDirection = MutableVec3f(-1f, -1.5f, -1f).norm()

    private lateinit var skySystem: SkyCubeIblSystem

    init {
        val w = 3f
        val h = 3.5f
        val dist = TreeTopPointDistribution(1f + h / 2f, w, h)
        //val dist = SphericalPointDistribution(2f, Vec3f(0f, 3f, 0f))
        //val dist = CubicPointDistribution(4f, Vec3f(0f, 3f, 0f))
        treeGen = TreeGenerator(dist, primaryLightDir = lightDirection)
        treeGen.generate()
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        val sceneContent = Group()
        +sceneContent

        // use simple shadow map with fixed bounds to avoid noise during camera movement
        val shadow = SimpleShadowMap(this, 0).apply { setDefaultDepthOffset(true) }
        val shadowMaps = listOf(shadow)
        onUpdate += {
            shadow.shadowBounds = sceneContent.bounds
        }

        // alternatively a CascadedShadowMap can be used for dynamic shadow bounds based on view frustum
        //val shadowMaps = listOf(CascadedShadowMap(this, 0).apply { maxRange = 50f })

        skySystem = SkyCubeIblSystem(this)
        skySystem.isAutoUpdateIblMaps = true
        skySystem.setupOffscreenPasses()
        val envMaps = skySystem.envMaps

        lighting.singleLight {
            skySystem.skyPass.syncLights += this
        }

        +Skybox.cube(skySystem.skyPass.colorTexture!!)

        // generate tree trunk mesh
        trunkMesh = textureMesh(isNormalMapped = true) {
            generate {
                timedMs({"Generated ${geometry.numIndices / 3} trunk triangles in"}) {
                    treeGen.buildTrunkMesh(this)
                }
            }

            var uWindSpeed: Uniform1f? = null
            var uWindStrength: Uniform1f? = null
            val pbrCfg = PbrMaterialConfig().apply {
                useAlbedoMap("${Demo.pbrBasePath}/bark_pine/Bark_Pine_baseColor.jpg")
                useAmbientOcclusionMap("${Demo.pbrBasePath}/bark_pine/Bark_Pine_ambientOcclusion.jpg")
                useNormalMap("${Demo.pbrBasePath}/bark_pine/Bark_Pine_normal.jpg")
                useRoughnessMap("${Demo.pbrBasePath}/bark_pine/Bark_Pine_roughness.jpg")
                useImageBasedLighting(envMaps)
                this.shadowMaps.addAll(shadowMaps)
                roughness = 1f
            }
            // custom tree shader model applies a (pretty crappy) vertex shader animation emulating wind
            shader = PbrShader(pbrCfg, treePbrModel(pbrCfg)).apply {
                onDispose += {
                    albedoMap.dispose()
                    aoMap.dispose()
                    normalMap.dispose()
                    roughnessMap.dispose()
                }
                onPipelineCreated += { _, _, _ ->
                    uWindSpeed = model.findNode<PushConstantNode1f>("windAnim")?.uniform
                    uWindStrength = model.findNode<PushConstantNode1f>("windStrength")?.uniform
                }
            }
            onUpdate += {
                windAnimationPos += ctx.deltaT * windSpeed
                uWindSpeed?.value = windAnimationPos
                uWindStrength?.value = windStrength
            }
        }

        // generate tree leaf mesh
        leafMesh = textureMesh {
            generate {
                timedMs({"Generated ${geometry.numIndices / 3} leaf triangles in"}) {
                    treeGen.buildLeafMesh(this)
                }
            }

            var uWindSpeed: Uniform1f? = null
            var uWindStrength: Uniform1f? = null
            val pbrCfg = PbrMaterialConfig().apply {
                useAlbedoMap("${Demo.pbrBasePath}/leaf.png")
                useImageBasedLighting(envMaps)
                roughness = 1f
                alphaMode = AlphaModeMask(0.5f)
                cullMethod = CullMethod.NO_CULLING
                isAlwaysLit = true
                this.shadowMaps.addAll(shadowMaps)
            }
            // custom tree shader model applies a (pretty crappy) vertex shader animation emulating wind
            shader = PbrShader(pbrCfg, treePbrModel(pbrCfg)).apply {
                onDispose += {
                    albedoMap.dispose()
                }
                onPipelineCreated += { _, _, _ ->
                    uWindSpeed = model.findNode<PushConstantNode1f>("windAnim")?.uniform
                    uWindStrength = model.findNode<PushConstantNode1f>("windStrength")?.uniform
                }
            }
            onUpdate += {
                uWindSpeed?.value = windAnimationPos
                uWindStrength?.value = windStrength
            }
        }

        sceneContent.apply {
            +makeTreeGroundGrid(10, shadowMaps, envMaps)
            +trunkMesh!!
            +leafMesh!!
        }

        +orbitInputTransform {
            +camera
            minZoom = 1.0
            maxZoom = 50.0
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
            zoom = 6.0

            setMouseRotation(0f, -10f)
            setMouseTranslation(0f, 2f, 0f)

            (camera as PerspectiveCamera).apply { clipFar = 50f }

            onUpdate += {
                if (autoRotate) {
                    verticalRotation += ctx.deltaT * 3f
                }
            }
        }
    }

    override fun setupMenu(ctx: KoolContext) = controlUi {
        section("Generator Settings") {
            sliderWithValue("Grow Distance:", treeGen.growDistance, 0.1f, 0.4f) { treeGen.growDistance = value }
            sliderWithValue("Kill Distance:", treeGen.killDistance, 1f, 4f) { treeGen.killDistance = value }
            sliderWithValue("Attraction Points:", treeGen.numberOfAttractionPoints.toFloat(), 100f, 10000f) {
                treeGen.numberOfAttractionPoints = value.toInt()
            }
            sliderWithValue("Radius of Influence:", treeGen.radiusOfInfluence, 0.25f, 10f) {
                treeGen.radiusOfInfluence = value
            }
            button("Generate Tree") {
                treeGen.generate()

                trunkMesh?.apply {
                    geometry.batchUpdate {
                        clear()
                        val builder = MeshBuilder(this)
                        timedMs({"Generated ${numIndices / 3} trunk triangles in"}) {
                            treeGen.buildTrunkMesh(builder)
                            generateTangents()
                        }
                    }
                }
                leafMesh?.apply {
                    geometry.batchUpdate {
                        clear()
                        val builder = MeshBuilder(this)
                        timedMs({"Generated ${numIndices / 3} leaf triangles in"}) {
                            treeGen.buildLeafMesh(builder)
                        }
                    }
                }
            }
        }
        section("Scene") {
            sliderWithValue("Animation Speed", windSpeed, 0f, 10f) { windSpeed = value }
            sliderWithValue("Animation Strength", windStrength, 0f, 10f) { windStrength = value }
            toggleButton("Toggle Leafs", true) { leafMesh?.isVisible = isEnabled }
            toggleButton("Auto Rotate", autoRotate) { autoRotate = isEnabled }
        }
        section("Light") {
            val aziSlider = sliderWithValueSmall("Azimuth", skySystem.skyPass.azimuth, 0f, 360f, 0, widthLabel = 25f) {
                skySystem.skyPass.azimuth = value
            }
            val eleSlider = sliderWithValueSmall("Elevation", skySystem.skyPass.elevation, -90f, 90f, 0, widthLabel = 25f) {
                skySystem.skyPass.elevation = value
            }
            toggleButton("Auto Update IBL Maps", skySystem.isAutoUpdateIblMaps) { skySystem.isAutoUpdateIblMaps = isEnabled }
            aziSlider.onDragFinished += { skySystem.updateIblMaps() }
            eleSlider.onDragFinished += { skySystem.updateIblMaps() }
        }
    }

    private class WindNode(graph: ShaderGraph) : ShaderNode("windNode", graph) {
        var inputPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        var inputAnim = ShaderNodeIoVar(ModelVar1fConst(0f))
        var inputStrength = ShaderNodeIoVar(ModelVar1fConst(1f))
        val outputPos = ShaderNodeIoVar(ModelVar3f("windOutPos"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inputPos, inputAnim, inputStrength)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
            float r = clamp(sqrt(${inputPos.ref3f()}.x * ${inputPos.ref3f()}.x + ${inputPos.ref3f()}.z * ${inputPos.ref3f()}.z) - 0.25, 0.0, 2.0) + 
                        clamp(${inputPos.ref3f()}.z - 1.0, 0.0, 1.0);
            float windTx = cos(${inputPos.ref3f()}.x * 10.0 + ${inputPos.ref3f()}.y * 2.0 + ${inputAnim.ref1f()}) * r * 0.01 * ${inputStrength.ref1f()};
            float windTz = sin(${inputPos.ref3f()}.z * 10.0 - ${inputPos.ref3f()}.y * 2.0 + ${inputAnim.ref1f()} * 1.1f) * r * 0.01 * ${inputStrength.ref1f()};
            ${outputPos.declare()} = ${inputPos.ref3f()} + vec3(windTx, 0.0, windTz);
        """)
        }
    }

    private fun treePbrModel(cfg: PbrMaterialConfig) = PbrShader.defaultPbrModel(cfg).apply {
        vertexStage {
            val localPosInput = findNode<NamedVariableNode>("localPosInput")!!
            addNode(WindNode(vertexStageGraph)).apply {
                inputPos = localPosInput.input
                inputAnim = pushConstantNode1f("windAnim").output
                inputStrength = pushConstantNode1f("windStrength").output
                localPosInput.input = outputPos
            }
        }
    }

    private fun makeTreeGroundGrid(cells: Int, shadowMaps: List<ShadowMap>, envMaps: EnvironmentMaps): Node {
        val groundExt = cells / 2

        return textureMesh(isNormalMapped = true) {
            //isCastingShadow = false
            generate {
                withTransform {
                    color = Color.LIGHT_GRAY.withAlpha(0.2f)
                    vertexModFun = {
                        texCoord.set(position.x, position.z).scale(0.2f)
                    }
                    grid {
                        sizeX = groundExt * 2f
                        sizeY = groundExt * 2f
                        stepsX = 200
                        stepsY = 200

                        heightFun = { x, y ->
                            val fx = (x.toFloat() / stepsX - 0.5f) * 7f
                            val fy = (y.toFloat() / stepsY - 0.5f) * 7f
                            cos(sqrt(fx*fx + fy*fy)) * 0.2f - 0.2f
                        }
                    }
                }
                geometry.generateTangents()
            }
            shader = pbrShader {
                useAlbedoMap("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_diff_2k.jpg")
                useNormalMap("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_Nor_2k.jpg")
                useRoughnessMap("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_rough_2k.jpg")
                useAmbientOcclusionMap("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_AO_2k.jpg")
                useDisplacementMap("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_disp_2k.jpg")
                useImageBasedLighting(envMaps)
                this.shadowMaps.addAll(shadowMaps)

                onDispose += {
                    albedoMap?.dispose()
                    normalMap?.dispose()
                    roughnessMap?.dispose()
                    aoMap?.dispose()
                    displacementMap?.dispose()
                }
            }
        }
    }
}