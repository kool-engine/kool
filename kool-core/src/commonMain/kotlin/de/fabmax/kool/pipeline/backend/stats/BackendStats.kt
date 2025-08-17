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

    private var _numDrawCommands = 0
    private var _numPrimitives = 0

    fun resetPerFrameCounts() {
        numDrawCommands = _numDrawCommands
        numPrimitives = _numPrimitives
        _numDrawCommands = 0
        _numPrimitives = 0
    }

    fun addDrawCommands(nCommands: Int, nPrimitives: Int) {
        _numPrimitives += nPrimitives
        _numDrawCommands += nCommands
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