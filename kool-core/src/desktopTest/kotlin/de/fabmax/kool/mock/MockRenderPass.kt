package de.fabmax.kool.mock

import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.util.Color

class MockRenderPass(
    override val size: Vec3i = Vec3i(Mock.testCtx.windowWidth, Mock.testCtx.windowHeight, 1)
) : RenderPass("mock-render-pass", MipMode.None) {

    val mockView = View("mock-view", Node(), PerspectiveCamera())
    override val clearColors: Array<Color?> = arrayOf(Color.BLACK)
    override val views = listOf(mockView)

    val updateEvent = views[0].makeUpdateEvent(Mock.testCtx)
}