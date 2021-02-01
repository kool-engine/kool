package de.fabmax.kool.physics

class FilterData() {
    val data = IntArray(4)

    constructor(word0: Int, word1: Int = 0, word2: Int = 0, word3: Int = 0) : this() {
        set(word0, word1, word2, word3)
    }

    fun set(word0: Int, word1: Int = data[1], word2: Int = data[2], word3: Int = data[3]): FilterData {
        data[0] = word0
        data[1] = word1
        data[2] = word2
        data[3] = word3
        return this
    }

    fun set(other: FilterData): FilterData {
        data[0] = other.data[0]
        data[1] = other.data[1]
        data[2] = other.data[2]
        data[3] = other.data[3]
        return this
    }

    fun setFlag(word: Int, bit: Int, value: Boolean): FilterData {
        if (value) {
            setBit(word, bit)
        } else {
            clearBit(word, bit)
        }
        return this
    }

    fun setBit(word: Int, bit: Int): FilterData {
        data[word] = data[word] or (1 shl bit)
        return this
    }

    fun clearBit(word: Int, bit: Int): FilterData {
        data[word] = data[word] and (1 shl bit).inv()
        return this
    }
}