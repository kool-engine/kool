package de.fabmax.kool.scene

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.DepthMode
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.backend.DepthRange
import de.fabmax.kool.util.LazyMat4d
import de.fabmax.kool.util.LazyMat4f
import de.fabmax.kool.util.Viewport
import de.fabmax.kool.util.logW
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.tan

/**
 * @author fabmax
 */
abstract class Camera(name: String = "camera") : Node(name) {

    /**
     * Camera position within the camera [transform] frame.
     */
    val position = MutableVec3f(0f, 0f, 1f)

    /**
     * Camera look-at position within the camera [transform] frame. Camera look direction is determined by
     * norm(lookAt - position).
     */
    val lookAt = MutableVec3f(Vec3f.ZERO)

    /**
     * Camera up direction within the camera [transform] frame. Usually, the default value of [Vec3f.Y_AXIS] should
     * work fine. But, the up-direction must not be collinear to the look direction, i.e. a different up direction
     * is needed if the camera looks exactly up- or downwards.
     */
    val up = MutableVec3f(Vec3f.Y_AXIS)

    var aspectRatio = 1.0f
    var useViewportAspectRatio = true

    val clip = MutableVec2f(0.1f, 1e9f)
    var clipNear: Float by clip::x
    var clipFar: Float by clip::y

    /**
     * Projection specific factors to convert linear depth to view space position (primarily used in shaders via
     * [de.fabmax.kool.modules.ksl.blocks.CameraData]).
     */
    val viewParams = MutableVec4f()

    val isZeroToOneDepth: Boolean
        get() = KoolSystem.getContextOrNull()?.backend?.depthRange == DepthRange.ZERO_TO_ONE
    var isReverseDepthProjection = false
        protected set

    val proj = MutableMat4f()
    private val lazyInvProj = LazyMat4f { proj.invert(it) }
    val invProj: Mat4f get() = lazyInvProj.get()

    val dataF = DataF()
    val dataD = DataD()

    val globalPos: Vec3f get() = dataF.globalPos
    val globalLookAt: Vec3f get() = dataF.globalLookAt
    val globalUp: Vec3f get() = dataF.globalUp
    val globalRight: Vec3f get() = dataF.globalRight
    val globalLookDir: Vec3f get() = dataF.globalLookDir
    val globalRange: Float get() = dataF.globalRange

    val view: Mat4f get() = dataF.view
    val viewProj: Mat4f get() = dataF.viewProj
    val invView: Mat4f get() = dataF.lazyInvView.get()
    val invViewProj: Mat4f get() = dataF.lazyInvViewProj.get()

    // we need a bunch of temporary vectors, keep them as members (#perfmatters)
    private val tmpVec3 = MutableVec3f()
    private val tmpVec4 = MutableVec4f()
    private val tmpVec3d = MutableVec3d()
    private val tmpVec4d = MutableVec4d()

    val onCameraUpdated = mutableListOf<(RenderPass.UpdateEvent) -> Unit>()

    fun setupCamera(position: Vec3f? = null, up: Vec3f? = null, lookAt: Vec3f? = null) {
        position?.let { this.position.set(it) }
        up?.let { this.up.set(up) }
        lookAt?.let { this.lookAt.set(it) }
    }

    fun setClipRange(near: Float, far: Float) {
        clipNear = near
        clipFar = far
    }

    open fun updateCamera(updateEvent: RenderPass.UpdateEvent) {
        isReverseDepthProjection = updateEvent.renderPass.depthMode == DepthMode.Reversed
        if (useViewportAspectRatio) {
            aspectRatio = updateEvent.view.viewport.aspectRatio
        }

        updateProjectionMatrix(updateEvent)
        lazyInvProj.isDirty = true
        updateViewMatrix(updateEvent)

        if (onCameraUpdated.isNotEmpty()) {
            for (i in onCameraUpdated.indices) {
                onCameraUpdated[i](updateEvent)
            }
        }
    }

    open fun updateViewMatrix(updateEvent: RenderPass.UpdateEvent) {
        if (updateEvent.renderPass.isDoublePrecision) {
            dataD.updateView()
            dataF.set(dataD)
        } else {
            dataF.updateView()
            dataD.set(dataF)
        }
    }

    abstract fun updateProjectionMatrix(updateEvent: RenderPass.UpdateEvent)

