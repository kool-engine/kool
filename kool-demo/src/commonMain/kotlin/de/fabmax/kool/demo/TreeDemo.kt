package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.cos
import kotlin.math.sqrt


/**
 * @author fabmax
 */

fun treeScene(ctx: KoolContext): List<Scene> {
    val scenes = mutableListOf<Scene>()

    // generate tree structure
    val w = 3f
    val h = 3.5f
    val dist = TreeTopPointDistribution(1f + h / 2f, w, h)
    //val dist = SphericalPointDistribution(2f, Vec3f(0f, 3f, 0f))
    //val dist = CubicPointDistribution(4f, Vec3f(0f, 3f, 0f))
    val treeGen = TreeGenerator(dist)
    treeGen.generate()

    // meshes
    var trunkMesh: Mesh? = null
    var leafMesh: Mesh? = null

    var autoRotate = true

    val treeScene = scene {
        //lighting.useDefaultShadowMap(ctx)
        lighting.lights.apply {
            clear()
            var pos = Vec3f(10f, 15f, 10f)
            add(Light()
                    .setSpot(pos, pos.scale(-1f, MutableVec3f()).norm(), 45f)
                    .setColor(Color.YELLOW.mix(Color.WHITE, 0.6f), 1000f))
            pos = Vec3f(-10f, 15f, -10f)
            add(Light()
                    .setDirectional(pos.scale(-1f, MutableVec3f()).norm())
                    .setColor(Color.LIGHT_BLUE.mix(Color.WHITE, 0.5f), 1f))
        }

        // fixme: shadow mapping is still a mess
        // currently only sport lights are supported, this scene would usually use a directional (sun) light
        // we emulate that by using as large sport light
        // moreover the shadow night node expects a shadow map for every light, since we use two lights, two are needed
        // although the second won't actually be used (therefore we make it super small, way too much overhead anyway)
        val shadowMaps = mutableListOf(
                ShadowMapPass(this, lighting.lights[0], 2048),
                ShadowMapPass(this, lighting.lights[1], 32)
        )
        shadowMaps.forEach { ctx.offscreenPasses += it.offscreenPass }
        onDispose += {
            shadowMaps.forEach {
                ctx.offscreenPasses -= it.offscreenPass
                it.dispose(ctx)
            }
        }

        +makeTreeGroundGrid(10, shadowMaps)

        // generate tree trunk mesh
        trunkMesh = textureMesh(isNormalMapped = true) {
            generate {
                timedMs({"Generated ${geometry.numIndices / 3} trunk triangles in"}) {
                    treeGen.buildTrunkMesh(this)
                }
            }

            val pbrCfg = PbrShader.PbrConfig().apply {
                albedoSource = Albedo.TEXTURE_ALBEDO
                isNormalMapped = true
                isAmbientOcclusionMapped = true
                isRoughnessMapped = true
                isReceivingShadows = true
                maxLights = 2
            }
            pipelineLoader = PbrShader(pbrCfg).apply {
                albedoMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/bark_pine/Bark_Pine_baseColor.jpg") }
                ambientOcclusionMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/bark_pine/Bark_Pine_ambientOcclusion.jpg") }
                normalMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/bark_pine/Bark_Pine_normal.jpg") }
                roughnessMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/bark_pine/Bark_Pine_roughness.jpg") }

                onDispose += {
                    albedoMap?.dispose()
                    normalMap?.dispose()
                    roughnessMap?.dispose()
                }
                onCreated += {
                    depthMaps?.let { maps ->
                        shadowMaps.forEachIndexed { i, pass ->
                            if (i < maps.size) {
                                maps[i] = pass.offscreenPass.impl.depthTexture
                            }
                        }
                    }
                }
            }
        }

        // generate tree leaf mesh
        leafMesh = textureMesh {
            generate {
                timedMs({"Generated ${geometry.numIndices / 3} leaf triangles in"}) {
                    treeGen.buildLeafMesh(this)
                }
            }

            val pbrCfg = PbrShader.PbrConfig().apply {
                albedoSource = Albedo.TEXTURE_ALBEDO
                isReceivingShadows = true
                maxLights = 2
            }
            pipelineLoader = PbrShader(pbrCfg).apply {
                albedoMap = Texture { it.loadTextureData("leaf.png") }
                roughness = 0.5f

                onDispose += {
                    albedoMap!!.dispose()
                }
                onSetup += {
                    it.cullMethod = CullMethod.NO_CULLING
                }
                onCreated += {
                    depthMaps?.let { maps ->
                        shadowMaps.forEachIndexed { i, pass ->
                            if (i < maps.size) {
                                maps[i] = pass.offscreenPass.impl.depthTexture
                            }
                        }
                    }
                }
            }
        }

        +trunkMesh!!
        +leafMesh!!

        // Add a mouse-controlled camera manipulator (actually a specialized TransformGroup)
        +orbitInputTransform {
            +camera
            minZoom = 1f
            maxZoom = 25f
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
            zoom = 6f

            setMouseRotation(0f, -10f)
            setMouseTranslation(0f, 2f, 0f)

            onPreRender += { ctx ->
                if (autoRotate) {
                    verticalRotation += ctx.deltaT * 3f
                }
            }
        }
    }
    scenes += treeScene



    scenes += uiScene {
        val smallFontProps = FontProps(Font.SYSTEM_FONT, 14f)
        val smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, uiDpi, ctx, smallFontProps.style, smallFontProps.chars)
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        +container("menu container") {
            ui.setCustom(SimpleComponentUi(this))
            layoutSpec.setOrigin(dps(-450f), dps(-560f), zero())
            layoutSpec.setSize(dps(330f), dps(440f), full())

            var y = -40f
            +label("Generator Settings") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }

            y -= 35f
            +label("Grow Distance:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
            }
            val growDistVal = label("growDistVal") {
                layoutSpec.setOrigin(pcs(70f), dps(y), zero())
                layoutSpec.setSize(pcs(30f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = treeGen.growDistance.toString(2)
            }
            +growDistVal
            y -= 25f
            +slider("growDist") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                setValue(0.1f, 0.4f, treeGen.growDistance)
                onValueChanged += { value ->
                    treeGen.growDistance = value
                    growDistVal.text = value.toString(2)
                }
            }

            y -= 35f
            +label("Kill Distance:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
            }
            val killDistVal = label("killDistVal") {
                layoutSpec.setOrigin(pcs(70f), dps(y), zero())
                layoutSpec.setSize(pcs(30f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = treeGen.killDistance.toString(2)
            }
            +killDistVal
            y -= 25f
            +slider("killDist") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                setValue(1f, 4f, treeGen.killDistance)
                onValueChanged += { value ->
                    treeGen.killDistance = value
                    killDistVal.text = value.toString(2)
                }
            }

            y -= 35f
            +label("Attraction Points:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
            }
            val attractPtsVal = label("attractPtsVal") {
                layoutSpec.setOrigin(pcs(70f), dps(y), zero())
                layoutSpec.setSize(pcs(30f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = "${treeGen.numberOfAttractionPoints}"
            }
            +attractPtsVal
            y -= 25f
            +slider("attractPts") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                setValue(100f, 10000f, treeGen.numberOfAttractionPoints.toFloat())
                onValueChanged += { value ->
                    treeGen.numberOfAttractionPoints = value.toInt()
                    attractPtsVal.text = "${value.toInt()}"
                }
            }

            y -= 35f
            +label("Radius of Influence:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
            }
            val infRadiusVal = label("infRadiusVal") {
                layoutSpec.setOrigin(pcs(70f), dps(y), zero())
                layoutSpec.setSize(pcs(30f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = treeGen.radiusOfInfluence.toString(2)
            }
            +infRadiusVal
            y -= 25f
            +slider("killDist") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                setValue(0.25f, 10f, treeGen.radiusOfInfluence)
                onValueChanged += { value ->
                    treeGen.radiusOfInfluence = value
                    infRadiusVal.text = value.toString(2)
                }
            }

            y -= 45f
            +button("Generate Tree!") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onClick += { _,_,_ ->
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

            y -= 40
            +label("Scene") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }

            y -= 35
            +toggleButton("Toggle Leafs") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                isEnabled = true
                onStateChange += {
                    leafMesh?.isVisible = isEnabled
                }
            }
            y -= 35
            +toggleButton("Auto Rotate") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                isEnabled = autoRotate
                onStateChange += {
                    autoRotate = isEnabled
                }
            }
        }
    }

    return scenes
}

