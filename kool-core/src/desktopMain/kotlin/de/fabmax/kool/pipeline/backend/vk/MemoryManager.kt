package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.vma.Vma.*
import org.lwjgl.util.vma.VmaAllocationCreateInfo
import org.lwjgl.util.vma.VmaAllocatorCreateInfo
import org.lwjgl.util.vma.VmaVulkanFunctions
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkBufferCreateInfo
import org.lwjgl.vulkan.VkImageCreateInfo
import org.lwjgl.vulkan.VkMemoryRequirements
import java.nio.LongBuffer

class MemoryManager(val sys: VkSystem) : VkResource() {

    private val impl: IMemManager

    init {
        impl = VmaMemManager(sys)
        //impl = NaiveMemManager(sys)
        sys.logicalDevice.addDependingResource(this)
    }

    fun createBuffer(bufferInfo: VkBufferCreateInfo, allocUsage: Int, pBuffer: LongBuffer, pAllocation: PointerBuffer) =
        impl.createBuffer(bufferInfo, allocUsage, pBuffer, pAllocation)
    fun freeBuffer(buffer: Long, allocation: Long) = impl.freeBuffer(buffer, allocation)

    fun createImage(imageInfo: VkImageCreateInfo, allocUsage: Int, pImage: LongBuffer, pAllocation: PointerBuffer) =
        impl.createImage(imageInfo, allocUsage, pImage, pAllocation)
    fun freeImage(image: Long, allocation: Long) = impl.freeImage(image, allocation)

    fun mapMemory(allocation: Long) = impl.mapMemory(allocation)
    fun unmapMemory(allocation: Long) = impl.unmapMemory(allocation)

    override fun freeResources() = impl.freeResources()

    interface IMemManager {
        fun createBuffer(bufferInfo: VkBufferCreateInfo, allocUsage: Int, pBuffer: LongBuffer, pAllocation: PointerBuffer): Int
        fun freeBuffer(buffer: Long, allocation: Long)
        fun createImage(imageInfo: VkImageCreateInfo, allocUsage: Int, pImage: LongBuffer, pAllocation: PointerBuffer): Int
        fun freeImage(image: Long, allocation: Long)
        fun mapMemory(allocation: Long): Long
        fun unmapMemory(allocation: Long)
        fun freeResources()
    }

    private class VmaMemManager(val sys: VkSystem) : IMemManager {
        val allocator: Long

        init {
            memStack {
                val vkFunctions = VmaVulkanFunctions.calloc(this).apply {
                    val pCaps = sys.physicalDevice.vkPhysicalDevice.capabilities
                    val dCaps = sys.logicalDevice.vkDevice.capabilities
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
                    physicalDevice(sys.physicalDevice.vkPhysicalDevice)
                    device(sys.logicalDevice.vkDevice)
                    instance(sys.instance.vkInstance)
                    pVulkanFunctions(vkFunctions)
                }
                val pp = mallocPointer(1)
                vmaCreateAllocator(createInfo, pp)
                allocator = pp[0]
            }
            logD { "Created VMA memory allocator" }
        }

        override fun createBuffer(bufferInfo: VkBufferCreateInfo, allocUsage: Int, pBuffer: LongBuffer, pAllocation: PointerBuffer): Int {
            return memStack {
                val allocInfo = VmaAllocationCreateInfo.calloc(this).apply { usage(allocUsage) }
                vmaCreateBuffer(allocator, bufferInfo, allocInfo, pBuffer, pAllocation, null)
            }
        }

        override fun freeBuffer(buffer: Long, allocation: Long) {
            vmaDestroyBuffer(allocator, buffer, allocation)
        }

        override fun createImage(imageInfo: VkImageCreateInfo, allocUsage: Int, pImage: LongBuffer, pAllocation: PointerBuffer): Int {
            return memStack {
                val allocInfo = VmaAllocationCreateInfo.calloc(this).apply { usage(allocUsage) }
                vmaCreateImage(allocator, imageInfo, allocInfo, pImage, pAllocation, null)
            }
        }

