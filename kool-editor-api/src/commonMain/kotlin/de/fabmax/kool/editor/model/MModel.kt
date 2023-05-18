package de.fabmax.kool.editor.model

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.scene.Model
import de.fabmax.kool.scene.Node
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class MModel(
    override val nodeId: Long,
    var modelPath: String
) : MSceneNode(), Creatable<Model> {

    override val creatable: Creatable<out Node>
        get() = this

    @Transient
    private var created: Model? = null

    override fun getOrNull() = created

    override suspend fun getOrCreate() = created ?: create()

    override suspend fun create(): Model {
        disposeCreatedNode()

        val model = Assets.loadGltfModel(modelPath)
        model.name = name
        transform.toTransform(model.transform)
        created = model
        return model
    }

    override fun disposeCreatedNode() {
        created?.dispose(KoolSystem.requireContext())
        created = null
    }
}