package de.fabmax.kool.rendertest

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.backend.vk.Instance
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.headless.HeadlessInput
import de.fabmax.kool.platform.headless.HeadlessWindow
import de.fabmax.kool.platform.headless.HeadlessWindowSubsystem
import de.fabmax.kool.scene.Scene
import kotlinx.coroutines.delay
import java.io.File
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

/**
 * Runs a render test: [block] is executed on the render thread of the shared headless [RenderTestContext]. Scenes
 * added via [RenderTestScope.addScene] are removed and released after the test. The test fails if the Vulkan
 * validation layer reports any errors while the test is running.
 */
fun renderTest(
    width: Int = RenderTestContext.DEFAULT_WIDTH,
    height: Int = RenderTestContext.DEFAULT_HEIGHT,
    block: suspend RenderTestScope.() -> Unit
) = RenderTestContext.runOnRenderThread {
    val instance = RenderTestContext.backend.instance
    if (RenderTestContext.isValidationRequired && !instance.isValidationActive) {
        fail("Vulkan validation layer is required (kool.renderTest.requireValidation=true) but not available")
    }

    // make sure the window has the requested size before the test starts
    val headless = RenderTestContext.headless
    headless.window.setSize(Vec2i(width, height), 1f)
    headless.renderFrame()
    instance.clearValidationErrors()

    val scope = RenderTestScope(headless)
    try {
        scope.block()
    } finally {
        scope.tearDown()
    }

    assertNoValidationErrors(instance)
}

/**
 * Fails if the Vulkan validation layer reported any errors since the last call to [Instance.clearValidationErrors].
 * Clears the reported errors.
 */
fun assertNoValidationErrors(instance: Instance) {
    val errorCount = instance.validationErrorCount
    if (errorCount > 0) {
        val errors = instance.validationErrors.joinToString("\n\n")
        instance.clearValidationErrors()
        fail("Vulkan validation layer reported $errorCount errors:\n$errors")
    }
}

class RenderTestScope internal constructor(val headless: HeadlessWindowSubsystem) {
    val ctx: Lwjgl3Context get() = RenderTestContext.ctx
    val window: HeadlessWindow get() = headless.window
    val input: HeadlessInput get() = headless.input

    private val scenes = mutableListOf<Scene>()

    /**
     * Creates a new scene, adds it to the context and removes / releases it after the test.
     */
    fun addScene(name: String? = null, block: Scene.() -> Unit): Scene {
        val scene = Scene(name).apply(block)
        addScene(scene)
        return scene
    }

    fun addScene(scene: Scene) {
        scenes += scene
        ctx.addScene(scene)
    }

    suspend fun renderFrame() = headless.renderFrame()

    suspend fun renderFrames(count: Int) = headless.renderFrames(count)

    /**
     * Renders frames until [condition] becomes true. Useful to wait for asynchronously loaded resources (e.g.
     * textures loaded from assets). Fails if the condition is not met within [timeout].
     */
    suspend fun renderUntil(timeout: Duration = 10.seconds, condition: () -> Boolean) {
        val mark = TimeSource.Monotonic.markNow()
        while (!condition()) {
            if (mark.elapsedNow() > timeout) {
                fail("Condition not met within $timeout")
            }
            renderFrame()
            // assets are loaded by other threads, don't hog the CPU
            delay(1)
        }
    }

    /**
     * Renders frames until all given textures are loaded (see [renderUntil]).
     */
    suspend fun awaitTexturesLoaded(vararg textures: Texture<*>, timeout: Duration = 10.seconds) {
        renderUntil(timeout) { textures.all { it.isLoaded } }
    }

    /**
     * Renders [warmupFrames] frames followed by a captured frame, which is returned.
     */
    suspend fun captureFrame(warmupFrames: Int = DEFAULT_WARMUP_FRAMES): BufferedImageData2d {
        return headless.captureFrame(warmupFrames)
    }

    /**
     * Captures a frame (see [captureFrame]) and compares it with the reference image [referenceName].
     */
    suspend fun assertFrameMatchesReference(
        referenceName: String,
        tolerance: ImageTolerance = ImageTolerance.Default,
        warmupFrames: Int = DEFAULT_WARMUP_FRAMES,
    ) {
        assertMatchesReference(referenceName, captureFrame(warmupFrames), tolerance)
    }

    fun assertMatchesReference(
        referenceName: String,
        image: BufferedImageData2d,
        tolerance: ImageTolerance = ImageTolerance.Default,
    ) = assertImageMatchesReference(referenceName, image, tolerance)

    internal suspend fun tearDown() {
        input.mouseExit()
        scenes.forEach { ctx.removeScene(it) }
        // render a frame without the removed scenes before releasing them
        headless.renderFrame()
        scenes.forEach { it.release() }
        scenes.clear()
        // let the backend process the deferred release of GPU resources
        headless.renderFrames(3)
    }

    companion object {
        const val DEFAULT_WARMUP_FRAMES = 2
    }
}

/**
 * Compares the given image with the reference image [referenceName] (a path relative to the reference image
 * directory, without file extension). If reference images are updated (`kool.renderTest.updateReferences=true`),
 * the given image is written as new reference image instead.
 */
fun assertImageMatchesReference(
    referenceName: String,
    image: BufferedImageData2d,
    tolerance: ImageTolerance = ImageTolerance.Default,
) {
    val actual = image.toBufferedImage()
    val referenceFile = File(RenderTestContext.referenceDir, "$referenceName.png")

    if (RenderTestContext.isUpdateReferences) {
        actual.writePng(referenceFile)
        println("Updated reference image: $referenceFile")
        return
    }

    val reportPrefix = File(RenderTestContext.reportDir, referenceName).path
    val actualFile = File("$reportPrefix-actual.png")
    if (!referenceFile.exists()) {
        actual.writePng(actualFile)
        fail(
            "Reference image $referenceFile does not exist. Rendered image was written to $actualFile. " +
                    "Run with -Pkool.renderTest.updateReferences=true to generate reference images."
        )
    }

    val expected = readPng(referenceFile)
    if (expected.width != actual.width || expected.height != actual.height) {
        actual.writePng(actualFile)
        fail(
            "Rendered image size (${actual.width} x ${actual.height}) differs from reference image size " +
                    "(${expected.width} x ${expected.height}): $referenceFile. Rendered image: $actualFile"
        )
    }

    val result = ImageComparison.compare(actual, expected, tolerance)
    if (!result.isWithin(tolerance)) {
        actual.writePng(actualFile)
        expected.writePng(File("$reportPrefix-expected.png"))
        result.diffImage.writePng(File("$reportPrefix-diff.png"))
        fail("Rendered image differs from reference $referenceName: $result (tolerance: $tolerance). See $reportPrefix-*.png")
    }
}
