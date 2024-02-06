package de.fabmax.kool.mock

import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.util.Color

class MockRenderPass(
    override val width: Int = Mock.testCtx.windowWidth,
    override val height: Int = Mock.testCtx.windowHeight,
    override val depth: Int = 1,
) : RenderPass("mock-render-pass") {

    val mockView = View("mock-view", Node(), PerspectiveCamera())
    override val clearColors: Array<Color?> = arrayOf(Color.BLACK)
    override val views = listOf(mockView)

    val updateEvent = views[0].makeUpdateEvent(Mock.testCtx)
}