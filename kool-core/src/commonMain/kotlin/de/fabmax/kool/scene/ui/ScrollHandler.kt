package de.fabmax.kool.scene.ui

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.isFuzzyEqual
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LocalPlaneClip
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.SpringDamperFloat
import kotlin.math.min
import kotlin.math.sqrt

open class ScrollHandler(val scrollTarget: UiContainer, name: String = "${scrollTarget.name}-scroll") : UiComponent(name, scrollTarget.root), Scene.DragHandler {

    var trackColor: Color = Color.GRAY.withAlpha(0.2f)
    var handleColor: Color = Color.GRAY.withAlpha(0.75f)

    val verticalTrackBounds = BoundingBox()
    val verticalHandleBounds = BoundingBox()

    val horizontalTrackBounds = BoundingBox()
    val horizontalHandleBounds = BoundingBox()

    private val scrollPosY = SpringDamperFloat(0f)
    private val scrollPosYGuard = SpringDamperFloat(0f)
    private val pickRay = Ray()

    init {
        padding.set(dps(3f))
        layoutSpec.setOrigin(zero(), zero(), zero())
        layoutSpec.setSize(scrollTarget.layoutSpec.width, scrollTarget.layoutSpec.height, scrollTarget.layoutSpec.depth)

        scrollPosY.stiffness = 300f
        scrollPosYGuard.stiffness = 500f
    }

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        oldScene?.removeDragHandler(this)
        newScene?.registerDragHandler(this)
    }

    override fun createThemeUi(ctx: KoolContext): ComponentUi {
        return root.theme.newScrollHandlerUi(this)
    }

    override fun setDrawBoundsFromWrappedComponentBounds(parentContainer: UiContainer?, ctx: KoolContext) {
        drawBounds.set(scrollTarget.drawBounds)
        bounds.set(drawBounds)
    }

//    override fun setThemeProps(ctx: KoolContext) {
//        super.setThemeProps(ctx)
//        handleColor = root.theme.accentColor.withAlpha(0.4f)
//    }

    override fun handleDrag(dragPtrs: List<InputManager.Pointer>, scene: Scene, ctx: KoolContext) {
        if (alpha == 0f) {
            // don't handle drag if component is hidden
            return
        }
        if (dragPtrs.isNotEmpty() && !dragPtrs[0].isConsumed() &&
                computeLocalPickRay(dragPtrs[0], ctx, pickRay) &&
                bounds.hitDistanceSqr(pickRay) < Float.MAX_VALUE) {
            scrollPosY.desired += dragPtrs[0].deltaScroll * 50
            dragPtrs[0].consume()
        }

        val max = 0f
        if (scrollPosY.desired > max) {
            if (scrollPosY.desired > scrollPosYGuard.actual) {
                scrollPosYGuard.actual = scrollPosY.desired
            }
            scrollPosYGuard.desired = max
            scrollPosY.desired = scrollPosYGuard.animate(ctx.deltaT)
        }

        val min = scrollTarget.drawBounds.size.y - scrollTarget.contentBounds.size.y
        if (scrollPosY.desired < min) {
            if (scrollPosY.desired < scrollPosYGuard.actual) {
                scrollPosYGuard.actual = scrollPosY.desired
            }
            scrollPosYGuard.desired = min
            scrollPosY.desired = scrollPosYGuard.animate(ctx.deltaT)
        }

        scrollPosY.animate(ctx.deltaT)
        if (!isFuzzyEqual(scrollTarget.scrollOffset.y, scrollPosY.actual)) {
            scrollTarget.setScrollOffset(0f, scrollPosY.actual, 0f)
        }
    }

    override fun rayTest(test: RayTest) {
        if (alpha != 0f) {
            val distSqr = min(verticalTrackBounds.hitDistanceSqr(test.ray), horizontalTrackBounds.hitDistanceSqr(test.ray))
            if (distSqr < Float.MAX_VALUE && distSqr <= test.hitDistanceSqr) {
                test.setHit(this, sqrt(distSqr))
            }
        }
    }
}

open class ScrollHandlerUi(val scrollHandler: ScrollHandler) : ComponentUi {

    protected val meshData = MeshData(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS)
    protected val meshBuilder = MeshBuilder(meshData)
    protected val mesh = Mesh(meshData)

    override fun updateComponentAlpha() {
        val shader = mesh.shader
        if (shader is BasicShader) {
            shader.alpha = scrollHandler.alpha
        }
    }

    override fun createUi(ctx: KoolContext) {
        mesh.shader = basicShader {
            lightModel = scrollHandler.root.shaderLightModel
            colorModel = ColorModel.VERTEX_COLOR
            isAlpha = true
            clipMethod = LocalPlaneClip(6)
        }
        scrollHandler += mesh
    }

