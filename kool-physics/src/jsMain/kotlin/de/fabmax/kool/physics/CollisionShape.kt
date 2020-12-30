package de.fabmax.kool.physics

import ammo.btCollisionShape

actual abstract class CollisionShape {

    abstract val shape: btCollisionShape

}