package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Random
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.deferred.DeferredPbrShader
import de.fabmax.kool.util.deferred.DeferredPipeline
import de.fabmax.kool.util.deferred.DeferredPipelineConfig
import de.fabmax.kool.util.deferred.DeferredPointLights
import de.fabmax.kool.util.ibl.EnvironmentHelper
import kotlin.math.sqrt

class DeferredDemo : DemoScene("Deferred Shading") {

    private lateinit var deferredPipeline: DeferredPipeline

    private lateinit var objects: Mesh
    private lateinit var objectShader: DeferredPbrShader

    private lateinit var lightPositionMesh: Mesh
    private lateinit var lightVolumeMesh: LineMesh

    private var autoRotate = true
    private val rand = Random(1337)

    private var lightCount = 2000
    private val lights = mutableListOf<AnimatedLight>()

    private val colorMap = Cycler(listOf(
            ColorMap("Colorful", listOf(MdColor.RED, MdColor.PINK, MdColor.PURPLE, MdColor.DEEP_PURPLE,
                    MdColor.INDIGO, MdColor.BLUE, MdColor.LIGHT_BLUE, MdColor.CYAN, MdColor.TEAL, MdColor.GREEN,
                    MdColor.LIGHT_GREEN, MdColor.LIME, MdColor.YELLOW, MdColor.AMBER, MdColor.ORANGE, MdColor.DEEP_ORANGE)),
            ColorMap("Hot-Cold", listOf(MdColor.PINK, MdColor.CYAN)),
            ColorMap("Summer", listOf(MdColor.ORANGE, MdColor.BLUE, MdColor.GREEN)),
            ColorMap("Sepia", listOf(MdColor.ORANGE tone 100))
    )).apply { index = 1 }

    override fun lateInit(ctx: KoolContext) {
        updateLights()
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        +orbitInputTransform {
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(0f, -40f)
            // Add camera to the transform group
            +camera
            zoom = 28.0
            maxZoom = 50.0

            translation.set(0.0, -11.0, 0.0)
            onUpdate += {
                if (autoRotate) {
                    verticalRotation += it.deltaT * 3f
                }
            }
        }

        // don't use any global lights
        lighting.lights.clear()
        // no need to clear the screen, as we draw a fullscreen quad containing the deferred render output every frame
        mainRenderPass.clearColor = null

        val ibl = EnvironmentHelper.singleColorEnvironment(this, Color(0.15f, 0.15f, 0.15f))

        val defCfg = DeferredPipelineConfig().apply {
            maxGlobalLights = 0
            isWithAmbientOcclusion = true
            isWithScreenSpaceReflections = false
            isWithImageBasedLighting = false
            isWithBloom = true
            isWithVignette = true
            isWithChromaticAberration = true

            // set output depth compare op to ALWAYS, so that the skybox with maximum depth value is drawn
            outputDepthTest = DepthCompareOp.ALWAYS
        }
        deferredPipeline = DeferredPipeline(this, defCfg)
        deferredPipeline.apply {
            bloomScale = 0.5f
            bloomStrength = 0.75f
            setBloomBrightnessThresholds(0.5f, 1f)

            lightingPassContent += Skybox.cube(ibl.reflectionMap, 1f, hdrOutput = true)
        }
        deferredPipeline.sceneContent.makeContent()
        +deferredPipeline.createDefaultOutputQuad()
        makeLightOverlays()

        onUpdate += { evt ->
            lights.forEach { it.animate(evt.deltaT) }
        }
    }

