package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ao.AoRadius
import de.fabmax.kool.pipeline.ao.ComputeAoPass
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.pipeline.swapPipelineDataCapturing
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
    val lighting: Lighting = Lighting(),
    var renderScale: Float = 1f,
    var tsaa: List<Vec2f> = TSAA_4,
) {
    val camera = PerspectiveCamera()
    val size: Vec2i get() = Vec2i(
        (scene.mainRenderPass.viewport.width * renderScale).toInt().coerceAtLeast(16),
        (scene.mainRenderPass.viewport.height * renderScale).toInt().coerceAtLeast(16)
    )

    val gbuffers = AlternatingPair {
        val suff = if (it) "A" else "B"
        GbufferPass(content, camera, size, "deferred2-gbuffer-pass-$suff", this)
    }

    val aoPass: ComputeAoPass = ComputeAoPass(
        camera = camera,
        inputDepth = gbuffers.a.depth,
        inputNormals = gbuffers.a.normals,
        initialSize = size
    )

    private val swapListeners = BufferedList<() -> Unit>()
    private val resizeListeners = BufferedList<(Vec2i) -> Unit>()

    val lightingPass = LightingPass(
        gbuffers = gbuffers,
        camera = camera,
        lighting = lighting,
        size = size,
        ssaoMap = aoPass.aoMap,
        ibl = ibl,
    )
    val filterPass = TemporalFilterPass(
        lightingOutput = lightingPass.lightingOutput,
        gbuffers = gbuffers,
        camera = camera,
        size = size,
        pipeline = this,
    )

    internal val prevViewProjMats = List(1024) { MutableMat4f() }
    internal val oldViewProj = MutableMat4f()
    private val prevViewProj = MutableMat4f()

    init {
        aoPass.kernelSize = 16
        aoPass.radius = AoRadius.relativeRadius(0.05f)
        aoPass.temporalKernels = tsaa.size

        scene.addOffscreenPass(gbuffers.a)
        scene.addOffscreenPass(gbuffers.b)
        scene.addComputePass(aoPass)
        scene.addOffscreenPass(lightingPass)
        scene.addComputePass(filterPass)

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
                    oldViewProj.set(prevViewProj)
                    prevViewProj.set(camera.viewProj)

                    lightingPass.swapBuffers()
                    filterPass.swapBuffers()

                    val currentGbuffer = gbuffers.newVal
                    aoPass.inputShader.swapPipelineDataCapturing(currentGbuffer) {
                        aoPass.inputDepth = currentGbuffer.depth
                        aoPass.inputNormals = currentGbuffer.normals
                        //aoPass.captureCamera()
                    }

                    swapListeners.forEachUpdated { it() }

                    gbuffers.newVal.objModelMatsGpu.uploadData(gbuffers.newVal.objModelMats)

                    // this is called after update, newVal was enabled and updated, disable it and enable oldVal for next frame
                    gbuffers.newVal.isEnabled = false
                    gbuffers.oldVal.isEnabled = true
                    yield()
                }
            }
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
            Vec2f(-5 * s, -2 * s),
            Vec2f(-2 * s, 6 * s),
            Vec2f(-8 * s, 0 * s),

            Vec2f(-1 * s, -3 * s),
            Vec2f(2 * s, 5 * s),
            Vec2f(0 * s, -7 * s),
            Vec2f(7 * s, -4 * s),

            Vec2f(-3 * s, 2 * s),
            Vec2f(5 * s, 3 * s),
            Vec2f(-4 * s, -6 * s),
            Vec2f(6 * s, 7 * s),

            Vec2f(4 * s, -1 * s),
            Vec2f(3 * s, -5 * s),
            Vec2f(-6 * s, 4 * s),
            Vec2f(-7 * s, -8 * s),
        )
    }
}

fun makeDitherPattern(): Texture2d {
    val buf = Uint8Buffer(16)
    fun u(i: Int): UByte = (255f * i.toFloat() / (buf.capacity - 1)).toInt().toUByte()

    buf[0] = u(0)
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

object ObjModelMatLayout : Struct("obj_model_mat", MemoryLayout.Std140) {
    val reprojectMat = mat4("reprojectMat")
}
