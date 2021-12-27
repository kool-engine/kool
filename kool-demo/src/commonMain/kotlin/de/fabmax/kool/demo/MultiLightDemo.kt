package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomF
import de.fabmax.kool.math.toRad
import de.fabmax.kool.pipeline.SingleColorTexture
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MutableColor
import de.fabmax.kool.util.deferred.*
import de.fabmax.kool.util.gltf.GltfFile
import de.fabmax.kool.util.gltf.loadGltfFile
import de.fabmax.kool.util.ibl.EnvironmentHelper
import kotlin.math.*

class MultiLightDemo : DemoScene("Reflections") {

    private lateinit var deferredPipeline: DeferredPipeline

    private val lights = listOf(
            LightMesh(MdColor.CYAN),
            LightMesh(MdColor.RED),
            LightMesh(MdColor.AMBER),
            LightMesh(MdColor.GREEN))
    private val noSsrMap = SingleColorTexture(Color(0f, 0f, 0f, 0f))

    private var lightCount = 4
    private var lightPower = 500f
    private var lightSaturation = 0.4f
    private var lightRandomness = 0.3f
    private var isScrSpcReflections = true
    private var autoRotate = true
    private var showLightIndicators = true

    private val colorCycler = Cycler(matColors).apply { index = 1 }
    private var roughness = 0.1f
    private var metallic = 0.0f

    private var bunnyMesh: Mesh? = null
    private var groundMesh: Mesh? = null

    private var modelShader: DeferredPbrShader? = null

    override fun lateInit(ctx: KoolContext) {
        updateLighting()
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        +orbitInputTransform {
            +camera
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
            zoom = 17.0
            maxZoom = 50.0
            translation.set(0.0, 2.0, 0.0)
            setMouseRotation(0f, -5f)
            // let the camera slowly rotate around vertical axis
            onUpdate += {
                if (autoRotate) {
                    verticalRotation += it.deltaT * 3f
                }
            }
        }

        lighting.lights.clear()
        lights.forEach {
            lighting.lights.add(it.light)
            +it
        }

        setupDeferred(this, ctx)
    }

    private fun setupDeferred(scene: Scene, ctx: KoolContext) {
        val envMaps = EnvironmentHelper.singleColorEnvironment(scene, Color(0.15f, 0.15f, 0.15f))
        val defCfg = DeferredPipelineConfig().apply {
            isWithAmbientOcclusion = false
            isWithScreenSpaceReflections = true
            useImageBasedLighting(envMaps)
        }
        deferredPipeline = DeferredPipeline(scene, defCfg)

        scene += deferredPipeline.createDefaultOutputQuad().also {
            (it.shader as? DeferredOutputShader)?.setupVignette(0f)
        }
        scene += Skybox.cube(envMaps.reflectionMap, 1f)

        scene.onDispose += {
            noSsrMap.dispose()
        }

        deferredPipeline.sceneContent.apply {
            ctx.assetMgr.launch {
                val floorAlbedo = loadAndPrepareTexture("${Demo.pbrBasePath}/woodfloor/WoodFlooringMahoganyAfricanSanded001_COL_2K.jpg")
                val floorNormal = loadAndPrepareTexture("${Demo.pbrBasePath}/woodfloor/WoodFlooringMahoganyAfricanSanded001_NRM_2K.jpg")
                val floorRoughness = loadAndPrepareTexture("${Demo.pbrBasePath}/woodfloor/WoodFlooringMahoganyAfricanSanded001_REFL_2K.jpg")
                onDispose += {
                    floorAlbedo.dispose()
                    floorNormal.dispose()
                    floorRoughness.dispose()
                }

                +textureMesh(isNormalMapped = true) {
                    generate {
                        rect {
                            rotate(-90f, Vec3f.X_AXIS)
                            size.set(100f, 100f)
                            origin.set(-size.x / 2, -size.y / 2, 0f)
                            generateTexCoords(4f)
                        }
                    }

                    // ground doesn't need to cast shadows (there's nothing underneath it...)
                    isCastingShadow = false
                    groundMesh = this

                    shader = deferredPbrShader {
                        useAlbedoMap(floorAlbedo)
                        useNormalMap(floorNormal)
                        useRoughnessMap(floorRoughness)
                    }
                }

                loadGltfFile("${Demo.modelBasePath}/bunny.gltf.gz")?.let {
                    val modelCfg = GltfFile.ModelGenerateConfig(generateNormals = true, applyMaterials = false)
                    val model = it.makeModel(modelCfg)
                    bunnyMesh = model.meshes.values.first()
                    +model

                    modelShader = deferredPbrShader {
                        useStaticAlbedo(colorCycler.current.linColor)
                        roughness = this@MultiLightDemo.roughness
                    }
                    bunnyMesh!!.shader = modelShader
                }
            }
        }
    }

