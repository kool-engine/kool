package de.fabmax.kool.editor

import de.fabmax.kool.Clipboard
import de.fabmax.kool.editor.actions.AddNodeAction
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

object EditorCopyAndPaste {

    fun copySelection() {
        val selection = EditorState.getSelectedSceneNodes()
        if (selection.isNotEmpty()) {
            logD { "Copy ${selection.size} selected objects" }
            if (selection.any { it.nodeData.childNodeIds.isNotEmpty() }) {
                logW { "Copied nodes contain child nodes, hierarchy won't be preserved during copy and paste. All copied nodes will be flattened" }
            }
            val json = EditorState.jsonCodec.encodeToString(selection.map { it.nodeData })
            Clipboard.copyToClipboard(json)
        } else {
            logD { "Nothing to copy: Selection is empty" }
        }
    }

    fun paste() {
        Clipboard.getStringFromClipboard { json ->
            val scene = EditorState.activeScene.value
            if (json != null && scene != null) {
                try {
                    val copyData = EditorState.jsonCodec.decodeFromString<List<SceneNodeData>>(json)
                    if (copyData.isNotEmpty()) {
                        logD { "Pasting ${copyData.size} objects from clipboard" }
                        sanitizeCopiedNodeIds(copyData)

                        val selection = EditorState.getSelectedNodes()
                        val parent = if (selection.size == 1) selection[0] else scene
                        val sceneNodes = copyData.map { SceneNodeModel(it, parent, scene) }
                        AddNodeAction(sceneNodes).apply()
                        EditorState.setSelection(sceneNodes)
                    }
                } catch (e: Exception) {
                    logW { "Unable to paste clipboard content: Invalid content" }
                }
            }
        }
    }

    fun duplicateSelection() {
        val selection = EditorState.getSelectedSceneNodes()
        logD { "Duplicate ${selection.size} selected objects" }
        val duplicatedNodes = selection.map { nodeModel ->
            val json = EditorState.jsonCodec.encodeToString(nodeModel.nodeData)
            val copyData = EditorState.jsonCodec.decodeFromString<SceneNodeData>(json)
            sanitizeCopiedNodeIds(listOf(copyData))

            val parent = nodeModel.parent
            SceneNodeModel(copyData, parent, nodeModel.sceneModel)
        }
        AddNodeAction(duplicatedNodes).apply()
        // update selection via launchOnMainThread so that it is called after node is inserted and components
        // are created
        launchOnMainThread {
            EditorState.setSelection(duplicatedNodes)
        }
    }

    private fun sanitizeCopiedNodeIds(copyData: List<SceneNodeData>) {
        // todo: support pasting node hierarchies, for now hierarchies are flattened
        copyData.forEach {
            it.nodeId = EditorState.projectModel.nextId()
            it.childNodeIds.clear()
        }
    }
}