private fun makeTreeGroundGrid(cells: Int, shadowMaps: List<ShadowMapPass>): Node {
    val groundExt = cells / 2

    return textureMesh(isNormalMapped = true) {
        isCastingShadow = false
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

        val pbrCfg = PbrShader.PbrConfig().apply {
            albedoSource = Albedo.TEXTURE_ALBEDO
            isNormalMapped = true
            isAmbientOcclusionMapped = true
            isRoughnessMapped = true
            isDisplacementMapped = true

            isReceivingShadows = true
            maxLights = 2
        }
        pipelineLoader = PbrShader(pbrCfg).apply {
            albedoMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_diff_2k.jpg") }
            normalMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_Nor_2k.jpg") }
            roughnessMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_rough_2k.jpg") }
            ambientOcclusionMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_AO_2k.jpg") }
            displacementMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/brown_mud_leaves_01/brown_mud_leaves_01_disp_2k.jpg") }

            onCreated += {
                depthMaps?.let { maps ->
                    shadowMaps.forEachIndexed { i, pass ->
                        if (i < maps.size) {
                            maps[i] = pass.offscreenPass.impl.depthTexture
                        }
                    }
                }
            }

            onDispose += {
                albedoMap?.dispose()
                normalMap?.dispose()
                roughnessMap?.dispose()
                ambientOcclusionMap?.dispose()
                displacementMap?.dispose()
            }
        }
    }
}