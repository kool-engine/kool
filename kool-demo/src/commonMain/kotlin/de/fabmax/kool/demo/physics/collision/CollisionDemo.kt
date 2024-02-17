package de.fabmax.kool.demo.physics.collision

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxD
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

class CollisionDemo : DemoScene("Physics - Collision") {

    private val ibl by hdriImage("${DemoLoader.hdriPath}/colorful_studio_1k.rgbe.png")
    private val groundAlbedo by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine.png")
    private val groundNormal by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine_normal.png")

    private lateinit var aoPipeline: AoPipeline
    private val shadows = mutableListOf<ShadowMap>()

    private val shapeMeshes = mutableMapOf<ShapeType, Mesh>()

    private val shapeTypes = ShapeType.entries
    private val selectedShapeType = mutableStateOf(6)
    private val numSpawnBodies = mutableStateOf(450)
    private val drawBodyState = mutableStateOf(false)
    private val friction = mutableStateOf(0.5f)
    private val restitution = mutableStateOf(0.2f)
    private var material: Material = Material(friction.value, friction.value, restitution.value)

    private val physicsTimeTxt = mutableStateOf("0.00 ms")
    private val activeActorsTxt = mutableStateOf("0")
    private val timeFactorTxt = mutableStateOf("1.00 x")

    private val physicsStepper = ConstantPhysicsStepperSync()
    private val physicsWorld: PhysicsWorld = PhysicsWorld(mainScene).apply {
        simStepper = physicsStepper
    }
    private val bodies = mutableMapOf<ShapeType, MutableList<ColoredBody>>()

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera(yaw = 10f, pitch = -40f).apply {
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_TRANSLATE
            panMethod = yPlanePan()
            translationBounds = BoundingBoxD(Vec3d(-50.0), Vec3d(50.0))
            minHorizontalRot = -90.0
            maxHorizontalRot = 0.0
            setZoom(75.0, min = 10.0)
        }

        (camera as PerspectiveCamera).apply {
            clipNear = 0.5f
            clipFar = 500f
        }

        lighting.singleDirectionalLight {
            setup(Vec3f(0.8f, -1.2f, 1f))
        }

        val shadowMap = CascadedShadowMap(this, lighting.lights[0], maxRange = 300f)
        shadows.add(shadowMap)
        aoPipeline = AoPipeline.createForward(this)

        shapeTypes.forEach {
            if (it != ShapeType.MIXED) {
                val mesh = it.createMesh()
                shapeMeshes[it] = mesh
                addNode(mesh)
            }
        }
        makeGround(physicsWorld)
        resetPhysics()

        val matBuf = MutableMat4f()
        val removeBodies = mutableListOf<ColoredBody>()
        onUpdate += {
            shapeMeshes.values.forEach {
                it.instances!!.clear()
            }

            val activeColor = MutableColor(MdColor.RED.toLinear())
            val inactiveColor = MutableColor(MdColor.LIGHT_GREEN.toLinear())

            bodies.forEach { (type, typeBodies) ->
                shapeMeshes[type]!!.instances!!.addInstances(typeBodies.size) { buf ->
                    for (i in typeBodies.indices) {
                        val body = typeBodies[i]
                        matBuf.set(body.rigidActor.transform.matrixF).scale(body.scale)
                        matBuf.putTo(buf)

                        if (drawBodyState.value) {
                            if (body.rigidActor.isActive) {
                                activeColor.putTo(buf)
                            } else {
                                inactiveColor.putTo(buf)
                            }
                        } else {
                            body.color.putTo(buf)
                        }

                        if (body.rigidActor.position.length() > 500f) {
                            removeBodies += body
                        }
                    }
                }
                if (removeBodies.isNotEmpty()) {
                    removeBodies.forEach { body ->
                        logI { "Removing out-of-range body" }
                        typeBodies.remove(body)
                        physicsWorld.removeActor(body.rigidActor)
                        body.rigidActor.release()
                    }
                    removeBodies.clear()
                }
            }

            physicsTimeTxt.set("${physicsStepper.perfCpuTime.toString(2)} ms")
            activeActorsTxt.set("${physicsWorld.activeActors}")
            timeFactorTxt.set("${physicsStepper.perfTimeFactor.toString(2)} x")
        }

