package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.GameEntityData
import kotlin.jvm.JvmName

class GameEntityDataHierarchy(val entityData: GameEntityData, val children: List<GameEntityDataHierarchy>) {

    fun flatten(): List<GameEntityData> {
        val result = mutableListOf<GameEntityData>()
        fun append(h: GameEntityDataHierarchy) {
            result += h.entityData
            h.children.forEach { append(it) }
        }
        append(this)
        return result
    }

    companion object {
        fun buildHierarchy(entities: List<GameEntityData>): List<GameEntityDataHierarchy> {
            val idMap = entities.associateBy { it.id }
            val childMap = mutableMapOf<EntityId?, MutableList<GameEntityData>>()
            entities.forEach {
                childMap.getOrPut(it.parentId) { mutableListOf() } += it
            }

            fun makeHierarchy(entity: GameEntityData): GameEntityDataHierarchy {
                val children = childMap[entity.id] ?: emptyList()
                return GameEntityDataHierarchy(entity, children.map { makeHierarchy(it) })
            }

            return entities.filter { it.parentId !in idMap }.map { makeHierarchy(it) }
        }
    }
}

@JvmName("dataToHierarchy")
fun List<GameEntityData>.toHierarchy() = GameEntityDataHierarchy.buildHierarchy(this)

@JvmName("entitiesToHierarchy")
fun List<GameEntity>.toHierarchy(): List<GameEntityDataHierarchy> {
    val allData = mutableSetOf<GameEntityData>()
    fun appendData(entity: GameEntity) {
        allData += entity.entityData
        entity.children.forEach { appendData(it) }
    }
    forEach { appendData(it) }
    return allData.toList().toHierarchy()
}