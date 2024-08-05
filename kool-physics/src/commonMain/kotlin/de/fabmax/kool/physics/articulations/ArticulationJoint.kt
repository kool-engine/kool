package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.PoseF

enum class ArticulationJointType {
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

enum class ArticulationJointAxis {
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

enum class ArticulationMotionMode {
    FREE,
    LIMITED,
    LOCKED
}

enum class ArticulationDriveType {
    ACCELERATION,
    FORCE,
    NONE
}

interface ArticulationJoint {

    var jointType: ArticulationJointType

    fun setParentPose(pose: PoseF)
    fun setChildPose(pose: PoseF)

    fun setAxisMotion(axis: ArticulationJointAxis, motionType: ArticulationMotionMode)
    fun setAxisLimits(axis: ArticulationJointAxis, low: Float, high: Float)

    fun setupSphericalSymmetrical(twistLimitDeg: Float, swingLimitDeg: Float)
    fun setupSpherical(twistMinDeg: Float, twistMaxDeg: Float, swingMinDeg: Float, swingMaxDeg: Float)
    fun setupSpherical(twistMinDeg: Float, twistMaxDeg: Float, swing1MinDeg: Float, swing1MaxDeg: Float, swing2MinDeg: Float, swing2MaxDeg: Float)

    fun setDriveParams(axis: ArticulationJointAxis, driveType: ArticulationDriveType, damping: Float, stiffness: Float, maxForce: Float)
    fun setDriveTarget(axis: ArticulationJointAxis, target: Float)
    fun setJointPosition(axis: ArticulationJointAxis, jointPos: Float)
}