    private fun updateLighting() {
        lights.forEachIndexed { i, light ->
            if (i < deferredPipeline.shadowMaps.size) {
                deferredPipeline.shadowMaps[i].isShadowMapEnabled = false
            }
            light.disable(mainScene.lighting)
        }

        var pos = 0f
        val step = 360f / lightCount
        for (i in 0 until min(lightCount, lights.size)) {
            lights[i].setup(pos)
            lights[i].enable(mainScene.lighting)
            pos += step
            if (i < deferredPipeline.shadowMaps.size) {
                deferredPipeline.shadowMaps[i].isShadowMapEnabled = true
            }
        }

        lights.forEach { it.updateVisibility() }
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
        val ssrMap = image(deferredPipeline.reflections?.reflectionMap)

        section("Lights") {
            cycler("Lights:", Cycler(1, 2, 3, 4).apply { index = 3 }) { count, _ ->
                lightCount = count
                updateLighting()
            }
            sliderWithValue("Strength:", lightPower, 0f, 1000f, 0) {
                lightPower = value
                updateLighting()
            }
            sliderWithValue("Saturation:", lightSaturation, 0f, 1f) {
                lightSaturation = value
                updateLighting()
            }
        }
        section("Screen Space Reflections") {
            toggleButton("Enabled", deferredPipeline.isSsrEnabled) { deferredPipeline.isSsrEnabled = isEnabled }
            toggleButton("Show SSR Map", ssrMap.isVisible) { ssrMap.isVisible = isEnabled }
            sliderWithValue("Map Size:", deferredPipeline.reflectionMapSize, 0.1f, 1f, 1) {
                deferredPipeline.reflectionMapSize = (value * 10).roundToInt() / 10f
            }
        }
        section("Material") {
            cycler("Color:", colorCycler) { color, _ -> modelShader?.albedo?.invoke(color.linColor) }
            sliderWithValue("Roughness:", roughness, 0f, 1f) {
                roughness = value
                modelShader?.roughness?.invoke(value)
            }
            sliderWithValue("Metallic:", metallic, 0f, 1f) {
                metallic = value
                modelShader?.metallic?.invoke(value)
            }
        }
        section("Scene") {
            toggleButton("Auto Rotate", autoRotate) { autoRotate = isEnabled }
            toggleButton("Light Indicators", showLightIndicators) {
                showLightIndicators = isEnabled
                updateLighting()
            }
        }
    }

