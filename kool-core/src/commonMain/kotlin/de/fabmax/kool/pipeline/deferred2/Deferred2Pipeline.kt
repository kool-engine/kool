package de.fabmax.kool.pipeline.deferred2

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.ShadowMapConfig
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.ao.ComputeAoPass
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class Deferred2Pipeline(
    val content: Node,
    val scene: Scene,
    val ibl: EnvironmentMap,
    val camera: Camera = PerspectiveCamera(),
    val lighting: Lighting = Lighting(),
    val shadowMapConfig: List<ShadowMapConfig> = emptyList(),
    val maxGlobalLights: Int = 1,
    var renderScale: Float = 1f,
    var tsaa: List<Vec2f> = TSAA_4,
    maxObjects: Int = 16384,
    addDefaultSkybox: Boolean = true,
    lightingMod: (KslProgram.() -> Unit)? = null,
) {
    val size: Vec2i get() = Vec2i(
        (scene.mainRenderPass.viewport.width * renderScale).toInt().coerceAtLeast(16),
        (scene.mainRenderPass.viewport.height * renderScale).toInt().coerceAtLeast(16)
    )
    val idAllocator: ObjectIdAllocator = DefaultObjectIdAllocator(maxObjects)
    private val camDataBuffer = StructBuffer(DeferredCamDataLayout, 1)
    val camData = camDataBuffer.asStorageBuffer()

    val reprojectMatrixComputePass = ReprojectComputePass(maxObjects, this)

    val gbuffers = AlternatingPair {
        val suff = if (it) "A" else "B"
        GbufferPass(size, "deferred2-gbuffer-pass-$suff", this)
    }
    val aoPass: ComputeAoPass = ComputeAoPass(
        camera = camera,
        inputDepth = gbuffers.a.depth,
        inputNormals = gbuffers.a.normals,
        initialSize = size,
        distFormat = TexFormat.R_F32,
    )
    val lightingPass = LightingPass(
        size = size,
        pipeline = this,
        addDefaultSkybox = addDefaultSkybox,
        lightingMod = lightingMod,
    )
    val filterPass = TemporalFilterPass(size = size, pipeline = this)

    internal val viewProjNoTsaa = MutableMat4f()
    internal val invViewProjNoTsaa = MutableMat4f()

    private val swapListeners = BufferedList<() -> Unit>()
    private val resizeListeners = BufferedList<(Vec2i) -> Unit>()

    init {
        aoPass.kernelSize = 32 / tsaa.size.coerceAtLeast(2)
        aoPass.temporalKernels = tsaa.size.coerceAtLeast(1)
        aoPass.resize(size.x, size.y)

        scene.addComputePass(reprojectMatrixComputePass)
        scene.addOffscreenPass(gbuffers.a)
        scene.addOffscreenPass(gbuffers.b)
        scene.addComputePass(aoPass)
        scene.addOffscreenPass(lightingPass)
        scene.addComputePass(filterPass)

        reprojectMatrixComputePass.isProfileGpu = true
        gbuffers.a.isProfileGpu = true
        gbuffers.b.isProfileGpu = true
        lightingPass.isProfileGpu = true
        filterPass.isProfileGpu = true
        aoPass.isProfileGpu = true

        lightingPass.onRelease { camData.releaseDelayed(1) }

        val offsetMat = MutableMat4f()
        camera.onCameraUpdated += {
            viewProjNoTsaa.set(camera.viewProj)
            invViewProjNoTsaa.set(camera.invViewProj)

            val tsaa = tsaa
            if (tsaa.isNotEmpty()) {
                val offset = tsaa[Time.frameCount % tsaa.size]
                val width = it.viewport.width
                val height = it.viewport.height
                offsetMat.setIdentity().translate(offset.x / width, offset.y / height, 0f).mul(camera.proj)
                camera.proj.set(offsetMat)
                camera.proj.mul(camera.view, camera.dataF.viewProj)
                camera.lazyInvProj.isDirty = true
                camera.dataF.lazyInvViewProj.isDirty = true
            }
        }

        scene.coroutineScope.launch {
            withContext(KoolDispatchers.Synced) {
                while (true) {
                    resizeIfNeeded()
                    swapBuffers()
                    yield()
                }
            }
        }
    }

    fun enableScreenSpaceReflections(numReflectionRays: Int = 3) {
        lightingPass.numReflectionRays = numReflectionRays.clamp(1, 16)
    }

    fun disableScreenSpaceReflections() {
        lightingPass.numReflectionRays = 0
    }

    private fun swapBuffers() {
        camDataBuffer.set(0) {
            set(it.proj, camera.proj)
            set(it.view, camera.view)
            set(it.viewProj, camera.viewProj)
            set(it.invView, camera.invView)
            set(it.invViewProj, invViewProjNoTsaa)
            set(it.oldViewProj, reprojectMatrixComputePass.uploadData.oldVal.viewProjMat)
            set(it.camPosition, camera.globalPos)
            set(it.camNear, camera.clipNear)
            set(it.frameIdx, Time.frameCount)
        }
        camData.uploadData(camDataBuffer)

        reprojectMatrixComputePass.swapBuffers()
        lightingPass.swapBuffers()
        filterPass.swapBuffers()
        val currentGbuffer = gbuffers.newVal
        aoPass.inputShader.swapPipelineData(currentGbuffer) {
            aoPass.inputDepth = currentGbuffer.depth
            aoPass.inputNormals = currentGbuffer.normals
        }
        swapListeners.forEachUpdated { it() }

        // this is called after update, newVal was enabled and updated, disable it and enable oldVal for next frame
        gbuffers.newVal.isEnabled = false
        gbuffers.oldVal.isEnabled = true
    }

    private fun resizeIfNeeded() {
        val newSize = size
        if (lightingPass.width != newSize.x || lightingPass.height != newSize.y) {
            logD { "Resizing to ${newSize.x}x${newSize.y}" }
            gbuffers.a.setSize(newSize.x, newSize.y)
            gbuffers.b.setSize(newSize.x, newSize.y)
            aoPass.resize(newSize.x, newSize.y)
            lightingPass.setSize(newSize.x, newSize.y)
            filterPass.resize(newSize)
            resizeListeners.forEachUpdated { it(newSize) }
        }
    }

    fun onResize(block: (Vec2i) -> Unit) {
        resizeListeners += block
    }

    fun onSwap(block: () -> Unit) {
        swapListeners += block
    }

    companion object {
        private val s = 1f/8f
        val TSAA_NONE = listOf(Vec2f.ZERO)
        val TSAA_2 = listOf(
            Vec2f(4 * s, 4 * s),
            Vec2f(-4 * s, -4 * s),
        )
        val TSAA_4 = listOf(
            Vec2f(-2 * s, -6 * s),
            Vec2f(6 * s, -2 * s),
            Vec2f(-6 * s, -2 * s),
            Vec2f(2 * s, 6 * s),
        )
        val TSAA_8 = listOf(
            Vec2f(1 * s, -3 * s),
            Vec2f(7 * s, -7 * s),
            Vec2f(3 * s, 7 * s),
            Vec2f(-3 * s, -5 * s),
            Vec2f(-1 * s, 3 * s),
            Vec2f(5 * s, 1 * s),
            Vec2f(-7 * s, -1 * s),
            Vec2f(-5 * s, -5 * s),
        )
        val TSAA_16 = listOf(
            Vec2f(1 * s, 1 * s),
            Vec2f(-7 * s, -8 * s),
            Vec2f(4 * s, -1 * s),
            Vec2f(7 * s, -4 * s),

            Vec2f(-2 * s, 6 * s),
            Vec2f(-8 * s, 0 * s),
            Vec2f(-1 * s, -3 * s),
            Vec2f(6 * s, 7 * s),

            Vec2f(-3 * s, 2 * s),
            Vec2f(3 * s, -5 * s),
            Vec2f(-5 * s, -2 * s),
            Vec2f(2 * s, 5 * s),

            Vec2f(-6 * s, 4 * s),
            Vec2f(0 * s, -7 * s),
            Vec2f(-4 * s, -6 * s),
            Vec2f(5 * s, 3 * s),
        )
    }
}

