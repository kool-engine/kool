package de.fabmax.kool.physics

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.Disposable

interface Releasable : Disposable {

    fun release()

    override fun dispose(ctx: KoolContext) {
        release()
    }
}