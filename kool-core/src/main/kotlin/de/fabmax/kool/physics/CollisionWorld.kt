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

    private var realTime = 0.0
    private var simTime = 0.0

    private var timeStep = 1 / 90f

    fun stepSimulation(dt: Float) {
        realTime += dt

        while (simTime < realTime) {
            simTime += timeStep

            for (i in bodies.indices) {
                bodies[i].applyGravity(timeStep, this)
                bodies[i].predictIntegratedTransform(timeStep)
            }

            broadPhase()

            for (i in bodies.indices) {
                bodies[i].stepSimulation(timeStep, this)
            }
        }
    }

    fun broadPhase() {
        for (i in bodies.indices) {
            bodies[i].isInCollision = false
        }

        // it's super effective!
        for (i in bodies.indices) {
            for (j in i+1 until bodies.size) {
                val coll = collisionChecker.testForCollision(bodies[i], bodies[j], contacts) > 0
                bodies[i].isInCollision = bodies[i].isInCollision || coll
                bodies[j].isInCollision = bodies[j].isInCollision || coll
            }
        }

        if (!contacts.contacts.isEmpty()) {
            //contacts.dumpContacts()

            // we got contacts, solve them!
            solver.solveContacts(bodies, contacts.contacts)
            // recycle all contact points
            contacts.clearContacts()
        }
    }
}