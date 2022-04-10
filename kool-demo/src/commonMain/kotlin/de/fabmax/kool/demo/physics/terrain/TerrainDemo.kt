package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.AssetManager
import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.controlUi
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.ksl.blinnPhongShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.util.CharacterTrackingCamRig
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.textureMesh
import de.fabmax.kool.util.*
import kotlin.math.atan2

class TerrainDemo : DemoScene("Terrain Demo") {

    private lateinit var colorTex: Texture2d
    private lateinit var normalTex: Texture2d
    private lateinit var ibl: EnvironmentMaps
    private lateinit var playerModel: PlayerModel
    private lateinit var camRig: CharacterTrackingCamRig

    private lateinit var physicsObjects: PhysicsObjects

    private lateinit var escKeyListener: InputManager.KeyEventListener

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        val heightMap = HeightMap.fromRawData(loadAsset("${Demo.heightMapPath}/terrain.raw")!!, 200f)
        // more or less the same, but falls back to 8-bit height-resolution in javascript
        //heightMap = HeightMap.fromTextureData2d(loadTextureData2d("${Demo.heightMapPath}/terrain.png", TexFormat.R_F16), 20f)

        colorTex = loadAndPrepareTexture("${Demo.materialPath}/tile_flat/tiles_flat_fine.png")
        normalTex = loadAndPrepareTexture("${Demo.materialPath}/tile_flat/tiles_flat_fine_normal.png")

        ibl = EnvironmentHelper.hdriEnvironment(mainScene, "${Demo.hdriPath}/blaubeuren_outskirts_1k.rgbe.png", this)

        Physics.awaitLoaded()
        physicsObjects = PhysicsObjects(mainScene, heightMap, ctx)

        val playerGltf = loadGltfModel("${Demo.modelPath}/player.glb") ?: throw IllegalStateException("Failed loading model")
        playerModel = PlayerModel(playerGltf, physicsObjects.playerController)

        escKeyListener = ctx.inputMgr.registerKeyListener(InputManager.KEY_ESC, "Exit cursor lock") {
            ctx.inputMgr.cursorMode = InputManager.CursorMode.NORMAL
        }
    }

    override fun dispose(ctx: KoolContext) {
        colorTex.dispose()
        normalTex.dispose()
        physicsObjects.release(ctx)

        ctx.inputMgr.removeKeyListener(escKeyListener)
        ctx.inputMgr.cursorMode = InputManager.CursorMode.NORMAL
    }

    override fun setupMenu(ctx: KoolContext) = controlUi {
        button("ESC to unlock Cursor") {
            camRig.isCursorLocked = true
        }.apply {
            onUpdate += {
                text = if (camRig.isCursorLocked) {
                    "ESC to unlock Cursor"
                } else {
                    "Click here to lock Cursor"
                }
            }
        }
        gap(8f)
        button("Respawn Boxes") {
            physicsObjects.respawnBoxes()
        }
        sliderWithValue("Push Force", physicsObjects.playerController.pushForceFac, 0.1f, 10f) {
            physicsObjects.playerController.pushForceFac = value
        }
        toggleButton("Draw Debug Info", playerModel.isDrawShapeOutline) {
            playerModel.isDrawShapeOutline = isEnabled
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        // lighting
        lighting.apply {
            singleLight {
                setDirectional(Vec3f(-1f, -1f, -1f))
                setColor(Color.WHITE, 1f)
            }
        }
        val shadowMap = CascadedShadowMap(this@setupMainScene, 0, 200f).apply {
            setMapRanges(0.05f, 0.25f, 1f)
            cascades.forEach {
                it.directionalCamNearOffset = -200f
                it.setDefaultDepthOffset(true)
            }
        }

        +makeTerrainMesh(shadowMap)
        +makeBoxMesh(shadowMap)
        +makeBridgeMesh(shadowMap)

        // change player model shader
        playerModel.model.meshes.values.forEach {
            it.shader = blinnPhongShader {
                color { addUniformColor(MdColor.PINK.toLinear()) }
                shadow { addShadowMap(shadowMap) }
                imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
                enableArmature(40)
                specularStrength = 0.5f
                colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
            }
        }
        +playerModel

        // setup camera tracking player
        camRig = CharacterTrackingCamRig(ctx.inputMgr).apply {
            camera.setClipRange(0.5f, 750f)
            trackedPose = physicsObjects.playerController.controller.actor.transform
            +camera
            minZoom = 0.75f
            pivotPoint.set(0.17f, 0.75f, 0f)

            // hardcoded start look direction
            lookDirection.set(-0.87f, 0.22f, 0.44f).norm()

            onUpdate += {
                // use camera look direction to control player move direction
                physicsObjects.playerController.frontHeading = atan2(lookDirection.x, -lookDirection.z).toDeg()
            }
        }
        // don't forget to add the cam rig to the scene
        +camRig
    }

    private fun makeTerrainMesh(shadowMap: ShadowMap) = textureMesh(isNormalMapped = true) {
        generate {
            vertexModFun = {
                texCoord.set(texCoord.x * 100f, texCoord.y * 100f)
            }
            withTransform {
                val shape = physicsObjects.terrain.shapes[0]
                transform.set(physicsObjects.terrain.transform).mul(shape.localPose)
                shape.geometry.generateMesh(this)
            }
        }

        shader = blinnPhongShader {
            color { addTextureColor(colorTex) }
            normalMapping { setNormalMap(normalTex) }
            shadow { addShadowMap(shadowMap) }
            imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
            specularStrength = 0.5f
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
        }
    }

    private fun makeBoxMesh(shadowMap: ShadowMap) = colorMesh {
        generate {
            color = MdColor.LIGHT_BLUE.toLinear()
            physicsObjects.boxes[0].shapes[0].geometry.generateMesh(this)
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
                addInstances(physicsObjects.boxes.size) { buf ->
                    physicsObjects.boxes.forEach { box ->
                        buf.put(box.transform.matrix)
                    }
                }
            }
        }
    }

    private fun makeBridgeMesh(shadowMap: ShadowMap) = colorMesh {
        generate {
            color = MdColor.BROWN toneLin 700
            cube {
                size.set(2f, 0.2f, 1.1f)
                centered()
            }
        }
        shader = blinnPhongShader {
            color { addVertexColor() }
            shadow { addShadowMap(shadowMap) }
            imageBasedAmbientColor(ibl.irradianceMap)
            isInstanced = true
            specularStrength = 0.2f
        }
        instances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT))
        onUpdate += {
            instances?.let { insts ->
                insts.clear()
                insts.addInstances(physicsObjects.chainBridge.segments.size) { buf ->
                    physicsObjects.chainBridge.segments.forEach { seg ->
                        buf.put(seg.transform.matrix)
                    }
                }
            }
        }
    }
}