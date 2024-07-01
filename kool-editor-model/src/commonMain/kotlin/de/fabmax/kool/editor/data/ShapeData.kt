package de.fabmax.kool.editor.data

import de.fabmax.kool.util.MdColor
import kotlinx.serialization.Serializable

@Serializable
sealed interface ShapeData {

    val name: String

    @Serializable
    data class Box(
        val size: Vec3Data = Vec3Data(1.0, 1.0, 1.0),
        val pose: TransformData = TransformData.IDENTITY,
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
        val pose: TransformData = TransformData.IDENTITY,
        val uvScale: Vec2Data = Vec2Data(1.0, 1.0),
        val color: ColorData = ColorData(MdColor.GREY),
    ) : ShapeData {

        override val name: String get() = "Sphere"
    }

    @Serializable
    data class Rect(
        val size: Vec2Data = Vec2Data(1.0, 1.0),
        val pose: TransformData = TransformData.IDENTITY,
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
        val pose: TransformData = TransformData.IDENTITY,
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
        val pose: TransformData = TransformData.IDENTITY,
        val uvScale: Vec2Data = Vec2Data(1.0, 1.0),
        val color: ColorData = ColorData(MdColor.GREY),
    ) : ShapeData {
        override val name: String get() = "Capsule"
    }

    @Serializable
    data class Model(
        val modelPath: String = "",
        val sceneIndex: Int = 0,
        val animationIndex: Int = -1,
    ) : ShapeData {
        override val name: String get() = "Model"
    }

    @Serializable
    data class Heightmap(
        val mapPath: String = "",
        val heightOffset: Double = 0.0,
        val heightScale: Double = 100.0,
        val rowScale: Double = 1.0,
        val colScale: Double = 1.0,
        val uvScale: Vec2Data = Vec2Data(1.0, 1.0),
    ) : ShapeData {
        override val name: String get() = "Heightmap"
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
}