package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.toString
import kotlin.math.min

/**
 * @author fabmax
 */

fun debugOverlay(ctx: KoolContext, position: DebugOverlay.Position = DebugOverlay.Position.UPPER_RIGHT): Scene {
    return DebugOverlay(ctx, position).ui
}

class DebugOverlay(ctx: KoolContext, position: Position = Position.UPPER_RIGHT) {

    val ui: Scene
    var xOffset = 0f
        set(value) {
            if (field != value) {
                field = value
                menuContainer.requestUpdateTransform()
            }
        }

    private lateinit var menuContainer: UiContainer

    init {
        ui = uiScene(ctx.screenDpi, "debug-overlay") {
            theme = theme(UiTheme.DARK_SIMPLE) {
                componentUi { BlankComponentUi() }
                containerUi(::SimpleComponentUi)
                standardFont(FontProps(Font.SYSTEM_FONT, 12f))
            }
            content.ui.setCustom(BlankComponentUi())

            +container("dbgPanel") {
                menuContainer = this
                customTransform = {
                    translate(xOffset, 0f, 0f)
                }

                val height = 150 + ctx.getSysInfos().size * 18f
                val width = 180f

                when (position) {
                    Position.UPPER_LEFT -> layoutSpec.setOrigin(zero(), dps(-height, true), zero())
                    Position.UPPER_RIGHT -> layoutSpec.setOrigin(dps(-width, true), dps(-height, true), zero())
                    Position.LOWER_LEFT -> layoutSpec.setOrigin(zero(), zero(), zero())
                    Position.LOWER_RIGHT -> layoutSpec.setOrigin(dps(-width, true), zero(), zero())
                }
                layoutSpec.setSize(dps(width, true), dps(height, true), full())

                +DeltaTGraph(this@uiScene).apply {
                    layoutSpec.setOrigin(zero(), dps(-40f, true), zero())
                    layoutSpec.setSize(dps(width, true), dps(40f, true), full())
                }

                +label("lblFps") {
                    layoutSpec.setOrigin(zero(), dps(-37f, true), zero())
                    layoutSpec.setSize(dps(width, true), dps(37f, true), full())
                    padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                    textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
                    text = ""
                    font.setCustom(UiTheme.DARK_SIMPLE.standardFont(dpi, ctx))
                    textColor.setCustom(root.theme.accentColor)

                    onUpdate += { _, ctx ->
                        text = "${ctx.fps.toString(1)} fps"
                    }
                }

                var yOri = -60f
                for (i in ctx.getSysInfos().indices) {
                    +label("lblSysInfo_$i") {
                        layoutSpec.setOrigin(zero(), dps(yOri, true), zero())
                        layoutSpec.setSize(dps(width, true), dps(18f, true), full())
                        padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                        textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                        text = ""
                        onUpdate += { _, _ ->
                            text = ctx.getSysInfos()[i]
                        }
                    }
                    yOri -= 18f
                }

                +label("lblVpSize") {
                    layoutSpec.setOrigin(zero(), dps(yOri, true), zero())
                    layoutSpec.setSize(dps(width, true), dps(18f, true), full())
                    padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                    var lastWndW = -1
                    var lastWndH = -1
                    onUpdate += { _, ctx ->
                        if (ctx.windowWidth != lastWndW || ctx.windowHeight != lastWndH) {
                            lastWndW = ctx.windowWidth
                            lastWndH = ctx.windowHeight
                            text = "Viewport: ${ctx.windowWidth}x${ctx.windowHeight}"
                        }
                    }
                }

                yOri -= 18f
                +label("lblUpTime") {
                    layoutSpec.setOrigin(zero(), dps(yOri, true), zero())
                    layoutSpec.setSize(dps(width, true), dps(18f, true), full())
                    padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                    text = "Up: 00:00.00"

                    var updateT = 1f
                    onUpdate += { _, ctx ->
                        updateT -= ctx.deltaT
                        if (updateT < 0) {
                            updateT += 1f

                            // still no javascript compatible string formatting in kotlin 1.1... :(
                            var hh = "" + (ctx.time / 3600.0).toInt()
                            if (hh.length == 1) {
                                hh = "0" + hh
                            }
                            var mm = "" + (ctx.time % 3600.0 / 60.0).toInt()
                            if (mm.length == 1) {
                                mm = "0" + mm
                            }
                            var ss = "" + (ctx.time % 60.0).toInt()
                            if (ss.length == 1) {
                                ss = "0" + ss
                            }
                            text = "Up: $hh:$mm.$ss"
                        }
                    }
                }

                yOri -= 18f
                +label("lblNumTextures") {
                    layoutSpec.setOrigin(zero(), dps(yOri, true), zero())
                    layoutSpec.setSize(dps(width, true), dps(18f, true), full())
                    padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                    var last = -1
                    var lastMem = -1.0
                    onUpdate += { _, ctx ->
                        val num = ctx.engineStats.textureAllocations.size
                        val mem = ctx.engineStats.totalTextureSize.toDouble()
                        if (num != last || mem != lastMem) {
                            last = num
                            lastMem = mem
                            text = "$num Textures: ${(mem / (1024.0 * 1024.0)).toString(1)}M"
                        }
                    }
                }

                yOri -= 18f
                +label("lblNumBuffers") {
                    layoutSpec.setOrigin(zero(), dps(yOri, true), zero())
                    layoutSpec.setSize(dps(width, true), dps(18f, true), full())
                    padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                    var last = -1
                    var lastMem = -1.0
                    onUpdate += { _, ctx ->
                        val num = ctx.engineStats.bufferAllocations.size
                        val mem = ctx.engineStats.totalBufferSize.toDouble()
                        if (num != last || mem != lastMem) {
                            last = num
                            lastMem = mem
                            text = "$num Buffers: ${(mem / (1024.0 * 1024.0)).toString(1)}M"
                        }
                    }
                }

                yOri -= 18f
                +label("lblNumShaders") {
                    layoutSpec.setOrigin(zero(), dps(yOri, true), zero())
                    layoutSpec.setSize(dps(width, true), dps(18f, true), full())
                    padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                    var lastPipelines = -1
                    var lastInstances = -1
                    onUpdate += { _, ctx ->
                        val numPipelines = ctx.engineStats.pipelines.size
                        val numDrawCmds = ctx.engineStats.numDrawCommands
                        if (numDrawCmds != lastInstances || numPipelines != lastPipelines) {
                            lastPipelines = numPipelines
                            lastInstances = numDrawCmds
                            text = "$numPipelines Shaders / $numDrawCmds Cmds"
                        }
                    }
                }

                yOri -= 18f
                +label("lblNumFaces") {
                    layoutSpec.setOrigin(zero(), dps(yOri, true), zero())
                    layoutSpec.setSize(dps(width, true), dps(18f, true), full())
                    padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                    var lastPrimitives = -1
                    onUpdate += { _, ctx ->
                        val numPrimitives = ctx.engineStats.numPrimitives
                        if (numPrimitives != lastPrimitives) {
                            lastPrimitives = numPrimitives
                            text = "$numPrimitives Faces"
                        }
                    }
                }
            }
        }
        ui.isPickingEnabled = false
    }

