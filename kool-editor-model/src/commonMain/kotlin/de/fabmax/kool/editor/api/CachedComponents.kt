package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.GameEntityComponent
import de.fabmax.kool.editor.components.project
import de.fabmax.kool.editor.components.scene
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.cast

class CachedEntityComponents<T: Any>(val gameEntity: GameEntity, val componentClass: KClass<T>) {
    private var modCnt = 0
    private val cache = mutableListOf<T>()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<T> = getComponents()

    fun getComponents(): List<T> {
        return if (gameEntity.componentModCnt == modCnt) cache else {
            modCnt = gameEntity.componentModCnt
            cache.clear()
            gameEntity.components
                .filter { componentClass.isInstance(it) }
                .map { componentClass.cast(it) }
                .also { cache.addAll(it) }
        }
    }
}

class CachedSceneComponents<T: Any>(val scene: EditorScene, val componentClass: KClass<T>) {
    private var modCnt = 0
    private val cache = mutableListOf<T>()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<T> = getComponents()

    fun getComponents(): List<T> {
        return if (scene.componentModCnt == modCnt) cache else {
            modCnt = scene.componentModCnt
            cache.clear()
            scene.sceneEntities.values
                .flatMap { it.components.filter { c -> componentClass.isInstance(c) } }
                .map { componentClass.cast(it) }
                .also { cache.addAll(it) }
        }
    }
}

class CachedProjectComponents<T: Any>(val project: EditorProject, val componentClass: KClass<T>) {
    private var modCnts = intArrayOf()
    private val cache = mutableListOf<T>()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<T> = getComponents()

    fun getComponents(): List<T> {
        val scenes = project.createdScenes.values.toList() + project.materialScene
        if (modCnts.size != scenes.size) {
            modCnts = IntArray(scenes.size)
        }

        var isDirty = false
        for (i in modCnts.indices) {
            if (modCnts[i] != scenes[i].componentModCnt) {
                isDirty = true
            }
            modCnts[i] = scenes[i].componentModCnt
        }

        if (isDirty) {
            cache.clear()
            scenes.forEach { scene ->
                scene.sceneEntities.values
                    .flatMap { it.components.filter { c -> componentClass.isInstance(c) } }
                    .map { componentClass.cast(it) }
                    .also { cache.addAll(it) }
            }
        }
        return cache
    }
}

inline fun <reified T: Any> GameEntity.cachedEntityComponents(): CachedEntityComponents<T> {
    return CachedEntityComponents(this, T::class)
}

inline fun <reified T: Any> GameEntityComponent.cachedEntityComponents(): CachedEntityComponents<T> {
    return CachedEntityComponents(gameEntity, T::class)
}

inline fun <reified T: Any> GameEntity.cachedSceneComponents(): CachedSceneComponents<T> {
    return CachedSceneComponents(scene, T::class)
}

inline fun <reified T: Any> GameEntityComponent.cachedSceneComponents(): CachedSceneComponents<T> {
    return CachedSceneComponents(scene, T::class)
}

inline fun <reified T: Any> GameEntity.cachedProjectComponents(): CachedProjectComponents<T> {
    return CachedProjectComponents(project, T::class)
}

inline fun <reified T: Any> GameEntityComponent.cachedProjectComponents(): CachedProjectComponents<T> {
    return CachedProjectComponents(project, T::class)
}