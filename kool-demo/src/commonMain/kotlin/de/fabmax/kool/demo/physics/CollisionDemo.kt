package de.fabmax.kool.demo.physics

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Cycler
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.controlUi
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

    private val shapes = Cycler(*BodyShape.values()).apply { index = 6 }

    private var numSpawnBodies = 450
    private var friction = 0.5f
    private var restitution = 0.2f
    private var physicsWorld: PhysicsWorld? = null
    private val bodies = mutableListOf<ColoredBody>()

    private var material = Material(friction, friction, restitution)

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
            makeGround(ibl, physicsWorld)

            resetPhysics()

            shapes.forEach {
                if (it != BodyShape.MIXED) {
                    it.mesh.shader = instancedBodyShader(ibl)
                    +it.mesh
                }
            }

            val matBuf = Mat4f()
            val removeBodies = mutableListOf<ColoredBody>()
            onUpdate += {
                physicsWorld.stepPhysics(it.deltaT)

                for (i in shapes.indices) {
                    shapes[i].instances.clear()
                }

                bodies.forEach { body ->
                    if (body.rigidActor.position.length() > 500f) {
                        removeBodies += body
                    } else {
                        matBuf.set(body.rigidActor.transform).scale(body.scale)
                        body.shape.instances.addInstance {
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
                    }
                    removeBodies.clear()
                }
            }

            +Skybox.cube(ibl.reflectionMap, 1f)
        }
    }

    private fun resetPhysics() {
        bodies.forEach {
            physicsWorld?.removeActor(it.rigidActor)
        }
        bodies.clear()

        val types = if (shapes.current == BodyShape.MIXED) {
            BodyShape.values().toList().filter { it != BodyShape.MIXED }
        } else {
            listOf(shapes.current)
        }

        material.release()
        material = Material(friction, friction, restitution)

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
            val (shape, mass) = type.generateShape(rand)

            val body = RigidDynamic(mass)
            shape.geoms.forEachIndexed { si, s ->
                body.attachShape(Shape(s, material, shape.poses[si]))
            }
            body.position = Vec3f(x, y, z)
            body.setRotation(Mat3f().rotate(rand.randomF(-90f, 90f), rand.randomF(-90f, 90f), rand.randomF(-90f, 90f)))
            physicsWorld?.addActor(body)
            bodies += ColoredBody(body, color, type)
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


        val groundAlbedo = Texture2d("${Demo.pbrBasePath}/tile_flat/tiles_flat_gray.png")
        val groundNormal = Texture2d("${Demo.pbrBasePath}/tile_flat/tiles_flat_normal.png")
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

    private class ColoredBody(val rigidActor: RigidActor, val color: MutableColor, val shape: BodyShape) {
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
                    val s = shape.points[0].length()
                    scale.set(s, s, s)
                }
                is CylinderGeometry -> scale.set(shape.length, shape.radius, shape.radius)
                is SphereGeometry -> scale.set(shape.radius, shape.radius, shape.radius)
            }
        }
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
        section("Physics") {
            cycler("Body Shape:", shapes) { _, _ -> }
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

    private class ShapeGeometries() {
        val geoms = mutableListOf<CollisionGeometry>()
        val poses = mutableListOf<Mat4f>()

        constructor(geom: CollisionGeometry) : this() {
            geoms += geom
            poses += Mat4f()
        }

        fun addShape(geom: CollisionGeometry, pose: Mat4f) {
            geoms += geom
            poses += pose
        }
    }

    private enum class BodyShape {
        BOX {
            override val label = "Box"

            override fun MeshBuilder.generateMesh() = bevelBox()

            override fun generateShape(rand: Random): Pair<ShapeGeometries, Float> {
                val shape = BoxGeometry(Vec3f(rand.randomF(2f, 3f), rand.randomF(2f, 3f), rand.randomF(2f, 3f)))
                val mass = shape.size.x * shape.size.y * shape.size.z
                return ShapeGeometries(shape) to mass
            }
        },

        CAPSULE {
            override val label = "Capsule"

            override fun MeshBuilder.generateMesh() = capsule()

            override fun generateShape(rand: Random): Pair<ShapeGeometries, Float> {
                val s = rand.randomF(0.75f, 1.5f)
                val shape = CapsuleGeometry(2.5f * s, s)
                val mass = shape.radius.pow(3)
                return ShapeGeometries(shape) to mass
            }
        },

        CONVEX_HULL {
            override val label = "Convex Hull"

            override fun MeshBuilder.generateMesh() = flatIcoSphere()

            override fun generateShape(rand: Random): Pair<ShapeGeometries, Float> {
                val s = rand.randomF(1.25f, 2.5f)
                val icoPoints = mutableListOf<Vec3f>()
                mesh.geometry.forEach { icoPoints.add(it.position.scale(s, MutableVec3f())) }
                val shape = ConvexMeshGeometry(icoPoints)
                val mass = s.pow(3)
                return ShapeGeometries(shape) to mass
            }
        },

        CYLINDER {
            override val label = "Cylinder"

            override fun MeshBuilder.generateMesh() = cylinder()

            override fun generateShape(rand: Random): Pair<ShapeGeometries, Float> {
                val shape = CylinderGeometry(rand.randomF(2f, 4f), rand.randomF(1f, 2f))
                val mass = shape.radius.pow(2) * shape.length * 0.5f
                return ShapeGeometries(shape) to mass
            }
        },

        MULTI_SHAPE {
            override val label = "Multi Shape"

            override fun MeshBuilder.generateMesh() {
                cube {
                    size.set(0.5f, 0.5f, 2f)
                    origin.set(size).scale(-0.5f).add(Vec3f(1f, 0f, 0f))
                }
                cube {
                    size.set(0.5f, 0.5f, 2f)
                    origin.set(size).scale(-0.5f).add(Vec3f(-1f, 0f, 0f))
                }
                cube {
                    size.set(2.5f, 0.5f, 0.5f)
                    origin.set(size).scale(-0.5f).add(Vec3f(0f, 0f, 1.25f))
                }
                cube {
                    size.set(2.5f, 0.5f, 0.5f)
                    origin.set(size).scale(-0.5f).add(Vec3f(0f, 0f, -1.25f))
                }
            }

            override fun generateShape(rand: Random): Pair<ShapeGeometries, Float> {
                val s = rand.randomF(1f, 2f)
                val geoms = ShapeGeometries()

                val box1 = BoxGeometry(MutableVec3f(0.5f, 0.5f, 2f).scale(s))
                geoms.addShape(box1, Mat4f().translate(1f * s, 0f, 0f))

                val box2 = BoxGeometry(MutableVec3f(0.5f, 0.5f, 2f).scale(s))
                geoms.addShape(box2, Mat4f().translate(-1f * s, 0f, 0f))

                val box3 = BoxGeometry(MutableVec3f(2.5f, 0.5f, 0.5f).scale(s))
                geoms.addShape(box3, Mat4f().translate(0f, 0f, 1.25f * s))

                val box4 = BoxGeometry(MutableVec3f(2.5f, 0.5f, 0.5f).scale(s))
                geoms.addShape(box4, Mat4f().translate(0f, 0f, -1.25f * s))

                val mass = 8 * s.pow(3)
                return geoms to mass
            }
        },

        SPHERE {
            override val label = "Sphere"

            override fun MeshBuilder.generateMesh() {
                icoSphere { steps = 2 }
            }

            override fun generateShape(rand: Random): Pair<ShapeGeometries, Float> {
                val shape = SphereGeometry(rand.randomF(1.25f, 2.5f))
                val mass = shape.radius.pow(3)
                return ShapeGeometries(shape) to mass
            }
        },

        MIXED {
            override val label = "Mixed"

            override fun MeshBuilder.generateMesh() { }

            override fun generateShape(rand: Random): Pair<ShapeGeometries, Float> {
                throw IllegalStateException()
            }
        };

        abstract val label: String
        val instances = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT, Attribute.COLORS))
        val mesh = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS)) {
            isFrustumChecked = false
            instances = this@BodyShape.instances
            generate {
                generateMesh()
            }
        }

        abstract fun MeshBuilder.generateMesh()
        abstract fun generateShape(rand: Random): Pair<ShapeGeometries, Float>

        override fun toString(): String {
            return label
        }

        fun MeshBuilder.bevelBox(rBevel: Float = 0.02f) {
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

        fun MeshBuilder.capsule(halfHeight: Float = 1.25f, radius: Float = 1f) {
            profile {
                simpleShape(false) {
                    xyArc(Vec2f(halfHeight + radius, 0f), Vec2f(halfHeight, 0f), 90f, 10, true)
                    xyArc(Vec2f(-halfHeight, radius), Vec2f(-halfHeight, 0f), 90f, 10, true)
                }
                for (i in 0 .. 20) {
                    sample()
                    rotate(360f / 20, 0f, 0f)
                }
            }
        }

        fun MeshBuilder.flatIcoSphere() {
            val icoMesh = MeshBuilder(IndexedVertexList(Attribute.POSITIONS)).apply { icoSphere { steps = 0 } }
            for (i in 0 until icoMesh.geometry.numIndices step 3) {
                val vIt = icoMesh.geometry.vertexIt
                vIt.index = icoMesh.geometry.indices[i]
                val i0 = geometry.addVertex(vIt)
                vIt.index = icoMesh.geometry.indices[i + 1]
                val i1 = geometry.addVertex(vIt)
                vIt.index = icoMesh.geometry.indices[i + 2]
                val i2 = geometry.addVertex(vIt)
                geometry.addTriIndices(i0, i1, i2)
            }
            geometry.generateNormals()
        }

        fun MeshBuilder.cylinder(height: Float = 1f, radius: Float = 1f) {
            // make a beveled cylinder which looks a bit nicer than a simple one with sharp edges
            profile {
                simpleShape(false) {
                    val bevelSteps = 3
                    val bevelR = 0.02f
                    for (i in 0..bevelSteps) {
                        val a = (i / bevelSteps.toFloat() * PI / 2 + PI * 1.5).toFloat()
                        val x = cos(a)
                        val y = sin(a)
                        xy(radius - bevelR + x * bevelR, height * -0.5f + bevelR + y * bevelR)
                        normals += MutableVec3f(x, y, 0f)
                    }
                    for (i in 0..bevelSteps) {
                        val a = (i / bevelSteps.toFloat() * PI / 2).toFloat()
                        val x = cos(a)
                        val y = sin(a)
                        xy(radius - bevelR + x * bevelR, height * 0.5f - bevelR + y * bevelR)
                        normals += MutableVec3f(x, y, 0f)
                    }
                }

                val topVertInds = mutableListOf<Int>()
                val bottomVertInds = mutableListOf<Int>()
                val topVerts = mutableListOf<Vec3f>()
                val bottomVerts = mutableListOf<Vec3f>()
                rotate(90f, Vec3f.Z_AXIS)
                for (i in 0 .. 40) {
                    rotate(0f, -360f / 40, 0f)
                    sample()
                    geometry.vertexIt.apply {
                        index = shapes[0].sampledVertIndices.first()
                        bottomVertInds += index
                        bottomVerts += Vec3f(position)
                    }
                    geometry.vertexIt.apply {
                        index = shapes[0].sampledVertIndices.last()
                        topVertInds += index
                        topVerts += Vec3f(position)
                    }
                }

                val topPoly = PolyUtil.fillPolygon(topVerts)
                for (i in topPoly.indices.indices step 3) {
                    geometry.addTriIndices(topVertInds[topPoly.indices[i]],
                        topVertInds[topPoly.indices[i + 2]], topVertInds[topPoly.indices[i + 1]])
                }

                val bottomPoly = PolyUtil.fillPolygon(bottomVerts)
                for (i in bottomPoly.indices.indices step 3) {
                    geometry.addTriIndices(bottomVertInds[bottomPoly.indices[i]],
                        bottomVertInds[bottomPoly.indices[i + 1]], bottomVertInds[bottomPoly.indices[i + 2]])
                }
            }
        }
    }
}