package de.fabmax.kool.modules.physics.constraintSolver

import de.fabmax.kool.math.FLT_EPSILON
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.isFuzzyZero
import de.fabmax.kool.modules.physics.RigidBody
import de.fabmax.kool.modules.physics.collision.Contact
import de.fabmax.kool.util.ObjectPool
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Solves constraints of a group of rigid bodies.
 *
 * Ported from bullet's b3PgsJacobiSolver, for now only pgs method is supported (what does pgs stand for?)
 *
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
class SequentialImpulseConstraintSolver {

    private var maxOverrideNumSolverIterations = 0

    private val infoGlobal = ContactSolverInfo()

    private val contactPointPool = ObjectPool { ContactPoint() }
    private val solverBodyPool = ObjectPool { SolverBody() }
    private val contactConstraintPool = ObjectPool { ContactConstraint() }
    private val contactFrictionConstraintPool = ObjectPool { FrictionConstraint() }
    private val contactRollingConstraintPool = ObjectPool { RollingFrictionConstraint() }

    private val tmpVec1 = MutableVec3f()
    private val tmpVec2 = MutableVec3f()
    private val tmpVec3 = MutableVec3f()

    private var n = 0

    init {
        infoGlobal.splitImpulse = false
        infoGlobal.timeStep = 1f / 60f
        infoGlobal.numIterations = 4
        infoGlobal.solverMode = infoGlobal.solverMode or ContactSolverInfo.SOLVER_USE_2_FRICTION_DIRECTIONS
    }

    fun solveContacts(bodies: List<RigidBody>, contacts: List<Contact>) {
//        println("${n++}: num manifolds: ${contacts.size}:")

        solveGroupSetup(bodies, contacts)
        solveGroupIterations()
        solveGroupFinish()
    }

    private fun solveGroupSetup(bodies: List<RigidBody>, contacts: List<Contact>) {
        maxOverrideNumSolverIterations = 0

        for (i in contacts.indices) {
            convertContact(contacts[i])
        }
    }

