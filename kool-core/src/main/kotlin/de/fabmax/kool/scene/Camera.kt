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

    private val tmpPos = MutableVec3f()
    private val tmpLookAt = MutableVec3f()
    private val tmpUp = MutableVec3f()

    private val invMvp = Mat4f()

    private val tmpVec3 = MutableVec3f()
    private val tmpVec4 = MutableVec4f()

    fun updateCamera(ctx: RenderContext) {
        aspectRatio = ctx.viewportWidth.toFloat() / ctx.viewportHeight.toFloat()

        updateViewMatrix(ctx)
        updateProjectionMatrix(ctx)

        ctx.mvpState.projMatrix.mul(invMvp, ctx.mvpState.viewMatrix)
        invMvp.invert()

        ctx.mvpState.update(ctx)
    }

    fun updateViewMatrix(ctx: RenderContext) {
        toGlobalCoords(tmpPos.set(position))
        toGlobalCoords(tmpLookAt.set(lookAt))
        toGlobalCoords(tmpUp.set(up), 0f)
        ctx.mvpState.viewMatrix.setLookAt(tmpPos, tmpLookAt, tmpUp)
    }

    fun updateProjectionMatrix(ctx: RenderContext) {
        ctx.mvpState.projMatrix.setPerspective(fovy, aspectRatio, clipNear, clipFar)
    }

    fun initRayTes(rayTest: RayTest, ctx: RenderContext, ptrIdx: Int = InputHandler.PRIMARY_POINTER): Boolean {
        val ptr = ctx.inputHandler.getPointer(ptrIdx)
        val y = ctx.viewportHeight - ptr.y
        var valid = ptr.isValid

        rayTest.clear()
        valid = valid && unProject(tmpVec3.set(ptr.x, y, 0f), ctx.viewportWidth, ctx.viewportHeight, rayTest.origin)
        valid = valid && unProject(tmpVec3.set(ptr.x, y, 1f), ctx.viewportWidth, ctx.viewportHeight, rayTest.direction)

        if (valid) {
            rayTest.direction.subtract(rayTest.origin)
            rayTest.direction.norm()
        }

        return valid
    }

    private fun unProject(win: Vec3f, viewW: Int, viewH: Int, result: MutableVec3f): Boolean {
        tmpVec4.set(2f * win.x / viewW - 1f, 2f * win.y / viewH - 1f, 2f * win.z - 1f, 1f)
        invMvp.transform(tmpVec4)
        val s = 1f / tmpVec4.w
        result.set(tmpVec4.x * s, tmpVec4.y * s, tmpVec4.z * s)
        return true
    }
}
