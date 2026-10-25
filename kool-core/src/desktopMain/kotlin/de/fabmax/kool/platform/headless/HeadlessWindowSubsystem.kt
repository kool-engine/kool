package de.fabmax.kool.platform.headless

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.backend.vk.RenderBackendVk
import de.fabmax.kool.platform.*
import de.fabmax.kool.util.logI
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration

/**
 * Window subsystem for running kool without any window ("headless mode"). Frames are rendered into an offscreen
 * image, which can be captured ([captureFrame]) or streamed to frame listeners ([onFrame]). Input events can be
 * forwarded via [input].
 *
 * Use cases are rendering tests (comparing rendered frames against reference images) and embedding kool into other
 * UI frameworks. Headless mode is only supported by the Vulkan backend ([RenderBackendVk]). No windowing library
 * (SDL / GLFW / AWT) is initialized, so there are no main-thread requirements (e.g. `-XstartOnFirstThread` on macOS).
 *
 * Example:
 * ```
 * val headless = HeadlessWindowSubsystem(initialSize = Vec2i(800, 600))
 * val ctx = createContext(KoolConfigJvm(windowSubsystem = headless, renderBackend = RenderBackendVk))
 * ctx.scenes += myScene
 * val image: BufferedImageData2d = headless.captureFrame()
 * ```
 */
