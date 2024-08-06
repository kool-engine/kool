package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.JointComponentData
import de.fabmax.kool.editor.data.JointData
import de.fabmax.kool.math.PoseF
import de.fabmax.kool.math.getPose
import de.fabmax.kool.math.toRad
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.physics.joints.*
import de.fabmax.kool.util.logE

class JointComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<JointComponentData> = ComponentInfo(JointComponentData())
) : GameEntityDataComponent<JointComponentData>(gameEntity, componentInfo) {

    var joint: Joint? = null
        private set

    override suspend fun applyComponent() {
        super.applyComponent()
        makeJoint()
    }

    override fun onDataChanged(oldData: JointComponentData, newData: JointComponentData) {
        super.onDataChanged(oldData, newData)
        makeJoint()
    }

    override fun onStart() {
        super.onStart()
        makeJoint()
    }

    private fun makeJoint() {
        joint?.release()
        joint = null

        val (actorComponentA, actorComponentB) = getBodies()
        if (actorComponentB == null) {
            return
        }

        val poseA = actorComponentA?.let { (it.gameEntity.globalToLocalF * gameEntity.localToGlobalF).getPose() }
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
        }
    }

    private fun JointData.Fixed.createJoint(bodyA: RigidActor?, bodyB: RigidActor, poseA: PoseF, poseB: PoseF): FixedJoint {
        val j = FixedJoint(bodyA, bodyB, poseA, poseB)
        if (isBreakable) {
            j.setBreakForce(breakForce, breakTorque)
        }
        return j
    }

    private fun JointData.Distance.createJoint(bodyA: RigidActor?, bodyB: RigidActor, poseA: PoseF, poseB: PoseF): DistanceJoint {
        val j = DistanceJoint(bodyA, bodyB, poseA, poseB)
        if (minDistance > 0f) {
            j.setMinDistance(minDistance)
        }
        if (maxDistance > 0f) {
            j.setMaxDistance(minDistance)
        }
        if (isBreakable) {
            j.setBreakForce(breakForce, breakTorque)
        }
        return j
    }

    private fun JointData.Prismatic.createJoint(bodyA: RigidActor?, bodyB: RigidActor, poseA: PoseF, poseB: PoseF): PrismaticJoint {
        val j = PrismaticJoint(bodyA, bodyB, poseA, poseB)
        if (isLimited) {
            if (stiffness > 0f && damping > 0f) {
                j.setSoftLimit(lowerLimit, upperLimit, stiffness, damping)
            } else {
                j.setHardLimit(lowerLimit, upperLimit)
            }
        }
        if (isBreakable) {
            j.setBreakForce(breakForce, breakTorque)
        }
        return j
    }

    private fun JointData.Revolute.createJoint(bodyA: RigidActor?, bodyB: RigidActor, poseA: PoseF, poseB: PoseF): RevoluteJoint {
        val j = RevoluteJoint(bodyA, bodyB, poseA, poseB)
        if (isMotor) {
            j.enableAngularMotor(motorSpeed, motorTorque)
        }
        if (isBreakable) {
            j.setBreakForce(breakForce, breakTorque)
        }
        return j
    }

    private fun JointData.Spherical.createJoint(bodyA: RigidActor?, bodyB: RigidActor, poseA: PoseF, poseB: PoseF): SphericalJoint {
        val j = SphericalJoint(bodyA, bodyB, poseA, poseB)
        if (isLimited) {
            if (stiffness > 0f && damping > 0f) {
                j.setSoftLimitCone(limitAngleY.toRad(), limitAngleZ.toRad(), stiffness, damping)
            } else {
                j.setHardLimitCone(limitAngleY.toRad(), limitAngleZ.toRad())
            }
        }
        if (isBreakable) {
            j.setBreakForce(breakForce, breakTorque)
        }
        return j
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
}