package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.AssetManager
import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.controlUi
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.ksl.blinnPhongShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.physics.*
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
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.textureMesh
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.HeightMap
import de.fabmax.kool.util.MdColor

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
    private lateinit var player: Player
    private lateinit var escKeyListener: InputManager.KeyEventListener

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        heightMap = HeightMap.fromRawData(loadAsset("${Demo.heightMapPath}/terrain.raw")!!, 200f)
        // more or less the same, but falls back to 8-bit height-resolution in javascript
        //heightMap = HeightMap.fromTextureData2d(loadTextureData2d("${Demo.heightMapPath}/terrain.png", TexFormat.R_F16), 20f)

        colorTex = loadAndPrepareTexture("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine.png")
        normalTex = loadAndPrepareTexture("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine_normal.png")

        ibl = EnvironmentHelper.hdriEnvironment(mainScene, "${Demo.envMapBasePath}/blaubeuren_outskirts_1k.rgbe.png", this)
        val playerModel = loadGltfModel("${Demo.modelBasePath}/player.glb") ?: throw IllegalStateException("Failed loading model")

        Physics.awaitLoaded()
        world = PhysicsWorld(isContinuousCollisionDetection = true)
        world.registerHandlers(mainScene)
        world.gravity = Vec3f(0f, -10f, 0f)

        player = Player(playerModel, world, ctx)
        player.controller.position = Vec3d(-146.5, 47.8, -89.0)

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

        escKeyListener = ctx.inputMgr.registerKeyListener(InputManager.KEY_ESC, "Exit cursor lock") {
            ctx.inputMgr.cursorMode = InputManager.CursorMode.NORMAL
        }
    }

    override fun dispose(ctx: KoolContext) {
        colorTex.dispose()
        normalTex.dispose()

        player.dispose(ctx)
        world.release()

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
            player.camRig?.isCursorLocked = true
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
                color { addVertexColor() }
                shadow { addShadowMap(shadowMap) }
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

        player.isDrawShapeOutline = false
        player.playerModel.meshes.values.forEach {
            it.shader = blinnPhongShader {
                color { addUniformColor(MdColor.LIGHT_BLUE.toLinear()) }
                shadow { addShadowMap(shadowMap) }
                imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
                enableArmature(40)
                specularStrength = 0.5f
                colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
            }
        }

        // setup camera (also controlled by Player)
        player.camRig = CharacterTrackingCamRig(ctx.inputMgr).apply {
            camera.setClipRange(0.5f, 750f)
            trackedPose = player.controller.actor.transform

            +camera
            // 3rd person view: camera is moved behind and slightly to the right of the character
            camera.lookAt.set(0.2f, 0.0f, 0f)
            camera.position.set(0.2f, 0.5f, 5f)

            lookDirection.set(-0.87f, 0.22f, 0.44f).norm()
        }

        // add camera rig to scene
        +player.camRig!!
        // add player (model) to scene
        +player
    }
}