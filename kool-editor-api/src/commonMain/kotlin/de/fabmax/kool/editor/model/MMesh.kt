package de.fabmax.kool.editor.model

import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.MdColor
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MMesh(
    override val nodeProperties: MCommonNodeProperties,
    val meshType: MMeshType
) : MSceneNode<Mesh> {

    @Transient
    override var created: Mesh? = null

    override fun create(): Mesh {
        val mesh = ColorMesh()
        mesh.name = nodeProperties.name
        mesh.shader = KslPbrShader {
            color { constColor(MdColor.LIGHT_GREEN.toLinear()) }
        }
        meshType.generate(mesh)
        nodeProperties.transform.toTransform(mesh.transform)
        created = mesh
        return mesh
    }
}

@Serializable
sealed class MMeshType(val name: String) {

    abstract fun generate(mesh: Mesh)

    @Serializable
    data class Box(val size: MVec3) : MMeshType("Box") {
        override fun generate(mesh: Mesh) {
            mesh.generate {
                cube(centered = true) {
                    size.set(this@Box.size.toVec3f())
                }
            }
        }
    }

    @Serializable
    data class IcoSphere(val radius: Float, val subDivisions: Int) : MMeshType("Ico-Sphere") {
        override fun generate(mesh: Mesh) {
            mesh.generate {
                icoSphere {
                    radius = this@IcoSphere.radius
                    steps = subDivisions
                }
            }
        }
    }

    @Serializable
    data class UvSphere(val radius: Float, val steps: Int) : MMeshType("UV-Sphere") {
        override fun generate(mesh: Mesh) {
            mesh.generate {
                uvSphere {
                    radius = this@UvSphere.radius
                    steps = this@UvSphere.steps
                }
            }
        }
    }

    @Serializable
    data class Rect(val size: MVec2) : MMeshType("Rect") {
        override fun generate(mesh: Mesh) {
            TODO()
        }
    }

    @Serializable
    data class Cylinder(val topRadius: Float, val bottomRadius: Float, val length: Float, val steps: Int) : MMeshType("Cylinder") {
        override fun generate(mesh: Mesh) {
            TODO()
        }
    }

    @Serializable
    object Empty : MMeshType("Empty") {
        override fun generate(mesh: Mesh) {
            // it's empty, nothing to generate!
        }
    }
}
