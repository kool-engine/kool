package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.input.*
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.physics.util.CharacterTrackingCamRig
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GradientTexture
import de.fabmax.kool.pipeline.SamplerSettings
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.pipeline.shading.DepthShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import kotlin.math.atan2

class TerrainDemo : DemoScene("Terrain Demo") {

    private val colorMap by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine.png")
    private val normalMap by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine_normal.png")
    private val oceanBump by texture2d("${DemoLoader.materialPath}/ocean-bump-1k.jpg")
    private val moonTex by texture2d("${DemoLoader.materialPath}/moon-blueish.png")
    private val grassColor by texture2d("${DemoLoader.materialPath}/grass_64.png",
        TextureProps(defaultSamplerSettings = SamplerSettings().clamped())
    )

    val oceanColor = GradientTexture(ColorGradient(
        0.0f to MdColor.CYAN,
        0.1f to MdColor.LIGHT_BLUE,
        0.2f to MdColor.BLUE,
        0.5f to MdColor.INDIGO.mix(MdColor.BLUE, 0.5f),
        1.0f to (MdColor.INDIGO tone 900).mix(MdColor.BLUE tone 900, 0.5f),
        toLinear = true)
    ).also { it.releaseWith(mainScene) }

    private val oceanFloorCopy = mainScene.mainRenderPass.screenView.copyOutput(isCopyColor = true, isCopyDepth = true, OCEAN_FLOOW_DRAW_GROUP)

    private lateinit var shadowMap: ShadowMap
    private lateinit var ssao: AoPipeline.ForwardAoPipeline
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

    private lateinit var escKeyListener: InputStack.SimpleKeyListener

    private val isSsao = mutableStateOf(true).onChange { ssao.isEnabled = it }
    private val isPlayerPbr = mutableStateOf(true).onChange { updatePlayerShader(it) }
    private val isGroundPbr = mutableStateOf(true).onChange {
        updateTerrainShader(it)
        updateOceanShader(it)
        updateBridgeShader(it)
    }
    private val isBoxesPbr = mutableStateOf(true).onChange { updateBoxShader(it) }
    private val isVegetationPbr = mutableStateOf(false).onChange {
        updateGrassShader(it)
        updateTreeShader(it)
    }

    private val isCursorLocked = mutableStateOf(false).onChange { camRig.isCursorLocked = it }
    private val windSpeed = mutableStateOf(2.5f).onChange {
        wind.speed.set(4f * it, 0.2f * it, 2.7f * it)
    }
    private val windStrength = mutableStateOf(1f).onChange {
        wind.offsetStrength.w = it
    }
    private val windScale = mutableStateOf(100f).onChange {
        wind.scale = it
    }
    private val isGrassEnabled = mutableStateOf(true).onChange { grass.grassQuads.isVisible = it }
    private val isCamLocalGrassEnabled = mutableStateOf(true).onChange { camLocalGrass.grassQuads.isVisible = it }
    private val isGrassShadows = mutableStateOf(true).onChange {
        grass.setIsCastingShadow(it)
        camLocalGrass.setIsCastingShadow(it)
    }

