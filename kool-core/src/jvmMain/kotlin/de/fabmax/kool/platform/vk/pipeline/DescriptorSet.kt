package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.vk.*
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10

class DescriptorSet(val graphicsPipeline: GraphicsPipeline, pipeline: Pipeline) {
    private val descriptorSets = mutableListOf<Long>()
    private val objects = Array<MutableList<DescriptorObject>>(graphicsPipeline.nImages) { mutableListOf() }

    var allValid = true
        private set
//    var isDescriptorSetUpdateRequired = false
//        private set

    private val isDescriptorSetUpdateRequired = BooleanArray(graphicsPipeline.nImages) { false }

    init {
        createDescriptorSets()
        createDescriptorObjects(pipeline)
    }

    fun getDescriptorSet(imageIdx: Int) = descriptorSets[imageIdx]

    private fun createDescriptorSets() {
        memStack {
            val layouts = mallocLong(graphicsPipeline.nImages)
            for (i in 0 until graphicsPipeline.nImages) {
                layouts.put(i, graphicsPipeline.descriptorSetLayout)
            }
            val allocInfo = callocVkDescriptorSetAllocateInfo {
                sType(VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
                descriptorPool(graphicsPipeline.descriptorPool)
                pSetLayouts(layouts)
            }

            val sets = mallocLong(graphicsPipeline.nImages)
            check(VK10.vkAllocateDescriptorSets(graphicsPipeline.sys.device.vkDevice, allocInfo, sets) == VK10.VK_SUCCESS)
            for (i in 0 until graphicsPipeline.nImages) {
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
                        val buffer = Buffer(graphicsPipeline.sys, desc.size.toLong(), usage, allocUsage).also {
                            graphicsPipeline.addDependingResource(it)
                        }
                        UboDescriptor(idx, desc, buffer)
                    }

                    DescriptorType.IMAGE_SAMPLER -> SamplerDescriptor(idx, desc as TextureSampler)
                    DescriptorType.CUBE_IMAGE_SAMPLER -> SamplerDescriptor(idx, desc as CubeMapSampler)
                }
            }
        }
    }

    private fun addDescriptor(block: () -> DescriptorObject): Int {
        for (i in 0 until graphicsPipeline.nImages) {
            objects[i].add(block())
        }
        return objects[0].size - 1
    }

    fun clear() {
        objects.forEach { it.clear() }
    }

    fun updateDescriptorSets(imageIdx: Int) {
        if (isDescriptorSetUpdateRequired[imageIdx]) {
            clearUpdateRequired(imageIdx)

            if (graphicsPipeline.pipeline.descriptorSetLayouts.size != 1) {
                TODO()
            }

            val descriptors = graphicsPipeline.pipeline.descriptorSetLayouts[0].descriptors
            memStack {
                val descriptorWrite = callocVkWriteDescriptorSetN(descriptors.size) {
                    for (descIdx in descriptors.indices) {
                        val descObj = objects[imageIdx][descIdx]
                        descObj.setDescriptorSet(this@memStack, this[descIdx], descriptorSets[imageIdx])
                    }
                }
                VK10.vkUpdateDescriptorSets(graphicsPipeline.sys.device.vkDevice, descriptorWrite, null)
            }
        }
    }

    private fun clearUpdateRequired(imageIdx: Int) {
        isDescriptorSetUpdateRequired[imageIdx] = false
        objects[imageIdx].forEach { it.isDescriptorSetUpdateRequired = false }
    }

    fun updateDescriptors(cmd: DrawCommand, imageIndex: Int, sys: VkSystem): Boolean {
        val descs = objects[imageIndex]
        allValid = true
        var updateRequired = false
        for (i in descs.indices) {
            val desc = descs[i]
            desc.update(cmd, sys)
            allValid = allValid && desc.isValid
            updateRequired = updateRequired || desc.isDescriptorSetUpdateRequired
        }
        isDescriptorSetUpdateRequired[imageIndex] = updateRequired
        return allValid
    }
}
