package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.physics.collision.BoxBoxCollision
import de.fabmax.kool.physics.collision.Contacts
import de.fabmax.kool.physics.constraintSolver.PgsJacobiSolver

class CollisionWorld {

    val bodies = mutableListOf<RigidBody>()

    val gravity = MutableVec3f(0f, -9.81f, 0f)

    private val collisionChecker = BoxBoxCollision()
    private val solver = PgsJacobiSolver()
    private val contacts = Contacts()

    var stop = false

    fun stepSimulation(dt: Float) {
        val timeStep = 1/60f

        if (!stop) {
            for (i in bodies.indices) {
                bodies[i].predictIntegratedTransform(timeStep)
            }
            broadPhase()
            for (i in bodies.indices) {
                bodies[i].stepSimulation(timeStep, this)
            }
        }
    }

    fun broadPhase() {
        // it's super effective!
        for (i in bodies.indices) {
            // only check non-static bodies for collisions
            if (!bodies[i].isStaticOrKinematic) {
                bodies[i].isInCollision = false
                for (j in bodies.indices) {
                    if (i != j) {
                        val coll = collisionChecker.testForCollision(bodies[i], bodies[j], contacts) > 0
                        bodies[i].isInCollision = bodies[i].isInCollision || coll
                    }
                }
            }
        }

        if (!contacts.contacts.isEmpty()) {
            if (stop) {
                contacts.dumpContacts()
            }

            // we got contacts, solve them!
            solver.solveContacts(bodies, contacts.contacts)
            // recycle all contact points
            contacts.clearContacts()
        }
    }
}