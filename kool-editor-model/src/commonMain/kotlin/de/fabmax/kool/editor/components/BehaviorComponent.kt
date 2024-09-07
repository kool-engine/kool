package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.*
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.logE

class BehaviorComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<BehaviorComponentData>
) : GameEntityDataComponent<BehaviorComponentData>(gameEntity, componentInfo) {

    override val componentType: String = "${this::class.simpleName}<${data.behaviorClassName}>"

    val behaviorInstance = mutableStateOf<KoolBehavior?>(null)

    private val EntityId.gameEntity: GameEntity? get() {
        return this@BehaviorComponent.gameEntity.scene.sceneEntities[this]
    }

    init {
        componentOrder = COMPONENT_ORDER_LATE
    }

    override suspend fun applyComponent() {
        checkDependencies()
        var allOk = true

        try {
            val behavior = BehaviorLoader.newInstance(data.behaviorClassName)
            behaviorInstance.set(behavior)

            // set script member properties from componentData, remove them in case they don't exist anymore (e.g.
            // because script has changed)
            val removeProps = mutableListOf<String>()
            data.propertyValues.forEach { (name, value) ->
                val setValue = when {
                    value.gameEntityRef != null -> {
                        val entity = value.gameEntityRef.gameEntity
                        if (!entity.isNullOrPrepared) {
                            allOk = false
                        }
                        entity
                    }

                    value.componentRef != null -> {
                        val entity = value.componentRef.entityId.gameEntity
                        if (!entity.isNullOrPrepared) {
                            allOk = false
                        }
                        entity?.getComponent(value.componentRef)
                    }

                    value.behaviorRef != null -> {
                        val entity = value.behaviorRef.entityId.gameEntity
                        if (!entity.isNullOrPrepared) {
                            allOk = false
                        }
                        entity?.getBehavior(value.behaviorRef)
                    }

                    else -> value.get()
                }

                if (!setProperty(name, setValue)) {
                    removeProps += name
                }
            }
            if (removeProps.isNotEmpty()) {
                setPersistent(data.copy(propertyValues = data.propertyValues - removeProps))
            }

        } catch (e: Exception) {
            logE { "Failed to initialize BehaviorComponent in entity ${gameEntity.id}('${gameEntity.name}'): $e" }
            e.printStackTrace()
        }

        if (allOk) {
            lifecycle = EntityLifecycle.PREPARED
            behaviorInstance.value?.init(this)
        }
    }

    private val GameEntity?.isNullOrPrepared: Boolean get() = this == null || isPreparedOrRunning

    override fun onStart() {
        super.onStart()
        behaviorInstance.value?.onStart()
    }

    override fun onUpdate(ev: RenderPass.UpdateEvent) {
        val instance = behaviorInstance.value ?: return
        if (AppState.appMode == AppMode.PLAY || instance.isUpdateInEditMode) {
            instance.onUpdate(ev)
        }
    }

    override fun onPhysicsUpdate(timeStep: Float) {
        behaviorInstance.value?.onPhysicsUpdate(timeStep)
    }

    override fun destroyComponent() {
        super.destroyComponent()
        behaviorInstance.value?.onDestroy()
    }

    fun setProperty(name: String, value: Any?): Boolean {
        return try {
            behaviorInstance.value?.let { BehaviorLoader.setProperty(it, name, value) }
            true
        } catch (e: Exception) {
            logE { "${data.behaviorClassName}: Failed setting property $name to value $value: $e" }
            e.printStackTrace()
            false
        }
    }

    fun getProperty(name: String): Any? {
        return behaviorInstance.value?.let { BehaviorLoader.getProperty(it, name) }
    }
}