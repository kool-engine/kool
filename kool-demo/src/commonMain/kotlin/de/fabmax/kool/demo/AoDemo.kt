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
import de.fabmax.kool.util.aoMapGen.AmbientOcclusionHelper
import de.fabmax.kool.util.pbrMapGen.BrdfLutPass
import de.fabmax.kool.util.pbrMapGen.IrradianceMapPass
import de.fabmax.kool.util.pbrMapGen.ReflectionMapPass
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.roundToInt

fun aoDemo(ctx: KoolContext): List<Scene> {
    val aoDemo = AmbientOcclusionDemo(ctx)
    return listOf(aoDemo.mainScene, aoDemo.menu)
}

class AmbientOcclusionDemo(ctx: KoolContext) {

    val mainScene: Scene
    val menu: Scene

    private var autoRotate = true
    private var spotLight = true
    private val noAoMap = Texture { BufferedTextureData.singleColor(Color.WHITE) }

    private lateinit var aoHelper: AmbientOcclusionHelper
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
        aoHelper = AmbientOcclusionHelper(this)

        val loadingAssets = LoadingAssets { teapotMesh, hdriMap ->
            val irrMapPass = IrradianceMapPass(this, hdriMap)
            val reflMapPass = ReflectionMapPass(this, hdriMap)
            val brdfLutPass = BrdfLutPass(this)

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

                    isScrSpcAmbientOcclusion = true
                    scrSpcAmbientOcclusionMap = aoHelper.aoMap

                    isImageBasedLighting = true
                    irradianceMap = irrMapPass.colorTextureCube
                    reflectionMap = reflMapPass.colorTextureCube
                    brdfLut = brdfLutPass.colorTexture
                }
                pipelineLoader = shader

                onUpdate += { _, _ ->
                    if (aoHelper.aoPass.isEnabled) {
                        shader.scrSpcAmbientOcclusionMap = aoHelper.aoMap
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
                    albedoSource = Albedo.TEXTURE_ALBEDO
                    shadowMaps += shadows

                    isScrSpcAmbientOcclusion = true
                    scrSpcAmbientOcclusionMap = aoHelper.aoMap

                    isImageBasedLighting = true
                    irradianceMap = irrMapPass.colorTextureCube
                    reflectionMap = reflMapPass.colorTextureCube
                    brdfLut = brdfLutPass.colorTexture

                    isNormalMapped = true
                    isRoughnessMapped = true
                    isAmbientOcclusionMapped = true
                    albedoMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_planks_03/brown_planks_03_diff_2k.jpg") }
                    ambientOcclusionMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_planks_03/brown_planks_03_AO_2k.jpg") }
                    normalMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_planks_03/brown_planks_03_Nor_2k.jpg") }
                    roughnessMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_planks_03/brown_planks_03_rough_2k.jpg") }
                }
                pipelineLoader = shader

                onUpdate += { _, _ ->
                    if (aoHelper.aoPass.isEnabled) {
                        shader.scrSpcAmbientOcclusionMap = aoHelper.aoMap
                    } else {
                        shader.scrSpcAmbientOcclusionMap = noAoMap
                    }
                }
            }