    private fun convertContact(contact: Contact) {
        if (contact.bodyA.mass.isFuzzyZero() && contact.bodyB.mass.isFuzzyZero()) {
            // don't do collision between two static objects
            return
        }

        val solverBodyA = solverBodyPool.get().initSolverBody(contact.bodyA)
        val solverBodyB = solverBodyPool.get().initSolverBody(contact.bodyB)

        var rollingFrictionCnt = 1
        for (i in contact.worldPosB.indices) {
            val cp = contactPointPool.get().initContactPoint(contact, i)
            if (cp.distance < getContactProcessingThreshold(contact)) {
                val contactConstraint = contactConstraintPool.get()
                contactConstraint.setupContactConstraint(cp, solverBodyA, solverBodyB, infoGlobal)

                // setup the friction constraints
                if (cp.combinedRollingFriction > 0f && rollingFrictionCnt > 0) {
                    // only a single rolling friction per contact
                    rollingFrictionCnt--

                    solverBodyB.angularVelocity.subtract(solverBodyA.angularVelocity, tmpVec1)
                    if (tmpVec1.length() > infoGlobal.singleAxisRollingFrictionThreshold) {
                        tmpVec1.norm()
                        addRollingFrictionConstraint(tmpVec1, contactConstraint)
                    } else {
                        addRollingFrictionConstraint(tmpVec1, contactConstraint)
                        cp.normalWorldOnB.planeSpace(tmpVec1, tmpVec2)
                        if (tmpVec1.length() > 0.001f) {
                            addRollingFrictionConstraint(tmpVec1, contactConstraint)
                        }
                        if (tmpVec2.length() > 0.001f) {
                            addRollingFrictionConstraint(tmpVec2, contactConstraint)
                        }
                    }
                }

                // Bullet has several options to set the friction directions.  By default, each contact has only a
                // single friction direction that is recomputed automatically every frame based on the relative linear
                // velocity. If the relative velocity it zero, it will automatically compute a friction direction.
                //
                // You can also enable two friction directions, using the SOLVER_USE_2_FRICTION_DIRECTIONS.
                // In that case, the second friction direction will be orthogonal to both contact normal and first
                // friction direction.
                //
                // If you choose SOLVER_DISABLE_VELOCITY_DEPENDENT_FRICTION_DIRECTION, then the friction will be
                // independent from the relative projected velocity.
                //
                // The user can manually override the friction directions for certain contacts using a contact callback,
                // and set the cp.lateralFrictionInitialized to true In that case, you can set the target relative
                // motion in each friction direction (cp.contactMotion1 and cp.contactMotion2) this will give a
                // conveyor belt effect.

                if (!infoGlobal.isSolverMode(ContactSolverInfo.SOLVER_ENABLE_FRICTION_DIRECTION_CACHING) ||
                        !cp.lateralFrictionInitialized) {

                    cp.lateralFrictionDir1.set(cp.normalWorldOnB)
                            .scale(-contactConstraint.relVelocity).add(contactConstraint.velocity)

                    val latRelVel = cp.lateralFrictionDir1.sqrLength()
                    if (!infoGlobal.isSolverMode(ContactSolverInfo.SOLVER_DISABLE_VELOCITY_DEPENDENT_FRICTION_DIRECTION) &&
                            latRelVel > FLT_EPSILON) {

                        cp.lateralFrictionDir1.scale(1f / sqrt(latRelVel))
                        if (infoGlobal.isSolverMode(ContactSolverInfo.SOLVER_USE_2_FRICTION_DIRECTIONS)) {
                            cp.lateralFrictionDir1.cross(cp.normalWorldOnB, cp.lateralFrictionDir2)
                            cp.lateralFrictionDir2.norm()
                            contactConstraint.frictionConstraint2 = addFrictionConstraint(cp.lateralFrictionDir2, contactConstraint)
                        }
                        contactConstraint.frictionConstraint1 = addFrictionConstraint(cp.lateralFrictionDir1, contactConstraint)

                    } else {
                        cp.normalWorldOnB.planeSpace(cp.lateralFrictionDir1, cp.lateralFrictionDir2)
                        if (infoGlobal.isSolverMode(ContactSolverInfo.SOLVER_USE_2_FRICTION_DIRECTIONS)) {
                            contactConstraint.frictionConstraint2 = addFrictionConstraint(cp.lateralFrictionDir2, contactConstraint)
                        }
                        contactConstraint.frictionConstraint1 = addFrictionConstraint(cp.lateralFrictionDir1, contactConstraint)

                        if (infoGlobal.isSolverMode(ContactSolverInfo.SOLVER_USE_2_FRICTION_DIRECTIONS) &&
                                infoGlobal.isSolverMode(ContactSolverInfo.SOLVER_DISABLE_VELOCITY_DEPENDENT_FRICTION_DIRECTION)) {
                            cp.lateralFrictionInitialized = true
                        }
                    }

                } else {
                    contactConstraint.frictionConstraint1 = addFrictionConstraint(cp.lateralFrictionDir1, contactConstraint)
                    if (infoGlobal.isSolverMode(ContactSolverInfo.SOLVER_USE_2_FRICTION_DIRECTIONS)) {
                        contactConstraint.frictionConstraint2 = addFrictionConstraint(cp.lateralFrictionDir2, contactConstraint)
                    }
                    setFrictionConstraintImpulse(contactConstraint)
                }
            }
        }
    }

    private fun addFrictionConstraint(normalAxis: Vec3f, contactConstraint: ContactConstraint, desiredVelocity: Float = 0f, cfmSlip: Float = 0f): SolverConstraint {
        val frictionConstraint = contactFrictionConstraintPool.get()
        frictionConstraint.contactConstraint = contactConstraint
        frictionConstraint.setupFrictionConstraint(normalAxis, contactConstraint, desiredVelocity, cfmSlip)
        return frictionConstraint
    }

