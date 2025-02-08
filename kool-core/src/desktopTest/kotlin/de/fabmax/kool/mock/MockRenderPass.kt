package de.fabmax.kool.mock

import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.RenderPassColorAttachment
import de.fabmax.kool.pipeline.RenderPassDepthAttachment
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera

class MockRenderPass(
    override val size: Vec3i = Vec3i(Mock.testCtx.windowWidth, Mock.testCtx.windowHeight, 1)
) : RenderPass(numSamples = 1, mipMode = MipMode.Single, name = "mock-render-pass") {

    override val colorAttachments: List<RenderPassColorAttachment> = emptyList()
    override val depthAttachment: RenderPassDepthAttachment? = null

    val mockView = View("mock-view", Node(), PerspectiveCamera())
    override val views = listOf(mockView)

    val updateEvent = views[0].makeUpdateEvent(Mock.testCtx)
}