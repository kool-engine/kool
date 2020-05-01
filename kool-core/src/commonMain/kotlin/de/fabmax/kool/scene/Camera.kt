package de.fabmax.kool.scene

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.tan

/**
 * @author fabmax
 */
abstract class Camera(name: String = "camera") : Node(name) {

    val position = MutableVec3f(0f, 0f, 1f)
    val lookAt = MutableVec3f(Vec3f.ZERO)
    val up = MutableVec3f(Vec3f.Y_AXIS)

    var aspectRatio = 1.0f
        protected set

    val globalPos: Vec3f get() = globalPosMut
    val globalLookAt: Vec3f get() = globalLookAtMut
    val globalUp: Vec3f get() = globalUpMut
    val globalRight: Vec3f get() = globalRightMut
    val globalLookDir: Vec3f get() = globalLookDirMut
    var globalRange = 0f
        protected set

    protected val globalPosMut = MutableVec3f()
    protected val globalLookAtMut = MutableVec3f()
    protected val globalUpMut = MutableVec3f()
    protected val globalRightMut = MutableVec3f()
    protected val globalLookDirMut = MutableVec3f()

    val proj = Mat4d()
    val view = Mat4d()

    private val lazyInvView = LazyMatrix { view.invert(it) }
    val invView: Mat4d get() = lazyInvView.get()

    private val lazyViewProj = LazyMatrix { proj.mul(view, it) }
    val viewProj: Mat4d get() = lazyViewProj.get()

    private val lazyInvViewProj = LazyMatrix { viewProj.invert(it) }
    val invViewProj: Mat4d get() = lazyInvViewProj.get()

    var projCorrectionMode = ProjCorrectionMode.ONSCREEN

    private val projCorrected = Mat4d()

    // we need a bunch of temporary vectors, keep them as members (#perfmatters)
    private val tmpVec3 = MutableVec3f()
    private val tmpVec4 = MutableVec4f()

    open fun updateCamera(ctx: KoolContext, viewport: KoolContext.Viewport) {
        aspectRatio = viewport.aspectRatio

        updateViewMatrix()
        updateProjectionMatrix()

        if (projCorrectionMode == ProjCorrectionMode.ONSCREEN) {
            ctx.projCorrectionMatrixScreen.mul(proj, projCorrected)
            proj.set(projCorrected)
        } else if (projCorrectionMode == ProjCorrectionMode.OFFSCREEN) {
            ctx.projCorrectionMatrixOffscreen.mul(proj, projCorrected)
            proj.set(projCorrected)
        }

        lazyViewProj.isDirty = true
        lazyInvViewProj.isDirty = true
    }

    protected open fun updateViewMatrix() {
        toGlobalCoords(globalPosMut.set(position))
        toGlobalCoords(globalLookAtMut.set(lookAt))
        toGlobalCoords(globalUpMut.set(up), 0f).norm()

        globalLookDirMut.set(globalLookAtMut).subtract(globalPosMut)
        globalRange = globalLookDirMut.length()
        globalLookDirMut.scale(1f / globalRange)

        globalLookDirMut.cross(globalUpMut, globalRightMut).norm()
        globalRightMut.cross(globalLookDirMut, globalUpMut).norm()

        view.setLookAt(globalPosMut, globalLookAtMut, globalUpMut)
        lazyInvView.isDirty = true
    }

    protected abstract fun updateProjectionMatrix()

    fun computePickRay(pickRay: Ray, ptr: InputManager.Pointer, viewport: KoolContext.Viewport, ctx: KoolContext): Boolean {
        return ptr.isValid && computePickRay(pickRay, ptr.x, ptr.y, viewport, ctx)
    }

    fun computePickRay(pickRay: Ray, screenX: Float, screenY: Float, viewport: KoolContext.Viewport, ctx: KoolContext): Boolean {
        var valid = unProjectScreen(tmpVec3.set(screenX, screenY, 0f), viewport, ctx, pickRay.origin)
        valid = valid && unProjectScreen(tmpVec3.set(screenX, screenY, 1f), viewport, ctx, pickRay.direction)

        if (valid) {
            pickRay.direction.subtract(pickRay.origin)
            pickRay.direction.norm()
        }

        return valid
    }

    fun initRayTes(rayTest: RayTest, ptr: InputManager.Pointer, viewport: KoolContext.Viewport, ctx: KoolContext): Boolean {
        return ptr.isValid && initRayTes(rayTest, ptr.x, ptr.y, viewport, ctx)
    }

