package de.fabmax.kool.util

class LongHash {
    var hash: Long = 0L
        private set

    operator fun plusAssign(hash: LongHash) {
        this += hash.hash
    }

    operator fun plusAssign(long: Long) {
        hash = hash * 31L + long
    }

    operator fun plusAssign(int: Int) {
        hash = hash * 31L + int
    }

    operator fun plusAssign(bool: Boolean) {
        hash = hash * 31L + bool.hashCode()
    }

    operator fun plusAssign(float: Float) {
        hash = hash * 31L + float.hashCode()
    }

    operator fun plusAssign(enum: Enum<*>) {
        hash = hash * 31L + enum.hashCode()
    }

    operator fun plusAssign(string: String) {
        hash = hash * 31L + string.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as LongHash
        return hash == other.hash
    }

    override fun hashCode(): Int = hash.hashCode()

    override fun toString(): String = "$hash"
}