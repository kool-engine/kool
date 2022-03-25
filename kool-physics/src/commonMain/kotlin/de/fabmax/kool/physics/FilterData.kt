package de.fabmax.kool.physics

class FilterData(val word0: Int = 0, val word1: Int = 0, val word2: Int = 0, val word3: Int = 0)

fun FilterData(block: FilterDataBuilder.() -> Unit): FilterData = FilterDataBuilder().apply(block).build()

class FilterDataBuilder() {
    var word0 = 0
    var word1 = 0
    var word2 = 0
    var word3 = 0

    constructor(word0: Int, word1: Int = 0, word2: Int = 0, word3: Int = 0) : this() {
        set(word0, word1, word2, word3)
    }

    fun set(word0: Int, word1: Int = this.word1, word2: Int = this.word2, word3: Int = this.word3): FilterDataBuilder {
        this.word0 = word0
        this.word1 = word1
        this.word2 = word2
        this.word3 = word3
        return this
    }

    fun set(other: FilterData): FilterDataBuilder {
        word0 = other.word0
        word1 = other.word1
        word2 = other.word2
        word3 = other.word3
        return this
    }

    fun setFlag(word: Int, bit: Int, value: Boolean): FilterDataBuilder {
        if (value) {
            setBit(word, bit)
        } else {
            clearBit(word, bit)
        }
        return this
    }

    fun setBit(word: Int, bit: Int): FilterDataBuilder {
        if (bit !in 0..31) {
            throw IllegalArgumentException("bit must be within 0..31 (is $bit)")
        }
        this[word] = this[word] or (1 shl bit)
        return this
    }

    fun clearBit(word: Int, bit: Int): FilterDataBuilder {
        if (bit !in 0..31) {
            throw IllegalArgumentException("bit must be within 0..31 (is $bit)")
        }
        this[word] = this[word] and (1 shl bit).inv()
        return this
    }

    fun setCollisionGroup(group: Int): FilterDataBuilder {
        word0 = 0
        setBit(0, group)
        return this
    }

    fun clearCollidesWith(group: Int): FilterDataBuilder {
        clearBit(1, group)
        return this
    }

    fun setCollidesWith(group: Int): FilterDataBuilder {
        setBit(1, group)
        return this
    }

    fun setCollidesWithEverything(): FilterDataBuilder {
        word1 = -1
        return this
    }

    operator fun get(i: Int): Int {
        return when(i) {
            0 -> word0
            1 -> word1
            2 -> word2
            3 -> word3
            else -> throw IllegalArgumentException("i must be in range 0..3")
        }
    }

    operator fun set(i: Int, value: Int) {
        when(i) {
            0 -> word0 = value
            1 -> word1 = value
            2 -> word2 = value
            3 -> word3 = value
            else -> throw IllegalArgumentException("i must be in range 0..3")
        }
    }

    fun build(): FilterData {
        return FilterData(word0, word1, word2, word3)
    }
}