    fun initRayTes(rayTest: RayTest, screenX: Float, screenY: Float, viewport: KoolContext.Viewport, ctx: KoolContext): Boolean {
        rayTest.clear()
        return computePickRay(rayTest.ray, screenX, screenY, viewport, ctx)
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
            return false
        }
        result.set(tmpVec4.x, tmpVec4.y, tmpVec4.z).scale(1f / tmpVec4.w)
        return true
    }

    fun project(world: Vec3f, result: MutableVec4f): MutableVec4f =
            viewProj.transform(result.set(world.x, world.y, world.z, 1f))

    fun projectScreen(world: Vec3f, viewport: KoolContext.Viewport, ctx: KoolContext, result: MutableVec3f): Boolean {
        if (!project(world, result)) {
            return false
        }
        result.x = (1 + result.x) * 0.5f * viewport.width + viewport.x
        result.y = (1 + result.y) * 0.5f * viewport.height + viewport.y
        result.z = (1 + result.z) * 0.5f

        // fixme: rather hacky solution for inverted y-viewport direction in Vulkan
        if (ctx.projCorrectionMatrixScreen[1, 1] > 0) {
            result.y = ctx.windowHeight - result.y
        }

        return true
    }

    fun unProjectScreen(screen: Vec3f, viewport: KoolContext.Viewport, ctx: KoolContext, result: MutableVec3f): Boolean {
        val x = screen.x - viewport.x

        // fixme: rather hacky solution for inverted y-viewport direction in Vulkan
        val y = if (ctx.projCorrectionMatrixScreen[1, 1] < 0) {
            screen.y - viewport.y.toFloat()
        } else {
            (ctx.windowHeight - screen.y) - viewport.y
        }

        tmpVec4.set(2f * x / viewport.width - 1f, 2f * y / viewport.height - 1f, 2f * screen.z - 1f, 1f)
        invViewProj.transform(tmpVec4)
        val s = 1f / tmpVec4.w
        result.set(tmpVec4.x * s, tmpVec4.y * s, tmpVec4.z * s)
        return true
    }

    private class LazyMatrix(val update: (Mat4d) -> Unit) {
        var isDirty = true

        private val mat = Mat4d()

        fun get(): Mat4d {
            if (isDirty) {
                update(mat)
                isDirty = false
            }
            return mat
        }
    }

    enum class ProjCorrectionMode {
        NONE,
        ONSCREEN,
        OFFSCREEN
    }
}

open class OrthographicCamera(name: String = "orthographicCam") : Camera(name) {
    var left = -10.0f
    var right = 10.0f
    var bottom = -10.0f
    var top = 10.0f
    var near = -10.0f
    var far = 10.0f

    var isClipToViewport = false
    var isKeepAspectRatio = true

    private val tmpNodeCenter = MutableVec3f()

    fun setCentered(height: Float, near: Float, far: Float) {
        top = height * 0.5f
        bottom = -top
        right = aspectRatio * top
        left = -right
        this.near = near
        this.far = far
    }

    override fun updateCamera(ctx: KoolContext, viewport: KoolContext.Viewport) {
        if (isClipToViewport) {
            left = 0f
            right = viewport.width.toFloat()
            bottom = 0f
            top = viewport.height.toFloat()

        } else if (isKeepAspectRatio) {
            val h = top - bottom
            val w = viewport.aspectRatio * h
            val xCenter = left + (right - left) * 0.5f
            left = xCenter - w * 0.5f
            right = xCenter + w * 0.5f
        }
        super.updateCamera(ctx, viewport)
    }

    override fun updateProjectionMatrix() {
        if (left != right && bottom != top && near != far) {
            proj.setOrthographic(left, right, bottom, top, near, far)
        }
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
        if (z > far + globalRadius || z < near - globalRadius) {
            // node's bounding sphere is either in front of near or behind far plane
            return false
        }
        return true
    }
}

open class PerspectiveCamera(name: String = "perspectiveCam") : Camera(name) {
    var clipNear = 0.1f
    var clipFar = 100.0f

    var fovY = 60.0f
    var fovX = 0f
        private set

    private var sphereFacX = 1f
    private var sphereFacY = 1f
    private var tangX = 1f
    private var tangY = 1f

    private val tmpNodeCenter = MutableVec3f()

    override fun updateProjectionMatrix() {
        proj.setPerspective(fovY, aspectRatio, clipNear, clipFar)

        // compute intermediate values needed for view frustum culling
        val angY = fovY.toRad() / 2f
        sphereFacY = 1f / cos(angY)
        tangY = tan(angY)

        val angX = atan(tangY * aspectRatio)
        sphereFacX = 1f / cos(angX)
        tangX = tan(angX)
        fovX = (angX * 2).toDeg()
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