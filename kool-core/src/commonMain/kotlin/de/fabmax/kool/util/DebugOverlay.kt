package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.Usage
import de.fabmax.kool.scene.ui.Font
import de.fabmax.kool.scene.ui.FontProps
import de.fabmax.kool.toString
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @author fabmax
 */

fun debugOverlay(position: DebugOverlay.Position = DebugOverlay.Position.UPPER_RIGHT): Scene {
    return DebugOverlay(position).ui
}

class DebugOverlay(position: Position = Position.UPPER_RIGHT) {

    val ui: Scene

    private val fpsText = mutableStateOf("")
    private val sysInfos = mutableStateListOf<String>()
    private val viewportText = mutableStateOf("")
    private val uptimeText = mutableStateOf("")
    private val numTexText = mutableStateOf("")
    private val numBufText = mutableStateOf("")
    private val numCmdsText = mutableStateOf("")
    private val numFacesText = mutableStateOf("")

    private var lastUpSecs = -1

    private val deltaTGraph = DeltaTGraph()

    private val isExpanded = mutableStateOf(false)

    init {
        val fpsFont = FontProps(Font.SYSTEM_FONT, 20f)

        ui = Ui2Scene("debug-overlay") {
            onUpdate += {
                fpsText.set("${it.ctx.fps.toString(1)} fps")
                if (isExpanded.value) {
                    updateExpandedStats(it)
                }
            }

            +UiSurface(
                name = "overview",
                sizes = Sizes.small(),
                colors = Colors.darkColors(accent = Color("b2ff00"), background = Color("10101080"))
            ) {
                modifier.layout(ColumnLayout)

                when (position) {
                    Position.UPPER_LEFT -> modifier.align(AlignmentX.Start, AlignmentY.Top)
                    Position.UPPER_RIGHT -> modifier.align(AlignmentX.End, AlignmentY.Top)
                    Position.LOWER_LEFT -> modifier.align(AlignmentX.Start, AlignmentY.Bottom)
                    Position.LOWER_RIGHT -> modifier.align(AlignmentX.End, AlignmentY.Bottom)
                }

                // min width
                Box { modifier.width(180.dp) }

                Text(fpsText.use()) {
                    modifier
                        .alignX(AlignmentX.Center)
                        .padding(sizes.smallGap)
                        .width(Grow.Std)
                        .textAlignX(AlignmentX.Center)
                        .font(fpsFont)
                        .textColor(colors.accent)
                        .background(deltaTGraph)

                    Text(if (isExpanded.use()) "-" else "+") {
                        modifier
                            .align(AlignmentX.End, AlignmentY.Center)
                            .font(fpsFont)
                            .textColor(colors.accent)
                            .onClick { isExpanded.set(!isExpanded.value) }
                            .margin(end = sizes.gap)
                    }
                }

                if (isExpanded.use()) {
                    Text("Kool v${KoolContext.KOOL_VERSION}") { debugTextStyle() }
                    sysInfos.use().forEach { txt -> Text(txt) { debugTextStyle() } }
                    Text(viewportText.use()) { debugTextStyle() }
                    Text(uptimeText.use()) { debugTextStyle() }
                    Text(numTexText.use()) { debugTextStyle() }
                    Text(numBufText.use()) { debugTextStyle() }
                    Text(numCmdsText.use()) { debugTextStyle() }
                    Text(numFacesText.use()) { debugTextStyle() }
                }
            }
        }
    }

