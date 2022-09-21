package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.scene.ui.Font
import de.fabmax.kool.scene.ui.FontProps
import de.fabmax.kool.util.PerfTimer
import de.fabmax.kool.util.TreeMap
import de.fabmax.kool.util.logD

class UiSurface(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium(),
    name: String = "uiSurface",
    private val uiBlock: UiScope.() -> Unit
) : Group(name) {

    private val meshLayers = TreeMap<Int, MeshLayer>()
    private val onEachFrame = mutableListOf<() -> Unit>()

    private val inputHandler = InputHandler()
    private val viewportWidth = mutableStateOf(0f)
    private val viewportHeight = mutableStateOf(0f)
    private val viewport = BoxNode(null, this).apply { modifier.layout(CellLayout) }

    internal var nodeIndex = 0

    private val registeredState = mutableListOf<MutableState>()
    var requiresUpdate: Boolean = true
        private set

    var deltaT = 0f
        private set

    var printTiming = false

    // colorsState and sizesState are private and use()d internally by UiSurface
    // for all other consumers the values are directly exposed
    private val colorsState = mutableStateOf(colors)
    private val sizesState = mutableStateOf(sizes)

    var colors: Colors by colorsState::value
    var sizes: Sizes by sizesState::value

    init {
        // mirror y-axis
        scale(1f, -1f, 1f)
        onUpdate += {
            UiScale.windowScale.set(it.ctx.windowScale)
            viewportWidth.set(it.renderPass.viewport.width.toFloat())
            viewportHeight.set(it.renderPass.viewport.height.toFloat())
            deltaT = it.deltaT

            for (i in onEachFrame.indices) {
                onEachFrame[i]()
            }

            inputHandler.handleInput(it)
            if (requiresUpdate) {
                requiresUpdate = false
                updateUi(it)
            }
        }
    }

    private fun updateUi(updateEvent: RenderPass.UpdateEvent) {
        val pt = PerfTimer()
        nodeIndex = 0
        registeredState.forEach { it.clearUsage(this) }
        registeredState.clear()
        meshLayers.values.forEach {
            it.clear()
            removeNode(it)
        }
        onEachFrame.clear()
        UiScale.updateScale(this, updateEvent.ctx)
        val prep = pt.takeMs().also { pt.reset() }

        viewport.setBounds(0f, 0f, viewportWidth.use(this), viewportHeight.use(this))
        viewport.applyDefaults()
        composeContent()
        val compose = pt.takeMs().also { pt.reset() }

        measureUiNodeContent(viewport, updateEvent.ctx)
        val measure = pt.takeMs().also { pt.reset() }
        layoutUiNodeChildren(viewport, updateEvent.ctx)
        val layout = pt.takeMs().also { pt.reset() }
        renderUiNode(viewport, updateEvent.ctx)

        // re-add mesh layers in correct order
        meshLayers.values.forEach {
            if (it.isUsed) {
                +it
            }
        }
        val render = pt.takeMs().also { pt.reset() }

        if (printTiming) {
            logD { "UI update: prep: ${(prep * 1000).toInt()} us, " +
                    "compose: ${(compose * 1000).toInt()} us, " +
                    "measure: ${(measure * 1000).toInt()} us, " +
                    "layout: ${(layout * 1000).toInt()} us, " +
                    "render: ${(render * 1000).toInt()} us" }
        }
    }

    fun onEachFrame(block: () -> Unit) {
        onEachFrame += block
    }

    fun requestFocus(focusable: Focusable?) {
        inputHandler.requestFocus(focusable)
    }

    fun registerState(state: MutableState) {
        registeredState += state
    }

    fun triggerUpdate() {
        requiresUpdate = true
    }

    fun getMeshLayer(layer: Int): MeshLayer {
        val meshLayer = meshLayers[layer] ?: MeshLayer().also { meshLayers[layer] = it }
        meshLayer.isUsed = true
        return meshLayer
    }

    fun getUiPrimitives(layer: Int): UiPrimitiveMesh {
        return getMeshLayer(layer).uiPrimitives
    }

    fun getPlainBuilder(layer: Int): MeshBuilder {
        return getMeshLayer(layer).plainBuilder
    }

    fun getTextBuilder(fontProps: FontProps, ctx: KoolContext, layer: Int): MeshBuilder {
        return getMeshLayer(layer).getTextBuilder(fontProps, ctx)
    }

    fun getFont(fontProps: FontProps, ctx: KoolContext) = UiScale.getOrCreateFont(fontProps, ctx)

    fun popup(): UiScope {
        return viewport.Box { }
    }

    private fun composeContent() {
        viewport.Box {
            modifier.background(colorsState.use().surface)
            sizesState.use()
            uiBlock()
        }
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
            if (node.children[i].isInClip) {
                layoutUiNodeChildren(node.children[i], ctx)
            }
        }
    }

    private fun renderUiNode(node: UiNode, ctx: KoolContext) {
        node.render(ctx)
        for (i in node.children.indices) {
            if (node.children[i].isInClip) {
                renderUiNode(node.children[i], ctx)
            }
        }
    }

    private inner class InputHandler {
        private val nodeResult = mutableListOf<UiNode>()
        private var focusedNode: Focusable? = null
        private var hoveredNode: UiNode? = null
        private var wasDrag = false
        private var dragNode: UiNode? = null

        private val nodeComparator = Comparator<UiNode> { a, b ->
            if (a.modifier.zLayer == b.modifier.zLayer) {
                // equal z-layers -> higher node index first
                b.nodeIndex.compareTo(a.nodeIndex)
            } else {
                // higher z-layer first
                b.modifier.zLayer.compareTo(a.modifier.zLayer)
            }
        }

        fun requestFocus(focusable: Focusable?) {
            focusedNode?.onFocusLost()
            focusedNode = focusable
            focusable?.onFocusGain()
        }

        fun handleInput(updateEvent: RenderPass.UpdateEvent) {
            handlePointerInput(updateEvent)
            handleKeyInput(updateEvent)
        }

        fun handleKeyInput(updateEvent: RenderPass.UpdateEvent) {
            if (updateEvent.ctx.inputMgr.keyEvents.isNotEmpty()) {
                focusedNode?.let { focusable ->
                    for (keyEv in updateEvent.ctx.inputMgr.keyEvents) {
                        focusable.onKeyEvent(keyEv)
                    }
                }
            }
        }

        fun handlePointerInput(updateEvent: RenderPass.UpdateEvent) {
            val ptr = updateEvent.ctx.inputMgr.pointerState.primaryPointer
            viewport.collectNodesAt(ptr.x.toFloat(), ptr.y.toFloat(), nodeResult, hasPointerListener)
            if (hoveredNode == null && dragNode == null && nodeResult.isEmpty()) {
                return
            }

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
                if (!invokePointerCallback(currentHover, ptrEv, currentHover.modifier.onHover, true)) {
                    hoveredNode = null
                }
            } else {
                // hovering stopped, cannot be rejected...
                invokePointerCallback(currentHover, ptrEv, currentHover.modifier.onExit)
                hoveredNode = null
            }
        }

        private fun handleDrag(currentDrag: UiNode, ptrEv: PointerEvent) {
            val ptr = ptrEv.pointer
            if (ptr.isDrag) {
                // dragging continues, drag event can be rejected, by dragNode to stop dragging
                if (!invokePointerCallback(currentDrag, ptrEv, currentDrag.modifier.onDrag, true)) {
                    dragNode = null
                }
            } else {
                // dragging stopped, cannot be rejected...
                invokePointerCallback(currentDrag, ptrEv, currentDrag.modifier.onDragEnd)
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
                val mod = node.modifier

                // onPointer is called for any node below pointer position
                invokePointerCallback(node, ptrEv, mod.onPointer)

                // check for new hover bodes
                if (mod.hasAnyHoverCallback && hoveredNode?.let { nodeComparator.compare(node, it) < 0 } != false) {
                    // stop hovering of previous hoveredNode - we found a new one on top of it
                    hoveredNode?.let { invokePointerCallback(it, ptrEv, it.modifier.onExit) }
                    // start hovering new node
                    if (invokePointerCallback(node, ptrEv, mod.onEnter, true)) {
                        hoveredNode = node
                    }
                }

                if (isDragStart && dragNode == null && mod.hasAnyDragCallback && invokePointerCallback(node, ptrEv, mod.onDragStart, true)) {
                    dragNode = node
                }

                if (isAnyClick && invokePointerCallback(node, ptrEv, mod.onClick)) {
                    // click was consumed
                    isAnyClick = false
                }
                if (isWheelX && invokePointerCallback(node, ptrEv, mod.onWheelX)) {
                    // wheel x was consumed
                    isWheelX = false
                }
                if (isWheelY && invokePointerCallback(node, ptrEv, mod.onWheelY)) {
                    // wheel y was consumed
                    isWheelY = false
                }
            }
        }

        private fun invokePointerCallback(
            uiNode: UiNode,
            ptrEvent: PointerEvent,
            callbacks: List<(PointerEvent) -> Unit>,
            consumedIfNull: Boolean = false
        ): Boolean {
            var wasConsumed = consumedIfNull
            if (callbacks.isNotEmpty()) {
                uiNode.toLocal(ptrEvent.pointer.x, ptrEvent.pointer.y, ptrEvent.position)
                // make sure consumed flag is set by default, callback has to actively reject() the
                // event to not consume it
                ptrEvent.isConsumed = true
                callbacks.forEach { it.invoke(ptrEvent) }
                wasConsumed = ptrEvent.isConsumed
            }
            return wasConsumed
        }


        private fun UiNode.collectNodesAt(x: Float, y: Float, result: MutableList<UiNode>, predicate: (UiNode) -> Boolean) {
            result.clear()
            if (isInClip(x, y)) {
                traverseChildren(this, x, y, result, predicate)
            }
            if (result.size > 1) {
                result.sortWith(nodeComparator)
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
            return x in clipLeftPx..clipRightPx && y in clipTopPx..clipBottomPx
        }
    }

    private class TextMesh(val font: Font, ctx: KoolContext) {
        val mesh = mesh(Ui2Shader.UI_MESH_ATTRIBS) {
            shader = Ui2Shader().apply { setFont(font, ctx) }
        }

        val builder = MeshBuilder(mesh.geometry)
        var isUsed = false

        init {
            builder.setupUiBuilder()
        }

        fun clear() {
            builder.clear()
            isUsed = false
        }
    }

    inner class MeshLayer : Group() {
        private val textMeshes = mutableMapOf<FontProps, TextMesh>()
        private val plainMesh = mesh(Ui2Shader.UI_MESH_ATTRIBS) { shader = Ui2Shader() }

        val uiPrimitives = UiPrimitiveMesh()
        val plainBuilder = MeshBuilder(plainMesh.geometry).apply { isInvertFaceOrientation = true }
        val customNodes = mutableListOf<Node>()

        var isUsed = false

        init {
            +uiPrimitives
            +plainMesh
        }

        fun getTextBuilder(fontProps: FontProps, ctx: KoolContext): MeshBuilder {
            val textMesh =  textMeshes.getOrPut(fontProps) {
                val font = UiScale.getOrCreateFont(fontProps, ctx)
                TextMesh(font, ctx).also { this += it.mesh }
            }
            textMesh.isUsed = true
            return textMesh.builder
        }

        fun addCustomNode(drawNode: Node, index: Int = -1) {
            addNode(drawNode, index)
            customNodes += drawNode
        }

        fun clear() {
            textMeshes.values.forEach { it.clear() }
            uiPrimitives.instances?.clear()
            plainBuilder.clear()

            if (customNodes.isNotEmpty()) {
                customNodes.forEach { removeNode(it) }
                customNodes.clear()
            }

            isUsed = false
        }
    }

    companion object {
        const val LAYER_DEFAULT = 0
        const val LAYER_BACKGROUND = -100
        const val LAYER_FLOATING = 100
        const val LAYER_POPUP = 1000

        fun MeshBuilder.setupUiBuilder() {
            isInvertFaceOrientation = true
        }

        private val hasPointerListener: (UiNode) -> Boolean = { it.modifier.hasAnyPointerCallback }
    }
}