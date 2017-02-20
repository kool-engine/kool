package de.fabmax.kool.scene

import de.fabmax.kool.InputHandler
import de.fabmax.kool.MvpState
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */
class Camera(name: String = "camera") : Node(name) {

    val position = MutableVec3f(0f, 0f, 10f)
    val lookAt = MutableVec3f(Vec3f.Companion.ZERO)
    val up = MutableVec3f(Vec3f.Companion.Y_AXIS)

    var fovy = 60.0f
    var clipNear = 0.2f
    var clipFar = 200.0f
    var aspectRatio = 1.0f
        private set

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

    fun updateProjectionMatrix(ctx: RenderContext) {
        ctx.mvpState.projMatrix.setPerspective(fovy, aspectRatio, clipNear, clipFar)
    }

    fun initRayTes(rayTest: RayTest, ctx: RenderContext, ptrIdx: Int = InputHandler.PRIMARY_POINTER): Boolean {
        val ptr = ctx.inputHandler.getPointer(ptrIdx)
        return initRayTes(rayTest, ctx, ptr.x, ptr.y) && ptr.isValid
    }

    fun initRayTes(rayTest: RayTest, ctx: RenderContext, screenX: Float, screenY: Float): Boolean {
        val w = ctx.viewportWidth
        val h = ctx.viewportHeight
        val y = ctx.viewportHeight - screenY
        var valid = true

        rayTest.clear()
        valid = valid && unProject(tmpPos.set(screenX, y, 0f), w, h, rayTest.ray.origin)
        valid = valid && unProject(tmpPos.set(screenX, y, 1f), w, h, rayTest.ray.direction)

        if (valid) {
            rayTest.ray.direction.subtract(rayTest.ray.origin)
            rayTest.ray.direction.norm()
        }

        return valid
    }

    fun isVisible(node: Node): Boolean {
        tmpPos.set(node.bounds.center)
        node.toGlobalCoords(tmpPos)

        project(tmpPos, tmpUp)
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
        if (sign(tmpUp.x) != sign(tmpLookAt.x) ||
                sign(tmpUp.y) != sign(tmpLookAt.y) ||
                sign(tmpUp.z) != sign(tmpLookAt.z)) {
            return true
        }
        // If we get here center and extent point are on the same side of the frustum and node is hopefully not visible
        return false
    }

    fun isVisible(point: Vec3f): Boolean {
        project(point, tmpPos)
        return isInFrustum(tmpPos)
    }

    private fun isInFrustum(camSpace: Vec3f): Boolean {
        return camSpace.x > -1 && camSpace.x < 1 &&
                camSpace.y > -1 && camSpace.y < 1 &&
                camSpace.z > -1 && camSpace.z < 1
    }

    private fun project(world: Vec3f, result: MutableVec3f): Boolean {
        tmpVec4.set(world.x, world.y, world.z, 1f)
        mvp.transform(tmpVec4)
        if (isZero(tmpVec4.w)) {
            return false
        }
        result.set(tmpVec4.x, tmpVec4.y, tmpVec4.z).scale(1f / tmpVec4.w)
        return true
    }

    private fun unProject(win: Vec3f, viewW: Int, viewH: Int, result: MutableVec3f): Boolean {
        tmpVec4.set(2f * win.x / viewW - 1f, 2f * win.y / viewH - 1f, 2f * win.z - 1f, 1f)
        invMvp.transform(tmpVec4)
        val s = 1f / tmpVec4.w
        result.set(tmpVec4.x * s, tmpVec4.y * s, tmpVec4.z * s)
        return true
    }
}
