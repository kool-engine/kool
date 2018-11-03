package de.fabmax.kool.modules.physics.constraintSolver

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

/**
 * 1D constraint along a normal axis between bodyA and bodyB. It can be combined to solve contact and friction
 * constraints.
 *
 * Ported version of b3SolverConstraint
 * Bullet's original copyright notice is below:
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2012 Erwin Coumans  http://bulletphysics.org
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the use of this software.
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 *    If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is
 *    not required.
 * 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the original
 *    software.
 * 3. This notice may not be removed or altered from any source distribution.
 */
abstract class SolverConstraint {
    lateinit var solverBodyA: SolverBody
    lateinit var solverBodyB: SolverBody

    val contactNormal = MutableVec3f()
    val relPosACrossNormal = MutableVec3f()
    val relPosBCrossNormal = MutableVec3f()

    val angularComponentA = MutableVec3f()
    val angularComponentB = MutableVec3f()

    var appliedPushImpulse = 0f
    var appliedImpulse = 0f
    var friction = 0f
    var jacDiagABInv = 0f
    var rhs = 0f
    var cfm = 0f

    var lowerLimit = 0f
    var upperLimit = 0f

    protected val tmpVec1 = MutableVec3f()
    protected val tmpVec2 = MutableVec3f()
}

class ContactConstraint : SolverConstraint() {
    var frictionConstraint1: SolverConstraint? = null
    var frictionConstraint2: SolverConstraint? = null

    lateinit var originalContactPoint: ContactPoint

    val relPosA = MutableVec3f()
    val relPosB = MutableVec3f()
    val velocity = MutableVec3f()
    var relaxation = 0f
    var relVelocity = 0f
    var rhsPenetration = 0f

    fun setupContactConstraint(cp: ContactPoint, bodyA: SolverBody, bodyB: SolverBody, solverInfo: ContactSolverInfo) {
        originalContactPoint = cp
        solverBodyA = bodyA
        solverBodyB = bodyB
        relaxation = 1f

        val rbA = bodyA.originalBody
        val rbB = bodyB.originalBody

        relPosA.set(cp.positionWorldOnA).subtract(bodyA.worldTransform.getOrigin(tmpVec1))
        relPosB.set(cp.positionWorldOnB).subtract(bodyB.worldTransform.getOrigin(tmpVec1))

        relPosA.cross(cp.normalWorldOnB, relPosACrossNormal)
        angularComponentA.set(rbA.invInertiaTensor.transform(relPosACrossNormal, tmpVec1))

        cp.normalWorldOnB.cross(relPosB, relPosBCrossNormal)
        angularComponentB.set(rbB.invInertiaTensor.transform(relPosBCrossNormal, tmpVec1))

        val denomA = rbA.invMass + cp.normalWorldOnB * angularComponentA.cross(relPosA, tmpVec1)
        val denomB = rbB.invMass + cp.normalWorldOnB * relPosB.cross(angularComponentB, tmpVec1)

        val scaledDenom = relaxation / (denomA + denomB)
        jacDiagABInv = scaledDenom
        contactNormal.set(cp.normalWorldOnB)

        val penetration = cp.distance + solverInfo.linearSlop

        rbA.getVelocityInLocalPoint(relPosA, tmpVec1)
        rbB.getVelocityInLocalPoint(relPosB, tmpVec2)
        tmpVec1.subtract(tmpVec2, velocity)
        relVelocity = contactNormal * velocity

        friction = cp.combinedFriction

        var restitution = restitutionCurve(relVelocity, cp.combinedRestitution)
        if (restitution < 0f) {
            restitution = 0f
        }

        if (solverInfo.isSolverMode(ContactSolverInfo.SOLVER_USE_WARMSTARTING)) {
            appliedImpulse = cp.appliedImpulse * solverInfo.warmstartingFactor

            tmpVec1.set(contactNormal).mul(bodyA.invMass)
            bodyA.internalApplyImpulse(tmpVec1, angularComponentA, appliedImpulse)

            tmpVec1.set(contactNormal).mul(bodyB.invMass)
            bodyB.internalApplyImpulse(tmpVec1, angularComponentB, -appliedImpulse)
        } else {
            appliedImpulse = 0f
        }

        appliedPushImpulse = 0f

        val velADotN = contactNormal * bodyA.linearVelocity + relPosACrossNormal * bodyA.angularVelocity
        val velBDotN = -(contactNormal * bodyB.linearVelocity) + relPosBCrossNormal * bodyB.angularVelocity

        val relVel = velADotN + velBDotN
        var positionalError = 0f
        var velocityError = restitution - relVel

        var erp = solverInfo.erp
        if (!solverInfo.splitImpulse || penetration > solverInfo.splitImpulsePenetrationThreshold) {
            erp = solverInfo.erp2
        }

        if (penetration > 0) {
            velocityError -= penetration / solverInfo.timeStep
        } else {
            positionalError = -penetration * erp / solverInfo.timeStep
        }
        val penetrationImpulse = positionalError * scaledDenom
        val velocityImpulse = velocityError * scaledDenom

        if (!solverInfo.splitImpulse || penetration > solverInfo.splitImpulsePenetrationThreshold) {
            rhs = penetrationImpulse + velocityImpulse
            rhsPenetration = 0f
        } else {
            rhs = velocityImpulse
            rhsPenetration = penetrationImpulse
        }
        cfm = 0f
        lowerLimit = 0f
        upperLimit = 1e10f
    }

