package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.joints.RevoluteJoint

class ChainBridge(val world: PhysicsWorld) {

    val segments = mutableListOf<RigidActor>()
    private val joints = mutableListOf<RevoluteJoint>()

    init {
        val pose = MutableMat4f().translate(-107.2f, 11.1f, -33.7f).rotate((-34f).deg, Vec3f.Y_AXIS).rotate((-0.95f).deg, Vec3f.X_AXIS)
        var prevSeg: RigidActor? = null
        val n = 37
        for (i in 0..n) {
            val seg = makeSegment(pose, prevSeg, i in 1 until n)
            segments += seg
            seg.tags["isBridge"] = true
            prevSeg = seg
            pose.translate(0f, 0f, 1.21f)
        }
    }

    fun isBridge(actor: RigidActor): Boolean = actor.tags.hasTag("isBridge")

    private fun makeSegment(pose: Mat4f, prevSegment: RigidActor?, isDynamic: Boolean): RigidActor {
        val body = if (isDynamic) {
            RigidDynamic(20f, pose).apply { linearDamping = 1f }
        } else {
            RigidStatic(pose)
        }
        val shape = Shape(BoxGeometry(Vec3f(2f, 0.2f, 1.1f)))
        body.attachShape(shape)
        world.addActor(body)
        prevSegment?.let {
            joints += RevoluteJoint(it, body, Vec3f(0f, 0f, 0.59f), Vec3f(0f, 0f, -0.59f), Vec3f.X_AXIS, Vec3f.X_AXIS)
        }
        return body
    }
}