package de.fabmax.kool.modules.ui2

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.Shader
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.*

open class UiSurface(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    name: String = "uiSurface"
) : Group(name) {

    private val meshLayers = TreeMap<Int, MeshLayer>()
    private val onEachFrame = mutableListOf<(KoolContext) -> Unit>()

    protected val inputHandler = UiInputHandler()
    val viewportWidth = mutableStateOf(0f)
    val viewportHeight = mutableStateOf(0f)
    val viewport = BoxNode(null, this).apply { modifier.layout(CellLayout) }

    // colorsState and sizesState are private and use()d internally by UiSurface
    // for all other consumers the values are directly exposed
    private val colorsState = mutableStateOf(colors)
    private val sizesState = mutableStateOf(sizes)

    private val registeredState = mutableListOf<MutableState>()
    private var requiresUpdate: Boolean = true
    internal var nodeIndex = 0

    private val focusableNodes = mutableListOf<Focusable>()

    var lastInputTime = 0.0

    // top-level window scope if this UiSurface hosts a window
    var windowScope: WindowScope? = null
    val isWindowFocused: Boolean
        get() = windowScope?.windowState?.isFocused?.value ?: true

    var colors: Colors by colorsState::value
    var sizes: Sizes by sizesState::value
    var content: (UiScope.() -> Unit)? = null

    var inputMode = InputCaptureMode.CaptureInsideBounds
        set(value) {
            field = value
            if (value == InputCaptureMode.CaptureDisabled) {
                InputStack.remove(inputHandler)
            }
        }

    var printTiming = false
    var perfPrep = 0.0
    var perfCompose = 0.0
    var perfMeasure = 0.0
    var perfLayout = 0.0
    var perfRender = 0.0

    init {
        // mirror y-axis
        scale(1f, -1f, 1f)
        onUpdate += {
            UiScale.windowScale.set(it.ctx.windowScale)
            viewportWidth.set(it.renderPass.viewport.width.toFloat())
            viewportHeight.set(it.renderPass.viewport.height.toFloat())

            for (i in onEachFrame.indices) {
                onEachFrame[i](it.ctx)
            }

            inputHandler.checkInputHandler(it.ctx)
            if (requiresUpdate) {
                requiresUpdate = false
                updateUi(it)
            }
        }
        onDispose += {
            InputStack.remove(inputHandler)
        }
    }

    /**
     * Brings this surface to top inside a [DockingHost] context.
     */
    fun bringToTop() {
        // kinda hacky: docked surfaces are ordered by their time of last user input
        // give this surface an input time 1ms in the future to force it on top
        lastInputTime = Time.gameTime + 0.001

        windowScope?.let {
            it.windowState.dockedTo.value?.bringToTop(it)
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
        focusableNodes.clear()
        UiScale.updateScale(this)
        perfPrep = pt.takeMs().also { pt.reset() }

        viewport.setBounds(0f, 0f, viewportWidth.use(this), viewportHeight.use(this))
        viewport.applyDefaults()
        composeContent()
        perfCompose = pt.takeMs().also { pt.reset() }

        measureUiNodeContent(viewport, updateEvent.ctx)
        perfMeasure = pt.takeMs().also { pt.reset() }
        layoutUiNodeChildren(viewport, updateEvent.ctx)
        perfLayout = pt.takeMs().also { pt.reset() }
        renderUiNode(viewport, updateEvent.ctx)

        // re-add mesh layers in correct order
        meshLayers.values.forEach {
            if (it.isUsed) {
                +it
            } // todo: else we should dispose it probably?
        }
        perfRender = pt.takeMs().also { pt.reset() }

        if (printTiming) {
            logD { "UI update: prep: ${(perfPrep * 1000).toInt()} us, " +
                    "compose: ${(perfCompose * 1000).toInt()} us, " +
                    "measure: ${(perfMeasure * 1000).toInt()} us, " +
                    "layout: ${(perfLayout * 1000).toInt()} us, " +
                    "render: ${(perfLayout * 1000).toInt()} us" }
        }
    }

    fun onEachFrame(block: (KoolContext) -> Unit) {
        onEachFrame += block
    }

    fun requestFocus(focusable: Focusable?) {
        inputHandler.requestFocus(focusable)
    }

    fun cycleFocus(backwards: Boolean = false) {
        inputHandler.cycleFocus(backwards)
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

    fun getTextBuilder(font: Font, ctx: KoolContext, layer: Int): MeshBuilder {
        return getMeshLayer(layer).getTextBuilder(font, ctx)
    }

    fun applyFontScale(font: Font, ctx: KoolContext) {
        font.setScale(UiScale.measuredScale, ctx)
    }

    fun popup(): UiScope {
        return viewport.Box { }
    }

    private fun composeContent() {
        with(viewport) {
            sizesState.use()
            colorsState.use()

            content?.invoke(this)
        }
    }

    protected open fun measureUiNodeContent(node: UiNode, ctx: KoolContext) {
        for (i in node.children.indices) {
            measureUiNodeContent(node.children[i], ctx)
        }
        node.measureContentSize(ctx)
    }

    protected open fun layoutUiNodeChildren(node: UiNode, ctx: KoolContext) {
        node.layoutChildren(ctx)
        if (node is Focusable) {
            focusableNodes += node
        }
        for (i in node.children.indices) {
            if (node.children[i].isInClip) {
                layoutUiNodeChildren(node.children[i], ctx)
            }
        }
    }

    protected open fun renderUiNode(node: UiNode, ctx: KoolContext) {
        node.render(ctx)
        for (i in node.children.indices) {
            if (node.children[i].isInClip) {
                renderUiNode(node.children[i], ctx)
            }
        }
    }

    enum class InputCaptureMode {
        CaptureInsideBounds,
        CaptureOverBackground,
        CaptureDisabled
    }

    protected inner class UiInputHandler : InputStack.InputHandler(name ?: "UiSurface") {
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
            if (focusable != focusedNode) {
                focusedNode?.onFocusLost()
                focusedNode = focusable
                focusable?.onFocusGain()
            }
        }

        fun cycleFocus(backwards: Boolean) {
            val currentIdx = focusedNode?.let { focusableNodes.indexOf(it) } ?: -1
            if (focusableNodes.isNotEmpty()) {
                var nextIdx = currentIdx + if (backwards) -1 else 1
                if (nextIdx < 0) {
                    nextIdx += focusableNodes.size
                }
                requestFocus(focusableNodes[nextIdx % focusableNodes.size])
            }
        }

        fun checkInputHandler(ctx: KoolContext) {
            if (inputMode == InputCaptureMode.CaptureDisabled) {
                InputStack.remove(this)
                return
            }

            // keyboard input is blocked by this UiSurface as soon as a ui element is focused
            blockAllKeyboardInput = isWindowFocused && focusedNode != null

            // pointer input is blocked as soon as the pointer is above this surface OR if drag is active
            // the drag check is needed to avoid losing the pointer while dragging, e.g., a slider and accidentally
            // leaving the surface bounds
            // the other way around we do not start to block the input while drag is active when the pointer enters the
            // surface area
            val wasBlockingPointerInput = blockAllPointerInput
            blockAllPointerInput = false
            val ptr = ctx.inputMgr.pointerState.primaryPointer
            if (ptr.isValid) {
                val ptrPos = Vec2f(ptr.x.toFloat(), ptr.y.toFloat())
                var isPointerOnSurface = dragNode != null || viewport.children.any { it.isInBounds(ptrPos) }

                if (dragNode == null && inputMode == InputCaptureMode.CaptureOverBackground) {
                    nodeResult.clear()
                    viewport.collectNodesAt(ptr.x.toFloat(), ptr.y.toFloat(), nodeResult) {
                        hasPointerListener(it) || it.modifier.background != null
                    }
                    isPointerOnSurface = nodeResult.isNotEmpty()
                }

                if (isPointerOnSurface && (wasBlockingPointerInput || !ptr.isDrag)) {
                    blockAllPointerInput = true
                }
            }

            if (blockAllPointerInput || blockAllKeyboardInput) {
                InputStack.pushTop(this)
            } else {
                InputStack.remove(this)
            }
        }

        override fun handlePointer(pointerState: InputManager.PointerState, ctx: KoolContext) {
            super.handlePointer(pointerState, ctx)

            val ptr = pointerState.primaryPointer
            nodeResult.clear()
            if (ctx.inputMgr.cursorMode != InputManager.CursorMode.LOCKED) {
                viewport.collectNodesAt(ptr.x.toFloat(), ptr.y.toFloat(), nodeResult, hasPointerListener)
            }
            if (hoveredNode == null && dragNode == null && nodeResult.isEmpty()) {
                return
            }

            val ptrEv = PointerEvent(ptr, ctx)
            if (dragNode == null) {
                // only do hover if there is no drag in progress
                hoveredNode?.let { handleHover(it, ptrEv) }
            }
            if (nodeResult.isNotEmpty()) {
                handlePointerEvents(nodeResult, ptrEv)
            }
            dragNode?.let { handleDrag(it, ptrEv) }
        }

        override fun handleKeyEvents(keyEvents: MutableList<InputManager.KeyEvent>, ctx: KoolContext) {
            if (!blockAllKeyboardInput) {
                return
            }

            super.handleKeyEvents(keyEvents, ctx)

            if (keyEvents.isNotEmpty()) {
                focusedNode?.let { focusable ->
                    lastInputTime = Time.gameTime
                    for (keyEv in keyEvents) {
                        focusable.onKeyEvent(keyEv)
                    }
                }
            }
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
            lastInputTime = Time.gameTime
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

            // kind of hacky: if pointer is close to the window border any ongoing hover is stopped so that we can
            // hover (and drag / resize) the window border
            val isCloseToWindowBorder = windowScope?.let {
                it.uiNode.toLocal(ptrEv.screenPosition, ptrEv.position)
                it.getResizeBorderFlags(ptrEv.position) != 0
            } == true
            if (isCloseToWindowBorder && hoveredNode !== windowScope?.uiNode) {
                hoveredNode?.let { invokePointerCallback(it, ptrEv, it.modifier.onExit) }
                hoveredNode = null
            }

            relevantNodes.forEach { node ->
                val mod = node.modifier

                // onPointer is called for any node below pointer position
                invokePointerCallback(node, ptrEv, mod.onPointer)

                val windowResizeGuard = !isCloseToWindowBorder || node === windowScope?.uiNode

                // check for new hover nodes (if no drag is in progress)
                if (windowResizeGuard && dragNode == null && mod.hasAnyHoverCallback
                    && hoveredNode?.let { nodeComparator.compare(node, it) < 0 } != false) {
                    // stop hovering of previous hoveredNode - we found a new one on top of it
                    hoveredNode?.let { invokePointerCallback(it, ptrEv, it.modifier.onExit) }
                    // start hovering new node
                    if (invokePointerCallback(node, ptrEv, mod.onEnter, true)) {
                        hoveredNode = node
                    }
                }

                if (windowResizeGuard && isDragStart && dragNode == null && mod.hasAnyDragCallback
                    && invokePointerCallback(node, ptrEv, mod.onDragStart, true)) {
                    dragNode = node
                    lastInputTime = Time.gameTime
                }

                if (isAnyClick && invokePointerCallback(node, ptrEv, mod.onClick)) {
                    // click was consumed
                    ptrEv.pointer.consume()
                    isAnyClick = false
                    lastInputTime = Time.gameTime
                }
                if (isWheelX && invokePointerCallback(node, ptrEv, mod.onWheelX)) {
                    // wheel x was consumed
                    ptrEv.pointer.consume(InputManager.CONSUMED_SCROLL_X)
                    isWheelX = false
                }
                if (isWheelY && invokePointerCallback(node, ptrEv, mod.onWheelY)) {
                    // wheel y was consumed
                    ptrEv.pointer.consume(InputManager.CONSUMED_SCROLL_Y)
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
                uiNode.toLocal(ptrEvent.screenPosition.x, ptrEvent.screenPosition.y, ptrEvent.position)
                // make sure consumed flag is set by default, callback has to actively reject() the
                // event to not consume it
                ptrEvent.isConsumed = true
                callbacks.forEach { it.invoke(ptrEvent) }
                wasConsumed = ptrEvent.isConsumed
            }
            return wasConsumed
        }


        private fun UiNode.collectNodesAt(x: Float, y: Float, result: MutableList<UiNode>, predicate: (UiNode) -> Boolean) {
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

    private class TextMesh(shader: Shader) {
        val mesh = mesh(MsdfUiShader.MSDF_UI_MESH_ATTRIBS) {
            isCastingShadow = false
            this.shader = shader
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

        companion object {
            fun msdfTextMesh(font: MsdfFont): TextMesh {
                val shader = MsdfUiShader().apply { fontMap = font.data.map }
                return TextMesh(shader)
            }
            fun atlasTextMesh(font: AtlasFont, ctx: KoolContext): TextMesh {
                val shader = Ui2Shader().apply { setFont(font, ctx) }
                return TextMesh(shader)
            }
        }
    }

    private class ImageMeshes {
        val meshes = mutableListOf<ImageMesh>()
        var lastUsed = -1

        fun getUnusedMesh(): ImageMesh {
            lastUsed++
            return if (lastUsed < meshes.size) meshes[lastUsed] else ImageMesh().also { meshes += it }
        }

        fun clear() {
            lastUsed = -1
        }
    }

    private class CustomLayer(val drawNode: Node) {
        var isUsed = false

        fun clear() {
            isUsed = false
        }
    }

    inner class MeshLayer : Group() {
        private val msdfMeshes = mutableMapOf<MsdfFontData, TextMesh>()
        private val textMeshes = mutableMapOf<Font, TextMesh>()
        private val imageMeshes = mutableMapOf<Texture2d, ImageMeshes>()
        private val customLayers = mutableMapOf<String, CustomLayer>()
        private val plainMesh = mesh(Ui2Shader.UI_MESH_ATTRIBS) { shader = Ui2Shader() }

        val uiPrimitives = UiPrimitiveMesh()
        val plainBuilder = MeshBuilder(plainMesh.geometry).apply { isInvertFaceOrientation = true }

        var isUsed = false

        init {
            +uiPrimitives
            +plainMesh
        }

        fun getTextBuilder(font: Font, ctx: KoolContext): MeshBuilder {
            return textMeshes[font]?.builder ?: when (font) {
                is MsdfFont -> getMsdfTextBuilder(font)
                is AtlasFont -> getAtlasTextBuilder(font, ctx)
            }
        }

        private fun getMsdfTextBuilder(font: MsdfFont): MeshBuilder {
            val textMesh = msdfMeshes.getOrPut(font.data) {
                TextMesh.msdfTextMesh(font).also { this += it.mesh }
            }
            textMeshes[font] = textMesh
            textMesh.isUsed = true
            return textMesh.builder
        }

        private fun getAtlasTextBuilder(font: AtlasFont, ctx: KoolContext): MeshBuilder {
            val textMesh = textMeshes.getOrPut(font) {
                TextMesh.atlasTextMesh(font, ctx).also { this += it.mesh }
            }
            textMesh.isUsed = true
            return textMesh.builder
        }

        fun addCustomLayer(key: String, block: () -> Node): Boolean {
            val layer = customLayers.getOrPut(key) { CustomLayer(block()) }
            val isFirstUsage = !layer.isUsed
            if (isFirstUsage) {
                addNode(layer.drawNode)
            }
            layer.isUsed = true
            return isFirstUsage
        }

        fun addImage(image: Texture2d): ImageMesh {
            val imgMesh = imageMeshes.getOrPut(image) { ImageMeshes() }.getUnusedMesh()
            addNode(imgMesh)
            return imgMesh
        }

        fun clear() {
            isUsed = false
            textMeshes.values.forEach { it.clear() }
            uiPrimitives.instances?.clear()
            plainBuilder.clear()

            if (imageMeshes.isNotEmpty()) {
                imageMeshes.values.forEach {
                    intChildren -= it.meshes.toSet()
                    it.clear()
                }
            }

            if (customLayers.isNotEmpty()) {
                customLayers.values.forEach {
                    it.clear()
                    removeNode(it.drawNode)
                }
            }
        }

        override fun dispose(ctx: KoolContext) {
            super.dispose(ctx)
            imageMeshes.values.forEach { it.meshes.forEach { img -> img.dispose(ctx) } }
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