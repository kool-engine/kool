package de.fabmax.kool.platform

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.backend.gl.RenderBackendGlImpl
import de.fabmax.kool.util.RenderLoopCoroutineDispatcher
import de.fabmax.kool.util.logE

class AndroidContext : KoolContext() {
    override val backend: RenderBackendGlImpl

    override val windowWidth: Int
        get() = backend.viewWidth
    override val windowHeight: Int
        get() = backend.viewHeight

    // todo: not really applicable on android?
    override var isFullscreen: Boolean
        get() = false
        set(_) { }

    private var prevFrameTime = System.nanoTime()

    init {
        check(!KoolSystem.isContextCreated) { "KoolContext was already created" }

        // todo: set correct value
        windowScale = 2f

        backend = RenderBackendGlImpl(this)
        KoolSystem.onContextCreated(this)
    }

    override fun openUrl(url: String, sameWindow: Boolean) {
        logE { "Open URL: $url" }
    }

    override fun run() {

    }

    override fun getSysInfos(): List<String> {
        return emptyList()
    }

    internal fun renderFrame() {
        RenderLoopCoroutineDispatcher.executeDispatchedTasks()

        // determine time delta
        val time = System.nanoTime()
        val dt = (time - prevFrameTime) / 1e9
        prevFrameTime = time

        // setup draw queues for all scenes / render passes
        render(dt)

        // execute draw queues
        backend.renderFrame(this)
    }
}