package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f

class CollisionWorld {

    val bodies = mutableListOf<RigidBody>()

    val gravity = MutableVec3f(0f, -9.81f, 0f)

    private val collisionChecker = BoxBoxCollision()
    private val contactPoints = ContactPoints()

    private val tmpVec = MutableVec3f()

    fun stepSimulation(dt: Float) {
        broadPhase()

        for (i in bodies.indices) {
            bodies[i].stepSimulation(dt, this)
        }
    }

    fun broadPhase() {
        // it's super effective!
        for (i in bodies.indices) {
            if (bodies[i].mass == 0f) {
                // body is static, no need for collision checking
                continue
            }

            for (j in bodies.indices) {
                if (i != j) {
                    contactPoints.clear()
                    bodies[i].isInCollision = collisionChecker.testForCollision(bodies[i].shape, bodies[j].shape, contactPoints) > 0
                    if (bodies[i].isInCollision) {
                        // bodies collide, do something
                        bodies[i].isInCollision = true
                    }
                }
            }
        }
    }
}