package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.BehaviorLoader
import de.fabmax.kool.editor.api.KoolBehavior
import de.fabmax.kool.editor.data.BehaviorComponentData
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.util.logE

class BehaviorComponent(nodeModel: NodeModel, override val componentData: BehaviorComponentData) :
    EditorModelComponent(nodeModel),
    EditorDataComponent<BehaviorComponentData>
{

    override val componentType: String = "${this::class.simpleName}<${componentData.behaviorClassName}>"

    val behaviorClassNameState = mutableStateOf(componentData.behaviorClassName).onChange { componentData.behaviorClassName = it }
    val runInEditMode = mutableStateOf(componentData.runInEditMode).onChange { componentData.runInEditMode = it }

    val behaviorInstance = mutableStateOf<KoolBehavior?>(null)

    private val sceneModel: SceneModel get() = when (val nd = this@BehaviorComponent.nodeModel) {
        is SceneNodeModel -> nd.sceneModel
        is SceneModel -> nd
    }

    private val NodeId.nodeModel: NodeModel? get() {
        val scene = sceneModel
        return scene.nodeModels[this] ?: if (this == scene.nodeId) scene else null
    }

    init {
        componentOrder = COMPONENT_ORDER_LATE
    }

    override suspend fun createComponent() {
        super.createComponent()

        try {
            val behavior = BehaviorLoader.newInstance(componentData.behaviorClassName)
            behaviorInstance.set(behavior)

            // set script member properties from componentData, remove them in case they don't exist anymore (e.g.
            // because script has changed)
            val removeProps = mutableListOf<String>()
            componentData.propertyValues.forEach { (name, value) ->
                val setValue = when {
                    value.nodeRef != null -> value.nodeRef.nodeModel
                    value.componentRef != null -> {
                        val refNode = value.componentRef.nodeId.nodeModel
                        refNode?.components?.find { value.componentRef.componentClassName == it::class.qualifiedName }
                    }
                    else -> value.get()
                }
                if (!setProperty(name, setValue)) {
                    removeProps += name
                }
            }
            removeProps.forEach { componentData.propertyValues -= it }

            // invoke script init callback
            behavior.init(nodeModel, this)

        } catch (e: Exception) {
            logE { "Failed to initialize BehaviorComponent for node ${nodeModel.name}: $e" }
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        behaviorInstance.value?.onStart()
    }

    fun setProperty(name: String, value: Any?): Boolean {
        return try {
            behaviorInstance.value?.let { BehaviorLoader.setProperty(it, name, value) }
            true
        } catch (e: Exception) {
            logE { "${componentData.behaviorClassName}: Failed setting property $name to value $value: $e" }
            false
        }
    }

    fun getProperty(name: String): Any? {
        return behaviorInstance.value?.let { BehaviorLoader.getProperty(it, name) }
    }
}