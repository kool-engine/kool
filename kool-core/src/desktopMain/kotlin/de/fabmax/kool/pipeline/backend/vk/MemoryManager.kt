package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import de.fabmax.kool.util.releaseWith
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.vma.Vma.*
import org.lwjgl.util.vma.VmaAllocationCreateInfo
import org.lwjgl.util.vma.VmaAllocatorCreateInfo
import org.lwjgl.util.vma.VmaVulkanFunctions
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkBufferCreateInfo
import org.lwjgl.vulkan.VkImageCreateInfo
import org.lwjgl.vulkan.VkMemoryRequirements
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class MemoryManager(val backend: RenderBackendVk) : BaseReleasable() {

    private val impl: MemManager = VmaMemManager(backend)

    private val buffers = mutableSetOf<VkBuffer>()
    private val images = mutableSetOf<VkImage>()

    init {
        releaseWith(backend.device)
    }

    fun createBuffer(bufferInfo: VkBufferCreateInfo, allocUsage: Int): VkBuffer{
        val buffer = impl.createBuffer(bufferInfo, allocUsage)
        buffers += buffer
        return buffer
    }

    fun freeBuffer(buffer: VkBuffer) {
        buffers -= buffer
        impl.freeBuffer(buffer)
    }

    fun createImage(imageInfo: VkImageCreateInfo, allocUsage: Int): VkImage {
        val image = impl.createImage(imageInfo, allocUsage)
        images += image
        return image
    }

    fun freeImage(image: VkImage) {
        images -= image
        impl.freeImage(image)
    }

    fun mapMemory(buffer: VkBuffer) = impl.mapMemory(buffer.allocation)
    fun unmapMemory(buffer: VkBuffer) = impl.unmapMemory(buffer.allocation)

    inline fun mappedBytes(buffer: VkBuffer, block: (ByteBuffer) -> Unit) {
        val addr = mapMemory(buffer)
        block(MemoryUtil.memByteBuffer(addr, buffer.bufferSize.toInt()))
        unmapMemory(buffer)
    }

    inline fun mappedFloats(buffer: VkBuffer, block: (FloatBuffer) -> Unit) {
        val addr = mapMemory(buffer)
        block(MemoryUtil.memFloatBuffer(addr, buffer.bufferSize.toInt()))
        unmapMemory(buffer)
    }

    inline fun mappedInts(buffer: VkBuffer, block: (IntBuffer) -> Unit) {
        val addr = mapMemory(buffer)
        block(MemoryUtil.memIntBuffer(addr, buffer.bufferSize.toInt()))
        unmapMemory(buffer)
    }

    override fun release() {
        super.release()
        buffers.toList().forEach { freeBuffer(it) }
        images.toList().forEach { freeImage(it) }
        impl.freeResources()
    }

    interface MemManager {
        fun createBuffer(bufferInfo: VkBufferCreateInfo, allocUsage: Int): VkBuffer
        fun freeBuffer(buffer: VkBuffer)
        fun createImage(imageInfo: VkImageCreateInfo, allocUsage: Int): VkImage
        fun freeImage(image: VkImage)
        fun mapMemory(allocation: Long): Long
        fun unmapMemory(allocation: Long)
        fun freeResources()
    }

    private class VmaMemManager(val backend: RenderBackendVk) : MemManager {
        val allocator: Long

        init {
            memStack {
                val vkFunctions = VmaVulkanFunctions.calloc(this).apply {
                    val pCaps = backend.physicalDevice.vkPhysicalDevice.capabilities
                    val dCaps = backend.device.vkDevice.capabilities
                    vkGetPhysicalDeviceMemoryProperties(pCaps.vkGetPhysicalDeviceMemoryProperties)
                    vkGetPhysicalDeviceProperties(pCaps.vkGetPhysicalDeviceProperties)
                    vkAllocateMemory(dCaps.vkAllocateMemory)
                    vkBindBufferMemory(dCaps.vkBindBufferMemory)
                    vkBindImageMemory(dCaps.vkBindImageMemory)
                    vkCmdCopyBuffer(dCaps.vkCmdCopyBuffer)
                    vkCreateBuffer(dCaps.vkCreateBuffer)
                    vkCreateImage(dCaps.vkCreateImage)
                    vkDestroyBuffer(dCaps.vkDestroyBuffer)
                    vkDestroyImage(dCaps.vkDestroyImage)
                    vkFlushMappedMemoryRanges(dCaps.vkFlushMappedMemoryRanges)
                    vkFreeMemory(dCaps.vkFreeMemory)
                    vkGetBufferMemoryRequirements(dCaps.vkGetBufferMemoryRequirements)
                    vkGetBufferMemoryRequirements2KHR(dCaps.vkGetBufferMemoryRequirements2KHR)
                    vkGetImageMemoryRequirements(dCaps.vkGetImageMemoryRequirements)
                    vkGetImageMemoryRequirements2KHR(dCaps.vkGetImageMemoryRequirements2KHR)
                    vkInvalidateMappedMemoryRanges(dCaps.vkInvalidateMappedMemoryRanges)
                    vkMapMemory(dCaps.vkMapMemory)
                    vkUnmapMemory(dCaps.vkUnmapMemory)
                }

                val createInfo = VmaAllocatorCreateInfo.calloc(this).apply {
                    physicalDevice(backend.physicalDevice.vkPhysicalDevice)
                    device(backend.device.vkDevice)
                    instance(backend.instance.vkInstance)
                    pVulkanFunctions(vkFunctions)
                }
                val pp = mallocPointer(1)
                vmaCreateAllocator(createInfo, pp)
                allocator = pp[0]
            }
            logD { "Created VMA memory allocator" }
        }

        override fun createBuffer(bufferInfo: VkBufferCreateInfo, allocUsage: Int): VkBuffer {
            return memStack {
                val pBuffer = mallocLong(1)
                val pAllocation = mallocPointer(1)
                val allocInfo = VmaAllocationCreateInfo.calloc(this).apply { usage(allocUsage) }
                checkVk(vmaCreateBuffer(allocator, bufferInfo, allocInfo, pBuffer, pAllocation, null)) {
                    "Failed allocating buffer: $it"
                }
                VkBuffer(pBuffer[0], pAllocation[0], bufferInfo.size())
            }
        }

        override fun freeBuffer(buffer: VkBuffer) {
            vmaDestroyBuffer(allocator, buffer.handle, buffer.allocation)
        }

        override fun createImage(imageInfo: VkImageCreateInfo, allocUsage: Int): VkImage {
            return memStack {
                val pImage = mallocLong(1)
                val pAllocation = mallocPointer(1)
                val allocInfo = VmaAllocationCreateInfo.calloc(this).apply { usage(allocUsage) }
                checkVk(vmaCreateImage(allocator, imageInfo, allocInfo, pImage, pAllocation, null)) {
                    "Failed allocating image: $it"
                }
                VkImage(pImage[0], pAllocation[0])
            }
        }

        override fun freeImage(image: VkImage) {
            vmaDestroyImage(allocator, image.handle, image.allocation)
        }

        override fun mapMemory(allocation: Long): Long {
            return memStack {
                val pp = mallocPointer(1)
                vmaMapMemory(allocator, allocation, pp)
                pp[0]
            }
        }

        override fun unmapMemory(allocation: Long) {
            vmaUnmapMemory(allocator, allocation)
        }

        fun printMemoryStats() {
            memStack {
                val pp = mallocPointer(1)
                vmaBuildStatsString(allocator, pp, true)
                val str = MemoryUtil.memASCII(pp[0])
                println(str)
                vmaFreeStatsString(allocator, MemoryUtil.memByteBuffer(pp[0], str.length + 1))
            }
        }

        override fun freeResources() {
            vmaDestroyAllocator(allocator)
            logD { "Destroyed VMA memory allocator" }
        }
    }

    private class NaiveMemManager(val backend: RenderBackendVk) : MemManager {

        class BufferInfo(val buffer: Long, val memory: Long, val size: Long, val allocUsage: Int)

        private val allocatedBuffers = mutableMapOf<Long, BufferInfo>()
        private val physicalDevice: PhysicalDevice get() = backend.physicalDevice
        private val device: Device get() = backend.device

        init {
            logD { "Created naive memory allocator" }
        }

        override fun createBuffer(bufferInfo: VkBufferCreateInfo, allocUsage: Int): VkBuffer {
            return memStack {
                val pBuffer = mallocLong(1)
                val pAllocation = mallocPointer(1)
                checkVk(vkCreateBuffer(device.vkDevice, bufferInfo, null, pBuffer))

                val properties = getPropertiesForAllocationUsage(allocUsage)
                val memRequirements = VkMemoryRequirements.malloc(this)
                vkGetBufferMemoryRequirements(device.vkDevice, pBuffer[0], memRequirements)
                val allocInfo = callocVkMemoryAllocateInfo {
                    sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                    allocationSize(memRequirements.size())
                    memoryTypeIndex(physicalDevice.findMemoryType(memRequirements.memoryTypeBits(), properties))
                }

                val lp = mallocLong(1)
                checkVk(vkAllocateMemory(device.vkDevice, allocInfo, null, lp))
                pAllocation.put(0, lp[0])
                checkVk(vkBindBufferMemory(device.vkDevice, pBuffer[0], lp[0], 0L))

                allocatedBuffers[pAllocation[0]] = BufferInfo(pBuffer[0], pAllocation[0], bufferInfo.size(), allocUsage)
                VkBuffer(pBuffer[0], pAllocation[0], bufferInfo.size())
            }
        }

        override fun freeBuffer(buffer: VkBuffer) {
            val bufferInfo = allocatedBuffers.remove(buffer.allocation)
            bufferInfo?.let {
                vkDestroyBuffer(device.vkDevice, buffer.handle, null)
                vkFreeMemory(device.vkDevice, it.memory, null)
            }
        }

        override fun createImage(imageInfo: VkImageCreateInfo, allocUsage: Int): VkImage {
            return memStack {
                val pImage = mallocLong(1)
                val pAllocation = mallocPointer(1)
                check(vkCreateImage(device.vkDevice, imageInfo, null, pImage) == VK_SUCCESS)

                val properties = getPropertiesForAllocationUsage(allocUsage)
                val memRequirements = VkMemoryRequirements.malloc(this)
                vkGetImageMemoryRequirements(device.vkDevice, pImage[0], memRequirements)
                val allocInfo = callocVkMemoryAllocateInfo {
                    sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                    allocationSize(memRequirements.size())
                    memoryTypeIndex(physicalDevice.findMemoryType(memRequirements.memoryTypeBits(), properties))
                }

                val lp = mallocLong(1)
                check(vkAllocateMemory(device.vkDevice, allocInfo, null, lp) == VK_SUCCESS)
                pAllocation.put(0, lp[0])
                check(vkBindImageMemory(device.vkDevice, pImage[0], lp[0], 0L) == VK_SUCCESS)

                allocatedBuffers[pAllocation[0]] = BufferInfo(pImage[0], pAllocation[0], memRequirements.size(), allocUsage)
                VkImage(pImage[0], pAllocation[0])
            }
        }

        override fun freeImage(image: VkImage) {
            val bufferInfo = allocatedBuffers.remove(image.allocation)
            bufferInfo?.let {
                vkDestroyImage(device.vkDevice, image.handle, null)
                vkFreeMemory(device.vkDevice, it.memory, null)
            }
        }

        override fun mapMemory(allocation: Long): Long {
            val bufferInfo = allocatedBuffers[allocation] ?: throw IllegalArgumentException("Invalid allocation")
            return memStack {
                val data = mallocPointer(1)
                vkMapMemory(device.vkDevice, allocation, 0L, bufferInfo.size, 0, data)
                data[0]
            }
        }

        override fun unmapMemory(allocation: Long) {
            vkUnmapMemory(device.vkDevice, allocation)
        }

        override fun freeResources() {
            logD { "Destroyed naive memory allocator" }
        }

        private fun getPropertiesForAllocationUsage(allocUsage: Int): Int {
            return when (allocUsage) {
                VMA_MEMORY_USAGE_CPU_ONLY -> VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT or VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
                VMA_MEMORY_USAGE_CPU_TO_GPU -> VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT or VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
                VMA_MEMORY_USAGE_GPU_ONLY -> VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT
                else -> throw IllegalArgumentException("Unsupported allocation usage: $allocUsage")
            }
        }
    }
}