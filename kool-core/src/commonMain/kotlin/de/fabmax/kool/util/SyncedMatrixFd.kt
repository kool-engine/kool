package de.fabmax.kool.util

import de.fabmax.kool.math.*

class SyncedMatrixFd {
    @PublishedApi
    internal val _matF = MutableMat4f()
    val matF: Mat4f get() {
        if (modCountF != modCount) {
            _matF.set(_matD)
            modCountF = modCount
        }
        return _matF
    }

    @PublishedApi
    internal val _matD = MutableMat4d()
    val matD: Mat4d get() {
        if (modCountD != modCount) {
            _matD.set(_matF)
            modCountD = modCount
        }
        return _matD
    }

    private val _invMatF = LazyMat4f { matF.invert(it) }
    val invF: Mat4f get() = _invMatF.get()

    private val _invMatD = LazyMat4d { matD.invert(it) }
    val invD: Mat4d get() = _invMatD.get()

    private var modCountF = 0
    private var modCountD = 0
    var modCount = 0
        private set

    @PublishedApi
    internal fun markUpdatedF() {
        modCount++
        modCountF = modCount
        _invMatF.isDirty = true
        _invMatD.isDirty = true
    }

    @PublishedApi
    internal fun markUpdatedD() {
        modCount++
        modCountD = modCount
        _invMatF.isDirty = true
        _invMatD.isDirty = true
    }

    inline fun setMatF(block: (MutableMat4f) -> Unit) {
        block(_matF)
        markUpdatedF()
    }

    inline fun setMatD(block: (MutableMat4d) -> Unit) {
        block(_matD)
        markUpdatedD()
    }
}