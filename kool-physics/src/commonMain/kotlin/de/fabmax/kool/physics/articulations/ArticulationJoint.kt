package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.Mat4f

expect enum class ArticulationJointType {
    /**
     * All joint axes, i.e. degrees of freedom (DOFs) locked
     */
    FIX,

    /**
     * Single linear DOF, e.g. cart on a rail
     */
    PRISMATIC,

    /**
     * Single rotational DOF, e.g. an elbow joint or a rotational motor, position wrapped at 2pi radians
     */
    REVOLUTE,

    /**
     * Ball and socket joint with two or three DOFs
     */
    SPHERICAL
}

expect enum class ArticulationJointAxis {
    /**
     * Rotational about eX
     */
    ROT_TWIST,
    /**
     * Rotational about eY
     */
    ROT_SWING1,
    /**
     * Rotational about eZ
     */
    ROT_SWING2,

    LINEAR_X,
    LINEAR_Y,
    LINEAR_Z
}

expect enum class ArticulationMotionMode {
    FREE,
    LIMITED,
    LOCKED
}

expect enum class ArticulationDriveType {
    ACCELERATION,
    FORCE,
    NONE,
    TARGET,
    VELOCITY
}

expect class ArticulationJoint {

    var jointType: ArticulationJointType

//    var damping: Float
//    var stiffness: Float
//
//    var tangentialDamping: Float
//    var tangentialStiffness: Float
//
//    var isSwingLimitEnabled: Boolean
//    var isTwistLimitEnabled: Boolean
//
//    var targetOrientation: Vec4f

    fun setParentPose(pose: Mat4f)
    fun setChildPose(pose: Mat4f)

//    fun setSwingLimit(zLimit: Float, yLimit: Float)
//
//    fun setTwistLimit(lower: Float, upper: Float)
//
//    fun setTargetOrientation(eulerX: Float, eulerY: Float, eulerZ: Float)
//    fun setTargetOrientation(rot: Mat3f)

    fun setAxisMotion(axis: ArticulationJointAxis, motionType: ArticulationMotionMode)
    fun setAxisLimits(axis: ArticulationJointAxis, low: Float, high: Float)

    fun setupSphericalSymmetrical(twistLimitDeg: Float, swingLimitDeg: Float)
    fun setupSpherical(twistMinDeg: Float, twistMaxDeg: Float, swingMinDeg: Float, swingMaxDeg: Float)
    fun setupSpherical(twistMinDeg: Float, twistMaxDeg: Float, swing1MinDeg: Float, swing1MaxDeg: Float, swing2MinDeg: Float, swing2MaxDeg: Float)

    fun setDriveParams(axis: ArticulationJointAxis, driveType: ArticulationDriveType, damping: Float, stiffness: Float, maxForce: Float)
    fun setDriveTarget(axis: ArticulationJointAxis, target: Float)
    fun setJointPosition(axis: ArticulationJointAxis, jointPos: Float)
}
