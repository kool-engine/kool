package de.fabmax.kool.editor

import de.fabmax.kool.Clipboard
import de.fabmax.kool.editor.actions.AddNodeAction
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW
import kotlinx.serialization.encodeToString

object EditorClipboard {

    private val editor: KoolEditor
        get() = KoolEditor.instance

    fun copySelection() {
        val selection = editor.selectionOverlay.getSelectedSceneNodes()
        if (selection.isNotEmpty()) {
            logD { "Copy ${selection.size} selected objects" }
            if (selection.any { it.nodeData.childNodeIds.isNotEmpty() }) {
                logW { "Copied nodes contain child nodes, hierarchy won't be preserved during copy and paste. All copied nodes will be flattened" }
            }
            val json = KoolEditor.jsonCodec.encodeToString(selection.map { it.nodeData })
            Clipboard.copyToClipboard(json)
        } else {
            logD { "Nothing to copy: Selection is empty" }
        }
    }

    fun paste() {
        Clipboard.getStringFromClipboard { json ->
            val scene = editor.activeScene.value
            if (json != null && scene != null) {
                try {
                    val copyData = KoolEditor.jsonCodec.decodeFromString<List<SceneNodeData>>(json)
                    if (copyData.isNotEmpty()) {
                        logD { "Pasting ${copyData.size} objects from clipboard" }
                        sanitizeCopiedNodeIds(copyData)
                        val selection = editor.selectionOverlay.getSelectedNodes()
                        val parent = (selection.firstOrNull { it is SceneNodeModel } as SceneNodeModel?)?.parent ?: scene

                        AddNodeAction(copyData, parent.nodeId, scene.nodeId).apply()
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
    }

    fun duplicateSelection() {
        val selection = editor.selectionOverlay.getSelectedSceneNodes()
        logD { "Duplicate ${selection.size} selected objects" }

        val parent = selection.firstOrNull()?.parent ?: return
        val scene = when (parent) {
            is SceneModel -> parent
            is SceneNodeModel -> parent.sceneModel
        }

        val duplicatedNodes = selection.map { nodeModel ->
            val json = KoolEditor.jsonCodec.encodeToString(nodeModel.nodeData)
            val copyData = KoolEditor.jsonCodec.decodeFromString<SceneNodeData>(json)
            sanitizeCopiedNodeIds(listOf(copyData))
            copyData
        }

        AddNodeAction(duplicatedNodes, parent.nodeId, scene.nodeId).apply()
        launchDelayed(1) {
            val nodes = duplicatedNodes.mapNotNull { scene.nodeModels[it.nodeId] }
            editor.selectionOverlay.setSelection(nodes)
            editor.editMode.mode.set(EditorEditMode.Mode.MOVE_IMMEDIATE)
        }
    }

    private fun sanitizeCopiedNodeIds(copyData: List<SceneNodeData>) {
        // todo: support pasting node hierarchies, for now hierarchies are flattened
        copyData.forEach {
            it.nodeId = editor.projectModel.nextId()
            it.name = editor.projectModel.uniquifyName(it.name)
            it.childNodeIds.clear()
        }
    }
}