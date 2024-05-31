package de.fabmax.kool.editor.data

import de.fabmax.kool.util.MdColor
import kotlinx.serialization.Serializable

@Serializable
sealed class ShapeData {

    abstract val name: String
    abstract val hasUvs: Boolean
    abstract val common: CommonShapeData

    fun copyShape(common: CommonShapeData = this.common): ShapeData {
        val copied = when (this) {
            is Box -> copy(common = common)
            is Capsule -> copy(common = common)
            is Cylinder -> copy(common = common)
            is Custom -> copy(common = common)
            is Sphere -> copy(common = common)
            is Rect -> copy(common = common)
            is Heightmap -> copy(common = common)
            is Plane -> copy(common = common)
        }
        return copied
    }

    @Serializable
    data class CommonShapeData(
        val pose: TransformData = TransformData.IDENTITY,
        val vertexColor: ColorData = ColorData(MdColor.GREY.toLinear()),
        val uvScale: Vec2Data = Vec2Data(1.0, 1.0)
    )

    @Serializable
    data class Box(val size: Vec3Data, override val common: CommonShapeData = CommonShapeData()) : ShapeData() {
        override val name: String get() = "Box"
        override val hasUvs: Boolean = true
    }

    @Serializable
    data class Sphere(
        val radius: Double,
        val steps: Int = 20,
        val sphereType: String = "uv",
        override val common: CommonShapeData = CommonShapeData()
    ) : ShapeData() {

        override val name: String get() = "Sphere"
        override val hasUvs: Boolean = true
    }

    @Serializable
    data class Rect(val size: Vec2Data, override val common: CommonShapeData = CommonShapeData()) : ShapeData() {
        override val name: String get() = "Rect"
        override val hasUvs: Boolean = true
    }

    @Serializable
    data class Cylinder(
        val topRadius: Double,
        val bottomRadius: Double,
        val length: Double,
        val steps: Int = 32,
        override val common: CommonShapeData = CommonShapeData()
    ) : ShapeData() {

        override val name: String get() = "Cylinder"
        override val hasUvs: Boolean = true
    }

    @Serializable
    data class Capsule(
        val radius: Double,
        val length: Double,
        val steps: Int = 32,
        override val common: CommonShapeData = CommonShapeData()
    ) : ShapeData() {

        override val name: String get() = "Capsule"
        override val hasUvs: Boolean = false
    }

    @Serializable
    data class Custom(override val common: CommonShapeData = CommonShapeData()) : ShapeData() {
        override val name: String get() = "Custom"
        override val hasUvs: Boolean = false
    }

    @Serializable
    data class Heightmap(
        val mapPath: String,
        val heightOffset: Double = 0.0,
        val heightScale: Double = 100.0,
        val rowScale: Double = 1.0,
        val colScale: Double = 1.0,
        override val common: CommonShapeData = CommonShapeData()
    ) : ShapeData() {
        override val name: String get() = "Heightmap"
        override val hasUvs: Boolean = true
    }

    @Serializable
    data class Plane(override val common: CommonShapeData = CommonShapeData()) : ShapeData() {
        override val name: String get() = "Plane"
        override val hasUvs: Boolean = false
    }

    companion object {
        val defaultCustom = Custom()
        val defaultBox = Box(Vec3Data(1.0, 1.0, 1.0))
        val defaultSphere = Sphere(1.0)
        val defaultRect = Rect(Vec2Data(1.0, 1.0))
        val defaultCylinder = Cylinder(1.0, 1.0, 1.0)
        val defaultCapsule = Capsule(1.0, 1.0)
        val defaultHeightmap = Heightmap("")
        val defaultPlane = Plane()
    }
}