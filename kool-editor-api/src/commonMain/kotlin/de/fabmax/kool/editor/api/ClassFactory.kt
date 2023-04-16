package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.model.MProceduralMesh
import de.fabmax.kool.scene.Mesh

interface ClassFactory {

    fun createProceduralMesh(procMesh: MProceduralMesh): Mesh

}