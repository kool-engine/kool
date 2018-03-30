package de.fabmax.kool.physics.constraintSolver

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
class SolverConstraint {
    val relPosACrossNormal = MutableVec3f()
    val relPosBCrossNormal = MutableVec3f()
    val contactNormal = MutableVec3f()

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
    var rhsPenetration = 0f

    // fixme: use dedicated (sub-)classes for contact, friction and rolling-friction constraints
    // fixme: for friction and rolling-friction constraints this points to the parent contact constraint
    var contactConstraint: SolverConstraint? = null
    // fixme: for contact constraints these point to the child friction constraints
    var frictionConstraint1: SolverConstraint? = null
    var frictionConstraint2: SolverConstraint? = null

    var originalContactPoint: ContactPoint? = null

    lateinit var solverBodyA: SolverBody
    lateinit var solverBodyB: SolverBody

    // those are only needed for contact constraints
    val relPosA = MutableVec3f()
    val relPosB = MutableVec3f()
    val velocity = MutableVec3f()
    var relaxation = 0f
    var relVelocity = 0f

    private val tmpVec1 = MutableVec3f()
    private val tmpVec2 = MutableVec3f()

    fun setupContactConstraint(cp: ContactPoint, bodyA: SolverBody, bodyB: SolverBody, solverInfo: ContactSolverInfo) {
        originalContactPoint = cp
        solverBodyA = bodyA
        solverBodyB = bodyB

        val rbA = bodyA.originalBody
        val rbB = bodyB.originalBody

        relPosA.set(cp.positionWorldOnA).subtract(bodyA.worldTransform.getOrigin(tmpVec1))
        relPosB.set(cp.positionWorldOnB).subtract(bodyB.worldTransform.getOrigin(tmpVec1))

        relaxation = 1f

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

        //println("n=$contactNormal, pene=$penetration, relV=$relVelocity, rhs=$rhs")

    }

    fun setupFrictionConstraint(normalAxis: Vec3f, contactConstraint: SolverConstraint, desiredVelocity: Float, cfmSlip: Float) {
        contactNormal.set(normalAxis)
        solverBodyA = contactConstraint.solverBodyA
        solverBodyB = contactConstraint.solverBodyB
        originalContactPoint = null

        val bodyA = solverBodyA
        val bodyB = solverBodyB
        val rbA = bodyA.originalBody
        val rbB = bodyB.originalBody

        friction = contactConstraint.originalContactPoint?.combinedFriction ?: 0f
        originalContactPoint = null

        appliedImpulse = 0f
        appliedPushImpulse = 0f

        relPosA.set(contactConstraint.relPosA)
        relPosA.cross(contactNormal, relPosACrossNormal)
        angularComponentA.set(Vec3f.ZERO)
        rbA.invInertiaTensor.transform(relPosACrossNormal, angularComponentA)

        relPosB.set(contactConstraint.relPosB)
        contactNormal.cross(relPosB, relPosBCrossNormal)
        angularComponentB.set(Vec3f.ZERO)
        rbB.invInertiaTensor.transform(relPosBCrossNormal, angularComponentB)

        val denomA = rbA.invMass + normalAxis * angularComponentA.cross(relPosA, tmpVec1)
        val denomB = rbB.invMass + normalAxis * relPosB.cross(angularComponentB, tmpVec1)

        relaxation = contactConstraint.relaxation
        val scaledDenom = relaxation / (denomA + denomB)
        jacDiagABInv = scaledDenom

        val velADotN = contactNormal * bodyA.linearVelocity + relPosACrossNormal * bodyA.angularVelocity
        val velBDotN = -(contactNormal * bodyB.linearVelocity) + relPosBCrossNormal * bodyB.angularVelocity

        val relVel = velADotN + velBDotN

        val velocityError = desiredVelocity - relVel
        val velocityImpulse = velocityError * scaledDenom
        rhs = velocityImpulse
        cfm = cfmSlip
        lowerLimit = 0f
        upperLimit = 1e10f
    }

    fun setupRollingFrictionConstraint(normalAxis: Vec3f, contactConstraint: SolverConstraint, desiredVelocity: Float, cfmSlip: Float) {
        contactNormal.set(normalAxis)
        solverBodyA = contactConstraint.solverBodyA
        solverBodyB = contactConstraint.solverBodyB
        originalContactPoint = null

        val bodyA = solverBodyA
        val bodyB = solverBodyB
        val rbA = bodyA.originalBody
        val rbB = bodyB.originalBody

        friction = contactConstraint.originalContactPoint?.combinedRollingFriction ?: 0f
        originalContactPoint = null

        appliedImpulse = 0f
        appliedPushImpulse = 0f

        relPosA.set(contactConstraint.relPosA)
        normalAxis.scale(-1f, relPosACrossNormal)
        angularComponentA.set(Vec3f.ZERO)
        rbA.invInertiaTensor.transform(relPosACrossNormal, angularComponentA)

        relPosB.set(contactConstraint.relPosB)
        relPosBCrossNormal.set(normalAxis)
        angularComponentB.set(Vec3f.ZERO)
        rbB.invInertiaTensor.transform(relPosBCrossNormal, angularComponentB)

        // iMJaA
        rbA.invInertiaTensor.transform(relPosACrossNormal, tmpVec1)
        // iMJaB
        rbB.invInertiaTensor.transform(relPosBCrossNormal, tmpVec2)
        jacDiagABInv = 1f / (tmpVec1 * relPosACrossNormal + tmpVec2 * relPosBCrossNormal)
        relaxation = contactConstraint.relaxation


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

    private fun restitutionCurve(relVel: Float, restitution: Float) = restitution * -relVel
}