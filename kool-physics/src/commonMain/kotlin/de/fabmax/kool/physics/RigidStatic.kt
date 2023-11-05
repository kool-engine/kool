package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f

expect fun RigidStatic(pose: Mat4f = Mat4f.IDENTITY): RigidStatic

interface RigidStatic : RigidActor