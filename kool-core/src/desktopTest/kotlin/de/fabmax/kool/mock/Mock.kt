package de.fabmax.kool.mock

import de.fabmax.kool.scene.Node

object Mock {

    val testCtx = TestKoolContext()

    val testRenderPass = MockRenderPass()

    fun mockDraw(node: Node) {
        testRenderPass.apply {
            mockView.drawQueue.reset(false)
            node.update(updateEvent)
            node.collectDrawCommands(updateEvent)
        }
    }

    init {
        testCtx.run()
    }
}