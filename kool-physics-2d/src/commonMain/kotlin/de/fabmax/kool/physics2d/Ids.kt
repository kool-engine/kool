package de.fabmax.kool.physics2d

/*
 * Box2D object IDs.
 * Usually long-lived and often used as map keys. We wrap them in data classes instead of values classes to avoid
 * repeated boxing/unboxing (this way they are only boxed once).
 * Also, we need to override hashCode to avoid runtime exceptions on js, because there the object IDs are provided by
 * the WASM implementation, which apparently uses a different underlying type for Long which does not implement
 * hashCode().
 */

internal data class BodyId(val id: Long) {
    override fun equals(other: Any?): Boolean = (other as? BodyId)?.id == id
    override fun hashCode(): Int = id.toInt() xor (id ushr 32).toInt()
}

internal data class ShapeId(val id: Long) {
    override fun equals(other: Any?): Boolean = (other as? ShapeId)?.id == id
    override fun hashCode(): Int = id.toInt() xor (id ushr 32).toInt()
}

internal data class WorldId(val id: Long) {
    override fun equals(other: Any?): Boolean = (other as? WorldId)?.id == id
    override fun hashCode(): Int = id.toInt() xor (id ushr 32).toInt()
}
