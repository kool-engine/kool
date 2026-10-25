package de.fabmax.kool.rendertest

import de.fabmax.kool.KoolConfigJvm
import de.fabmax.kool.createContext
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.backend.vk.RenderBackendVk
import de.fabmax.kool.pipeline.backend.vk.VkSetup
import de.fabmax.kool.platform.headless.HeadlessFrame
import de.fabmax.kool.platform.headless.HeadlessFrameLoop
import de.fabmax.kool.platform.headless.HeadlessPixelFormat
import de.fabmax.kool.platform.headless.HeadlessWindowSubsystem
import de.fabmax.kool.scene.addColorMesh
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.Log
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

/**
 * Tests headless mode with a continuous render loop, as it would be used when embedding kool into another UI
 * framework: The render loop runs on its own thread, frames are streamed to a frame listener and captured from
 * another thread. This uses its own context and therefore must be the only test in this class (the renderTest
 * task runs each test class in a separate JVM).
 */
class ContinuousRenderTest {

    @Test
    fun continuousRenderLoop() {
        Log.level = Log.Level.WARN
        val headless = HeadlessWindowSubsystem(
            initialSize = Vec2i(RenderTestContext.DEFAULT_WIDTH, RenderTestContext.DEFAULT_HEIGHT),
            pixelFormat = HeadlessPixelFormat.BGRA8,
            frameLoop = HeadlessFrameLoop.Continuous(maxFps = 60),
        )
        val config = KoolConfigJvm(
            renderBackend = RenderBackendVk,
            windowSubsystem = headless,
            vkSetup = VkSetup(
                isValidation = System.getProperty("kool.renderTest.validation")?.toBoolean() ?: true,
                forceDeviceName = System.getProperty("kool.renderTest.device")?.ifBlank { null },
            ),
            useOpenGlFallback = false,
        )
        val ctx = runBlocking { createContext(config) }
        val instance = (ctx.backend as RenderBackendVk).instance

        // same scene as in BasicRenderTest.unlitVertexColorCube -> must produce the same image
        ctx.addScene(scene {
            camera.setupCamera(position = Vec3f(2f, 2f, 3f), lookAt = Vec3f.ZERO)
            addColorMesh {
                generate {
                    cube { colored() }
                }
                shader = KslUnlitShader { color { vertexColor() } }
            }
        })

        val frames = CopyOnWriteArrayList<FrameInfo>()
        headless.onFrame { frame -> frames += FrameInfo(frame) }

        var renderLoopError: Throwable? = null
        val renderThread = thread(name = "kool-continuous-render-test") {
            try {
                ctx.run()
            } catch (e: Throwable) {
                renderLoopError = e
            }
        }

        try {
            val image = runBlocking {
                withTimeout(30.seconds) {
                    // capture from this (non-render) thread, skip a few frames to let everything settle
                    repeat(3) { headless.captureFrame() }
                    headless.captureFrame()
                }
            }
            assertImageMatchesReference("basic/unlit-vertex-color-cube", image)

            // frame listeners receive all frames in order and with the requested pixel format
            assertTrue(frames.size >= 4, "Expected at least 4 frames, got ${frames.size}")
            assertTrue(frames.all { it.format == HeadlessPixelFormat.BGRA8 && it.width == image.width && it.height == image.height })
            assertEquals(frames.map { it.frameCount }.sorted(), frames.map { it.frameCount }, "Frames not delivered in order")
            assertEquals(frames.size, frames.map { it.frameCount }.distinct().size, "Frames delivered more than once")
        } finally {
            headless.close()
            renderThread.join(10_000)
        }
        assertFalse(renderThread.isAlive, "Render loop did not terminate after close()")
        renderLoopError?.let { throw it }

        // includes errors reported during shutdown (e.g. leaked objects)
        assertNoValidationErrors(instance)
    }

    private class FrameInfo(frame: HeadlessFrame) {
        val width = frame.width
        val height = frame.height
        val format = frame.format
        val frameCount = frame.frameCount
    }
}
