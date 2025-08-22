package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.AttachmentConfig
import de.fabmax.kool.pipeline.OffscreenPass2d
import de.fabmax.kool.pipeline.TexFormat

class PbrLightingPass(pipeline: DeferredPipeline, suffix: String, val materialPass: MaterialPass) :
    OffscreenPass2d(
        drawNode = pipeline.lightingPassContent,
        attachmentConfig = AttachmentConfig.singleColorDefaultDepth(TexFormat.RGBA_F16),
        initialSize = materialPass.dimensions.xy,
        name = "pbr-lighting-pass-$suffix"
    )
{

    init {
        mirrorIfInvertedClipY()
        val scene = pipeline.scene
        lighting = scene.lighting
        camera = materialPass.camera
        isUpdateDrawNode = false

        dependsOn(materialPass)

        scene.mainRenderPass.onAfterCollectDrawCommands += { viewData ->
            if (isEnabled) {
                val ctx = KoolSystem.requireContext()
                for (i in materialPass.alphaMeshes.indices) {
                    val mesh = materialPass.alphaMeshes[i]
                    mesh.getOrCreatePipeline(ctx)?.let { pipeline ->
                        viewData.drawQueue.addMesh(mesh, pipeline)
                    }
                }
            }
        }
    }
}
