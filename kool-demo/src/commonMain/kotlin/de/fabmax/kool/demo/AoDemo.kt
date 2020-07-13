package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.scale
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.pipeline.BufferedTextureData
import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.gltf.GltfFile
import de.fabmax.kool.util.gltf.loadGltfModel
import de.fabmax.kool.util.ibl.BrdfLutPass
import de.fabmax.kool.util.ibl.IrradianceMapPass
import de.fabmax.kool.util.ibl.ReflectionMapPass
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.roundToInt

fun aoDemo(ctx: KoolContext): List<Scene> {
    val aoDemo = AoDemo(ctx)
    return listOf(aoDemo.mainScene, aoDemo.menu)
}

class AoDemo(ctx: KoolContext) {

    val mainScene: Scene
    val menu: Scene

    private var autoRotate = true
    private var spotLight = true
    private val noAoMap = Texture { BufferedTextureData.singleColor(Color.WHITE) }

    private lateinit var aoPipeline: AoPipeline
    private val shadows = mutableListOf<ShadowMap>()

    init {
        mainScene = makeMainScene(ctx)
        menu = menu(ctx)

        updateLighting()
    }

    private fun makeMainScene(ctx: KoolContext) = scene {
        +orbitInputTransform {
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(0f, -20f)
            // Add camera to the transform group
            +camera
            zoom = 8.0

            onUpdate += { _, _ ->
                if (autoRotate) {
                    verticalRotation += ctx.deltaT * 3f
                }
            }
        }

        shadows.add(SimpleShadowMap(this, 0, 2048))
        aoPipeline = AoPipeline.createForward(this)

        onDispose += {
            noAoMap.dispose()
        }

        ctx.assetMgr.launch {
            val hdriTexProps = TextureProps(minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST, mipMapping = true)
            val hdriMap = loadAndPrepareTexture("${Demo.envMapBasePath}/mossy_forest_1k.rgbe.png", hdriTexProps)

            val modelCfg = GltfFile.ModelGenerateConfig(generateNormals = true, applyMaterials = false)
            val model = loadGltfModel("${Demo.modelBasePath}/teapot.gltf.gz", modelCfg)!!
            val teapotMesh = model.meshes.values.first()

            val irrMapPass = IrradianceMapPass(this@scene, hdriMap)
            val reflMapPass = ReflectionMapPass(this@scene, hdriMap)
            val brdfLutPass = BrdfLutPass(this@scene)

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
                    useImageBasedLighting(irrMapPass.colorTextureCube, reflMapPass.colorTextureCube, brdfLutPass.colorTexture)
                }
                pipelineLoader = shader

                onUpdate += { _, _ ->
                    if (aoPipeline.aoPass.isEnabled) {
                        shader.scrSpcAmbientOcclusionMap = aoPipeline.aoMap
                    } else {
                        shader.scrSpcAmbientOcclusionMap = noAoMap
                    }
                }
            }

            +textureMesh("ground", isNormalMapped = true) {
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
                    useOcclusionMap("${Demo.pbrBasePath}/brown_planks_03/brown_planks_03_AO_2k.jpg")
                    useNormalMap("${Demo.pbrBasePath}/brown_planks_03/brown_planks_03_Nor_2k.jpg")
                    useRoughnessMap("${Demo.pbrBasePath}/brown_planks_03/brown_planks_03_rough_2k.jpg")

                    useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                    useImageBasedLighting(irrMapPass.colorTextureCube, reflMapPass.colorTextureCube, brdfLutPass.colorTexture)
                    shadowMaps += shadows

                    onDispose += {
                        albedoMap?.dispose()
                        occlusionMap?.dispose()
                        normalMap?.dispose()
                        roughnessMap?.dispose()
                        hdriMap.dispose()
                    }
                }
                pipelineLoader = shader

