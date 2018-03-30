package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.physics.collision.BoxBoxCollision
import de.fabmax.kool.physics.collision.Contact
import de.fabmax.kool.physics.constraintSolver.PgsJacobiSolver

class CollisionWorld {

    val bodies = mutableListOf<RigidBody>()

    val gravity = MutableVec3f(0f, -9.81f, 0f)

    private val collisionChecker = BoxBoxCollision()
    private val solver = PgsJacobiSolver()
    private val contactPoints = ContactPoints()

    private val tmpVec = MutableVec3f()

    fun stepSimulation(dt: Float) {
        val timeStep = 1/60f

        for (i in bodies.indices) {
            bodies[i].predictIntegratedTransform(timeStep)
        }

        broadPhase()

        for (i in bodies.indices) {
            bodies[i].stepSimulation(timeStep, this)
        }
    }

    fun broadPhase() {
        val contacts = mutableListOf<Contact>()

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
                        //collisionResponse(bodies[i], contactPoints)

                        val c = Contact()
                        c.bodyA = bodies[i]
                        c.bodyB = bodies[j]
                        c.batchIdx = 0
                        c.frictionCoeff = 0.7f
                        c.restitutionCoeff = 0f
                        c.worldNormalOnB.set(contactPoints.points[0].normalOnBInWorld).norm()

//                        var maxDepth = 0f
                        for (pt in contactPoints.points) {
                            c.worldPosB += MutableVec4f(pt.pointInWorld, pt.depth)
//                            if (pt.depth < maxDepth) {
//                                maxDepth = pt.depth
//                            }
                        }

//                        println(maxDepth)
//                        bodies[i].worldTransform[0, 3] -= maxDepth * c.worldNormalOnB.x
//                        bodies[i].worldTransform[1, 3] -= maxDepth * c.worldNormalOnB.y
//                        bodies[i].worldTransform[2, 3] -= maxDepth * c.worldNormalOnB.z

                        contacts += c
                    }
                }
            }
        }

        if (!contacts.isEmpty()) {
            solver.solveContacts(bodies, contacts)
        }
    }

    private fun collisionResponse(body: RigidBody, contactPoints: ContactPoints) {
        tmpVec.set(Vec3f.ZERO)
        var normalMag = 0f
        for (i in contactPoints.points.indices) {
            // todo: compute body velocity at collision point and only consider points with negative dot
            tmpVec.add(contactPoints.points[i].normalOnBInWorld)
            normalMag += contactPoints.points[i].normalOnBInWorld.length()
        }

        if (body.velocity * tmpVec < 0) {
            val j = body.mass * 1.25f / normalMag * body.velocity.length().clamp(0.05f, 100f)
            for (i in contactPoints.points.indices) {
                tmpVec.set(contactPoints.points[i].normalOnBInWorld).scale(j)
                body.applyImpulseGlobal(contactPoints.points[i].pointInWorld, tmpVec)

//                if (contactPoints.points[i].depth < -0.1) {
//                    tmpVec.set(contactPoints.points[i].normalOnBInWorld).scale(body.mass * 10f / normalMag)
//                    body.applyForceGlobal(contactPoints.points[i].pointInWorld, tmpVec)
//                }
            }
        }
    }
}