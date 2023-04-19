package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.model.MMesh
import de.fabmax.kool.scene.Mesh

interface ClassFactory {

    fun createProceduralMesh(procMesh: MMesh): Mesh

}