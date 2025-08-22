package de.fabmax.kool.util

import de.fabmax.kool.math.*

class SyncedMatrixFd {
    @PublishedApi
    internal val _matF = MutableMat4f()
    val matF: Mat4f get() {
        if (modCountF.count != modCount.count) {
            _matF.set(_matD)
            modCountF.reset(modCount)
        }
        return _matF
    }

    @PublishedApi
    internal val _matD = MutableMat4d()
    val matD: Mat4d get() {
        if (modCountD.count != modCount.count) {
            _matD.set(_matF)
            modCountD.reset(modCount)
        }
        return _matD
    }

    private val _invMatF = LazyMat4f { matF.invert(it) }
    val invF: Mat4f get() = _invMatF.get()

    private val _invMatD = LazyMat4d { matD.invert(it) }
    val invD: Mat4d get() = _invMatD.get()

    private val modCountF = ModCounter()
    private val modCountD = ModCounter()
    val modCount = ModCounter()

    @PublishedApi
    internal fun markUpdatedF() {
        modCount.increment()
        modCountF.reset(modCount.count)
        _invMatF.isDirty = true
        _invMatD.isDirty = true
    }

    @PublishedApi
    internal fun markUpdatedD() {
        modCount.increment()
        modCountD.reset(modCount)
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