fun Lighting.createShadowMaps(
    sceneContent: Node,
    sceneCam: Camera,
    range: Float = 100f,
    mapSize: Int = 2048,
): List<ShadowMap> {
    val shadows = mutableListOf<ShadowMap>()
    for (light in lights) {
        val shadowMap: ShadowMap? = when (light) {
            is Light.Directional -> CascadedShadowMap(sceneCam, sceneContent, light, maxRange = range, mapSizes = List(3) { mapSize })
            is Light.Spot -> SimpleShadowMap(sceneCam, sceneContent, light, mapSize)
            is Light.Point -> {
                logW { "Point light shadow maps not yet supported" }
                null
            }
        }
        shadowMap?.let { shadows += shadowMap }
    }
    return shadows
}

fun makeDitherPattern(): Texture2d {
    val buf = Uint8Buffer(16)
    fun u(i: Int): UByte = (255f * (i-1).toFloat() / (buf.capacity - 1)).toInt().toUByte()

    buf[0] = u(1)
    buf[1] = u(9)
    buf[2] = u(3)
    buf[3] = u(11)

    buf[4] = u(13)
    buf[5] = u(5)
    buf[6] = u(15)
    buf[7] = u(7)

    buf[8] = u(4)
    buf[9] = u(12)
    buf[10] = u(2)
    buf[11] = u(10)

    buf[12] = u(16)
    buf[13] = u(8)
    buf[14] = u(14)
    buf[15] = u(6)

    val data = BufferedImageData2d(buf, 4, 4, TexFormat.R)
    return Texture2d(data)
}

