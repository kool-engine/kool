package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.deferred.*
import kotlin.math.sqrt

fun deferredScene(ctx: KoolContext): List<Scene> {
    val deferredDemo = DeferredDemo(ctx)
    return listOf(deferredDemo.mainScene, deferredDemo.menu)
}

class DeferredDemo(ctx: KoolContext) {

    val mainScene: Scene
    val menu: Scene

    private lateinit var aoPipeline: AoPipeline
    private lateinit var mrtPass: DeferredMrtPass
    private lateinit var pbrPass: PbrLightingPass

    private lateinit var objects: Mesh
    private lateinit var objectShader: DeferredPbrShader
    private val noAoMap = Texture { BufferedTextureData.singleColor(Color.WHITE) }

    private lateinit var lightPositionMesh: Mesh
    private lateinit var lightVolumeMesh: LineMesh

    private var autoRotate = true
    private val rand = Random(1337)

    private var lightCount = 2000
    private val lights = mutableListOf<AnimatedLight>()

    private val colorMap = Cycler(listOf(
            ColorMap("Colorful", listOf(Color.MD_RED, Color.MD_PINK, Color.MD_PURPLE, Color.MD_DEEP_PURPLE,
                    Color.MD_INDIGO, Color.MD_BLUE, Color.MD_LIGHT_BLUE, Color.MD_CYAN, Color.MD_TEAL, Color.MD_GREEN,
                    Color.MD_LIGHT_GREEN, Color.MD_LIME, Color.MD_YELLOW, Color.MD_AMBER, Color.MD_ORANGE, Color.MD_DEEP_ORANGE)),
            ColorMap("Hot-Cold", listOf(Color.MD_PINK, Color.MD_CYAN)),
            ColorMap("Summer", listOf(Color.MD_ORANGE, Color.MD_BLUE, Color.MD_GREEN)),
            ColorMap("White", listOf(Color.WHITE))
    )).apply { index = 1 }

    init {
        mainScene = makeDeferredScene()
        menu = makeMenu(ctx)

        updateLights()
    }

    private fun makeDeferredScene() = scene {
        // don't use any global lights
        lighting.lights.clear()

        // setup MRT pass: contains actual scene content
        mrtPass = DeferredMrtPass(this)
        mrtPass.makeContent(this)

        // setup ambient occlusion pass
        aoPipeline = AoPipeline.createDeferred(this, mrtPass)
        aoPipeline.intensity = 1.2f
        aoPipeline.kernelSz = 32

        // setup lighting pass
        val cfg = PbrSceneShader.DeferredPbrConfig().apply {
            isScrSpcAmbientOcclusion = true
            scrSpcAmbientOcclusionMap = aoPipeline.aoMap
        }
        pbrPass = PbrLightingPass(this, mrtPass, cfg)
        pbrPass.makeLightOverlays()

        // main scene only contains a quad used to draw the deferred shading output
        +textureMesh {
            isFrustumChecked = false
            generate {
                rect {
                    mirrorTexCoordsY()
                }
            }
            pipelineLoader = DeferredOutputShader(pbrPass.colorTexture)
        }

        onUpdate += { _, ctx ->
            lights.forEach { it.animate(ctx.deltaT) }
        }
        onDispose += {
            noAoMap.dispose()
        }
    }

