package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.Lighting
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

private const val renderScale = 0.5f
val tsaa = GbufferPass.TSAA_PATTERN_8

class Deferred2Pipeline(
    val content: Node,
    val lighting: Lighting = Lighting(),
    scene: Scene,
) {
    val camera = PerspectiveCamera()

    val gbuffers = AlternatingPair {
        val suff = if (it) "A" else "B"
        GbufferPass(content, camera, scene.renderSize, "deferred2-gbuffer-pass-$suff")
    }

    private val swapListeners = BufferedList<() -> Unit>()

    val lightingPass = LightingPass(
        gbuffers = gbuffers,
        camera = camera,
        lighting = lighting,
        size = scene.renderSize,
    )
    val filterPass = TemporalFilterPass(
        lightingOutput = lightingPass.lightingOutput,
        gbuffers = gbuffers,
        camera = camera,
        size = scene.renderSize,
    )

    init {
        scene.addOffscreenPass(gbuffers.a)
        scene.addOffscreenPass(gbuffers.b)
        scene.addOffscreenPass(lightingPass)
        scene.addComputePass(filterPass)

        var size = scene.renderSize
        scene.onRenderScene += {
            val newSize = scene.renderSize
            if (size != newSize) {
                size = newSize
                gbuffers.a.setSize(size.x, size.y)
                gbuffers.b.setSize(size.x, size.y)
                lightingPass.setSize(size.x, size.y)
                filterPass.resize(size)
            }
        }

        scene.coroutineScope.launch {
            withContext(KoolDispatchers.Synced) {
                while (true) {
                    lightingPass.swapBuffers()
                    filterPass.swapBuffers()
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

    fun onSwap(block: () -> Unit) {
        swapListeners += block
    }

    private val Scene.renderSize: Vec2i get() = Vec2i(
        (mainRenderPass.viewport.width * renderScale).toInt().coerceAtLeast(1),
        (mainRenderPass.viewport.height * renderScale).toInt().coerceAtLeast(1)
    )
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