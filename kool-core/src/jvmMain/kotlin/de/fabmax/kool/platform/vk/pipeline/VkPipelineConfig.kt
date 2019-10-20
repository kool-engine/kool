package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.pipeline.AttributeType
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.DepthTest
import de.fabmax.kool.pipeline.PipelineConfig
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkVertexInputAttributeDescription
import org.lwjgl.vulkan.VkVertexInputBindingDescription

data class VkPipelineConfig(
    val shaderStages: List<ShaderStage>,

    val nVertexAttributes: Int,
    val vertexInputBindingDescription: (VkVertexInputBindingDescription.Buffer) -> Unit,
    val vertexInputAttributeDescription: (VkVertexInputAttributeDescription.Buffer) -> Unit,

    val isDepthTestEnabled: Boolean = true,
    val cullMode: Int = VK_CULL_MODE_BACK_BIT,
    val isCullingEnabled: Boolean = true
) {
    companion object {
        fun fromPipelineConfig(cfg: PipelineConfig): VkPipelineConfig {
            val shaderStages = mutableListOf<ShaderStage>()
            (cfg.shaderCode as SpirvShaderCode).let {
                shaderStages += it.vertexStage
                shaderStages += it.fragmentStage
            }

            val nAttribs = cfg.vertexLayout.bindings[0].attributes.size
            val vertexInputBindingDescription: (VkVertexInputBindingDescription.Buffer) -> Unit = {
                it.binding(cfg.vertexLayout.bindings[0].binding)
                it.stride(cfg.vertexLayout.bindings[0].strideBytes)
                it.inputRate(VK_VERTEX_INPUT_RATE_VERTEX)
            }

            val vertexInputAttributeDescription: (VkVertexInputAttributeDescription.Buffer) -> Unit = {
                cfg.vertexLayout.bindings[0].attributes.forEachIndexed { idx, attrib ->
                    it[idx].apply {
                        binding(attrib.binding)
                        location(attrib.location)
                        offset(attrib.offset)
                        format(when (attrib.type) {
                            AttributeType.FLOAT -> VK_FORMAT_R32_SFLOAT
                            AttributeType.VEC_2F -> VK_FORMAT_R32G32_SFLOAT
                            AttributeType.VEC_3F -> VK_FORMAT_R32G32B32_SFLOAT
                            AttributeType.VEC_4F -> VK_FORMAT_R32G32B32A32_SFLOAT
                            AttributeType.COLOR_4F -> VK_FORMAT_R32G32B32A32_SFLOAT
                            else -> throw IllegalStateException("Attribute is not a float type")
                        })

//                        println("attrib $idx, ${attrib.name}")
//                        println("  binding: ${binding()}")
//                        println("  location: ${location()}")
//                        println("  offset: ${offset()}")
//                        println("  format: ${format()}")
                    }
                }
            }

            val isDepthTest = cfg.depthTest != DepthTest.DISABLED

            val cullMode = when (cfg.cullMethod) {
                CullMethod.FRONT_FACE -> VK_CULL_MODE_FRONT_BIT
                CullMethod.BACK_FACE -> VK_CULL_MODE_BACK_BIT
                CullMethod.NO_CULL -> 0
            }
            val isCullingEnabled = cfg.cullMethod != CullMethod.NO_CULL

            return VkPipelineConfig(shaderStages, nAttribs, vertexInputBindingDescription, vertexInputAttributeDescription, isDepthTest, cullMode, isCullingEnabled)
        }
    }
}