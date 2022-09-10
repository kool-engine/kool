package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.scene.ui.Font
import de.fabmax.kool.scene.ui.FontProps

class UiContext(val surface: UiSurface) {

    val usedState = mutableSetOf<MutableState<*>>()

    private val defaultMesh = mesh(Ui2Shader.UI_MESH_ATTRIBS) {
        shader = Ui2Shader()
    }
    val defaultBuilder = MeshBuilder(defaultMesh.geometry).apply { setupUiBuilder() }

    private val textMeshes = mutableMapOf<FontProps, TextMesh>()

    private val viewportBox = BoxNode(null, this)
    private val uiHierarchy = viewportBox.createChild(RootBox::class) { parent, _ -> RootBox(parent) }

    val requiresUpdate: Boolean
        get() = usedState.any { it.isChanged }

    var measuredScale = 1f
        private set

    init {
        // add some initial state to enforce initial UI update
        mutableStateOf(0).use(this)
        surface += defaultMesh
    }

    internal fun updateUi(updateEvent: RenderPass.UpdateEvent, block: BoxScope.() -> Unit) {
        if (!requiresUpdate) {
            return
        }

        val vp = updateEvent.renderPass.viewport
        measuredScale = updateEvent.ctx.windowScale
        viewportBox.setBounds(0f, 0f, vp.width.toFloat(), vp.height.toFloat())
        viewportBox.setClipBounds(0f, 0f, vp.width.toFloat(), vp.height.toFloat())

        usedState.clear()
        uiHierarchy.reset()
        uiHierarchy.block()

        usedState.forEach { it.isChanged = false }
        textMeshes.values.forEach { it.clear() }
        defaultMesh.geometry.clear()

        measureUiNodeContent(viewportBox, updateEvent.ctx)
        layoutUiNodeChildren(viewportBox, updateEvent.ctx)
        renderUiNode(uiHierarchy, updateEvent.ctx)
    }

    fun getTextBuilder(font: Font, ctx: KoolContext): MeshBuilder {
        val textMesh =  textMeshes.getOrPut(font.fontProps) { TextMesh(font, ctx).also { surface += it.mesh } }
        textMesh.used = true
        return textMesh.builder
    }

    private fun measureUiNodeContent(node: UiNode, ctx: KoolContext) {
        for (i in node.children.indices) {
            measureUiNodeContent(node.children[i], ctx)
        }
        node.measureContentSize(ctx)
    }

    private fun layoutUiNodeChildren(node: UiNode, ctx: KoolContext) {
        node.layoutChildren(ctx)
        for (i in node.children.indices) {
            if (node.children[i].isInBounds) {
                layoutUiNodeChildren(node.children[i], ctx)
            }
        }
    }

    private fun renderUiNode(node: UiNode, ctx: KoolContext) {
        if (node.isInBounds) {
            node.render(ctx)
            for (i in node.children.indices) {
                renderUiNode(node.children[i], ctx)
            }
        }
    }

    private inner class RootBox(parent: UiNode) : BoxNode(parent, this@UiContext) {
        fun reset() {
            resetDefaults()
        }
    }

    private class TextMesh(font: Font, ctx: KoolContext) {
        val mesh = mesh(Ui2Shader.UI_MESH_ATTRIBS) {
            shader = Ui2Shader().apply { setFont(font, ctx) }
        }

        val builder = MeshBuilder(mesh.geometry)
        var used = false

        init {
            builder.setupUiBuilder()
        }

        fun clear() {
            mesh.geometry.clear()
            used = false
        }
    }

    companion object {
        fun MeshBuilder.setupUiBuilder() {
            scale(1f, -1f, 1f)
            invertFaceOrientation = true
        }
    }
}