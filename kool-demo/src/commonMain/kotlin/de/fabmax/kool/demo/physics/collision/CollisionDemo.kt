package de.fabmax.kool.demo.physics.collision

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.*
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
import kotlin.math.*

class CollisionDemo : DemoScene("Physics - Collision") {

    private lateinit var aoPipeline: AoPipeline
    private val shadows = mutableListOf<ShadowMap>()

    private val shapeType = Cycler(*ShapeType.values()).apply { index = 6 }

    private var numSpawnBodies = 450
    private var friction = 0.5f
    private var restitution = 0.2f
    private var physicsWorld: PhysicsWorld? = null
    private val bodies = mutableListOf<ColoredBody>()

    private val shapeGenCtx = ShapeType.ShapeGeneratorContext()

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
            val ibl = EnvironmentHelper.hdriEnvironment(this@scene, "${Demo.envMapBasePath}/colorful_studio_1k.rgbe.png", this)

            Physics.awaitLoaded()
            val physicsWorld = PhysicsWorld()
            this@CollisionDemo.physicsWorld = physicsWorld
            // disable async physics to get accurate physics cpu time measurements
            physicsWorld.isStepAsync = false
            makeGround(ibl, physicsWorld)

            shapeGenCtx.material = Material(friction, friction, restitution)

            resetPhysics()

            shapeType.forEach {
                if (it != ShapeType.MIXED) {
                    it.mesh.shader = instancedBodyShader(ibl)
                    +it.mesh
                }
            }

            val matBuf = Mat4f()
            val removeBodies = mutableListOf<ColoredBody>()
            onUpdate += {
                for (i in shapeType.indices) {
                    shapeType[i].instances.clear()
                }

                bodies.forEach { body ->
                    if (body.rigidActor.position.length() > 500f) {
                        removeBodies += body
                    } else {
                        matBuf.set(body.rigidActor.transform).scale(body.scale)
                        body.shapeType.instances.addInstance {
                            put(matBuf.matrix)
                            put(body.color.array)
                        }
                    }
                }

                if (removeBodies.isNotEmpty()) {
                    removeBodies.forEach { body ->
                        logI { "Removing out-of-range body" }
                        bodies.remove(body)
                        physicsWorld.removeActor(body.rigidActor)
                        body.rigidActor.release()
                    }
                    removeBodies.clear()
                }
            }

