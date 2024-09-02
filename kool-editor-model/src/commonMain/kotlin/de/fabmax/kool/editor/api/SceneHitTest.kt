package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.components.localToGlobalD
import de.fabmax.kool.editor.components.localToGlobalF
import de.fabmax.kool.editor.components.viewToLocalD
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Mesh
import kotlin.math.sqrt

class SceneHitTest(val scene: EditorScene) {
    private val meshComponents by CachedSceneComponents(scene, MeshComponent::class)

    private val tmpHitVec = MutableVec3d()
    private val localRayTest = RayTest()

    fun computePickRay(pointer: Pointer, result: RayF): Boolean = scene.scene.computePickRay(pointer, result)

    fun computePickRay(pointer: Pointer, result: RayD): Boolean = scene.scene.computePickRay(pointer, result)

    fun hitTest(rayTest: RayTest): GameEntity? {
        var hitEntity: GameEntity? = null
        val meshes = meshComponents
        for (i in meshes.indices) {
            val mesh = meshes[i]
            if (mesh.gameEntity.isVisible && mesh.hitTest(rayTest)) {
                hitEntity = mesh.gameEntity
            }
        }
        return hitEntity
    }

    private fun MeshComponent.hitTest(rayTest: RayTest): Boolean {
        val node = sceneNode ?: return false
        val localRay = rayTest.getRayTransformed(gameEntity.viewToLocalD)
        val hitDistSqr = node.bounds.hitDistanceSqr(localRay)
        if (hitDistSqr < Float.POSITIVE_INFINITY) {
            val dGlobal = gameEntity.localToGlobalD
                .transform(tmpHitVec.set(localRay.direction).mul(sqrt(hitDistSqr.toDouble())), 0.0)
                .length()

            if (dGlobal < rayTest.hitDistance) {
                localRayTest.clear()
                localRay.toRayD(localRayTest.ray)
                when (node) {
                    is Mesh -> node.rayTestLocal(localRayTest, localRay)
                    else -> node.rayTest(localRayTest)
                }
                if (localRayTest.isHit) {
                    val dist = gameEntity.localToGlobalF
                        .transform(tmpHitVec.set(localRay.direction).mul(localRayTest.hitDistance), 0.0)
                        .length()
                    rayTest.setHit(node, dist)
                    return true
                }
            }
        }
        return false
    }
}