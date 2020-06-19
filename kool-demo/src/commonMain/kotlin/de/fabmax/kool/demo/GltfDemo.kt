package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.defaultCamTransform
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.gltf.loadGltfModel

fun gltfTest(ctx: KoolContext) = scene {
    defaultCamTransform()

    ctx.assetMgr.loadGltfModel("local/gltf/camera.glb") { gltf ->
        gltf?.let {
            +it.makeModel().apply {
                scale(50f, 50f, 50f)
            }
        }
    }
}