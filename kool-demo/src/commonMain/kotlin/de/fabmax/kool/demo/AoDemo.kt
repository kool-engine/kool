package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.lang.b
import de.fabmax.kool.modules.ksl.lang.g
import de.fabmax.kool.modules.ksl.lang.getFloat4Port
import de.fabmax.kool.modules.ksl.lang.r
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.RectProps
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.*

class AoDemo : DemoScene("Ambient Occlusion") {

    private lateinit var aoPipeline: AoPipeline
    private val shadows = mutableListOf<ShadowMap>()
    private val lighting = mainScene.lighting

    private val ibl by hdriImage("${DemoLoader.hdriPath}/mossy_forest_1k.rgbe.png")

    private val albedoMap by texture2d("${DemoLoader.materialPath}/brown_planks_03/brown_planks_03_diff_2k.jpg")
    private val ambientOcclusionMap by texture2d("${DemoLoader.materialPath}/brown_planks_03/brown_planks_03_AO_2k.jpg")
    private val normalMap by texture2d("${DemoLoader.materialPath}/brown_planks_03/brown_planks_03_Nor_2k.jpg")
    private val roughnessMap by texture2d("${DemoLoader.materialPath}/brown_planks_03/brown_planks_03_rough_2k.jpg")

    private val teapot by model(
        "${DemoLoader.modelPath}/teapot.gltf.gz",
        GltfLoadConfig(generateNormals = true, applyMaterials = false)
    )
    private val teapotMesh: Mesh get() = teapot.meshes.values.first()

    private val isAoEnabled = mutableStateOf(true).onChange { _, new -> aoPipeline.isEnabled = new }
    private val isAutoRotate = mutableStateOf(true)
    private val isSpotLight = mutableStateOf(true).onChange { _, new -> updateLighting(new) }
    private val showAoMapValues = listOf("None", "Filtered", "Noisy")
    private val showAoMapIndex = mutableStateOf(0)

    private val aoRadius = mutableStateOf(1f).onChange { _, new -> aoPipeline.radius = new }
    private val aoPower = mutableStateOf(1f).onChange { _, new -> aoPipeline.power = new }
    private val aoStrength = mutableStateOf(1f).onChange { _, new -> aoPipeline.strength = new }
    private val aoSamples = mutableStateOf(16).onChange { _, new -> aoPipeline.kernelSz = new }
    private val aoMapSize = mutableStateOf(1f).onChange { _, new -> aoPipeline.mapSize = new }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        updateLighting(isSpotLight.value)

        orbitCamera {
            translation.set(0.0, -0.7, 0.0)
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(0f, -30f)
            setZoom(8.0)

            onUpdate += {
                if (isAutoRotate.value) {
                    verticalRotation += Time.deltaT * 3f
                }
            }
        }

        val shadowMap = SimpleShadowMap(this, lighting.lights[0], mapSize = 2048)
        mainScene.addOffscreenPass(shadowMap)
        shadows.add(shadowMap)

        aoPipeline = AoPipeline.createForward(mainScene)
        aoRadius.set(aoPipeline.radius)
        aoPower.set(aoPipeline.power)
        aoStrength.set(aoPipeline.strength)
        aoSamples.set(aoPipeline.kernelSz)
        aoMapSize.set(aoPipeline.mapSize)

        addColorMesh("teapots") {
            generate {
                for (x in -3..3) {
                    for (y in -3..3) {
                        val h = atan2(y.toFloat(), x.toFloat()).toDeg()
                        val s = max(abs(x), abs(y)) / 17f
                        color = Color.Oklab.fromHueChroma(0.75f, h, s).toLinearRgb()

                        withTransform {
                            translate(x.toFloat(), 0f, y.toFloat())
                            scale(0.25f, 0.25f, 0.25f)
                            rotate((-37.5f).deg, Vec3f.Y_AXIS)
                            geometry(teapotMesh.geometry)
                        }
                    }
                }
            }
            val shader = KslPbrShader {
                color { vertexColor() }
                roughness(0.1f)
                enableSsao(aoPipeline.aoMap)
                lighting {
                    addShadowMaps(shadows)
                    imageBasedAmbientLight(ibl.irradianceMap)
                }
                reflectionMap = ibl.reflectionMap
            }
            this.shader = shader
        }

