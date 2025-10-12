package de.fabmax.kool.scene

import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct

object InstanceLayouts {
    object Empty : Struct("InstanceLayoutEmpty", MemoryLayout.TightlyPacked)

    object ModelMat : Struct("InstanceLayoutModelMat", MemoryLayout.TightlyPacked) {
        val modelMat = mat4(Attribute.INSTANCE_MODEL_MAT.name)
    }

    object ModelMatColor : Struct("InstanceLayoutModelMatColor", MemoryLayout.TightlyPacked) {
        val modelMat = mat4(Attribute.INSTANCE_MODEL_MAT.name)
        val color = float4(Attribute.INSTANCE_COLOR.name)
    }
}