package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import de.fabmax.kool.util.releaseWith
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*

class DescriptorPoolManager(val backend: RenderBackendVk) : BaseReleasable() {

    private val pools = mutableMapOf<PoolLayout, DescriptorPools>()

    init {
        releaseWith(backend.device)
    }

    fun allocateSets(
        poolLayout: PoolLayout,
        layout: VkDescriptorSetLayout,
        stack: MemoryStack,
        numSets: Int = Swapchain.MAX_FRAMES_IN_FLIGHT
    ): List<VkDescriptorSet> {
        return pools.getOrPut(poolLayout) { DescriptorPools(poolLayout) }.allocateSets(layout, numSets, stack)
    }

    fun releaseSets(poolLayout: PoolLayout, sets: List<VkDescriptorSet>) {
        pools[poolLayout]?.releaseSets(sets)
    }

    override fun release() {
        super.release()
        pools.values.forEach { it.release() }
    }

    private inner class DescriptorPools(val poolLayout: PoolLayout) {
        var poolSize = INIT_POOL_SIZE
        val exceededPools = mutableSetOf<DescriptorPool>()
        val openPools = mutableListOf<DescriptorPool>()

        val allocatedSets = mutableMapOf<VkDescriptorSet, DescriptorPool>()

        private fun nextPool(): DescriptorPool {
            return if (openPools.isNotEmpty()) {
                openPools.last()
            } else {
                poolSize = (poolSize * 2).coerceAtMost(MAX_POOL_SIZE)
                DescriptorPool(poolLayout, poolSize).also { openPools.add(it) }
            }
        }

        fun allocateSets(layout: VkDescriptorSetLayout, numSets: Int, stack: MemoryStack): List<VkDescriptorSet> {
            require(numSets <= INIT_POOL_SIZE)

            var sets: List<VkDescriptorSet>? = null
            while (sets == null) {
                val pool = nextPool()
                sets = pool.allocateSets(layout, numSets, stack)
                if (sets == null) {
                    openPools -= pool

                    logD { "Descriptor pool exceeded" }
                    if (pool.allocatedSets.isEmpty()) {
                        ReleaseQueue.enqueue {
                            pool.resetPool()
                            openPools += pool
                        }
                    } else {
                        exceededPools += pool
                    }

                } else {
                    sets.forEach { allocatedSets[it] = pool }
                }
            }
            return sets
        }

        fun releaseSets(sets: List<VkDescriptorSet>) {
            sets.forEach {
                val pool = checkNotNull(allocatedSets.remove(it))
                pool.allocatedSets -= it
                if (pool.isExceeded && pool.allocatedSets.isEmpty()) {
                    ReleaseQueue.enqueue {
                        pool.resetPool()
                        openPools += pool
                        exceededPools -= pool
                    }
                }
            }
        }

        fun release() {
            openPools.forEach { backend.device.destroyDescriptorPool(it.descriptorPool) }
            exceededPools.forEach { backend.device.destroyDescriptorPool(it.descriptorPool) }
        }
    }

    private inner class DescriptorPool(poolLayout: PoolLayout, maxSets: Int) {
        val descriptorPool: VkDescriptorPool

        val allocatedSets = mutableSetOf<VkDescriptorSet>()
        var isExceeded = false

        init {
            logD { "Creating descriptor pool for: $poolLayout (size: $maxSets sets)" }

            val numBindingTypes = poolLayout.numUbos.coerceAtMost(1) +
                    poolLayout.numTextures.coerceAtMost(1) +
                    poolLayout.numStorageBuffers.coerceAtMost(1) +
                    poolLayout.numStorageTextures.coerceAtMost(1)

            memStack {
                descriptorPool = backend.device.createDescriptorPool(this) {
                    val poolSizes = callocVkDescriptorPoolSizeN(numBindingTypes) {
                        var iPoolSize = 0
                        if (poolLayout.numUbos > 0) {
                            this[iPoolSize++].set(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, poolLayout.numUbos * maxSets)
                        }
                        if (poolLayout.numStorageBuffers > 0) {
                            this[iPoolSize++].set(VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, poolLayout.numStorageBuffers * maxSets)
                        }
                        if (poolLayout.numTextures > 0) {
                            this[iPoolSize++].set(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, poolLayout.numTextures * maxSets)
                        }
                        if (poolLayout.numStorageTextures > 0) {
                            this[iPoolSize++].set(VK_DESCRIPTOR_TYPE_STORAGE_IMAGE, poolLayout.numStorageTextures * maxSets)
                        }
                    }
                    pPoolSizes(poolSizes)
                    maxSets(maxSets)
                }
            }
        }

        fun resetPool() {
            logD { "Reset descriptor pool" }
            backend.device.resetDescriptorPool(descriptorPool)
            allocatedSets.clear()
            isExceeded = false
        }

        fun allocateSets(layout: VkDescriptorSetLayout, numSets: Int, stack: MemoryStack): List<VkDescriptorSet>? {
            val sets = backend.device.allocateDescriptorSets(stack = stack) {
                val layouts = stack.mallocLong(numSets)
                repeat(numSets) { layouts.put(it, layout.handle) }
                pSetLayouts(layouts)
                descriptorPool(descriptorPool.handle)
            }
            if (sets != null) {
                allocatedSets += sets
            } else {
                isExceeded = true
            }
            return sets
        }
    }

    companion object {
        const val INIT_POOL_SIZE = 64
        const val MAX_POOL_SIZE = 1024
    }
}

data class PoolLayout(val numUbos: Int, val numTextures: Int, val numStorageBuffers: Int, val numStorageTextures: Int)