        addTextureMesh("ground", isNormalMapped = true) {
            isCastingShadow = false
            generate {
                // generate a cube (as set of rects for better control over tex coords)
                val texScale = 0.1955f

                // top
                withTransform {
                    rotate(90f.deg, Vec3f.NEG_X_AXIS)
                    rect {
                        size.set(12f, 12f)
                        setUvs(0.06f, 0f, size.x * texScale, size.y * texScale)
                    }
                }

                // bottom
                withTransform {
                    translate(0f, -0.25f, 0f)
                    rotate(90f.deg, Vec3f.X_AXIS)
                    rect {
                        size.set(12f, 12f)
                        setUvs(0.06f, 0f, size.x * texScale, size.y * texScale)
                    }
                }

                // left
                withTransform {
                    translate(-6f, -0.125f, 0f)
                    rotate(90f.deg, Vec3f.NEG_Y_AXIS)
                    rotate(90f.deg, Vec3f.Z_AXIS)
                    rect {
                        size.set(0.25f, 12f)
                        setUvs(0.06f - size.x * texScale, 0f, size.x * texScale, size.y * texScale)
                    }
                }

                // right
                withTransform {
                    translate(6f, -0.125f, 0f)
                    rotate(90f.deg, Vec3f.Y_AXIS)
                    rotate((-90f).deg, Vec3f.Z_AXIS)
                    rect {
                        size.set(0.25f, 12f)
                        setUvs(0.06f + 12 * texScale, 0f, size.x * texScale, size.y * texScale)
                    }
                }

                // front
                withTransform {
                    translate(0f, -0.125f, 6f)
                    rect {
                        size.set(12f, 0.25f)
                        setUvs(0.06f, 12f * texScale, size.x * texScale, size.y * texScale)
                    }
                }

                // back
                withTransform {
                    translate(0f, -0.125f, -6f)
                    rotate(180f.deg, Vec3f.X_AXIS)
                    rect {
                        size.set(12f, 0.25f)
                        setUvs(0.06f, -0.25f * texScale, size.x * texScale, size.y * texScale)
                    }
                }
            }

            val shader = KslPbrShader {
                color { textureColor(albedoMap) }
                normalMapping { setNormalMap(normalMap) }
                roughness { textureProperty(roughnessMap) }
                ao {
                    textureProperty(ambientOcclusionMap)
                }
                lighting {
                    enableSsao(aoPipeline.aoMap)
                    imageBasedAmbientLight(ibl.irradianceMap)
                    addShadowMaps(shadows)
                }
                reflectionMap = ibl.reflectionMap
            }
            this.shader = shader
        }

        addNode(Skybox.cube(ibl.reflectionMap, 1.5f))
    }

    private fun RectProps.setUvs(u: Float, v: Float, width: Float, height: Float) {
        texCoordUpperLeft.set(u, v)
        texCoordUpperRight.set(u + width, v)
        texCoordLowerLeft.set(u, v + height)
        texCoordLowerRight.set(u + width, v + height)
    }

    private fun updateLighting(enabled: Boolean) {
        if (enabled) {
            lighting.singleSpotLight {
                val p = Vec3f(6f, 10f, -6f)
                setup(p, p.mul(-1f, MutableVec3f()).norm(), 40f.deg)
                setColor(Color.WHITE.mix(MdColor.AMBER, 0.2f).toLinear(), 500f)
            }
        } else {
            lighting.clear()
        }
        shadows.forEach {
            it.light = lighting.lights.getOrNull(0)
            it.isShadowMapEnabled = enabled
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        LabeledSwitch("AO enabled", isAoEnabled)
        MenuRow {
            Text("Show AO map") { labelStyle() }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(showAoMapValues)
                    .selectedIndex(showAoMapIndex.use())
                    .onItemSelected { showAoMapIndex.set(it) }
            }
        }
        LabeledSwitch("Spot light", isSpotLight)
        LabeledSwitch("Auto rotate view", isAutoRotate)

        val lblW = UiSizes.baseSize * 1.6f
        val txtW = UiSizes.baseSize * 0.8f

        Text("AO Settings") { sectionTitleStyle() }
        MenuRow {
            Text("Radius") { labelStyle(lblW) }
            MenuSlider(aoRadius.use(), 0.1f, 3f, txtWidth = txtW) {
                aoRadius.set(it)
            }
        }
        MenuRow {
            Text("Power") { labelStyle(lblW) }
            MenuSlider(log(aoPower.use(), 10f), log(0.2f, 10f), log(5f, 10f), txtWidth = txtW) {
                aoPower.set(10f.pow(it))
            }
        }
        MenuRow {
            Text("Strength") { labelStyle(lblW) }
            MenuSlider(aoStrength.use(), 0f, 5f, txtWidth = txtW) {
                aoStrength.set(it)
            }
        }
        MenuRow {
            Text("Samples") { labelStyle(lblW) }
            MenuSlider(aoSamples.use().toFloat(), 4f, 64f, { "${it.roundToInt()}" }, txtW) {
                aoSamples.set(it.roundToInt())
            }
        }
        MenuRow {
            Text("Map Size") { labelStyle(lblW) }
            MenuSlider(aoMapSize.use(), 0.1f, 1f, { it.toString(1) }, txtW) {
                aoMapSize.set((it * 10).roundToInt() / 10f)
            }
        }

        if (showAoMapIndex.value != 0) {
            val shader = when (showAoMapIndex.value) {
                1 -> aoMapShader.apply { colorMap = aoPipeline.aoMap }
                2 -> noisyAoMapShader.apply { colorMap = aoPipeline.aoPass.colorTexture }
                else -> null
            }
            if (shader != null) {
                surface.popup().apply {
                    modifier
                        .margin(sizes.gap)
                        .zLayer(UiSurface.LAYER_BACKGROUND)
                        .align(AlignmentX.Start, AlignmentY.Bottom)

                    Image {
                        modifier
                            .imageSize(ImageSize.FixedScale(0.45f / aoMapSize.value))
                            .imageProvider(FlatImageProvider(shader.colorMap, true).mirrorY())
                            .customShader(shader)
                    }
                }
            }
        }
    }

    companion object {
        val aoMapShader = aoShader()
        val noisyAoMapShader = aoShader()

        private fun aoShader() = KslUnlitShader {
            pipeline { depthTest = DepthCompareOp.ALWAYS }
            color { textureData() }
            modelCustomizer = {
                fragmentStage {
                    main {
                        val baseColorPort = getFloat4Port("baseColor")
                        val inColor = float4Var(baseColorPort.input.input)
                        inColor.g set inColor.r
                        inColor.b set inColor.r
                        baseColorPort.input(inColor)
                    }
                }
            }
        }
    }
}