    private fun restitutionCurve(relVel: Float, restitution: Float) = restitution * -relVel
}

class FrictionConstraint : SolverConstraint() {
    lateinit var contactConstraint: ContactConstraint

    fun setupFrictionConstraint(normalAxis: Vec3f, contactConstraint: ContactConstraint, desiredVelocity: Float, cfmSlip: Float) {
        this.contactConstraint = contactConstraint

        contactNormal.set(normalAxis)
        solverBodyA = contactConstraint.solverBodyA
        solverBodyB = contactConstraint.solverBodyB
        friction = contactConstraint.originalContactPoint.combinedFriction
        appliedImpulse = 0f
        appliedPushImpulse = 0f

        val bodyA = solverBodyA
        val bodyB = solverBodyB
        val rbA = bodyA.originalBody
        val rbB = bodyB.originalBody

        contactConstraint.relPosA.cross(contactNormal, relPosACrossNormal)
        angularComponentA.set(Vec3f.ZERO)
        rbA.invInertiaTensor.transform(relPosACrossNormal, angularComponentA)

        contactNormal.cross(contactConstraint.relPosB, relPosBCrossNormal)
        angularComponentB.set(Vec3f.ZERO)
        rbB.invInertiaTensor.transform(relPosBCrossNormal, angularComponentB)

        val denomA = rbA.invMass + normalAxis * angularComponentA.cross(contactConstraint.relPosA, tmpVec1)
        val denomB = rbB.invMass + normalAxis * contactConstraint.relPosB.cross(angularComponentB, tmpVec1)

        jacDiagABInv = contactConstraint.relaxation / (denomA + denomB)

        val velADotN = contactNormal * bodyA.linearVelocity + relPosACrossNormal * bodyA.angularVelocity
        val velBDotN = -(contactNormal * bodyB.linearVelocity) + relPosBCrossNormal * bodyB.angularVelocity

        val relVel = velADotN + velBDotN

        val velocityError = desiredVelocity - relVel
        val velocityImpulse = velocityError * jacDiagABInv
        rhs = velocityImpulse
        cfm = cfmSlip
        lowerLimit = 0f
        upperLimit = 1e10f
    }
}

class RollingFrictionConstraint : SolverConstraint() {
    lateinit var contactConstraint: ContactConstraint

    fun setupRollingFrictionConstraint(normalAxis: Vec3f, contactConstraint: ContactConstraint, desiredVelocity: Float, cfmSlip: Float) {
        this.contactConstraint = contactConstraint

        contactNormal.set(normalAxis)
        solverBodyA = contactConstraint.solverBodyA
        solverBodyB = contactConstraint.solverBodyB
        friction = contactConstraint.originalContactPoint.combinedRollingFriction
        appliedImpulse = 0f
        appliedPushImpulse = 0f

        val bodyA = solverBodyA
        val bodyB = solverBodyB
        val rbA = bodyA.originalBody
        val rbB = bodyB.originalBody

        normalAxis.scale(-1f, relPosACrossNormal)
        angularComponentA.set(Vec3f.ZERO)
        rbA.invInertiaTensor.transform(relPosACrossNormal, angularComponentA)

        relPosBCrossNormal.set(normalAxis)
        angularComponentB.set(Vec3f.ZERO)
        rbB.invInertiaTensor.transform(relPosBCrossNormal, angularComponentB)

        // iMJaA
        rbA.invInertiaTensor.transform(relPosACrossNormal, tmpVec1)
        // iMJaB
        rbB.invInertiaTensor.transform(relPosBCrossNormal, tmpVec2)
        jacDiagABInv = 1f / (tmpVec1 * relPosACrossNormal + tmpVec2 * relPosBCrossNormal)


        val velADotN = contactNormal * bodyA.linearVelocity + relPosACrossNormal * bodyA.angularVelocity
        val velBDotN = -(contactNormal * bodyB.linearVelocity) + relPosBCrossNormal * bodyB.angularVelocity

        val relVel = velADotN + velBDotN

        val velocityError = desiredVelocity - relVel
        val velocityImpulse = velocityError * jacDiagABInv
        rhs = velocityImpulse
        cfm = cfmSlip
        lowerLimit = 0f
        upperLimit = 1e10f
    }
}
