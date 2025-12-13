package de.fabmax.kool.demo.physics.box2d.mixer

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.deg
import de.fabmax.kool.math.randomF
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.physics2d.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.*

class MixerDemo : DemoScene("Box2D Mixer Demo") {
    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera(0f, 0f).apply {
            maxZoom = 300.0
            zoom = 150.0
            setTranslation(0f, 20f, 0f)
            panMethod = zPlanePan()
            leftDragMethod = OrbitInputTransform.DragMethod.PAN
            rightDragMethod = OrbitInputTransform.DragMethod.NONE
        }

        val bodies = mutableListOf<Box>()

        val world = Physics2dWorld()
        world.registerHandlers(this)
        bodies.add(world.makeStaticBox(0f, -4f, 100f, 4f))
        bodies.add(world.makeStaticBox(-99.5f, 50f, 0.5f, 50f))
        bodies.add(world.makeStaticBox(99.5f, 50f, 0.5f, 50f))

        val mixer1 = world.makeMixer(Vec2f(-40f, 40f))
        val mixer2 = world.makeMixer(Vec2f(40f, 40f), 90f.deg)
        mixer2.speed = -mixer1.speed
        bodies.add(mixer1.box)
        bodies.add(mixer2.box)

        val g1 = ColorGradient(MdColor.RED, MdColor.PURPLE, MdColor.INDIGO)
        val g2 = ColorGradient(MdColor.AMBER, MdColor.LIGHT_GREEN, MdColor.CYAN)

        val shapeDef = ShapeDef(density = 1f, material = SurfaceMaterial(friction = 0.3f))
        for (colX in -25 .. 25) {
            repeat(75) { i ->
                val position = Vec2f(colX * 3f + randomF(-0.1f, 0.1f), 10f + 3 * i)
                val body = world.createBody(BodyType.Dynamic, position)
                if ((colX + i) % 2 == 0) {
                    body.attachShape(Geometry.Box(1f, 1f), shapeDef)
                    bodies.add(Box(body, 1f, 1f, g1.getColor(i / 75f), isCircle = true))
                } else {
                    body.attachShape(Geometry.Circle(1f), shapeDef)
                    bodies.add(Box(body, 1f, 1f, g2.getColor(i / 75f), isCircle = true))
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
                mixer1.mix(timeStep)
                mixer2.mix(timeStep)
                world.removeOutOfRangeBodies(bodies)
            }
        }
        onUpdate {
            if (Time.frameCount % 30 == 0) {
                val t = world.simStepper.cpuMillisPerStep
                println("${bodies.size} bodies, step time: $t")
            }
        }
    }

    private fun Physics2dWorld.removeOutOfRangeBodies(bodies: MutableList<Box>) {
        val remove = bodies.filter { it.body.position.y < -500f }
        if (remove.isNotEmpty()) {
            bodies -= remove.toSet()
            remove.forEach {
                logD { "Remove out-of-range body: ${it.body.position}" }
                removeBody(it.body)
            }
        }
    }
}

fun Physics2dWorld.makeMixer(center: Vec2f, rotation: AngleF = 0f.deg): Mixer {
    val body = createBody(BodyType.Kinematic, center, rotation.toRotation())
    body.attachShape(Geometry.Box(30f, 0.75f))
    return Mixer(Box(body, 30f, 0.75f, MdColor.PINK, isCircle = false), center, rotation)
}

class Mixer(val box: Box, val center: Vec2f, var rotation: AngleF = 0f.deg) {
    var speed = 70f.deg

    fun mix(dt: Float) {
        rotation += speed * dt
        val target = Pose2f(center, rotation.toRotation())
        box.body.setTargetTransform(target, dt)
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
