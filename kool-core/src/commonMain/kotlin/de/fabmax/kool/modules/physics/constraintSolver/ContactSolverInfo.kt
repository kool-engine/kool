package de.fabmax.kool.modules.physics.constraintSolver

/**
 * Ported from b3ContactSolverInfo.h
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
class ContactSolverInfo {

    var tau = 0.6f
    var damping = 1.0f
    var friction = 0.3f
    var timeStep = 1f / 60f
    var restitution = 0f
    var maxErrorReduction = 20f
    var numIterations = 10
    var erp = 0.2f
    var erp2 = 0.8f
    var globalCfm = 0f
    var sor = 1f
    var splitImpulse = true
    var splitImpulsePenetrationThreshold = -0.04f
    var splitImpulseTurnErp = 0.1f
    var linearSlop = 0f
    var warmstartingFactor = 0.85f
    var solverMode = SOLVER_USE_WARMSTARTING
    var minimumSolverBatchSize = 128  //try to combine islands until the amount of constraints reaches this limit
    var maxGyroscopicForce = 100f
    var singleAxisRollingFrictionThreshold = 1e30f

    companion object {
        const val SOLVER_RANDOMIZE_ORDER = 1
        const val SOLVER_FRICTION_SEPARATE = 2
        const val SOLVER_USE_WARMSTARTING = 4
        const val SOLVER_USE_2_FRICTION_DIRECTIONS = 16
        const val SOLVER_ENABLE_FRICTION_DIRECTION_CACHING = 32
        const val SOLVER_DISABLE_VELOCITY_DEPENDENT_FRICTION_DIRECTION = 64
        const val SOLVER_INTERLEAVE_CONTACT_AND_FRICTION_CONSTRAINTS = 512
        const val SOLVER_ALLOW_ZERO_LENGTH_FRICTION_DIRECTIONS = 1024
    }

    fun isSolverMode(mode: Int): Boolean = solverMode and mode != 0
}