    enum class Position {
        UPPER_LEFT,
        UPPER_RIGHT,
        LOWER_LEFT,
        LOWER_RIGHT
    }
}

private class DeltaTGraph(root: UiRoot) : UiComponent("deltaT", root) {
    val graphMesh: Mesh
    val graphGeom = IndexedVertexList(UiShader.UI_MESH_ATTRIBS)
    val graphBuilder = MeshBuilder(graphGeom)
    val graphVertex = graphGeom[0]

    var graphIdx = 0
    var prevDeltaT = 0f

    init {
        graphMesh = Mesh(graphGeom)
        graphMesh.geometry.usage = Usage.DYNAMIC
        graphMesh.shader = UiShader()
    }

    override fun collectDrawCommands(renderPass: RenderPass, ctx: KoolContext) {
        // set previous bar color according to previous deltaT
        var color = Color.WHITE
        if (prevDeltaT > 0.05f) {
            color = Color.RED
        } else if (prevDeltaT > 0.025f) {
            color = Color.YELLOW
        }
        setCurrentBarColor(color)
        prevDeltaT = ctx.deltaT

        // modify vertices in graph mesh to change line height of current bar
        graphIdx = (graphIdx + 4) % (width.toInt() * 4)
        graphVertex.index = graphIdx
        val y0 = graphVertex.position.y
        val h = min(ctx.deltaT * 250, height)
        graphVertex.index++
        graphVertex.position.y = y0 + h
        graphVertex.index++
        graphVertex.position.y = y0 + h

        setCurrentBarColor(Color.MAGENTA)
        graphGeom.hasChanged = true

        super.collectDrawCommands(renderPass, ctx)
    }

    fun setCurrentBarColor(color: Color) {
        graphVertex.index = graphIdx
        for (i in 0..3) {
            graphVertex.index = graphIdx + i
            graphVertex.color.set(color)
        }
    }

    override fun updateUi(ctx: KoolContext) {
        super.updateUi(ctx)

        setupBuilder(graphBuilder)
        graphBuilder.color = Color.WHITE
        graphBuilder.withTransform {
            translate(0f, 0f, 0f)
            for (i in 1..width.toInt()) {
                graphBuilder.line(i - 0.5f, 0f, i - 0.5f, 1f, 1f)
            }
        }
    }

    override fun updateTheme(ctx: KoolContext) {
        super.updateTheme(ctx)

        // re-add graph mesh to make sure it is drawn after the background
        this -= graphMesh
        this += graphMesh
    }
}
