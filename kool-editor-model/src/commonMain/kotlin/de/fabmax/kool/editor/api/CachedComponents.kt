package de.fabmax.kool.editor.api

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.cast

class CachedComponents<T: Any>(val gameEntity: GameEntity, val componentClass: KClass<T>, val scope: Scope) {

    private var modCnt = 0
    private val cache = mutableListOf<T>()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<T> = cache

    fun getComponents(): List<T> {
        return when (scope) {
            Scope.ENTITY -> getEntityComponents()
            Scope.SCENE -> getSceneComponents()
        }
    }

    private fun getEntityComponents(): List<T> {
        return if (gameEntity.componentModCnt == modCnt) cache else {
            modCnt = gameEntity.componentModCnt
            gameEntity.components
                .filter { componentClass.isInstance(it) }
                .map { componentClass.cast(it) }
                .also {
                    cache.clear()
                    cache.addAll(it)
                }
        }
    }

    private fun getSceneComponents(): List<T> {
        return if (gameEntity.scene.componentModCnt == modCnt) cache else {
            modCnt = gameEntity.componentModCnt
            gameEntity.scene.sceneEntities.values
                .flatMap { it.components.filter { c -> componentClass.isInstance(c) } }
                .map { componentClass.cast(it) }
                .also {
                    cache.clear()
                    cache.addAll(it)
                }
        }
    }

    enum class Scope {
        ENTITY,
        SCENE
    }
}