    override fun updateUi(ctx: KoolContext) {
        meshBuilder.clear()
        meshBuilder.identity()

        computeTrackBounds()
        drawVerticalBar()
        drawHorizontalBar()
    }

    protected open fun computeTrackBounds() {
        val target = scrollHandler.scrollTarget
        val showVertBar = target.drawBounds.size.y < target.contentBounds.size.y
        val showHoriBar = target.drawBounds.size.x < target.contentBounds.size.x

        val width = target.drawBounds.size.x
        val height = target.drawBounds.size.y

        val paddingT = scrollHandler.padding.top.toUnits(height, scrollHandler.dpi)
        val paddingB = scrollHandler.padding.bottom.toUnits(height, scrollHandler.dpi)
        val paddingL = scrollHandler.padding.left.toUnits(width, scrollHandler.dpi)
        val paddingR = scrollHandler.padding.right.toUnits(width, scrollHandler.dpi)
        val trackW = scrollHandler.dp(6f)
        val trackZ = scrollHandler.dp(.1f)
        val handleZ = scrollHandler.dp(.25f)

        if (showVertBar) {
            val x = width - paddingR - trackW
            val y = scrollHandler.padding.bottom.toUnits(height, scrollHandler.dpi) +
                    if (showHoriBar) { trackW + paddingR } else { 0f }
            val w = trackW
            val h = height - paddingT - paddingB -
                    if (showHoriBar) { trackW + paddingR } else { 0f }
            scrollHandler.verticalTrackBounds.set(x, y, trackZ, x + w, y + h, trackZ)
                    .move(target.drawBounds.min)

            val handleH = target.drawBounds.size.y / target.contentBounds.size.y * h
            val handleHClamped = handleH.clamp(scrollHandler.dp(12f), h)
            val trackHMod = h - (handleHClamped - handleH).clamp(0f, h)
            val handleY = (target.drawBounds.min.y - target.contentBounds.min.y) / target.contentBounds.size.y * trackHMod + y
            val handleYClamped = handleY.clamp(y, y + h - handleHClamped)
            scrollHandler.verticalHandleBounds.set(x, handleYClamped, handleZ, x + w, handleYClamped + handleHClamped, handleZ)
                    .move(target.drawBounds.min)

        } else {
            scrollHandler.verticalTrackBounds.clear()
            scrollHandler.verticalHandleBounds.clear()
        }

        if (showHoriBar) {
            val x = paddingL
            val y = paddingB
            val w = scrollHandler.width - paddingL - paddingR - if (showVertBar) { trackW + paddingR } else { 0f }
            val h = trackW
            scrollHandler.horizontalTrackBounds.set(x, y, trackZ, x + w, y + h, trackZ)
                    .move(target.drawBounds.min)

            val handleW = target.drawBounds.size.x / target.contentBounds.size.x * w
            val handleWClamped = handleW.clamp(scrollHandler.dp(12f), w)
            val trackWMod = w - (handleWClamped - handleW).clamp(0f, w)
            val handleX = (target.drawBounds.min.x - target.contentBounds.min.x) / target.contentBounds.size.x * trackWMod + x
            val handleXClamped = handleX.clamp(x, x + w - handleWClamped)
            scrollHandler.horizontalHandleBounds.set(handleXClamped, y, handleZ, handleXClamped + handleWClamped, y + h, handleZ)
                    .move(target.drawBounds.min)

        } else {
            scrollHandler.horizontalTrackBounds.clear()
            scrollHandler.horizontalHandleBounds.clear()
        }
    }

    protected open fun drawVerticalBar() {
        drawDefaultBar(scrollHandler.verticalTrackBounds, scrollHandler.verticalHandleBounds)
    }

    protected open fun drawHorizontalBar() {
        drawDefaultBar(scrollHandler.horizontalTrackBounds, scrollHandler.horizontalHandleBounds)
    }

    private fun drawDefaultBar(track: BoundingBox, handle: BoundingBox) {
        if (!track.isEmpty) {
            meshBuilder.color = scrollHandler.trackColor
            meshBuilder.rect {
                origin.set(track.min.x, track.min.y, track.min.z)
                size.set(track.size.x, track.size.y)
                cornerRadius = min(track.size.x, track.size.y)/ 2f
                cornerSteps = 4
            }
        }
        if (!handle.isEmpty) {
            meshBuilder.color = scrollHandler.handleColor
            meshBuilder.rect {
                origin.set(handle.min.x, handle.min.y, handle.min.z)
                size.set(handle.size.x, handle.size.y)
                cornerRadius = min(handle.size.x, handle.size.y)/ 2f
                cornerSteps = 4
            }
        }
    }

    override fun onRender(ctx: KoolContext) {
        mesh.shader?.setDrawBounds(scrollHandler.scrollTarget.drawBounds)
    }

    override fun dispose(ctx: KoolContext) {
        scrollHandler -= mesh
        mesh.dispose(ctx)
    }
}
