package de.fabmax.kool.mock

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.KoolConfig
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.MsdfFontInfo
import kotlinx.coroutines.runBlocking

object Mock {

    init {
        val mockConfig = object : KoolConfig {
            override val defaultAssetLoader: AssetLoader get() = TODO("Not yet implemented")
            override val defaultFont: MsdfFontInfo get() = TODO("Not yet implemented")
            override val numSamples: Int = 1
        }
        KoolSystem.initialize(mockConfig)
    }

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
        runBlocking {
            testCtx.run()
        }
    }

}