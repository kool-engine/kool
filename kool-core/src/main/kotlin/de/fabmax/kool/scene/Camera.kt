package de.fabmax.kool.scene

import de.fabmax.kool.InputManager
import de.fabmax.kool.MvpState
import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */
abstract class Camera(name: String = "camera") : Node(name) {

    val position = MutableVec3f(0f, 0f, 10f)
    val lookAt = MutableVec3f(Vec3f.Companion.ZERO)
    val up = MutableVec3f(Vec3f.Companion.Y_AXIS)

    var aspectRatio = 1.0f
        protected set

    // we need a bunch of temporary vectors, keep them as members (#perfmatters)
    private val tmpPos = MutableVec3f()
    private val tmpLookAt = MutableVec3f()
    private val tmpUp = MutableVec3f()
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

    fun updateViewMatrix(ctx: RenderContext) {
        toGlobalCoords(tmpPos.set(position))
        toGlobalCoords(tmpLookAt.set(lookAt))
        toGlobalCoords(tmpUp.set(up), 0f)

        ctx.mvpState.viewMatrix.setLookAt(tmpPos, tmpLookAt, tmpUp)
        viewRay.setFromLookAt(tmpPos, tmpLookAt)
    }

    abstract fun updateProjectionMatrix(ctx: RenderContext)

    fun initRayTes(rayTest: RayTest, ptr: InputManager.Pointer, ctx: RenderContext): Boolean {
        return ptr.isValid && initRayTes(rayTest, ptr.x, ptr.y, ctx)
    }

    fun initRayTes(rayTest: RayTest, screenX: Float, screenY: Float, ctx: RenderContext): Boolean {
        rayTest.clear()

        var valid = unProjectScreen(rayTest.ray.origin, tmpPos.set(screenX, screenY, 0f), ctx)
        valid = valid && unProjectScreen(rayTest.ray.direction, tmpPos.set(screenX, screenY, 1f), ctx)

        if (valid) {
            rayTest.ray.direction.subtract(rayTest.ray.origin)
            rayTest.ray.direction.norm()
        }

        return valid
    }

    fun isVisible(node: Node): Boolean {
        tmpPos.set(node.bounds.center)
        node.toGlobalCoords(tmpPos)

        project(tmpUp, tmpPos)
        if (isInFrustum(tmpUp)) {
            // center of bounding box is inside the view frustum -> it's visible
            return true
        }

        // if we get here the center of the bounding box is outside the view frustum, apply a simple heuristic to
        // check if bounds are completely outside
        tmpLookAt.set(node.bounds.max)
        node.toGlobalCoords(tmpLookAt)
        // in case objects pop in when coming into site, the extent size needs to be increased...
        val extent = tmpLookAt.subtract(tmpPos).length() * 2

        // find nearest point to tmpPos on this camera's view ray, tmpLookAt will store the result
        viewRay.nearestPointOnRay(tmpLookAt, tmpPos)
        // compute the point on the orthogonal line from bounds center to the view ray at half the bounds
        // extents distance
        tmpLookAt.subtract(tmpPos).norm().scale(extent).add(tmpPos)

        project(tmpLookAt, tmpLookAt)
        if (isInFrustum(tmpLookAt)) {
            // extent point is visible -> node might be as well
            return true
        }

        // if we get here extent point and center point are not visible, however they might be on opposing sides
        // of the view frustum, tmpUp and tmpLookAt store their coordinates in camera space, compare signs
        if (Math.sign(tmpUp.x) != Math.sign(tmpLookAt.x) ||
                Math.sign(tmpUp.y) != Math.sign(tmpLookAt.y) ||
                Math.sign(tmpUp.z) != Math.sign(tmpLookAt.z)) {
            return true
        }
        // If we get here center and extent point are on the same side of the frustum and node is hopefully not visible
        return false
    }

    fun isVisible(point: Vec3f): Boolean {
        project(tmpPos, point)
        return isInFrustum(tmpPos)
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
    var near = 0.0f
    var far = 1000.0f

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
