package de.fabmax.kool.physics.shapes

import de.fabmax.kool.util.MeshBuilder

expect interface CollisionShape {

    fun generateGeometry(target: MeshBuilder)

}