    fun computePickRay(pickRay: RayF, ptr: Pointer, viewport: Viewport): Boolean {
        return ptr.isValid && computePickRay(pickRay, ptr.pos.x, ptr.pos.y, viewport)
    }

    fun computePickRay(pickRay: RayF, screenX: Float, screenY: Float, viewport: Viewport): Boolean {
        var valid = unProjectScreen(tmpVec3.set(screenX, screenY, 0f), viewport, pickRay.origin)
        valid = valid && unProjectScreen(tmpVec3.set(screenX, screenY, 1f), viewport, pickRay.direction)

        if (valid) {
            pickRay.direction.subtract(pickRay.origin)
            pickRay.direction.norm()
        }

        return valid
    }

    fun computePickRay(pickRay: RayD, ptr: Pointer, viewport: Viewport): Boolean {
        return ptr.isValid && computePickRay(pickRay, ptr.pos.x, ptr.pos.y, viewport)
    }

    fun computePickRay(pickRay: RayD, screenX: Float, screenY: Float, viewport: Viewport): Boolean {
        var valid = unProjectScreen(tmpVec3d.set(screenX.toDouble(), screenY.toDouble(), 0.0), viewport, pickRay.origin)
        valid = valid && unProjectScreen(tmpVec3d.set(screenX.toDouble(), screenY.toDouble(), 1.0), viewport, pickRay.direction)
        if (valid) {
            pickRay.direction.subtract(pickRay.origin)
            pickRay.direction.norm()
        }
        return valid
    }

    fun initRayTes(rayTest: RayTest, ptr: Pointer, viewport: Viewport): Boolean {
        return ptr.isValid && initRayTes(rayTest, ptr.pos.x, ptr.pos.y, viewport)
    }

    fun initRayTes(rayTest: RayTest, screenX: Float, screenY: Float, viewport: Viewport): Boolean {
        rayTest.clear()
        return computePickRay(rayTest.ray, screenX, screenY, viewport)
    }

    abstract fun computeFrustumPlane(z: Float, result: FrustumPlane)

    /**
     * Tests if the node is inside the view frustum of this camera. For performance reasons the node's bounding sphere
     * is used instead of the bounding box. [Node.globalCenter] and [Node.globalRadius] properties of the tested node
     * must be valid.
     */
    open fun isInFrustum(node: Node): Boolean = isInFrustum(node.globalCenter, node.globalRadius)

    abstract fun isInFrustum(globalCenter: Vec3f, globalRadius: Float): Boolean

    fun project(world: Vec3f, result: MutableVec3f): Boolean {
        tmpVec4.set(world.x, world.y, world.z, 1f)
        viewProj.transform(tmpVec4)
        if (tmpVec4.w.isFuzzyZero()) {
            result.set(Vec3f.ZERO)
            return false
        }
        result.set(tmpVec4.x, tmpVec4.y, tmpVec4.z).mul(1f / tmpVec4.w)
        return result.x in -1f..1f && result.y in -1f..1f && result.z in -1f..1f
    }

    fun project(world: Vec3f, result: MutableVec4f): MutableVec4f =
        viewProj.transform(result.set(world.x, world.y, world.z, 1f))

    fun projectViewport(world: Vec3f, viewport: Viewport, result: MutableVec3f): Boolean {
        val projectOk = project(world, result)
        result.x = (1f + result.x) * 0.5f * viewport.width
        result.y = (1f - (1f + result.y) * 0.5f) * viewport.height
        result.z = (1f + result.z) * 0.5f
        return projectOk
    }

    fun projectScreen(world: Vec3f, viewport: Viewport, result: MutableVec3f): Boolean {
        val projectOk = projectViewport(world, viewport, result)
        result.x += viewport.x
        result.y += viewport.y
        return projectOk
    }

    open fun unProjectScreen(screen: Vec3f, viewport: Viewport, result: MutableVec3f): Boolean {
        if (unProjectScreen(tmpVec3d.set(screen), viewport, tmpVec3d)) {
            result.set(tmpVec3d)
            return true
        }
        return false
    }

    fun project(world: Vec3d, result: MutableVec3d): Boolean {
        tmpVec4d.set(world.x, world.y, world.z, 1.0)
        dataD.viewProj.transform(tmpVec4d)
        if (tmpVec4d.w.isFuzzyZero()) {
            result.set(Vec3d.ZERO)
            return false
        }
        result.set(tmpVec4d.x, tmpVec4d.y, tmpVec4d.z).mul(1f / tmpVec4d.w)
        return result.x in -1.0..1.0 && result.y in -1.0..1.0 && result.z in -1.0..1.0
    }

