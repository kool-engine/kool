package de.fabmax.kool.pipeline

import org.lwjgl.vulkan.VK10.*

actual class PlatformAttributeProps actual constructor(attribute: Attribute) {

    actual val nSlots: Int
    val slotOffset: Int
    val slotType: Int

    val glAttribSize: Int

    init {
        when (attribute.type) {
            GlslType.FLOAT -> {
                nSlots = 1
                slotOffset = 0
                slotType = VK_FORMAT_R32_SFLOAT
                glAttribSize = 1
            }
            GlslType.VEC_2F -> {
                nSlots = 1
                slotOffset = 0
                slotType = VK_FORMAT_R32G32_SFLOAT
                glAttribSize = 2
            }
            GlslType.VEC_3F -> {
                nSlots = 1
                slotOffset = 0
                slotType = VK_FORMAT_R32G32B32_SFLOAT
                glAttribSize = 3
            }
            GlslType.VEC_4F -> {
                nSlots = 1
                slotOffset = 0
                slotType = VK_FORMAT_R32G32B32A32_SFLOAT
                glAttribSize = 4
            }
            GlslType.MAT_2F -> {
                nSlots = 2
                slotOffset = GlslType.VEC_2F.byteSize
                slotType = VK_FORMAT_R32G32_SFLOAT
                glAttribSize = 2
            }
            GlslType.MAT_3F -> {
                nSlots = 3
                slotOffset = GlslType.VEC_3F.byteSize
                slotType = VK_FORMAT_R32G32B32_SFLOAT
                glAttribSize = 3
            }
            GlslType.MAT_4F -> {
                nSlots = 4
                slotOffset = GlslType.VEC_4F.byteSize
                slotType = VK_FORMAT_R32G32B32A32_SFLOAT
                glAttribSize = 4
            }
            GlslType.INT -> {
                nSlots = 1
                slotOffset = 0
                slotType = VK_FORMAT_R32_SINT
                glAttribSize = 1
            }
            GlslType.VEC_2I -> {
                nSlots = 1
                slotOffset = 0
                slotType = VK_FORMAT_R32G32_SINT
                glAttribSize = 2
            }
            GlslType.VEC_3I -> {
                nSlots = 1
                slotOffset = 0
                slotType = VK_FORMAT_R32G32B32_SINT
                glAttribSize = 3
            }
            GlslType.VEC_4I -> {
                nSlots = 1
                slotOffset = 0
                slotType = VK_FORMAT_R32G32B32A32_SINT
                glAttribSize = 4
            }
        }
    }

}