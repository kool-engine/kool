package de.fabmax.kool.scene

import de.fabmax.kool.InputManager
import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */
abstract class Camera(name: String = "camera") : Node(name) {

    protected val position = MutableVec3f(0f, 0f, 1f)
    protected val lookAt = MutableVec3f(Vec3f.Companion.ZERO)
    protected val up = MutableVec3f(Vec3f.Companion.Y_AXIS)

    var aspectRatio = 1.0f
        protected set

    val globalPos: Vec3f get() = globalPosMut
    val globalLookAt: Vec3f get() = globalLookAtMut
    val globalUp: Vec3f get() = globalUpMut
    val globalLookDir: Vec3f get() = globalLookDirMut
    var globalRange = 0f
        protected set

    // we need a bunch of temporary vectors, keep them as members (#perfmatters)
    private val globalPosMut = MutableVec3f()
    private val globalLookAtMut = MutableVec3f()
    private val globalUpMut = MutableVec3f()
    private val globalLookDirMut = MutableVec3f()

    private val aabbCenter = MutableVec3f()
    private val aabbCenterProj = MutableVec3f()
    private val aabbExtent = MutableVec3f()
    private val tmpVec3 = MutableVec3f()
    private val tmpVec4 = MutableVec4f()

    private val viewRay = Ray()
    private val mvp = Mat4f()
    private val invMvp = Mat4f()

    fun updateCamera(ctx: RenderContext) {
        aspectRatio = ctx.viewportWidth.toFloat() / ctx.viewportHeight.toFloat()

        updateViewMatrix(ctx)
        updateProjectionMatrix(ctx)

        ctx.mvpState.projMatrix.mul(mvp, ctx.mvpState.viewMatrix)
        mvp.invert(invMvp)

        ctx.mvpState.update(ctx)
    }

    protected fun updateViewMatrix(ctx: RenderContext) {
        toGlobalCoords(globalPosMut.set(position))
        toGlobalCoords(globalLookAtMut.set(lookAt))
        toGlobalCoords(globalUpMut.set(up), 0f)
        globalLookDirMut.set(globalLookAtMut).subtract(globalPosMut)

        globalRange = globalPosMut.distance(globalLookAtMut)

        ctx.mvpState.viewMatrix.setLookAt(globalPosMut, globalLookAtMut, globalUpMut)
        viewRay.setFromLookAt(globalPosMut, globalLookAtMut)
    }

    abstract protected fun updateProjectionMatrix(ctx: RenderContext)

    fun computePickRay(pickRay: Ray, ptr: InputManager.Pointer, ctx: RenderContext): Boolean {
        return ptr.isValid && computePickRay(pickRay, ptr.x, ptr.y, ctx)
    }

    fun computePickRay(pickRay: Ray, screenX: Float, screenY: Float, ctx: RenderContext): Boolean {
        var valid = unProjectScreen(pickRay.origin, tmpVec3.set(screenX, screenY, 0f), ctx)
        valid = valid && unProjectScreen(pickRay.direction, tmpVec3.set(screenX, screenY, 1f), ctx)

        if (valid) {
            pickRay.direction.subtract(pickRay.origin)
            pickRay.direction.norm()
        }

        return valid
    }

    fun initRayTes(rayTest: RayTest, ptr: InputManager.Pointer, ctx: RenderContext): Boolean {
        return ptr.isValid && initRayTes(rayTest, ptr.x, ptr.y, ctx)
    }

    fun initRayTes(rayTest: RayTest, screenX: Float, screenY: Float, ctx: RenderContext): Boolean {
        rayTest.clear()
        return computePickRay(rayTest.ray, screenX, screenY, ctx)
    }

