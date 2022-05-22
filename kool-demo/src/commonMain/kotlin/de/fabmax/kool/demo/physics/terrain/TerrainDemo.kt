package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.AssetManager
import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.controlUi
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.ksl.*
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.util.CharacterTrackingCamRig
import de.fabmax.kool.pipeline.AddressMode
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.*
import kotlin.math.atan2

class TerrainDemo : DemoScene("Terrain Demo") {

    private lateinit var colorMap: Texture2d
    private lateinit var normalMap: Texture2d
    private lateinit var grassColor: Texture2d
    private lateinit var oceanBump: Texture2d
    //private lateinit var ibl: EnvironmentMaps
    private lateinit var shadowMap: ShadowMap
    private lateinit var ssao: AoPipeline.ForwardAoPipeline
    private lateinit var oceanFloorPass: OceanFloorRenderPass
    private lateinit var sky: Sky

    private lateinit var wind: Wind
    private lateinit var terrain: Terrain
    private lateinit var trees: Trees
    private lateinit var grass: Grass
    private lateinit var camLocalGrass: CamLocalGrass
    private lateinit var ocean: Ocean

    private lateinit var playerModel: PlayerModel
    private lateinit var camRig: CharacterTrackingCamRig
    private lateinit var terrainTiles: TerrainTiles
    private lateinit var physicsObjects: PhysicsObjects

    private var boxMesh: Mesh? = null
    private var bridgeMesh: Mesh? = null

    private lateinit var escKeyListener: InputManager.KeyEventListener

    private var isSsao = true

    private var isPlayerPbr = true
    private var isGroundPbr = true
    private var isBridgePbr = true
    private var isBoxesPbr = true
    // trees and grass actually look better with cheap blinn-phong shading
    private var isTreesPbr = false
    private var isGrassPbr = false

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        showLoadText("Loading height map...")
        val heightMap = HeightMap.fromRawData(loadAsset("${Demo.heightMapPath}/terrain_ocean.raw")!!, 200f, heightOffset = -50f)
        // more or less the same, but falls back to 8-bit height-resolution in javascript
        //heightMap = HeightMap.fromTextureData2d(loadTextureData2d("${Demo.heightMapPath}/terrain.png", TexFormat.R_F16), 200f)

        showLoadText("Loading textures...")
        colorMap = loadAndPrepareTexture("${Demo.materialPath}/tile_flat/tiles_flat_fine.png")
        normalMap = loadAndPrepareTexture("${Demo.materialPath}/tile_flat/tiles_flat_fine_normal.png")
        oceanBump = loadAndPrepareTexture("${Demo.materialPath}/ocean-bump-1k.jpg")

        val grassProps = TextureProps(
            addressModeU = AddressMode.CLAMP_TO_EDGE,
            addressModeV = AddressMode.CLAMP_TO_EDGE
        )
        grassColor = loadAndPrepareTexture("${Demo.materialPath}/grass_64.png", grassProps)

        showLoadText("Generating wind density texture...")
        wind = Wind()

        //ibl = EnvironmentHelper.hdriEnvironment(mainScene, "${Demo.hdriPath}/blaubeuren_outskirts_1k.rgbe.png", this)

        sky = Sky(mainScene).apply { generateMaps(this@TerrainDemo, loadingScreen!!, ctx) }
        //ibl = sky.skies.ceilingValue(0.5f)!!.envMaps

        showLoadText("Creating terrain...")
        Physics.awaitLoaded()
        terrain = Terrain(heightMap)
        terrainTiles = TerrainTiles(terrain, sky)
        showLoadText("Creating ocean...")
        oceanFloorPass = OceanFloorRenderPass(mainScene, terrainTiles)
        ocean = Ocean(terrainTiles, mainScene.camera, wind, sky)
        showLoadText("Creating trees...")
        trees = Trees(terrain, 150, wind, sky)
        showLoadText("Creating grass (1/2, may take a bit)...")
        grass = Grass(terrain, wind, sky)
        showLoadText("Creating grass (2/2, may take a bit)...")
        camLocalGrass = CamLocalGrass(mainScene.camera, terrain, wind, sky)

        showLoadText("Creating physics...")
        physicsObjects = PhysicsObjects(mainScene, terrain, trees, ctx)
        boxMesh = if (physicsObjects.boxes.isNotEmpty()) makeBoxMesh() else null
        bridgeMesh = makeBridgeMesh()

