package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.BehaviorLoader
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.KoolBehavior
import de.fabmax.kool.editor.data.BehaviorComponentData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.getComponent
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.util.logE

class BehaviorComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<BehaviorComponentData>
) : GameEntityDataComponent<BehaviorComponent, BehaviorComponentData>(gameEntity, componentInfo) {

    override val componentType: String = "${this::class.simpleName}<${data.behaviorClassName}>"

    val behaviorClassNameState = mutableStateOf(data.behaviorClassName).onChange {
        data = data.copy(behaviorClassName = it)
    }

    val behaviorInstance = mutableStateOf<KoolBehavior?>(null)

    private val EntityId.gameEntity: GameEntity? get() {
        return this@BehaviorComponent.gameEntity.scene.sceneEntities[this]
    }

    init {
        componentOrder = COMPONENT_ORDER_LATE
    }

    override suspend fun applyComponent() {
        super.applyComponent()

        try {
            val behavior = BehaviorLoader.newInstance(data.behaviorClassName)
            behaviorInstance.set(behavior)

            // set script member properties from componentData, remove them in case they don't exist anymore (e.g.
            // because script has changed)
            val removeProps = mutableListOf<String>()
            data.propertyValues.forEach { (name, value) ->
                val setValue = when {
                    value.nodeRef != null -> value.nodeRef.gameEntity
                    value.componentRef != null -> value.componentRef.entityId.gameEntity?.getComponent(value.componentRef)
                    else -> value.get()
                }
                if (!setProperty(name, setValue)) {
                    removeProps += name
                }
            }
            if (removeProps.isNotEmpty()) {
                setPersistent(data.copy(propertyValues = data.propertyValues - removeProps))
            }

            // invoke script init callback
            behavior.init(gameEntity, this)

        } catch (e: Exception) {
            logE { "Failed to initialize BehaviorComponent for node ${gameEntity.name}: $e" }
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
            logE { "${data.behaviorClassName}: Failed setting property $name to value $value: $e" }
            false
        }
    }

    fun getProperty(name: String): Any? {
        return behaviorInstance.value?.let { BehaviorLoader.getProperty(it, name) }
    }
}