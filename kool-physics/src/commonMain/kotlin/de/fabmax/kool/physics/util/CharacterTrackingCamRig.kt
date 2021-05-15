package de.fabmax.kool.physics.util

import de.fabmax.kool.InputManager
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Group
import kotlin.math.*

class CharacterTrackingCamRig(private val inputManager: InputManager, enableCursorLock: Boolean = true) : Group("PointerLockCamRig") {
    var isCursorLocked: Boolean
        get() = inputManager.cursorMode == InputManager.CursorMode.LOCKED
        set(value) {
            if (value) {
                inputManager.cursorMode = InputManager.CursorMode.LOCKED
            } else {
                inputManager.cursorMode = InputManager.CursorMode.NORMAL
            }
        }

    var sensitivity = 1f

    var trackedPose = Mat4f()
    val pivotPoint = MutableVec3f()

    val lookDirection = MutableVec3f(Vec3f.NEG_Z_AXIS)

    private val globalPivot = MutableVec3f()
    private var lookPhi = 0f
    private var lookTheta = PI.toFloat() / 2f

    init {
        isCursorLocked = enableCursorLock

        onUpdate += {
            if (isCursorLocked) {
                handlePointerInput()
            }
            updateTracking()
        }
    }

    private fun handlePointerInput() {
        lookPhi = atan2(lookDirection.z, lookDirection.x)
        lookTheta = acos(lookDirection.y)

        val div = 1000f / sensitivity

        lookPhi -= inputManager.pointerState.primaryPointer.deltaX.toFloat() / div
        lookTheta = (lookTheta - inputManager.pointerState.primaryPointer.deltaY.toFloat() / div).clamp(0.0001f, PI.toFloat() - 0.0001f)

        lookDirection.x = sin(lookTheta) * cos(lookPhi)
        lookDirection.z = sin(lookTheta) * sin(lookPhi)
        lookDirection.y = cos(lookTheta)
    }

    private fun updateTracking() {
        globalPivot.set(pivotPoint)
        trackedPose.transform(globalPivot)

        setIdentity()
        translate(globalPivot)

        rotate(lookTheta.toDeg() - 90f, lookPhi.toDeg() + 90f, 0f)
    }
}