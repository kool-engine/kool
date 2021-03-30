package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import org.lwjgl.system.MemoryStack
import physx.physics.PxArticulationJoint

actual class ArticulationJoint(val pxJoint: PxArticulationJoint) {

    actual var damping: Float
        get() = pxJoint.damping
        set(value) { pxJoint.damping = value }

    actual var stiffness: Float
        get() = pxJoint.stiffness
        set(value) { pxJoint.stiffness = value }

    actual var tangentialDamping: Float
        get() = pxJoint.tangentialDamping
        set(value) { pxJoint.tangentialDamping = value }

    actual var tangentialStiffness: Float
        get() = pxJoint.tangentialStiffness
        set(value) { pxJoint.tangentialStiffness = value }

    actual var isSwingLimitEnabled: Boolean
        get() = pxJoint.swingLimitEnabled
        set(value) { pxJoint.swingLimitEnabled = value }

    actual var isTwistLimitEnabled: Boolean
        get() = pxJoint.twistLimitEnabled
        set(value) { pxJoint.twistLimitEnabled = value }

    private val bufTargetOrientation = MutableVec4f()
    actual var targetOrientation: Vec4f
        get() = pxJoint.targetOrientation.toVec4f(bufTargetOrientation)
        set(value) {
            MemoryStack.stackPush().use { mem ->
                pxJoint.targetOrientation = value.toPxQuat(mem.createPxQuat())
            }
        }

    actual fun setParentPose(pose: Mat4f) {
        MemoryStack.stackPush().use { mem ->
            pxJoint.parentPose = pose.toPxTransform(mem.createPxTransform())
        }
    }

    actual fun setChildPose(pose: Mat4f) {
        MemoryStack.stackPush().use { mem ->
            pxJoint.childPose = pose.toPxTransform(mem.createPxTransform())
        }
    }

    actual fun setSwingLimit(zLimit: Float, yLimit: Float) {
        pxJoint.setSwingLimit(zLimit.toRad(), yLimit.toRad())
    }

    actual fun setTwistLimit(lower: Float, upper: Float) {
        pxJoint.setTwistLimit(lower.toRad(), upper.toRad())
    }

    actual fun setTargetOrientation(eulerX: Float, eulerY: Float, eulerZ: Float) {
        setTargetOrientation(Mat3f().rotate(eulerX, eulerY, eulerZ))
    }

    actual fun setTargetOrientation(rot: Mat3f) {
        targetOrientation = rot.getRotation(MutableVec4f())
    }
}