package de.fabmax.kool.pipeline

actual class PlatformAttributeProps actual constructor(attribute: Attribute) {

    val nSlots: Int
    val attribSize: Int

    init {
        when (attribute.type) {
            GlslType.FLOAT -> {
                nSlots = 1
                attribSize = 1
            }
            GlslType.VEC_2F -> {
                nSlots = 1
                attribSize = 2
            }
            GlslType.VEC_3F -> {
                nSlots = 1
                attribSize = 3
            }
            GlslType.VEC_4F -> {
                nSlots = 1
                attribSize = 4
            }
            GlslType.MAT_2F -> {
                nSlots = 2
                attribSize = 2
            }
            GlslType.MAT_3F -> {
                nSlots = 3
                attribSize = 3
            }
            GlslType.MAT_4F -> {
                nSlots = 4
                attribSize = 4
            }
            else -> {
                throw IllegalArgumentException("Attribute type not supported: ${attribute.type}")
            }
        }
    }

}
