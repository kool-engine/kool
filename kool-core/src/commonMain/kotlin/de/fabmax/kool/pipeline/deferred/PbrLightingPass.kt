package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.Color

class PbrLightingPass(pipeline: DeferredPipeline, suffix: String, val materialPass: MaterialPass) :
    OffscreenRenderPass2d(
        pipeline.lightingPassContent,
        colorAttachmentDefaultDepth(TexFormat.RGBA_F16),
        materialPass.size.xy,
        name = "pbr-lighting-pass-$suffix"
    )
{

    init {
        mirrorIfInvertedClipY()
        val scene = pipeline.scene
        lighting = scene.lighting
        clearColor = Color(0f, 0f, 0f, 0f)
        camera = materialPass.camera
        isUpdateDrawNode = false

        dependsOn(materialPass)

        scene.mainRenderPass.onAfterCollectDrawCommands += { ev ->
            if (isEnabled) {
                for (i in materialPass.alphaMeshes.indices) {
                    scene.mainRenderPass.screenView.drawQueue.addMesh(materialPass.alphaMeshes[i], ev)
                }
            }
        }
    }
}
