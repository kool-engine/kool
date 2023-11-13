package de.fabmax.kool.pipeline.backend.stats

import de.fabmax.kool.pipeline.Pipeline

class PipelineInfo(
    val pipeline: Pipeline
) : ResourceInfo(pipeline.name) {

    var numInstances = 0

    init {
        BackendStats.pipelines[id] = this
    }

    override fun deleted() {
        BackendStats.pipelines -= id
    }
}