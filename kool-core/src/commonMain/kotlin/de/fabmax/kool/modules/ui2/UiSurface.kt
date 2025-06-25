package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.*
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ui2.docking.Dock
import de.fabmax.kool.pipeline.DrawShader
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.Usage
import de.fabmax.kool.util.*

open class UiSurface(
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    name: String = "uiSurface"
) : Node(name) {

    constructor(
        colors: Colors = Colors.darkColors(),
        sizes: Sizes = Sizes.medium,
        name: String = "uiSurface",
        block: UiScope.() -> Unit
    ) : this(colors, sizes, name) {
        content = block
    }

    private val meshLayers = SortedMap<Int, MeshLayer>()
    private val onEachFrame = mutableListOf<(KoolContext) -> Unit>()

    val inputHandler = UiInputHandler()
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

    val onCompose = mutableListOf<() -> Unit>()

    var lastInputTime = 0.0
    val isFocused = mutableStateOf(false).onChange { _, new -> if (new) setFocusedSurface(this) }

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
        transform.scale(mirrorTransformScale)
        onUpdate {
            viewportWidth.set(it.viewport.width.toFloat())
            viewportHeight.set(it.viewport.height.toFloat())

            for (i in onEachFrame.indices) {
                onEachFrame[i](it.ctx)
            }

            if (isVisible) {
                if (PointerInput.cursorMode == CursorMode.NORMAL) {
                    inputHandler.checkInputHandler(it.ctx)
                    InputStack.updateHandlerStack()
                }

                if (requiresUpdate) {
                    requiresUpdate = false
                    updateUi(it)
                }

                if (isFocused.value) {
                    val focused = inputHandler.focusedNode as? UiNode?
                    focused?.let { focusedNd ->
                        val ptr = PointerInput.primaryPointer
                        if (ptr.isAnyButtonPressed) {
                            if (!focusedNd.isInBounds(ptr.pos)) {
                                requestFocus(null)
                            }
                        }
                    }
                }
            } else {
                InputStack.remove(inputHandler)
            }
        }
        onRelease {
            InputStack.remove(inputHandler)
        }
    }

    override fun release() {
        super.release()
        // release unused mesh layers - they are detached from this node and not released by Node.release()
        meshLayers.values.filter { it !in children }.forEach { it.release() }
    }

    fun onCompose(block: () -> Unit) {
        onCompose += block
    }

    /**
     * Checks whether this [UiSurface] is on top (i.e. visible) at the specified screen position within a
     * [Dock] context. For this to work this surface needs to be a child of [Dock]. If no parent [Dock] is found,
     * true is returned.
     */
    fun isOnTop(screenPositionPx: Vec2f): Boolean {
        val parentDock = findParentOfType<Dock>() ?: return true
        return parentDock.isSurfaceOnTop(this, screenPositionPx)
    }

    private fun updateUi(updateEvent: RenderPass.UpdateEvent) {
        val pt = PerfTimer()
        onCompose.forEach { it() }

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
                addNode(it)
            }
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

        // todo: replace this by something better
        //  - other nodes may want to show keyboard as well
        //  - non-editable text fields / text areas should not show the keyboard
        if (focusable is TextFieldNode || focusable is TextAreaNode) {
            KeyboardInput.requestKeyboard()
        } else {
            KeyboardInput.hideKeyboard()
        }
    }

    fun unfocus(focusable: Focusable) {
        inputHandler.unfocus(focusable)
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
        val meshLayer = meshLayers[layer] ?: MeshLayer("$name/MeshLayer[$layer]").also { meshLayers[layer] = it }
        meshLayer.isUsed = true
        return meshLayer
    }

    fun getUiPrimitives(layer: Int): UiPrimitiveMesh {
        return getMeshLayer(layer).uiPrimitives
    }

    fun getPlainBuilder(layer: Int): MeshBuilder {
        return getMeshLayer(layer).plainBuilder
    }

    fun getTextBuilder(font: Font, layer: Int): MeshBuilder {
        return getMeshLayer(layer).getTextBuilder(font)
    }

    fun applyFontScale(font: Font, ctx: KoolContext) {
        font.setScale(UiScale.measuredScale, ctx)
    }

    fun popup(scopeName: String? = null): UiScope {
        return viewport.Box(scopeName = scopeName) { }
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
        /**
         * Pointer input is captured / blocked as soon as the pointer enters the bounding box of any node of this
         * surface. This is fine for most cases but can cause unwanted pointer capture if multiple panels are layouted
         * in an invisible box hierarchy (use [CaptureOverBackground] in that case).
         */
        CaptureInsideBounds,

        /**
         * Similar to [CaptureInsideBounds] but also checks if the child node has a background set.
         */
        CaptureOverBackground,

        /**
         * Similar to [CaptureInsideBounds] but never blocks pointer input. This way, non-consumed pointer events
         * can be processed by following input handlers. However, this also means that used pointer events have to
         * be explicitly consumed via [Pointer.consume].
         */
        CapturePassthrough,

        /**
         * Pointer is not used at all.
         */
        CaptureDisabled
    }

    inner class UiInputHandler : InputStack.InputHandler(name) {
        private val nodeResult = mutableListOf<UiNode>()
        private var hoveredNode: UiNode? = null
        private var wasDrag = false
        private var dragNode: UiNode? = null

        internal var focusedNode: Focusable? = null

        private var isCapturePointer = false

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

        fun unfocus(focusable: Focusable) {
            if (focusedNode === focusable) {
                requestFocus(null)
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
            blockAllKeyboardInput = isFocused.value && focusedNode != null

            // pointer input is blocked as soon as the pointer is above this surface OR if drag is active
            // the drag check is needed to avoid losing the pointer while dragging, e.g., a slider and accidentally
            // leaving the surface bounds
            // the other way around we do not start to block the input while drag is active when the pointer enters the
            // surface area
            val wasBlockingPointerInput = isCapturePointer
            isCapturePointer = false
            val ptr = PointerInput.primaryPointer
            var isPointerOnSurface = false
            if (ptr.isValid) {
                isPointerOnSurface = dragNode != null || viewport.children.any { it.modifier.isBlocking && it.isInBounds(ptr.pos) }

                if (dragNode == null && inputMode == InputCaptureMode.CaptureOverBackground) {
                    nodeResult.clear()
                    viewport.collectNodesAt(ptr.pos.x, ptr.pos.y, nodeResult) {
                        hasPointerListener(it) || it.modifier.background != null
                    }
                    isPointerOnSurface = nodeResult.isNotEmpty()
                }

                if (isPointerOnSurface && (wasBlockingPointerInput || !ptr.isDrag)) {
                    isCapturePointer = true
                }
            }

            if (!isPointerOnSurface) {
                hoveredNode?.let { stopHover(it, PointerEvent(ptr, ctx)) }
            }
            if (dragNode != null && !ptr.isDrag) {
                dragNode?.let { stopDrag(it, PointerEvent(ptr, ctx)) }
            }

            blockAllPointerInput = if (isCapturePointer && inputMode != InputCaptureMode.CapturePassthrough) {
                isCapturePointer
            } else {
                false
            }

            if (isCapturePointer || blockAllKeyboardInput) {
                InputStack.pushTop(this)
            } else {
                InputStack.remove(this)
            }
        }

        override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
            super.handlePointer(pointerState, ctx)

            val ptr = pointerState.primaryPointer
            nodeResult.clear()
            if (PointerInput.cursorMode != CursorMode.LOCKED) {
                viewport.collectNodesAt(ptr.pos.x, ptr.pos.y, nodeResult, hasPointerListener)
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

        override fun handleKeyEvents(keyEvents: MutableList<KeyEvent>, ctx: KoolContext) {
            if (!blockAllKeyboardInput) {
                return
            }

            super.handleKeyEvents(keyEvents, ctx)

            if (keyEvents.isNotEmpty()) {
                focusedNode?.let { focusable ->
                    onInput()
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
                stopHover(currentHover, ptrEv)
            }
        }

        private fun handleDrag(currentDrag: UiNode, ptrEv: PointerEvent) {
            onInput()
            val ptr = ptrEv.pointer
            if (ptr.isDrag) {
                // dragging continues, drag event can be rejected, by dragNode to stop dragging
                if (!invokePointerCallback(currentDrag, ptrEv, currentDrag.modifier.onDrag, true)) {
                    dragNode = null
                }
            } else {
                // dragging stopped, cannot be rejected...
                stopDrag(currentDrag, ptrEv)
            }
        }

        private fun stopHover(currentHover: UiNode, ptrEv: PointerEvent) {
            invokePointerCallback(currentHover, ptrEv, currentHover.modifier.onExit)
            hoveredNode = null
        }

        private fun stopDrag(currentDrag: UiNode, ptrEv: PointerEvent) {
            invokePointerCallback(currentDrag, ptrEv, currentDrag.modifier.onDragEnd)
            dragNode = null
        }

        fun handlePointerEvents(relevantNodes: List<UiNode>, ptrEv: PointerEvent) {
            val ptr = ptrEv.pointer

            var isWheelX = ptr.scroll.x != 0f
            var isWheelY = ptr.scroll.y != 0f
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

                // check for new hover nodes (if no drag is in progress)
                if (dragNode == null && mod.hasAnyHoverCallback
                    && hoveredNode?.let { nodeComparator.compare(node, it) < 0 } != false) {
                    // stop hovering of previous hoveredNode - we found a new one on top of it
                    hoveredNode?.let { invokePointerCallback(it, ptrEv, it.modifier.onExit) }
                    // start hovering new node
                    if (invokePointerCallback(node, ptrEv, mod.onEnter, true)) {
                        hoveredNode = node
                    }
                }

                if (isDragStart && dragNode == null && mod.hasAnyDragCallback
                    && invokePointerCallback(node, ptrEv, mod.onDragStart, true)) {
                    dragNode = node
                    onInput()
                }

                if (isAnyClick && invokePointerCallback(node, ptrEv, mod.onClick)) {
                    // click was consumed
                    ptrEv.pointer.consume()
                    isAnyClick = false
                    onInput()
                }
                if (isWheelX && invokePointerCallback(node, ptrEv, mod.onWheelX)) {
                    // wheel x was consumed
                    ptrEv.pointer.consume(PointerInput.CONSUMED_SCROLL_X)
                    isWheelX = false
                }
                if (isWheelY && invokePointerCallback(node, ptrEv, mod.onWheelY)) {
                    // wheel y was consumed
                    ptrEv.pointer.consume(PointerInput.CONSUMED_SCROLL_Y)
                    isWheelY = false
                }
            }
        }

        private fun onInput() {
            lastInputTime = Time.gameTime
            isFocused.set(true)
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

    private class TextMesh(shader: DrawShader, name: String) {
        val mesh = Mesh(MsdfUiShader.MSDF_UI_MESH_ATTRIBS, name = name).apply {
            isCastingShadow = false
            geometry.usage = Usage.DYNAMIC
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
            fun msdfTextMesh(font: MsdfFont, name: String): TextMesh {
                val shader = MsdfUiShader().apply { fontMap = font.data.map }
                return TextMesh(shader, name)
            }
            fun atlasTextMesh(font: AtlasFont, name: String): TextMesh {
                val shader = Ui2Shader().apply { setFont(font) }
                return TextMesh(shader, name)
            }
        }
    }

    private class ImageMeshes(val imageName: String) {
        val meshes = mutableListOf<ImageMesh>()
        var lastUsed = -1

        fun getUnusedMesh(): ImageMesh {
            lastUsed++
            return if (lastUsed < meshes.size) {
                meshes[lastUsed]
            } else {
                ImageMesh("UiImageMesh-${meshes.size+1}[$imageName]").also { meshes += it }
            }
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

    inner class MeshLayer(name: String) : Node(name) {
        private val msdfMeshes = mutableMapOf<MsdfFontData, TextMesh>()
        private val textMeshes = mutableMapOf<Font, TextMesh>()
        private val imageMeshes = mutableMapOf<Texture2d, ImageMeshes>()
        private val customLayers = mutableMapOf<String, CustomLayer>()
        private val plainMesh = Mesh(Ui2Shader.UI_MESH_ATTRIBS, name = "$name.plainMesh").apply { shader = Ui2Shader() }

        val uiPrimitives = UiPrimitiveMesh("$name/UiPrimitiveMesh")
        val plainBuilder = MeshBuilder(plainMesh.geometry).apply { isInvertFaceOrientation = true }

        var isUsed = false

        init {
            addNode(uiPrimitives)
            addNode(plainMesh)
            plainMesh.geometry.usage = Usage.DYNAMIC
        }

        fun getTextBuilder(font: Font): MeshBuilder {
            return textMeshes[font]?.builder ?: when (font) {
                is MsdfFont -> getMsdfTextBuilder(font)
                is AtlasFont -> getAtlasTextBuilder(font)
            }
        }

        private fun getMsdfTextBuilder(font: MsdfFont): MeshBuilder {
            val textMesh = msdfMeshes.getOrPut(font.data) {
                TextMesh.msdfTextMesh(font, "$name.msdfTextMesh:${font.data.meta.name}").also { this += it.mesh }
            }
            textMeshes[font] = textMesh
            textMesh.isUsed = true
            return textMesh.builder
        }

        private fun getAtlasTextBuilder(font: AtlasFont): MeshBuilder {
            val textMesh = textMeshes.getOrPut(font) {
                TextMesh.atlasTextMesh(font, "$name.atlasTextMesh:${font.family}").also { this += it.mesh }
            }
            textMesh.isUsed = true
            return textMesh.builder
        }

        fun addCustomLayer(key: String, order: Int = -1, block: () -> Node): Boolean {
            val layer = customLayers.getOrPut(key) { CustomLayer(block()) }
            val isFirstUsage = !layer.isUsed
            if (isFirstUsage) {
                addNode(layer.drawNode, order)
            }
            layer.isUsed = true
            return isFirstUsage
        }

        fun addImage(image: Texture2d, order: Int = -1): ImageMesh {
            val imgMesh = imageMeshes.getOrPut(image) { ImageMeshes(image.name) }.getUnusedMesh()
            addNode(imgMesh, order)
            return imgMesh
        }

        fun clear() {
            isUsed = false
            textMeshes.values.forEach { it.clear() }
            uiPrimitives.instances?.clear()
            plainBuilder.clear()

            if (imageMeshes.isNotEmpty()) {
                imageMeshes.values.forEach {
                    mutChildren -= it.meshes.toSet()
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

        override fun release() {
            super.release()
            imageMeshes.values.forEach { it.meshes.forEach { img -> img.release() } }
        }
    }

    companion object {
        const val LAYER_DEFAULT = 0
        const val LAYER_BACKGROUND = -100
        const val LAYER_FLOATING = 100
        const val LAYER_POPUP = 1000

        private val mirrorTransformScale = Vec3f(1f, -1f, 1f)

        fun MeshBuilder.setupUiBuilder() {
            isInvertFaceOrientation = true
        }

        private val hasPointerListener: (UiNode) -> Boolean = { it.modifier.hasAnyPointerCallback }

        private var focusedSurface: UiSurface? = null
        private fun setFocusedSurface(surface: UiSurface?) {
            if (surface != focusedSurface) {
                focusedSurface?.isFocused?.set(false)
                focusedSurface = surface

                surface?.let {
                    it.isFocused.set(true)
                    it.lastInputTime = Time.gameTime
                }
            }
        }
    }
}