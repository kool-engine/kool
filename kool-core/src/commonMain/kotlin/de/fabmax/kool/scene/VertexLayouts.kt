package de.fabmax.kool.scene

import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct

object VertexLayouts {

    object Empty : Struct("Empty", MemoryLayout.TightlyPacked)

    object Position : Struct("attrPosition", MemoryLayout.TightlyPacked) {
        val position = float3("attrPosition")
    }

    object Normal : Struct("attrNormal", MemoryLayout.TightlyPacked) {
        val normal = float3("attrNormal")
    }

    object Tangent : Struct("attrTangent", MemoryLayout.TightlyPacked) {
        val tangent = float4("attrTangent")
    }

    object TexCoord : Struct("attrTexCoord", MemoryLayout.TightlyPacked) {
        val texCoord = float2("attrTexCoord")
    }

    object Color : Struct("attrColor", MemoryLayout.TightlyPacked) {
        val color = float4("attrColor")
    }

    object EmissiveColor : Struct("attrEmissiveColor", MemoryLayout.TightlyPacked) {
        val emissiveColor = float3("attrEmissiveColor")
    }

    object Metallic : Struct("attrMetallic", MemoryLayout.TightlyPacked) {
        val metallic = float1("attrMetallic")
    }

    object Roughness : Struct("attrRoughness", MemoryLayout.TightlyPacked) {
        val roughness = float1("attrRoughness")
    }

    object Joint : Struct("attrJoint", MemoryLayout.TightlyPacked) {
        val joint = int4("attrJoint")
    }

    object Weight : Struct("attrWeight", MemoryLayout.TightlyPacked) {
        val weight = float4("attrWeight")
    }

    object PositionColor : Struct("PositionColor", MemoryLayout.TightlyPacked) {
        val position = include(Position.position)
        val color = include(Color.color)
    }

    object PositionNormalColor : Struct("PositionNormalColor", MemoryLayout.TightlyPacked) {
        val position = include(Position.position)
        val normal = include(Normal.normal)
        val color = include(Color.color)
    }

    object PositionNormalColorMetalRough : Struct("PositionNormalColor", MemoryLayout.TightlyPacked) {
        val position = include(Position.position)
        val normal = include(Normal.normal)
        val color = include(Color.color)
        val metallic = include(Metallic.metallic)
        val roughness = include(Roughness.roughness)
    }

    object PositionNormal : Struct("PositionNormal", MemoryLayout.TightlyPacked) {
        val position = include(Position.position)
        val normal = include(Normal.normal)
    }

    object PositionTexCoord : Struct("PositionTexCoord", MemoryLayout.TightlyPacked) {
        val position = include(Position.position)
        val texCoord = include(TexCoord.texCoord)
    }

    object PositionNormalTexCoord : Struct("PositionNormalTexCoord", MemoryLayout.TightlyPacked) {
        val position = include(Position.position)
        val normal = include(Normal.normal)
        val texCoord = include(TexCoord.texCoord)
    }

    object PositionNormalTexCoordTangent : Struct("PositionNormalTexCoordTangent", MemoryLayout.TightlyPacked) {
        val position = include(Position.position)
        val normal = include(Normal.normal)
        val texCoord = include(TexCoord.texCoord)
        val tangent = include(Tangent.tangent)
    }

    object PositionNormalTexCoordColor : Struct("PositionNormalTexCoordColor", MemoryLayout.TightlyPacked) {
        val position = include(Position.position)
        val normal = include(Normal.normal)
        val texCoord = include(TexCoord.texCoord)
        val color = include(Color.color)
    }

    object PositionNormalTexCoordColorTangent : Struct("PositionNormalTexCoordColorTangent", MemoryLayout.TightlyPacked) {
        val position = include(Position.position)
        val normal = include(Normal.normal)
        val texCoord = include(TexCoord.texCoord)
        val color = include(PositionColor.color)
        val tangent = include(Tangent.tangent)
    }
}