    private val doubleClickListener = object : InputStack.PointerListener {
        override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
            if (pointerState.primaryPointer.isLeftButtonClicked && pointerState.primaryPointer.leftButtonRepeatedClickCount == 2) {
                isCursorLocked.set(true)
            }
        }
    }

    override suspend fun Assets.loadResources(ctx: KoolContext) {
        // enable infinite depth early, skybox creation needs to know if it was successful
        // infinite depth isn't really needed for this demo, but it's a nice test
        mainScene.tryEnableInfiniteDepth()

        showLoadText("Loading height map...")
        val heightMap = Heightmap.fromRawData(loadBlobAsset("${DemoLoader.heightMapPath}/terrain_ocean.raw"), 200f, heightOffset = -50f)
        // more or less the same, but falls back to 8-bit height-resolution in javascript
        //heightMap = HeightMap.fromTextureData2d(loadTextureData2d("${Demo.heightMapPath}/terrain.png", TexFormat.R_F16), 200f)

        showLoadText("Generating wind density texture...")
        wind = Wind()
        wind.offsetStrength.w = windStrength.value
        wind.scale = windScale.value

        sky = Sky(mainScene, moonTex).apply { generateSkyMaps(this@TerrainDemo, loadingScreen!!) }
        showLoadText("Creating terrain...")
        terrain = Terrain(this@TerrainDemo, heightMap)
        terrainTiles = TerrainTiles(terrain, sky)
        showLoadText("Creating ocean...")
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
        val playerGltf = loadGltfModel("${DemoLoader.modelPath}/player.glb")
        playerModel = PlayerModel(playerGltf, physicsObjects.playerController)

        escKeyListener = KeyboardInput.addKeyListener(KeyboardInput.KEY_ESC, "Exit cursor lock") {
            isCursorLocked.set(false)
            PointerInput.cursorMode = CursorMode.NORMAL
        }
        InputStack.defaultInputHandler.pointerListeners += doubleClickListener
    }

    override fun onRelease(ctx: KoolContext) {
        physicsObjects.release()

        wind.density.release()
        oceanFloorCopy.release()

        KeyboardInput.removeKeyListener(escKeyListener)
        PointerInput.cursorMode = CursorMode.NORMAL
        InputStack.defaultInputHandler.pointerListeners -= doubleClickListener
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        Button(if (isCursorLocked.use()) "ESC to unlock cursor" else "Lock cursor") {
            modifier
                .alignX(AlignmentX.Center)
                .width(Grow.Std)
                .margin(horizontal = 16.dp, vertical = 24.dp)
                .onClick { isCursorLocked.set(true) }
            if (isCursorLocked.use()) {
                modifier.colors(buttonColor = MdColor.RED)
            }
        }
        MenuRow {
            Text("[WASD / cursor keys]") { labelStyle(Grow.Std) }
            Text("move") { labelStyle() }
        }
        MenuRow {
            Text("[Shift]") { labelStyle(Grow.Std) }
            Text("walk") { labelStyle() }
        }
        MenuRow {
            Text("[Space]") { labelStyle(Grow.Std) }
            Text("jump") { labelStyle() }
        }
//        toggleButton("Draw Debug Info", playerModel.isDrawShapeOutline) {
//            playerModel.isDrawShapeOutline = isEnabled
//            physicsObjects.debugLines.isVisible = isEnabled
//        }

        Text("Wind") { sectionTitleStyle() }
        val lblSize = UiSizes.baseSize * 1.5f
        val txtSize = UiSizes.baseSize * 1.1f
        MenuRow {
            Text("Speed") { labelStyle(lblSize) }
            MenuSlider(windSpeed.use(), 0.1f, 20f, txtWidth = txtSize) { windSpeed.set(it) }
        }
        MenuRow {
            Text("Strength") { labelStyle(lblSize) }
            MenuSlider(windStrength.use(), 0f, 2f, txtWidth = txtSize) { windStrength.set(it) }
        }
        MenuRow {
            Text("Scale") { labelStyle(lblSize) }
            MenuSlider(windScale.use(), 10f, 500f, txtWidth = txtSize) { windScale.set(it) }
        }

        Text("Grass") { sectionTitleStyle() }
        LabeledSwitch("Enabled", isGrassEnabled)
        LabeledSwitch("Dense grass", isCamLocalGrassEnabled)
        LabeledSwitch("Shadow casting", isGrassShadows)

        Text("Shading") { sectionTitleStyle() }
        LabeledSwitch("Ambient occlusion", isSsao)
        LabeledSwitch("Player PBR shading", isPlayerPbr)
        LabeledSwitch("Ground PBR shading", isGroundPbr)
        LabeledSwitch("Boxes PBR shading", isBoxesPbr)
        LabeledSwitch("Vegetation PBR shading", isVegetationPbr)

        // crosshair
        surface.popup().apply {
            modifier.width(24.dp).height(24.dp).align(AlignmentX.Center, AlignmentY.Center)
            Box { modifier.width(10.dp).height(2.dp).align(AlignmentX.Start, AlignmentY.Center).backgroundColor(Color.WHITE) }
            Box { modifier.width(10.dp).height(2.dp).align(AlignmentX.End, AlignmentY.Center).backgroundColor(Color.WHITE) }
            Box { modifier.width(2.dp).height(10.dp).align(AlignmentX.Center, AlignmentY.Top).backgroundColor(Color.WHITE) }
            Box { modifier.width(2.dp).height(10.dp).align(AlignmentX.Center, AlignmentY.Bottom).backgroundColor(Color.WHITE) }
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        // lighting
        lighting.singleDirectionalLight {
            setup(Vec3f(-1f, -1f, -1f))
            setColor(Color.WHITE, 1f)
        }
        shadowMap = CascadedShadowMap(this@setupMainScene, lighting.lights[0], 300f).apply {
            setMapRanges(0.035f, 0.17f, 1f)
            subMaps.forEach {
                it.directionalCamNearOffset = -200f
                it.setDefaultDepthOffset(true)
            }
        }

        ssao = AoPipeline.createForward(this).apply {
            // negative radius is used to set radius relative to camera distance
            radius = -0.05f
            isEnabled = isSsao.value
            kernelSz = 8
        }

        camLocalGrass.setupGrass(grassColor)
        grass.setupGrass(grassColor)
        trees.setupTrees()

        boxMesh?.let { addNode(it) }
        bridgeMesh?.let { addNode(it) }

        addNode(trees.treeGroup)
        addNode(grass.grassQuads)
        addNode(camLocalGrass.grassQuads)
        addNode(playerModel)
        addNode(terrainTiles)
        addNode(ocean.oceanMesh)
        addNode(sky.skyGroup)
        addNode(physicsObjects.debugLines)

        // set ocean-floor drawGroupId for all objects that can be underwater
        // by setting the draw group ID these objects are drawn before the frame buffer is captured for the ocean shader
        // the captured frame buffer then contains color and depth information for these objects, which is then used
        // by the ocean shader to compute the water color depending on depth and ground color
        terrainTiles.drawGroupId = OCEAN_FLOOW_DRAW_GROUP
        playerModel.drawGroupId = OCEAN_FLOOW_DRAW_GROUP
        boxMesh?.drawGroupId = OCEAN_FLOOW_DRAW_GROUP

        // setup camera tracking player
        setupCamera()

        updateTerrainShader(isGroundPbr.value)
        updateOceanShader(isGroundPbr.value)
        updateGrassShader(isVegetationPbr.value)
        updateTreeShader(isVegetationPbr.value)
        updatePlayerShader(isPlayerPbr.value)
        updateBoxShader(isBoxesPbr.value)
        updateBridgeShader(isGroundPbr.value)

        onUpdate += {
            wind.updateWind(Time.deltaT)
            sky.updateLight(lighting.lights[0] as Light.Directional)

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

    private fun Scene.setupCamera() {
        camRig = CharacterTrackingCamRig(false).apply {
            if (isInfiniteDepth) {
                camera.setClipRange(0.1f, 1e9f)
            } else {
                camera.setClipRange(0.5f, 5000f)
            }
            trackedPose = physicsObjects.playerController.controller.actor.transform
            minZoom = 0.75f
            maxZoom = 100f
            pivotPoint.set(0.25f, 0.75f, 0f)
            addNode(camera)

            setupCollisionAwareCamZoom(physicsObjects.world)

            // hardcoded start look direction
            lookDirection.set(-0.87f, 0.22f, 0.44f).norm()
            applyLookDirection()

            onUpdate {
                // use camera look direction to control player move direction
                physicsObjects.playerController.frontHeading = atan2(lookDirection.x, -lookDirection.z).toDeg()

                val gun = physicsObjects.playerController.tractorGun
                val ptr = PointerInput.primaryPointer
                if (gun.tractorState == TractorGun.TractorState.TRACTOR) {
                    ptr.consume(PointerInput.CONSUMED_SCROLL_Y)
                    if (KeyboardInput.isShiftDown) {
                        gun.tractorDistance += ptr.deltaScroll.toFloat() * 0.25f
                    } else {
                        gun.rotationTorque += ptr.deltaScroll.toFloat()
                    }
                }

                // disable camera zoom, when tractor gun is active
                camRig.isZoomEnabled = gun.tractorState != TractorGun.TractorState.TRACTOR
            }
        }
        // don't forget to add the cam rig to the scene
        addNode(camRig)
        // player / tractor gun needs the camRig to know where it is aiming at
        physicsObjects.playerController.tractorGun.camRig = camRig
    }

    private fun updateTerrainShader(isGroundPbr: Boolean) {
        terrainTiles.makeTerrainShaders(colorMap, normalMap, terrain.splatMap, shadowMap, ssao.aoMap, isGroundPbr)
    }

    private fun updateOceanShader(isGroundPbr: Boolean) {
        ocean.oceanShader = OceanShader.makeOceanShader(oceanFloorCopy, shadowMap, wind.density, oceanBump, oceanColor, isGroundPbr, mainScene.isInfiniteDepth)
    }

    private fun updateTreeShader(isVegetationPbr: Boolean) {
        trees.makeTreeShaders(shadowMap, ssao.aoMap, wind.density, isVegetationPbr)
    }

    private fun updateGrassShader(isVegetationPbr: Boolean) {
        camLocalGrass.grassShader = GrassShader.makeGrassShader(grassColor, shadowMap, ssao.aoMap, wind.density, true, isVegetationPbr)
        val grassQuadShader = GrassShader.makeGrassShader(grassColor, shadowMap, ssao.aoMap, wind.density, false, isVegetationPbr).shader
        grass.grassQuads.children.filterIsInstance<Mesh>().forEach {
            it.shader = grassQuadShader
        }
    }

    private fun updateBoxShader(isBoxesPbr: Boolean) {
        boxMesh?.shader = instancedObjectShader(0.2f, 0.25f, isBoxesPbr)
    }

    private fun updateBridgeShader(isGroundPbr: Boolean) {
        bridgeMesh?.shader = instancedObjectShader(0.8f, 0f, isGroundPbr)
    }

    private fun updatePlayerShader(isPlayerPbr: Boolean) {
        fun KslLitShader.LitShaderConfig.Builder.baseConfig() {
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

            it.normalLinearDepthShader = DepthShader(DepthShader.Config.Builder().apply {
                outputNormals = true
                outputLinearDepth = true
                vertices { enableArmature(40) }
            }.build())
        }
    }

    private fun instancedObjectShader(roughness: Float, metallic: Float, isPbr: Boolean): KslShader {
        fun KslLitShader.LitShaderConfig.Builder.baseConfig() {
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

    private fun makeBoxMesh() = ColorMesh(MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT))).apply {
        generate {
            color = MdColor.PURPLE.toLinear()
            physicsObjects.boxes[0].shapes[0].geometry.generateMesh(this)
        }
        onUpdate += {
            instances!!.apply {
                clear()
                addInstances(physicsObjects.boxes.size) { buf ->
                    physicsObjects.boxes.forEach { box ->
                        box.transform.matrixF.putTo(buf)
                    }
                }
            }
        }
    }

    private fun makeBridgeMesh() = ColorMesh(MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT))).apply {
        generate {
            color = MdColor.BROWN toneLin 700
            cube {
                size.set(2f, 0.2f, 1.1f)
            }
        }
        onUpdate += {
            instances?.let { insts ->
                insts.clear()
                insts.addInstances(physicsObjects.chainBridge.segments.size) { buf ->
                    physicsObjects.chainBridge.segments.forEach { seg ->
                        seg.transform.matrixF.putTo(buf)
                    }
                }
            }
        }
    }

    companion object {
        private const val OCEAN_FLOOW_DRAW_GROUP = -100

        fun KslPbrShader.Config.Builder.iblConfig() {
            isTextureReflection = true
            lightStrength = 3f
        }

        fun KslLitShader.updateSky(envMaps: Sky.WeightedEnvMaps) {
            ambientMaps[0].set(envMaps.envA.irradianceMap)
            ambientMaps[1].set(envMaps.envB.irradianceMap)
            ambientMapWeights = Vec2f(envMaps.weightA, envMaps.weightB)

            if (this is KslPbrShader) {
                reflectionMaps[0].set(envMaps.envA.reflectionMap)
                reflectionMaps[1].set(envMaps.envB.reflectionMap)
                reflectionMapWeights = Vec2f(envMaps.weightA, envMaps.weightB)
            }
        }
    }
}