    private fun Scene.makeLightOverlays() {
        apply {
            lightVolumeMesh = wireframeMesh(deferredPipeline.dynamicPointLights.mesh.geometry).apply {
                isFrustumChecked = false
                isVisible = false
                shader = ModeledShader(instancedLightVolumeModel())
            }
            +lightVolumeMesh

            val lightPosInsts = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT, Attribute.COLORS), MAX_LIGHTS)
            val lightVolInsts = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT, Attribute.COLORS), MAX_LIGHTS)
            lightPositionMesh.instances = lightPosInsts
            lightVolumeMesh.instances = lightVolInsts

            val lightModelMat = Mat4f()
            onUpdate += {
                if (lightPositionMesh.isVisible || lightVolumeMesh.isVisible) {
                    lightPosInsts.clear()
                    lightVolInsts.clear()
                    val srgbColor = MutableColor()

                    deferredPipeline.dynamicPointLights.lightInstances.forEach { light ->
                        lightModelMat.setIdentity()
                        lightModelMat.translate(light.position)


                        if (lightPositionMesh.isVisible) {
                            lightPosInsts.addInstance {
                                put(lightModelMat.matrix)
                                put(light.color.array)
                            }
                        }
                        if (lightVolumeMesh.isVisible) {
                            light.color.toSrgb(srgbColor)
                            val s = sqrt(light.power)
                            lightModelMat.scale(s, s, s)
                            lightVolInsts.addInstance {
                                put(lightModelMat.matrix)
                                put(srgbColor.array)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Group.makeContent() {
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
            shader = objectShader
        }
        +objects

        lightPositionMesh = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS)) {
            isFrustumChecked = false
            isVisible = true
            generate {
                icoSphere {
                    steps = 1
                    radius = 0.05f
                    center.set(Vec3f.ZERO)
                }
            }
            shader = lightPosShader()
        }
        +lightPositionMesh

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
                useAmbientOcclusionMap("${Demo.pbrBasePath}/futuristic-panels1/futuristic-panels1-ao.jpg")
            }
            val groundShader = DeferredPbrShader(pbrCfg)
            shader = groundShader

            onDispose += {
                groundShader.albedoMap.dispose()
                groundShader.aoMap.dispose()
                groundShader.normalMap.dispose()
                groundShader.metallicMap.dispose()
                groundShader.roughnessMap.dispose()
                groundShader.displacementMap.dispose()
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
            deferredPipeline.dynamicPointLights.lightInstances.clear()
        } else {
            while (lights.size > lightCount) {
                lights.removeAt(lights.lastIndex)
                deferredPipeline.dynamicPointLights.lightInstances.removeAt(deferredPipeline.dynamicPointLights.lightInstances.lastIndex)
            }
        }

        while (lights.size < lightCount) {
            val grp = lightGroups[rand.randomI(lightGroups.indices)]
            val x = rand.randomI(0 until grp.rows)
            val light = deferredPipeline.dynamicPointLights.addPointLight {
                power = 1.0f
            }
            val animLight = AnimatedLight(light).apply {
                startColor = colorMap.current.getColor(lights.size).toLinear()
                desiredColor = startColor
                colorMix = 1f
            }
            lights += animLight
            grp.setupLight(animLight, x, travel, rand.randomF())
        }
        updateLightColors()
    }

    private fun updateLightColors() {
        lights.forEachIndexed { iLight, it ->
            it.startColor = it.desiredColor
            it.desiredColor = colorMap.current.getColor(iLight).toLinear()
            it.colorMix = 0f
        }
    }

    override fun setupMenu(ctx: KoolContext) = controlUi {
        val images = mutableListOf<UiImage>()
        images += image(imageShader = gBufferShader(deferredPipeline.activePass.materialPass.albedoMetal, 0f, 1f)).apply {
            setupImage(0.025f, 0.025f)
        }
        images += image(imageShader = gBufferShader(deferredPipeline.activePass.materialPass.normalRoughness, 1f, 0.5f)).apply {
            setupImage(0.025f, 0.35f)
        }
        images += image(imageShader = gBufferShader(deferredPipeline.activePass.materialPass.positionFlags, 10f, 0.05f)).apply {
            setupImage(0.025f, 0.675f)
        }
        images += image(imageShader = ModeledShader.TextureColor(deferredPipeline.aoPipeline?.aoMap, model = AoDemo.aoMapColorModel())).apply {
            setupImage(0.35f, 0.35f)
        }
        images += image(imageShader = MetalRoughFlagsTex(deferredPipeline)).apply {
            setupImage(0.35f, 0.675f)
        }
        images += image(imageShader = ModeledShader.HdrTextureColor(deferredPipeline.bloom?.bloomMap)).apply {
            setupImage(0.35f, 0.025f)
        }

        section("Dynamic Lights") {
            sliderWithValue("Light Count:", lightCount.toFloat(), 1f, MAX_LIGHTS.toFloat(), 0) {
                lightCount = value.toInt()
                updateLights()
            }
            toggleButton("Light Bodies", lightPositionMesh.isVisible) { lightPositionMesh.isVisible = isEnabled }
            toggleButton("Light Volumes", lightVolumeMesh.isVisible) { lightVolumeMesh.isVisible = isEnabled }
            cycler("Color Map:", colorMap) { _, _ -> updateLightColors() }
        }
        section("Deferred Shading") {
            toggleButton("Show Maps", images.first().isVisible) { images.forEach { it.isVisible = isEnabled } }
            toggleButton("Ambient Occlusion", deferredPipeline.isAoEnabled) { deferredPipeline.isAoEnabled = isEnabled }
            toggleButton("Bloom", deferredPipeline.isBloomEnabled) { deferredPipeline.isBloomEnabled = isEnabled }
        }
        section("Bloom") {
            sliderWithValue("Strength", deferredPipeline.bloomStrength, 0f, 2f) { deferredPipeline.bloomStrength = value }
            sliderWithValue("Radius", deferredPipeline.bloomScale, 0f, 2f) { deferredPipeline.bloomScale = value }
            sliderWithValue("Min Brightness", deferredPipeline.bloom?.lowerThreshold ?: 0f, 0f, 2f) {
                deferredPipeline.bloom?.lowerThreshold = value
                deferredPipeline.bloom?.upperThreshold = value + 0.5f
            }
        }
        section("Scene") {
            toggleButton("Auto Rotate", autoRotate) { autoRotate = isEnabled}
            toggleButton("Show Objects", objects.isVisible) {
                objects.isVisible = isEnabled
                updateLights(true)
            }
            sliderWithValue("Object Roughness:", objectShader.roughness.value, 0f, 1f, 2) {
                objectShader.roughness(value)
            }
        }
    }

    private fun UiImage.setupImage(x: Float, y: Float) {
        isVisible = false
        relativeWidth = 0.3f
        relativeX = x
        relativeY = y
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

        var startColor = Color.WHITE
        var desiredColor = Color.WHITE
        var colorMix = 0f

        fun animate(deltaT: Float) {
            travelPos += deltaT * speed
            if (travelPos > travelDist) {
                travelPos -= travelDist
            }
            light.position.set(dir).scale(travelPos).add(startPos)

            if (colorMix < 1f) {
                colorMix += deltaT * 2f
                if (colorMix > 1f) {
                    colorMix = 1f
                }
                startColor.mix(desiredColor, colorMix, light.color)
            }
        }
    }

    private class ColorMap(val name: String, val colors: List<Color>) {
        fun getColor(idx: Int): Color = colors[idx % colors.size]
        override fun toString() = name
    }

    private fun instancedLightVolumeModel(): ShaderModel = ShaderModel("instancedLightIndicators").apply {
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

    private fun lightPosShader(): DeferredPbrShader {
        val cfg = PbrMaterialConfig().apply {
            albedoSource = Albedo.STATIC_ALBEDO
            albedo = Color.WHITE
            isInstanced = true
        }
        val model = DeferredPbrShader.defaultMrtPbrModel(cfg).apply {
            val ifColors: StageInterfaceNode
            vertexStage {
                ifColors = stageInterfaceNode("ifColors", instanceAttributeNode(Attribute.COLORS).output)
            }
            fragmentStage {
                findNodeByType<DeferredPbrShader.MrtMultiplexNode>()!!.inEmissive = multiplyNode(ifColors.output, 2f).output
            }
        }
        return DeferredPbrShader(cfg, model)
    }

    private fun gBufferShader(map: Texture2d, offset: Float, scale: Float): ModeledShader {
        return ModeledShader.TextureColor(map, model = rgbMapColorModel(offset, scale))
    }

    private fun rgbMapColorModel(offset: Float, scale: Float) = ShaderModel("rgbMap").apply {
        val ifTexCoords: StageInterfaceNode

        vertexStage {
            ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
            positionOutput = simpleVertexPositionNode().outVec4
        }
        fragmentStage {
            val sampler = texture2dSamplerNode(texture2dNode("colorTex"), ifTexCoords.output)
            val rgb = splitNode(sampler.outColor, "rgb").output
            val scaled = multiplyNode(addNode(rgb, ShaderNodeIoVar(ModelVar1fConst(offset))).output, scale)
            colorOutput(scaled.output, alpha = ShaderNodeIoVar(ModelVar1fConst(1f)))
        }
    }

    private class MetalRoughFlagsTex(val deferredPipeline: DeferredPipeline) : ModeledShader(shaderModel()) {
        override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
            model.findNode<Texture2dNode>("positionFlags")!!.sampler.texture = deferredPipeline.activePass.materialPass.positionFlags
            model.findNode<Texture2dNode>("normalRough")!!.sampler.texture = deferredPipeline.activePass.materialPass.normalRoughness
            model.findNode<Texture2dNode>("albedoMetal")!!.sampler.texture = deferredPipeline.activePass.materialPass.albedoMetal
            super.onPipelineCreated(pipeline, mesh, ctx)
        }

        companion object {
            fun shaderModel() = ShaderModel("metal-rough-flags-shader").apply {
                val ifTexCoords: StageInterfaceNode

                vertexStage {
                    ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                    positionOutput = simpleVertexPositionNode().outVec4
                }
                fragmentStage {
                    val flagsSampler = texture2dSamplerNode(texture2dNode("positionFlags"), ifTexCoords.output)
                    val roughSampler = texture2dSamplerNode(texture2dNode("normalRough"), ifTexCoords.output)
                    val metalSampler = texture2dSamplerNode(texture2dNode("albedoMetal"), ifTexCoords.output)
                    val flags = splitNode(flagsSampler.outColor, "a").output
                    val rough = splitNode(roughSampler.outColor, "a").output
                    val metal = splitNode(metalSampler.outColor, "a").output
                    val outColor = combineNode(GlslType.VEC_3F).apply {
                        inX = flags
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