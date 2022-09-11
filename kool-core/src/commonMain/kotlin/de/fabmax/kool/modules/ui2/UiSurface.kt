package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.scene.ui.Font
import de.fabmax.kool.scene.ui.FontProps

class UiSurface(name: String = "uiSurface", private val uiBlock: BoxScope.() -> Unit) : Group(name) {

    private val defaultMesh = mesh(Ui2Shader.UI_MESH_ATTRIBS) {
        shader = Ui2Shader()
    }
    val defaultBuilder = MeshBuilder(defaultMesh.geometry).apply { setupUiBuilder() }
    private val textMeshes = mutableMapOf<FontProps, TextMesh>()

    private val viewportBox = BoxNode(null, this)
    private val uiHierarchy = viewportBox.createChild(RootBox::class) { parent, _ -> RootBox(parent) }

    private val nodeResult = mutableListOf<UiNode>()
    private var hoveredNode: UiNode? = null

    private val registeredState = mutableListOf<MutableState>()
    var requiresUpdate: Boolean = true
        private set

    var measuredScale = 1f
        private set

    init {
        this += defaultMesh
        onUpdate += {
            doInput(it)
            updateUi(it)
        }
    }

    private fun doInput(updateEvent: RenderPass.UpdateEvent) {
        val ptr = updateEvent.ctx.inputMgr.pointerState.primaryPointer
        uiHierarchy.collectNodesAt(ptr.x.toFloat(), ptr.y.toFloat(), nodeResult, hasPointerListener)

        if (hoveredNode == null && nodeResult.isEmpty()) {
            // nothing else to check
            return
        }

        val ptrEv = PointerEvent(ptr, updateEvent.ctx)
        var isWheelX = ptr.deltaScrollX != 0.0
        var isWheelY = ptr.deltaScrollY != 0.0
        var isAnyClick = ptr.isLeftButtonClicked ||
                ptr.isRightButtonClicked ||
                ptr.isMiddleButtonClicked ||
                ptr.isForwardButtonClicked ||
                ptr.isBackButtonClicked

        // check if we still hover previously hovered node
        if (hoveredNode != null) {
            if (hoveredNode in nodeResult) {
                // hovering continues
                hoveredNode?.modifier?.pointerCallbacks?.onHover?.invoke(ptrEv)
                if (!ptrEv.isConsumed) {
                    // hovering was rejected, do not treat this node as hover node anymore
                    hoveredNode = null
                }
            } else {
                // hovering stopped, cannot be rejected...
                hoveredNode?.modifier?.pointerCallbacks?.onExit?.invoke(ptrEv)
                hoveredNode = null
            }
        }

        nodeResult.forEach { node ->
            val cbs = node.modifier.pointerCallbacks

            // onRawPointer is called for any node below pointer position
            cbs.onRawPointer?.invoke(ptrEv)

            // make sure consumed flag is true, as this is the default. if a node does not want to consume an event
            // it has to actively reject() it
            ptrEv.isConsumed = true

            if (hoveredNode == null && cbs.hasAnyHoverCallback) {
                // no node was hovered before (or we just exited it) and we found a new one which has hover
                // callbacks installed -> select it as new hovered node
                cbs.onEnter?.invoke(ptrEv)
                if (ptrEv.isConsumed) {
                    hoveredNode = node
                }
                ptrEv.isConsumed = true
            }

            if (isAnyClick) {
                cbs.onClick?.invoke(ptrEv)
                if (ptrEv.isConsumed) {
                    // click was consumed
                    isAnyClick = false
                }
                ptrEv.isConsumed = true
            }

            if (isWheelX) {
                cbs.onWheelX?.invoke(ptrEv)
                if (ptrEv.isConsumed) {
                    // wheel was consumed
                    isWheelX = false
                }
                ptrEv.isConsumed = true
            }
            if (isWheelY) {
                cbs.onWheelY?.invoke(ptrEv)
                if (ptrEv.isConsumed) {
                    // wheel was consumed
                    isWheelY = false
                }
                ptrEv.isConsumed = true
            }
        }
    }

    private fun updateUi(updateEvent: RenderPass.UpdateEvent) {
        if (!requiresUpdate) {
            return
        }
        requiresUpdate = false
        registeredState.forEach { it.clear() }
        registeredState.clear()

        val vp = updateEvent.renderPass.viewport
        measuredScale = updateEvent.ctx.windowScale
        viewportBox.setBounds(0f, 0f, vp.width.toFloat(), vp.height.toFloat())

        uiHierarchy.reset()
        uiHierarchy.uiBlock()

        textMeshes.values.forEach { it.clear() }
        defaultMesh.geometry.clear()

        measureUiNodeContent(viewportBox, updateEvent.ctx)
        layoutUiNodeChildren(viewportBox, updateEvent.ctx)
        renderUiNode(uiHierarchy, updateEvent.ctx)
    }

    fun triggerUpdate(changedState: MutableState) {
        requiresUpdate = true
        registeredState += changedState
    }

    fun getTextBuilder(font: Font, ctx: KoolContext): MeshBuilder {
        val textMesh =  textMeshes.getOrPut(font.fontProps) { TextMesh(font, ctx).also { this += it.mesh } }
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
        node.render(ctx)
        for (i in node.children.indices) {
            if (node.children[i].isInBounds) {
                renderUiNode(node.children[i], ctx)
            }
        }
    }

    private inner class RootBox(parent: UiNode) : BoxNode(parent, this@UiSurface) {
        fun reset() {
            resetDefaults()
        }

        fun collectNodesAt(x: Float, y: Float, result: MutableList<UiNode>, predicate: (UiNode) -> Boolean) {
            result.clear()
            if (isInClip(x, y)) {
                traverseChildren(this, x, y, result, predicate)
            }
            if (result.size > 1) {
                result.sortBy { -it.layer }
            }
        }

        private fun traverseChildren(node: UiNode, x: Float, y: Float, result: MutableList<UiNode>, predicate: (UiNode) -> Boolean) {
            for (i in node.children.indices) {
                val child = node.children[i]
                if (child.isInClip(x, y)) {
                    traverseChildren(child, x, y, result, predicate)
                }
            }
            if (predicate(node)) {
                result += node
            }
        }

        fun UiNode.isInClip(x: Float, y: Float): Boolean {
            return x in clippedMinX..clippedMaxX && y in clippedMinY..clippedMaxY
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

        private val hasPointerListener: (UiNode) -> Boolean = { it.modifier.pointerCallbacks.hasAnyCallback }
    }
}