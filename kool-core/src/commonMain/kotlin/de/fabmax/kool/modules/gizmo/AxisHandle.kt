package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.RayD
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TrsTransformD
import de.fabmax.kool.util.Color

class AxisHandle(
    val color: Color,
    val axis: GizmoHandle.Axis,
    override val gizmoOperation: GizmoOperation = AxisTranslation(axis),
    handleShape: HandleType = HandleType.ARROW,
    val coveredColor: Color = color.withAlpha(0.5f),
    val colorIdle: Color = color.mulRgb(0.8f),
    val coveredColorIdle: Color = colorIdle.withAlpha(0.3f),
    length: Float = 1f,
    innerDistance: Float = 0.1f,
    name: String = "axis-handle"
) : Node(name), GizmoHandle {

    override val handleTransform = TrsTransformD()

    override val drawNode: Node
        get() = this

    private val hitMesh: Mesh
    private val mesh: Mesh
    private val coveredMesh: Mesh

    init {
        transform = handleTransform

        mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "${name}-mesh")
        mesh.isPickable = false
        coveredMesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "${name}-coveredMesh")
        coveredMesh.isPickable = false
        hitMesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "${name}-hitMesh")
        hitMesh.rayTest = MeshRayTest.geometryTest(mesh)

        mesh.setupGeometry(handleShape, innerDistance, length, 0.015f, 0.07f)
        mesh.setupShader(DepthCompareOp.LESS)
        coveredMesh.setupGeometry(handleShape, innerDistance, length, 0.015f, 0.07f)
        coveredMesh.setupShader(DepthCompareOp.ALWAYS)
        hitMesh.setupGeometry(HandleType.SPHERE, innerDistance, length, 0.07f, 0.15f)
        setColors(colorIdle, coveredColorIdle)

        addNode(coveredMesh)
        addNode(mesh)
        addNode(hitMesh)

        setAxis(axis)
    }

    private fun setColors(mainColor: Color, coveredColor: Color) {
        (mesh.shader as KslUnlitShader).color = mainColor
        (coveredMesh.shader as KslUnlitShader).color = coveredColor
    }

    override fun onHover(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode) {
        setColors(color, coveredColor)
    }

    override fun onHoverExit(gizmo: GizmoNode) {
        setColors(colorIdle, coveredColorIdle)
    }

    private fun Mesh.setupGeometry(
        handleType: HandleType,
        innerDistance: Float,
        length: Float,
        lineRadius: Float,
        tipRadius: Float,
    ) {
        isCastingShadow = false
        generate {
            rotate(90f.deg, Vec3f.NEG_Z_AXIS)
            translate(0f, innerDistance, 0f)
            translate(0f, length * 0.5f, 0f)
            cylinder {
                height = length
                radius = lineRadius
                steps = 8
            }

            translate(0f, length * 0.5f + 0.0475f, 0f)
            when (handleType) {
                HandleType.ARROW -> cylinder {
                    height = 0.14f
                    bottomRadius = tipRadius
                    topRadius = 0f
                    topFill = false
                    steps = 16
                }
                HandleType.SPHERE -> icoSphere {
                    steps = 2
                    radius = tipRadius
                }
            }
        }
    }

    private fun Mesh.setupShader(depthCompareOp: DepthCompareOp) {
        shader = KslUnlitShader {
            pipeline {
                depthTest = depthCompareOp
                if (depthCompareOp == DepthCompareOp.ALWAYS) {
                    isWriteDepth = false
                }
            }
            color { uniformColor() }
        }
    }

    enum class HandleType {
        ARROW,
        SPHERE
    }
}