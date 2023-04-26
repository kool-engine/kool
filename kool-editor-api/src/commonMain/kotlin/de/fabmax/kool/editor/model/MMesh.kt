package de.fabmax.kool.editor.model

import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.MdColor
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MMesh(
    override val nodeProperties: MCommonNodeProperties,
    var shape: MMeshShape
) : MSceneNode<Mesh> {

    @Transient
    override var created: Mesh? = null
    @Transient
    override val childNodes: MutableMap<Long, MSceneNode<*>> = mutableMapOf()
    @Transient
    val shapeMutableState = mutableStateOf(shape).onChange { shape = it }

    override fun create(): Mesh {
        val mesh = ColorMesh()
        mesh.name = nodeProperties.name
        mesh.shader = KslPbrShader {
            color { constColor(MdColor.LIGHT_GREEN.toLinear()) }
        }
        nodeProperties.transform.toTransform(mesh.transform)
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
sealed class MMeshShape(val name: String) {

    abstract fun generate(mesh: Mesh)

    @Serializable
    data class Box(val size: MVec3) : MMeshShape("Box") {
        override fun generate(mesh: Mesh) {
            mesh.generate {
                cube(centered = true) {
                    size.set(this@Box.size.toVec3f())
                }
            }
        }
    }

    @Serializable
    data class IcoSphere(val radius: Double, val subDivisions: Int) : MMeshShape("Ico-Sphere") {
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
    data class UvSphere(val radius: Double, val steps: Int) : MMeshShape("UV-Sphere") {
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
    data class Rect(val size: MVec2) : MMeshShape("Rect") {
        override fun generate(mesh: Mesh) {
            TODO()
        }
    }

    @Serializable
    data class Cylinder(val topRadius: Double, val bottomRadius: Double, val length: Double, val steps: Int) : MMeshShape("Cylinder") {
        override fun generate(mesh: Mesh) {
            TODO()
        }
    }

    @Serializable
    object Empty : MMeshShape("Empty") {
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
