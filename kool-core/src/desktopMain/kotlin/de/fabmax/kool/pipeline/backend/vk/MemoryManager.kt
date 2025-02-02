package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.*
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.vma.Vma.*
import org.lwjgl.util.vma.VmaAllocationCreateInfo
import org.lwjgl.util.vma.VmaAllocationInfo
import org.lwjgl.util.vma.VmaAllocatorCreateInfo
import org.lwjgl.util.vma.VmaVulkanFunctions
import org.lwjgl.vulkan.VK10.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class MemoryManager(val backend: RenderBackendVk) : BaseReleasable() {

    private val impl: MemManager = VmaMemManager(backend)

    private val buffers = mutableMapOf<VkBuffer, MemoryInfo>()
    private val images = mutableMapOf<VkImage, ImageInfo>()

    init {
        releaseWith(backend.device)
    }

    fun createBuffer(info: MemoryInfo): VkBuffer{
        val buffer = impl.createBuffer(info)
        buffers[buffer] = info
        return buffer
    }

    fun freeBuffer(buffer: VkBuffer, deferTicks: Int = Swapchain.MAX_FRAMES_IN_FLIGHT) {
        check(buffer in buffers) { "buffer was already release" }
        buffers -= buffer
        impl.freeBuffer(deferTicks, buffer)
    }

    inline fun stagingBuffer(
        size: Long,
        usage: Int = VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
        bufferDeleteTicks: Int = Swapchain.MAX_FRAMES_IN_FLIGHT,
        block: (VkBuffer) -> Unit
    ) {
        val stagingInfo = MemoryInfo(
            size = size,
            usage = usage,
            label = "<staging-buffer:$size>",
            createMapped = true
        )
        val stagingBuf = createBuffer(stagingInfo)
        block(stagingBuf)
        freeBuffer(stagingBuf, bufferDeleteTicks)
    }

    fun createImage(imageInfo: ImageInfo): VkImage {
        val image = impl.createImage(imageInfo)
        images[image] = imageInfo
        return image
    }

    fun freeImage(image: VkImage, deferTicks: Int = Swapchain.MAX_FRAMES_IN_FLIGHT) {
        images -= image
        impl.freeImage(deferTicks, image)
    }

    fun mapMemory(buffer: VkBuffer) = impl.mapMemory(buffer.allocation)
    fun unmapMemory(buffer: VkBuffer) = impl.unmapMemory(buffer.allocation)

    inline fun mappedBytes(buffer: VkBuffer, block: (ByteBuffer) -> Unit) {
        val buf = buffer.mapped ?: MemoryUtil.memByteBuffer(mapMemory(buffer), buffer.bufferSize.toInt())
        block(buf)
        if (buffer.mapped == null) {
            unmapMemory(buffer)
        }
    }

    inline fun mappedFloats(buffer: VkBuffer, block: (FloatBuffer) -> Unit) {
        mappedBytes(buffer) { block(it.asFloatBuffer()) }
    }

    inline fun mappedInts(buffer: VkBuffer, block: (IntBuffer) -> Unit) {
        mappedBytes(buffer) { block(it.asIntBuffer()) }
    }

    override fun release() {
        super.release()
        if (buffers.isNotEmpty()) {
            logW { "Freeing ${buffers.size} leaked buffers:" }
            buffers.keys.toList().forEach {
                logW { " * ${buffers[it]?.label}" }
                freeBuffer(it)
            }
        }
        if (images.isNotEmpty()) {
            logW { "Freeing ${images.size} leaked images:" }
            images.keys.toList().forEach {
                logW { " * ${images[it]?.label}" }
                freeImage(it)
            }
        }
        impl.freeResources()
    }

    interface MemManager {
        fun createBuffer(info: MemoryInfo): VkBuffer
        fun freeBuffer(deferTicks: Int, buffer: VkBuffer)
        fun createImage(info: ImageInfo): VkImage
        fun freeImage(deferTicks: Int, image: VkImage)
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

        override fun createBuffer(info: MemoryInfo): VkBuffer {
            return memStack {
                val pBuffer = mallocLong(1)
                val pAllocation = mallocPointer(1)
                val bufferInfo = callocVkBufferCreateInfo {
                    size(info.size)
                    usage(info.usage)
                    sharingMode(VK_SHARING_MODE_EXCLUSIVE)
                }
                val createInfo = VmaAllocationCreateInfo.calloc(this).apply {
                    usage(info.allocUsage)
                    if (info.createMapped) {
                        flags(VMA_ALLOCATION_CREATE_HOST_ACCESS_SEQUENTIAL_WRITE_BIT or VMA_ALLOCATION_CREATE_MAPPED_BIT)
                    }
                    if (info.isReadback) {
                        flags(VMA_ALLOCATION_CREATE_HOST_ACCESS_RANDOM_BIT or VMA_ALLOCATION_CREATE_MAPPED_BIT)
                    }
                }
                val allocInfo = VmaAllocationInfo.calloc(this)
                vkCheck(vmaCreateBuffer(allocator, bufferInfo, createInfo, pBuffer, pAllocation, allocInfo)) {
                    "Failed allocating buffer ${info.label} (${info.size} bytes): $it"
                }
                val mapped = if (!info.createMapped && !info.isReadback) null else {
                    MemoryUtil.memByteBuffer(allocInfo.pMappedData(), bufferInfo.size().toInt())
                }
                VkBuffer(pBuffer[0], pAllocation[0], bufferInfo.size(), mapped)
            }
        }

        override fun freeBuffer(deferTicks: Int, buffer: VkBuffer) {
            if (deferTicks > 0) {
                ReleaseQueue.enqueue(deferTicks) {
                    vmaDestroyBuffer(allocator, buffer.handle, buffer.allocation)
                }
            } else {
                vmaDestroyBuffer(allocator, buffer.handle, buffer.allocation)
            }
        }

        override fun createImage(info: ImageInfo): VkImage {
            return memStack {
                val pImage = mallocLong(1)
                val pAllocation = mallocPointer(1)
                val imageInfo = callocVkImageCreateInfo {
                    imageType(info.imageType)
                    format(info.format)
                    extent().set(info.width, info.height, info.depth)
                    arrayLayers(info.arrayLayers)
                    mipLevels(info.mipLevels)
                    samples(info.samples)
                    usage(info.usage)
                    tiling(info.tiling)
                    initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    sharingMode(info.sharingMode)
                    flags(info.flags)
                }
                val createInfo = VmaAllocationCreateInfo.calloc(this).apply { usage(info.allocUsage) }
                vkCheck(vmaCreateImage(allocator, imageInfo, createInfo, pImage, pAllocation, null)) {
                    "Failed allocating image ${info.label} (${info.width} x ${info.height} x ${info.depth}, ${info.arrayLayers} layers, ${info.mipLevels} levels): $it"
                }
                VkImage(pImage[0], pAllocation[0])
            }
        }

        override fun freeImage(deferTicks: Int, image: VkImage) {
            if (deferTicks > 0) {
                ReleaseQueue.enqueue(deferTicks) {
                    vmaDestroyImage(allocator, image.handle, image.allocation)
                }
            } else {
                vmaDestroyImage(allocator, image.handle, image.allocation)
            }
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
            ReleaseQueue.enqueue {
                vmaDestroyAllocator(allocator)
                logD { "Destroyed VMA memory allocator" }
            }
        }
    }
}

data class MemoryInfo(
    val size: Long,
    val usage: Int,
    val label: String,
    val allocUsage: Int = VMA_MEMORY_USAGE_AUTO,
    val createMapped: Boolean = false,
    val isReadback: Boolean = false
)

data class ImageInfo(
    val imageType: Int,
    val format: Int,
    val width: Int,
    val height: Int,
    val depth: Int,
    val arrayLayers: Int,
    val mipLevels: Int,
    val samples: Int,
    val usage: Int,
    val label: String,
    val aspectMask: Int,
    val tiling: Int = VK_IMAGE_TILING_OPTIMAL,
    val sharingMode: Int = VK_SHARING_MODE_EXCLUSIVE,
    val allocUsage: Int = VMA_MEMORY_USAGE_AUTO,
    val flags: Int = 0,
)
