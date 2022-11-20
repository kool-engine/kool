package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.physics.createPxArticulationDrive
import de.fabmax.kool.physics.createPxArticulationLimit
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import org.lwjgl.system.MemoryStack
import physx.physics.*

actual enum class ArticulationJointType(val pxVal: Int) {
    /**
     * All joint axes, i.e. degrees of freedom (DOFs) locked
     */
    FIX(PxArticulationJointTypeEnum.eFIX),

    /**
     * Single linear DOF, e.g. cart on a rail
     */
    PRISMATIC(PxArticulationJointTypeEnum.ePRISMATIC),

    /**
     * Single rotational DOF, e.g. an elbow joint or a rotational motor, position wrapped at 2pi radians
     */
    REVOLUTE(PxArticulationJointTypeEnum.eREVOLUTE),

    /**
     * Ball and socket joint with two or three DOFs
     */
    SPHERICAL(PxArticulationJointTypeEnum.eSPHERICAL);

    companion object {
        fun fromPx(pxVal: Int) = when(pxVal) {
            PxArticulationJointTypeEnum.eFIX -> FIX
            PxArticulationJointTypeEnum.ePRISMATIC -> PRISMATIC
            PxArticulationJointTypeEnum.eREVOLUTE -> REVOLUTE
            PxArticulationJointTypeEnum.eSPHERICAL -> SPHERICAL
            else -> throw IllegalStateException("Invalid joint type: $this")
        }
    }
}

actual enum class ArticulationJointAxis(val pxVal: Int) {
    ROT_TWIST(PxArticulationAxisEnum.eTWIST),
    ROT_SWING1(PxArticulationAxisEnum.eSWING1),
    ROT_SWING2(PxArticulationAxisEnum.eSWING2),

    LINEAR_X(PxArticulationAxisEnum.eX),
    LINEAR_Y(PxArticulationAxisEnum.eY),
    LINEAR_Z(PxArticulationAxisEnum.eZ)
}

actual enum class ArticulationMotionMode(val pxVal: Int) {
    FREE(PxArticulationMotionEnum.eFREE),
    LIMITED(PxArticulationMotionEnum.eLIMITED),
    LOCKED(PxArticulationMotionEnum.eLOCKED)
}

actual enum class ArticulationDriveType(val pxVal: Int) {
    ACCELERATION(PxArticulationDriveTypeEnum.eACCELERATION),
    FORCE(PxArticulationDriveTypeEnum.eFORCE),
    NONE(PxArticulationDriveTypeEnum.eNONE),
    TARGET(PxArticulationDriveTypeEnum.eTARGET),
    VELOCITY(PxArticulationDriveTypeEnum.eVELOCITY)
}

actual class ArticulationJoint(val pxJoint: PxArticulationJointReducedCoordinate) {

    actual var jointType: ArticulationJointType
        get() = ArticulationJointType.fromPx(pxJoint.jointType)
        set(value) { pxJoint.jointType = value.pxVal }

    init {
        pxJoint.jointType = PxArticulationJointTypeEnum.eFIX
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

    actual fun setAxisMotion(axis: ArticulationJointAxis, motionType: ArticulationMotionMode) {
        pxJoint.setMotion(axis.pxVal, motionType.pxVal)
    }

    actual fun setAxisLimits(axis: ArticulationJointAxis, low: Float, high: Float) {
        MemoryStack.stackPush().use { mem ->
            val limit = mem.createPxArticulationLimit(low, high)
            pxJoint.setLimitParams(axis.pxVal, limit)
        }
    }

    actual fun setupSphericalSymmetrical(twistLimitDeg: Float, swingLimitDeg: Float) {
        jointType = ArticulationJointType.SPHERICAL
        if (twistLimitDeg > 0f) {
            if (twistLimitDeg < 360f) {
                setAxisMotion(ArticulationJointAxis.ROT_TWIST, ArticulationMotionMode.LIMITED)
                val limit = twistLimitDeg.toRad()
                setAxisLimits(ArticulationJointAxis.ROT_TWIST, -limit, limit)
            } else {
                setAxisMotion(ArticulationJointAxis.ROT_TWIST, ArticulationMotionMode.FREE)
            }
        } else {
            setAxisMotion(ArticulationJointAxis.ROT_TWIST, ArticulationMotionMode.LOCKED)
        }
        if (swingLimitDeg > 0f) {
            if (swingLimitDeg < 360f) {
                setAxisMotion(ArticulationJointAxis.ROT_SWING1, ArticulationMotionMode.LIMITED)
                setAxisMotion(ArticulationJointAxis.ROT_SWING2, ArticulationMotionMode.LIMITED)
                val limit = swingLimitDeg.toRad()
                setAxisLimits(ArticulationJointAxis.ROT_SWING1, -limit, limit)
                setAxisLimits(ArticulationJointAxis.ROT_SWING2, -limit, limit)
            } else {
                setAxisMotion(ArticulationJointAxis.ROT_SWING1, ArticulationMotionMode.FREE)
                setAxisMotion(ArticulationJointAxis.ROT_SWING2, ArticulationMotionMode.FREE)
            }
        }
    }

    actual fun setupSpherical(twistMinDeg: Float, twistMaxDeg: Float, swingMinDeg: Float, swingMaxDeg: Float) =
        setupSpherical(twistMinDeg, twistMaxDeg, swingMinDeg, swingMaxDeg, swingMinDeg, swingMaxDeg)

    actual fun setupSpherical(
        twistMinDeg: Float, twistMaxDeg: Float,
        swing1MinDeg: Float, swing1MaxDeg: Float,
        swing2MinDeg: Float, swing2MaxDeg: Float
    ) {
        jointType = ArticulationJointType.SPHERICAL

        setAxisMotion(ArticulationJointAxis.ROT_TWIST, ArticulationMotionMode.LIMITED)
        setAxisLimits(ArticulationJointAxis.ROT_TWIST, twistMinDeg.toRad(), twistMaxDeg.toRad())

        setAxisMotion(ArticulationJointAxis.ROT_SWING1, ArticulationMotionMode.LIMITED)
        setAxisMotion(ArticulationJointAxis.ROT_SWING2, ArticulationMotionMode.LIMITED)
        setAxisLimits(ArticulationJointAxis.ROT_SWING1, swing1MinDeg.toRad(), swing1MaxDeg.toRad())
        setAxisLimits(ArticulationJointAxis.ROT_SWING2, swing2MinDeg.toRad(), swing2MaxDeg.toRad())
    }

    actual fun setDriveParams(
        axis: ArticulationJointAxis,
        driveType: ArticulationDriveType,
        damping: Float,
        stiffness: Float,
        maxForce: Float
    ) {
        MemoryStack.stackPush().use { mem ->
            val drive = mem.createPxArticulationDrive()
            drive.driveType = driveType.pxVal
            drive.stiffness = stiffness
            drive.damping = damping
            drive.maxForce = maxForce
            pxJoint.setDriveParams(axis.pxVal, drive)
        }
    }

    actual fun setDriveTarget(axis: ArticulationJointAxis, target: Float) {
        pxJoint.setDriveTarget(axis.pxVal, target)
    }

    actual fun setJointPosition(axis: ArticulationJointAxis, jointPos: Float) {
        pxJoint.setJointPosition(axis.pxVal, jointPos)
    }
}