    private fun addRollingFrictionConstraint(normalAxis: Vec3f, contactConstraint: ContactConstraint, desiredVelocity: Float = 0f, cfmSlip: Float = 0f): SolverConstraint {
        val frictionConstraint = contactRollingConstraintPool.get()
        frictionConstraint.contactConstraint = contactConstraint
        frictionConstraint.setupRollingFrictionConstraint(normalAxis, contactConstraint, desiredVelocity, cfmSlip)
        return frictionConstraint
    }

    private fun setFrictionConstraintImpulse(contactConstraint: ContactConstraint) {
        val bodyA = contactConstraint.solverBodyA
        val bodyB = contactConstraint.solverBodyB

        val cp = contactConstraint.originalContactPoint
        val frictionConstraint1 = contactConstraint.frictionConstraint1!!
        if (infoGlobal.isSolverMode(ContactSolverInfo.SOLVER_USE_WARMSTARTING)) {
            frictionConstraint1.appliedImpulse = cp.appliedImpulseLateral1 * infoGlobal.warmstartingFactor
            if (bodyA.originalBody.invMass != 0f) {
                bodyA.internalApplyImpulse(frictionConstraint1.contactNormal.mul(bodyA.invMass, tmpVec1),
                        frictionConstraint1.angularComponentA, frictionConstraint1.appliedImpulse)
            }
            if (bodyB.originalBody.invMass != 0f) {
                bodyB.internalApplyImpulse(frictionConstraint1.contactNormal.mul(bodyB.invMass, tmpVec1),
                        frictionConstraint1.angularComponentB.scale(-1f, tmpVec3), -frictionConstraint1.appliedImpulse)
            }
        } else {
            frictionConstraint1.appliedImpulse = 0f
        }

        if (infoGlobal.isSolverMode(ContactSolverInfo.SOLVER_USE_2_FRICTION_DIRECTIONS)) {
            val frictionConstraint2 = contactConstraint.frictionConstraint2!!
            if (infoGlobal.isSolverMode(ContactSolverInfo.SOLVER_USE_WARMSTARTING)) {
                frictionConstraint2.appliedImpulse = cp.appliedImpulseLateral2 * infoGlobal.warmstartingFactor
                if (bodyA.originalBody.invMass != 0f) {
                    bodyA.internalApplyImpulse(frictionConstraint2.contactNormal.mul(bodyA.invMass, tmpVec1),
                            frictionConstraint2.angularComponentA, frictionConstraint2.appliedImpulse)
                }
                if (bodyB.originalBody.invMass != 0f) {
                    bodyB.internalApplyImpulse(frictionConstraint2.contactNormal.mul(bodyB.invMass, tmpVec1),
                            frictionConstraint2.angularComponentB.scale(-1f, tmpVec3), -frictionConstraint2.appliedImpulse)
                }
            } else {
                frictionConstraint2.appliedImpulse = 0f
            }
        }
    }

    private fun getContactProcessingThreshold(contact: Contact): Float = 0.02f

    private fun solveGroupIterations() {
        // this is a special step to resolve penetrations (just for contacts)
        solveGroupSplitImpulseIterations()

        val maxIterations = max(maxOverrideNumSolverIterations, infoGlobal.numIterations)
        for (iteration in 0 until maxIterations) {
            solveSingleIteration(iteration)
        }
    }

    private fun solveGroupSplitImpulseIterations() {
        if (infoGlobal.splitImpulse) {
            for (iteration in 0 until infoGlobal.numIterations) {
                for (i in 0 until contactConstraintPool.size) {
                    resolveSplitPenetrationImpulse(contactConstraintPool[i])
                }
            }
        }
    }

