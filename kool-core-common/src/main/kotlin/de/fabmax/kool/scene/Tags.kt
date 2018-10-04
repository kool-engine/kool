package de.fabmax.kool.scene

class Tags(private val tags: MutableMap<String, String?> = mutableMapOf()) : MutableMap<String, String?> by tags{

    fun addTag(tag: String) {
        val splitIdx = tag.indexOf('=')
        if (splitIdx >= 0) {
            // add key / value tag
            val key = tag.substring(0..splitIdx-1)
            val value = tag.substring(splitIdx+1)
            this[key] = value
        } else {
            // add tag without value
            this[tag] = null
        }
    }

    operator fun set(key: String, value: Any?) {
        put(key, value?.toString())
    }

    operator fun plusAssign(tag: String) {
        addTag(tag)
    }

    fun hasTag(tag: String, value: String? = null): Boolean {
        return containsKey(tag) && this[tag] == value
    }

    fun getBoolean(tag: String, default: Boolean): Boolean = getTyped(tag, default) { it.toBoolean() }

    fun getInt(tag: String, default: Int): Int = getTyped(tag, default) { it.toInt() }

    fun getFloat(tag: String, default: Float): Float = getTyped(tag, default) { it.toFloat() }

    fun getDouble(tag: String, default: Double): Double = getTyped(tag, default) { it.toDouble() }

    inline fun <T> getTyped(tag: String, default: T, mapper: (String) -> T): T {
        val value = this[tag]
        return if (value == null) {
            default
        } else {
            try {
                mapper(value)
            } catch (e: Exception) {
                default
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