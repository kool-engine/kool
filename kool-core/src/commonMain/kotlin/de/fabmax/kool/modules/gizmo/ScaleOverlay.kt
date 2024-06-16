package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TriangulatedLineMesh
import de.fabmax.kool.scene.TriangulatedPointMesh
import de.fabmax.kool.scene.TrsTransformD
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class ScaleOverlay(val gizmo: GizmoNode) : Node(), GizmoListener {

    private val points = TriangulatedPointMesh()
    private val lines = TriangulatedLineMesh()
    private val weakLines = TriangulatedLineMesh()

    private val startPos = MutableVec3f()
    private val currentScale = MutableVec3d()
    private var labelPosMidW = 0f

    var pointSize = 10f
    var lineWidth = 2f
    var labelClearance = 75f

    val _labelPosition = MutableVec2f()
    val labelPosition: Vec2f
        get() = _labelPosition
    var labelValue = 0.0
        private set
    var isLabelValid = false
        private set

    init {
        addNode(lines)
        addNode(weakLines)
        addNode(points)
        isVisible = false

        weakLines.shader = TriangulatedLineMesh.Shader().apply {
            pipelineConfig = pipelineConfig.copy(isWriteDepth = false, depthTest = DepthCompareOp.ALWAYS)
        }
        points.shader = TriangulatedPointMesh.Shader().apply {
            pipelineConfig = pipelineConfig.copy(isWriteDepth = false, depthTest = DepthCompareOp.ALWAYS)
        }
    }

    override fun onManipulationStart(startTransform: TrsTransformD) {
        if (gizmo.activeOp is GizmoScale) {
            isVisible = true
            startPos.set(startTransform.translation)
        }
    }

    override fun onGizmoUpdate(transform: TrsTransformD) {
        currentScale.set(transform.scale)
    }

    override fun update(updateEvent: RenderPass.UpdateEvent) {
        super.update(updateEvent)
        if (!isVisible) {
            return
        }

        val scaleOp = gizmo.activeOp as? GizmoScale ?: return
        val baseOp = scaleOp as GizmoOperationBase
        if (!baseOp.isDrag) {
            return
        }

        val cam = updateEvent.camera
        val vp = updateEvent.viewport

        points.clear()
        lines.clear()
        weakLines.clear()

        val cursorScreen = Vec3d(baseOp.dragPointerPos.x, baseOp.dragPointerPos.y, 0.999)
        val cursorWorldPos = MutableVec3d()
        updateEvent.camera.unProjectScreen(cursorScreen, vp, cursorWorldPos)

        var color = Color.WHITE
        val endPos: Vec3f = baseOp.projectedPointerPos.toVec3f()
        if (scaleOp is AxisScale) {
            color = when (scaleOp.axis) {
                GizmoHandle.Axis.POS_X -> MdColor.RED.withAlpha(0.5f)
                GizmoHandle.Axis.NEG_X -> MdColor.RED.withAlpha(0.5f)
                GizmoHandle.Axis.POS_Y -> MdColor.LIGHT_GREEN.withAlpha(0.5f)
                GizmoHandle.Axis.NEG_Y -> MdColor.LIGHT_GREEN.withAlpha(0.5f)
                GizmoHandle.Axis.POS_Z -> MdColor.BLUE.withAlpha(0.5f)
                GizmoHandle.Axis.NEG_Z -> MdColor.BLUE.withAlpha(0.5f)
            }
        }

        if (startPos != endPos) {
            points.addPoint(startPos, pointSize, color)
            points.addPoint(endPos, pointSize, color)

            val extent = startPos.distance(cam.globalPos)
            lines.buildAxisLines(endPos, color, lineWidth, extent)
            weakLines.buildAxisLines(endPos, color.withAlpha(color.a * 0.35f), lineWidth, extent)

            val projStart = MutableVec3f()
            val projEnd = MutableVec3f()
            cam.projectScreen(startPos, vp, projStart)
            cam.projectScreen(endPos, vp, projEnd)
            val vpPos = Vec2f(vp.x.toFloat(), vp.y.toFloat())
            val startScreen = projStart.xy - vpPos
            val endScreen = projEnd.xy - vpPos
            val l = startScreen.distance(endScreen)

            val relPos = 0.3f + smoothStep(labelClearance, labelClearance * 10f, l) * 0.4f

            val mid = startScreen + (endScreen - startScreen) * relPos
            val out = startScreen + (startScreen - endScreen).normed() * labelClearance
            val midW = if (l * 0.5f < labelClearance) 0f else 1f

            labelPosMidW = labelPosMidW.expDecay(midW, 16f)
            _labelPosition.set(mid * labelPosMidW + out * (1f - labelPosMidW))
            labelValue = gizmo.overwriteManipulatorValue.value ?: scaleOp.scaleValue
            isLabelValid = true

        } else {
            val projStart = MutableVec3f()
            cam.projectScreen(startPos, vp, projStart)
            labelPosMidW = 0f
            isLabelValid = false
        }
    }

    private fun TriangulatedLineMesh.buildAxisLines(endPos: Vec3f, color: Color, lineWidth: Float, extent: Float) {
        addLine(startPos, endPos, color, lineWidth)

        if (extent > 0f) {
            val ex = (endPos-startPos).normed() * extent * 0.5f
            addLine(endPos, endPos + ex, color, lineWidth)
            addLine(endPos + ex, color, lineWidth, endPos + ex * 2f, color.withAlpha(0f), lineWidth)

            addLine(startPos, startPos - ex, color, lineWidth)
            addLine(startPos - ex, color, lineWidth, startPos - ex * 2f, color.withAlpha(0f), lineWidth)
        }
    }

    override fun onManipulationFinished(startTransform: TrsTransformD, endTransform: TrsTransformD) {
        points.clear()
        lines.clear()
        weakLines.clear()
        isVisible = false
        isLabelValid = false
    }

    override fun onManipulationCanceled(startTransform: TrsTransformD) {
        points.clear()
        lines.clear()
        weakLines.clear()
        isVisible = false
        isLabelValid = false
    }

}