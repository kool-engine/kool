package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TrsTransformD
import de.fabmax.kool.util.Color
import kotlin.math.abs
import kotlin.math.cos

class AxisHandle(
    val color: Color,
    val axis: GizmoHandle.Axis,
    override val gizmoOperation: GizmoOperation = AxisTranslation(axis),
    handleShape: HandleType = HandleType.ARROW,
    val coveredColor: Color = color.withAlpha(0.7f),
    val colorIdle: Color = color.mulRgb(0.8f),
    val coveredColorIdle: Color = colorIdle.withAlpha(0.7f),
    length: Float = 0.6f,
    innerDistance: Float = 0.2f,
    name: String = "axis-handle"
) : Node(name), GizmoHandle {

    override val drawNode: Node
        get() = this
    override var isHidden: Boolean = false
        set(value) {
            field = value
            drawNode.isVisible = !value
        }

    private val hitMesh: Mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "${name}-hitMesh")
    private val mesh: Mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "${name}-mesh")
    private val coveredMesh: Mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "${name}-coveredMesh")

    private var isHovered = false
    private var alphaFactor = 1f

    init {
        transform = TrsTransformD().apply {
            rotation.set(axis.orientation)
            markDirty()
        }

        mesh.isPickable = false
        coveredMesh.isPickable = false
        hitMesh.rayTest = MeshRayTest.geometryTest(hitMesh)

        mesh.setupGeometry(handleShape, innerDistance, length, 0.015f, 0.07f)
        mesh.setupShader(DepthCompareOp.LESS)
        coveredMesh.setupGeometry(handleShape, innerDistance, length, 0.015f, 0.07f)
        coveredMesh.setupShader(DepthCompareOp.ALWAYS)
        hitMesh.setupGeometry(HandleType.SPHERE, innerDistance, length, 0.07f, 0.15f)

        // hasChanged flag is usually cleared after mesh is drawn the first time, but hitMesh is never drawn
        // -> clear flag manually to avoid hitTest kd-tree being regenerated every frame
        hitMesh.geometry.hasChanged = false

        addNode(coveredMesh)
        addNode(mesh)
        addNode(hitMesh)

        val camDelta = MutableVec3d()
        val alphaThreshHigh = cos(5f.deg.rad)
        val alphaThreshLow = cos(15f.deg.rad)

        onUpdate {
            parent?.invModelMatD?.transform(camDelta.set(it.camera.dataD.globalPos), 1.0)
            val cosAngle = abs(camDelta.norm() dot axis.axis).toFloat()
            alphaFactor = if (cosAngle > alphaThreshLow) {
                1f - smoothStep(alphaThreshLow, alphaThreshHigh, cosAngle)
            } else {
                1f
            }

            isVisible = !isHidden && alphaFactor > 0.01f
            updateColors()
        }
    }

    private fun updateColors() {
        var mainColor: Color = if (isHovered) color else colorIdle
        var coveredColor: Color = if (isHovered) coveredColor else coveredColorIdle

        if (alphaFactor > 0f && isHovered) {
            alphaFactor = 1f
        }
        if (alphaFactor != 1f) {
            mainColor = mainColor.withAlpha(mainColor.a * alphaFactor)
            coveredColor = coveredColor.withAlpha(coveredColor.a * alphaFactor)
        }

        (mesh.shader as KslUnlitShader).color = mainColor
        (coveredMesh.shader as KslUnlitShader).color = coveredColor
    }

    override fun onHover(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode) {
        isHovered = true
    }

    override fun onHoverExit(gizmo: GizmoNode) {
        isHovered = false
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