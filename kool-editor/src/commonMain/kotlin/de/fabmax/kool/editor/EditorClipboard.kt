package de.fabmax.kool.editor

import de.fabmax.kool.Clipboard
import de.fabmax.kool.editor.actions.AddSceneNodeAction
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.util.sceneNodeModel
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
        val selection = editor.selectionOverlay.getSelectedSceneNodes()
        return if (selection.isEmpty()) "" else {
            val copyNodes = mutableSetOf<SceneNodeData>()
            fun collect(node: SceneNodeModel) {
                if (copyNodes.add(node.nodeData)) {
                    node.nodeData.childNodeIds
                        .mapNotNull { it.sceneNodeModel }
                        .forEach { collect(it) }
                }
            }
            selection.forEach { collect(it) }

            logD { "Copy ${copyNodes.size} selected nodes" }
            KoolEditor.jsonCodec.encodeToString(copyNodes)
        }
    }

    private fun addSerializedNodes(json: String) {
        val scene = editor.activeScene.value
        if (json.isNotBlank() && scene != null) {
            try {
                val copyData = KoolEditor.jsonCodec.decodeFromString<List<SceneNodeData>>(json)
                if (copyData.isNotEmpty()) {
                    sanitizeCopiedNodeIds(copyData, scene)

                    val selection = editor.selectionOverlay.getSelectedNodes()
                    val parent = (selection.firstOrNull { it is SceneNodeModel } as SceneNodeModel?)?.parent ?: scene

                    AddSceneNodeAction(copyData, parent.nodeId).apply()
                    launchDelayed(1) {
                        val nodes = copyData.mapNotNull { scene.nodeModels[it.nodeId] }
                        editor.selectionOverlay.setSelection(nodes)
                        editor.editMode.mode.set(EditorEditMode.Mode.MOVE_IMMEDIATE)
                    }
                }
            } catch (e: Exception) {
                logW { "Unable to paste clipboard content: Invalid content" }
            }
        }
    }

    private fun sanitizeCopiedNodeIds(nodeData: List<SceneNodeData>, scene: SceneModel) {
        val existingNames = scene.nodeModels.values.map { it.name }.toMutableSet()
        val nodesByIds = nodeData.associateBy { it.nodeId }

        nodeData.forEach {
            it.nodeId = editor.projectModel.nextId()
            it.name = uniquifyName(it.name, existingNames)
            existingNames += it.name
        }
        nodeData.forEach {
            val newChildIds = it.childNodeIds.mapNotNull { oldId -> nodesByIds[oldId]?.nodeId }
            it.childNodeIds.clear()
            it.childNodeIds += newChildIds
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