                onUpdate += { _, _ ->
                    if (aoPipeline.aoPass.isEnabled) {
                        shader.scrSpcAmbientOcclusionMap = aoPipeline.aoMap
                    } else {
                        shader.scrSpcAmbientOcclusionMap = noAoMap
                    }
                }
            }

            this@scene += Skybox(reflMapPass.colorTextureCube, 1f)
        }
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
                setColor(Color.WHITE.mix(Color.MD_AMBER, 0.2f).toLinear(), 500f)
            }
        } else {
            mainScene.lighting.lights.clear()
        }
        shadows.forEach { it.isShadowMapEnabled = spotLight }
    }

    private fun menu(ctx: KoolContext) = uiScene {
        val smallFontProps = FontProps(Font.SYSTEM_FONT, 14f)
        val smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, uiDpi, ctx, smallFontProps.style, smallFontProps.chars)
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        val aoMap = transformGroup {
            isVisible = false
            +textureMesh {
                generate {
                    rect {
                        size.set(1f, 1f)
                        mirrorTexCoordsY()
                    }
                }
                pipelineLoader = ModeledShader.TextureColor(aoPipeline.aoMap, "colorTex", aoMapColorModel())
            }

            onUpdate += { rp, _ ->
                val screenSz = 0.33f
                val scaleX = rp.viewport.width * screenSz
                val scaleY = scaleX * (aoPipeline.denoisePass.texHeight.toFloat() / aoPipeline.denoisePass.texWidth.toFloat())

                setIdentity()
                val margin = rp.viewport.height * 0.05f
                translate(margin, margin, 0f)
                scale(scaleX, scaleY, 1f)
            }
        }
        +aoMap

        +container("menu container") {
            ui.setCustom(SimpleComponentUi(this))
            layoutSpec.setOrigin(dps(-370f), dps(-705f), zero())
            layoutSpec.setSize(dps(250f), dps(585f), full())

            // light setup
            var y = -40f
            +label("Ambient Occulsion") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            y -= 35f
            +toggleButton("Enabled") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = aoPipeline.aoPass.isEnabled
                onStateChange += {
                    aoPipeline.setEnabled(isEnabled)
                }
            }
            y -= 35f
            +toggleButton("Show AO Map") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = aoMap.isVisible
                onStateChange += {
                    aoMap.isVisible = isEnabled
                }
            }
            y -= 35f
            +label("Radius:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
            }
            val radiusVal = label(aoPipeline.aoPass.radius.toString(2)) {
                layoutSpec.setOrigin(pcs(75f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +radiusVal
            y -= 35f
            +slider("radiusSlider", 0.1f, 3f, aoPipeline.aoPass.radius) {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onValueChanged += {
                    radiusVal.text = value.toString(2)
                    aoPipeline.radius = value
                }
            }
            y -= 35f
            +label("Intensity:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
            }
            val intensityVal = label(aoPipeline.aoPass.intensity.toString(2)) {
                layoutSpec.setOrigin(pcs(75f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +intensityVal
            y -= 35f
            +slider("intensitySlider", 0f, 5f, aoPipeline.aoPass.intensity) {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onValueChanged += {
                    intensityVal.text = value.toString(2)
                    aoPipeline.intensity = value
                }
            }
            y -= 35f
            +label("Bias:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
            }
            val biasVal = label(aoPipeline.aoPass.bias.toString(2)) {
                layoutSpec.setOrigin(pcs(75f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +biasVal
            y -= 35f
            +slider("biasSlider", -0.5f, 0.5f, aoPipeline.aoPass.bias) {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onValueChanged += {
                    biasVal.text = value.toString(2)
                    aoPipeline.bias = value
                }
            }
            y -= 35f
            +label("AO Samples:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
            }
            val kernelSzVal = label(aoPipeline.aoPass.kernelSz.toString()) {
                layoutSpec.setOrigin(pcs(75f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +kernelSzVal
            y -= 35f
            +slider("kernelSlider", 4f, 128f, aoPipeline.aoPass.kernelSz.toFloat()) {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onValueChanged += {
                    aoPipeline.aoPass.kernelSz = value.roundToInt()
                    kernelSzVal.text = aoPipeline.kernelSz.toString()
                }
            }
            y -= 35f
            +label("Map Size:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
            }
            val mapSzVal = label("${aoPipeline.size.toString(1)} x") {
                layoutSpec.setOrigin(pcs(75f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +mapSzVal
            y -= 35f
            +slider("mapSizeSlider", 1f, 10f, aoPipeline.size * 10) {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onValueChanged += {
                    aoPipeline.size = value.roundToInt() / 10f
                    mapSzVal.text = "${aoPipeline.size.toString(1)} x"
                }
            }

            y -= 40f
            +label("Scene") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            y -= 35f
            +toggleButton("Auto Rotate") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = autoRotate
                onStateChange += {
                    autoRotate = isEnabled
                }
            }
            y -= 35f
            +toggleButton("Spot Light") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = spotLight
                onStateChange += {
                    spotLight = isEnabled
                    updateLighting()
                }
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
                val sampler = textureSamplerNode(textureNode("colorTex"), ifTexCoords.output)
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