    fun project(world: Vec3d, result: MutableVec4d): MutableVec4d =
        dataD.viewProj.transform(result.set(world.x, world.y, world.z, 1.0))

    fun projectViewport(world: Vec3d, viewport: Viewport, result: MutableVec3d): Boolean {
        val projectOk = project(world, result)
        result.x = (1.0 + result.x) * 0.5 * viewport.width
        result.y = (1.0 - (1.0 + result.y) * 0.5) * viewport.height
        result.z = (1.0 + result.z) * 0.5
        return projectOk
    }

    fun projectScreen(world: Vec3d, viewport: Viewport, result: MutableVec3d): Boolean {
        val projectOk = projectViewport(world, viewport, result)
        result.x += viewport.x
        result.y += viewport.y
        return projectOk
    }

    open fun unProjectScreen(screen: Vec3d, viewport: Viewport, result: MutableVec3d): Boolean {
        val viewX = screen.x - viewport.x
        val viewY = viewport.y + viewport.height - screen.y
        val x = 2f * viewX / viewport.width - 1f
        val y = 2f * viewY / viewport.height - 1f
        val z = if (isZeroToOneDepth) screen.z else 2f * screen.z - 1f

        if (isReverseDepthProjection) {
            val w = clipFar * z
            tmpVec4d.set(x * w, y * w, 1.0, w)
        } else {
            tmpVec4d.set(x, y, z, 1.0)
        }
        dataD.lazyInvViewProj.get().transform(tmpVec4d)
        val s = 1.0 / tmpVec4d.w
        result.set(tmpVec4d.x * s, tmpVec4d.y * s, tmpVec4d.z * s)
        return true
    }

    inner class DataF {
        val globalPos = MutableVec3f()
        val globalLookAt = MutableVec3f()
        val globalUp = MutableVec3f()
        val globalRight = MutableVec3f()
        val globalLookDir = MutableVec3f()
        var globalRange = 0f

        val view = MutableMat4f()
        val viewProj = MutableMat4f()

        val lazyInvView = LazyMat4f { view.invert(it) }
        val lazyInvViewProj = LazyMat4f { viewProj.invert(it) }

        fun set(dataD: DataD) {
            globalPos.set(dataD.globalPos)
            globalLookAt.set(dataD.globalLookAt)
            globalUp.set(dataD.globalUp)
            globalRight.set(dataD.globalRight)
            globalLookDir.set(dataD.globalLookDir)
            view.set(dataD.view)
            viewProj.set(dataD.viewProj)
            globalRange = dataD.globalRange.toFloat()

            lazyInvView.isDirty = true
            lazyInvViewProj.isDirty = true
        }

        fun updateView() {
            toGlobalCoords(globalPos.set(position))
            toGlobalCoords(globalLookAt.set(lookAt))
            toGlobalCoords(globalUp.set(up), 0f).norm()

            globalLookDir.set(globalLookAt).subtract(globalPos)
            globalRange = globalLookDir.length()
            globalLookDir.mul(1f / globalRange)

            globalLookDir.cross(globalUp, globalRight).norm()
            globalRight.cross(globalLookDir, globalUp).norm()

            view.setIdentity().lookAt(globalPos, globalLookAt, globalUp)
            proj.mul(view, viewProj)

            lazyInvView.isDirty = true
            lazyInvViewProj.isDirty = true
        }
    }

