package de.fabmax.kool.demo

import de.fabmax.kool.RenderContext
import de.fabmax.kool.TextureProps
import de.fabmax.kool.assetTexture
import de.fabmax.kool.currentTimeMillis
import de.fabmax.kool.gl.GL_LINEAR
import de.fabmax.kool.gl.GL_REPEAT
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.Vec3f
import kotlin.math.round


/**
 * @author fabmax
 */

fun treeScene(ctx: RenderContext): List<Scene> {
    val scenes = mutableListOf<Scene>()

    // generate tree structure
    val treeGen = TreeGenerator()
    treeGen.generate()

    // meshes
    var trunkMesh: Mesh? = null
    var leafMesh: Mesh? = null

    val treeScene = scene {
        defaultShadowMap = CascadedShadowMap.defaultCascadedShadowMap3()

        +makeGroundGrid(40, defaultShadowMap)

        // generate tree trunk mesh
        trunkMesh = textureMesh(isNormalMapped = true) {
            generator = {
                val t = currentTimeMillis()
                treeGen.buildTrunkMesh(this)
                println("Generated ${meshData.numIndices / 3} trunk triangles, took ${currentTimeMillis() - t} ms")
            }

            shader = basicShader {
                colorModel = ColorModel.TEXTURE_COLOR
                lightModel = LightModel.PHONG_LIGHTING
                shadowMap = defaultShadowMap
                isNormalMapped = true
                specularIntensity = 0.25f

                val textureProps = TextureProps("tree_bark.png", GL_LINEAR, GL_REPEAT, 16)
                val nrmMapProps = TextureProps("tree_bark_nrm.png", GL_LINEAR, GL_REPEAT, 16)
                texture = assetTexture(textureProps)
                normalMap = assetTexture(nrmMapProps)
            }
        }

        // generate tree leaf mesh
        leafMesh = textureMesh {
            generator = {
                val t = currentTimeMillis()
                treeGen.buildLeafMesh(this)
                println("Generated ${meshData.numIndices / 3} leaf triangles, took ${currentTimeMillis() - t} ms")
            }

            shader = basicShader {
                colorModel = ColorModel.TEXTURE_COLOR
                lightModel = LightModel.PHONG_LIGHTING
                shadowMap = defaultShadowMap
                specularIntensity = 0.1f
                isDiscardTranslucent = true
                texture = assetTexture("leaf.png")
            }
        }

        +trunkMesh!!
        +leafMesh!!

        // Add a mouse-controlled camera manipulator (actually a specialized TransformGroup)
        +sphericalInputTransform {
            +camera
            setMouseRotation(0f, -30f)
            minZoom = 1f
            maxZoom = 25f
            // panning / camera translation is limited to a certain area
            translationBounds = BoundingBox(Vec3f(-10f, -10f, -10f), Vec3f(10f, 10f, 10f))

            zoomMethod = SphericalInputTransform.ZoomMethod.ZOOM_CENTER
            rightDragMethod = SphericalInputTransform.DragMethod.NONE

            translate(0f, 2f, 0f)
        }
    }
    scenes += treeScene

    fun Slider.disableCamDrag() {
        onHoverEnter += { _, _, _ ->
            // disable mouse interaction on content scene while pointer is over menu
            treeScene.isPickingEnabled = false
        }
        onHoverExit += { _, rt, _->
            // enable mouse interaction on content scene when pointer leaves menu (and nothing else in this scene
            // is hit instead)
            treeScene.isPickingEnabled = true
        }
    }

    scenes += uiScene(ctx.screenDpi) {
        theme = theme(UiTheme.DARK_SIMPLE) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        +container("menu") {
            layoutSpec.setOrigin(zero(), zero(), zero())
            layoutSpec.setSize(pcs(100f), dps(150f), zero())
            ui.setCustom(SimpleComponentUi(this))


            +label("Grow Distance:") {
                layoutSpec.setOrigin(dps(0f, true), dps(110f, true), zero())
                layoutSpec.setSize(dps(200f, true), dps(35f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
            }
            val growDistVal = label("growDistVal") {
                layoutSpec.setOrigin(dps(380f, true), dps(110f, true), zero())
                layoutSpec.setSize(dps(50f, true), dps(35f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                text = formatFloat(treeGen.growDistance)
            }
            +growDistVal
            +slider("growDist") {
                layoutSpec.setOrigin(dps(200f, true), dps(110f, true), zero())
                layoutSpec.setSize(dps(200f, true), dps(35f, true), zero())
                setValue(0.05f, 1f, treeGen.growDistance)
                disableCamDrag()
                onValueChanged += { value ->
                    treeGen.growDistance = value
                    growDistVal.text = formatFloat(value)
                }
            }

            +label("Kill Distance:") {
                layoutSpec.setOrigin(dps(0f, true), dps(75f, true), zero())
                layoutSpec.setSize(dps(200f, true), dps(35f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
            }
            val killDistVal = label("killDistVal") {
                layoutSpec.setOrigin(dps(380f, true), dps(75f, true), zero())
                layoutSpec.setSize(dps(50f, true), dps(35f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                text = formatFloat(treeGen.killDistance)
            }
            +killDistVal
            +slider("killDist") {
                layoutSpec.setOrigin(dps(200f, true), dps(75f, true), zero())
                layoutSpec.setSize(dps(200f, true), dps(35f, true), zero())
                setValue(1f, 10f, treeGen.killDistance)
                disableCamDrag()
                onValueChanged += { value ->
                    treeGen.killDistance = value
                    killDistVal.text = formatFloat(value)
                }
            }

            +label("Attraction Points:") {
                layoutSpec.setOrigin(dps(0f, true), dps(40f, true), zero())
                layoutSpec.setSize(dps(200f, true), dps(35f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
            }
            val attractPtsVal = label("attractPtsVal") {
                layoutSpec.setOrigin(dps(380f, true), dps(40f, true), zero())
                layoutSpec.setSize(dps(50f, true), dps(35f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                text = "${treeGen.numberOfAttractionPoints}"
            }
            +attractPtsVal
            +slider("attractPts") {
                layoutSpec.setOrigin(dps(200f, true), dps(40f, true), zero())
                layoutSpec.setSize(dps(200f, true), dps(35f, true), zero())
                setValue(100f, 20000f, treeGen.numberOfAttractionPoints.toFloat())
                disableCamDrag()
                onValueChanged += { value ->
                    treeGen.numberOfAttractionPoints = value.toInt()
                    attractPtsVal.text = "${value.toInt()}"
                }
            }

            +label("Radius of Influence:") {
                layoutSpec.setOrigin(dps(0f, true), dps(5f, true), zero())
                layoutSpec.setSize(dps(200f, true), dps(35f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
            }
            val infRadiusVal = label("killDistVal") {
                layoutSpec.setOrigin(dps(380f, true), dps(5f, true), zero())
                layoutSpec.setSize(dps(50f, true), dps(35f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                text = formatFloat(treeGen.radiusOfInfluence)
            }
            +infRadiusVal
            +slider("killDist") {
                layoutSpec.setOrigin(dps(200f, true), dps(5f, true), zero())
                layoutSpec.setSize(dps(200f, true), dps(35f, true), zero())
                setValue(0.25f, 10f, treeGen.radiusOfInfluence)
                disableCamDrag()
                onValueChanged += { value ->
                    treeGen.radiusOfInfluence = value
                    infRadiusVal.text = formatFloat(value)
                }
            }

            +button("generate") {
                layoutSpec.setOrigin(dps(470f, true), dps(110f, true), zero())
                layoutSpec.setSize(dps(220f, true), dps(40f, true), zero())
                text = "Generate Tree!"
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                onClick += { _,_,_ ->
                    treeGen.generate()

                    trunkMesh?.apply {
                        meshData.isBatchUpdate = true
                        meshData.clear()
                        val builder = MeshBuilder(meshData)
                        val t = currentTimeMillis()
                        treeGen.buildTrunkMesh(builder)
                        meshData.generateTangents()
                        println("Generated ${meshData.numIndices / 3} trunk triangles, took ${currentTimeMillis() - t} ms")
                        meshData.isBatchUpdate = false
                    }
                    leafMesh?.apply {
                        meshData.isBatchUpdate = true
                        meshData.clear()
                        val builder = MeshBuilder(meshData)
                        val t = currentTimeMillis()
                        treeGen.buildLeafMesh(builder)
                        println("Generated ${meshData.numIndices / 3} leaf triangles, took ${currentTimeMillis() - t} ms")
                        meshData.isBatchUpdate = false
                    }
                }
            }

            +toggleButton("toggleLeafs") {
                layoutSpec.setOrigin(dps(470f, true), dps(75f, true), zero())
                layoutSpec.setSize(dps(230f, true), dps(40f, true), zero())
                text = "Toggle Leafs"
                isEnabled = true
                onClick += { _, _, _ ->
                    leafMesh?.isVisible = isEnabled
                }
            }
        }
    }

    return scenes
}


private fun formatFloat(value: Float): String {
    val i = round(value * 100).toInt()
    val str = when {
        i < 10 -> "0.0$i"
        else -> "${i / 100}.${i % 100}0"
    }
    return str.substring(0, str.indexOf('.') + 3)
}