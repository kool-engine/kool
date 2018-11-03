package de.fabmax.kool.util

import de.fabmax.kool.KoolContext

interface Disposable {
    fun dispose(ctx: KoolContext)
}