    inner class DataD {
        val globalPos = MutableVec3d()
        val globalLookAt = MutableVec3d()
        val globalUp = MutableVec3d()
        val globalRight = MutableVec3d()
        val globalLookDir = MutableVec3d()
        var globalRange = 0.0

        val view = MutableMat4d()
        val viewProj = MutableMat4d()

        private val tmpProjD = MutableMat4d()
        val lazyInvView = LazyMat4d { view.invert(it) }
        val lazyInvViewProj = LazyMat4d { viewProj.invert(it) }

        fun set(dataF: DataF) {
            globalPos.set(dataF.globalPos)
            globalLookAt.set(dataF.globalLookAt)
            globalUp.set(dataF.globalUp)
            globalRight.set(dataF.globalRight)
            globalLookDir.set(dataF.globalLookDir)
            view.set(dataF.view)
            viewProj.set(dataF.viewProj)
            globalRange = dataF.globalRange.toDouble()

            lazyInvView.isDirty = true
            lazyInvViewProj.isDirty = true
        }

        fun updateView() {
            toGlobalCoords(globalPos.set(position))
            toGlobalCoords(globalLookAt.set(lookAt))
            toGlobalCoords(globalUp.set(up), 0.0).norm()

            globalLookDir.set(globalLookAt).subtract(globalPos)
            globalRange = globalLookDir.length()
            globalLookDir.mul(1f / globalRange)

            globalLookDir.cross(globalUp, globalRight).norm()
            globalRight.cross(globalLookDir, globalUp).norm()

            view.setIdentity().lookAt(globalPos, globalLookAt, globalUp)
            tmpProjD.set(proj).mul(view, viewProj)

            lazyInvView.isDirty = true
            lazyInvViewProj.isDirty = true
        }
    }
}

open class OrthographicCamera(name: String = "orthographicCam") : Camera(name) {
    var left = -10.0f
    var right = 10.0f
    var bottom = -10.0f
    var top = 10.0f

    var isClipToViewport = false
    var isKeepAspectRatio = true

    private val tmpNodeCenter = MutableVec3f()
    private val tmpProjCorrection = MutableMat4f()

    fun setCentered(height: Float, near: Float, far: Float) {
        top = height * 0.5f
        bottom = -top
        right = aspectRatio * top
        left = -right
        this.clipNear = near
        this.clipFar = far
    }

    override fun updateCamera(updateEvent: RenderPass.UpdateEvent) {
        val vp = updateEvent.view.viewport
        if (isClipToViewport) {
            left = 0f
            right = vp.width.toFloat()
            bottom = 0f
            top = vp.height.toFloat()

        } else if (isKeepAspectRatio) {
            val h = top - bottom
            val w = vp.aspectRatio * h
            val xCenter = left + (right - left) * 0.5f
            left = xCenter - w * 0.5f
            right = xCenter + w * 0.5f
        }
        super.updateCamera(updateEvent)
    }

    override fun updateProjectionMatrix(updateEvent: RenderPass.UpdateEvent) {
        if (left != right && bottom != top && clipNear != clipFar) {
            proj.setIdentity()
            if (updateEvent.renderPass.isMirrorY) {
                proj.m11 *= -1f
            }
            proj.orthographic(left, right, bottom, top, clipNear, clipFar, updateEvent.ctx.backend.depthRange, isReverseDepthProjection)
        }

        viewParams.set(0f, 0f, (right - left) / 2f, (top - bottom) / 2f)
    }

    override fun computeFrustumPlane(z: Float, result: FrustumPlane) {
        invView.transform(result.upperLeft.set(left, top, -z))
        invView.transform(result.upperRight.set(right, top, -z))
        invView.transform(result.lowerLeft.set(left, bottom, -z))
        invView.transform(result.lowerRight.set(right, bottom, -z))
    }

    /**
     * Tests whether a bounding sphere intersects this camera's view frustum.
     * Similar approach as for perspective camera, but even simpler because no perspective is involved.
     */
    override fun isInFrustum(globalCenter: Vec3f, globalRadius: Float): Boolean {
        tmpNodeCenter.set(globalCenter)
        tmpNodeCenter.subtract(globalPos)

        val x = tmpNodeCenter.dot(globalRight)
        if (x > right + globalRadius || x < left - globalRadius) {
            // node's bounding sphere is either left or right of frustum
            return false
        }

        val y = tmpNodeCenter.dot(globalUp)
        if (y > top + globalRadius || y < bottom - globalRadius) {
            // node's bounding sphere is either above or below frustum
            return false
        }

        val z = tmpNodeCenter.dot(globalLookDir)
        if (z > clipFar + globalRadius || z < clipNear - globalRadius) {
            // node's bounding sphere is either in front of near or behind far plane
            return false
        }
        return true
    }
}

open class PerspectiveCamera(name: String = "perspectiveCam") : Camera(name) {
    var fovY = 60.0f.deg
    var fovX = 0f.deg
        private set

    private var sphereFacX = 1f
    private var sphereFacY = 1f
    private var tangX = 1f
    private var tangY = 1f

    private val tmpNodeCenter = MutableVec3f()
    private val tmpProjCorrection = MutableMat4f()