class AlternatingPair<out T>(factory: (Boolean) -> T) {
    val a: T = factory(true)
    val b: T = factory(false)

    val newVal: T get() = if (Time.frameCount % 2 == 0) a else b
    val oldVal: T get() = if (Time.frameCount % 2 == 0) b else a
}

object DeferredCamDataLayout : Struct("deferred_cam_data", MemoryLayout.Std140) {
    val proj = mat4("proj")
    val view = mat4("view")
    val viewProj = mat4("viewProj")
    val invView = mat4("invView")
    val invViewProj = mat4("invViewProj")
    val oldViewProj = mat4("oldViewProj")
    val camPosition = float3("camPosition")
    val camNear = float1("camClipNear")
    val frameIdx = int1("frameIdx")
}

context(_: KslScopeBuilder)
val KslStructStorage<DeferredCamDataLayout>.proj: KslExprMat4 get() = this[0.const][DeferredCamDataLayout.proj]
context(_: KslScopeBuilder)
val KslStructStorage<DeferredCamDataLayout>.view: KslExprMat4 get() = this[0.const][DeferredCamDataLayout.view]
context(_: KslScopeBuilder)
val KslStructStorage<DeferredCamDataLayout>.viewProj: KslExprMat4 get() = this[0.const][DeferredCamDataLayout.viewProj]
context(_: KslScopeBuilder)
val KslStructStorage<DeferredCamDataLayout>.invView: KslExprMat4 get() = this[0.const][DeferredCamDataLayout.invView]
context(_: KslScopeBuilder)
val KslStructStorage<DeferredCamDataLayout>.invViewProj: KslExprMat4 get() = this[0.const][DeferredCamDataLayout.invViewProj]
context(_: KslScopeBuilder)
val KslStructStorage<DeferredCamDataLayout>.oldViewProj: KslExprMat4 get() = this[0.const][DeferredCamDataLayout.oldViewProj]
context(_: KslScopeBuilder)
val KslStructStorage<DeferredCamDataLayout>.camPosition: KslExprFloat3 get() = this[0.const][DeferredCamDataLayout.camPosition]
context(_: KslScopeBuilder)
val KslStructStorage<DeferredCamDataLayout>.camNear: KslExprFloat1 get() = this[0.const][DeferredCamDataLayout.camNear]
context(_: KslScopeBuilder)
val KslStructStorage<DeferredCamDataLayout>.frameIdx: KslExprInt1 get() = this[0.const][DeferredCamDataLayout.frameIdx]

fun Deferred2Pipeline.installBloomPass(): BloomPass {
    val bloomPass = BloomPass(filterPass.filterOutput.newVal)
    bloomPass.isProfileGpu = true
    scene.addComputePass(bloomPass)
    onSwap {
        val filterOutput = filterPass.filterOutput.newVal
        bloomPass.inputShader.swapPipelineData(filterOutput) {
            bloomPass.inputTexture = filterOutput
        }
    }
    return bloomPass
}

fun Deferred2Pipeline.defaultOutputQuad(
    bloomPass: BloomPass?,
    writeDepth: Boolean = false,
    vignette: Vignette? = null,
    chromaticAberration: Vec3f? = null,
): Mesh<*> {
    val outputShader = defaultOutputShader(bloomPass, writeDepth, vignette, chromaticAberration)
    return TextureMesh().apply {
        generate {
            generateFullscreenQuad()
        }
        shader = outputShader
    }
}

