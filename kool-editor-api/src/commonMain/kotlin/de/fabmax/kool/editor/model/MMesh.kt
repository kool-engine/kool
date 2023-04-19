package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.api.ClassFactory
import de.fabmax.kool.scene.Mesh
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MMesh(
    override val nodeProperties: MCommonNodeProperties,
    val generatorClass: String
) : MSceneNode<Mesh> {

    @Transient
    override var created: Mesh? = null

    override fun create(classFactory: ClassFactory): Mesh {
        val mesh = classFactory.createProceduralMesh(this)
        mesh.name = nodeProperties.name
        nodeProperties.transform.toTransform(mesh.transform)
        created = mesh
        return mesh
    }
}
