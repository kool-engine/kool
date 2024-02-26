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

class AxisRotationHandle(
    val color: Color,
    val axis: GizmoHandle.Axis,
    override val gizmoOperation: GizmoOperation = AxisRotation(axis),
    val coveredColor: Color = color.withAlpha(0.5f),
    val colorIdle: Color = color.mulRgb(0.8f),
    val coveredColorIdle: Color = colorIdle.withAlpha(0.3f),
    radius: Float = 0.8f,
    name: String = "axis-rotation-handle"
) : Node(name), GizmoHandle {

    override val drawNode: Node
        get() = this

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

        mesh.setupGeometry(radius, 0.01f)
        mesh.setupShader(DepthCompareOp.LESS)
        coveredMesh.setupGeometry(radius, 0.01f)
        coveredMesh.setupShader(DepthCompareOp.ALWAYS)
        hitMesh.setupGeometry(radius, 0.05f)

        // hasChanged flag is usually cleared after mesh is drawn the first time, but hitMesh is never drawn
        // -> clear flag manually to avoid hitTest kd-tree being regenerated every frame
        hitMesh.geometry.hasChanged = false

        addNode(coveredMesh)
        addNode(mesh)
        addNode(hitMesh)

        val camDelta = MutableVec3d()
        val alphaThreshHigh = cos(75f.deg.rad)
        val alphaThreshLow = cos(85f.deg.rad)

        onUpdate {
            modelMatD.transform(camDelta.set(Vec3d.ZERO), 1.0)
            camDelta.subtract(it.camera.dataD.globalPos)
            val cosAngle = abs(camDelta.norm() dot axis.axis).toFloat()
            alphaFactor = if (cosAngle < alphaThreshHigh) {
                smoothStep(alphaThreshLow, alphaThreshHigh, cosAngle)
            } else {
                1f
            }

            isVisible = alphaFactor > 0.01f
            hitMesh.isPickable = isVisible
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
        orbitRadius: Float,
        geomRadius: Float,
    ) {
        isCastingShadow = false
        generate {
            rotate(90f.deg, Vec3f.Z_AXIS)
            profile {
                circleShape(geomRadius, 6)

                val n = 60
                for (i in 0..n) {
                    withTransform {
                        rotate((360f * i / n).deg, Vec3f.NEG_Y_AXIS)
                        translate(orbitRadius, 0f, 0f)
                        sample()
                    }
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
}