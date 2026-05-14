package de.fabmax.kool.demo.physics.box2d.platformer

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.physics2d.BodyType
import de.fabmax.kool.physics2d.ChainDef
import de.fabmax.kool.physics2d.Geometry
import de.fabmax.kool.physics2d.Physics2dWorld
import de.fabmax.kool.physics2d.createBody
import de.fabmax.kool.scene.OrbitInputTransform
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addColorMesh
import de.fabmax.kool.scene.addLineMesh
import de.fabmax.kool.scene.addPointMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.scene.zPlanePan
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.InterpolatableSimulation

private val chains = (100 downTo -100 step 20).map {
    Vec3f(it.toFloat(), 0f, 0f)
}

class PlatformerDemo : DemoScene("Box2D Platformer Demo") {
    private val world = Physics2dWorld()

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera(0f, 0f).apply {
            maxZoom = 150.0
            zoom = 75.0
            setTranslation(0f, 25f, 0f)
            panMethod = zPlanePan()
            leftDragMethod = OrbitInputTransform.DragMethod.PAN
            rightDragMethod = OrbitInputTransform.DragMethod.NONE
        }

        world.registerHandlers(this)

        addLineMesh {
            for (i in 0 until chains.size - 1) {
                addLine(chains[i], chains[i + 1], Color.GREEN)
            }

            shader = KslUnlitShader {
                color { vertexColor() }
                pipeline {
                    lineWidth = 1f
                }
            }
        }

        addPointMesh {
            chains.forEach {
                addPoint(it, 10f, Color.RED)
            }
        }

        val playerBody = world.createBody(BodyType.Dynamic, Vec2f(10f, 10f))
        playerBody.attachShape(Geometry.Box(2.5f, 2.5f))

        val chainBody = world.createBody(BodyType.Static, Vec2f(0f, 0f))
        val chainDef = ChainDef(chains.map { Vec2f(it.x, it.y) })
        chainBody.attachChain(chainDef)

        val mesh = addColorMesh {
            generate {
                rect {
                    size.set(5f, 5f)
                }
            }

            shader = KslUnlitShader {
                color { Color.WHITE }
            }
        }

        world.simulationListeners += object : InterpolatableSimulation {
            override fun simulateStep(timeStep: Float) {
                mesh.transform.setIdentity().translate(playerBody.position.x, playerBody.position.y, 0f)
            }
        }
    }
}