        showLoadText("Loading player model...")
        val playerGltf = loadGltfModel("${Demo.modelPath}/player.glb") ?: throw IllegalStateException("Failed loading model")
        playerModel = PlayerModel(playerGltf, physicsObjects.playerController)

        escKeyListener = ctx.inputMgr.registerKeyListener(InputManager.KEY_ESC, "Exit cursor lock") {
            ctx.inputMgr.cursorMode = InputManager.CursorMode.NORMAL
        }
    }

    override fun dispose(ctx: KoolContext) {
        colorMap.dispose()
        normalMap.dispose()
        physicsObjects.release(ctx)

        ctx.inputMgr.removeKeyListener(escKeyListener)
        ctx.inputMgr.cursorMode = InputManager.CursorMode.NORMAL
    }

    override fun setupMenu(ctx: KoolContext) = controlUi {
        //image(oceanFloorPass.colorTexture)

        section("Terrain Demo") {
            button("ESC to unlock Cursor") {
                camRig.isCursorLocked = true
            }.apply {
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
                textColorHovered.setCustom(MdColor.DEEP_ORANGE)
                onUpdate += {
                    text = if (camRig.isCursorLocked) {
                        textColor.setCustom(MdColor.DEEP_ORANGE)
                        "ESC to unlock Cursor"
                    } else {
                        textColor.setCustom(Color.WHITE)
                        "Click here to lock Cursor"
                    }
                }
            }
            text("[WASD / Cursor Keys]: Move").apply { font.setCustom(smallFont); menuY += 10f }
            text("[Shift]: Walk").apply { font.setCustom(smallFont); menuY += 10f }
            text("[Space]: Jump").apply { font.setCustom(smallFont); menuY += 10f }
        }
//        button("Respawn Boxes") {
//            physicsObjects.respawnBoxes()
//        }
//        toggleButton("Draw Debug Info", playerModel.isDrawShapeOutline) {
//            playerModel.isDrawShapeOutline = isEnabled
//            physicsObjects.debugLines.isVisible = isEnabled
//        }
        section("Wind") {
            sliderWithValue("Wind Speed", 2.5f, 0.1f, 20f) {
                wind.speed.set(4f * value, 0.2f * value, 2.7f * value)
            }
            sliderWithValue("Wind Strength", wind.offsetStrength.w, 0f, 2f) {
                wind.offsetStrength.w = value
            }
            sliderWithValue("Wind Scale", wind.scale, 10f, 500f) {
                wind.scale = value
            }
//            sliderWithValue("Time of Day", sky.timeOfDay, 0f, 1f) {
//                sky.timeOfDay = value
//            }
        }
        section("Grass") {
            toggleButton("Grass", grass.grassQuads.isVisible) {
                grass.grassQuads.isVisible = isEnabled
            }
            toggleButton("Cam Local Grass", camLocalGrass.grassQuads.isVisible) {
                camLocalGrass.grassQuads.isVisible = isEnabled
            }
            toggleButton("Grass Shadow Casting", camLocalGrass.grassQuads.isCastingShadow) {
                grass.setIsCastingShadow(isEnabled)
                camLocalGrass.setIsCastingShadow(isEnabled)
            }
        }
        section("Shading") {
            toggleButton("Ambient Occlusion", isSsao) {
                isSsao = isEnabled
                updateSsaoEnabled()
            }
            toggleButton("Player PBR Shading", isPlayerPbr) {
                isPlayerPbr = isEnabled
                updatePlayerShader()
            }
            toggleButton("Ground PBR Shading", isGroundPbr) {
                isGroundPbr = isEnabled
                isBridgePbr = isEnabled
                updateTerrainShader()
                updateOceanShader()
                updateBridgeShader()
            }
            toggleButton("Boxes PBR Shading", isBoxesPbr) {
                isBoxesPbr = isEnabled
                updateBoxShader()
            }
            toggleButton("Vegetation PBR Shading", isTreesPbr) {
                isTreesPbr = isEnabled
                isGrassPbr = isEnabled
                updateGrassShader()
                updateTreeShader()
            }
        }

        uiRoot.apply {
            +container("x1") {
                layoutSpec.setOrigin(pcs(50f) - dps(13f), pcs(50f) - dps(1f), zero())
                layoutSpec.setSize(dps(10f), dps(2f), full())
                ui.setCustom(SimpleComponentUi(this).apply { color.setCustom(Color.WHITE) })
            }
            +container("x2") {
                layoutSpec.setOrigin(pcs(50f) + dps(3f), pcs(50f) - dps(1f), zero())
                layoutSpec.setSize(dps(10f), dps(2f), full())
                ui.setCustom(SimpleComponentUi(this).apply { color.setCustom(Color.WHITE) })
            }
            +container("x3") {
                layoutSpec.setOrigin(pcs(50f) - dps(1f), pcs(50f) - dps(13f), zero())
                layoutSpec.setSize(dps(2f), dps(10f), full())
                ui.setCustom(SimpleComponentUi(this).apply { color.setCustom(Color.WHITE) })
            }
            +container("x4") {
                layoutSpec.setOrigin(pcs(50f) - dps(1f), pcs(50f) + dps(3f), zero())
                layoutSpec.setSize(dps(2f), dps(10f), full())
                ui.setCustom(SimpleComponentUi(this).apply { color.setCustom(Color.WHITE) })
            }
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        //ctx.isProfileRenderPasses = true
        mainRenderPass.clearColor = MdColor.LIGHT_BLUE

        // lighting
        lighting.apply {
            singleLight {
                setDirectional(Vec3f(-1f, -1f, -1f))
                setColor(Color.WHITE, 1f)
            }
        }
        shadowMap = CascadedShadowMap(this@setupMainScene, 0, 200f).apply {
            setMapRanges(0.05f, 0.25f, 1f)
            cascades.forEach {
                it.directionalCamNearOffset = -200f
                it.setDefaultDepthOffset(true)

                oceanFloorPass.dependsOn(it)
            }
        }

        ssao = AoPipeline.createForward(this).apply {
            // negative radius is used to set radius relative to camera distance
            radius = -0.05f
            isEnabled = isSsao
        }

        camLocalGrass.setupGrass(grassColor)
        grass.setupGrass(grassColor)
        trees.setupTrees()

        boxMesh?.let { +it }
        bridgeMesh?.let { +it }

        +trees.treeGroup
        +grass.grassQuads
        +camLocalGrass.grassQuads
        +playerModel
        +terrainTiles
        +ocean.oceanMesh
        +sky.skybox
        +physicsObjects.debugLines

        oceanFloorPass.renderGroup += playerModel
        boxMesh?.let { oceanFloorPass.renderGroup += it }

        //defaultCamTransform()

        // setup camera tracking player
        camRig = CharacterTrackingCamRig(ctx.inputMgr).apply {
            camera.setClipRange(0.5f, 5000f)
            trackedPose = physicsObjects.playerController.controller.actor.transform
            +camera
            minZoom = 0.75f
            maxZoom = 100f
            pivotPoint.set(0.25f, 0.75f, 0f)

            // hardcoded start look direction
            lookDirection.set(-0.87f, 0.22f, 0.44f).norm()

            // make sure onUpdate listener is called before internal one of CharacterTrackingCamRig, so we can
            // consume the scroll event
            onUpdate.add(0) { ev ->
                // use camera look direction to control player move direction
                physicsObjects.playerController.frontHeading = atan2(lookDirection.x, -lookDirection.z).toDeg()

                val gun = physicsObjects.playerController.tractorGun
                val ptr = ev.ctx.inputMgr.pointerState.primaryPointer
                if (gun.tractorState == TractorGun.TractorState.TRACTOR) {
                    ptr.consume(InputManager.CONSUMED_SCROLL)
                    if (ctx.inputMgr.isShiftDown) {
                        gun.tractorDistance += ptr.deltaScroll.toFloat() * 0.25f
                    } else {
                        gun.rotationTorque += ptr.deltaScroll.toFloat()
                    }
                }
            }
        }
        // don't forget to add the cam rig to the scene
        +camRig
        physicsObjects.playerController.tractorGun.camRig = camRig

        updateTerrainShader()
        updateOceanShader()
        updateGrassShader()
        updateTreeShader()
        updatePlayerShader()
        updateBoxShader()
        updateBridgeShader()

        onUpdate += {
            wind.updateWind(it.deltaT)
            sky.updateSun(lighting.lights[0])

            (playerModel.model.meshes.values.first().shader as KslLitShader).apply {
                updateSky(sky.weightedEnvs)
            }
            (bridgeMesh?.shader as KslLitShader).apply {
                updateSky(sky.weightedEnvs)
            }
            (boxMesh?.shader as KslLitShader).apply {
                updateSky(sky.weightedEnvs)
            }
        }
    }

    private fun updateSsaoEnabled() {
        ssao.isEnabled = isSsao
    }

    private fun updateTerrainShader() {
        terrainTiles.makeTerrainShaders(colorMap, normalMap, terrain.splatMap, shadowMap, ssao.aoMap, isGroundPbr)
    }

    private fun updateOceanShader() {
        ocean.oceanShader = OceanShader.makeOceanShader(oceanFloorPass, shadowMap, wind.density, oceanBump, isGroundPbr)
    }

    private fun updateTreeShader() {
        trees.makeTreeShaders(shadowMap, ssao.aoMap, wind.density, isTreesPbr)
    }

    private fun updateGrassShader() {
        camLocalGrass.grassShader = GrassShader.makeGrassShader(grassColor, shadowMap, ssao.aoMap, wind.density, true, isGrassPbr)
        grass.grassQuads.children.filterIsInstance<Mesh>().forEach {
            it.shader = GrassShader.makeGrassShader(grassColor, shadowMap, ssao.aoMap, wind.density, false, isGrassPbr).shader
        }
    }

    private fun updateBoxShader() {
        boxMesh?.shader = instancedObjectShader(0.2f, 0.25f, isBoxesPbr)
    }

    private fun updateBridgeShader() {
        bridgeMesh?.shader = instancedObjectShader(0.8f, 0f, isBridgePbr)
    }

    private fun updatePlayerShader() {
        fun KslLitShader.LitShaderConfig.baseConfig() {
            vertices { enableArmature(40) }
            color { constColor(MdColor.PINK.toLinear()) }
            shadow { addShadowMap(shadowMap) }
            enableSsao(ssao.aoMap)
            dualImageBasedAmbientColor()
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
        }

        val shader = if (isPlayerPbr) {
            KslPbrShader {
                baseConfig()
                iblConfig()
                roughness(0.2f)
            }

        } else {
            KslBlinnPhongShader {
                baseConfig()
                specularStrength(0.5f)
            }
        }

        playerModel.model.meshes.values.forEach {
            it.shader = shader

            it.normalLinearDepthShader = KslDepthShader(KslDepthShader.Config().apply {
                outputMode = KslDepthShader.OutputMode.NORMAL_LINEAR
                vertices { enableArmature(40) }
            })
        }
    }

    private fun instancedObjectShader(roughness: Float, metallic: Float, isPbr: Boolean): KslShader {
        fun KslLitShader.LitShaderConfig.baseConfig() {
            vertices { isInstanced = true }
            color { vertexColor() }
            shadow { addShadowMap(shadowMap) }
            enableSsao(ssao.aoMap)
            dualImageBasedAmbientColor()
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
        }
        return if (isPbr) {
            KslPbrShader {
                baseConfig()
                iblConfig()
                roughness(roughness)
                metallic(metallic)
            }
        } else {
            KslBlinnPhongShader {
                baseConfig()
                specularStrength(1f - roughness)
            }
        }
    }

    private fun makeBoxMesh() = colorMesh {
        generate {
            color = MdColor.PURPLE.toLinear()
            physicsObjects.boxes[0].shapes[0].geometry.generateMesh(this)
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

    private fun makeBridgeMesh() = colorMesh {
        generate {
            color = MdColor.BROWN toneLin 700
            cube {
                size.set(2f, 0.2f, 1.1f)
                centered()
            }
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

    companion object {
        fun KslPbrShader.Config.iblConfig() {
            lightStrength = 3f
        }

        fun KslLitShader.updateSky(envMaps: Sky.WeightedEnvMaps) {
            ambientTextures[0] = envMaps.envA.irradianceMap
            ambientTextures[1] = envMaps.envB.irradianceMap
            ambientTextureWeights = Vec2f(envMaps.weightA, envMaps.weightB)

            if (this is KslPbrShader) {
                reflectionMaps[0] = envMaps.envA.reflectionMap
                reflectionMaps[1] = envMaps.envB.reflectionMap
                reflectionMapWeights = Vec2f(envMaps.weightA, envMaps.weightB)
            }
        }
    }
}