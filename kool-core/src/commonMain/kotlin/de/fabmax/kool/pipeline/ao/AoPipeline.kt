package de.fabmax.kool.pipeline.ao

import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.deferred.DeferredPipeline
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Releasable

interface AoPipeline : Releasable {
    val aoMap: Texture2d
    var isEnabled: Boolean

    var radius: Float
    var strength: Float
    var power: Float
    var kernelSz: Int

    companion object {
        fun createForward(
            scene: Scene,
            camera: PerspectiveCamera = (scene.camera as PerspectiveCamera),
            drawNode: Node = scene
        ) = ForwardAoPipeline(scene, camera, drawNode)

        fun createDeferred(deferredPipeline: DeferredPipeline) = DeferredAoPipeline(deferredPipeline)
    }
}
