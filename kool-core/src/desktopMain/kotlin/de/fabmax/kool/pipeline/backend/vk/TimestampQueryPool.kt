package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.releaseWith
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class TimestampQueryPool(val backend: RenderBackendVk, size: Int = 32) : BaseReleasable() {
    private val nanosPerTick: Double = backend.physicalDevice.vkDeviceProperties.limits().timestampPeriod().toDouble()
    private val vkQueryPool: VkQueryPool
    private val poolSize: Int
    private val querySlots: List<QuerySlot>

    private var isReady = true
    private var queriesInFlight = false
    private var nextQuery: Int = 0

    init {
        if (isSupported()) {
            poolSize = size
            vkQueryPool = backend.device.createQueryPool {
                queryType(VK_QUERY_TYPE_TIMESTAMP)
                queryCount(size)
            }
        } else {
            vkQueryPool = VkQueryPool(0L)
            poolSize = 0
        }
        querySlots = List(poolSize) { QuerySlot(it) }

        backend.commandPool.singleShotCommands {
            vkCmdResetQueryPool(it, vkQueryPool.handle, 0, poolSize)
        }
        releaseWith(backend.device)
    }

    override fun release() {
        super.release()
        backend.device.destroyQueryPool(vkQueryPool)
    }

    private fun isSupported(): Boolean {
        if (nanosPerTick == 0.0) {
            return false
        }
        return backend.physicalDevice.vkDeviceProperties.limits().timestampComputeAndGraphics() ||
                backend.device.graphicsQueueProperties.timestampValidBits() != 0
    }

    fun reset(commandBuffer: VkCommandBuffer) {
        if (queriesInFlight || poolSize == 0) {
            return
        }
        isReady = true
        vkCmdResetQueryPool(commandBuffer, vkQueryPool.handle, 0, poolSize)
        nextQuery = 0
    }

    fun queryTimestamp(isOnBegin: Boolean, commandBuffer: VkCommandBuffer): QuerySlot? {
        if (!isReady || nextQuery >= poolSize) {
            return null
        }
        queriesInFlight = true

        val queryIndex = nextQuery++
        val stage = if (isOnBegin) VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT else VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT
        vkCmdWriteTimestamp(commandBuffer, stage, vkQueryPool.handle, queryIndex)
        return querySlots[queryIndex].apply { reset() }
    }

    fun pollResults(stack: MemoryStack) {
        if (!queriesInFlight) {
            return
        }
        isReady = false

        val results = stack.mallocLong(nextQuery * 2)
        vkGetQueryPoolResults(backend.device.vkDevice, vkQueryPool.handle, 0, nextQuery, results, 16, VK_QUERY_RESULT_64_BIT or VK_QUERY_RESULT_WITH_AVAILABILITY_BIT)

        var allComplete = true
        for (i in 0 until nextQuery) {
            if (results[i * 2 + 1] != 0L) {
                querySlots[i].complete((results[i * 2] * nanosPerTick).roundToLong())
            } else {
                allComplete = false
            }
        }

        if (allComplete) {
            queriesInFlight = false
            for (i in 0 until nextQuery) {
                querySlots[i].onComplete?.invoke()
            }
        }
    }

    inner class QuerySlot(val slot: Int) {
        var isComplete = false
            private set
        var timestamp = 0L
            private set

        var onComplete: (() -> Unit)? = null

        fun complete(timestamp: Long) {
            this.timestamp = timestamp
            isComplete = true
        }

        fun reset() {
            isComplete = false
            timestamp = 0L
            onComplete = null
        }
    }
}

class Timer(val pool: TimestampQueryPool, val onAvailable: OnTimerAvailable) {
    private var beginSlot: TimestampQueryPool.QuerySlot? = null
    private var endSlot: TimestampQueryPool.QuerySlot? = null

    var isComplete: Boolean = false
        private set
    var latestResult: Duration = 0.0.seconds
        private set

    fun begin(commandBuffer: VkCommandBuffer) {
        beginSlot = pool.queryTimestamp(true, commandBuffer)
    }

    fun end(commandBuffer: VkCommandBuffer) {
        endSlot = pool.queryTimestamp(false, commandBuffer)?.also {
            it.onComplete = this::onComplete
        }
    }

    private fun onComplete() {
        val start = beginSlot
        val end = endSlot
        if (start != null && end != null) {
            latestResult = (end.timestamp - start.timestamp).nanoseconds
            isComplete = true
            onAvailable.onTimerAvailable(latestResult)
        }
    }
}

fun interface OnTimerAvailable {
    fun onTimerAvailable(delta: Duration)
}
