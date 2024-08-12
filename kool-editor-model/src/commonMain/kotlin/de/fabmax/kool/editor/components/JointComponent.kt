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

        return when (val jData = newData.jointData) {
            is JointData.Distance -> jData.updateJoint(j)
            is JointData.Fixed -> jData.updateJoint(j)
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
        joint = createJoint(bodyA, bodyB, poseA, poseB)
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
            is JointData.Fixed -> d.createJoint(bodyA, bodyB, poseA, poseB)
            is JointData.Distance -> d.createJoint(bodyA, bodyB, poseA, poseB)
            is JointData.Prismatic -> d.createJoint(bodyA, bodyB, poseA, poseB)
            is JointData.Revolute -> d.createJoint(bodyA, bodyB, poseA, poseB)
            is JointData.Spherical -> d.createJoint(bodyA, bodyB, poseA, poseB)
            is JointData.D6 -> d.createJoint(bodyA, bodyB, poseA, poseB)
        }
    }

    private fun JointData.Fixed.createJoint(bodyA: RigidActor?, bodyB: RigidActor, poseA: PoseF, poseB: PoseF): FixedJoint {
        return FixedJoint(bodyA, bodyB, poseA, poseB).also { updateJoint(it) }
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

    private fun JointData.Fixed.updateJoint(joint: Joint): Boolean {
        val j = joint as? FixedJoint ?: return false
        if (isBreakable) j.enableBreakage(breakForce, breakTorque) else j.disableBreakage()
        return true
    }

    private fun JointData.Distance.updateJoint(joint: Joint): Boolean {
        val j = joint as? DistanceJoint ?: return false
        if (minDistance > 0f) j.setMinDistance(minDistance) else j.clearMinDistance()
        if (maxDistance > 0f) j.setMaxDistance(maxDistance) else j.clearMaxDistance()
        if (isBreakable) j.enableBreakage(breakForce, breakTorque) else j.disableBreakage()
        return true
    }

    private fun JointData.Prismatic.updateJoint(joint: Joint): Boolean {
        val j = joint as? PrismaticJoint ?: return false
        if (isBreakable) j.enableBreakage(breakForce, breakTorque) else j.disableBreakage()
        if (limit != null) {
            j.enableLimit(limit.limit1, limit.limit2, limit.limitBehavior)
        } else {
            j.disableLimit()
        }
        return true
    }

    private fun JointData.Revolute.updateJoint(joint: Joint): Boolean {
        val j = joint as? RevoluteJoint ?: return false
        if (isMotor) j.enableAngularMotor(motorSpeed, motorTorque) else j.disableAngularMotor()
        if (isBreakable) j.enableBreakage(breakForce, breakTorque) else j.disableBreakage()
        if (limit != null) {
            j.enableLimit(limit.limit1.rad, limit.limit2.rad, limit.limitBehavior)
        } else {
            j.disableLimit()
        }
        return true
    }

    private fun JointData.Spherical.updateJoint(joint: Joint): Boolean {
        val j = joint as? SphericalJoint ?: return false
        if (isBreakable) j.enableBreakage(breakForce, breakTorque) else j.disableBreakage()
        if (limit != null) {
            j.enableLimit(limit.limit1.rad, limit.limit2.rad, limit.limitBehavior)
        } else {
            j.disableLimit()
        }
        return true
    }

    private fun JointData.D6.updateJoint(joint: Joint): Boolean {
        val j = joint as? D6Joint ?: return false
        if (isBreakable) j.enableBreakage(breakForce, breakTorque) else j.disableBreakage()

        if (limitX != null) {
            j.enableLinearLimitX(limitX.limit1, limitX.limit2, limitX.limitBehavior)
        } else {
            j.disableLinearLimitX()
        }
        if (limitY != null) {
            j.enableLinearLimitX(limitY.limit1, limitY.limit2, limitY.limitBehavior)
        } else {
            j.disableLinearLimitY()
        }
        if (limitZ != null) {
            j.enableLinearLimitX(limitZ.limit1, limitZ.limit2, limitZ.limitBehavior)
        } else {
            j.disableLinearLimitZ()
        }

        j.motionX = motionX
        j.motionY = motionY
        j.motionZ = motionZ
        j.motionTwist = motionTwist
        j.motionSwingY = motionSwingY
        j.motionSwingZ = motionSwingZ

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
}