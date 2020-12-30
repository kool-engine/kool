package de.fabmax.kool.physics

import de.fabmax.kool.math.Vec3f

expect class BoxShape(size: Vec3f) : CollisionShape {
    val size: Vec3f
}