package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec4f

expect class ArticulationJoint {

    var damping: Float
    var stiffness: Float

    var tangentialDamping: Float
    var tangentialStiffness: Float

    var isSwingLimitEnabled: Boolean
    var isTwistLimitEnabled: Boolean

    var targetOrientation: Vec4f

    fun setParentPose(pose: Mat4f)

    fun setChildPose(pose: Mat4f)

    fun setSwingLimit(zLimit: Float, yLimit: Float)

    fun setTwistLimit(lower: Float, upper: Float)

    fun setTargetOrientation(eulerX: Float, eulerY: Float, eulerZ: Float)
    fun setTargetOrientation(rot: Mat3f)
}