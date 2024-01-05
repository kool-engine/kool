package de.fabmax.kool.mock

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.backend.RenderBackend

class TestKoolContext(
    override val backend: RenderBackend = MockBackend()
) : KoolContext() {

    override val isJavascript: Boolean = false
    override val isJvm: Boolean = true
    override val windowWidth: Int = 1600
    override val windowHeight: Int = 900
    override var isFullscreen: Boolean = false

    override fun run() {
        throw IllegalStateException("TextKoolContext cannot be run")
    }

    override fun openUrl(url: String, sameWindow: Boolean) {
        println("open url: $url")
    }

    override fun getSysInfos(): List<String> = emptyList()
}