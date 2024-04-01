package de.fabmax.kool.pipeline.backend.stats

object BackendStats {

    val pipelines = mutableMapOf<Long, PipelineInfo>()

    val offscreenPasses = mutableMapOf<Long, OffscreenPassInfo>()

    val allocatedBuffers = mutableMapOf<Long, BufferInfo>()
    var totalBufferSize: Long = 0L
        internal set


    val allocatedTextures = mutableMapOf<Long, TextureInfo>()
    var totalTextureSize: Long = 0L
        internal set

    var numDrawCommands = 0
        private set
    var numPrimitives = 0
        private set

    fun resetPerFrameCounts() {
        numDrawCommands = 0
        numPrimitives = 0
    }

    fun addDrawCommands(nCommands: Int, nPrimitives: Int) {
        numPrimitives += nPrimitives
        numDrawCommands += nCommands
    }

    internal fun onDestroy() {
        pipelines.clear()
        offscreenPasses.clear()
        allocatedBuffers.clear()
        allocatedTextures.clear()
        totalBufferSize = 0L
        totalTextureSize = 0
        numDrawCommands = 0
        numPrimitives = 0
    }
}