package de.fabmax.kool.physics.shapes

import ammo.btCollisionShape
import de.fabmax.kool.util.MeshBuilder

actual interface CollisionShape {

    val btShape: btCollisionShape

    actual fun generateGeometry(target: MeshBuilder)

}