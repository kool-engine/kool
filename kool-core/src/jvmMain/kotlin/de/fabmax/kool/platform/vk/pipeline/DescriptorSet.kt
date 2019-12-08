package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.DescriptorType
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.TextureSampler
import de.fabmax.kool.pipeline.UniformBuffer
import de.fabmax.kool.platform.vk.*
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10

class DescriptorSet(val graphicsPipeline: GraphicsPipeline, pipeline: Pipeline) {
    private val descriptorSets = mutableListOf<Long>()
    private val objects = Array<MutableList<DescriptorObject>>(graphicsPipeline.swapChain.nImages) { mutableListOf() }

    var allValid = true
        private set
    var isDescriptorSetUpdateRequired = false
        private set

    init {
        createDescriptorSets()
        createDescriptorObjects(pipeline)
    }

    fun getDescriptorSet(imageIdx: Int) = descriptorSets[imageIdx]

    private fun createDescriptorSets() {
        memStack {
            val swapChain = graphicsPipeline.swapChain
            val layouts = mallocLong(swapChain.images.size)
            for (i in swapChain.images.indices) {
                layouts.put(i, graphicsPipeline.descriptorSetLayout)
            }
            val allocInfo = callocVkDescriptorSetAllocateInfo {
                sType(VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
                descriptorPool(graphicsPipeline.descriptorPool)
                pSetLayouts(layouts)
            }

            val sets = mallocLong(swapChain.images.size)
            check(VK10.vkAllocateDescriptorSets(swapChain.sys.device.vkDevice, allocInfo, sets) == VK10.VK_SUCCESS)
            for (i in swapChain.images.indices) {
                descriptorSets += sets[i]
            }
        }
    }

    private fun createDescriptorObjects(pipeline: Pipeline) {
        if (pipeline.descriptorSetLayouts.size != 1) {
            TODO()
        }
        pipeline.descriptorSetLayouts[0].descriptors.forEachIndexed { idx, desc ->
            addDescriptor {
                // fixme: more reasonable binding index needed?
                when (desc.type) {
                    DescriptorType.UNIFORM_BUFFER -> {
                        desc as UniformBuffer
                        val usage = VK10.VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT
                        val allocUsage = Vma.VMA_MEMORY_USAGE_CPU_TO_GPU
                        val buffer = Buffer(graphicsPipeline.swapChain.sys, desc.size.toLong(), usage, allocUsage).also {
                            graphicsPipeline.addDependingResource(it)
                        }
                        UboDescriptor(idx, desc, buffer)
                    }

                    DescriptorType.IMAGE_SAMPLER -> {
                        desc as TextureSampler
                        SamplerDescriptor(idx, desc)
                    }
                }
            }
        }
    }

    private fun addDescriptor(block: () -> DescriptorObject): Int {
        for (i in 0 until graphicsPipeline.swapChain.nImages) {
            objects[i].add(block())
        }
        return objects[0].size - 1
    }

    fun clear() {
        objects.forEach { it.clear() }
    }

    fun updateDescriptorSets() {
        if (isDescriptorSetUpdateRequired) {
            clearUpdateRequired()

            if (graphicsPipeline.pipeline.descriptorSetLayouts.size != 1) {
                TODO()
            }

            val swapChain = graphicsPipeline.swapChain
            val descriptors = graphicsPipeline.pipeline.descriptorSetLayouts[0].descriptors
            for (imageIndex in 0 until swapChain.nImages) {
                memStack {
                    val descriptorWrite = callocVkWriteDescriptorSetN(descriptors.size) {
                        for (descIdx in descriptors.indices) {
                            val descObj = objects[imageIndex][descIdx]
                            descObj.setDescriptorSet(this@memStack, this[descIdx], descriptorSets[imageIndex])
                        }
                    }
                    VK10.vkUpdateDescriptorSets(swapChain.sys.device.vkDevice, descriptorWrite, null)
                }
            }
        }
    }

    private fun clearUpdateRequired() {
        isDescriptorSetUpdateRequired = false
        objects.forEach { descs -> descs.forEach { it.isDescriptorSetUpdateRequired = false } }
    }

    fun updateDescriptors(cmd: DrawCommand, imageIndex: Int, sys: VkSystem): Boolean {
        val descs = objects[imageIndex]
        allValid = true
        isDescriptorSetUpdateRequired = false
        for (i in descs.indices) {
            val desc = descs[i]
            desc.update(cmd, sys)
            allValid = allValid && desc.isValid
            isDescriptorSetUpdateRequired = isDescriptorSetUpdateRequired || desc.isDescriptorSetUpdateRequired
        }
        return allValid
    }
}
