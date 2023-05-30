package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.modules.ui2.mutableStateOf

class MaterialModel(materialData: MaterialData, private val editorProject: EditorProject) {

    val materialState = mutableStateOf(materialData).onChange {
        editorProject.projectData.materials[it.id] = it
    }

}