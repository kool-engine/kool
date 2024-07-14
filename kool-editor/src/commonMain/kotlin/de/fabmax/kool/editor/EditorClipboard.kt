package de.fabmax.kool.editor

import de.fabmax.kool.Clipboard
import de.fabmax.kool.editor.actions.AddEntitiesAction
import de.fabmax.kool.editor.api.EditorScene
import de.fabmax.kool.editor.api.toHierarchy
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.GameEntityData
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW
import kotlinx.serialization.encodeToString

object EditorClipboard {

    private val editor: KoolEditor
        get() = KoolEditor.instance

    fun copySelection() {
        val json = serializeSelectedNodes()
        if (json.isNotBlank()) {
            Clipboard.copyToClipboard(json)
        }
    }

    fun paste() {
        Clipboard.getStringFromClipboard { json ->
            val scene = editor.activeScene.value
            if (json != null && scene != null) {
                addSerializedNodes(json)
            }
        }
    }

    fun duplicateSelection() {
        addSerializedNodes(serializeSelectedNodes())
    }

    private fun serializeSelectedNodes(): String {
        val selection = editor.selectionOverlay.getSelectedSceneEntities()
        return if (selection.isEmpty()) "" else {
            val copyData = selection.toHierarchy().flatMap { it.flatten() }
            logD { "Copy ${copyData.size} selected entities" }
            KoolEditor.jsonCodec.encodeToString(copyData)
        }
    }

    private fun addSerializedNodes(json: String) {
        val scene = editor.activeScene.value
        if (json.isNotBlank() && scene != null) {
            try {
                val copyData = KoolEditor.jsonCodec.decodeFromString<List<GameEntityData>>(json)
                if (copyData.isNotEmpty()) {
                    val sanitized = sanitizeCopiedEntityIds(copyData, scene)

                    val selection = editor.selectionOverlay.getSelectedEntities()
                    val parent = selection.firstOrNull()?.parent ?: scene.sceneEntity
                    sanitized.toHierarchy().forEach { root -> root.entityData.parentId = parent.id }

                    AddEntitiesAction(sanitized).apply()
                    launchDelayed(1) {
                        val nodes = sanitized.mapNotNull { scene.sceneEntities[it.id] }
                        editor.selectionOverlay.setSelection(nodes)
                        editor.editMode.mode.set(EditorEditMode.Mode.MOVE_IMMEDIATE)
                    }
                }
            } catch (e: Exception) {
                logW { "Unable to paste clipboard content: Invalid content" }
            }
        }
    }

    private fun sanitizeCopiedEntityIds(entityData: List<GameEntityData>, scene: EditorScene): List<GameEntityData> {
        val existingNames = scene.sceneEntities.values.map { it.name }.toMutableSet()
        val sanitizedIds = entityData.associate { it.id to editor.projectModel.nextId() }

        return entityData.map { data ->
            val uniqueName = uniquifyName(data.settings.name, existingNames)
            data.copy(
                id = sanitizedIds[data.id]!!,
                parentId = sanitizedIds[data.parentId] ?: EntityId.NULL,
                settings = data.settings.copy(name = uniqueName)
            ).also {
                it.components.addAll(data.components)
                existingNames.add(uniqueName)
            }
        }
    }

    private fun uniquifyName(name: String, existingNames: Set<String>): String {
        var nameBase = name
        while (nameBase.isNotEmpty() && nameBase.last().isDigit()) {
            nameBase = nameBase.substring(0 until nameBase.lastIndex)
        }
        nameBase = nameBase.trim()

        var counter = 1
        var uniqueName = "$nameBase ${counter++}"
        while (uniqueName in existingNames) {
            uniqueName = "$nameBase ${counter++}"
        }
        return uniqueName
    }
}