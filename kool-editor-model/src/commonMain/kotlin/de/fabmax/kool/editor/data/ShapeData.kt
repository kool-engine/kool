package de.fabmax.kool.editor.data

import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.util.MdColor
import kotlinx.serialization.Serializable

@Serializable
sealed interface ShapeData {
    val name: String
    val isPrimitiveShape: Boolean
        get() = when (this) {
            is Box -> true
            is Capsule -> true
            is Cylinder -> true
            is Rect -> true
            is Sphere -> true
            else -> false
        }

    @Serializable
    data class Box(
        val size: Vec3Data = Vec3Data(1.0, 1.0, 1.0),
        val pose: PoseData = PoseData.IDENTITY,
        val uvScale: Vec2Data = Vec2Data(1.0, 1.0),
        val color: ColorData = ColorData(MdColor.GREY),
    ) : ShapeData {
        override val name: String get() = "Box"
    }

    @Serializable
    data class Sphere(
        val radius: Double = 1.0,
        val steps: Int = 20,
        val sphereType: String = "uv",
        val pose: PoseData = PoseData.IDENTITY,
        val uvScale: Vec2Data = Vec2Data(1.0, 1.0),
        val color: ColorData = ColorData(MdColor.GREY),
    ) : ShapeData {
        override val name: String get() = "Sphere"
    }

    @Serializable
    data class Rect(
        val size: Vec2Data = Vec2Data(1.0, 1.0),
        val pose: PoseData = PoseData.IDENTITY,
        val uvScale: Vec2Data = Vec2Data(1.0, 1.0),
        val color: ColorData = ColorData(MdColor.GREY),
    ) : ShapeData {
        override val name: String get() = "Rect"
    }

    @Serializable
    data class Cylinder(
        val topRadius: Double = 1.0,
        val bottomRadius: Double = 1.0,
        val length: Double = 1.0,
        val steps: Int = 32,
        val pose: PoseData = PoseData.IDENTITY,
        val uvScale: Vec2Data = Vec2Data(1.0, 1.0),
        val color: ColorData = ColorData(MdColor.GREY),
    ) : ShapeData {
        override val name: String get() = "Cylinder"
    }

    @Serializable
    data class Capsule(
        val radius: Double = 1.0,
        val length: Double = 1.0,
        val steps: Int = 32,
        val pose: PoseData = PoseData.IDENTITY,
        val uvScale: Vec2Data = Vec2Data(1.0, 1.0),
        val color: ColorData = ColorData(MdColor.GREY),
    ) : ShapeData {
        override val name: String get() = "Capsule"
    }

    @Serializable
    data class Model(
        val modelPath: String? = null,
        val sceneIndex: Int = 0,
        val animationIndex: Int = -1,
    ) : ShapeData, AssetBased {
        override val name: String get() = "Model"

        override fun toAssetRef() = modelPath?.let { AssetReference.Model(it) }
    }

    @Serializable
    data class Heightmap(
        val mapPath: String? = null,
        val heightOffset: Double = 0.0,
        val heightScale: Double = 100.0,
        val rowScale: Double = 1.0,
        val colScale: Double = 1.0,
        val uvScale: Vec2Data = Vec2Data(1.0, 1.0),
    ) : ShapeData, AssetBased {
        override val name: String get() = "Heightmap"

        override fun toAssetRef() = mapPath?.let { AssetReference.Heightmap(it, heightScale.toFloat(), heightOffset.toFloat()) }
    }

    @Serializable
    data object Custom : ShapeData {
        override val name: String get() = "Custom"
    }

    @Serializable
    data object Plane : ShapeData {
        override val name: String get() = "Plane"
    }

    companion object {
        val defaultBox = Box()
        val defaultSphere = Sphere()
        val defaultRect = Rect()
        val defaultCylinder = Cylinder()
        val defaultCapsule = Capsule()
        val defaultModel = Model()
        val defaultHeightmap = Heightmap()
    }

    interface AssetBased {
        fun toAssetRef(): AssetReference?
    }
}