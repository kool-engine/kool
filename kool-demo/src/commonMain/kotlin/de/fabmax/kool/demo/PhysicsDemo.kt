package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.ibl.EnvironmentHelper
import de.fabmax.kool.util.ibl.EnvironmentMaps
import kotlin.math.sqrt

class PhysicsDemo : DemoScene("Physics") {

    private lateinit var aoPipeline: AoPipeline
    private val shadows = mutableListOf<ShadowMap>()

    private var numSpawnBodies = 450
    private var physicsWorld: PhysicsWorld? = null
    private val bodies = mutableListOf<ColoredBody>()

    private val boxMesh = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS)) {
        isFrustumChecked = false
        instances = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT, Attribute.COLORS))
        generate {
            bevelBox()
        }
    }

    override fun setupMainScene(ctx: KoolContext) = scene {
        defaultCamTransform().apply {
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_TRANSLATE
            panMethod = yPlanePan()
            translationBounds = BoundingBox(Vec3f(-50f), Vec3f(50f))
            minHorizontalRot = -90.0
            maxHorizontalRot = -20.0
            minZoom = 10.0
            zoom = 75.0
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
            boxMesh.shader = instancedBoxShader(ibl)

            Physics.awaitLoaded()
            val physicsWorld = PhysicsWorld()
            this@PhysicsDemo.physicsWorld = physicsWorld
            makeGround(ibl, physicsWorld)

            resetPhysics()
            +boxMesh

            val matBuf = Mat4f()
            val removeBoxes = mutableListOf<ColoredBody>()
            onUpdate += {
                physicsWorld.stepPhysics(it.deltaT)

                boxMesh.instances?.apply {
                    clear()
                    bodies.forEach { box ->
                        if (box.rigidBody.origin.length() > 500f) {
                            removeBoxes += box
                        } else {
                            matBuf.set(box.rigidBody.transform).scale(box.size)
                            addInstance {
                                put(matBuf.matrix)
                                put(box.color.array)
                            }
                        }
                    }
                }

                if (removeBoxes.isNotEmpty()) {
                    removeBoxes.forEach { body ->
                        logI { "Removing out-of-range body" }
                        bodies.remove(body)
                        physicsWorld.removeRigidBody(body.rigidBody)
                    }
                    removeBoxes.clear()
                }
            }

            +Skybox.cube(ibl.reflectionMap, 1f)
        }
    }

    private fun resetPhysics() {
        physicsWorld?.cpuTime = 0.0

        bodies.forEach {
            physicsWorld?.removeRigidBody(it.rigidBody)
        }
        bodies.clear()

        val stacks = numSpawnBodies / 50
        val centers = makeCenters(stacks)

        val rand = Random(39851564)
        for (i in 0 until numSpawnBodies) {
            val layer = i / stacks
            val stack = i % stacks
            val color = Color.MD_COLORS[layer % Color.MD_COLORS.size].toLinear()

            val x = centers[stack].x * 10f + rand.randomF(-1f, 1f)
            val z = centers[stack].y * 10f + rand.randomF(-1f, 1f)
            val y = layer * 5f + 10f

            val boxShape = BoxShape(Vec3f(rand.randomF(2f, 3f), rand.randomF(2f, 3f), rand.randomF(2f, 3f)))
            val box = RigidBody(boxShape, boxShape.size.sqrLength())
            box.origin = Vec3f(x, y, z)
            box.setRotation(Mat3f().rotate(rand.randomF(-45f, 45f), 0f, rand.randomF(-45f, 45f)))
            physicsWorld?.addRigidBody(box)
            bodies += ColoredBody(box, color)
        }
    }

    private fun makeCenters(stacks: Int): List<Vec2f> {
        val dir = MutableVec2f(1f, 0f)
        val centers = mutableListOf(Vec2f(0f, 0f))
        var steps = 1
        var stepsSteps = 1
        while (centers.size < stacks) {
            for (i in 1..steps) {
                centers += MutableVec2f(centers.last()).add(dir)
                if (centers.size == stacks) {
                    break
                }
            }
            dir.rotate(90f)
            if (stepsSteps++ == 2) {
                stepsSteps = 1
                steps++
            }
        }

        return centers
    }

    private fun Scene.makeGround(ibl: EnvironmentMaps, physicsWorld: PhysicsWorld) {
        val frame = mutableListOf<RigidBody>()

        val groundShape = BoxShape(Vec3f(100f, 1f, 100f))
        val ground = RigidBody(groundShape, 0f)
        ground.origin = Vec3f(0f, -0.5f, 0f)
        physicsWorld.addRigidBody(ground)

        val frameLtShape = BoxShape(Vec3f(3f, 6f, 100f))
        val frameLt = RigidBody(frameLtShape, 0f)
        frameLt.origin = Vec3f(-51.5f, 2f, 0f)
        physicsWorld.addRigidBody(frameLt)
        frame += frameLt

        val frameRtShape = BoxShape(Vec3f(3f, 6f, 100f))
        val frameRt = RigidBody(frameRtShape, 0f)
        frameRt.origin = Vec3f(51.5f, 2f, 0f)
        physicsWorld.addRigidBody(frameRt)
        frame += frameRt

        val frameFtShape = BoxShape(Vec3f(106f, 6f, 3f))
        val frameFt = RigidBody(frameFtShape, 0f)
        frameFt.origin = Vec3f(0f, 2f, 51.5f)
        physicsWorld.addRigidBody(frameFt)
        frame += frameFt

        val frameBkShape = BoxShape(Vec3f(106f, 6f, 3f))
        val frameBk = RigidBody(frameBkShape, 0f)
        frameBk.origin = Vec3f(0f, 2f, -51.5f)
        physicsWorld.addRigidBody(frameBk)
        frame += frameBk

        val groundTex = groundTexture()
        onDispose += {
            groundTex.dispose()
        }

        // render textured ground box
        +textureMesh {
            generate {
                cube {
                    size.set(groundShape.size)
                    origin.set(size).scale(-0.5f).add(ground.origin)
                }
            }
            shader = pbrShader {
                roughness = 0.75f
                shadowMaps += shadows
                useImageBasedLighting(ibl)
                useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                useAlbedoMap(groundTex)
            }
        }

        // render frame
        +colorMesh {
            generate {
                frame.forEach {
                    val shape = it.collisionShape as BoxShape
                    cube {
                        size.set(shape.size)
                        origin.set(size).scale(-0.5f).add(it.origin)
                    }
                }
            }
            shader = pbrShader {
                roughness = 0.75f
                shadowMaps += shadows
                useImageBasedLighting(ibl)
                useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                useStaticAlbedo(Color.MD_BLUE_GREY_700.toLinear())
            }
        }
    }

    private fun instancedBoxShader(ibl: EnvironmentMaps): PbrShader {
        val cfg = PbrMaterialConfig().apply {
            roughness = 0.6f
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

    private fun groundTexture(): Texture2d {
        val props = TextureProps(minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST, mipMapping = false, maxAnisotropy = 1)
        return Texture2d(props, "groundTex") {
            val w = 64
            val h = 64
            val buf = createUint8Buffer(w * h * 4)
            val grad = ColorGradient(Color.MD_BLUE_GREY_600, Color.WHITE)
            val rand = Random(18594253)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    val c = grad.getColor(rand.randomF())
                    val i = (y * w + x) * 4
                    buf[i] = (c.r * 255).toInt().toByte()
                    buf[i + 1] = (c.g * 255).toInt().toByte()
                    buf[i + 2] = (c.b * 255).toInt().toByte()
                    buf[i + 3] = (c.a * 255).toInt().toByte()
                }
            }
            TextureData2d(buf, w, h, TexFormat.RGBA)
        }
    }

    private fun MeshBuilder.bevelBox(rBevel: Float = 0.02f) {
        withTransform {
            rotate(0f, 90f, 0f)
            rect {
                size.set(1f - 2 * rBevel, 1f - 2 * rBevel)
                origin.set(-0.5f + rBevel, -0.5f + rBevel, 0.5f)
            }
        }
        withTransform {
            rotate(0f, -90f, 0f)
            rect {
                size.set(1f - 2 * rBevel, 1f - 2 * rBevel)
                origin.set(-0.5f + rBevel, -0.5f + rBevel, 0.5f)
            }
        }
        withTransform {
            profile {
                val s = 1f / sqrt(2f)
                val c = 1f - s
                simpleShape(false) {
                    xy(-0.5f, 0f)
                    normals += MutableVec3f(-1f, 0f, 0f)
                    xy(-0.5f + rBevel * c, rBevel * s)
                    normals += MutableVec3f(-1f, 1f, 0f).norm()
                    xy(-0.5f + rBevel, rBevel)
                    normals += MutableVec3f(0f, 1f, 0f)

                    xy(0.5f - rBevel, rBevel)
                    normals += MutableVec3f(0f, 1f, 0f)
                    xy(0.5f - rBevel * c, rBevel * s)
                    normals += MutableVec3f(1f, 1f, 0f).norm()
                    xy(0.5f, 0f)
                    normals += MutableVec3f(1f, 0f, 0f)
                }

                translate(0f, 0.5f - rBevel, -0.5f + rBevel)
                sample()
                for (i in 0..3) {
                    rotate(-45f, 0f, 0f)
                    sample()
                    rotate(-45f, 0f, 0f)
                    sample()
                    translate(0f, 0f, -1f + 2 * rBevel)
                    sample()
                }
            }
        }
    }

    private class ColoredBody(val rigidBody: RigidBody, val color: MutableColor) {
        val size = MutableVec3f((rigidBody.collisionShape as BoxShape).size)
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
        section("Physics") {
            sliderWithValue("Spawn Bodies:", numSpawnBodies.toFloat(), 50f, 1000f, 0) {
                numSpawnBodies = value.toInt()
            }
            button("Reset Physics") { resetPhysics() }
        }
        section("Info") {
            textWithValue("CPU Step Time:", "0.00 ms").apply {
                onUpdate += {
                    text = "${physicsWorld?.cpuTime?.toString(2)} ms"
                }
            }
        }
    }
}