class HeadlessWindowSubsystem(
    /**
     * Initial window (= output image) size in pixels. Can be changed later via [HeadlessWindow.setSize].
     */
    val initialSize: Vec2i = Vec2i(1280, 720),

    /**
     * Initial content scale, i.e. the UI scale of the host (1.0 = 100%).
     */
    val initialScale: Float = 1f,

    /**
     * Pixel format of the frames passed to [onFrame] listeners. Frames returned by [captureFrame] are always RGBA.
     */
    val pixelFormat: HeadlessPixelFormat = HeadlessPixelFormat.RGBA8,

    /**
     * Determines whether frames are rendered on request (default, for tests) or continuously by a render loop.
     */
    val frameLoop: HeadlessFrameLoop = HeadlessFrameLoop.Manual,

    /**
     * If set, each frame advances the time by exactly this duration (instead of the real elapsed time). This makes
     * time-based animations deterministic, which is useful for tests.
     */
    val fixedTimeStep: Duration? = null,
) : WindowSubsystem {

    private var _window: HeadlessWindow? = null
    val window: HeadlessWindow get() = checkNotNull(_window) { "Headless window not yet created" }

    override val input: HeadlessInput = HeadlessInput(this)

    private val closeRequested = AtomicBoolean(false)
    override val isCloseRequested: Boolean get() = closeRequested.get()

    private val frameListeners = CopyOnWriteArrayList<(HeadlessFrame) -> Unit>()
    private val pendingCaptures = ConcurrentLinkedQueue<CompletableDeferred<BufferedImageData2d>>()

    private val ctx: Lwjgl3Context get() = KoolSystem.requireContext() as Lwjgl3Context

    private var isShutdown = false

    /**
     * Registers a listener, which is called for every rendered frame on the kool render thread. The frame's pixel
     * data is only valid during the call. Frames are only read back from the GPU while at least one listener
     * is registered.
     */
    fun onFrame(listener: (HeadlessFrame) -> Unit): (HeadlessFrame) -> Unit {
        frameListeners += listener
        return listener
    }

    fun removeFrameListener(listener: (HeadlessFrame) -> Unit) {
        frameListeners -= listener
    }

    /**
     * Renders a single frame. Only available with [HeadlessFrameLoop.Manual], must be called from the thread the
     * context was created on.
     */
    suspend fun renderFrame() {
        check(frameLoop == HeadlessFrameLoop.Manual) {
            "renderFrame() is only available in HeadlessFrameLoop.Manual mode, frames are rendered automatically in $frameLoop mode"
        }
        check(!isShutdown) { "Headless context was already shut down" }
        window.pollEvents()
        ctx.renderFrame()
    }

    /**
     * Renders [count] frames. Only available with [HeadlessFrameLoop.Manual].
     */
    suspend fun renderFrames(count: Int) {
        repeat(count) { renderFrame() }
    }

    /**
     * Captures the next rendered frame and returns it as RGBA image data.
     *
     * With [HeadlessFrameLoop.Manual], the frame is rendered by this function (after [warmupFrames] additional
     * frames, which can be used to let asynchronous stuff, like texture uploads, settle). With
     * [HeadlessFrameLoop.Continuous], the function suspends until the next frame is rendered by the render loop and
     * can be called from any thread.
     */
    suspend fun captureFrame(warmupFrames: Int = 0): BufferedImageData2d {
        val capture = CompletableDeferred<BufferedImageData2d>()
        if (frameLoop == HeadlessFrameLoop.Manual) {
            renderFrames(warmupFrames)
            pendingCaptures += capture
            renderFrame()
        } else {
            check(!isCloseRequested) { "Headless context is closed" }
            pendingCaptures += capture
        }
        return capture.await()
    }

    /**
     * Requests the context to be closed. With [HeadlessFrameLoop.Continuous], the render loop exits after the
     * current frame and the context is shut down. With [HeadlessFrameLoop.Manual], call [shutdown] afterward
     * (or instead) to release all resources.
     */
    fun close() {
        closeRequested.set(true)
    }

    /**
     * Shuts down the context and releases all resources. Only needed with [HeadlessFrameLoop.Manual], must be
     * called from the thread the context was created on. With [HeadlessFrameLoop.Continuous], use [close] instead.
     */
    fun shutdown() {
        check(frameLoop == HeadlessFrameLoop.Manual) { "In $frameLoop mode, use close() to end the render loop" }
        if (!isShutdown) {
            close()
            shutdownContext()
        }
    }

    private fun shutdownContext() {
        isShutdown = true
        (this as WindowSubsystem).shutdown()
        failPendingCaptures()
    }

    internal val isFrameRequested: Boolean get() = frameListeners.isNotEmpty() || pendingCaptures.isNotEmpty()

    internal val isSyncReadback: Boolean get() = frameLoop == HeadlessFrameLoop.Manual || pendingCaptures.isNotEmpty()

    internal fun takePendingCaptures(): List<CompletableDeferred<BufferedImageData2d>> {
        return buildList {
            while (true) {
                add(pendingCaptures.poll() ?: break)
            }
        }
    }

    internal fun deliverFrame(frame: HeadlessFrame, captures: List<CompletableDeferred<BufferedImageData2d>>) {
        frameListeners.forEach { it(frame) }
        if (captures.isNotEmpty()) {
            val image = frame.toImageData()
            captures.forEach { it.complete(image) }
        }
    }

    private fun failPendingCaptures() {
        val error = IllegalStateException("Headless context was shut down before frame was captured")
        takePendingCaptures().forEach { it.completeExceptionally(error) }
    }

    override fun queryRequiredVkExtensions(): List<String> = emptyList()

    override fun createWindow(clientApi: ClientApi, glInitCallback: GlInitCallback?, ctx: Lwjgl3Context): KoolWindowJvm {
        check(clientApi == ClientApi.UNMANAGED) { UNSUPPORTED_BACKEND_MSG }
        check(_window == null) { "HeadlessWindowSubsystem only supports a single window" }
        return HeadlessWindow(this, initialSize, initialScale).also { _window = it }
    }

    override fun onEarlyInit() {
        val backend = KoolSystem.configJvm.renderBackend
        check(backend === RenderBackendVk) {
            "$UNSUPPORTED_BACKEND_MSG Configured render backend: ${backend.displayName}"
        }
    }

    override fun onBackendCreated(ctx: Lwjgl3Context) { }

    override fun runRenderLoop() {
        val loop = frameLoop
        check(loop is HeadlessFrameLoop.Continuous) {
            "HeadlessFrameLoop.Manual does not have a render loop. Call renderFrame() / captureFrame() to render " +
                    "frames instead of KoolContext.run() / KoolApplication { }, or use HeadlessFrameLoop.Continuous."
        }
        val ctx = ctx
        val frameNanos = if (loop.maxFps > 0) 1_000_000_000L / loop.maxFps else 0L
        runBlocking {
            logI { "Starting headless render loop (max fps: ${loop.maxFps})" }
            var nextFrame = System.nanoTime()
            while (!isCloseRequested) {
                window.pollEvents()
                ctx.renderFrame()

                if (frameNanos > 0) {
                    nextFrame += frameNanos
                    val now = System.nanoTime()
                    if (nextFrame > now) {
                        Thread.sleep((nextFrame - now) / 1_000_000L, ((nextFrame - now) % 1_000_000L).toInt())
                    } else {
                        // we are behind schedule, don't try to catch up
                        nextFrame = now
                    }
                }
            }
        }
        shutdownContext()
    }

    companion object {
        private const val UNSUPPORTED_BACKEND_MSG = "HeadlessWindowSubsystem requires the Vulkan render backend: Use " +
                "KoolConfigJvm(renderBackend = RenderBackendVk, ...). Headless mode is not supported by OpenGL."
    }
}
