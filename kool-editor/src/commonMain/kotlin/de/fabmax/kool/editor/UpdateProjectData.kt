@file:Suppress("DEPRECATION")

package de.fabmax.kool.editor

import de.fabmax.kool.editor.data.*
import de.fabmax.kool.util.logW

fun ProjectData.updateData() {
    replaceModelComponentsByMeshComponents()
    meta = meta.copy(modelVersion = ProjectData.MODEL_VERSION)
}

private fun ProjectData.replaceModelComponentsByMeshComponents() {
    scenes.flatMap { it.entities }.forEach { entityData ->
        entityData.components.filter { it.data is ModelComponentData }.forEach { modelInfo ->
            val idx = entityData.components.indexOf(modelInfo)
            val modelData = modelInfo.data as ModelComponentData
            val meshData = ComponentInfo(
                displayOrder = modelInfo.displayOrder,
                data = MeshComponentData(
                    ShapeData.Model(
                        modelPath = modelData.modelPath,
                        sceneIndex = modelData.sceneIndex,
                        animationIndex = modelData.animationIndex
                    )
                )
            )
            logW { "Replacing deprecated ModelComponentData by corresponding MeshComponentData" }
            entityData.components[idx] = meshData
        }
    }
}