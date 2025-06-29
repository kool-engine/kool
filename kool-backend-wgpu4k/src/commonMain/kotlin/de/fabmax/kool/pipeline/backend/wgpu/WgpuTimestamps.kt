package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.util.Releasable
import io.ygdrasil.webgpu.*
import kotlin.math.max

class WgpuTimestamps(val size: Int, val backend: RenderBackendWgpu4k) {

    private var querySet: GPUQuerySet? = null
    private val resolveBuffer = backend.device.createBuffer(
        BufferDescriptor(
            (size * 8L).toULong(),
            setOf(GPUBufferUsage.QueryResolve, GPUBufferUsage.CopySrc)
        )
    )
    private val readBuffer = backend.device.createBuffer(
        BufferDescriptor(
            (size * 8L).toULong(),
            setOf(GPUBufferUsage.MapRead,GPUBufferUsage.CopyDst)
        )
    )

    private var isInFlight = false
    private var isMapping = false

    private val slots = Array<QuerySlot?>(size) { null }
    var activeQueries = 0
        private set

    private var lastActive = -1

    fun getQuerySet(): GPUQuerySet? {
        if (!backend.isTimestampQuerySupported) {
            return null
        }
        if (querySet == null) {
            querySet = backend.device.createQuerySet(QuerySetDescriptor(GPUQueryType.Timestamp, size.toUInt()))
        }
        return querySet
    }

    fun createQuery(): QuerySlot? {
        if (!backend.isTimestampQuerySupported) {
            return null
        }

        var slot: QuerySlot? = null
        for (i in slots.indices) {
            if (slots[i] == null) {
                slot = QuerySlot(i)
                slots[i] = slot
                activeQueries++
                lastActive = max(lastActive, i)
                break
            }
        }
        return slot
    }

    fun resolve(encoder: GPUCommandEncoder) {
        val querySet = getQuerySet() ?: return
        if (!isInFlight && activeQueries > 0) {
            isInFlight = true
            encoder.resolveQuerySet(querySet, 0u, (lastActive + 1).toUInt(), resolveBuffer, 0uL)
            encoder.copyBufferToBuffer(resolveBuffer, 0uL, readBuffer, 0uL, (size * 8L).toULong())
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    suspend fun readTimestamps() {
        if (isInFlight && !isMapping) {
            isMapping = true
            readBuffer.mapAsync(setOf(GPUMapMode.Read)).onSuccess {
                val decoded = readBuffer.getMappedRange()
                    .asUIntArray()
                for (i in 0..lastActive) {
                    val slot = slots[i]
                    if (slot != null) {
                        val lower = decoded[i * 2]
                        val upper = decoded[i * 2 + 1]
                        slot.latestResult = upper.toLong() shl 32 or lower.toLong()
                    }
                }
                readBuffer.unmap()
                isInFlight = false
                isMapping = false
            }
        }
    }

    inner class QuerySlot(val index: Int) : Releasable {
        override var isReleased = false
            private set

        val isReady: Boolean
            get() = !isInFlight
        var latestResult = 0L

        override fun release() {
            slots[index] = null
            if (index == lastActive) {
                lastActive = slots.indices.reversed().firstOrNull { slots[it] != null } ?: -1
            }
            activeQueries--
            isReleased = true
        }
    }
}