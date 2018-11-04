package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.TextureProps
import de.fabmax.kool.assetTexture
import de.fabmax.kool.gl.GL_LINEAR
import de.fabmax.kool.gl.GL_REPEAT
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.toString
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.timedMs


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

    val treeScene = scene {
        defaultShadowMap = CascadedShadowMap.defaultCascadedShadowMap3()

        +makeGroundGrid(40, defaultShadowMap)

        // generate tree trunk mesh
        trunkMesh = textureMesh(isNormalMapped = true) {
            generator = {
                timedMs({"Generated ${meshData.numIndices / 3} trunk triangles in"}) {
                    treeGen.buildTrunkMesh(this)
                }
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
                timedMs({"Generated ${meshData.numIndices / 3} leaf triangles in"}) {
                    treeGen.buildLeafMesh(this, light.direction)
                }
            }

            // disable culling, leafs are visible from both sides
            cullMethod = CullMethod.NO_CULLING

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

            translate(0f, 2f, 0f)
        }
    }
    scenes += treeScene

    fun Slider.disableCamDrag() {
        onHoverEnter += { _, _, _ ->
            // disable mouse interaction on content scene while pointer is over menu
            treeScene.isPickingEnabled = false
        }
        onHoverExit += { _, _, _->
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
                text = treeGen.growDistance.toString(2)
            }
            +growDistVal
            +slider("growDist") {
                layoutSpec.setOrigin(dps(200f, true), dps(110f, true), zero())
                layoutSpec.setSize(dps(200f, true), dps(35f, true), zero())
                setValue(0.05f, 0.4f, treeGen.growDistance)
                disableCamDrag()
                onValueChanged += { value ->
                    treeGen.growDistance = value
                    growDistVal.text = value.toString(2)
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
                text = treeGen.killDistance.toString(2)
            }
            +killDistVal
            +slider("killDist") {
                layoutSpec.setOrigin(dps(200f, true), dps(75f, true), zero())
                layoutSpec.setSize(dps(200f, true), dps(35f, true), zero())
                setValue(1f, 4f, treeGen.killDistance)
                disableCamDrag()
                onValueChanged += { value ->
                    treeGen.killDistance = value
                    killDistVal.text = value.toString(2)
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
                setValue(100f, 10000f, treeGen.numberOfAttractionPoints.toFloat())
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
                text = treeGen.radiusOfInfluence.toString(2)
            }
            +infRadiusVal
            +slider("killDist") {
                layoutSpec.setOrigin(dps(200f, true), dps(5f, true), zero())
                layoutSpec.setSize(dps(200f, true), dps(35f, true), zero())
                setValue(0.25f, 10f, treeGen.radiusOfInfluence)
                disableCamDrag()
                onValueChanged += { value ->
                    treeGen.radiusOfInfluence = value
                    infRadiusVal.text = value.toString(2)
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
                        meshData.batchUpdate {
                            clear()
                            val builder = MeshBuilder(this)
                            timedMs({"Generated ${numIndices / 3} trunk triangles in"}) {
                                treeGen.buildTrunkMesh(builder)
                                generateTangents()
                            }
                        }
                    }
                    leafMesh?.apply {
                        meshData.batchUpdate {
                            clear()
                            val builder = MeshBuilder(this)
                            timedMs({"Generated ${numIndices / 3} leaf triangles in"}) {
                                treeGen.buildLeafMesh(builder, scene?.light?.direction ?: Vec3f.ZERO)
                            }
                        }
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