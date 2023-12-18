package de.fabmax.kool.mock

import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera

class MockRenderPass(
    override val width: Int = Mock.testCtx.windowWidth,
    override val height: Int = Mock.testCtx.windowHeight,
    override val depth: Int = 1,
    override val isReverseDepth: Boolean = false
) : RenderPass("mock-render-pass") {

    val mockView = View("mock-view", Node(), PerspectiveCamera(), arrayOf())
    override val views = listOf(mockView)

    val updateEvent = views[0].makeUpdateEvent(Mock.testCtx)
}