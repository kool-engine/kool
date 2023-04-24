package de.fabmax.kool.app

import de.fabmax.kool.editor.api.ClassFactory

actual object AppClassFactory : ClassFactory {
//    override fun createProceduralMesh(procMesh: MMesh): Mesh {
//        // Unfortunately, reflective class creation is not possible on JS
//        // todo: generate this file
//        return when (procMesh.generatorClass) {
//            "de.fabmax.kool.app.SampleProceduralMesh" -> SampleProceduralMesh()
//            else -> throw IllegalStateException("Procedural mesh generator class not registered: ${procMesh.generatorClass}")
//        }
//    }
}