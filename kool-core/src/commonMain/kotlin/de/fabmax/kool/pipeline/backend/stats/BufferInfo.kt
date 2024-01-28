package de.fabmax.kool.pipeline.backend.stats

class BufferInfo(
    name: String,
    val info: String
) : ResourceInfo(name) {

    var size: Long = 0L

    init {
        BackendStats.allocatedBuffers[id] = this
    }

    fun allocated(allocSize: Long) {
        BackendStats.totalBufferSize += allocSize - size
        size = allocSize
    }

    override fun deleted() {
        BackendStats.totalBufferSize -= size
        BackendStats.allocatedBuffers -= id
    }
}