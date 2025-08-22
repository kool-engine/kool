package de.fabmax.kool

import de.fabmax.kool.math.MutableVec3i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.ResettableData
import de.fabmax.kool.util.ResettableDataList
import de.fabmax.kool.util.Viewport

class FrameData {
    private val _passData = ResettableDataList { PassData() }
    val passData: List<PassData> get() = _passData

    fun reset() {
        _passData.reset()
    }

    fun acquirePassData(pass: GpuPass): PassData {
        return _passData.acquire(pass)
    }

    inline fun forEachPass(block: (PassData) -> Unit) {
        for (i in 0 until passData.size) {
            block(passData[i])
        }
    }
}

class PassData : ResettableData<GpuPass> {
    private val _viewData = ResettableDataList { ViewData() }
    val viewData: List<ViewData> get() = _viewData

    lateinit var gpuPass: GpuPass; private set

    private val _frameCopies = mutableListOf<FrameCopy>()
    val frameCopies: List<FrameCopy> get() = _frameCopies

    val isCopySource: Boolean get() {
        if (frameCopies.isNotEmpty()) {
            return true
        }
        for (i in viewData.indices) {
            if (viewData[i].frameCopies.isNotEmpty()) {
                return true
            }
        }
        return false
    }

    override fun reset(init: GpuPass) {
        _viewData.reset()
        gpuPass = init
        _frameCopies.clear()
        if (init is RenderPass) {
            _frameCopies.addAll(init.frameCopies)
        }
    }

    fun acquireViewData(view: RenderPass.View): ViewData {
        return _viewData.acquire(view)
    }

    inline fun forEachView(block: (ViewData) -> Unit) {
        for (i in 0 until viewData.size) {
            block(viewData[i])
        }
    }
}

class ViewData : ResettableData<RenderPass.View> {
    val passDimensions = MutableVec3i()
    var viewport = Viewport.EMPTY
    var numMipLevels = 0
    var depthMode = DepthMode.Reversed
    val drawQueue = DrawQueue()

    private val _frameCopies = mutableListOf<FrameCopy>()
    val frameCopies: List<FrameCopy> get() = _frameCopies

    override fun reset(init: RenderPass.View) {
        passDimensions.set(init.renderPass.dimensions)
        viewport = init.viewport
        numMipLevels = init.renderPass.numRenderMipLevels
        depthMode = init.renderPass.depthMode
        _frameCopies.clear()
        _frameCopies.addAll(init.frameCopies)
        drawQueue.reset(init)
    }
}
