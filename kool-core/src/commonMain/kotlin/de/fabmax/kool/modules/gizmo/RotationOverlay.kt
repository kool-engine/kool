package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TriangulatedLineMesh
import de.fabmax.kool.scene.TrsTransformD
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.*

class RotationOverlay(val gizmo: GizmoNode) : Node(), GizmoListener {

    private val lines = TriangulatedLineMesh()
    private val mesh = ColorMesh()
    private val startTransform = TrsTransformD()
    private var rotPlane: PlaneD? = null
    private var planeBaseX: Vec3d = Vec3d.X_AXIS
    private var planeBaseY: Vec3d = Vec3d.Y_AXIS

    var lineWidth = 2f
    var lineWidthTicks = 1f
    var labelClearance = 75f

    val _labelPosition = MutableVec2f()
    val labelPosition: Vec2f
        get() = _labelPosition
    var labelValue = 0.0
        private set
    var isLabelValid = false
        private set

    init {
        addNode(mesh)
        addNode(lines)
        isVisible = false

        lines.shader = TriangulatedLineMesh.Shader().apply {
            pipelineConfig = pipelineConfig.copy(isWriteDepth = false, depthTest = DepthCompareOp.ALWAYS)
        }
        mesh.shader = KslUnlitShader {
            pipeline {
                isWriteDepth = false
                depthTest = DepthCompareOp.ALWAYS
                cullMethod = CullMethod.NO_CULLING
            }
            color { constColor(Color.WHITE.withAlpha(0.3f)) }
        }
    }

    override fun onManipulationStart(startTransform: TrsTransformD) {
        (gizmo.activeOp as? GizmoRotation)?.let { rotOp ->
            this.startTransform.set(startTransform)
            isVisible = true

            rotPlane = rotOp.rotationPlane?.let { p ->
                val globalPlane = PlaneD(p.p, p.n)
                startTransform.transform(globalPlane.p)
                startTransform.transform(globalPlane.n, 0.0)

                val (bx, by) = globalPlane.planeBase()
                planeBaseX = bx
                planeBaseY = by
                globalPlane
            }
        }
    }

    override fun update(updateEvent: RenderPass.UpdateEvent) {
        super.update(updateEvent)
        if (!isVisible) {
            return
        }

        lines.clear()

        val cam = updateEvent.camera
        val vp = updateEvent.viewport
        val plane = rotPlane ?: return
        val rotationOp = gizmo.activeOp as? GizmoRotation ?: return
        val start = startTransform.transform(MutableVec3d(rotationOp.startProjectedPos))
        var delta = gizmo.overwriteManipulatorValue.value?.deg ?: rotationOp.deltaRotation

        delta = (delta.rad % (PI * 2)).rad
        if (delta.rad > PI) {
            delta -= (PI * 2).rad
        } else if (delta.rad < -PI) {
            delta += (PI * 2).rad
        }

        val color = (rotationOp as? AxisRotation)?.axis?.let {
            when (it) {
                GizmoHandle.Axis.POS_X -> MdColor.RED
                GizmoHandle.Axis.NEG_X -> MdColor.RED
                GizmoHandle.Axis.POS_Y -> MdColor.LIGHT_GREEN
                GizmoHandle.Axis.NEG_Y -> MdColor.LIGHT_GREEN
                GizmoHandle.Axis.POS_Z -> MdColor.BLUE
                GizmoHandle.Axis.NEG_Z -> MdColor.BLUE
            }
        } ?: Color.WHITE

        val r = gizmo.handleTransform.scale.x * 0.8
        val b0 = planeBaseX
        val b1 = planeBaseY

        val aStart = plane.projAngle(start, b0, b1)
        val aDrag = aStart + delta

        mesh.generate {
            val steps = (abs(delta.deg) / 5).roundToInt() + 1
            val step = (delta.deg / steps).deg

            vertex(plane.p.toVec3f(), Vec3f.ZERO)
            for (i in 0..steps) {
                val p = plane.unprojAngle(aStart + step * i, r).toVec3f()
                vertex(p, Vec3f.ZERO)
                if (i > 0) {
                    addTriIndices(0, i, i+1)
                }
            }
        }

        for (i in 0 until 72) {
            val a0 = i / 72.0 * 2.0 * PI
            val a1 = (i + 1) / 72.0 * 2.0 * PI
            val p0 = plane.unprojAngle(a0.rad, r).toVec3f()
            val p1 = plane.unprojAngle(a1.rad, r).toVec3f()
            lines.addLine(color, lineWidth, p0, p1)
        }

        val tickColor = Color.WHITE
        for (i in 0 until 72) {
            val a = aStart.rad + i / 72.0 * 2.0 * PI
            val l = if (i % 2 == 0) 1.2 else 1.15
            val p0 = plane.unprojAngle(a.rad, r * 1.05).toVec3f()
            val p1 = plane.unprojAngle(a.rad, r * l).toVec3f()
            lines.addLine(tickColor, lineWidthTicks, p0, p1)
        }

        val pStart = plane.unprojAngle(aStart, r)
        val pDrag = plane.unprojAngle(aDrag, r)
        lines.addLine(color, lineWidth, plane.p.toVec3f(), pStart.toVec3f())
        lines.addLine(color, lineWidth, plane.p.toVec3f(), pDrag.toVec3f())

        val midAngle = aStart + delta * 0.5
        val midPos = plane.unprojAngle(midAngle, r)
        val midScreen = MutableVec3d().also { cam.projectScreen(midPos, vp, it) }.xy.toVec2f()
        val centerScreen = MutableVec3d().also { cam.projectScreen(plane.p, vp, it) }.xy.toVec2f()

        val lblPos = midScreen + (midScreen - centerScreen).normed() * labelClearance

        _labelPosition.set(lblPos.x - vp.x, lblPos.y - vp.y)
        labelValue = gizmo.overwriteManipulatorValue.value ?: delta.deg
        isLabelValid = true
    }

    private fun PlaneD.projAngle(vec: Vec3d, b0: Vec3d, b1: Vec3d): AngleD {
        val x = (vec - p) dot b0
        val y = (vec - p) dot b1
        return atan2(y, x).rad
    }

    private fun PlaneD.unprojAngle(angle: AngleD, r: Double): Vec3d {
        return p + planeBaseX * cos(angle.rad) * r + planeBaseY * sin(angle.rad) * r
    }

    private fun PlaneD.planeBase(): Pair<Vec3d, Vec3d> {
        val dotX = abs(n dot Vec3d.X_AXIS)
        val dotY = abs(n dot Vec3d.Y_AXIS)
        val dotZ = abs(n dot Vec3d.Z_AXIS)

        val b0 = MutableVec3d()
        val b1 = MutableVec3d()
        when {
            dotX <= dotY && dotX <= dotZ -> {
                b0.set(Vec3d.X_AXIS)
                n.cross(b0, b1)
                b1.cross(n, b0)
            }
            dotY <= dotX && dotY <= dotZ -> {
                b0.set(Vec3d.Y_AXIS)
                n.cross(b0, b1)
                b1.cross(n, b0)
            }
            else -> {
                b0.set(Vec3d.Z_AXIS)
                n.cross(b0, b1)
                b1.cross(n, b0)
            }
        }
        b0.norm()
        b1.norm()
        return b0 to b1
    }

    override fun onManipulationFinished(startTransform: TrsTransformD, endTransform: TrsTransformD) {
        lines.clear()
        isVisible = false
        isLabelValid = false
    }

    override fun onManipulationCanceled(startTransform: TrsTransformD) {
        lines.clear()
        isVisible = false
        isLabelValid = false
    }
}