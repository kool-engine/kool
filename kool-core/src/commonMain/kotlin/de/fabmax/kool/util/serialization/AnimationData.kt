package de.fabmax.kool.util.serialization

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.scene.animation.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoId

@Serializable
data class AnimationData(
    @ProtoId(1) val name: String,
    @ProtoId(2) val duration: Float,
    @ProtoId(3) val channels: List<NodeAnimationData>
) {
    fun <T : AnimatedNode> getAnimation(nodes: Map<String, T>): Animation {
        val anim = Animation(duration)
        channels.forEach {
            val node = nodes[it.name]
            if (node != null) {
                anim.channels += it.getNodeAnimation(node)
            }
        }
        return anim
    }
}

@Serializable
data class NodeAnimationData(
    @ProtoId(1) val name: String,
    @ProtoId(2) val positionKeys: List<Vec3KeyData>,
    @ProtoId(3) val rotationKeys: List<Vec4KeyData>,
    @ProtoId(4) val scalingKeys: List<Vec3KeyData>
) {
    fun getNodeAnimation(node: AnimatedNode): NodeAnimation {
        val nodeAnim = NodeAnimation(name, node)

        positionKeys.forEach { nodeAnim.positionKeys += it.getPositionKey() }
        rotationKeys.forEach { nodeAnim.rotationKeys += it.getRotationKey() }
        scalingKeys.forEach { nodeAnim.scalingKeys += it.getScalingKey() }

        return nodeAnim
    }
}

@Serializable
data class Vec3KeyData(
    @ProtoId(1) val time: Float,
    @ProtoId(2) val x: Float,
    @ProtoId(3) val y: Float,
    @ProtoId(4) val z: Float
) {
    fun getPositionKey(): PositionKey = PositionKey(time, Vec3f(x, y, z))
    fun getScalingKey(): ScalingKey = ScalingKey(time, Vec3f(x, y, z))
}

@Serializable
data class Vec4KeyData(
    @ProtoId(1) val time: Float,
    @ProtoId(2) val x: Float,
    @ProtoId(3) val y: Float,
    @ProtoId(4) val z: Float,
    @ProtoId(5) val w: Float
) {
    fun getRotationKey(): RotationKey = RotationKey(time, Vec4f(x, y, z, w))
}
