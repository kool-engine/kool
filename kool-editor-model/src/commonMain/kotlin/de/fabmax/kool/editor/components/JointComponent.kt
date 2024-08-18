package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.math.PoseF
import de.fabmax.kool.math.getPose
import de.fabmax.kool.math.rad
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.physics.joints.*
import de.fabmax.kool.util.logE

class JointComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<JointComponentData> = ComponentInfo(JointComponentData())
) : GameEntityDataComponent<JointComponentData>(gameEntity, componentInfo), PhysicsComponent {

    var joint: Joint? = null
        private set

    var actorComponentA: RigidActorComponent? = null
        private set
    var actorComponentB: RigidActorComponent? = null
        private set

    val actorA: RigidActor? get() = actorComponentA?.rigidActor
    val actorB: RigidActor? get() = actorComponentB?.rigidActor

    override suspend fun applyComponent() {
        super.applyComponent()
        makeJoint()
    }

    override fun onDataChanged(oldData: JointComponentData, newData: JointComponentData) {
        super.onDataChanged(oldData, newData)
        if (!updateJoint(oldData, newData)) {
            makeJoint()
        }
        (actorA as? RigidDynamic)?.wakeUp()
        (actorB as? RigidDynamic)?.wakeUp()
    }

    override fun onStart() {
        super.onStart()
        makeJoint()
    }

    private fun updateJoint(oldData: JointComponentData, newData: JointComponentData): Boolean {
        if (oldData.bodyA != newData.bodyA || oldData.bodyB != newData.bodyB) return false
        val j = joint ?: return false

        if (newData.isBreakable) j.enableBreakage(newData.breakForce, newData.breakTorque) else j.disableBreakage()
        j.isChildCollisionEnabled = newData.isCollisionEnabled

        return when (val jData = newData.jointData) {
            is JointData.Fixed -> true
            is JointData.Distance -> jData.updateJoint(j)
            is JointData.Prismatic -> jData.updateJoint(j)
            is JointData.Revolute -> jData.updateJoint(j)
            is JointData.Spherical -> jData.updateJoint(j)
            is JointData.D6 -> jData.updateJoint(j)
        }
    }

    private fun makeJoint() {
        joint?.release()
        joint = null

        val (actorComponentA, actorComponentB) = getBodies()
        this.actorComponentA = actorComponentA
        this.actorComponentB = actorComponentB
        if (actorComponentB == null) {
            return
        }

        val poseA = actorComponentA
            ?.let { (it.gameEntity.globalToLocalF * gameEntity.localToGlobalF).getPose() }
            ?: gameEntity.localToGlobalF.getPose()
        val poseB = (actorComponentB.gameEntity.globalToLocalF * gameEntity.localToGlobalF).getPose()

        val bodyA = actorComponentA?.rigidActor
        val bodyB = actorComponentB.rigidActor ?: return
        joint = createJoint(bodyA, bodyB, poseA, poseB).apply {
            isChildCollisionEnabled = data.isCollisionEnabled
            if (data.isBreakable) enableBreakage(data.breakForce, data.breakTorque) else disableBreakage()
        }
    }

    override fun destroyComponent() {
        super.destroyComponent()
        joint?.release()
        joint = null
    }

    private fun createJoint(bodyA: RigidActor?, bodyB: RigidActor, poseA: PoseF, poseB: PoseF): Joint {
        if (bodyA is RigidDynamic) {
            bodyA.wakeUp()
        }
        if (bodyB is RigidDynamic) {
            bodyB.wakeUp()
        }
        return when (val d = data.jointData) {
            is JointData.Fixed -> FixedJoint(bodyA, bodyB, poseA, poseB)
            is JointData.Distance -> d.createJoint(bodyA, bodyB, poseA, poseB)
            is JointData.Prismatic -> d.createJoint(bodyA, bodyB, poseA, poseB)
            is JointData.Revolute -> d.createJoint(bodyA, bodyB, poseA, poseB)
            is JointData.Spherical -> d.createJoint(bodyA, bodyB, poseA, poseB)
            is JointData.D6 -> d.createJoint(bodyA, bodyB, poseA, poseB)
        }
    }

    private fun JointData.Distance.createJoint(bodyA: RigidActor?, bodyB: RigidActor, poseA: PoseF, poseB: PoseF): DistanceJoint {
        return DistanceJoint(bodyA, bodyB, poseA, poseB).also { updateJoint(it) }
    }

    private fun JointData.Prismatic.createJoint(bodyA: RigidActor?, bodyB: RigidActor, poseA: PoseF, poseB: PoseF): PrismaticJoint {
        return PrismaticJoint(bodyA, bodyB, poseA, poseB).also { updateJoint(it) }
    }

    private fun JointData.Revolute.createJoint(bodyA: RigidActor?, bodyB: RigidActor, poseA: PoseF, poseB: PoseF): RevoluteJoint {
        return RevoluteJoint(bodyA, bodyB, poseA, poseB).also { updateJoint(it) }
    }

    private fun JointData.Spherical.createJoint(bodyA: RigidActor?, bodyB: RigidActor, poseA: PoseF, poseB: PoseF): SphericalJoint {
        return SphericalJoint(bodyA, bodyB, poseA, poseB).also { updateJoint(it) }
    }

    private fun JointData.D6.createJoint(bodyA: RigidActor?, bodyB: RigidActor, poseA: PoseF, poseB: PoseF): D6Joint {
        return D6Joint(bodyA, bodyB, poseA, poseB).also { updateJoint(it) }
    }

    private fun JointData.Distance.updateJoint(joint: Joint): Boolean {
        val j = joint as? DistanceJoint ?: return false
        if (minDistance > 0f) j.setMinDistance(minDistance) else j.clearMinDistance()
        if (maxDistance > 0f) j.setMaxDistance(maxDistance) else j.clearMaxDistance()
        return true
    }

    private fun JointData.Prismatic.updateJoint(joint: Joint): Boolean {
        val j = joint as? PrismaticJoint ?: return false
        if (limit != null) {
            j.enableLimit(limit.limit1, limit.limit2, limit.limitBehavior)
        } else {
            j.disableLimit()
        }
        return true
    }

    private fun JointData.Revolute.updateJoint(joint: Joint): Boolean {
        val j = joint as? RevoluteJoint ?: return false
        if (isMotor) j.enableAngularMotor(driveSpeed, driveTorque) else j.disableAngularMotor()
        if (limit != null) {
            j.enableLimit(limit.limit1.rad, limit.limit2.rad, limit.limitBehavior)
        } else {
            j.disableLimit()
        }
        return true
    }

    private fun JointData.Spherical.updateJoint(joint: Joint): Boolean {
        val j = joint as? SphericalJoint ?: return false
        if (limit != null) {
            j.enableLimit(limit.limit1.rad, limit.limit2.rad, limit.limitBehavior)
        } else {
            j.disableLimit()
        }
        return true
    }

    private fun JointData.D6.updateJoint(joint: Joint): Boolean {
        val j = joint as? D6Joint ?: return false

        if (linearLimitX != null) {
            j.enableLinearLimitX(linearLimitX.limit1, linearLimitX.limit2, linearLimitX.limitBehavior)
        } else {
            j.disableLinearLimitX()
        }
        if (linearLimitY != null) {
            j.enableLinearLimitY(linearLimitY.limit1, linearLimitY.limit2, linearLimitY.limitBehavior)
        } else {
            j.disableLinearLimitY()
        }
        if (linearLimitZ != null) {
            j.enableLinearLimitZ(linearLimitZ.limit1, linearLimitZ.limit2, linearLimitZ.limitBehavior)
        } else {
            j.disableLinearLimitZ()
        }
        if (angularLimitX != null) {
            j.enableAngularLimitX(angularLimitX.limit1.rad, angularLimitX.limit2.rad, angularLimitX.limitBehavior)
        } else {
            j.disableAngularLimitX()
        }
        if (angularLimitY != null) {
            j.enableAngularLimitY(angularLimitY.limit1.rad, angularLimitY.limit2.rad, angularLimitY.limitBehavior)
        } else {
            j.disableAngularLimitY()
        }
        if (angularLimitZ != null) {
            j.enableAngularLimitZ(angularLimitZ.limit1.rad, angularLimitZ.limit2.rad, angularLimitZ.limitBehavior)
        } else {
            j.disableAngularLimitZ()
        }

        if (linearDriveX != null) j.enableLinearDriveX(linearDriveX.jointDrive) else j.disableLinearDriveX()
        if (linearDriveY != null) j.enableLinearDriveY(linearDriveY.jointDrive) else j.disableLinearDriveY()
        if (linearDriveZ != null) j.enableLinearDriveZ(linearDriveZ.jointDrive) else j.disableLinearDriveZ()
        if (angularDriveX != null) j.enableAngularDriveX(angularDriveX.jointDrive) else j.disableAngularDriveX()
        if (angularDriveY != null) j.enableAngularDriveY(angularDriveY.jointDrive) else j.disableAngularDriveY()
        if (angularDriveZ != null) j.enableAngularDriveZ(angularDriveZ.jointDrive) else j.disableAngularDriveZ()

        j.linearMotionX = linearMotionX
        j.linearMotionY = linearMotionY
        j.linearMotionZ = linearMotionZ
        j.angularMotionX = angularMotionX
        j.angularMotionY = angularMotionY
        j.angularMotionZ = angularMotionZ

        return true
    }

    private fun getBodies(): Pair<RigidActorComponent?, RigidActorComponent?> {
        val entityA = scene.sceneEntities[data.bodyA]
        val entityB = scene.sceneEntities[data.bodyB]
        if (entityA == null && data.bodyA != EntityId.NULL) {
            logE { "Failed getting body A for joint component of entity ${gameEntity.name}: Entity not found (id=${data.bodyA})" }
        }
        if (entityB == null && data.bodyB != EntityId.NULL) {
            logE { "Failed getting body B for joint component of entity ${gameEntity.name}: Entity not found (id=${data.bodyB})" }
        }

        val bodyA = entityA?.getComponent<RigidActorComponent>()
        val bodyB = entityB?.getComponent<RigidActorComponent>()
        if (entityA != null && bodyA == null) {
            logE { "Failed getting body A for joint component of entity ${gameEntity.name}: Entity has no RigidActorComponent" }
        }
        if (entityB != null && bodyB == null) {
            logE { "Failed getting body B for joint component of entity ${gameEntity.name}: Entity has no RigidActorComponent" }
        }
        return bodyA to bodyB
    }

    private val LimitData.limitBehavior: LimitBehavior get() = LimitBehavior(stiffness, damping, restitution, bounceThreshold)
    private val D6DriveData.jointDrive: D6JointDrive get() = D6JointDrive(targetVelocity, damping, stiffness, forceLimit, isAcceleration)
}