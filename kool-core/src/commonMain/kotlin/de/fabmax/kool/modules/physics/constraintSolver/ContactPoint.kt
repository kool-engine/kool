package de.fabmax.kool.modules.physics.constraintSolver

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.modules.physics.collision.Contact

class ContactPoint {
    val positionWorldOnA = MutableVec3f()
    val positionWorldOnB = MutableVec3f()
    val normalWorldOnB = MutableVec3f()
    var appliedImpulse = 0f
    var distance = 0f
    var combinedRestitution = 0f

    // friction related stuff
    var combinedFriction = 0f
    val lateralFrictionDir1 = MutableVec3f()
    val lateralFrictionDir2 = MutableVec3f()
    var appliedImpulseLateral1 = 0f
    var appliedImpulseLateral2 = 0f
    var combinedRollingFriction = 0f
    var contactMotion1 = 0f
    var contactMotion2 = 0f
    var contactCFM1 = 0f
    var contactCFM2 = 0f

    var lateralFrictionInitialized = false

    fun initContactPoint(contact: Contact, contactIndex: Int): ContactPoint {
        appliedImpulse = 0f
        appliedImpulseLateral1 = 0f
        appliedImpulseLateral2 = 0f
        combinedFriction = contact.frictionCoeff
        combinedRestitution = contact.restitutionCoeff
        combinedRollingFriction = 0f
        contactCFM1 = 0f
        contactCFM2 = 0f
        contactMotion1 = 0f
        contactMotion2 = 0f
        distance = contact.worldPosB[contactIndex].w
        normalWorldOnB.set(contact.worldNormalOnB).norm()
        normalWorldOnB.planeSpace(lateralFrictionDir1, lateralFrictionDir2)
        lateralFrictionInitialized = true
        contact.worldPosB[contactIndex].getXyz(positionWorldOnB)
        positionWorldOnA.set(normalWorldOnB).scale(distance).add(positionWorldOnB)
        return this
    }
}