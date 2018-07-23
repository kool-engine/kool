package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.formatFloat
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.physics.BoxMesh
import de.fabmax.kool.modules.physics.CollisionWorld
import de.fabmax.kool.modules.physics.staticBox
import de.fabmax.kool.modules.physics.uniformMassBox
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.scene
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.lineMesh


fun basicCollisionDemo(ctx: KoolContext): List<Scene> {
    val scenes = mutableListOf<Scene>()

    val ARRAY_SIZE_Y = 2
    val ARRAY_SIZE_X = 1
    val ARRAY_SIZE_Z = 1

    scenes += scene {
        +sphericalInputTransform {
            +camera
            maxZoom = 50f
            resetZoom(4f)
            setMouseRotation(45f, -30f)
        }

        val world = CollisionWorld()
        world.gravity.set(0f, -10f, 0f)
        onPreRender += { ctx ->
            world.stepSimulation(ctx.deltaT)
        }

        world.bodies += staticBox(100f, 100f, 100f).apply {
            centerOfMass.set(0f, -50f, 0f)
            this@scene += BoxMesh(this)
        }

        var c = 0
        for (k in 0 until ARRAY_SIZE_Y) {
            for (i in 0 until ARRAY_SIZE_X) {
                for (j in 0 until ARRAY_SIZE_Z) {
                    world.bodies += uniformMassBox(.2f, .2f, .2f, 1f).apply {
                        centerOfMass.set(0.2f * i, 2 + 0.2f*k, 0.2f * j)
                        this@scene += BoxMesh(this, BOX_COLORS[c++ % BOX_COLORS.size])
                    }
                }
            }
        }

    }

    return scenes
}

