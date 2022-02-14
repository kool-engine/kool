package de.fabmax.kool.scene

class Tags(private val tags: MutableMap<String, Any?> = mutableMapOf()) : MutableMap<String, Any?> by tags {

    operator fun set(key: String, value: Any?) {
        put(key, value)
    }

    fun hasTag(tag: String, value: String? = null): Boolean {
        return containsKey(tag) && (value == null || this[tag] == value)
    }

    fun getBoolean(tag: String, default: Boolean): Boolean = getTyped(tag, default) { it.toBoolean() }

    fun getInt(tag: String, default: Int): Int = getTyped(tag, default) { it.toInt() }

    fun getFloat(tag: String, default: Float): Float = getTyped(tag, default) { it.toFloat() }

    fun getDouble(tag: String, default: Double): Double = getTyped(tag, default) { it.toDouble() }

    inline fun <reified T> getTyped(tag: String, default: T, mapper: (String) -> T): T {
        return when (val value = this[tag]) {
            null -> default
            is T -> value
            else -> {
                try {
                    mapper(value.toString())
                } catch (e: Exception) {
                    default
                }
            }
        }
    }

    override fun toString(): String {
        val str = StringBuilder()
        map { (k, v) ->
            if (v != null) "$k=$v" else k
        }.joinTo(str, prefix = "[", postfix = "]")
        return str.toString()
    }
}