package de.fabmax.kool.util

import de.fabmax.kool.gl.GlResource
import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader

/**
 * @author fabmax
 */

fun debugOverlay(ctx: RenderContext): Scene {
    val dbgOverlay = uiScene(ctx.screenDpi) {
        theme = theme(UiTheme.DARK) {
            componentUi({ BlankComponentUi() })
            containerUi(::BlurredComponentUi)
            standardFont(FontProps(Font.SYSTEM_FONT, 12f))
        }
        content.ui.setCustom(BlankComponentUi())

        +container("dbgPanel") {
            layoutSpec.setOrigin(dps(-120f, true), dps(-132f, true), zero())
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

                onRender += { ctx ->
                    text = "${ctx.fps.toInt()}.${(ctx.fps * 10f).toInt() % 10} fps"
                }
            }

            +label("lblVpSize") {
                layoutSpec.setOrigin(dps(-120f, true), dps(-60f, true), zero())
                layoutSpec.setSize(dps(120f, true), dps(18f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                var lastWndW = -1
                var lastWndH = -1
                onRender += { ctx ->
                    if (ctx.windowWidth != lastWndW || ctx.windowHeight != lastWndH) {
                        lastWndW = ctx.windowWidth
                        lastWndH = ctx.windowHeight
                        text = "Viewport: ${ctx.windowWidth}x${ctx.windowHeight}"
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
                onRender += { ctx ->
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

            +label("lblNumTextures") {
                layoutSpec.setOrigin(dps(-120f, true), dps(-96f, true), zero())
                layoutSpec.setSize(dps(120f, true), dps(18f, true), zero())
                padding = Margin(zero(), zero(), dps(4f, true), dps(4f, true))
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)

                var last = -1
                var lastMem = -1.0
                onRender += { ctx ->
                    val num = ctx.memoryMgr.numTextures
                    val mem = ctx.memoryMgr.getTotalMemory(GlResource.Type.TEXTURE)
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
                onRender += { ctx ->
                    val num = ctx.memoryMgr.numBuffers
                    val mem = ctx.memoryMgr.getTotalMemory(GlResource.Type.BUFFER)
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
                onRender += { ctx ->
                    val num = ctx.memoryMgr.numShaders
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
    val graphData = MeshData(false, true, false)
    val graphBuilder = MeshBuilder(graphData)
    val graphItem = graphData.data[0]

    var graphIdx = 0
    var prevDeltaT = 0f

    companion object {
        const val WIDTH = 120
    }

    init {
        graphMesh = Mesh(graphData)
        graphMesh.meshData.usage = GL.DYNAMIC_DRAW
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
        prevDeltaT = ctx.deltaT

        // modify vertices in graph mesh to change line height of current bar
        graphIdx = (graphIdx + 4) % (WIDTH * 4)
        graphItem.index = graphIdx
        val y0 = graphItem.position.y
        val h = Math.min(ctx.deltaT * 250, height)
        graphItem.index++
        graphItem.position.y = y0 + h
        graphItem.index++
        graphItem.position.y = y0 + h

        setCurrentBarColor(Color.MAGENTA)
        graphData.isSyncRequired = true

        super.render(ctx)
    }

    fun setCurrentBarColor(color: Color) {
        graphItem.index = graphIdx
        for (i in 0..3) {
            graphItem.index = graphIdx + i
            graphItem.color.set(color)
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
