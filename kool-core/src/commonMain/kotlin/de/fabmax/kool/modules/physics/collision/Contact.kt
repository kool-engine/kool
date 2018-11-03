package de.fabmax.kool.modules.physics.collision

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.physics.RigidBody
import de.fabmax.kool.util.ObjectRecycler

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
class Contact internal constructor(){
    // w component of world pos holds penetration depth
    val worldPosB = mutableListOf<MutableVec4f>()
    val worldNormalOnB = MutableVec3f()
    var restitutionCoeff = 0f
    var frictionCoeff = 0f
    var batchIdx = 0

    lateinit var bodyA: RigidBody
    lateinit var bodyB: RigidBody

    fun initContact(bodyA: RigidBody, bodyB: RigidBody): Contact {
        this.bodyA = bodyA
        this.bodyB = bodyB
        worldNormalOnB.set(Vec3f.ZERO)
        restitutionCoeff = 0f
        frictionCoeff = 1f
        batchIdx = 0

        if (!worldPosB.isEmpty()) {
            // this shouldn't happen
            worldPosB.clear()
        }

        return this
    }
}

/**
 * Container class for [Contact]s. Recycles all objects for less GC load.
 */
class Contacts {
    private val contactRecycler = ObjectRecycler { Contact() }
    private val vec4Recycler = ObjectRecycler { MutableVec4f() }
    private val liveContacts = mutableListOf<Contact>()

    val contacts: List<Contact>
        get() = liveContacts

    fun addNewContact(bodyA: RigidBody, bodyB: RigidBody): Contact {
        val c = contactRecycler.get().initContact(bodyA, bodyB)
        liveContacts += c
        return c
    }

    inline fun addContact(bodyA: RigidBody, bodyB: RigidBody, block: Contact.() -> Unit): Contact {
        val c = addNewContact(bodyA, bodyB)
        c.block()
        return c
    }

    fun newWorldPosVec(pos: Vec3f, depth: Float): MutableVec4f {
        val vec4 = vec4Recycler.get()
        vec4.set(pos, depth)
        return vec4
    }

    fun clearContacts() {
        for (i in liveContacts.indices) {
            val c = liveContacts[i]
            for (j in c.worldPosB.indices) {
                vec4Recycler.recycle(c.worldPosB[j])
            }
            c.worldPosB.clear()
            contactRecycler.recycle(c)
        }
        liveContacts.clear()
    }

    fun dumpContacts() {
        println("${contacts.size} contacts:")
        for (c in contacts) {
            println("a: ${c.bodyA}, b: ${c.bodyB}, normal = ${c.worldNormalOnB}")
            for (pt in c.worldPosB) {
                println("  $pt")
            }
        }
    }
}
