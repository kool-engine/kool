package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.vk.*
import org.lwjgl.vulkan.VK10.*

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
    }

    fun getDescriptorSet(imageIdx: Int) = descriptorSets[imageIdx]

    private fun createDescriptorSets() {
        memStack {
            val layouts = mallocLong(graphicsPipeline.nImages)
            for (i in 0 until graphicsPipeline.nImages) {
                layouts.put(i, graphicsPipeline.descriptorSetLayout)
            }
            val allocInfo = callocVkDescriptorSetAllocateInfo {
                sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
                descriptorPool(graphicsPipeline.descriptorPool)
                pSetLayouts(layouts)
            }

            val sets = mallocLong(graphicsPipeline.nImages)
            check(vkAllocateDescriptorSets(graphicsPipeline.sys.device.vkDevice, allocInfo, sets) == VK_SUCCESS)
            for (i in 0 until graphicsPipeline.nImages) {
                descriptorSets += sets[i]
            }
        }
    }

    fun clearDescriptorObjects() {
        objects.forEach {
            it.forEach { desc -> desc.destroy(graphicsPipeline) }
            it.clear()
        }
    }

    fun createDescriptorObjects(pipeline: Pipeline) {
        if (pipeline.descriptorSetLayouts.size != 1) {
            TODO()
        }
        pipeline.descriptorSetLayouts[0].descriptors.forEachIndexed { idx, desc ->
            addDescriptor {
                when (desc.type) {
                    DescriptorType.UNIFORM_BUFFER -> UboDescriptor(idx, graphicsPipeline, desc as UniformBuffer)
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
                vkUpdateDescriptorSets(graphicsPipeline.sys.device.vkDevice, descriptorWrite, null)
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
