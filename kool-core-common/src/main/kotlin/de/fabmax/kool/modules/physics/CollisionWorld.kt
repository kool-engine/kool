package de.fabmax.kool.modules.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.modules.physics.collision.BoxBoxCollision
import de.fabmax.kool.modules.physics.collision.Contacts
import de.fabmax.kool.modules.physics.constraintSolver.SequentialImpulseConstraintSolver
import kotlin.math.min

class CollisionWorld {

    val bodies = mutableListOf<RigidBody>()

    val gravity = MutableVec3f(0f, -9.81f, 0f)

    private val collisionChecker = BoxBoxCollision()
    private val solver = SequentialImpulseConstraintSolver()
    private val contacts = Contacts()

    private var realTime = 0.0
    private var simTime = 0.0

    private var timeStep = 1 / 60f

    fun stepSimulation(dt: Float) {
        // limit max step time to 0.1 secs, i.e. if fram rate goes below 10 fps, real time is not achieved anymore
        realTime += min(dt, 0.1f)

        while ((realTime - simTime) > timeStep / 2) {
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
        // it's super effective!
        for (i in bodies.indices) {
            for (j in i+1 until bodies.size) {
                if (!bodies[i].isStaticOrKinematic || !bodies[j].isStaticOrKinematic) {
                    collisionChecker.testForCollision(bodies[i], bodies[j], contacts)
                }
            }
        }

        //if (!contacts.contacts.isEmpty()) {
            //contacts.dumpContacts()

            // we got contacts, solve them!
            solver.solveContacts(bodies, contacts.contacts)
            // recycle all contact points
            contacts.clearContacts()
        //}
    }
}