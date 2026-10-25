package de.fabmax.kool.rendertest

import de.fabmax.kool.KoolConfigJvm
import de.fabmax.kool.createContext
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.backend.vk.RenderBackendVk
import de.fabmax.kool.pipeline.backend.vk.VkSetup
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.headless.HeadlessFrameLoop
import de.fabmax.kool.platform.headless.HeadlessWindowSubsystem
import de.fabmax.kool.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.seconds

/**
 * Shared headless kool context used by all render tests within a JVM (there can only be one context per JVM). The
 * context is created lazily on first use and is never shut down explicitly, it lives until the test JVM exits.
 *
 * All interactions with the context happen on a dedicated render thread, see [runOnRenderThread].
 *
 * Configuration is done via system properties (forwarded from gradle properties with the same name by the
 * `renderTest` task, e.g. `./gradlew :kool-core:renderTest -Pkool.renderTest.device=llvmpipe`):
 *
 * - `kool.renderTest.updateReferences`: If true, reference images are (re-)generated instead of compared.
 * - `kool.renderTest.referenceDir`: Directory containing the reference images.
 * - `kool.renderTest.reportDir`: Directory, actual / expected / diff images of failed tests are written to.
 * - `kool.renderTest.device`: Name (or part of the name) of the Vulkan device to use, e.g. "llvmpipe" for lavapipe.
 * - `kool.renderTest.validation`: Enables the Vulkan validation layer (default: true, ignored if layer isn't available).
 * - `kool.renderTest.requireValidation`: Fails tests if the validation layer is not available (default: false).
 * - `kool.renderTest.logLevel`: Log level used during render tests (default: WARN).
 */
object RenderTestContext {
    val isUpdateReferences: Boolean = System.getProperty("kool.renderTest.updateReferences").toBoolean()
    val referenceDir = File(System.getProperty("kool.renderTest.referenceDir", "src/desktopTest/resources/render-references"))
    val reportDir = File(System.getProperty("kool.renderTest.reportDir", "build/reports/render-tests"))

    private val deviceName: String? = System.getProperty("kool.renderTest.device")?.ifBlank { null }
    private val isValidation: Boolean = System.getProperty("kool.renderTest.validation")?.toBoolean() ?: true
    val isValidationRequired: Boolean = System.getProperty("kool.renderTest.requireValidation").toBoolean()
    private val logLevel: Log.Level = System.getProperty("kool.renderTest.logLevel")
        ?.let { Log.Level.valueOf(it.uppercase()) } ?: Log.Level.WARN

    /**
     * Fixed time step used for each frame, which makes animations deterministic.
     */
    val timeStep = (1.0 / 60.0).seconds

    private val renderThread = Executors.newSingleThreadExecutor { r ->
        Thread(r, "kool-render-test").apply { isDaemon = true }
    }.asCoroutineDispatcher()

    private var _headless: HeadlessWindowSubsystem? = null
    private var _ctx: Lwjgl3Context? = null

    val headless: HeadlessWindowSubsystem get() = checkNotNull(_headless) { "Render test context not initialized" }
    val ctx: Lwjgl3Context get() = checkNotNull(_ctx) { "Render test context not initialized" }
    val backend: RenderBackendVk get() = ctx.backend as RenderBackendVk

    /**
     * Executes the given block on the render thread and blocks until it completes. Creates the context if it
     * does not exist yet.
     */
    fun <T> runOnRenderThread(block: suspend CoroutineScope.() -> T): T = runBlocking(renderThread) {
        if (_ctx == null) {
            createTestContext()
        }
        block()
    }

    private suspend fun createTestContext() {
        Log.level = logLevel
        val headless = HeadlessWindowSubsystem(
            initialSize = Vec2i(DEFAULT_WIDTH, DEFAULT_HEIGHT),
            frameLoop = HeadlessFrameLoop.Manual,
            fixedTimeStep = timeStep,
        )
        val config = KoolConfigJvm(
            renderBackend = RenderBackendVk,
            windowSubsystem = headless,
            vkSetup = VkSetup(isValidation = isValidation, forceDeviceName = deviceName),
            useOpenGlFallback = false,
            asyncSceneUpdate = false,
            numSamples = 4,
            storageDir = "build/render-test-storage",
            httpCacheDir = "build/render-test-http-cache",
        )
        _headless = headless
        _ctx = createContext(config)

        val validationState = when {
            backend.instance.isValidationActive -> "active"
            isValidation -> "requested but not available"
            else -> "disabled"
        }
        // always print this, independent of the log level, so that it is clear what the tests were run with
        println("Render test context created, device: ${backend.deviceName}, validation: $validationState")
    }

    const val DEFAULT_WIDTH = 320
    const val DEFAULT_HEIGHT = 240
}
