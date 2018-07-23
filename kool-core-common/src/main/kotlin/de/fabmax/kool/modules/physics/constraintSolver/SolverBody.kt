package de.fabmax.kool.modules.physics.constraintSolver

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.physics.RigidBody
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Wraps info from a [RigidBody] plus some additional data needed for constraint solving.
 *
 * Ported version of b3SolverBody
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
class SolverBody {
    val worldTransform = Mat4f()
    val deltaLinearVelocity = MutableVec3f()
    val deltaAngularVelocity = MutableVec3f()
    val angularFactor = MutableVec3f()
    val linearFactor = MutableVec3f()
    val invMass = MutableVec3f()
    val pushVelocity = MutableVec3f()
    val turnVelocity = MutableVec3f()
    val linearVelocity = MutableVec3f()
    val angularVelocity = MutableVec3f()

    lateinit var originalBody: RigidBody

    private val tmpVec1 = MutableVec3f()
    private val tmpVec2 = MutableVec3f()
    private val tmpVec3 = MutableVec3f()
    private val tmpQuat1 = MutableVec4f()
    private val tmpQuat2 = MutableVec4f()

    fun initSolverBody(originalBody: RigidBody): SolverBody {
        this.originalBody = originalBody

        deltaLinearVelocity.set(Vec3f.ZERO)
        deltaAngularVelocity.set(Vec3f.ZERO)
        pushVelocity.set(Vec3f.ZERO)
        turnVelocity.set(Vec3f.ZERO)

        worldTransform.set(originalBody.worldTransform)
        invMass.set(originalBody.invMass, originalBody.invMass, originalBody.invMass)
        angularFactor.set(1f, 1f, 1f)
        linearFactor.set(1f, 1f, 1f)
        linearVelocity.set(originalBody.velocity)
        angularVelocity.set(originalBody.angularVelocity)
        return this
    }

    internal fun internalApplyImpulse(linearComponent: Vec3f, angularComponent: Vec3f, impulseMagnitude: Float) {
        deltaLinearVelocity.add(tmpVec1.set(linearComponent).scale(impulseMagnitude).mul(linearFactor))
        deltaAngularVelocity.add(tmpVec1.set(angularComponent).scale(impulseMagnitude).mul(angularFactor))
    }

    internal fun internalApplyPushImpulse(linearComponent: Vec3f, angularComponent: Vec3f, impulseMagnitude: Float) {
        pushVelocity.add(tmpVec1.set(linearComponent).scale(impulseMagnitude).mul(linearFactor))
        turnVelocity.add(tmpVec1.set(angularComponent).scale(impulseMagnitude).mul(angularFactor))
    }

    fun writebackVelocity() {
        linearVelocity.add(deltaLinearVelocity)
        angularVelocity.add(deltaAngularVelocity)
    }

    fun writebackVelocityAndTransform(timeStep: Float, splitImpulseTurnErp: Float) {
        linearVelocity.add(deltaLinearVelocity)
        angularVelocity.add(deltaAngularVelocity)

        if (pushVelocity != Vec3f.ZERO || turnVelocity != Vec3f.ZERO) {
            tmpVec3.set(turnVelocity).scale(splitImpulseTurnErp)
            integrateTransform(pushVelocity, tmpVec3,timeStep)
        }
    }

    private fun integrateTransform(linVel: Vec3f, angVel: Vec3f, timeStep: Float) {
        worldTransform.getOrigin(tmpVec1).add(tmpVec2.set(linVel).scale(timeStep))
        val oriX = tmpVec1.x
        val oriY = tmpVec1.y
        val oriZ = tmpVec1.z

        // Exponential map
        // google for "Practical Parameterization of Rotations Using the Exponential Map", F. Sebastian Grassia

        var fAngle = angVel.length()
        // limit the angular motion
        if (fAngle * timeStep > ANGULAR_MOTION_THRESHOLD) {
            fAngle = ANGULAR_MOTION_THRESHOLD / timeStep
        }

        // determine rotation axis, tmoVec1
        if (fAngle < 0.001f) {
            // use Taylor's expansions of sync function
            tmpVec1.set(angVel).scale(0.5f * timeStep - timeStep * timeStep * timeStep * 0.020833333333f * fAngle * fAngle)
        } else {
            tmpVec1.set(angVel).scale(sin(0.5f * fAngle * timeStep) / fAngle)
        }
        tmpQuat1.set(tmpVec1, cos(fAngle * timeStep * 0.5f))
        worldTransform.getRotation(tmpQuat2)
        tmpQuat1.quatProduct(tmpQuat2).norm()

        worldTransform.setRotate(tmpQuat1)
        worldTransform[0, 3] = oriX
        worldTransform[1, 3] = oriY
        worldTransform[2, 3] = oriZ
    }

    companion object {
        private const val ANGULAR_MOTION_THRESHOLD = PI.toFloat() / 4f
    }
}