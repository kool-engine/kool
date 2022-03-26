package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.AssetManager
import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.controlUi
import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.ksl.blinnPhongShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.character.CharacterController
import de.fabmax.kool.physics.character.CharacterControllerManager
import de.fabmax.kool.physics.character.CharacterProperties
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.geometry.HeightField
import de.fabmax.kool.physics.geometry.HeightFieldGeometry
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.physics.util.CharacterTrackingCamRig
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.pipeline.shading.unlitShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import kotlin.math.*

class TerrainDemo : DemoScene("Terrain Demo") {

    private lateinit var heightMap: HeightMap
    private lateinit var colorTex: Texture2d
    private lateinit var normalTex: Texture2d
    private lateinit var ibl: EnvironmentMaps

    private lateinit var heightFieldBody: RigidStatic
    private val dynBodies = mutableListOf<RigidDynamic>()
    private val startTransforms = mutableListOf<Mat4f>()

    private val bodySize = 2f

    private lateinit var world: PhysicsWorld

    private lateinit var player: Model
    private lateinit var charManager: CharacterControllerManager
    private lateinit var egoCharacter: CharacterController
    private lateinit var escKeyListener: InputManager.KeyEventListener
    private lateinit var characterCam: CharacterTrackingCamRig
    private lateinit var walkAxes: WalkAxes

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        heightMap = HeightMap.fromRawData(loadAsset("${Demo.heightMapPath}/terrain.raw")!!, 200f)
        // more or less the same, but falls back to 8-bit height-resolution in javascript
        //heightMap = HeightMap.fromTextureData2d(loadTextureData2d("${Demo.heightMapPath}/terrain.png", TexFormat.R_F16), 20f)

        colorTex = loadAndPrepareTexture("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine.png")
        normalTex = loadAndPrepareTexture("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine_normal.png")

        ibl = EnvironmentHelper.hdriEnvironment(mainScene, "${Demo.envMapBasePath}/blaubeuren_outskirts_1k.rgbe.png", this)
        player = loadGltfModel("models/player.glb") ?: throw IllegalStateException("Failed loading model")

        Physics.awaitLoaded()
        world = PhysicsWorld(isContinuousCollisionDetection = true)
        world.registerHandlers(mainScene)
        world.gravity = Vec3f(0f, -10f, 0f)

        val heightField = HeightField(heightMap, 1f, 1f)
        val hfGeom = HeightFieldGeometry(heightField)
        val hfBounds = hfGeom.getBounds(BoundingBox())
        heightFieldBody = RigidStatic()
        heightFieldBody.attachShape(Shape(hfGeom, Physics.defaultMaterial))
        heightFieldBody.position = Vec3f(hfBounds.size.x * -0.5f, 0f, hfBounds.size.z * -0.5f)
        world.addActor(heightFieldBody)

        val groundPlane = RigidStatic()
        groundPlane.attachShape(Shape(PlaneGeometry(), Physics.defaultMaterial))
        groundPlane.position = Vec3f(0f, -5f, 0f)
        groundPlane.setRotation(Mat3f().rotate(90f, Vec3f.Z_AXIS))
        world.addActor(groundPlane)

        val n = 15
        for (x in -n..n) {
            for (z in -n..n) {
                val shape = BoxGeometry(Vec3f(bodySize))
                val body = RigidDynamic(100f)
                body.attachShape(Shape(shape, Physics.defaultMaterial))
                body.position = Vec3f(x * 5.5f, 100f, z * 5.5f)
                world.addActor(body)
                dynBodies += body

                startTransforms += Mat4f().set(body.transform)
            }
        }

