package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import kotlin.math.cos
import kotlin.math.sin

class CenterCircleHandle(
    val color: Color,
    override val gizmoOperation: GizmoOperation = CamPlaneTranslation(),
    val drawMode: CircleMode = CircleMode.LINE,
    val hitTestMode: CircleMode = CircleMode.SOLID,
    val coveredColor: Color = color.withAlpha(0.7f),
    val colorIdle: Color = color.mulRgb(0.8f),
    val coveredColorIdle: Color = colorIdle.withAlpha(0.7f),
    radius: Float = 0.2f,
    innerRadius: Float = 0f,
    name: String = "center-handle"
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

    private val handleTransform = TrsTransformD()
    private var parentCam: Camera? = null
    private val camUpdateListener: (RenderPass.UpdateEvent) -> Unit = { ev ->
        val cam = ev.camera
        parent?.let { parent ->
            val parentZ = parent.invModelMatD.transform(cam.dataD.globalLookDir * -1.0, 0.0, MutableVec3d())
            val parentY = parent.invModelMatD.transform(cam.dataD.globalUp, 0.0, MutableVec3d())
            val parentX = parent.invModelMatD.transform(cam.dataD.globalRight, 0.0, MutableVec3d())

            val rot = MutableMat3d(parentX, parentY, parentZ)
            rot.getRotation(handleTransform.rotation)
            handleTransform.markDirty()

            // update cached model matrices of handle and child meshes
            updateModelMatRecursive()
        }
    }

    init {
        transform = handleTransform

        mesh.setupGeometry(radius, innerRadius, drawMode)
        mesh.setupShader(DepthCompareOp.LESS)
        coveredMesh.setupGeometry(radius, innerRadius, drawMode)
        coveredMesh.setupShader(DepthCompareOp.ALWAYS)
        hitMesh.setupGeometry(radius, innerRadius, hitTestMode)
        setColors(colorIdle, coveredColorIdle)

        mesh.isPickable = false
        coveredMesh.isPickable = false
        hitMesh.rayTest = LowPriorityMeshTest(hitMesh)

        // hasChanged flag is usually cleared after mesh is drawn the first time, but hitMesh is never drawn
        // -> clear flag manually to avoid hitTest kd-tree being regenerated every frame
        hitMesh.geometry.hasChanged = false

        addNode(coveredMesh)
        addNode(mesh)
        addNode(hitMesh)

        onUpdate { ev ->
            if (parentCam != ev.camera) {
                parentCam?.let { it.onCameraUpdated -= camUpdateListener }
                parentCam = ev.camera
                ev.camera.onCameraUpdated += camUpdateListener
            }
        }
    }

    override fun release() {
        super.release()
        parentCam?.let { it.onCameraUpdated -= camUpdateListener }
    }

    private fun setColors(mainColor: Color, coveredColor: Color) {
        (mesh.shader as KslUnlitShader).color = mainColor
        (coveredMesh.shader as KslUnlitShader).color = coveredColor
    }

    private fun Mesh.setupGeometry(radius: Float, innerRadius: Float, mode: CircleMode) {
        isCastingShadow = false
        generate {
            when (mode) {
                CircleMode.SOLID -> ring(innerRadius, radius)
                CircleMode.LINE -> {
                    ring(radius - lineWidth, radius + lineWidth)
                    if (innerRadius > 0f) {
                        ring(innerRadius - lineWidth, innerRadius + lineWidth)
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

    private fun MeshBuilder.ring(inner: Float, outer: Float) {
        val n = 60
        for (i in 0 .. n) {
            val ang = i.toFloat() / n * 2f * PI_F
            val i1 = geometry.addVertex(Vec3f(cos(ang) * inner, sin(ang) * inner, 0f))
            val i2 = geometry.addVertex(Vec3f(cos(ang) * outer, sin(ang) * outer, 0f))
            if (i < n) {
                val i3 = i1 + 2
                val i4 = i2 + 2
                addTriIndices(i1, i2, i3)
                addTriIndices(i3, i2, i4)
            }
        }
    }

    override fun onHover(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode) {
        setColors(color, coveredColor)
    }

    override fun onHoverExit(gizmo: GizmoNode) {
        setColors(colorIdle, coveredColorIdle)
    }

    private class LowPriorityMeshTest(mesh: Mesh) : MeshRayTest {
        private val geometryTest = MeshRayTest.geometryTest(mesh)

        override fun rayTest(test: RayTest, localRay: RayF): Boolean {
            if (!test.isHit) {
                return geometryTest.rayTest(test, localRay)
            }
            return false
        }
    }

    enum class CircleMode {
        SOLID,
        LINE,
    }

    companion object {
        private const val lineWidth = 0.01f
    }
}