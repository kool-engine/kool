package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.BoxShape
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidBody
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.ibl.EnvironmentHelper
import de.fabmax.kool.util.ibl.EnvironmentMaps

class PhysicsDemo : DemoScene("Physics") {

    init {
        Physics.loadPhysics()
    }

    private val boxes = mutableListOf<ColoredBox>()
    private val boxMesh = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS)) {
        isFrustumChecked = false
        instances = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT, Attribute.COLORS))
        generate {
            cube {
                size.set(1f, 1f, 1f)
                centered()
            }
        }
    }

    private lateinit var aoPipeline: AoPipeline
    private val shadows = mutableListOf<ShadowMap>()

    override fun setupMainScene(ctx: KoolContext) = scene {
        defaultCamTransform().apply {
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_TRANSLATE
            panMethod = yPlanePan()
            translationBounds = BoundingBox(Vec3f(-50f), Vec3f(50f))
            zoom = 50.0
        }

        (camera as PerspectiveCamera).apply {
            clipNear = 0.5f
            clipFar = 500f
        }

        val shadowMap = CascadedShadowMap(this, 0, maxRange = 175f)
        shadows.add(shadowMap)
        aoPipeline = AoPipeline.createForward(this)

        lighting.singleLight {
            setDirectional(Vec3f(0.8f, -1.2f, 1f))
        }

        ctx.assetMgr.launch {
            val ibl = EnvironmentHelper.hdriEnvironment(this@scene, "${Demo.envMapBasePath}/shanghai_bund_1k.rgbe.png", this)
            boxMesh.shader = boxShader(ibl)
            +Skybox.cube(ibl.reflectionMap, 1f)

            Physics.awaitLoaded()

            val physicsWorld = PhysicsWorld()
            makeGround(physicsWorld)

            val r = Random(654685)
            for (j in 0 until 9) {
                for (i in 0 until 50) {
                    val x = (j % 3 - 1) * 10f
                    val y = (j / 3 - 1) * 10f

                    val boxShape = BoxShape(Vec3f(r.randomF(2f, 4f), r.randomF(1f, 2f), r.randomF(2f, 4f)))
                    val box = RigidBody(boxShape, boxShape.size.sqrLength())
                    box.origin = Vec3f(r.randomF(-1f, 1f) + x, 10f + 5f * i, r.randomF(-1f, 1f) + y)
                    box.setRotation(Mat3f().rotate(r.randomF(-45f, 45f), 0f, r.randomF(-45f, 45f)))
                    physicsWorld.addRigidBody(box)
                    boxes += ColoredBox(box, Color.MD_COLORS[i % Color.MD_COLORS.size].toLinear())
                }
            }

            +boxMesh

            val matBuf = Mat4f()
            onUpdate += {
                physicsWorld.stepPhysics(it.deltaT)

                boxMesh.instances?.apply {
                    clear()
                    boxes.forEach { box ->
                        matBuf.set(box.box.transform).scale(box.size)
                        addInstance {
                            put(matBuf.matrix)
                            put(box.color.array)
                        }
                    }
                }
            }
        }
    }

    private fun makeGround(physicsWorld: PhysicsWorld) {
        val groundShape = BoxShape(Vec3f(100f, 1f, 100f))
        val ground = RigidBody(groundShape, 0f)
        ground.origin = Vec3f(0f, -0.5f, 0f)
        physicsWorld.addRigidBody(ground)
        boxes += ColoredBox(ground, Color.LIGHT_GRAY.toLinear())

        val frameLtShape = BoxShape(Vec3f(3f, 6f, 100f))
        val frameLt = RigidBody(frameLtShape, 0f)
        frameLt.origin = Vec3f(-51.5f, 2f, 0f)
        physicsWorld.addRigidBody(frameLt)
        boxes += ColoredBox(frameLt, Color.GRAY.toLinear())

        val frameRtShape = BoxShape(Vec3f(3f, 6f, 100f))
        val frameRt = RigidBody(frameRtShape, 0f)
        frameRt.origin = Vec3f(51.5f, 2f, 0f)
        physicsWorld.addRigidBody(frameRt)
        boxes += ColoredBox(frameRt, Color.GRAY.toLinear())

        val frameFtShape = BoxShape(Vec3f(106f, 6f, 3f))
        val frameFt = RigidBody(frameFtShape, 0f)
        frameFt.origin = Vec3f(0f, 2f, 51.5f)
        physicsWorld.addRigidBody(frameFt)
        boxes += ColoredBox(frameFt, Color.GRAY.toLinear())

        val frameBkShape = BoxShape(Vec3f(106f, 6f, 3f))
        val frameBk = RigidBody(frameBkShape, 0f)
        frameBk.origin = Vec3f(0f, 2f, -51.5f)
        physicsWorld.addRigidBody(frameBk)
        boxes += ColoredBox(frameBk, Color.GRAY.toLinear())
    }

    private fun boxShader(ibl: EnvironmentMaps): PbrShader {
        val cfg = PbrMaterialConfig().apply {
            isInstanced = true
            shadowMaps += shadows
            useImageBasedLighting(ibl)
            useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
            useStaticAlbedo(Color.WHITE)
        }
        val model = PbrShader.defaultPbrModel(cfg).apply {
            val ifInstColor: StageInterfaceNode
            vertexStage {
                ifInstColor = stageInterfaceNode("ifInstColor", instanceAttributeNode(Attribute.COLORS).output)
            }
            fragmentStage {
                findNodeByType<PbrMaterialNode>()!!.apply {
                    inAlbedo = ifInstColor.output
                }
            }
        }
        return PbrShader(cfg, model)
    }

    private class ColoredBox(val box: RigidBody, val color: MutableColor) {
        val size = MutableVec3f((box.collisionShape as BoxShape).size)
    }
}