    private fun updateExpandedStats(ev: RenderPass.UpdateEvent) {
        viewportText.set("Viewport: ${ev.viewport.width}x${ev.viewport.height} / ${(ev.ctx.windowScale * 100f).roundToInt()} %")
        updateUpText(ev.time)

        val numTex = ev.ctx.engineStats.textureAllocations.size
        val memTex = ev.ctx.engineStats.totalTextureSize.toDouble()
        numTexText.set("$numTex Textures: ${(memTex / (1024.0 * 1024.0)).toString(1)}M")

        val numBuf = ev.ctx.engineStats.bufferAllocations.size
        val memBuf = ev.ctx.engineStats.totalBufferSize.toDouble()
        numBufText.set("$numBuf Buffers: ${(memBuf / (1024.0 * 1024.0)).toString(1)}M")

        val numPipelines = ev.ctx.engineStats.pipelines.size
        val numDrawCmds = ev.ctx.engineStats.numDrawCommands
        numCmdsText.set("$numPipelines Shaders / $numDrawCmds Cmds")

        val numPrimitives = ev.ctx.engineStats.numPrimitives
        numFacesText.set("$numPrimitives Faces")

        ev.ctx.getSysInfos().forEachIndexed { i, txt ->
            val clampedTxt = if (txt.length > 32) txt.substring(0..31) else txt
            if (i == sysInfos.size) {
                sysInfos += clampedTxt
            } else if (clampedTxt != sysInfos[i]) {
                sysInfos[i] = clampedTxt
            }
        }
    }

    private fun TextScope.debugTextStyle() {
        modifier
            .alignX(AlignmentX.End)
            .margin(top = (-2).dp, bottom = (-2).dp, start = 4.dp, end = 4.dp)
    }

    private fun updateUpText(time: Double) {
        if (time.toInt() != lastUpSecs) {
            lastUpSecs = time.toInt()

            var hh = "" + (time / 3600.0).toInt()
            var mm = "" + (time % 3600.0 / 60.0).toInt()
            var ss = "" + (time % 60.0).toInt()
            if (hh.length == 1) hh = "0$hh"
            if (mm.length == 1) mm = "0$mm"
            if (ss.length == 1) ss = "0$ss"
            uptimeText.set("Up: $hh:$mm.$ss")
        }
    }

    enum class Position {
        UPPER_LEFT,
        UPPER_RIGHT,
        LOWER_LEFT,
        LOWER_RIGHT
    }
}

private class DeltaTGraph : UiRenderer<UiNode> {
    val graphMesh: Mesh
    val graphGeom = IndexedVertexList(Ui2Shader.UI_MESH_ATTRIBS)
    val graphBuilder = MeshBuilder(graphGeom).apply { isInvertFaceOrientation = true }
    val graphVertex = graphGeom[0]

    var graphIdx = 0
    var prevDeltaT = 0f

    val prevClip = MutableVec4f()
    var width = 0
    var height = 0

    init {
        graphMesh = Mesh(graphGeom)
        graphMesh.geometry.usage = Usage.DYNAMIC
        graphMesh.shader = Ui2Shader()
        graphMesh.onUpdate += this::updateGraph
    }

    override fun renderUi(node: UiNode) {
        if (node.clipBoundsPx != prevClip) {
            prevClip.set(node.clipBoundsPx)
            node.apply {
                width = widthPx.toInt()
                height = heightPx.toInt()
                graphBuilder.clear()
                graphBuilder.configured(Color.WHITE) {
                    for (i in 1..width) {
                        line(i - 0.5f, heightPx, i - 0.5f, heightPx - 1f, 1f)
                    }
                }
            }
        }
        node.surface.getMeshLayer(node.modifier.zLayer - 1).addCustomLayer("dt-graph") { graphMesh }
    }

    fun updateGraph(updateEvent: RenderPass.UpdateEvent) {
        // set previous bar color according to previous deltaT
        var color = Color.WHITE
        if (prevDeltaT > 0.05f) {
            color = Color.RED
        } else if (prevDeltaT > 0.025f) {
            color = Color.YELLOW
        }
        setCurrentBarColor(color)
        prevDeltaT = updateEvent.deltaT

        // modify vertices in graph mesh to change line height of current bar
        graphIdx = (graphIdx + 4) % (width * 4)
        graphVertex.index = graphIdx
        val y0 = graphVertex.position.y
        val h = min(updateEvent.deltaT * 250, height.toFloat())
        graphVertex.index++
        graphVertex.position.y = y0 - h
        graphVertex.index++
        graphVertex.position.y = y0 - h

        setCurrentBarColor(Color.MAGENTA)
        graphGeom.hasChanged = true
    }

    fun setCurrentBarColor(color: Color) {
        graphVertex.index = graphIdx
        for (i in 0..3) {
            graphVertex.index = graphIdx + i
            graphVertex.color.set(color)
        }
    }
}