        addNode(Skybox.cube(ibl.reflectionMap, 1.5f))
    }

    private fun ShapeType.createMesh(): Mesh {
        val mesh = Mesh(
            attributes = listOf(Attribute.POSITIONS, Attribute.NORMALS),
            instances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS), 2000)
        )
        mesh.generate { generateShapeMesh() }
        mesh.shader = instancedBodyShader()
        return mesh
    }


    override fun onRelease(ctx: KoolContext) {
        physicsWorld.release()
        material.release()
    }

    private fun resetPhysics() {
        bodies.values.forEach { typedBodies ->
            typedBodies.forEach {
                physicsWorld.removeActor(it.rigidActor)
                it.rigidActor.release()
            }
        }
        bodies.clear()

        val types = if (shapeTypes[selectedShapeType.value] == ShapeType.MIXED) {
            ShapeType.entries.filter { it != ShapeType.MIXED }
        } else {
            listOf(shapeTypes[selectedShapeType.value])
        }

        material.release()
        material = Material(friction.value, friction.value, restitution.value)

        val stacks = max(1, numSpawnBodies.value / 50)
        val centers = makeCenters(stacks)

        val rand = Random(39851564)
        for (i in 0 until numSpawnBodies.value) {
            val layer = i / stacks
            val stack = i % stacks
            val color = MdColor.PALETTE[layer % MdColor.PALETTE.size].toLinear()

            val x = centers[stack].x * 10f + rand.randomF(-1f, 1f)
            val z = centers[stack].y * 10f + rand.randomF(-1f, 1f)
            val y = layer * 5f + 10f

            val type = types[rand.randomI(types.indices)]
            val mesh = shapeMeshes[type]!!
            val shapes = type.generatePhysicsShapes(mesh.geometry, material, rand)

            val body = RigidDynamic(shapes.mass)
            shapes.primitives.forEach { s ->
                body.attachShape(s)
                // after shape is attached, geometry can be released
                s.geometry.release()
            }
            body.updateInertiaFromShapesAndMass()
            body.position = Vec3f(x, y, z)
            body.setRotation(MutableMat3f().rotate(rand.randomF(-90f, 90f).deg, rand.randomF(-90f, 90f).deg, rand.randomF(-90f, 90f).deg))
            physicsWorld.addActor(body)

            val coloredBody = ColoredBody(body, color, shapes)
            bodies.getOrPut(type) { mutableListOf() } += coloredBody
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
            dir.rotate(90f.deg)
            if (stepsSteps++ == 2) {
                stepsSteps = 1
                steps++
            }
        }

        return centers
    }

    private fun Scene.makeGround(physicsWorld: PhysicsWorld) {
        val frame = mutableListOf<RigidStatic>()
        val frameSimFilter = FilterData {
            setCollisionGroup(1)
            clearCollidesWith(1)
        }

        val groundMaterial = Material(0.5f, 0.5f, 0.2f)
        val groundShape = BoxGeometry(Vec3f(100f, 1f, 100f))
        val ground = RigidStatic().apply {
            attachShape(Shape(groundShape, groundMaterial))
            position = Vec3f(0f, -0.5f, 0f)
            simulationFilterData = frameSimFilter
        }
        physicsWorld.addActor(ground)

        val frameLtShape = BoxGeometry(Vec3f(3f, 6f, 100f))
        val frameLt = RigidStatic().apply {
            attachShape(Shape(frameLtShape, groundMaterial))
            position = Vec3f(-51.5f, 2f, 0f)
            simulationFilterData = frameSimFilter
        }
        physicsWorld.addActor(frameLt)
        frame += frameLt

        val frameRtShape = BoxGeometry(Vec3f(3f, 6f, 100f))
        val frameRt = RigidStatic().apply {
            attachShape(Shape(frameRtShape, groundMaterial))
            position = Vec3f(51.5f, 2f, 0f)
            simulationFilterData = frameSimFilter
        }
        physicsWorld.addActor(frameRt)
        frame += frameRt

        val frameFtShape = BoxGeometry(Vec3f(106f, 6f, 3f))
        val frameFt = RigidStatic().apply {
            attachShape(Shape(frameFtShape, groundMaterial))
            position = Vec3f(0f, 2f, 51.5f)
            simulationFilterData = frameSimFilter
        }
        physicsWorld.addActor(frameFt)
        frame += frameFt

        val frameBkShape = BoxGeometry(Vec3f(106f, 6f, 3f))
        val frameBk = RigidStatic().apply {
            attachShape(Shape(frameBkShape, groundMaterial))
            position = Vec3f(0f, 2f, -51.5f)
            simulationFilterData = frameSimFilter
        }
        physicsWorld.addActor(frameBk)
        frame += frameBk

        // render textured ground box
        addTextureMesh(isNormalMapped = true) {
            generate {
                vertexModFun = {
                    texCoord.set(x / 10, z / 10)
                }
                cube {
                    size.set(groundShape.size)
                    origin.set(ground.position)
                }
            }
            shader = KslPbrShader {
                color { textureColor(groundAlbedo) }
                normalMapping { setNormalMap(groundNormal) }
                roughness(0.75f)
                enableSsao(aoPipeline.aoMap)
                shadow { addShadowMaps(shadows) }
                imageBasedAmbientColor(ibl.irradianceMap)
                reflectionMap = ibl.reflectionMap
            }
        }

        // render frame
        addColorMesh {
            generate {
                frame.forEach {
                    val shape = it.shapes[0].geometry as BoxGeometry
                    cube {
                        size.set(shape.size)
                        origin.set(it.position)
                    }
                }
            }
            shader = KslPbrShader {
                color { constColor(MdColor.BLUE_GREY toneLin 700) }
                roughness(0.75f)
                enableSsao(aoPipeline.aoMap)
                shadow { addShadowMaps(shadows) }
                imageBasedAmbientColor(ibl.irradianceMap)
                reflectionMap = ibl.reflectionMap
            }
        }
    }

    private fun instancedBodyShader() = KslPbrShader {
        vertices { isInstanced = true }
        color { instanceColor(Attribute.COLORS) }
        roughness(1f)
        enableSsao(aoPipeline.aoMap)
        shadow { addShadowMaps(shadows) }
        imageBasedAmbientColor(ibl.irradianceMap)
        reflectionMap = ibl.reflectionMap
    }

    private class ColoredBody(val rigidActor: RigidActor, val color: MutableColor, bodyShapes: ShapeType.CollisionShapes) {
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

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        MenuRow {
            Text("Body shape") { labelStyle() }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(shapeTypes)
                    .selectedIndex(selectedShapeType.use())
                    .onItemSelected { selectedShapeType.set(it) }
            }
        }

        MenuSlider2("Number of Bodies", numSpawnBodies.use().toFloat(), 50f, 2000f, { "${it.roundToInt()}" }) {
            numSpawnBodies.set(it.roundToInt())
        }
        MenuSlider2("Friction", friction.use(), 0f, 2f) { friction.set(it) }
        MenuSlider2("Restitution", restitution.use(), 0f, 1f) { restitution.set(it) }

        Button("Apply settings") {
            modifier
                .alignX(AlignmentX.Center)
                .width(Grow.Std)
                .margin(horizontal = 16.dp, vertical = 24.dp)
                .onClick { resetPhysics() }
        }

        Text("Statistics") { sectionTitleStyle() }
        LabeledSwitch("Show body state", drawBodyState)
        MenuRow {
            Text("Active actors") { labelStyle(Grow.Std) }
            Text(activeActorsTxt.use()) { labelStyle() }
        }
        MenuRow {
            Text("Physics step CPU time") { labelStyle(Grow.Std) }
            Text(physicsTimeTxt.use()) { labelStyle() }
        }
        MenuRow {
            Text("Time factor") { labelStyle(Grow.Std) }
            Text(timeFactorTxt.use()) { labelStyle() }
        }
    }
}