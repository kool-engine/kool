package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshRayTest
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TrsTransformD
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.Color
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max

class PlaneHandle(
    val color: Color,
    axis: GizmoHandle.Axis,
    override val gizmoOperation: GizmoOperation = PlaneTranslation(axis.axis),
    val coveredColor: Color = color.withAlpha(0.7f),
    val colorIdle: Color = color.mulRgb(0.8f),
    val coveredColorIdle: Color = colorIdle.withAlpha(0.7f),
    size: Float = 0.25f,
    innerDistance: Float = 0.25f,
    name: String = "plane-handle"
) : Node(name), GizmoHandle {

    override val drawNode: Node
        get() = this
    override var isHidden: Boolean = false
        set(value) {
            field = value
            drawNode.isVisible = !value
        }

    private val mesh: Mesh
    private val coveredMesh: Mesh
    private val lineMesh: Mesh

    private var isHovered = false
    private var alphaFactor = 1f

    init {
        transform = TrsTransformD().apply {
            rotation.set(axis.orientation)
            markDirty()
        }

        mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "${name}-mesh")
        mesh.setup(size, innerDistance, DepthCompareOp.LESS)
        mesh.rayTest = MeshRayTest.geometryTest(mesh)

        coveredMesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, name = "${name}-coveredMesh")
        coveredMesh.setup(size, innerDistance, DepthCompareOp.ALWAYS)
        coveredMesh.isPickable = false

        lineMesh = Mesh(IndexedVertexList(Attribute.POSITIONS, primitiveType = PrimitiveType.LINES), name = "${name}-lineMesh")
        lineMesh.makeOutline(size, innerDistance, mesh)

        addNode(coveredMesh)
        addNode(mesh)
        addNode(lineMesh)

        val camDelta = MutableVec3d()
        val alphaThreshHigh = cos(75f.deg.rad)
        val alphaThreshLow = cos(85f.deg.rad)

        onUpdate {
            parent?.invModelMatD?.transform(camDelta.set(it.camera.dataD.globalPos), 1.0)
            val cosAngle = abs(camDelta.norm() dot axis.axis).toFloat()
            alphaFactor = if (cosAngle < alphaThreshHigh) {
                smoothStep(alphaThreshLow, alphaThreshHigh, cosAngle)
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

        val planeAlpha = mainColor.a * 0.3f
        (mesh.shader as KslUnlitShader).color = mainColor.withAlpha(planeAlpha)
        (coveredMesh.shader as KslUnlitShader).color = coveredColor
        (lineMesh.shader as KslUnlitShader).color = mainColor
    }

    override fun onHover(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode) {
        isHovered = true
    }

    override fun onHoverExit(gizmo: GizmoNode) {
        isHovered = false
    }

    private fun Mesh.setup(
        planeSize: Float,
        innerDistance: Float,
        depthCompareOp: DepthCompareOp
    ) {
        generate {
            rotate(90f.deg, Vec3f.Y_AXIS)
            rect {
                cornerRadius = planeSize * 0.1f
                size.set(planeSize, planeSize)
                origin.set(innerDistance + planeSize * 0.5f, innerDistance + planeSize * 0.5f, 0f)
            }
        }
        shader = KslUnlitShader {
            pipeline {
                cullMethod = CullMethod.NO_CULLING
                depthTest = depthCompareOp
                if (depthCompareOp == DepthCompareOp.ALWAYS) {
                    isWriteDepth = false
                }
            }
            color { uniformColor() }
        }
    }

    private fun Mesh.makeOutline(
        planeSize: Float,
        innerDistance: Float,
        planeMesh: Mesh
    ) {
        val center = innerDistance + planeSize * 0.5f

        (0 until planeMesh.geometry.numVertices)
            .map { Vec3f(planeMesh.geometry[it]) }
            .sortedBy { max(abs(abs(it.y) - center), abs(abs(it.z) - center)) }
            .drop(8)
            .sortedBy { atan2(abs(it.y) - center, abs(it.z) - center) }
            .forEach { geometry.addVertex(it) }
        for (i in 0 until geometry.numVertices) {
            geometry.addIndices(i, (i + 1) % geometry.numVertices)
        }
        lineMesh.shader = KslUnlitShader {
            pipeline {
                depthTest = DepthCompareOp.ALWAYS
                isWriteDepth = false
            }
            color { uniformColor(color) }
        }
    }
}