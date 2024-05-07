package de.fabmax.kool.editor.data

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.MdColor
import kotlinx.serialization.Serializable

@Serializable
class MeshComponentData() : ComponentData {
    val shapes = mutableListOf<ShapeData>()

    constructor(singleShape: ShapeData) : this() {
        shapes += singleShape
    }
}

@Serializable
sealed class ShapeData {

    abstract val name: String
    abstract val hasUvs: Boolean
    abstract val common: CommonShapeData

    abstract fun generate(builder: MeshBuilder)

    fun copyShape(common: CommonShapeData = this.common): ShapeData {
        val copied = when (this) {
            is Box -> copy(common = common)
            is Capsule -> copy(common = common)
            is Cylinder -> copy(common = common)
            is Empty -> copy(common = common)
            is Sphere -> copy(common = common)
            is Rect -> copy(common = common)
            is Heightmap -> copy(common = common)
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

        override fun generate(builder: MeshBuilder) {
            builder.apply {
                cube {
                    size.set(this@Box.size.toVec3f())
                }
            }
        }
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

        override fun generate(builder: MeshBuilder) {
            builder.apply {
                if (sphereType == "uv") {
                    uvSphere {
                        radius = this@Sphere.radius.toFloat()
                        steps = this@Sphere.steps
                    }
                } else {
                    icoSphere {
                        radius = this@Sphere.radius.toFloat()
                        steps = this@Sphere.steps
                    }
                }
            }
        }
    }

    @Serializable
    data class Rect(val size: Vec2Data, override val common: CommonShapeData = CommonShapeData()) : ShapeData() {
        override val name: String get() = "Rect"
        override val hasUvs: Boolean = true

        override fun generate(builder: MeshBuilder) {
            builder.apply {
                grid {
                    sizeX = size.x.toFloat()
                    sizeY = size.y.toFloat()
                }
            }
        }
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

        override fun generate(builder: MeshBuilder) {
            builder.apply {
                withTransform {
                    // generate cylinder in x-axis major orientation to make it align with physics geometry
                    rotate(90f.deg, Vec3f.Z_AXIS)
                    cylinder {
                        height = this@Cylinder.length.toFloat()
                        topRadius = this@Cylinder.topRadius.toFloat()
                        bottomRadius = this@Cylinder.bottomRadius.toFloat()
                        steps = this@Cylinder.steps
                    }
                }
            }
        }
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

        override fun generate(builder: MeshBuilder) {
            builder.withTransform {
                profile {
                    val r = radius.toFloat()
                    val h = length.toFloat()
                    val hh = h / 2f
                    simpleShape(false) {
                        xyArc(Vec2f(hh + r, 0f), Vec2f(hh, 0f), 90f.deg, steps / 2, true)
                        xyArc(Vec2f(-hh, r), Vec2f(-hh, 0f), 90f.deg, steps / 2, true)
                    }
                    for (i in 0 .. steps) {
                        sample()
                        rotate(360f.deg / steps, 0f.deg, 0f.deg)
                    }
                }
            }
        }
    }

    @Serializable
    data class Heightmap(
        val mapPath: String,
        val heightOffset: Float = 0f,
        val heightScale: Float = 0.01f,
        val rowScale: Float = 1f,
        val colScale: Float = 1f,
        override val common: CommonShapeData = CommonShapeData()
    ) : ShapeData() {
        override val name: String get() = "Heightmap"
        override val hasUvs: Boolean = true

        override fun generate(builder: MeshBuilder) {
            TODO()
        }
    }

    @Serializable
    data class Empty(override val common: CommonShapeData = CommonShapeData()) : ShapeData() {
        override val name: String get() = "Empty"
        override val hasUvs: Boolean = false

        override fun generate(builder: MeshBuilder) {
            // empty - nothing to generate
        }
    }

    companion object {
        val defaultEmpty = Empty()
        val defaultBox = Box(Vec3Data(1.0, 1.0, 1.0))
        val defaultSphere = Sphere(1.0)
        val defaultRect = Rect(Vec2Data(1.0, 1.0))
        val defaultCylinder = Cylinder(1.0, 1.0, 1.0)
        val defaultCapsule = Capsule(1.0, 1.0)
    }
}