fun Deferred2Pipeline.defaultOutputShader(
    bloomPass: BloomPass?,
    writeDepth: Boolean = false,
    vignette: Vignette? = null,
    chromaticAberration: Vec3f? = null,
): KslShader {
    val pipelineConfig = if (writeDepth) {
        PipelineConfig(blendMode = BlendMode.DISABLED)
    } else {
        PipelineConfig(
            blendMode = BlendMode.DISABLED,
            depthTest = DepthCompareOp.ALWAYS,
            isWriteDepth = false,
        )
    }

    val outputShader = KslShader("deferred2-output", pipelineConfig) {
        val uv = interStageFloat2()
        fullscreenQuadVertexStage(uv)
        fragmentStage {
            val output = texture2d("deferredOutput")
            val bloom = texture2d("bloomOutput")
            val ditherTex = texture2d("ditherPattern")
            val depthTex = if (writeDepth) texture2d("depth", isUnfilterable = true) else null
            val vignetteR = if (vignette != null) uniformFloat2("vignetteR") else null
            val aberrationCfg = if (chromaticAberration != null) uniformFloat3("aberrationCfg") else null

            val fnSampleRgb = functionFloat3("fnSampleRgb") {
                val tex = paramColorTex2d()
                val uv = paramFloat2()
                body {
                    if (aberrationCfg == null) {
                        tex.sample(uv).rgb
                    } else {
                        val centerUv by uv - 0.5f.const
                        val s by length(centerUv) / 0.3f.const
                        val str by aberrationCfg * s * s
                        val uvR by centerUv * (1f.const + str.r)
                        val uvG by centerUv * (1f.const + str.g)
                        val uvB by centerUv * (1f.const + str.b)
                        val r by tex.sample(uvR + 0.5f.const).r
                        val g by tex.sample(uvG + 0.5f.const).g
                        val b by tex.sample(uvB + 0.5f.const).b
                        float3Value(r, g, b)
                    }
                }
            }

            main {
                val uvi = (uv.output * output.size().toFloat2()).toInt2()
                val color by fnSampleRgb(output, uv.output) + fnSampleRgb(bloom, uv.output)
                val ditherC by uvi % ditherTex.size()
                val ditherNoise by ditherTex.load(ditherC).r
                val srgb by convertColorSpace(color, ColorSpaceConversion.LinearToSrgbHdr()) + (ditherNoise - 0.5f.const) / 255f.const

                vignetteR?.let {
                    val vignetteColor = uniformFloat4("vignetteColor")
                    val uvR by length(uv.output - 0.5f.const2)
                    val vignetteF by smoothStep(vignetteR.x, vignetteR.y, uvR) * vignetteColor.a
                    srgb set mix(srgb, vignetteColor.rgb, vignetteF)
                }
                colorOutput(srgb)

                depthTex?.let {
                    outDepth set it.load(uvi).x
                }
            }
        }
    }

    val ditherTex = makeDitherPattern()
    ditherTex.releaseWith(filterPass)
    outputShader.bindTexture2d("ditherPattern", ditherTex)
    val bloomMap = bloomPass?.bloomMap ?: SingleColorTexture(Color.BLACK)
    outputShader.bindTexture2d("bloomOutput", bloomMap)
    var inputTex by outputShader.bindTexture2d("deferredOutput", defaultSampler = SamplerSettings().nearest().clamped())
    var inputDepth by outputShader.bindTexture2d("depth")
    vignette?.let {
        outputShader.bindUniformFloat2("vignetteR", Vec2f(it.innerRadius, it.outerRadius))
        outputShader.bindUniformColor("vignetteColor", it.vignetteColor)
    }
    chromaticAberration?.let {
        outputShader.bindUniformFloat3("aberrationCfg", it)
    }
    onSwap {
        val filterOutput = filterPass.filterOutput.newVal
        outputShader.swapPipelineData(filterOutput) {
            inputTex = filterOutput
            if (writeDepth) {
                inputDepth = gbuffers.newVal.depth
            }
        }
    }
    return outputShader
}

data class Vignette(val innerRadius: Float = 0.4f, val outerRadius: Float = 0.71f, val vignetteColor: Color = Color.BLACK.withAlpha(0.3f))

val ChromaticAberrationDefault = Vec3f(-0.003f, 0.0f, 0.003f)
