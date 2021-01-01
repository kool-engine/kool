package de.fabmax.kool.physics.shapes

import ammo.btCollisionShape

actual abstract class CollisionShape {

    abstract val shape: btCollisionShape

}