package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ao.AoRadius
import de.fabmax.kool.pipeline.ao.ComputeAoPass
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.pipeline.swapPipelineData
import de.fabmax.kool.scene.Lighting
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class Deferred2Pipeline(
    val content: Node,
    private val scene: Scene,
    val ibl: EnvironmentMap,
    val isScreenSpaceReflections: Boolean,
    val lighting: Lighting = Lighting(),
    var renderScale: Float = 1f,
    var tsaa: List<Vec2f> = TSAA_4,
    maxObjects: Int = 16384
) {
    val size: Vec2i get() = Vec2i(
        (scene.mainRenderPass.viewport.width * renderScale).toInt().coerceAtLeast(16),
        (scene.mainRenderPass.viewport.height * renderScale).toInt().coerceAtLeast(16)
    )
    val camera = PerspectiveCamera()
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
    val lightingPass = LightingPass(size = size, pipeline = this)
    val filterPass = TemporalFilterPass(size = size, pipeline = this)

    private val swapListeners = BufferedList<() -> Unit>()
    private val resizeListeners = BufferedList<(Vec2i) -> Unit>()

    init {
        aoPass.kernelSize = 4
        aoPass.radius = AoRadius.relativeRadius(1 / 20f)
        aoPass.temporalKernels = tsaa.size

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

        lightingPass.onRelease { camData.release() }

        val offsetMat = MutableMat4f()
        camera.onCameraUpdated += {
            val tsaa = tsaa
            if (tsaa.isNotEmpty()) {
                val offset = tsaa[Time.frameCount % tsaa.size]
                val width = it.viewport.width
                val height = it.viewport.height
                offsetMat.setIdentity().translate(offset.x / width, offset.y / height, 0f).mul(camera.proj)
                camera.proj.set(offsetMat)
                camera.lazyInvProj.isDirty = true
            }
        }

        var oldSize = size
        scene.onRenderScene += {
            val newSize = size
            if (oldSize != newSize) {
                logD { "Resizing to ${newSize.x}x${newSize.y}" }
                oldSize = newSize
                gbuffers.a.setSize(size.x, size.y)
                gbuffers.b.setSize(size.x, size.y)
                aoPass.resize(size.x, size.y)
                lightingPass.setSize(size.x, size.y)
                filterPass.resize(size)
                resizeListeners.forEachUpdated { it(size) }
            }
        }

        scene.coroutineScope.launch {
            withContext(KoolDispatchers.Synced) {
                while (true) {
                    swapBuffers()
                    yield()
                }
            }
        }
    }

    private fun swapBuffers() {
        camDataBuffer.set(0) {
            set(it.proj, camera.proj)
            set(it.view, camera.view)
            set(it.invView, camera.invView)
            set(it.invViewProj, camera.invViewProj)
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
