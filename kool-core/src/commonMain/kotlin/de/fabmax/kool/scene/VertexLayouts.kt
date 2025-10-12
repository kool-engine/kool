package de.fabmax.kool.scene

import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct

object VertexLayouts {
    object Empty : Struct("Empty", MemoryLayout.TightlyPacked)

    object Position : Struct("Position", MemoryLayout.TightlyPacked) {
        val position = float3(Attribute.POSITIONS.name)
    }

    object PositionColor : Struct("PositionColor", MemoryLayout.TightlyPacked) {
        val position = float3(Attribute.POSITIONS.name)
        val color = float4(Attribute.COLORS.name)
    }

    object PositionNormalColor : Struct("PositionNormalColor", MemoryLayout.TightlyPacked) {
        val position = float3(Attribute.POSITIONS.name)
        val normal = float3(Attribute.NORMALS.name)
        val color = float4(Attribute.COLORS.name)
    }

    object PositionNormal : Struct("PositionNormal", MemoryLayout.TightlyPacked) {
        val position = float3(Attribute.POSITIONS.name)
        val normal = float3(Attribute.NORMALS.name)
    }

    object PositionTexCoord : Struct("PositionTexCoord", MemoryLayout.TightlyPacked) {
        val position = float3(Attribute.POSITIONS.name)
        val textureCoords = float2(Attribute.TEXTURE_COORDS.name)
    }

    object PositionNormalTexCoord : Struct("PositionNormalTexCoord", MemoryLayout.TightlyPacked) {
        val position = float3(Attribute.POSITIONS.name)
        val normal = float3(Attribute.NORMALS.name)
        val textureCoords = float2(Attribute.TEXTURE_COORDS.name)
    }

    object PositionNormalTexCoordTangent : Struct("PositionNormalTexCoordTangent", MemoryLayout.TightlyPacked) {
        val position = float3(Attribute.POSITIONS.name)
        val normal = float3(Attribute.NORMALS.name)
        val textureCoords = float2(Attribute.TEXTURE_COORDS.name)
        val tangent = float4(Attribute.TANGENTS.name)
    }
}