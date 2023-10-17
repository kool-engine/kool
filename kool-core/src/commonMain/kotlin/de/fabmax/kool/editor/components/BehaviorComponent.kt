package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.BehaviorLoader
import de.fabmax.kool.editor.api.KoolBehavior
import de.fabmax.kool.editor.data.BehaviorComponentData
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.util.logE

class BehaviorComponent(nodeModel: NodeModel, override val componentData: BehaviorComponentData) :
    EditorModelComponent(nodeModel),
    EditorDataComponent<BehaviorComponentData>
{

    val behaviorClassNameState = mutableStateOf(componentData.behaviorClassName).onChange { componentData.behaviorClassName = it }
    val runInEditMode = mutableStateOf(componentData.runInEditMode).onChange { componentData.runInEditMode = it }

    val behaviorInstance = mutableStateOf<KoolBehavior?>(null)

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
                if (!setProperty(name, value.get())) {
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

    fun setProperty(name: String, value: Any): Boolean {
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