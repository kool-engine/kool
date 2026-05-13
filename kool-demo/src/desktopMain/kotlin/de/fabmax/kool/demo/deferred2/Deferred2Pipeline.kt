package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.demo.deferred2.GbufferPass.Companion.TSAA_PATTERN_4
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.Lighting
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.KoolDispatchers
import de.fabmax.kool.util.Uint8Buffer
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

private const val renderScale = 0.75f
val tsaa = TSAA_PATTERN_4

class Deferred2Pipeline(
    val content: Node,
    val lighting: Lighting = Lighting(),
    scene: Scene,
) {
    val camera = PerspectiveCamera()

    val gbuffers = AlternatingPair {
        GbufferPass(content, camera, scene.renderSize)
    }

    // fixme: think of something better for providing a camera
    //val sceneCam get() = gbufferPass.camera

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
                    gbuffers.newVal.isEnabled = true
                    gbuffers.oldVal.isEnabled = false
                    lightingPass.swapBuffers()
                    filterPass.swapBuffers()
                    yield()
                }
            }
        }
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