            this += Skybox(reflMapPass.colorTextureCube, 1f)
        }

        val hdriTexProps = TextureProps(minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST, mipMapping = true)
        ctx.assetMgr.loadAndPrepareTexture("${Demo.envMapBasePath}/mossy_forest_1k.rgbe.png", hdriTexProps) { tex ->
            loadingAssets.hdriMap = tex
        }
        ctx.loadModel("teapot.kmfz") { loadingAssets.teapotMesh = it }
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
                        size.set(aoHelper.denoisePass.texWidth.toFloat(), aoHelper.denoisePass.texHeight.toFloat())
                        mirrorTexCoordsY()
                    }
                }
                pipelineLoader = ModeledShader.TextureColor(aoHelper.aoMap, "colorTex", aoMapColorModel())
            }

            onUpdate += { rp, _ ->
                val screenSz = 0.33f
                val scaleX = rp.viewport.width / aoHelper.denoisePass.texWidth.toFloat() * screenSz
                val scaleY = rp.viewport.height / aoHelper.denoisePass.texHeight.toFloat() * screenSz

                setIdentity()
                val margin = rp.viewport.height * 0.05f
                translate(margin, margin, 0f)
                scale(scaleX, scaleY, 1f)
            }
        }
        +aoMap

        +container("menu container") {
            ui.setCustom(SimpleComponentUi(this))
            layoutSpec.setOrigin(dps(-370f), dps(-635f), zero())
            layoutSpec.setSize(dps(250f), dps(515f), full())

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
                isEnabled = aoHelper.aoPass.isEnabled
                onStateChange += {
                    aoHelper.setEnabled(isEnabled)
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
            val radiusVal = label(aoHelper.aoPass.radius.toString(2)) {
                layoutSpec.setOrigin(pcs(75f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +radiusVal
            y -= 35f
            +slider("radiusSlider", 0.1f, 3f, aoHelper.aoPass.radius) {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onValueChanged += {
                    radiusVal.text = value.toString(2)
                    aoHelper.radius = value
                }
            }
            y -= 35f
            +label("Intensity:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
            }
            val intensityVal = label(aoHelper.aoPass.intensity.toString(2)) {
                layoutSpec.setOrigin(pcs(75f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +intensityVal
            y -= 35f
            +slider("intensitySlider", 0f, 5f, aoHelper.aoPass.intensity) {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onValueChanged += {
                    intensityVal.text = value.toString(2)
                    aoHelper.intensity = value
                }
            }
            y -= 35f
            +label("Bias:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
            }
            val biasVal = label(aoHelper.aoPass.bias.toString(2)) {
                layoutSpec.setOrigin(pcs(75f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +biasVal
            y -= 35f
            +slider("biasSlider", -0.5f, 0.5f, aoHelper.aoPass.bias) {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onValueChanged += {
                    biasVal.text = value.toString(2)
                    aoHelper.bias = value
                }
            }
            y -= 35f
            +label("AO Samples:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
            }
            val kernelSzVal = label(aoHelper.aoPass.kernelSz.toString()) {
                layoutSpec.setOrigin(pcs(75f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +kernelSzVal
            y -= 35f
            +slider("kernelSlider", 4f, 128f, aoHelper.aoPass.kernelSz.toFloat()) {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onValueChanged += {
                    aoHelper.aoPass.kernelSz = value.roundToInt()
                    kernelSzVal.text = aoHelper.kernelSz.toString()
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

    private fun KoolContext.loadModel(path: String, recv: (Mesh) -> Unit) {
        assetMgr.loadModel(path) { model ->
            if (model != null) {
                val mesh = model.meshes[0].toMesh()
                recv(mesh)
            }
        }
    }

    private class LoadingAssets(val block: (Mesh, Texture) -> Unit) {
        var teapotMesh: Mesh? = null
            set(value) {
                field = value
                check()
            }
        var hdriMap: Texture? = null
            set(value) {
                field = value
                check()
            }

        fun check() {
            val mesh = teapotMesh
            val hdri = hdriMap

            if (mesh != null && hdri != null) {
                block(mesh, hdri)
            }
        }
    }

    private fun aoMapColorModel() = ShaderModel("ModeledShader.textureColor()").apply {
        val ifTexCoords: StageInterfaceNode

        vertexStage {
            ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
            positionOutput = simpleVertexPositionNode().outPosition
        }
        fragmentStage {
            val sampler = textureSamplerNode(textureNode("colorTex"), ifTexCoords.output)
            val gray = addNode(Red2GrayNode(sampler.outColor, stage)).outGray
            colorOutput = unlitMaterialNode(gray).outColor
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