package de.fabmax.kool.demo.physics.box2d.mixer

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.physics2d.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.log10
import kotlin.math.pow

class MixerDemo : DemoScene("Box2D Mixer Demo") {
    private val world = Physics2dWorld()

    private val simTimeFactor = mutableStateOf(1f).onChange { _, new -> world.simStepper.desiredTimeFactor = new }
    private var leftMixerSpeed = 1f
    private var rightMixerSpeed = 1f

    private val physicsTimeTxt = mutableStateOf("0.00 ms")
    private val activeActorsTxt = mutableStateOf("0")
    private val timeFactorTxt = mutableStateOf("1.00 x")

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera(0f, 0f).apply {
            maxZoom = 150.0
            zoom = 75.0
            setTranslation(0f, 25f, 0f)
            panMethod = zPlanePan()
            leftDragMethod = OrbitInputTransform.DragMethod.PAN
            rightDragMethod = OrbitInputTransform.DragMethod.NONE
        }

        val bodies = mutableListOf<Box>()

        world.registerHandlers(this)
        bodies.add(world.makeStaticBox(0f, -2f, 50f, 2f))
        bodies.add(world.makeStaticBox(-49.75f, 25f, 0.25f, 25f))
        bodies.add(world.makeStaticBox(49.75f, 25f, 0.25f, 25f))

        val mixer1 = world.makeMixer(Vec2f(-20f, 20f))
        val mixer2 = world.makeMixer(Vec2f(20f, 20f), 45f.deg)
        mixer2.speed = -mixer1.speed
        bodies.add(mixer1.box)
        bodies.add(mixer2.box)

        val g1 = ColorGradient(MdColor.RED, MdColor.PURPLE, MdColor.INDIGO)
        val g2 = ColorGradient(MdColor.AMBER, MdColor.LIGHT_GREEN, MdColor.CYAN)

        val shapeDef = ShapeDef(density = 1f, material = SurfaceMaterial(friction = 0.3f))
        for (colX in -25 .. 25) {
            repeat(75) { i ->
                val position = Vec2f(colX * 1.5f + randomF(-0.05f, 0.05f), 5f + 1.5f * i)
                val body = world.createBody(BodyType.Dynamic, position)
                if ((colX + i) % 2 == 0) {
                    body.attachShape(Geometry.Box(0.5f, 0.5f), shapeDef)
                    bodies.add(Box(body, 0.5f, 0.5f, g1.getColor(i / 75f), isCircle = false))
                } else {
                    body.attachShape(Geometry.Circle(0.5f), shapeDef)
                    bodies.add(Box(body, 0.5f, 0.5f, g2.getColor(i / 75f), isCircle = true))
                }
            }
        }

        addShapeMesh(bodies.filter { !it.isCircle }) {
            rect {
                size.set(2f, 2f)
            }
        }
        addShapeMesh(bodies.filter { it.isCircle }) {
            circle {
                radius = 1f
            }
        }

        world.simulationListeners += object : InterpolatableSimulation {
            override fun simulateStep(timeStep: Float) {
                mixer1.mix(timeStep, leftMixerSpeed)
                mixer2.mix(timeStep, rightMixerSpeed)
                world.removeOutOfRangeBodies(bodies)

                physicsTimeTxt.set("${world.simStepper.cpuMillisPerStep.toString(2)} ms")
                activeActorsTxt.set("${bodies.size}")
                timeFactorTxt.set("${world.simStepper.actualTimeFactor.toString(2)} x")
            }
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext): UiSurface = menuSurface {
        MenuSlider2("Time factor".l, log10(simTimeFactor.use()), -1f, 1f, { 10f.pow(it).toString(2) }) {
            simTimeFactor.set(10f.pow(it))
        }

        leftMixerSpeed = RotorToggle("Left Rotor".l)
        rightMixerSpeed = RotorToggle("Right Rotor".l)

        Text("Statistics".l) { sectionTitleStyle() }
        MenuRow {
            Text("Active bodies".l) { labelStyle(Grow.Std) }
            Text(activeActorsTxt.use()) { labelStyle() }
        }
        MenuRow {
            Text("Physics step CPU time".l) { labelStyle(Grow.Std) }
            Text(physicsTimeTxt.use()) { labelStyle() }
        }
        MenuRow {
            Text("Actual time factor".l) { labelStyle(Grow.Std) }
            Text(timeFactorTxt.use()) { labelStyle() }
        }
    }

