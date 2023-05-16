package de.fabmax.kool.editor.model

import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.MdColor
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class MMesh(
    override val nodeId: Long
) : MSceneNode(), Creatable<Mesh> {

    var shape: MMeshShape = MMeshShape.Box(MVec3(1.0, 1.0, 1.0))

    @Transient
    val shapeMutableState = mutableStateOf(shape).onChange { shape = it }

    override val creatable: Creatable<out Node>
        get() = this

    @Transient
    private var created: Mesh? = null

    override fun getOrNull() = created

    override suspend fun getOrCreate() = created ?: create()

    private fun create(): Mesh {
        val mesh = ColorMesh(name)
        mesh.shader = KslPbrShader {
            color { constColor(MdColor.LIGHT_GREEN.toLinear()) }
        }
        transform.toTransform(mesh.transform)
        created = mesh
        generateMeshType()
        return mesh
    }

    fun generateMeshType() {
        created?.let {
            shape.generate(it)
        }
    }
}

@Serializable
sealed class MMeshShape {

    abstract val name: String

    abstract fun generate(mesh: Mesh)

    @Serializable
    data class Box(val size: MVec3) : MMeshShape() {
        override val name: String get() = "Box"

        override fun generate(mesh: Mesh) {
            mesh.generate {
                cube(centered = true) {
                    size.set(this@Box.size.toVec3f())
                }
            }
        }
    }

    @Serializable
    data class IcoSphere(val radius: Double, val subDivisions: Int) : MMeshShape() {
        override val name: String get() = "Ico-Sphere"

        override fun generate(mesh: Mesh) {
            mesh.generate {
                icoSphere {
                    radius = this@IcoSphere.radius.toFloat()
                    steps = subDivisions
                }
            }
        }
    }

    @Serializable
    data class UvSphere(val radius: Double, val steps: Int) : MMeshShape() {
        override val name: String get() = "UV-Sphere"

        override fun generate(mesh: Mesh) {
            mesh.generate {
                uvSphere {
                    radius = this@UvSphere.radius.toFloat()
                    steps = this@UvSphere.steps
                }
            }
        }
    }

    @Serializable
    data class Rect(val size: MVec2) : MMeshShape() {
        override val name: String get() = "Rect"

        override fun generate(mesh: Mesh) {
            TODO()
        }
    }

    @Serializable
    data class Cylinder(val topRadius: Double, val bottomRadius: Double, val length: Double, val steps: Int) : MMeshShape() {
        override val name: String get() = "Cylinder"

        override fun generate(mesh: Mesh) {
            TODO()
        }
    }

    @Serializable
    object Empty : MMeshShape() {
        override val name: String get() = "Empty"

        override fun generate(mesh: Mesh) {
            mesh.geometry.clear()
        }
    }

    companion object {
        val defaultBox = Box(MVec3(1.0, 1.0, 1.0))
        val defaultIcoSphere = IcoSphere(1.0, 2)
        val defaultUvSphere = UvSphere(1.0, 20)
        val defaultRect = Rect(MVec2(1.0, 1.0))
        val defaultCylinder = Cylinder(1.0, 1.0, 1.0, 16)
    }
}