    private inner class LightMesh(val color: Color) : Group() {
        val light = Light()

        private val spotAngleMesh = LineMesh().apply { isCastingShadow = false }

        private var isEnabled = true
        private var animPos = 0.0
        private val lightMeshShader = ModeledShader.StaticColor()
        private val meshPos = MutableVec3f()
        private var anglePos = 0f
        private val rotOff = randomF(0f, 3f)

        init {
            light.setSpot(Vec3f.ZERO, Vec3f.X_AXIS, 50f)
            val lightMesh = colorMesh {
                isCastingShadow = false
                generate {
                    uvSphere {
                        radius = 0.1f
                    }
                    rotate(90f, Vec3f.Z_AXIS)
                    cylinder {
                        bottomRadius = 0.015f
                        topRadius = 0.015f
                        height = 0.85f
                        steps = 4
                    }
                    translate(0f, 0.85f, 0f)
                    cylinder {
                        bottomRadius = 0.1f
                        topRadius = 0.0025f
                        height = 0.15f
                    }
                }
                shader = lightMeshShader
            }
            +lightMesh
            +spotAngleMesh

            onUpdate += {
                if (autoRotate) {
                    animPos += it.deltaT
                }

                val r = cos(animPos / 15 + rotOff).toFloat() * lightRandomness
                light.spotAngle = 60f - r * 20f
                updateSpotAngleMesh()

                setIdentity()
                rotate(animPos.toFloat() * -10f, Vec3f.Y_AXIS)
                translate(meshPos)
                rotate(anglePos, Vec3f.Y_AXIS)
                rotate(30f + 20f * r, Vec3f.Z_AXIS)

                transform.transform(light.position.set(Vec3f.ZERO), 1f)
                transform.transform(light.direction.set(Vec3f.NEG_X_AXIS), 0f)
            }
        }

        private fun updateSpotAngleMesh() {
            val r = 1f * tan(light.spotAngle.toRad() / 2)
            val c = lightMeshShader.color
            val n = 40

            spotAngleMesh.clear()
            for (i in 0 until n) {
                val a0 = i.toFloat() / n * 2 * PI
                val a1 = (i+1).toFloat() / n * 2 * PI
                spotAngleMesh.addLine(Vec3f(-1f, cos(a0).toFloat() * r, sin(a0).toFloat() * r), c,
                        Vec3f(-1f, cos(a1).toFloat() * r, sin(a1).toFloat() * r), c)
            }
            val e = cos(45f.toRad()) * r
            spotAngleMesh.addLine(Vec3f.ZERO, c, Vec3f(-1f, e, e), c)
            spotAngleMesh.addLine(Vec3f.ZERO, c, Vec3f(-1f, e, -e), c)
            spotAngleMesh.addLine(Vec3f.ZERO, c, Vec3f(-1f, -e, -e), c)
            spotAngleMesh.addLine(Vec3f.ZERO, c, Vec3f(-1f, -e, e), c)
        }

        fun setup(angPos: Float) {
            val x = cos(angPos.toRad()) * 10f
            val z = sin(angPos.toRad()) * 10f
            meshPos.set(x, 9f, -z)
            anglePos = angPos
            val color = Color.WHITE.mix(color, lightSaturation, MutableColor())
            light.setColor(color.toLinear(), lightPower)
            lightMeshShader.color = color
            updateSpotAngleMesh()
        }

        fun enable(lighting: Lighting) {
            isEnabled = true
            lighting.lights.apply {
                if (!contains(light)) {
                    add(light)
                }
            }
            updateVisibility()
        }

        fun disable(lighting: Lighting) {
            isEnabled = false
            lighting.lights.remove(light)
            updateVisibility()
        }

        fun updateVisibility() {
            isVisible = isEnabled && showLightIndicators
        }
    }

    private data class MatColor(val name: String, val linColor: Color) {
        override fun toString() = name
    }

    companion object {
        private val matColors = listOf(
                MatColor("White", Color.WHITE.toLinear()),
                MatColor("Red", MdColor.RED.toLinear()),
                MatColor("Pink", MdColor.PINK.toLinear()),
                MatColor("Purple", MdColor.PURPLE.toLinear()),
                MatColor("Deep Purple", MdColor.DEEP_PURPLE.toLinear()),
                MatColor("Indigo", MdColor.INDIGO.toLinear()),
                MatColor("Blue", MdColor.BLUE.toLinear()),
                MatColor("Cyan", MdColor.CYAN.toLinear()),
                MatColor("Teal", MdColor.TEAL.toLinear()),
                MatColor("Green", MdColor.GREEN.toLinear()),
                MatColor("Light Green", MdColor.LIGHT_GREEN.toLinear()),
                MatColor("Lime", MdColor.LIME.toLinear()),
                MatColor("Yellow", MdColor.YELLOW.toLinear()),
                MatColor("Amber", MdColor.AMBER.toLinear()),
                MatColor("Orange", MdColor.ORANGE.toLinear()),
                MatColor("Deep Orange", MdColor.DEEP_ORANGE.toLinear()),
                MatColor("Brown", MdColor.BROWN.toLinear()),
                MatColor("Grey", MdColor.GREY.toLinear()),
                MatColor("Blue Grey", MdColor.BLUE_GREY.toLinear()),
                MatColor("Almost Black", Color(0.1f, 0.1f, 0.1f).toLinear())
        )
    }
}