    private fun PbrLightingPass.makeLightOverlays() {
        content.apply {
            lightPositionMesh = colorMesh {
                isFrustumChecked = false
                isVisible = true
                generate {
                    color = Color.RED
                    icoSphere {
                        steps = 1
                        radius = 0.05f
                        center.set(Vec3f.ZERO)
                    }
                }
                pipelineLoader = ModeledShader(instancedLightIndicatorModel())
            }
            +lightPositionMesh

            lightVolumeMesh = wireframeMesh(dynamicPointLights.mesh.geometry).apply {
                isFrustumChecked = false
                isVisible = false
                pipelineLoader = ModeledShader(instancedLightIndicatorModel())
            }
            +lightVolumeMesh

            val lightPosInsts = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT, Attribute.COLORS), MAX_LIGHTS)
            val lightVolInsts = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT, Attribute.COLORS), MAX_LIGHTS)
            lightPositionMesh.instances = lightPosInsts
            lightVolumeMesh.instances = lightVolInsts

            val lightModelMat = Mat4f()
            onUpdate += { _, _ ->
                if (lightPositionMesh.isVisible || lightVolumeMesh.isVisible) {
                    lightPosInsts.clear()
                    lightVolInsts.clear()
                    dynamicPointLights.lightInstances.forEach { light ->
                        lightModelMat.setIdentity()
                        lightModelMat.translate(light.position)

                        if (lightPositionMesh.isVisible) {
                            lightPosInsts.addInstance {
                                put(lightModelMat.matrix)
                                put(light.color.array)
                            }
                        }
                        if (lightVolumeMesh.isVisible) {
                            val s = sqrt(light.intensity)
                            lightModelMat.scale(s, s, s)
                            lightVolInsts.addInstance {
                                put(lightModelMat.matrix)
                                put(light.color.array)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun DeferredMrtPass.makeContent(scene: Scene) {
        content.apply {
            +scene.orbitInputTransform {
                // Set some initial rotation so that we look down on the scene
                setMouseRotation(0f, -40f)
                // Add camera to the transform group
                +camera
                zoom = 28.0
                maxZoom = 50.0

                translation.set(0.0, -11.0, 0.0)
                onUpdate += { _, ctx ->
                    if (autoRotate) {
                        verticalRotation += ctx.deltaT * 3f
                    }
                }
            }

            objects = colorMesh {
                generate {
                    val sphereProtos = mutableListOf<IndexedVertexList>()
                    for (i in 0..10) {
                        val builder = MeshBuilder(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS))
                        sphereProtos += builder.geometry
                        builder.apply {
                            icoSphere {
                                steps = 3
                                radius = rand.randomF(0.3f, 0.4f)
                                center.set(0f, 0.1f + radius, 0f)
                            }
                        }
                    }

                    for (x in -19..19) {
                        for (y in -19..19) {
                            color = Color.WHITE
                            withTransform {
                                translate(x.toFloat(), 0f, y.toFloat())
                                if ((x + 100) % 2 == (y + 100) % 2) {
                                    cube {
                                        size.set(rand.randomF(0.6f, 0.8f), rand.randomF(0.6f, 0.95f), rand.randomF(0.6f, 0.8f))
                                        origin.set(-size.x / 2, 0.1f, -size.z / 2)
                                    }
                                } else {
                                    geometry(sphereProtos[rand.randomI(sphereProtos.indices)])
                                }
                            }
                        }
                    }
                }
                val pbrCfg = PbrMaterialConfig().apply {
                    roughness = 0.15f
                }
                objectShader = DeferredPbrShader(pbrCfg)
                pipelineLoader = objectShader
            }
            +objects

            +textureMesh(isNormalMapped = true) {
                generate {
                    rotate(90f, Vec3f.NEG_X_AXIS)
                    color = Color.WHITE
                    rect {
                        size.set(40f, 40f)
                        origin.set(size.x, size.y, 0f).scale(-0.5f)
                        generateTexCoords(30f)
                    }
                }
                val pbrCfg = PbrMaterialConfig().apply {
                    useAlbedoMap("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-albedo1.jpg")
                    useNormalMap("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-normal.jpg")
                    useRoughnessMap("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-roughness.jpg")
                    useMetallicMap("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-metallic.jpg")
                    useOcclusionMap("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-ao.jpg")
                }
                val groundShader = DeferredPbrShader(pbrCfg)
                pipelineLoader = groundShader

                onDispose += {
                    groundShader.albedoMap?.dispose()
                    groundShader.occlusionMap?.dispose()
                    groundShader.normalMap?.dispose()
                    groundShader.metallicMap?.dispose()
                    groundShader.roughnessMap?.dispose()
                    groundShader.displacementMap?.dispose()
                }
            }
        }
    }

    private fun updateLights(forced: Boolean = false) {
        val rows = 41
        val travel = rows.toFloat()
        val start = travel / 2

        val objOffset = if (objects.isVisible) 0.7f else 0f
        val lightGroups = listOf(
                LightGroup(Vec3f(1 - start, 0.45f, -start), Vec3f(1f, 0f, 0f), Vec3f(0f, 0f, 1f), rows - 1),
                LightGroup(Vec3f(-start, 0.45f, 1 - start), Vec3f(0f, 0f, 1f), Vec3f(1f, 0f, 0f), rows - 1),

                LightGroup(Vec3f(1.5f - start, 0.45f + objOffset, start), Vec3f(1f, 0f, 0f), Vec3f(0f, 0f, -1f), rows - 2),
                LightGroup(Vec3f(start, 0.45f + objOffset, 1.5f - start), Vec3f(0f, 0f, 1f), Vec3f(-1f, 0f, 0f), rows - 2)
        )

        if (forced) {
            lights.clear()
            pbrPass.dynamicPointLights.lightInstances.clear()
        } else {
            while (lights.size > lightCount) {
                lights.removeAt(lights.lastIndex)
                pbrPass.dynamicPointLights.lightInstances.removeAt(pbrPass.dynamicPointLights.lightInstances.lastIndex)
            }
        }

        while (lights.size < lightCount) {
            val grp = lightGroups[rand.randomI(lightGroups.indices)]
            val x = rand.randomI(0 until grp.rows)
            val light = pbrPass.dynamicPointLights.addPointLight {
                intensity = 1.0f
                color.set(colorMap.current.colors[rand.randomI(colorMap.current.colors.indices)].toLinear())
            }
            val animLight = AnimatedLight(light)
            lights += animLight
            grp.setupLight(animLight, x, travel, rand.randomF())
        }
    }

    private fun updateLightColors() {
        lights.forEach {
            it.light.color.set(colorMap.current.colors[rand.randomI(colorMap.current.colors.indices)].toLinear())
        }
    }

    private fun setAoState(enabled: Boolean) {
        aoPipeline.setEnabled(enabled)
        if (enabled) {
            pbrPass.sceneShader.scrSpcAmbientOcclusionMap = aoPipeline.aoMap
        } else {
            pbrPass.sceneShader.scrSpcAmbientOcclusionMap = noAoMap
        }
    }

    private fun makeMenu(ctx: KoolContext) = uiScene {
        val smallFontProps = FontProps(Font.SYSTEM_FONT, 14f)
        val smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, uiDpi, ctx, smallFontProps.style, smallFontProps.chars)
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        val mapGroup = transformGroup {
            isVisible = false

            val positions = listOf(
                    Vec2f(0f, 0f),
                    Vec2f(0f, 1.2f),
                    Vec2f(0f, 2.4f),
                    Vec2f(1.1f, 1.2f),
                    Vec2f(1.1f, 2.4f))

            positions.forEachIndexed { i, p ->
                +textureMesh {
                    generate {
                        rect {
                            origin.set(p.x, p.y, 0f)
                            size.set(1f, 1f)
                            mirrorTexCoordsY()
                        }
                    }

                    pipelineLoader = when (i) {
                        0 -> ModeledShader.TextureColor(mrtPass.albedoMetal, "colorTex", rgbMapColorModel(0f, 1f))
                        1 -> ModeledShader.TextureColor(mrtPass.normalRoughness, "colorTex", rgbMapColorModel(1f, 0.5f))
                        2 -> ModeledShader.TextureColor(mrtPass.positionAo, "colorTex", rgbMapColorModel(10f, 0.05f))
                        3 -> ModeledShader.TextureColor(aoPipeline.aoMap, "colorTex", AoDemo.aoMapColorModel())
                        4 -> MetalRoughAoTex(mrtPass)
                        else -> ModeledShader.StaticColor(Color.MAGENTA)
                    }
                }
            }

            onUpdate += { rp, _ ->
                val mapSz = 0.26f
                val scaleX = rp.viewport.width * mapSz
                val scaleY = scaleX * (rp.viewport.height.toFloat() / rp.viewport.width.toFloat())
                val margin = rp.viewport.height * 0.05f

                setIdentity()
                translate(margin, margin, 0f)
                scale(scaleX, scaleY, 1f)
            }
        }
        +mapGroup

        +container("menu container") {
            ui.setCustom(SimpleComponentUi(this))
            layoutSpec.setOrigin(dps(-370f), dps(-675f), zero())
            layoutSpec.setSize(dps(250f), dps(555f), full())

            var y = -40f
            +label("Dynamic Lights") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            y -= 35f
            +label("Light Count:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
            }
            val lightCntVal = label("$lightCount") {
                layoutSpec.setOrigin(pcs(75f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +lightCntVal
            y -= 35f
            +slider("lightCntSlider", 100f, MAX_LIGHTS.toFloat(), lightCount.toFloat()) {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onValueChanged += {
                    lightCount = value.toInt()
                    lightCntVal.text = "$lightCount"
                    updateLights()
                }
            }
            y -= 35f
            +toggleButton("Light Positions") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = lightPositionMesh.isVisible
                onStateChange += {
                    lightPositionMesh.isVisible = isEnabled
                }
            }
            y -= 35f
            +toggleButton("Light Volumes") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = lightVolumeMesh.isVisible
                onStateChange += {
                    lightVolumeMesh.isVisible = isEnabled
                }
            }
            y -= 35f
            +label("Color Map:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
            }
            y -= 35f
            val colorMapLabel = button(colorMap.current.name) {
                layoutSpec.setOrigin(pcs(15f), dps(y), zero())
                layoutSpec.setSize(pcs(70f), dps(35f), full())
                onClick += { _, _, _ ->
                    text = colorMap.next().name
                    updateLightColors()
                }
            }
            +colorMapLabel
            +button("colors-left") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(20f), dps(35f), full())
                text = "<"

                onClick += { _, _, _ ->
                    colorMap.prev()
                    updateLightColors()
                }
            }
            +button("colors-right") {
                layoutSpec.setOrigin(pcs(80f), dps(y), zero())
                layoutSpec.setSize(pcs(20f), dps(35f), full())
                text = ">"

                onClick += { _, _, _ ->
                    colorMap.next()
                    updateLightColors()
                }
            }

            y -= 40f
            +label("Deferred Shading") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            y -= 35f
            +toggleButton("Show Maps") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = mapGroup.isVisible
                onStateChange += {
                    mapGroup.isVisible = isEnabled
                }
            }
            y -= 35f
            +toggleButton("Ambient Occlusion") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = aoPipeline.aoPass.isEnabled
                onStateChange += {
                    setAoState(isEnabled)
                }
            }

            y -= 40f
            +label("Scene") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            y -= 35f
            +toggleButton("Auto Rotate") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = autoRotate
                onStateChange += {
                    autoRotate = isEnabled
                }
            }
            y -= 35f
            +toggleButton("Show Objects") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = objects.isVisible
                onStateChange += {
                    objects.isVisible = isEnabled
                    updateLights(true)
                }
            }
            y -= 35f
            +label("Object Roughness:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
            }
            y -= 35f
            +slider("roughnessSlider", 0f, 1f, lightCount.toFloat()) {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                value = objectShader.roughness
                onValueChanged += {
                    objectShader.roughness = value
                }
            }
        }
    }

    private inner class LightGroup(val startConst: Vec3f, val startIt: Vec3f, val travelDir: Vec3f, val rows: Int) {
        fun setupLight(light: AnimatedLight, x: Int, travelDist: Float, travelPos: Float) {
            light.startPos.set(startIt).scale(x.toFloat()).add(startConst)
            light.dir.set(travelDir)

            light.travelDist = travelDist
            light.travelPos = travelPos * travelDist
            light.speed = rand.randomF(1f, 3f) * 0.25f
        }
    }

    private class AnimatedLight(val light: DeferredPointLights.PointLight) {
        val startPos = MutableVec3f()
        val dir = MutableVec3f()
        var speed = 1.5f
        var travelPos = 0f
        var travelDist = 10f

        fun animate(deltaT: Float) {
            travelPos += deltaT * speed
            if (travelPos > travelDist) {
                travelPos -= travelDist
            }
            light.position.set(dir).scale(travelPos).add(startPos)
        }
    }

    private class ColorMap(val name: String, val colors: List<Color>)

    private fun instancedLightIndicatorModel(): ShaderModel = ShaderModel("instancedLightIndicators").apply {
        val ifColors: StageInterfaceNode
        vertexStage {
            ifColors = stageInterfaceNode("ifColors", instanceAttributeNode(Attribute.COLORS).output)
            val modelMvp = premultipliedMvpNode().outMvpMat
            val instMvp = multiplyNode(modelMvp, instanceAttrModelMat().output).output
            positionOutput = vec4TransformNode(attrPositions().output, instMvp).outVec4
        }
        fragmentStage {
            colorOutput(unlitMaterialNode(ifColors.output).outColor)
        }
    }

    private fun rgbMapColorModel(offset: Float, scale: Float) = ShaderModel("rgbMap").apply {
        val ifTexCoords: StageInterfaceNode

        vertexStage {
            ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
            positionOutput = simpleVertexPositionNode().outVec4
        }
        fragmentStage {
            val sampler = textureSamplerNode(textureNode("colorTex"), ifTexCoords.output)
            val rgb = splitNode(sampler.outColor, "rgb").output
            val scaled = multiplyNode(addNode(rgb, ShaderNodeIoVar(ModelVar1fConst(offset))).output, scale)
            colorOutput(scaled.output, alpha = ShaderNodeIoVar(ModelVar1fConst(1f)))
        }
    }

    private class MetalRoughAoTex(val mrtPass: DeferredMrtPass) : ModeledShader(shaderModel()) {
        override fun onPipelineCreated(pipeline: Pipeline) {
            model.findNode<TextureNode>("positionAo")!!.sampler.texture = mrtPass.positionAo
            model.findNode<TextureNode>("normalRough")!!.sampler.texture = mrtPass.normalRoughness
            model.findNode<TextureNode>("albedoMetal")!!.sampler.texture = mrtPass.albedoMetal
            super.onPipelineCreated(pipeline)
        }

        companion object {
            fun shaderModel() = ShaderModel().apply {
                val ifTexCoords: StageInterfaceNode

                vertexStage {
                    ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                    positionOutput = simpleVertexPositionNode().outVec4
                }
                fragmentStage {
                    val aoSampler = textureSamplerNode(textureNode("positionAo"), ifTexCoords.output)
                    val roughSampler = textureSamplerNode(textureNode("normalRough"), ifTexCoords.output)
                    val metalSampler = textureSamplerNode(textureNode("albedoMetal"), ifTexCoords.output)
                    val ao = splitNode(aoSampler.outColor, "a").output
                    val rough = splitNode(roughSampler.outColor, "a").output
                    val metal = splitNode(metalSampler.outColor, "a").output
                    val outColor = combineNode(GlslType.VEC_3F).apply {
                        inX = ao
                        inY = rough
                        inZ = metal
                    }
                    colorOutput(outColor.output, alpha = ShaderNodeIoVar(ModelVar1fConst(1f)))
                }
            }
        }
    }

    companion object {
        const val MAX_LIGHTS = 5000
    }
}