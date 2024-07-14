package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.components.globalToLocalF
import de.fabmax.kool.editor.components.localToGlobalF
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.RayF
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.scene.Mesh
import kotlin.math.sqrt

class SceneHitTest(val scene: EditorScene) {
    private val meshComponents by CachedSceneComponents(scene, MeshComponent::class)

    private val tmpHitVec = MutableVec3f()
    private val localRayTest = RayTest()

    fun computePickRay(pointer: Pointer, result: RayF): Boolean = scene.scene.computePickRay(pointer, result)

    fun hitTest(rayTest: RayTest): GameEntity? {
        var hitEntity: GameEntity? = null
        val meshes = meshComponents
        for (i in meshes.indices) {
            val mesh = meshes[i]
            if (mesh.hitTest(rayTest)) {
                hitEntity = mesh.gameEntity
            }
        }
        return hitEntity
    }

    private fun MeshComponent.hitTest(rayTest: RayTest): Boolean {
        val node = sceneNode ?: return false
        val localRay = rayTest.getRayTransformed(gameEntity.globalToLocalF)
        val hitDistSqr = node.bounds.hitDistanceSqr(localRay)
        if (hitDistSqr < Float.MAX_VALUE) {
            val dGlobal = gameEntity.localToGlobalF
                .transform(tmpHitVec.set(localRay.direction).mul(sqrt(hitDistSqr)), 0f)
                .sqrLength()

            if (dGlobal < rayTest.hitDistanceSqr) {
                localRayTest.clear()
                localRayTest.ray.set(localRay)
                when (node) {
                    is Mesh -> node.rayTestLocal(localRayTest, localRay)
                    else -> node.rayTest(localRayTest)
                }
                if (localRayTest.isHit) {
                    val dist = gameEntity.localToGlobalF
                        .transform(tmpHitVec.set(localRay.direction).mul(sqrt(localRayTest.hitDistanceSqr)), 0f)
                        .length()
                    rayTest.setHit(node, dist)
                    return true
                }
            }
        }
        return false
    }
}