    private fun UiScope.RotorToggle(label: String): Float {
        val rotorEnabled = remember { mutableStateOf(true) }
        LabeledSwitch(label, rotorEnabled) {  }
        val rotorSpeed by animateFloatAsState(
            targetValue = if (rotorEnabled.value) 1f else 0f,
            animationSpec = tween(duration = 0.5f, easing = Easing.smooth)
        )
        return rotorSpeed
    }

    private fun Physics2dWorld.removeOutOfRangeBodies(bodies: MutableList<Box>) {
        val remove = bodies.filter { it.body.position.y < -500f }
        if (remove.isNotEmpty()) {
            bodies -= remove.toSet()
            remove.forEach {
                removeBody(it.body)
            }
        }
    }
}

fun Physics2dWorld.makeMixer(center: Vec2f, rotation: AngleF = 0f.deg): Mixer {
    val body = createBody(BodyType.Kinematic, center, rotation.toRotation())
    body.attachShape(Geometry.Box(15f, 0.4f))
    return Mixer(Box(body, 15f, 0.4f, MdColor.PINK, isCircle = false))
}

class Mixer(val box: Box, var speed: AngleF = 90f.deg) {
    fun mix(dt: Float, factor: Float) {
        box.body.setAngularVelocity(speed.rad * factor)
    }
}

fun Scene.addShapeMesh(boxes: List<Box>, builder: MeshBuilder<ColorMeshLayout>.() -> Unit) {
    val instances = MeshInstanceList(BoxInstance)
    addColorMesh(instances = instances) {
        generate {
            builder()
        }
        shader = KslUnlitShader {
            color { instanceColor(BoxInstance.color) }
            modelCustomizer = {
                vertexStage {
                    main {
                        val size by instanceAttrib(BoxInstance.size)
                        val rot by instanceAttrib(BoxInstance.rot)
                        val boxPos by instanceAttrib(BoxInstance.pos)
                        val vertexPos2d by vertexAttrib(VertexLayouts.Position.position).xy * size

                        val rotMat by mat2Value(rot, float2Value(-rot.y, rot.x))
                        vertexPos2d set rotMat * vertexPos2d + boxPos

                        val camData = cameraData()
                        outPosition set camData.viewProjMat * float4Value(vertexPos2d, 0f.const, 1f.const)
                    }
                }
            }
        }

        onUpdate {
            instances.clear()
            instances.addInstances(boxes.size) { buffer ->
                boxes.forEach { box ->
                    if (box.body.isValid) {
                        val pos = box.body.position
                        val rot = box.body.rotation
                        buffer.put {
                            set(it.size, box.halfWidth, box.halfHeight)
                            set(it.pos, pos.x, pos.y)
                            set(it.rot, rot.cos, rot.sin)
                            set(it.color, box.color)
                        }
                    }
                }
            }
        }
    }
}

private fun Physics2dWorld.makeStaticBox(posX: Float, posY: Float, halfWidth: Float, halfHeight: Float): Box {
    val body = createBody(BodyType.Static, Vec2f(posX, posY))
    body.attachShape(Geometry.Box(halfWidth, halfHeight))
    return Box(body, halfWidth, halfHeight, MdColor.GREY, isCircle = false)
}

object BoxInstance : Struct("BoxInstance", MemoryLayout.TightlyPacked) {
    val size = float2("size")
    val pos = float2("pos")
    val color = float4("color")
    val rot = float2("rot")
}

data class Box(val body: Body, val halfWidth: Float, val halfHeight: Float, val color: Color, val isCircle: Boolean)
