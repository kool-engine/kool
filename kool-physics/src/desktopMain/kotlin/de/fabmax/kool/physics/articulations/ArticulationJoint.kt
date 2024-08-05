package de.fabmax.kool.physics.articulations

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.math.toRad
import de.fabmax.kool.physics.createPxArticulationDrive
import de.fabmax.kool.physics.createPxArticulationLimit
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import org.lwjgl.system.MemoryStack
import physx.physics.*

class ArticulationJointImpl(val pxJoint: PxArticulationJointReducedCoordinate) : ArticulationJoint {

    override var jointType: ArticulationJointType
        get() = pxJoint.jointType.toArticulationJointType()
        set(value) { pxJoint.jointType = value.pxVal }

    init {
        pxJoint.jointType = PxArticulationJointTypeEnum.eFIX
    }

    override fun setParentPose(pose: PoseF) {
        MemoryStack.stackPush().use { mem ->
            pxJoint.parentPose = pose.toPxTransform(mem.createPxTransform())
        }
    }

    override fun setChildPose(pose: PoseF) {
        MemoryStack.stackPush().use { mem ->
            pxJoint.childPose = pose.toPxTransform(mem.createPxTransform())
        }
    }

    override fun setAxisMotion(axis: ArticulationJointAxis, motionType: ArticulationMotionMode) {
        pxJoint.setMotion(axis.pxVal, motionType.pxVal)
    }

    override fun setAxisLimits(axis: ArticulationJointAxis, low: Float, high: Float) {
        MemoryStack.stackPush().use { mem ->
            val limit = mem.createPxArticulationLimit(low, high)
            pxJoint.setLimitParams(axis.pxVal, limit)
        }
    }

    override fun setupSphericalSymmetrical(twistLimitDeg: Float, swingLimitDeg: Float) {
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

    override fun setupSpherical(twistMinDeg: Float, twistMaxDeg: Float, swingMinDeg: Float, swingMaxDeg: Float) =
        setupSpherical(twistMinDeg, twistMaxDeg, swingMinDeg, swingMaxDeg, swingMinDeg, swingMaxDeg)

    override fun setupSpherical(
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

    override fun setDriveParams(
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

    override fun setDriveTarget(axis: ArticulationJointAxis, target: Float) {
        pxJoint.setDriveTarget(axis.pxVal, target)
    }

    override fun setJointPosition(axis: ArticulationJointAxis, jointPos: Float) {
        pxJoint.setJointPosition(axis.pxVal, jointPos)
    }

    companion object {
        private fun PxArticulationJointTypeEnum.toArticulationJointType(): ArticulationJointType = when (this) {
            PxArticulationJointTypeEnum.eFIX -> ArticulationJointType.FIX
            PxArticulationJointTypeEnum.ePRISMATIC -> ArticulationJointType.PRISMATIC
            PxArticulationJointTypeEnum.eREVOLUTE -> ArticulationJointType.REVOLUTE
            PxArticulationJointTypeEnum.eSPHERICAL -> ArticulationJointType.SPHERICAL
            PxArticulationJointTypeEnum.eUNDEFINED -> throw IllegalStateException("Invalid joint type: $this")
        }

        private val ArticulationJointType.pxVal: PxArticulationJointTypeEnum get() = when (this) {
            ArticulationJointType.FIX -> PxArticulationJointTypeEnum.eFIX
            ArticulationJointType.PRISMATIC -> PxArticulationJointTypeEnum.ePRISMATIC
            ArticulationJointType.REVOLUTE -> PxArticulationJointTypeEnum.eREVOLUTE
            ArticulationJointType.SPHERICAL -> PxArticulationJointTypeEnum.eSPHERICAL
        }

        private val ArticulationJointAxis.pxVal: PxArticulationAxisEnum get() = when (this) {
            ArticulationJointAxis.ROT_TWIST -> PxArticulationAxisEnum.eTWIST
            ArticulationJointAxis.ROT_SWING1 -> PxArticulationAxisEnum.eSWING1
            ArticulationJointAxis.ROT_SWING2 -> PxArticulationAxisEnum.eSWING2
            ArticulationJointAxis.LINEAR_X -> PxArticulationAxisEnum.eX
            ArticulationJointAxis.LINEAR_Y -> PxArticulationAxisEnum.eY
            ArticulationJointAxis.LINEAR_Z -> PxArticulationAxisEnum.eZ
        }

        private val ArticulationMotionMode.pxVal: PxArticulationMotionEnum get() = when (this) {
            ArticulationMotionMode.FREE -> PxArticulationMotionEnum.eFREE
            ArticulationMotionMode.LIMITED -> PxArticulationMotionEnum.eLIMITED
            ArticulationMotionMode.LOCKED -> PxArticulationMotionEnum.eLOCKED
        }

        private val ArticulationDriveType.pxVal: PxArticulationDriveTypeEnum get() = when (this) {
            ArticulationDriveType.ACCELERATION -> PxArticulationDriveTypeEnum.eACCELERATION
            ArticulationDriveType.FORCE -> PxArticulationDriveTypeEnum.eFORCE
            ArticulationDriveType.NONE -> PxArticulationDriveTypeEnum.eNONE
        }
    }
}