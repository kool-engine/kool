package de.fabmax.kool.editor.data

import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.MdColor
import kotlinx.serialization.Serializable

@Serializable
class MeshComponentData() : ComponentData {
    val shapes = mutableListOf<MeshShapeData>()

    constructor(singleShape: MeshShapeData) : this() {
        shapes += singleShape
    }
}

@Serializable
sealed class MeshShapeData {

    var pose = TransformData.IDENTITY
    var vertexColor = ColorData(MdColor.GREY)

    abstract val name: String

    abstract fun generate(builder: MeshBuilder)

    @Serializable
    data class Box(val size: Vec3Data) : MeshShapeData() {
        override val name: String get() = "Box"

        override fun generate(builder: MeshBuilder) {
            builder.apply {
                cube(centered = true) {
                    size.set(this@Box.size.toVec3f())
                }
            }
        }
    }

    @Serializable
    data class IcoSphere(val radius: Double, val subDivisions: Int) : MeshShapeData() {
        override val name: String get() = "Ico-Sphere"

        override fun generate(builder: MeshBuilder) {
            builder.apply {
                icoSphere {
                    radius = this@IcoSphere.radius.toFloat()
                    steps = subDivisions
                }
            }
        }
    }

    @Serializable
    data class UvSphere(val radius: Double, val steps: Int) : MeshShapeData() {
        override val name: String get() = "UV-Sphere"

        override fun generate(builder: MeshBuilder) {
            builder.apply {
                uvSphere {
                    radius = this@UvSphere.radius.toFloat()
                    steps = this@UvSphere.steps
                }
            }
        }
    }

    @Serializable
    data class Rect(val size: Vec2Data) : MeshShapeData() {
        override val name: String get() = "Rect"

        override fun generate(builder: MeshBuilder) {
            TODO()
        }
    }

    @Serializable
    data class Cylinder(val topRadius: Double, val bottomRadius: Double, val length: Double, val steps: Int) : MeshShapeData() {
        override val name: String get() = "Cylinder"

        override fun generate(builder: MeshBuilder) {
            TODO()
        }
    }

    @Serializable
    object Empty : MeshShapeData() {
        override val name: String get() = "Empty"

        override fun generate(builder: MeshBuilder) {
            builder.clear()
        }
    }

    companion object {
        val defaultBox = Box(Vec3Data(1.0, 1.0, 1.0))
        val defaultIcoSphere = IcoSphere(1.0, 2)
        val defaultUvSphere = UvSphere(1.0, 20)
        val defaultRect = Rect(Vec2Data(1.0, 1.0))
        val defaultCylinder = Cylinder(1.0, 1.0, 1.0, 16)
    }
}