fun collisionDemo(ctx: KoolContext): List<Scene> {
    var boxWorld: BoxWorld? = null
    val scenes = mutableListOf<Scene>()
    var boxCnt = 40

    val boxScene = scene {
        light.direction.set(1f, 0.8f, 0.4f)
        defaultShadowMap = CascadedShadowMap.defaultCascadedShadowMap3()

        +sphericalInputTransform {
            +camera
            maxZoom = 50f
            resetZoom(50f)
            setMouseRotation(45f, -30f)
        }

        boxWorld = BoxWorld(defaultShadowMap)
        boxWorld!!.createBoxes(boxCnt)
        +boxWorld!!

        +lineMesh {
            val sz = 25
            val y = 0.005f
            for (i in -sz..sz) {
                val color = Color.MD_GREY_600.withAlpha(0.5f)
                addLine(Vec3f(i.toFloat(), y, -sz.toFloat()), color,
                        Vec3f(i.toFloat(), y, sz.toFloat()), color)
                addLine(Vec3f(-sz.toFloat(), y, i.toFloat()), color,
                        Vec3f(sz.toFloat(), y, i.toFloat()), color)
            }
            isCastingShadow = false
            shader = basicShader {
                lightModel = LightModel.NO_LIGHTING
                colorModel = ColorModel.VERTEX_COLOR
                shadowMap = defaultShadowMap
            }
        }
    }

    scenes += boxScene
    scenes += uiScene(ctx.screenDpi) {
        theme = theme(UiTheme.DARK_SIMPLE) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        fun Slider.disableCamDrag() {
            onHoverEnter += { _, _, _ ->
                // disable mouse interaction on content scene while pointer is over menu
                boxScene.isPickingEnabled = false
            }
            onHoverExit += { _, _, _->
                // enable mouse interaction on content scene when pointer leaves menu (and nothing else in this scene
                // is hit instead)
                boxScene.isPickingEnabled = true
            }
        }

        +container("menu") {
            layoutSpec.setOrigin(zero(), zero(), zero())
            layoutSpec.setSize(dps(280f), dps(115f), zero())
            ui.setCustom(SimpleComponentUi(this))

            +label("Boxes:") {
                layoutSpec.setOrigin(dps(0f, true), dps(75f, true), zero())
                layoutSpec.setSize(dps(80f, true), dps(35f, true), zero())
            }
            val boxCntLbl = label("boxCntLbl") {
                layoutSpec.setOrigin(dps(210f, true), dps(75f, true), zero())
                layoutSpec.setSize(dps(50f, true), dps(35f, true), zero())
                text = "$boxCnt"
            }
            +boxCntLbl
            +slider("boxCnt", 1f, 200f,  40f) {
                layoutSpec.setOrigin(dps(80f, true), dps(75f, true), zero())
                layoutSpec.setSize(dps(150f, true), dps(35f, true), zero())
                disableCamDrag()

                onValueChanged += { value ->
                    boxCnt = value.toInt()
                    boxCntLbl.text = "$boxCnt"
                }
            }

            +label("Gravity:") {
                layoutSpec.setOrigin(dps(0f, true), dps(40f, true), zero())
                layoutSpec.setSize(dps(80f, true), dps(35f, true), zero())
            }
            val gravityLbl = label("gravityLbl") {
                layoutSpec.setOrigin(dps(210f, true), dps(40f, true), zero())
                layoutSpec.setSize(dps(50f, true), dps(35f, true), zero())
                text = formatFloat(boxWorld!!.world.gravity.length(), 2)
            }
            +gravityLbl
            +slider("gravity", 0f, 10f,  boxWorld!!.world.gravity.length()) {
                layoutSpec.setOrigin(dps(80f, true), dps(40f, true), zero())
                layoutSpec.setSize(dps(150f, true), dps(35f, true), zero())
                disableCamDrag()

                onValueChanged += { value ->
                    val grav = boxWorld!!.world.gravity
                    grav.set(0f, -value, 0f)
                    gravityLbl.text = formatFloat(grav.length(), 2)
                }
            }

            +button("Reset Boxes!") {
                layoutSpec.setOrigin(dps(0f, true), dps(5f, true), zero())
                layoutSpec.setSize(dps(150f, true), dps(35f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)

                onClick += { _, _, ctx ->
                    boxWorld!!.clearBoxes(ctx)
                    boxWorld!!.createBoxes(boxCnt)
                }
            }
        }
    }

    return scenes
}

class BoxWorld(private val shadowMap: ShadowMap?): Group() {
    val world = CollisionWorld()
    private val rand = Random(20)

    init {
        world.gravity.set(0f, -2.5f, 0f)
        onPreRender += { ctx ->
            world.stepSimulation(ctx.deltaT)
        }
    }

    fun clearBoxes(ctx: KoolContext) {
        children.forEach {
            it.dispose(ctx)
        }
        children.clear()
        world.bodies.clear()
    }

    fun createBoxes(n: Int) {
        val stacks = n / 50 + 1
        val centers = makeCenters(stacks)

        for (i in 0..(n-1)) {
            val x = rand.randomF(1f, 2f)
            val y = rand.randomF(1f, 2f)
            val z = rand.randomF(1f, 2f)
            val box = uniformMassBox(x, y, z, x * y * z).apply {
                centerOfMass.x = centers[i%centers.size].x + rand.randomF(-.5f, .5f)
                centerOfMass.z = centers[i%centers.size].y + rand.randomF(-.5f, .5f)
                centerOfMass.y = (n - i) / stacks * 3f + 3

                worldTransform.rotate(rand.randomF(0f, 360f),
                        MutableVec3f(rand.randomF(-1f, 1f), rand.randomF(-1f, 1f), rand.randomF(-1f, 1f)).norm())
            }
            world.bodies += box
            //this += BoxMesh(box, BOX_COLORS[rand.randomI(0, BOX_COLORS.size-1)], shadowMap)
            this += BoxMesh(box, BOX_COLORS[i % BOX_COLORS.size], shadowMap)
        }
        createGround()
    }

    private fun makeCenters(stacks: Int): List<Vec2f> {
        val dir = MutableVec2f(4f, 0f)
        val centers = mutableListOf(Vec2f(0f, 0f))
        var j = 0
        var steps = 1
        var stepsSteps = 1
        while (j < stacks-1) {
            for (i in 1..steps) {
                centers += MutableVec2f(centers.last()).add(dir)
                j++
            }
            dir.rotate(90f)
            if (stepsSteps++ == 2) {
                stepsSteps = 1
                steps++
            }
        }

        return centers
    }

    private fun createGround() {
        val groundBox = staticBox(50f, 1f, 50f).apply {
            centerOfMass.set(0f, -0.5f, 0f)
        }
        world.bodies += groundBox
        this += BoxMesh(groundBox, Color.MD_GREY, shadowMap)

        val borderLt = staticBox(1f, 4f, 50f).apply {
            centerOfMass.set(-25.5f, 2f, 0f)
        }
        world.bodies += borderLt
        this += BoxMesh(borderLt, Color.MD_ORANGE, shadowMap)

        val borderRt = staticBox(1f, 4f, 50f).apply {
            centerOfMass.set(25.5f, 2f, 0f)
        }
        world.bodies += borderRt
        this += BoxMesh(borderRt, Color.MD_ORANGE, shadowMap)

        val borderBk = staticBox(52f, 4f, 1f).apply {
            centerOfMass.set(0f, 2f, -25.5f)
        }
        world.bodies += borderBk
        this += BoxMesh(borderBk, Color.MD_ORANGE, shadowMap)

        val borderFt = staticBox(52f, 4f, 1f).apply {
            centerOfMass.set(0f, 2f, 25.5f)
        }
        world.bodies += borderFt
        this += BoxMesh(borderFt, Color.MD_ORANGE, shadowMap)
    }
}


private val BOX_COLORS = listOf(Color.MD_YELLOW, Color.MD_AMBER, Color.MD_ORANGE, Color.MD_DEEP_ORANGE, Color.MD_RED,
        Color.MD_PINK, Color.MD_PURPLE, Color.MD_DEEP_PURPLE, Color.MD_INDIGO, Color.MD_BLUE,
        Color.MD_LIGHT_BLUE, Color.MD_CYAN, Color.MD_TEAL, Color.MD_GREEN, Color.MD_LIGHT_GREEN, Color.MD_LIME)

//private val BOX_COLORS = listOf(Color.MD_AMBER_500, Color.MD_BLUE_500, Color.MD_BROWN_500, Color.MD_CYAN_500,
//        Color.MD_GREEN_500, Color.MD_INDIGO_500, Color.MD_LIME_500, Color.MD_ORANGE_500, Color.MD_PINK_500,
//        Color.MD_PURPLE_500, Color.MD_RED_500, Color.MD_TEAL_500, Color.MD_YELLOW_500, Color.MD_DEEP_ORANGE_500,
//        Color.MD_DEEP_PURPLE_500, Color.MD_LIGHT_BLUE_500, Color.MD_LIGHT_GREEN_500, Color.MD_BLUE_GREY_500)