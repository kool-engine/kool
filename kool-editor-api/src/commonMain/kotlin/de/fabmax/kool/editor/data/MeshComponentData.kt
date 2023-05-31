package de.fabmax.kool.editor.data

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape
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
                cube {
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
            builder.apply {
                grid {
                    sizeX = size.x.toFloat()
                    sizeY = size.y.toFloat()
                }
            }
        }
    }

    @Serializable
    data class Cylinder(val topRadius: Double, val bottomRadius: Double, val height: Double, val steps: Int) : MeshShapeData() {
        override val name: String get() = "Cylinder"

        override fun generate(builder: MeshBuilder) {
            builder.apply {
                cylinder {
                    height = this@Cylinder.height.toFloat()
                    topRadius = this@Cylinder.topRadius.toFloat()
                    bottomRadius = this@Cylinder.bottomRadius.toFloat()
                    steps = this@Cylinder.steps
                }
            }
        }
    }

    @Serializable
    data class Capsule(val radius: Double, val length: Double, val steps: Int) : MeshShapeData() {
        override val name: String get() = "Capsule"

        override fun generate(builder: MeshBuilder) {
            builder.withTransform {
                profile {
                    val r = radius.toFloat()
                    val h = length.toFloat()
                    val hh = h / 2f
                    simpleShape(false) {
                        xyArc(Vec2f(hh + r, 0f), Vec2f(hh, 0f), 90f, steps / 2, true)
                        xyArc(Vec2f(-hh, r), Vec2f(-hh, 0f), 90f, steps / 2, true)
                    }
                    for (i in 0 .. steps) {
                        sample()
                        rotate(360f / steps, 0f, 0f)
                    }
                }
            }
        }
    }

    @Serializable
    object Empty : MeshShapeData() {
        override val name: String get() = "Empty"

        override fun generate(builder: MeshBuilder) {
            // empty - nothing to generate
        }
    }

    companion object {
        val defaultBox = Box(Vec3Data(1.0, 1.0, 1.0))
        val defaultIcoSphere = IcoSphere(1.0, 2)
        val defaultUvSphere = UvSphere(1.0, 20)
        val defaultRect = Rect(Vec2Data(1.0, 1.0))
        val defaultCylinder = Cylinder(1.0, 1.0, 1.0, 16)
        val defaultCapsule = Capsule(1.0, 1.0, 16)
    }
}
