package de.fabmax.kool.app

import de.fabmax.kool.editor.api.ClassFactory
import de.fabmax.kool.editor.model.MProceduralMesh
import de.fabmax.kool.scene.Mesh

actual object AppClassFactory : ClassFactory {
    override fun createProceduralMesh(procMesh: MProceduralMesh): Mesh {
        // Unfortunately, reflective class creation is not possible on JS
        // todo: generate this file
        return when (procMesh.generatorClass) {
            "de.fabmax.kool.app.SampleProceduralMesh" -> SampleProceduralMesh()
            else -> throw IllegalStateException("Procedural mesh generator class not registered: ${procMesh.generatorClass}")
        }
    }
}