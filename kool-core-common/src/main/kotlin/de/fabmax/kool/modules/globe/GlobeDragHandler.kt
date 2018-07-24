package de.fabmax.kool.modules.globe

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import kotlin.math.sqrt

class GlobeDragHandler(val globe: Globe) : InputManager.DragHandler {
    private var steadyScreenPt = MutableVec2f()
    private var steadyScreenPtMode = STEADY_SCREEN_PT_OFF

    private val startTransform = Mat4d()
    private val ptOrientation = Mat4d()
    private val mouseRotationStart = Mat4d()
    private var isDragging = false
    private val pickRay = Ray()

    private val tmpVec = MutableVec3d()
    private val tmpVecRt = MutableVec3d()
    private val tmpVecUp = MutableVec3d()
    private val tmpVecY = MutableVec3d()
    private val tmpVecf = MutableVec3f()

    private val tmpRayO = MutableVec3d()
    private val tmpRayL = MutableVec3d()

    init {
        globe.onPreRender += { onPreRender(it) }
    }

    override fun handleDrag(dragPtrs: List<InputManager.Pointer>, ctx: KoolContext): Int {
        if (dragPtrs.size == 1 && dragPtrs[0].isInViewport(ctx)) {
            val ptrX = dragPtrs[0].x
            val ptrY = dragPtrs[0].y

            if (dragPtrs[0].isLeftButtonDown) {
                steadyScreenPtMode = STEADY_SCREEN_PT_OFF

                if (dragPtrs[0].isLeftButtonEvent) {
                    isDragging = computePointOrientation(ptrX, ptrY, ctx)
                    ptOrientation.transpose(mouseRotationStart)
                    globe.getTransform(startTransform)

                } else if (isDragging) {
                    globe.set(startTransform)

                    val valid = computePointOrientation(ptrX, ptrY, ctx)
                    if (valid) {
                        ptOrientation.mul(mouseRotationStart)
                    }
                    globe.mul(ptOrientation)
                    isDragging = valid
                }
            } else if (dragPtrs[0].deltaScroll != 0f || (dragPtrs[0].isRightButtonEvent && dragPtrs[0].isRightButtonDown)) {
                if (steadyScreenPtMode == STEADY_SCREEN_PT_OFF || ptrX != steadyScreenPt.x || ptrY != steadyScreenPt.y) {
                    setSteadyPoint(ptrX, ptrY)
                }
            }
        }
        return 0
    }

    private fun onPreRender(ctx: KoolContext) {
        if (steadyScreenPtMode == STEADY_SCREEN_PT_INIT &&
                computePointOrientation(steadyScreenPt.x, steadyScreenPt.y, ctx)) {
            steadyScreenPtMode = STEADY_SCREEN_PT_HOLD
            ptOrientation.transpose(mouseRotationStart)
            globe.getTransform(startTransform)

        } else if (steadyScreenPtMode == STEADY_SCREEN_PT_HOLD) {
            globe.set(startTransform)

            if (computePointOrientation(steadyScreenPt.x, steadyScreenPt.y, ctx)) {
                ptOrientation.mul(mouseRotationStart)
            } else {
                steadyScreenPtMode = STEADY_SCREEN_PT_OFF
            }
            globe.mul(ptOrientation)
        }
    }

    private fun setSteadyPoint(screenX: Float, screenY: Float) {
        steadyScreenPt.set(screenX, screenY)
        steadyScreenPtMode = STEADY_SCREEN_PT_INIT
    }

    private fun computePointOrientation(screenX: Float, screenY: Float, ctx: KoolContext): Boolean {
        if (globe.scene?.camera?.computePickRay(pickRay, screenX, screenY, ctx) == true) {
            pickRay.origin.toMutableVec3d(tmpRayO)
            pickRay.direction.toMutableVec3d(tmpRayL)
            globe.toLocalCoordsDp(tmpRayO, 1.0)
            globe.toLocalCoordsDp(tmpRayL, 0.0)
            globe.toLocalCoordsDp(tmpVecY.set(Vec3d.Y_AXIS), 0.0)

            val ldo = tmpRayL * tmpRayO
            val sqr = ldo * ldo - tmpRayO.sqrLength() + globe.radius * globe.radius
            if (sqr > 0) {
                val d = -ldo - sqrt(sqr)
                tmpRayL.scale(d, tmpVec).add(tmpRayO)

                tmpVec.norm()
                if (tmpVec.isFuzzyEqual(tmpVecY)) {
                    return false
                }
                tmpVecY.cross(tmpVec, tmpVecRt).norm()
                tmpVec.cross(tmpVecRt, tmpVecUp)

                ptOrientation.setColVec(0, tmpVec, 0.0)
                ptOrientation.setColVec(1, tmpVecRt, 0.0)
                ptOrientation.setColVec(2, tmpVecUp, 0.0)
                ptOrientation.setColVec(3, Vec3d.ZERO, 1.0)
                return true
            }
        }
        return false
    }

    companion object {
        private const val STEADY_SCREEN_PT_OFF = 0
        private const val STEADY_SCREEN_PT_INIT = 1
        private const val STEADY_SCREEN_PT_HOLD = 2
    }
}