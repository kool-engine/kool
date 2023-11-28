package de.fabmax.kool.pipeline.backend.stats

import de.fabmax.kool.pipeline.PipelineBase

class PipelineInfo(
    val pipeline: PipelineBase
) : ResourceInfo(pipeline.name) {

    var numInstances = 0

    init {
        BackendStats.pipelines[id] = this
    }

    override fun deleted() {
        BackendStats.pipelines -= id
    }
}