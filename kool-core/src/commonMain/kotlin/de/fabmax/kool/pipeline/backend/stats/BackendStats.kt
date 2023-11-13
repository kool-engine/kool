package de.fabmax.kool.pipeline.backend.stats

object BackendStats {

    val offscreenPasses = mutableMapOf<Long, OffscreenPassInfo>()

    val allocatedBuffers = mutableMapOf<Long, BufferInfo>()
    var totalBufferSize: Long = 0L
        internal set

}