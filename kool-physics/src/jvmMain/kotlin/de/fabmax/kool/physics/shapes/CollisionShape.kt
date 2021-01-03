package de.fabmax.kool.physics.shapes

import de.fabmax.kool.physics.BtCollisionShape
import de.fabmax.kool.util.MeshBuilder

actual interface CollisionShape {

    val btShape: BtCollisionShape

    actual fun generateGeometry(target: MeshBuilder)

}