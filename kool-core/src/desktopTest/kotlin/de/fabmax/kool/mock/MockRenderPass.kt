package de.fabmax.kool.mock

import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.util.Color

class MockRenderPass(
    override val size: Vec3i = Vec3i(Mock.testCtx.windowWidth, Mock.testCtx.windowHeight, 1)
) : RenderPass("mock-render-pass", MipMode.None) {

    override val numSamples: Int = 1

    override val clearColors: List<ClearColor> = listOf(ClearColorFill(Color.BLACK))
    override val clearDepth: ClearDepth = ClearDepthFill

    val mockView = View("mock-view", Node(), PerspectiveCamera())
    override val views = listOf(mockView)

    val updateEvent = views[0].makeUpdateEvent(Mock.testCtx)
}