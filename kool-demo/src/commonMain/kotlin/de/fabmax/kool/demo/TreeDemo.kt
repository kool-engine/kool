package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.Uniform1f
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.pipeline.ibl.SkyCubeIblSystem
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.*
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sqrt


/**
 * @author fabmax
 */

class TreeDemo : DemoScene("Procedural Tree") {

    private val treeGen: TreeGenerator
    private var trunkMesh: Mesh? = null
    private var leafMesh: Mesh? = null

    private val growDist: MutableStateValue<Float>
    private val killDist: MutableStateValue<Float>
    private val attractionPoints: MutableStateValue<Int>
    private val radiusOfInfluence: MutableStateValue<Float>

    private val isAutoRotate = mutableStateOf(true)
    private val windSpeed = mutableStateOf(2.5f)
    private val windStrength = mutableStateOf(1f)
    private var windAnimationPos = 0f

    private val lightDirection = MutableVec3f(-1f, -1.5f, -1f).norm()

    private lateinit var skySystem: SkyCubeIblSystem

    private val isLeafs = mutableStateOf(true).onChange { leafMesh?.isVisible = it }
    private val sunAzimuth = mutableStateOf(0f)
    private val sunElevation = mutableStateOf(0f)
    private val isAutoUpdateIbl = mutableStateOf(true)

    init {
        val w = 3f
        val h = 3.5f
        val dist = TreeTopPointDistribution(1f + h / 2f, w, h)
        //val dist = SphericalPointDistribution(2f, Vec3f(0f, 3f, 0f))
        //val dist = CubicPointDistribution(4f, Vec3f(0f, 3f, 0f))
        treeGen = TreeGenerator(dist, primaryLightDir = lightDirection)
        treeGen.generate()

        growDist = mutableStateOf(treeGen.growDistance).onChange { treeGen.growDistance = it }
        killDist = mutableStateOf(treeGen.killDistance).onChange { treeGen.killDistance = it }
        attractionPoints = mutableStateOf(treeGen.numberOfAttractionPoints).onChange { treeGen.numberOfAttractionPoints = it }
        radiusOfInfluence = mutableStateOf(treeGen.radiusOfInfluence).onChange { treeGen.radiusOfInfluence = it }
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

        skySystem = SkyCubeIblSystem.simpleSkyCubeIblSystem(this)
        skySystem.isAutoUpdateIblMaps = true
        sunAzimuth.set(skySystem.skyPass.azimuth)
        sunElevation.set(skySystem.skyPass.elevation)
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
                useAlbedoMap("${DemoLoader.materialPath}/bark_pine/Bark_Pine_baseColor.jpg")
                useAmbientOcclusionMap("${DemoLoader.materialPath}/bark_pine/Bark_Pine_ambientOcclusion.jpg")
                useNormalMap("${DemoLoader.materialPath}/bark_pine/Bark_Pine_normal.jpg")
                useRoughnessMap("${DemoLoader.materialPath}/bark_pine/Bark_Pine_roughness.jpg")
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
                windAnimationPos += Time.deltaT * windSpeed.value
                uWindSpeed?.value = windAnimationPos
                uWindStrength?.value = windStrength.value
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
                useAlbedoMap("${DemoLoader.materialPath}/leaf.png")
                useImageBasedLighting(envMaps)
                roughness = 1f
                alphaMode = AlphaMode.Mask(0.5f)
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
                uWindStrength?.value = windStrength.value
            }
        }

        sceneContent.apply {
            +makeTreeGroundGrid(10, shadowMaps, envMaps)
            +trunkMesh!!
            +leafMesh!!
        }

        +orbitInputTransform {
            +camera
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
            setZoom(6.0, 1.0, 50.0)

            setMouseRotation(0f, -10f)
            setMouseTranslation(0f, 2f, 0f)

            (camera as PerspectiveCamera).apply { clipFar = 50f }

            onUpdate += {
                if (isAutoRotate.value) {
                    verticalRotation += Time.deltaT * 3f
                }
            }
        }
    }

    private fun updateTree() {
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

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        Button("Generate tree") {
            modifier
                .alignX(AlignmentX.Center)
                .width(Grow.Std)
                .margin(horizontal = 16.dp, vertical = 24.dp)
                .onClick {
                    updateTree()
                }
        }
        MenuSlider2("Grow distance", growDist.use(), 0.1f, 0.4f) { growDist.set(it) }
        MenuSlider2("Kill distance", killDist.use(), 1f, 4f) { killDist.set(it) }
        MenuSlider2("Radius of influence", radiusOfInfluence.use(), 0.25f, 10f) { radiusOfInfluence.set(it) }
        MenuSlider2("Attraction points", attractionPoints.use().toFloat(), 100f, 10000f, { "${it.roundToInt()}" } ) {
            attractionPoints.set(it.roundToInt())
        }

        Text("Scene") { sectionTitleStyle() }
        MenuSlider2("Animation speed", windSpeed.use(), 0f, 10f) { windSpeed.set(it) }
        MenuSlider2("Animation strength", windStrength.use(), 0f, 10f) { windStrength.set(it) }
        MenuRow {  LabeledSwitch("Toggle leafs", isLeafs) }
        MenuRow { LabeledSwitch("Auto rotate view", isAutoRotate) }

        Text("Sunlight") { sectionTitleStyle() }
        MenuRow {
            Text("Azimuth") { labelStyle(UiSizes.baseSize * 1.85f) }
            MenuSlider(
                sunAzimuth.use(), 0f, 360f,
                txtFormat = { "${it.roundToInt()} °" },
                txtWidth = UiSizes.baseSize * 0.9f,
                onChangeEnd = { skySystem.skyPass.azimuth = it }
            ) {
                sunAzimuth.set(it)
                if (isAutoUpdateIbl.value) {
                    skySystem.skyPass.azimuth = it
                }
            }
        }
        MenuRow {
            Text("Elevation") { labelStyle(UiSizes.baseSize * 1.85f) }
            MenuSlider(
                sunElevation.use(), -90f, 90f,
                txtFormat = { "${it.roundToInt()} °" },
                txtWidth = UiSizes.baseSize * 0.9f,
                onChangeEnd = { skySystem.skyPass.elevation = it }
            ) {
                sunElevation.set(it)
                if (isAutoUpdateIbl.value) {
                    skySystem.skyPass.elevation = it
                }
            }
        }
        MenuRow { LabeledSwitch("Auto update environment map", isAutoUpdateIbl) }
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
                useAlbedoMap("${DemoLoader.materialPath}/brown_mud_leaves_01/brown_mud_leaves_01_diff_2k.jpg")
                useNormalMap("${DemoLoader.materialPath}/brown_mud_leaves_01/brown_mud_leaves_01_Nor_2k.jpg")
                useRoughnessMap("${DemoLoader.materialPath}/brown_mud_leaves_01/brown_mud_leaves_01_rough_2k.jpg")
                useAmbientOcclusionMap("${DemoLoader.materialPath}/brown_mud_leaves_01/brown_mud_leaves_01_AO_2k.jpg")
                useDisplacementMap("${DemoLoader.materialPath}/brown_mud_leaves_01/brown_mud_leaves_01_disp_2k.jpg")
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