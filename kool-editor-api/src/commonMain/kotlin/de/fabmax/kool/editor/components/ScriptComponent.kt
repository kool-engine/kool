package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.KoolScript
import de.fabmax.kool.editor.api.ScriptLoader
import de.fabmax.kool.editor.data.ScriptComponentData
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.util.logE

class ScriptComponent(nodeModel: EditorNodeModel, override val componentData: ScriptComponentData) :
    EditorModelComponent(nodeModel),
    EditorDataComponent<ScriptComponentData>
{

    val scriptClassNameState = mutableStateOf(componentData.scriptClassName).onChange { componentData.scriptClassName = it }
    val runInEditMode = mutableStateOf(componentData.runInEditMode).onChange { componentData.runInEditMode = it }

    val scriptInstance = mutableStateOf<KoolScript?>(null)

    init {
        componentOrder = COMPONENT_ORDER_LATE
    }

    override suspend fun createComponent() {
        super.createComponent()

        try {
            val script = ScriptLoader.newScriptInstance(componentData.scriptClassName)
            scriptInstance.set(script)

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
            scriptInstance.value?.init(nodeModel, this)

        } catch (e: Exception) {
            logE { "Failed to initialize ScriptComponents for node ${nodeModel.name}: $e" }
            e.printStackTrace()
        }
    }

    fun setProperty(name: String, value: Any): Boolean {
        return try {
            scriptInstance.value?.let { ScriptLoader.setScriptProperty(it, name, value) }
            true
        } catch (e: Exception) {
            logE { "${componentData.scriptClassName}: Failed setting property $name to value $value: $e" }
            false
        }
    }

    fun getProperty(name: String): Any? {
        return scriptInstance.value?.let { ScriptLoader.getScriptProperty(it, name) }
    }
}