package de.fabmax.kool.mock

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.util.RenderLoopCoroutineDispatcher
import kotlin.concurrent.thread

class TestKoolContext(
    override val backend: RenderBackend = MockBackend()
) : KoolContext() {
    override var renderScale: Float = 1f

    override val windowWidth: Int = 1600
    override val windowHeight: Int = 900
    override var isFullscreen: Boolean = false

    override fun run() {
        thread(isDaemon = true) {
            while (true) {
                Thread.sleep(16)
                RenderLoopCoroutineDispatcher.executeDispatchedTasks()
            }
        }
    }

    override fun openUrl(url: String, sameWindow: Boolean) {
        println("open url: $url")
    }

    override fun getSysInfos(): List<String> = emptyList()
}