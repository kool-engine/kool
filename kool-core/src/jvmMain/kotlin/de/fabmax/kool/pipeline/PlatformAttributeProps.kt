package de.fabmax.kool.pipeline

import org.lwjgl.vulkan.VK10

actual class PlatformAttributeProps actual constructor(attribute: Attribute) {

    val nSlots: Int
    val slotOffset: Int
    val slotType: Int

    init {
        when (attribute.type) {
            GlslType.FLOAT -> {
                nSlots = 1
                slotOffset = 0
                slotType = VK10.VK_FORMAT_R32_SFLOAT
            }
            GlslType.VEC_2F -> {
                nSlots = 1
                slotOffset = 0
                slotType = VK10.VK_FORMAT_R32G32_SFLOAT
            }
            GlslType.VEC_3F -> {
                nSlots = 1
                slotOffset = 0
                slotType = VK10.VK_FORMAT_R32G32B32_SFLOAT
            }
            GlslType.VEC_4F -> {
                nSlots = 1
                slotOffset = 0
                slotType = VK10.VK_FORMAT_R32G32B32A32_SFLOAT
            }
            GlslType.MAT_2F -> {
                nSlots = 2
                slotOffset = GlslType.VEC_2F.size
                slotType = VK10.VK_FORMAT_R32G32_SFLOAT
            }
            GlslType.MAT_3F -> {
                nSlots = 3
                slotOffset = GlslType.VEC_3F.size
                slotType = VK10.VK_FORMAT_R32G32B32_SFLOAT
            }
            GlslType.MAT_4F -> {
                nSlots = 4
                slotOffset = GlslType.VEC_4F.size
                slotType = VK10.VK_FORMAT_R32G32B32A32_SFLOAT
            }
            else -> {
                throw IllegalArgumentException("Attribute type not supported: ${attribute.type}")
            }
        }
    }

}