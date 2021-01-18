package de.fabmax.kool.physics

class PhysicsFilterData() {
    val data = IntArray(4)

    constructor(word0: Int, word1: Int, word2: Int, word3: Int) : this() {
        set(word0, word1, word2, word3)
    }

    fun set(word0: Int, word1: Int = data[1], word2: Int = data[2], word3: Int = data[3]) {
        data[0] = word0
        data[1] = word1
        data[2] = word2
        data[3] = word3
    }

    fun set(other: PhysicsFilterData) {
        data[0] = other.data[0]
        data[1] = other.data[1]
        data[2] = other.data[2]
        data[3] = other.data[3]
    }

    fun setFlag(word: Int, bit: Int, value: Boolean) {
        if (value) {
            setBit(word, bit)
        } else {
            clearBit(word, bit)
        }
    }

    fun setBit(word: Int, bit: Int) {
        data[word] = data[word] or (1 shl bit)
    }

    fun clearBit(word: Int, bit: Int) {
        data[word] = data[word] and (1 shl bit).inv()
    }
}