package de.fabmax.kool.editor.overlays

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class SceneObjectsOverlay : Node("Scene objects overlay") {

    var displayLighting: Lighting? = null

    private val directionInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS))
    private val spotInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS))
    private val pointInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS))

    private val directionalMesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "Directional lights").apply {
        isCastingShadow = false
        instances = directionInstances
        generate {
            uvSphere {
                radius = 0.15f
            }

            line3d(Vec3f.ZERO, Vec3f(1.5f, 0f, 0f), Vec3f.Z_AXIS, lineW)
            line3d(Vec3f.ZERO, Vec3f(1.5f, 0f, 0f), Vec3f.Y_AXIS, lineW)
            withTransform {
                rotate(-90f, Vec3f.Z_AXIS)
                translate(0f, 1.6f, 0f)
                cylinder {
                    bottomRadius = 0.15f
                    topRadius = 0f
                    height = 0.2f
                    topFill = false
                }
            }

            for (i in 0 until 63) {
                val a1 = i / 63f * 2f * PI.toFloat()
                val z1 = cos(a1)
                val y1 = sin(a1)
                val a2 = (i + 1) / 63f * 2f * PI.toFloat()
                val z2 = cos(a2)
                val y2 = sin(a2)

                val nrm = Vec3f(0f, y1, z1)
                line3d(Vec3f(0f, y1, z1), Vec3f(0f, y2, z2), nrm, lineW)
                line3d(Vec3f(0f, y1, z1), Vec3f(0f, y2, z2), Vec3f.X_AXIS, lineW)

                val nrm2 = nrm.rotate(90f, Vec3f.X_AXIS, MutableVec3f())
                if (i % 8 == 0) {
                    line3d(Vec3f(0f, y1, z1), Vec3f(0.7f, y1, z1), nrm, lineW)
                    line3d(Vec3f(0f, y1, z1), Vec3f(0.7f, y1, z1), nrm2, lineW)
                }
                if ((i + 4) % 8 == 0) {
                    val r = 0.5f
                    line3d(Vec3f(0f, y1 * r, z1 * r), Vec3f(0.7f, y1 * r, z1 * r), nrm, lineW)
                    line3d(Vec3f(0f, y1 * r, z1 * r), Vec3f(0.7f, y1 * r, z1 * r), nrm2, lineW)
                }
            }
        }

        shader = KslUnlitShader {
            vertices { isInstanced = true }
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color { instanceColor(Attribute.COLORS) }
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB
        }
    }

    private val spotMesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "Spot lights").apply {
        isCastingShadow = false
        instances = spotInstances
        generate {
            uvSphere {
                radius = 0.15f
            }

            line3d(Vec3f.ZERO, Vec3f(1.5f, 0f, 0f), Vec3f.Z_AXIS, lineW)
            line3d(Vec3f.ZERO, Vec3f(1.5f, 0f, 0f), Vec3f.Y_AXIS, lineW)
            withTransform {
                rotate(-90f, Vec3f.Z_AXIS)
                translate(0f, 1.6f, 0f)
                cylinder {
                    bottomRadius = 0.15f
                    topRadius = 0f
                    height = 0.2f
                    topFill = false
                }
            }

            for (i in 0 until 63) {
                val a1 = i / 63f * 2f * PI.toFloat()
                val z1 = cos(a1)
                val y1 = sin(a1)
                val a2 = (i + 1) / 63f * 2f * PI.toFloat()
                val z2 = cos(a2)
                val y2 = sin(a2)

                val nrm = Vec3f(0f, y1, z1)
                line3d(Vec3f(1f, y1, z1), Vec3f(1f, y2, z2), nrm, lineW)
                line3d(Vec3f(1f, y1, z1), Vec3f(1f, y2, z2), Vec3f.X_AXIS, lineW)

                if (i % 16 == 0){
                    line3d(Vec3f.ZERO, Vec3f(1f, y1, z1), nrm, lineW)
                    val nrm2 = nrm.rotate(90f, Vec3f.X_AXIS, MutableVec3f())
                    line3d(Vec3f.ZERO, Vec3f(1f, y1, z1), nrm2, lineW)
                }
            }
        }

        shader = KslUnlitShader {
            vertices { isInstanced = true }
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color { instanceColor(Attribute.COLORS) }
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB
        }
    }

    private val pointMesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "Point lights").apply {
        isCastingShadow = false
        instances = pointInstances
        generate {
            uvSphere {
                radius = 0.15f
            }

            val ico = MeshBuilder.IcoGenerator()
            ico.subdivide(1)

            ico.verts.forEachIndexed { i, pt ->
                val o = if (pt * Vec3f.Y_AXIS > 0.9f) Vec3f.X_AXIS else Vec3f.Y_AXIS
                val n1 = pt.cross(o, MutableVec3f())
                val n2 = n1.rotate(90f, pt, MutableVec3f())

                val l0 = if (i % 2 == 0) 0f else 0.45f
                val l1 = if (i % 2 == 0) 0.9f else 0.9f

                line3d(MutableVec3f(pt).scale(l0), MutableVec3f(pt).scale(l1), n1, 0.03f)
                line3d(MutableVec3f(pt).scale(l0), MutableVec3f(pt).scale(l1), n2, 0.03f)
            }
        }

        shader = KslUnlitShader {
            vertices { isInstanced = true }
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color { instanceColor(Attribute.COLORS) }
            colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB
        }
    }

    init {
        addNode(spotMesh)
        addNode(pointMesh)
        addNode(directionalMesh)

        // todo: addNode(cameraMesh)
        // todo: addNode(emptyNodeMesh)

        val lightMat = Mat4f()
        onUpdate {
            directionInstances.clear()
            spotInstances.clear()
            pointInstances.clear()

            displayLighting?.lights?.let { lights ->
                lights.filter { it.isVisible }.forEach { light ->
                    val instances: MeshInstanceList
                    val yzScale: Float
                    when (light) {
                        is Light.Directional -> {
                            yzScale = 1f
                            instances = directionInstances
                        }
                        is Light.Point -> {
                            yzScale = 1f
                            instances = pointInstances
                        }
                        is Light.Spot -> {
                            yzScale = tan(light.spotAngle.toRad() / 2f)
                            instances = spotInstances
                        }
                    }

                    lightMat.set(light.transform.matrix).scale(1f, yzScale, yzScale)
                    instances.addInstance {
                        put(lightMat.array)
                        put(light.color.array)
                    }
                }
            }
        }
    }

    companion object {
        const val lineW = 0.06f
    }
}