    private fun solveSingleIteration(iteration: Int) {
//        // todo:
//        if (infoGlobal.isSolverMode(ContactSolverInfo.SOLVER_RANDOMIZE_ORDER)) {
//            TODO("randomize constraint order - not yet implemented")
//        }

        // todo: solve all joint constraints

        if (iteration < infoGlobal.numIterations) {
            // solve all contact constraints
            for (j in 0 until contactConstraintPool.size) {
                resolveSingleConstraintRowLowerLimit(contactConstraintPool[j])
            }

            // solve all friction constraints
            for (j in 0 until contactFrictionConstraintPool.size) {
                val constraint = contactFrictionConstraintPool[j]
                val totalImpulse = constraint.contactConstraint.appliedImpulse
                if (totalImpulse > 0) {
                    constraint.lowerLimit = constraint.friction * -totalImpulse
                    constraint.upperLimit = constraint.friction * totalImpulse

                    resolveSingleConstraintRowGeneric(constraint)
                }
            }

            // solve all rolling friction constraints
            for (j in 0 until contactRollingConstraintPool.size) {
                val constraint = contactFrictionConstraintPool[j]
                val totalImpulse = constraint.contactConstraint.appliedImpulse
                if (totalImpulse > 0) {
                    var rollingFrictionMagnitude = constraint.friction * totalImpulse
                    if (rollingFrictionMagnitude > constraint.friction) {
                        rollingFrictionMagnitude = constraint.friction
                    }
                    constraint.lowerLimit = -rollingFrictionMagnitude
                    constraint.upperLimit = rollingFrictionMagnitude

                    resolveSingleConstraintRowGeneric(constraint)
                }
            }
        }
    }

    private fun resolveSingleConstraintRowLowerLimit(contConst: SolverConstraint) {
        val bodyA = contConst.solverBodyA
        val bodyB = contConst.solverBodyB

        var deltaImpulse = contConst.rhs - contConst.appliedImpulse * contConst.cfm
        val deltaVelADotn = contConst.contactNormal * bodyA.deltaLinearVelocity + contConst.relPosACrossNormal * bodyA.deltaAngularVelocity
        val deltaVelBDotn = -(contConst.contactNormal * bodyB.deltaLinearVelocity) + contConst.relPosBCrossNormal * bodyB.deltaAngularVelocity

        deltaImpulse -= deltaVelADotn * contConst.jacDiagABInv
        deltaImpulse -= deltaVelBDotn * contConst.jacDiagABInv
        val sum = contConst.appliedImpulse + deltaImpulse

        contConst.appliedImpulse = when {
            sum < contConst.lowerLimit -> {
                deltaImpulse = contConst.lowerLimit - contConst.appliedImpulse
                contConst.lowerLimit
            }
            else -> sum
        }

        //println("deltaImp=${contConst.appliedImpulse}, deltaVelADotn=$deltaVelADotn, deltaVelBDotn=$deltaVelBDotn, va=${bodyA.deltaLinearVelocity}, vb=${bodyB.deltaLinearVelocity}")

        bodyA.internalApplyImpulse(tmpVec1.set(contConst.contactNormal).mul(bodyA.invMass), contConst.angularComponentA, deltaImpulse)
        bodyB.internalApplyImpulse(tmpVec1.set(contConst.contactNormal).scale(-1f).mul(bodyB.invMass), contConst.angularComponentB, deltaImpulse)
    }

    private fun resolveSingleConstraintRowGeneric(contConst: SolverConstraint) {
        val bodyA = contConst.solverBodyA
        val bodyB = contConst.solverBodyB

        var deltaImpulse = contConst.rhs - contConst.appliedImpulse * contConst.cfm
        val deltaVelADotn = contConst.contactNormal * bodyA.deltaLinearVelocity + contConst.relPosACrossNormal * bodyA.deltaAngularVelocity
        val deltaVelBDotn = -(contConst.contactNormal * bodyB.deltaLinearVelocity) + contConst.relPosBCrossNormal * bodyB.deltaAngularVelocity

        deltaImpulse -= deltaVelADotn * contConst.jacDiagABInv
        deltaImpulse -= deltaVelBDotn * contConst.jacDiagABInv
        val sum = contConst.appliedImpulse + deltaImpulse

        contConst.appliedImpulse = when {
            sum < contConst.lowerLimit -> {
                deltaImpulse = contConst.lowerLimit - contConst.appliedImpulse
                contConst.lowerLimit

            }
            sum > contConst.upperLimit -> {
                deltaImpulse = contConst.upperLimit - contConst.appliedImpulse
                contConst.upperLimit

            }
            else -> sum
        }

        //println("deltaAngV=${bodyA.deltaAngularVelocity} deltaI=$deltaImpulse, deltaVelADotn=$deltaVelADotn, ${contConst.relPosACrossNormal}")

        bodyA.internalApplyImpulse(tmpVec1.set(contConst.contactNormal).mul(bodyA.invMass), contConst.angularComponentA, deltaImpulse)
        bodyB.internalApplyImpulse(tmpVec1.set(contConst.contactNormal).scale(-1f).mul(bodyB.invMass), contConst.angularComponentB, deltaImpulse)
    }

