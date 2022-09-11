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

    private val inputHandler = InputHandler()
    private val viewportBox = BoxNode(null, this)
    private val uiHierarchy = viewportBox.createChild(RootBox::class) { parent, _ -> RootBox(parent) }

    private val registeredState = mutableListOf<MutableState>()
    var requiresUpdate: Boolean = true
        private set

    var measuredScale = 1f
        private set

    init {
        this += defaultMesh
        onUpdate += {
            inputHandler.handleInput(it)
            updateUi(it)
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

    private inner class InputHandler {
        private val nodeResult = mutableListOf<UiNode>()
        private var hoveredNode: UiNode? = null
        private var wasDrag = false
        private var dragNode: UiNode? = null

        fun handleInput(updateEvent: RenderPass.UpdateEvent) {
            val ptr = updateEvent.ctx.inputMgr.pointerState.primaryPointer
            uiHierarchy.collectNodesAt(ptr.x.toFloat(), ptr.y.toFloat(), nodeResult, hasPointerListener)

            val ptrEv = PointerEvent(ptr, updateEvent.ctx)
            hoveredNode?.let { handleHover(it, ptrEv) }
            if (nodeResult.isNotEmpty()) {
                handlePointerEvents(nodeResult, ptrEv)
            }
            dragNode?.let { handleDrag(it, ptrEv) }
        }

        private fun handleHover(currentHover: UiNode, ptrEv: PointerEvent) {
            // check if we still hover previously hovered node
            if (currentHover in nodeResult) {
                // hovering continues, hover event can be rejected, by hoverNode to stop hovering
                if (!invokeCallback(ptrEv, currentHover.modifier.pointerCallbacks.onHover, true)) {
                    hoveredNode = null
                }
            } else {
                // hovering stopped, cannot be rejected...
                currentHover.modifier.pointerCallbacks.onExit?.invoke(ptrEv)
                hoveredNode = null
            }
        }

        private fun handleDrag(currentDrag: UiNode, ptrEv: PointerEvent) {
            val ptr = ptrEv.pointer
            if (ptr.isDrag) {
                // dragging continues, drag event can be rejected, by dragNode to stop dragging
                if (!invokeCallback(ptrEv, currentDrag.modifier.pointerCallbacks.onDrag, true)) {
                    dragNode = null
                }
            } else {
                // dragging stopped, cannot be rejected...
                currentDrag.modifier.pointerCallbacks.onDragEnd?.invoke(ptrEv)
                dragNode = null
            }
        }

        fun handlePointerEvents(relevantNodes: List<UiNode>, ptrEv: PointerEvent) {
            val ptr = ptrEv.pointer

            var isWheelX = ptr.deltaScrollX != 0.0
            var isWheelY = ptr.deltaScrollY != 0.0
            val isDragStart = !wasDrag && ptr.isDrag
            var isAnyClick = ptr.isLeftButtonClicked ||
                    ptr.isRightButtonClicked ||
                    ptr.isMiddleButtonClicked ||
                    ptr.isForwardButtonClicked ||
                    ptr.isBackButtonClicked

            wasDrag = ptr.isDrag

            relevantNodes.forEach { node ->
                val cbs = node.modifier.pointerCallbacks

                // onRawPointer is called for any node below pointer position
                cbs.onRawPointer?.invoke(ptrEv)

                if (hoveredNode == null && cbs.hasAnyHoverCallback && invokeCallback(ptrEv, cbs.onEnter, true)) {
                    // no node was hovered before (or we just exited it) and we found a new one which has hover
                    // callbacks installed -> select it as new hovered node
                    hoveredNode = node
                }

                if (isDragStart && dragNode == null && cbs.hasAnyDragCallback && invokeCallback(ptrEv, cbs.onDragStart, true)) {
                    dragNode = node
                }

                if (isAnyClick && invokeCallback(ptrEv, cbs.onClick)) {
                    // click was consumed
                    isAnyClick = false
                }
                if (isWheelX && invokeCallback(ptrEv, cbs.onWheelX)) {
                    // wheel x was consumed
                    isWheelX = false
                }
                if (isWheelY && invokeCallback(ptrEv, cbs.onWheelY)) {
                    // wheel y was consumed
                    isWheelY = false
                }
            }
        }

        private fun invokeCallback(ptrEvent: PointerEvent, cb: ((PointerEvent) -> Unit)?, consumedIfNull: Boolean = false): Boolean {
            var wasConsumed = consumedIfNull
            if (cb != null) {
                // make sure consumed flag is set by default, callback has to actively reject() the
                // event to not consume it
                ptrEvent.isConsumed = true
                cb(ptrEvent)
                wasConsumed = ptrEvent.isConsumed
            }
            return wasConsumed
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