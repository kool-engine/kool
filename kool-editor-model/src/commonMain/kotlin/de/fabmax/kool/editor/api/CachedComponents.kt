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

    val isOutdated: Boolean get() = gameEntity.componentModCnt != modCnt

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

    val isOutdated: Boolean get() = scene.componentModCnt != modCnt

    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<T> = getComponents()

    fun getComponents(): List<T> {
        if (isOutdated) {
            modCnt = scene.componentModCnt
            cache.clear()
            scene.orderedEntities.asSequence()
                .flatMap { it.components.filter { c -> componentClass.isInstance(c) } }
                .map { componentClass.cast(it) }
                .toList()
                .also { cache.addAll(it) }
            scene.listenerComponents
                .filter { c -> componentClass.isInstance(c) }
                .forEach { cache.add(componentClass.cast(it)) }
        }
        return cache
    }
}

class CachedProjectComponents<T: Any>(val project: EditorProject, val componentClass: KClass<T>) {
    private var sceneModCnt = -1
    private var materialModCnt = -1
    private var sceneModCnts = intArrayOf()
    private val cache = mutableListOf<T>()

    val isOutdated: Boolean get() {
        var isDirty = false
        val numScenes = project.createdScenes.size
        if (sceneModCnts.size != numScenes) {
            sceneModCnts = IntArray(numScenes) { -1 }
            isDirty = true
        }
        if (project.sceneModCnt != sceneModCnt) {
            isDirty = true
        }

        if (materialModCnt != project.materialScene.componentModCnt) {
            isDirty = false
            materialModCnt = project.materialScene.componentModCnt
        }
        val scenes = project.createdScenes.values.toList()
        project.createdScenes.values.forEachIndexed { i, scene ->
            if (sceneModCnts[i] != scenes[i].componentModCnt) {
                isDirty = true
            }
            sceneModCnts[i] = scenes[i].componentModCnt
        }
        return isDirty
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<T> = getComponents()

    fun getComponents(): List<T> {
        if (isOutdated) {
            cache.clear()
            val allScenes = project.createdScenes.values + project.materialScene
            allScenes.forEach { scene ->
                scene.orderedEntities.asSequence()
                    .flatMap { it.components.filter { c -> componentClass.isInstance(c) } }
                    .map { componentClass.cast(it) }
                    .also { cache.addAll(it) }
                scene.listenerComponents
                    .filter { c -> componentClass.isInstance(c) }
                    .forEach { cache.add(componentClass.cast(it)) }
            }
            project.listenerComponents
                .filter { c -> componentClass.isInstance(c) }
                .forEach { cache.add(componentClass.cast(it)) }
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