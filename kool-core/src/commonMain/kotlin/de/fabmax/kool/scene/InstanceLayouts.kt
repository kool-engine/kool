package de.fabmax.kool.scene

import de.fabmax.kool.modules.ksl.lang.KslVertexStage
import de.fabmax.kool.util.*

object InstanceLayouts {
    object Empty : Struct("InstanceLayoutEmpty", MemoryLayout.TightlyPacked)

    object ModelMat : Struct("instattr_model_mat", MemoryLayout.TightlyPacked) {
        val modelMat = mat4("instattr_model_mat")
    }

    object Color : Struct("instattr_color", MemoryLayout.TightlyPacked) {
        val color = float4("instattr_color")
    }

    object ModelMatColor : Struct("InstanceLayoutModelMatColor", MemoryLayout.TightlyPacked) {
        val modelMat = include(ModelMat.modelMat)
        val color = include(Color.color)
    }
}

fun KslVertexStage.instanceAttrib(layoutMember: Float1Member<*>) = instanceAttribFloat1(layoutMember.name)
fun KslVertexStage.instanceAttrib(layoutMember: Float2Member<*>) = instanceAttribFloat2(layoutMember.name)
fun KslVertexStage.instanceAttrib(layoutMember: Float3Member<*>) = instanceAttribFloat3(layoutMember.name)
fun KslVertexStage.instanceAttrib(layoutMember: Float4Member<*>) = instanceAttribFloat4(layoutMember.name)

fun KslVertexStage.instanceAttrib(layoutMember: Int1Member<*>) = instanceAttribInt1(layoutMember.name)
fun KslVertexStage.instanceAttrib(layoutMember: Int2Member<*>) = instanceAttribInt2(layoutMember.name)
fun KslVertexStage.instanceAttrib(layoutMember: Int3Member<*>) = instanceAttribInt3(layoutMember.name)
fun KslVertexStage.instanceAttrib(layoutMember: Int4Member<*>) = instanceAttribInt4(layoutMember.name)

fun KslVertexStage.instanceAttrib(layoutMember: Uint1Member<*>) = instanceAttribUint1(layoutMember.name)
fun KslVertexStage.instanceAttrib(layoutMember: Uint2Member<*>) = instanceAttribUint2(layoutMember.name)
fun KslVertexStage.instanceAttrib(layoutMember: Uint3Member<*>) = instanceAttribUint3(layoutMember.name)
fun KslVertexStage.instanceAttrib(layoutMember: Uint4Member<*>) = instanceAttribUint4(layoutMember.name)

fun KslVertexStage.instanceAttrib(layoutMember: Mat2Member<*>) = instanceAttribMat2(layoutMember.name)
fun KslVertexStage.instanceAttrib(layoutMember: Mat3Member<*>) = instanceAttribMat3(layoutMember.name)
fun KslVertexStage.instanceAttrib(layoutMember: Mat4Member<*>) = instanceAttribMat4(layoutMember.name)
