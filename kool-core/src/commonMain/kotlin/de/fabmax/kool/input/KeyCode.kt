package de.fabmax.kool.input

sealed class KeyCode(val code: Int, val isLocal: Boolean, name: String?) {
    val name = name ?: if (code in 32..126) "${code.toChar()}" else "$code"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KeyCode) return false
        return code == other.code && isLocal == other.isLocal
    }

    override fun hashCode(): Int = code * if (isLocal) -1 else 1
}

class UniversalKeyCode(code: Int, name: String? = null) : KeyCode(code, false, name) {
    constructor(codeChar: Char) : this(codeChar.uppercaseChar().code)
    override fun toString() = "{universal:$name}"
}

class LocalKeyCode(code: Int, name: String? = null) : KeyCode(code, true, name) {
    constructor(codeChar: Char) : this(codeChar.uppercaseChar().code)
    override fun toString() = "{local:$name}"
}