    private fun resolveSplitPenetrationImpulse(contConst: ContactConstraint) {
        if (contConst.rhsPenetration != 0f) {
            val bodyA = contConst.solverBodyA
            val bodyB = contConst.solverBodyB

            var deltaImpulse = contConst.rhsPenetration - contConst.appliedPushImpulse * contConst.cfm
            val deltaVelADotn = contConst.contactNormal * bodyA.pushVelocity + contConst.relPosACrossNormal * bodyA.turnVelocity
            val deltaVelBDotn = -(contConst.contactNormal * bodyB.pushVelocity) + contConst.relPosBCrossNormal * bodyB.turnVelocity

            deltaImpulse -= deltaVelADotn * contConst.jacDiagABInv
            deltaImpulse -= deltaVelBDotn * contConst.jacDiagABInv
            val sum = contConst.appliedPushImpulse + deltaImpulse

            contConst.appliedPushImpulse = when {
                sum < contConst.lowerLimit -> {
                    deltaImpulse = contConst.lowerLimit - contConst.appliedPushImpulse
                    contConst.lowerLimit
                }
                else -> sum
            }

            bodyA.internalApplyPushImpulse(tmpVec1.set(contConst.contactNormal).mul(bodyA.invMass), contConst.angularComponentA, deltaImpulse)
            bodyB.internalApplyPushImpulse(tmpVec1.set(contConst.contactNormal).scale(-1f).mul(bodyB.invMass), contConst.angularComponentB, deltaImpulse)
        }
    }

    private fun solveGroupFinish() {
        if (infoGlobal.isSolverMode(ContactSolverInfo.SOLVER_USE_WARMSTARTING)) {
            for (i in 0 until contactConstraintPool.size) {
                val constraint = contactConstraintPool[i]
                val cp = constraint.originalContactPoint

                cp.appliedImpulse = constraint.appliedImpulse
                cp.appliedImpulseLateral1 = constraint.frictionConstraint1!!.appliedImpulse
                if (infoGlobal.isSolverMode(ContactSolverInfo.SOLVER_USE_2_FRICTION_DIRECTIONS)) {
                    cp.appliedImpulseLateral2 = constraint.frictionConstraint2!!.appliedImpulse
                }
            }
        }

        // todo: non contact constraints (aka joints)

        // write back velocities and transforms
        for (i in 0 until solverBodyPool.size) {
            val solverBody = solverBodyPool[i]
            val body = solverBody.originalBody
            if (body.invMass != 0f) {
                if (infoGlobal.splitImpulse) {
                    solverBody.writebackVelocityAndTransform(infoGlobal.timeStep, infoGlobal.splitImpulseTurnErp)
                    body.worldTransform.set(solverBody.worldTransform)
                } else {
                    solverBody.writebackVelocity()
                }

                body.velocity.set(solverBody.linearVelocity)
                body.angularVelocity.set(solverBody.angularVelocity)
            }
        }

//        if (contactPointPool.size > 0) {
//            println("  num contacts: ${contactPointPool.size}")
//            for (i in 0 until contactPointPool.size) {
//                val c = contactPointPool[i]
//                println("    ${c.positionWorldOnB} ${c.distance} applImp: ${c.appliedImpulse}")
//            }
//        }

        contactPointPool.recycleAll()
        solverBodyPool.recycleAll()
        contactConstraintPool.recycleAll()
        contactFrictionConstraintPool.recycleAll()
        contactRollingConstraintPool.recycleAll()
    }
}