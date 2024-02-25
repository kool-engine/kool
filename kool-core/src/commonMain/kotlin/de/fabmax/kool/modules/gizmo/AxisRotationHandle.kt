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
        setColors(colorIdle, coveredColorIdle)

        // hasChanged flag is usually cleared after mesh is drawn the first time, but hitMesh is never drawn
        // -> clear flag manually to avoid hitTest kd-tree being regenerated every frame
        hitMesh.geometry.hasChanged = false

        addNode(coveredMesh)
        addNode(mesh)
        addNode(hitMesh)
    }

    private fun setColors(mainColor: Color, coveredColor: Color) {
        (mesh.shader as KslUnlitShader).color = mainColor
        (coveredMesh.shader as KslUnlitShader).color = coveredColor
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

    override fun onHover(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode) {
        setColors(color, coveredColor)
    }

    override fun onHoverExit(gizmo: GizmoNode) {
        setColors(colorIdle, coveredColorIdle)
    }
}