        walkAxes = WalkAxes(ctx)
        charManager = CharacterControllerManager(world)
        escKeyListener = ctx.inputMgr.registerKeyListener(InputManager.KEY_ESC, "Exit cursor lock") {
            ctx.inputMgr.cursorMode = InputManager.CursorMode.NORMAL
        }
    }

    override fun dispose(ctx: KoolContext) {
        colorTex.dispose()
        normalTex.dispose()

        charManager.release()
        world.release()

        walkAxes.dispose(ctx)
        ctx.inputMgr.removeKeyListener(escKeyListener)
        ctx.inputMgr.cursorMode = InputManager.CursorMode.NORMAL
    }

    override fun setupMenu(ctx: KoolContext) = controlUi {
        button("Respawn Boxes") {
            dynBodies.forEachIndexed { i, body ->
                body.setTransform(startTransforms[i])
                body.linearVelocity = Vec3f.ZERO
                body.angularVelocity = Vec3f.ZERO
            }
        }
        button("Lock Cursor") {
            characterCam.isCursorLocked = true
            println("pos: ${egoCharacter.position}")
            println("dir: ${characterCam.lookDirection}")
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        lighting.apply {
            singleLight {
                setDirectional(Vec3f(-1f, -1f, -1f))
                setColor(Color.WHITE, 1f)
            }
        }
        val shadowMap = CascadedShadowMap(this@setupMainScene, 0, 200f).apply {
            setMapRanges(0.05f, 0.25f, 1f)
            cascades.forEach { it.directionalCamNearOffset = -200f }
        }

        // height map / ground mesh
        +textureMesh(isNormalMapped = true) {
            generate {
                vertexModFun = {
                    texCoord.set(texCoord.x * 100f, texCoord.y * 100f)
                }
                val shape = heightFieldBody.shapes[0]
                withTransform {
                    transform.set(heightFieldBody.transform).mul(shape.localPose)
                    shape.geometry.generateMesh(this)
                }
            }

            shader = blinnPhongShader {
                color {
                    addTextureColor(colorTex)
                }
                normalMapping {
                    setNormalMap(normalTex)
                }
                shadow {
                    addShadowMap(shadowMap)
                }
                pipeline {
                    cullMethod = CullMethod.NO_CULLING
                }
                imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
                specularStrength = 0.5f
                colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
            }
        }

        // dynamic bodies
        +colorMesh {
            generate {
                color = MdColor.LIGHT_BLUE.toLinear()
                cube {
                    size.set(Vec3f(bodySize))
                    centered()
                }
            }
            shader = blinnPhongShader {
                color {
                    addVertexColor()
                }
                shadow {
                    addShadowMap(shadowMap)
                }
                imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
                isInstanced = true
                specularStrength = 0.5f
                colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
            }

            instances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT))
            onUpdate += {
                instances!!.apply {
                    clear()
                    addInstances(dynBodies.size) { buf ->
                        dynBodies.forEach { body ->
                            buf.put(body.transform.matrix)
                        }
                    }
                }
            }
        }

        spawnCharacter()
        // camera needs spawned character
        setupCamera(ctx)
    }

    private fun Scene.setupCamera(ctx: KoolContext) {
//        defaultCamTransform().apply {
//            zoom = 30.0
//            maxZoom = 50.0
//        }

        characterCam = CharacterTrackingCamRig(ctx.inputMgr).apply {
            +camera
            camera.setClipRange(0.5f, 1000f)
            pivotPoint.set(0.8f, 0f, 0f)
            trackedPose = egoCharacter.actor.transform

            //lookDirection.set(0.8f, 0.5f, 0.3f).norm()
            lookDirection.set(-0.87f, 0.22f, 0.44f).norm()

            // 1st person
            //camera.lookAt.set(Vec3f.NEG_Z_AXIS)
            //camera.position.set(Vec3f.ZERO)

            // 3rd person
            camera.lookAt.set(0.2f, 0.0f, 0f)
            camera.position.set(0.2f, 0.5f, 5f)

        }
        +characterCam
    }

    private fun spawnCharacter() {
        val charProps = CharacterProperties()
        egoCharacter = charManager.createController(charProps)
        //egoCharacter.position = Vec3d(123.0, 71.0, 74.0)
        //egoCharacter.position = Vec3d(86.2, 87.8, -56.9)
        egoCharacter.position = Vec3d(-146.5, 47.8, -89.0)

        mainScene += makeCharModel(charProps)
    }

    private fun makeCharModel(charProps: CharacterProperties) = group {
        +player

        // player model has origin below the model, but we need it centered
        player.translate(0f, -0.9f, 0f)
        player.rotate(180f, Vec3f.Y_AXIS)

        val idleAnimation = 0
        val runAnimation = 1
        val walkAnimation = 2
        player.enableAnimation(walkAnimation)

        +lineMesh {
            isCastingShadow = false
            val cr = MdColor.RED
            val cg = MdColor.GREEN
            val cb = MdColor.BLUE
            val r = charProps.radius + charProps.contactOffset
            val h = charProps.height / 2
            for (i in 0 until 40) {
                val a0 = (i / 40f) * 2 * PI.toFloat()
                val a1 = ((i + 1) / 40f) * 2 * PI.toFloat()
                addLine(Vec3f(cos(a0) * r, h, sin(a0) * r), Vec3f(cos(a1) * r, h, sin(a1) * r), cg)
                addLine(Vec3f(cos(a0) * r, -h, sin(a0) * r), Vec3f(cos(a1) * r, -h, sin(a1) * r), cg)
            }

            for (i in 0 until 20) {
                val a0 = (i / 40f) * 2 * PI.toFloat()
                val a1 = ((i + 1) / 40f) * 2 * PI.toFloat()
                addLine(Vec3f(cos(a0) * r, sin(a0) * r + h, 0f), Vec3f(cos(a1) * r, sin(a1) * r + h, 0f), cr)
                addLine(Vec3f(cos(a0) * r, -sin(a0) * r - h, 0f), Vec3f(cos(a1) * r, -sin(a1) * r - h, 0f), cr)

                addLine(Vec3f(0f, sin(a0) * r + h, cos(a0) * r), Vec3f(0f, sin(a1) * r + h, cos(a1) * r), cb)
                addLine(Vec3f(0f, -sin(a0) * r - h, cos(a0) * r), Vec3f(0f, -sin(a1) * r - h, cos(a1) * r), cb)
            }

            addLine(Vec3f(-r, h, 0f), Vec3f(-r, -h, 0f), cr)
            addLine(Vec3f(r, h, 0f), Vec3f(r, -h, 0f), cr)
            addLine(Vec3f(0f, h, -r), Vec3f(0f, -h, -r), cb)
            addLine(Vec3f(0f, h, r), Vec3f(0f, -h, r), cb)

            shader = unlitShader {
                lineWidth = 2f
            }
        }

        onUpdate += { ev ->
            var heading = atan2(characterCam.lookDirection.x, -characterCam.lookDirection.z).toDeg()
            val walkDir = Vec2f(-walkAxes.leftRight, walkAxes.forwardBackward)
            if (walkDir.length() > 0f) {
                heading += atan2(walkDir.x, walkDir.y).toDeg()
            }

            setIdentity()
            translate(egoCharacter.position)
            rotate(heading, Vec3f.Y_AXIS)

            val walkSpeed = 1.3f
            val crouchSpeed = 0.5f
            val runSpeed = 5f

            val speedFactor = max(abs(walkAxes.forwardBackward), abs(walkAxes.leftRight))
            var speed = walkSpeed * speedFactor
            if (walkAxes.runFactor > 0f) {
                speed = speed * (1f - walkAxes.runFactor) + runSpeed * speedFactor * walkAxes.runFactor
            }
            if (walkAxes.crouchFactor > 0f) {
                speed = speed * (1f - walkAxes.crouchFactor) + crouchSpeed * speedFactor * walkAxes.crouchFactor
            }

            egoCharacter.moveVelocity.set(0f, 0f, -speed)
            egoCharacter.moveVelocity.rotate(heading, Vec3f.Y_AXIS)
            egoCharacter.jump = walkAxes.isJump

            // determine which animation to use based on speed
            if (abs(speed) <= walkSpeed) {
                val w = (abs(speed) / walkSpeed).clamp(0f, 1f)
                player.setAnimationWeight(walkAnimation, w)
                player.setAnimationWeight(idleAnimation, 1f - w)
                player.setAnimationWeight(runAnimation, 0f)

            } else {
                val w = ((abs(speed) - walkSpeed) / (runSpeed - walkSpeed)).clamp(0f, 1f)
                player.setAnimationWeight(runAnimation, w)
                player.setAnimationWeight(walkAnimation, 1f - w)
                player.setAnimationWeight(idleAnimation, 0f)
            }
            player.applyAnimation(ev.deltaT * sign(speed))
        }
    }
}