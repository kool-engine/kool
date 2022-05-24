package de.fabmax.kool.physics.util

import de.fabmax.kool.InputManager
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RaycastResult
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
    var zoom = 4f
    var minZoom = 0.5f
    var maxZoom = 25f

    private var actualZoom = zoom

    var trackedPose = Mat4f()
    val pivotPoint = MutableVec3f()

    val lookDirection = MutableVec3f(Vec3f.NEG_Z_AXIS)

    var zoomModifier: (Float) -> Float = { it }

    private val poseOrigin = MutableVec3f()
    private var lookPhi = 0f
    private var lookTheta = PI.toFloat() / 2f

    init {
        isCursorLocked = enableCursorLock

        onUpdate += {
            if (isCursorLocked) {
                handlePointerInput()
            }
            updateTracking(it.deltaT)
        }
    }

    fun setupCollisionAwareCamZoom(world: PhysicsWorld, hitOffset: Float = 0.5f) {
        val testRay = Ray()
        val rayResult = RaycastResult()

        zoomModifier = { desiredZoom ->
            var zoom = desiredZoom
            transform.transform(testRay.origin.set(Vec3f.ZERO))
            transform.transform(testRay.direction.set(-0.15f, -0.1f, 1f).norm(), 0f)
            if (world.raycast(testRay, desiredZoom, rayResult)) {
                val hitDist = rayResult.hitPosition.distance(testRay.origin)
                zoom = max(minZoom, hitDist - hitOffset)
            }
            transform.transform(testRay.direction.set(0.15f, -0.1f, 1f).norm(), 0f)
            if (world.raycast(testRay, desiredZoom, rayResult)) {
                val hitDist = rayResult.hitPosition.distance(testRay.origin)
                zoom = min(zoom, max(minZoom, hitDist - hitOffset))
            }
            zoom
        }
    }

    private fun handlePointerInput() {
        lookPhi = atan2(lookDirection.z, lookDirection.x)
        lookTheta = acos(lookDirection.y)

        val div = 1000f / sensitivity
        val ptr = inputManager.pointerState.primaryPointer

        lookPhi -= ptr.deltaX.toFloat() / div
        lookTheta = (lookTheta - ptr.deltaY.toFloat() / div).clamp(0.0001f, PI.toFloat() - 0.0001f)

        lookDirection.x = sin(lookTheta) * cos(lookPhi)
        lookDirection.z = sin(lookTheta) * sin(lookPhi)
        lookDirection.y = cos(lookTheta)

        if (!ptr.isConsumed(InputManager.CONSUMED_SCROLL)) {
            zoom *= 1f - inputManager.pointerState.primaryPointer.deltaScroll.toFloat() / 10f
            zoom = zoom.clamp(minZoom, maxZoom)
        }
    }

    private fun updateTracking(deltaT: Float) {
        trackedPose.transform(poseOrigin.set(Vec3f.ZERO))

        setIdentity()
        translate(poseOrigin)
        rotate(lookPhi.toDeg() + 90f, Vec3f.Y_AXIS)
        translate(pivotPoint)
        rotate(lookTheta.toDeg() - 90f, Vec3f.X_AXIS)

        val modZoom = zoomModifier(zoom)
        val wActual = (30f * deltaT).clamp(0f, 0.99f)
        actualZoom = actualZoom * wActual + modZoom * (1f - wActual)
        scale(actualZoom)
    }
}