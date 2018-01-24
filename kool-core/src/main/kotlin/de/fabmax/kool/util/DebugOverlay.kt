package de.fabmax.kool.util

import de.fabmax.kool.RenderContext
import de.fabmax.kool.gl.GL_DYNAMIC_DRAW
import de.fabmax.kool.gl.GlResource
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import kotlin.math.min

/**
 * @author fabmax
 */

fun debugOverlay(ctx: RenderContext, alignBottom: Boolean = false): Scene {
    val dbgOverlay = uiScene(ctx.screenDpi) {
        theme = theme(UiTheme.DARK) {
            componentUi({ BlankComponentUi() })
            containerUi(::SimpleComponentUi)
            standardFont(FontProps(Font.SYSTEM_FONT, 12f))
        }
        content.ui.setCustom(BlankComponentUi())

        +container("dbgPanel") {
            if (alignBottom) {
                layoutSpec.setOrigin(dps(-120f, true), dps(0f, true), zero())
            } else {
                layoutSpec.setOrigin(dps(-120f, true), dps(-132f, true), zero())
            }
            layoutSpec.setSize(dps(120f, true), dps(132f, true), zero())

            +DeltaTGraph(this@uiScene).apply {
                layoutSpec.setOrigin(dps(-120f, true), dps(-40f, true), zero())
                layoutSpec.setSize(dps(120f, true), dps(40f, true), zero())
            }

            +label("lblFps") {
                layoutSpec.setOrigin(dps(-120f, true), dps(-40f, true), zero())
                layoutSpec.setSize(dps(120f, true), dps(40f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
                text = ""
                font.setCustom(UiTheme.DARK_SIMPLE.standardFont(ctx.screenDpi))
                textColor.setCustom(root.theme.accentColor)

                onRender += { c ->
                    text = "${c.fps.toInt()}.${(c.fps * 10f).toInt() % 10} fps"
                }
            }

            +label("lblVpSize") {
                layoutSpec.setOrigin(dps(-120f, true), dps(-60f, true), zero())
                layoutSpec.setSize(dps(120f, true), dps(18f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                var lastWndW = -1
                var lastWndH = -1
                onRender += { c ->
                    if (c.windowWidth != lastWndW || c.windowHeight != lastWndH) {
                        lastWndW = c.windowWidth
                        lastWndH = c.windowHeight
                        text = "Viewport: ${c.windowWidth}x${c.windowHeight}"
                    }
                }
            }

            +label("lblUpTime") {
                layoutSpec.setOrigin(dps(-120f, true), dps(-78f, true), zero())
                layoutSpec.setSize(dps(120f, true), dps(18f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = "Up: 00:00.00"

                var updateT = 1f
                onRender += { c ->
                    updateT -= c.deltaT.toFloat()
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

            +label("lblNumTextures") {
                layoutSpec.setOrigin(dps(-120f, true), dps(-96f, true), zero())
                layoutSpec.setSize(dps(120f, true), dps(18f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                var last = -1
                var lastMem = -1.0
                onRender += { c ->
                    val num = c.memoryMgr.numTextures
                    val mem = c.memoryMgr.getTotalMemory(GlResource.Type.TEXTURE)
                    if (num != last || mem != lastMem) {
                        last = num
                        lastMem = mem
                        var mb = "${mem / (1024*1024) + 0.05}"
                        val pt = mb.indexOf('.')
                        mb = mb.substring(0, pt+2)
                        text = "$num Textures: ${mb}M"
                    }
                }
            }

            +label("lblNumBuffers") {
                layoutSpec.setOrigin(dps(-120f, true), dps(-114f, true), zero())
                layoutSpec.setSize(dps(120f, true), dps(18f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                var last = -1
                var lastMem = -1.0
                onRender += { c ->
                    val num = c.memoryMgr.numBuffers
                    val mem = c.memoryMgr.getTotalMemory(GlResource.Type.BUFFER)
                    if (num != last || mem != lastMem) {
                        last = num
                        lastMem = mem
                        var mb = "${mem / (1024*1024) + 0.05}"
                        val pt = mb.indexOf('.')
                        mb = mb.substring(0, pt+2)
                        text = "$num Buffers: ${mb}M"
                    }
                }
            }

            +label("lblNumShaders") {
                layoutSpec.setOrigin(dps(-120f, true), dps(-132f, true), zero())
                layoutSpec.setSize(dps(120f, true), dps(18f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                var last = -1
                onRender += { c ->
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

    companion object {
        const val WIDTH = 120
    }

    init {
        graphMesh = Mesh(graphData)
        graphMesh.meshData.usage = GL_DYNAMIC_DRAW
        graphMesh.shader = basicShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = LightModel.NO_LIGHTING
        }
    }

    override fun render(ctx: RenderContext) {
        // set previous bar color according to previous deltaT
        var color = Color.WHITE
        if (prevDeltaT > 0.05f) {
            color = Color.RED
        } else if (prevDeltaT > 0.025f) {
            color = Color.YELLOW
        }
        setCurrentBarColor(color)
        prevDeltaT = ctx.deltaT.toFloat()

        // modify vertices in graph mesh to change line height of current bar
        graphIdx = (graphIdx + 4) % (WIDTH * 4)
        graphVertex.index = graphIdx
        val y0 = graphVertex.position.y
        val h = min(ctx.deltaT.toFloat() * 250, height)
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

    override fun updateUi(ctx: RenderContext) {
        super.updateUi(ctx)

        setupBuilder(graphBuilder)
        graphBuilder.color = Color.WHITE
        for (i in 1..WIDTH) {
            graphBuilder.line(i - 0.5f, 0f, i - 0.5f, 1f, 1f)
        }
    }

    override fun updateTheme(ctx: RenderContext) {
        super.updateTheme(ctx)

        // re-add graph mesh to make sure it is drawn after the background
        this -= graphMesh
        this += graphMesh
    }
}
