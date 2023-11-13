package de.fabmax.kool.pipeline.backend.stats

import de.fabmax.kool.pipeline.RenderPass

class OffscreenPassInfo(
    name: String,
    val renderPass: RenderPass
) : ResourceInfo(name) {

    var sceneName: String = "<unknown>"

    init {
        BackendStats.offscreenPasses[id] = this
    }

    override fun deleted() {
        BackendStats.offscreenPasses -= id
    }
}