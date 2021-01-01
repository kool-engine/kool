package de.fabmax.kool.physics.shapes

import de.fabmax.kool.physics.btCollisionShape

actual abstract class CollisionShape {

    abstract val shape: btCollisionShape

}