            +Skybox.cube(ibl.reflectionMap, 1f)
            physicsWorld.registerHandlers(this@scene)
        }

        onDispose += {
            cleanUp()
        }
    }

    private fun cleanUp() {
        physicsWorld?.apply {
            clear()
            release()
        }
        shapeGenCtx.material.release()
    }

    private fun resetPhysics() {
        bodies.forEach {
            physicsWorld?.removeActor(it.rigidActor)
            it.rigidActor.release()
        }
        bodies.clear()

        val types = if (shapeType.current == ShapeType.MIXED) {
            ShapeType.values().toList().filter { it != ShapeType.MIXED }
        } else {
            listOf(shapeType.current)
        }

        shapeGenCtx.material.release()
        shapeGenCtx.material = Material(friction, friction, restitution)

        val stacks = max(1, numSpawnBodies / 50)
        val centers = makeCenters(stacks)

        val rand = Random(39851564)
        for (i in 0 until numSpawnBodies) {
            val layer = i / stacks
            val stack = i % stacks
            val color = Color.MD_COLORS[layer % Color.MD_COLORS.size].toLinear()

            val x = centers[stack].x * 10f + rand.randomF(-1f, 1f)
            val z = centers[stack].y * 10f + rand.randomF(-1f, 1f)
            val y = layer * 5f + 10f

            val type = types[rand.randomI(types.indices)]
            val shapes = type.generateShapes(shapeGenCtx)

            val body = RigidDynamic(shapes.mass)
            shapes.primitives.forEach { s ->
                body.attachShape(s)
                // after shape is attached, geometry can be released
                s.geometry.release()
            }
            body.position = Vec3f(x, y, z)
            body.setRotation(Mat3f().rotate(rand.randomF(-90f, 90f), rand.randomF(-90f, 90f), rand.randomF(-90f, 90f)))
            physicsWorld?.addActor(body)

            val coloredBody = ColoredBody(body, color, type, shapes)
            bodies += coloredBody
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
        val frame = mutableListOf<RigidStatic>()
        val frameSimFilter = FilterData().apply {
            setCollisionGroup(1)
            clearCollidesWith(1)
        }

        val groundMaterial = Material(0.5f, 0.5f, 0.2f)
        val groundShape = BoxGeometry(Vec3f(100f, 1f, 100f))
        val ground = RigidStatic().apply {
            attachShape(Shape(groundShape, groundMaterial))
            position = Vec3f(0f, -0.5f, 0f)
            setSimulationFilterData(frameSimFilter)
        }
        physicsWorld.addActor(ground)

        val frameLtShape = BoxGeometry(Vec3f(3f, 6f, 100f))
        val frameLt = RigidStatic().apply {
            attachShape(Shape(frameLtShape, groundMaterial))
            position = Vec3f(-51.5f, 2f, 0f)
            setSimulationFilterData(frameSimFilter)
        }
        physicsWorld.addActor(frameLt)
        frame += frameLt

        val frameRtShape = BoxGeometry(Vec3f(3f, 6f, 100f))
        val frameRt = RigidStatic().apply {
            attachShape(Shape(frameRtShape, groundMaterial))
            position = Vec3f(51.5f, 2f, 0f)
            setSimulationFilterData(frameSimFilter)
        }
        physicsWorld.addActor(frameRt)
        frame += frameRt

        val frameFtShape = BoxGeometry(Vec3f(106f, 6f, 3f))
        val frameFt = RigidStatic().apply {
            attachShape(Shape(frameFtShape, groundMaterial))
            position = Vec3f(0f, 2f, 51.5f)
            setSimulationFilterData(frameSimFilter)
        }
        physicsWorld.addActor(frameFt)
        frame += frameFt

        val frameBkShape = BoxGeometry(Vec3f(106f, 6f, 3f))
        val frameBk = RigidStatic().apply {
            attachShape(Shape(frameBkShape, groundMaterial))
            position = Vec3f(0f, 2f, -51.5f)
            setSimulationFilterData(frameSimFilter)
        }
        physicsWorld.addActor(frameBk)
        frame += frameBk


        val groundAlbedo = Texture2d("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine.png")
        val groundNormal = Texture2d("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine_normal.png")
        onDispose += {
            groundAlbedo.dispose()
            groundNormal.dispose()
        }

        // render textured ground box
        +textureMesh(isNormalMapped = true) {
            generate {
                vertexModFun = {
                    texCoord.set(x / 10, z / 10)
                }
                cube {
                    size.set(groundShape.size)
                    origin.set(size).scale(-0.5f).add(ground.position)
                }
            }
            shader = pbrShader {
                roughness = 0.75f
                shadowMaps += shadows
                useImageBasedLighting(ibl)
                useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                useAlbedoMap(groundAlbedo)
                useNormalMap(groundNormal)
            }
        }

        // render frame
        +colorMesh {
            generate {
                frame.forEach {
                    val shape = it.shapes[0].geometry as BoxGeometry
                    cube {
                        size.set(shape.size)
                        origin.set(size).scale(-0.5f).add(it.position)
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

    private fun instancedBodyShader(ibl: EnvironmentMaps): PbrShader {
        val cfg = PbrMaterialConfig().apply {
            roughness = 1f
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

    private class ColoredBody(val rigidActor: RigidActor, val color: MutableColor, val shapeType: ShapeType, bodyShapes: ShapeType.CollisionShapes) {
        val scale = MutableVec3f()

        init {
            when (val shape = rigidActor.shapes[0].geometry) {
                is BoxGeometry -> {
                    if (rigidActor.shapes.size == 1) {
                        // Box
                        scale.set(shape.size)
                    } else {
                        // Multi shape
                        val s = shape.size.z / 2f
                        scale.set(s, s, s)
                    }
                }
                is CapsuleGeometry -> scale.set(shape.radius, shape.radius, shape.radius)
                is ConvexMeshGeometry -> {
                    val s = bodyShapes.scale
                    scale.set(s, s, s)
                }
                is CylinderGeometry -> scale.set(shape.length, shape.radius, shape.radius)
                is SphereGeometry -> scale.set(shape.radius, shape.radius, shape.radius)
            }
        }
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
        section("Physics") {
            cycler("Body Shape:", shapeType) { _, _ -> }
            sliderWithValue("Number of Bodies:", numSpawnBodies.toFloat(), 50f, 5000f, 0) {
                numSpawnBodies = value.toInt()
            }
            sliderWithValue("Friction:", friction, 0f, 2f, 2) {
                friction = value
            }
            sliderWithValue("Restitution:", restitution, 0f, 1f, 2) {
                restitution = value
            }
            gap(10f)
            button("Apply") { resetPhysics() }
            gap(10f)
        }
        section("Performance") {
            textWithValue("Physics:", "0.00 ms").apply {
                onUpdate += {
                    text = "${physicsWorld?.cpuTime?.toString(2)} ms"
                }
            }
            textWithValue("Time Factor:", "1.00 x").apply {
                onUpdate += {
                    text = "${physicsWorld?.timeFactor?.toString(2)} x"
                }
            }
        }
    }
}