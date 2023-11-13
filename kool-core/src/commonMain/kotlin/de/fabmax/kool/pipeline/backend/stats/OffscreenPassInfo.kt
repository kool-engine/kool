package de.fabmax.kool.pipeline.backend.stats

import de.fabmax.kool.pipeline.RenderPass

class OffscreenPassInfo(val renderPass: RenderPass) : ResourceInfo(renderPass.name) {

    var sceneName: String = "<unknown>"

    init {
        BackendStats.offscreenPasses[id] = this
    }

    override fun deleted() {
        BackendStats.offscreenPasses -= id
    }
}