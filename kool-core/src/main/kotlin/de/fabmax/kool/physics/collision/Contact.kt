package de.fabmax.kool.physics.collision

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.RigidBody

/**
 * Ported version of b3Contact4
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
class Contact {
    // w component of world pos holds penetration depth
    val worldPosB = mutableListOf<MutableVec4f>()
    val worldNormalOnB = MutableVec3f()
    var restitutionCoeff = 0f
    var frictionCoeff = 0f
    var batchIdx = 0

    var bodyA: RigidBody? = null
    var bodyB: RigidBody? = null
}