        override fun freeImage(image: Long, allocation: Long) {
            vmaDestroyImage(allocator, image, allocation)
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

    private class NaiveMemManager(val sys: VkSystem) : IMemManager {

        class BufferInfo(val buffer: Long, val memory: Long, val size: Long, val allocUsage: Int)

        private val allocatedBuffers = mutableMapOf<Long, BufferInfo>()

        init {
            logD { "Created naive memory allocator" }
        }

        override fun createBuffer(bufferInfo: VkBufferCreateInfo, allocUsage: Int, pBuffer: LongBuffer, pAllocation: PointerBuffer): Int {
            memStack {
                var res = vkCreateBuffer(sys.logicalDevice.vkDevice, bufferInfo, null, pBuffer)
                if (res != VK_SUCCESS) { return res }

                val properties = getPropertiesForAllocationUsage(allocUsage)
                val memRequirements = VkMemoryRequirements.malloc(this)
                vkGetBufferMemoryRequirements(sys.logicalDevice.vkDevice, pBuffer[0], memRequirements)
                val allocInfo = callocVkMemoryAllocateInfo {
                    sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                    allocationSize(memRequirements.size())
                    memoryTypeIndex(sys.physicalDevice.findMemoryType(memRequirements.memoryTypeBits(), properties))
                }

                val lp = mallocLong(1)
                res = vkAllocateMemory(sys.logicalDevice.vkDevice, allocInfo, null, lp)
                if (res != VK_SUCCESS) { return res }
                pAllocation.put(0, lp[0])
                res = vkBindBufferMemory(sys.logicalDevice.vkDevice, pBuffer[0], lp[0], 0L)
                if (res != VK_SUCCESS) { return res }

                allocatedBuffers[pAllocation[0]] = BufferInfo(pBuffer[0], pAllocation[0], bufferInfo.size(), allocUsage)
            }
            return VK_SUCCESS
        }

        override fun freeBuffer(buffer: Long, allocation: Long) {
            val bufferInfo = allocatedBuffers.remove(allocation)
            bufferInfo?.let {
                vkDestroyBuffer(sys.logicalDevice.vkDevice, buffer, null)
                vkFreeMemory(sys.logicalDevice.vkDevice, it.memory, null)
            }
        }

        override fun createImage(imageInfo: VkImageCreateInfo, allocUsage: Int, pImage: LongBuffer, pAllocation: PointerBuffer): Int {
            memStack {
                var res = vkCreateImage(sys.logicalDevice.vkDevice, imageInfo, null, pImage)
                if (res != VK_SUCCESS) { return res }

                val properties = getPropertiesForAllocationUsage(allocUsage)
                val memRequirements = VkMemoryRequirements.malloc(this)
                vkGetImageMemoryRequirements(sys.logicalDevice.vkDevice, pImage[0], memRequirements)
                val allocInfo = callocVkMemoryAllocateInfo {
                    sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                    allocationSize(memRequirements.size())
                    memoryTypeIndex(sys.physicalDevice.findMemoryType(memRequirements.memoryTypeBits(), properties))
                }

                val lp = mallocLong(1)
                res = vkAllocateMemory(sys.logicalDevice.vkDevice, allocInfo, null, lp)
                if (res != VK_SUCCESS) { return res }
                pAllocation.put(0, lp[0])
                res = vkBindImageMemory(sys.logicalDevice.vkDevice, pImage[0], lp[0], 0L)
                if (res != VK_SUCCESS) { return res }

                allocatedBuffers[pAllocation[0]] = BufferInfo(pImage[0], pAllocation[0], memRequirements.size(), allocUsage)
            }
            return VK_SUCCESS
        }

        override fun freeImage(image: Long, allocation: Long) {
            val bufferInfo = allocatedBuffers.remove(allocation)
            bufferInfo?.let {
                vkDestroyImage(sys.logicalDevice.vkDevice, image, null)
                vkFreeMemory(sys.logicalDevice.vkDevice, it.memory, null)
            }
        }

        override fun mapMemory(allocation: Long): Long {
            val bufferInfo = allocatedBuffers[allocation] ?: throw IllegalArgumentException("Invalid allocation")
            return memStack {
                val data = mallocPointer(1)
                vkMapMemory(sys.logicalDevice.vkDevice, allocation, 0L, bufferInfo.size, 0, data)
                data[0]
            }
        }

        override fun unmapMemory(allocation: Long) {
            vkUnmapMemory(sys.logicalDevice.vkDevice, allocation)
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