package de.fabmax.kool

/**
 * @author fabmax
 */
class EngineStats internal constructor() {

    val bufferAllocations = mutableMapOf<Long, Int>()
    var totalBufferSize = 0L
        private set

    val textureAllocations = mutableMapOf<Long, Int>()
    var totalTextureSize = 0L
        private set

    var pipelines = mutableMapOf<Long, Int>()
    var numPipelineInstances = 0
        private set

    var numPrimitives = 0
        private set

    fun bufferAllocated(bufferId: Long, size: Int) {
        bufferAllocations.put(bufferId, size)?.let { totalBufferSize -= it }
        totalBufferSize += size
    }

    fun bufferDeleted(bufferId: Long) {
        bufferAllocations.remove(bufferId)?.let { totalBufferSize -= it }
    }

    fun textureAllocated(textureId: Long, size: Int) {
        textureAllocations.put(textureId, size)?.let { totalTextureSize -= it }
        totalTextureSize += size
    }

    fun textureDeleted(textureId: Long) {
        textureAllocations.remove(textureId)?.let { totalTextureSize -= it }
    }

    fun pipelineInstanceCreated(pipelineId: Long) {
        val insts = pipelines.getOrElse(pipelineId) { 0 } + 1
        pipelines[pipelineId] = insts
        numPipelineInstances++
    }

    fun pipelineInstanceDestroyed(pipelineId: Long) {
        val insts = pipelines.getOrElse(pipelineId) { 0 } - 1
        if (insts >= 0) {
            pipelines[pipelineId] = insts
            numPipelineInstances--
        }
    }

    fun pipelineDestroyed(pipelineId: Long) {
        pipelines.remove(pipelineId)?.let { numPipelineInstances -= it }
    }

    fun resetPrimitveCount() {
        numPrimitives = 0
    }

    fun addPrimitiveCount(nPrimitives: Int) {
        numPrimitives += nPrimitives
    }
}