    private var complainedAboutDepthRange = false

    override fun updateProjectionMatrix(updateEvent: RenderPass.UpdateEvent) {
        proj.setIdentity()
        if (updateEvent.renderPass.isMirrorY) {
            proj.m11 *= -1f
        }
        if (isReverseDepthProjection) {
            proj.perspectiveReversedDepthInfiniteRange(fovY, aspectRatio, clipNear)
            if (updateEvent.ctx.backend.depthRange != DepthRange.ZERO_TO_ONE && !complainedAboutDepthRange) {
                complainedAboutDepthRange = true
                logW { "Using infinite depth projection on incompatible clip depth range" }
            }

        } else {
            proj.perspective(fovY, aspectRatio, clipNear, clipFar, updateEvent.ctx.backend.depthRange)
        }

        // compute intermediate values needed for view frustum culling
        val angY = fovY.rad / 2f
        sphereFacY = 1f / cos(angY)
        tangY = tan(angY)

        val angX = atan(tangY * aspectRatio)
        sphereFacX = 1f / cos(angX)
        tangX = tan(angX)
        fovX = (angX * 2).rad

        viewParams.set(tangX, tangY, 0f, 0f)
    }

    override fun computeFrustumPlane(z: Float, result: FrustumPlane) {
        val x = z * tangX
        val y = z * tangY

        invView.transform(result.upperLeft.set(-x, y, -z))
        invView.transform(result.upperRight.set(x, y, -z))
        invView.transform(result.lowerLeft.set(-x, -y, -z))
        invView.transform(result.lowerRight.set(x, -y, -z))
    }

    /**
     * Tests whether a bounding sphere intersects this camera's view frustum.
     * Implements the radar approach from http://www.lighthouse3d.com/tutorials/view-frustum-culling/
     */
    override fun isInFrustum(globalCenter: Vec3f, globalRadius: Float): Boolean {
        tmpNodeCenter.set(globalCenter)
        tmpNodeCenter.subtract(globalPos)

        var z = tmpNodeCenter.dot(globalLookDir)
        if (z > clipFar + globalRadius || z < clipNear - globalRadius) {
            // node's bounding sphere is either in front of near or behind far plane
            return false
        }

        val y = tmpNodeCenter.dot(globalUp)
        var d = globalRadius * sphereFacY
        z *= tangY
        if (y > z + d || y < -z - d) {
            // node's bounding sphere is either above or below view frustum
            return false
        }

        val x = tmpNodeCenter.dot(globalRight)
        d = globalRadius * sphereFacX
        z *= aspectRatio
        if (x > z + d || x < -z - d) {
            // node's bounding sphere is either left or right of view frustum
            return false
        }

        return true
    }
}

open class PerspectiveProxyCam(var trackedCam: PerspectiveCamera) : PerspectiveCamera() {

    var overrideNear = 0f
    var overrideFar = 0f

    init {
        useViewportAspectRatio = false
    }

    open fun sync(updateEvent: RenderPass.UpdateEvent) {
        // updateViewMatrix also updates the global pos and orientation parameters of tracked cam. Call it here
        // to avoid 1 frame latency in tracked camera position
        trackedCam.updateViewMatrix(updateEvent)

        position.set(trackedCam.globalPos)
        lookAt.set(trackedCam.globalLookAt)
        up.set(trackedCam.globalUp)

        aspectRatio = trackedCam.aspectRatio
        fovY = trackedCam.fovY
        clipNear = trackedCam.clipNear
        clipFar = trackedCam.clipFar

        if (updateEvent.renderPass.depthMode == DepthMode.Legacy && trackedCam.isReverseDepthProjection) {
            // limit far plane distance if this render pass is not reversed depth but the tracked camera is
            clipFar = min(clipFar, clipNear * 10_000f)
        }

        if (overrideNear != 0f) {
            clipNear = overrideNear
        }
        if (overrideFar != 0f) {
            clipFar = overrideFar
        }
    }
}

class FrustumPlane {
    val upperLeft = MutableVec3f()
    val upperRight = MutableVec3f()
    val lowerLeft = MutableVec3f()
    val lowerRight = MutableVec3f()

    fun set(other: FrustumPlane) {
        upperLeft.set(other.upperLeft)
        upperRight.set(other.upperRight)
        lowerLeft.set(other.lowerLeft)
        lowerRight.set(other.lowerRight)
    }
}