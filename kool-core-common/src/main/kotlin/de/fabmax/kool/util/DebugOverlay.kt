package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.getMemoryInfo
import de.fabmax.kool.gl.GL_DYNAMIC_DRAW
import de.fabmax.kool.gl.GlResource
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.toString
import kotlin.math.min

/**
 * @author fabmax
 */

enum class Position {
    UPPER_LEFT,
    UPPER_RIGHT,
    LOWER_LEFT,
    LOWER_RIGHT
}

fun debugOverlay(ctx: KoolContext, position: Position = Position.UPPER_RIGHT): Scene {
    val dbgOverlay = uiScene(ctx.screenDpi) {
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi(::SimpleComponentUi)
            standardFont(FontProps(Font.SYSTEM_FONT, 12f))
        }
        content.ui.setCustom(BlankComponentUi())

        +container("dbgPanel") {
            val hasMemInfo = !getMemoryInfo().isEmpty()
            val height = if (hasMemInfo) { 168f } else { 150f }
            val width = 130f

            when (position) {
                Position.UPPER_LEFT -> layoutSpec.setOrigin(zero(), dps(-height, true), zero())
                Position.UPPER_RIGHT -> layoutSpec.setOrigin(dps(-width, true), dps(-height, true), zero())
                Position.LOWER_LEFT -> layoutSpec.setOrigin(zero(), dps(0f, true), zero())
                Position.LOWER_RIGHT -> layoutSpec.setOrigin(dps(-width, true), dps(0f, true), zero())
            }
            layoutSpec.setSize(dps(width, true), dps(height, true), zero())

            +DeltaTGraph(this@uiScene).apply {
                layoutSpec.setOrigin(zero(), dps(-40f, true), zero())
                layoutSpec.setSize(dps(width, true), dps(40f, true), zero())
            }

            +label("lblFps") {
                layoutSpec.setOrigin(zero(), dps(-37f, true), zero())
                layoutSpec.setSize(dps(width, true), dps(37f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
                text = ""
                font.setCustom(UiTheme.DARK_SIMPLE.standardFont(dpi, ctx))
                textColor.setCustom(root.theme.accentColor)

                onPreRender += { c ->
                    text = "${c.fps.toString(1)} fps"
                }
            }

            var yOri = -60f
            +label("lblVersion") {
                layoutSpec.setOrigin(zero(), dps(yOri, true), zero())
                layoutSpec.setSize(dps(width, true), dps(18f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = ctx.glCapabilities.glVersion.toString()
            }

            if (hasMemInfo) {
                yOri -= 18f
                +label("lblMemInfo") {
                    layoutSpec.setOrigin(zero(), dps(yOri, true), zero())
                    layoutSpec.setSize(dps(width, true), dps(18f, true), zero())
                    padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                    onPreRender += {
                        text = getMemoryInfo()
                    }
                }
            }

            yOri -= 18f
            +label("lblVpSize") {
                layoutSpec.setOrigin(zero(), dps(yOri, true), zero())
                layoutSpec.setSize(dps(width, true), dps(18f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                var lastWndW = -1
                var lastWndH = -1
                onPreRender += { c ->
                    if (c.windowWidth != lastWndW || c.windowHeight != lastWndH) {
                        lastWndW = c.windowWidth
                        lastWndH = c.windowHeight
                        text = "Viewport: ${c.windowWidth}x${c.windowHeight}"
                    }
                }
            }

            yOri -= 18f
            +label("lblUpTime") {
                layoutSpec.setOrigin(zero(), dps(yOri, true), zero())
                layoutSpec.setSize(dps(width, true), dps(18f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = "Up: 00:00.00"

                var updateT = 1f
                onPreRender += { c ->
                    updateT -= c.deltaT
                    if (updateT < 0) {
                        updateT += 1f

                        // still no javascript compatible string formatting in kotlin 1.1... :(
                        var hh = "" + (c.time / 3600.0).toInt()
                        if (hh.length == 1) {
                            hh = "0" + hh
                        }
                        var mm = "" + (c.time % 3600.0 / 60.0).toInt()
                        if (mm.length == 1) {
                            mm = "0" + mm
                        }
                        var ss = "" + (c.time % 60.0).toInt()
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
                layoutSpec.setSize(dps(width, true), dps(18f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                var last = -1
                var lastMem = -1.0
                onPreRender += { c ->
                    val num = c.memoryMgr.numTextures
                    val mem = c.memoryMgr.getTotalMemory(GlResource.Type.TEXTURE)
                    if (num != last || mem != lastMem) {
                        last = num
                        lastMem = mem
                        text = "$num Textures: ${(mem / (1024.0*1024.0)).toString(1)}M"
                    }
                }
            }

            yOri -= 18f
            +label("lblNumBuffers") {
                layoutSpec.setOrigin(zero(), dps(yOri, true), zero())
                layoutSpec.setSize(dps(width, true), dps(18f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                var last = -1
                var lastMem = -1.0
                onPreRender += { c ->
                    val num = c.memoryMgr.numBuffers
                    val mem = c.memoryMgr.getTotalMemory(GlResource.Type.BUFFER)
                    if (num != last || mem != lastMem) {
                        last = num
                        lastMem = mem
                        text = "$num Buffers: ${(mem / (1024.0*1024.0)).toString(1)}M"
                    }
                }
            }

            yOri -= 18f
            +label("lblNumShaders") {
                layoutSpec.setOrigin(zero(), dps(yOri, true), zero())
                layoutSpec.setSize(dps(width, true), dps(18f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                var last = -1
                onPreRender += { c ->
                    val num = c.memoryMgr.numShaders
                    if (num != last) {
                        last = num
                        text = "$num Shaders"
                    }
                }
            }
        }
    }

    dbgOverlay.isPickingEnabled = false
    return dbgOverlay
}

private class DeltaTGraph(root: UiRoot) : UiComponent("deltaT", root) {
    val graphMesh: Mesh
    val graphData = MeshData(Attribute.POSITIONS, Attribute.COLORS)
    val graphBuilder = MeshBuilder(graphData)
    val graphVertex = graphData[0]

    var graphIdx = 0
    var prevDeltaT = 0f

    init {
        graphMesh = Mesh(graphData)
        graphMesh.meshData.usage = GL_DYNAMIC_DRAW
        graphMesh.shader = basicShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = LightModel.NO_LIGHTING
        }
    }

    override fun render(ctx: KoolContext) {
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
        graphData.isSyncRequired = true

        super.render(ctx)
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
        for (i in 1..width.toInt()) {
            graphBuilder.line(i - 0.5f, 0f, i - 0.5f, 1f, 1f)
        }
    }

    override fun updateTheme(ctx: KoolContext) {
        super.updateTheme(ctx)

        // re-add graph mesh to make sure it is drawn after the background
        this -= graphMesh
        this += graphMesh
    }
}