    fun isVisible(node: Node): Boolean {
        aabbCenter.set(node.bounds.center)
        node.toGlobalCoords(aabbCenter)

        project(aabbCenterProj, aabbCenter)
        if (isInFrustum(aabbCenterProj)) {
            // center of bounding box is inside the view frustum -> it's visible
            return true
        }

        // if we get here the center of the bounding box is outside the view frustum, apply a simple heuristic to
        // check if bounds are completely outside
        tmpVec3.set(node.bounds.max)
        node.toGlobalCoords(tmpVec3)
        // in case objects pop in when coming into site, the extent size needs to be increased...
        val extent = tmpVec3.subtract(aabbCenter).length() * 2

        // find nearest point to aabb center on this camera's view ray, tmpLookAt will store the result
        viewRay.nearestPointOnRay(aabbExtent, aabbCenter)
        // compute the point on the orthogonal line from bounds center to the view ray at half the bounds
        // extents distance
        aabbExtent.subtract(aabbCenter).norm().scale(extent).add(aabbCenter)

        project(aabbExtent, aabbExtent)
        if (isInFrustum(aabbExtent)) {
            // extent point is visible -> node might be as well
            return true
        }

        // if we get here extent point and center point are not visible, however they might be on opposing sides
        // of the view frustum, compare signs
        if (Math.sign(aabbExtent.x) != Math.sign(aabbCenterProj.x) ||
                Math.sign(aabbExtent.y) != Math.sign(aabbCenterProj.y) ||
                Math.sign(aabbExtent.z) != Math.sign(aabbCenterProj.z)) {
            return true
        }
        // If we get here center and extent point are on the same side of the frustum and node is hopefully not visible
        return false
    }

    fun isVisible(point: Vec3f): Boolean {
        project(tmpVec3, point)
        return isInFrustum(tmpVec3)
    }

    private fun isInFrustum(camSpace: Vec3f): Boolean {
        return camSpace.x > -1 && camSpace.x < 1 &&
                camSpace.y > -1 && camSpace.y < 1 &&
                camSpace.z > -1 && camSpace.z < 1
    }

    fun project(result: MutableVec3f, world: Vec3f): Boolean {
        tmpVec4.set(world.x, world.y, world.z, 1f)
        mvp.transform(tmpVec4)
        if (Math.isZero(tmpVec4.w)) {
            return false
        }
        result.set(tmpVec4.x, tmpVec4.y, tmpVec4.z).scale(1f / tmpVec4.w)
        return true
    }

    fun projectScreen(result: MutableVec3f, world: Vec3f, ctx: RenderContext): Boolean {
        if (!project(result, world)) {
            return false
        }
        result.x = (1 + result.x) * 0.5f * ctx.viewportWidth + ctx.viewportX
        result.y = ctx.windowHeight - ((1 + result.y) * 0.5f * ctx.viewportHeight + ctx.viewportY)
        result.z = (1 + result.z) * 0.5f
        return true
    }

    fun unProjectScreen(result: MutableVec3f, screen: Vec3f, ctx: RenderContext): Boolean {
        val x = screen.x - ctx.viewportX
        val y = (ctx.windowHeight - screen.y) - ctx.viewportY
        tmpVec4.set(2f * x / ctx.viewportWidth - 1f, 2f * y / ctx.viewportHeight - 1f, 2f * screen.z - 1f, 1f)
        invMvp.transform(tmpVec4)
        val s = 1f / tmpVec4.w
        result.set(tmpVec4.x * s, tmpVec4.y * s, tmpVec4.z * s)
        return true
    }
}

class OrthographicCamera(name: String = "orthographicCam") : Camera(name) {
    var left = -10.0f
    var right = 10.0f
    var bottom = -10.0f
    var top = 10.0f
    var near = -10.0f
    var far = 10.0f

    var clipToViewport = false
    var keepAspectRatio = true

    fun setCentered(height: Float, near: Float, far: Float) {
        top = height * 0.5f
        bottom = -top
        right = aspectRatio * top
        left = -right
        this.near = near
        this.far = far
    }

    override fun updateProjectionMatrix(ctx: RenderContext) {
        if (clipToViewport) {
            left = 0f
            right = ctx.viewportWidth.toFloat()
            bottom = 0f
            top = ctx.viewportHeight.toFloat()

        } else if (keepAspectRatio) {
            val h = top - bottom
            val w = aspectRatio * h
            val xCenter = left + (right - left) * 0.5f
            left = xCenter - w * 0.5f
            right = xCenter + w * 0.5f
        }
        ctx.mvpState.projMatrix.setOrthographic(left, right, bottom, top, near, far)
    }
}

class PerspectiveCamera(name: String = "perspectiveCam") : Camera(name) {
    var fovy = 60.0f
    var clipNear = 0.2f
    var clipFar = 200.0f

    override fun updateProjectionMatrix(ctx: RenderContext) {
        ctx.mvpState.projMatrix.setPerspective(fovy, aspectRatio, clipNear, clipFar)
    }
}
