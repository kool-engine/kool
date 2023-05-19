package de.fabmax.kool.editor.model

//@Serializable
//class MModel(
//    override val nodeId: Long,
//    var modelPath: String
//) : MSceneNode(), Creatable<Model> {
//
//    override val creatable: Creatable<out Node>
//        get() = this
//
//    @Transient
//    private var created: Model? = null
//
//    override fun getOrNull() = created
//
//    override suspend fun getOrCreate(createContext: CreateContext) = created ?: create(createContext)
//
//    override suspend fun create(createContext: CreateContext): Model {
//        disposeCreatedNode()
//
//        val model = Assets.loadGltfModel(modelPath)
//        model.name = name
//        transform.toTransform(model.transform)
//        created = model
//        return model
//    }
//
//    override fun disposeCreatedNode() {
//        created?.dispose(KoolSystem.requireContext())
//        created = null
//    }
//}