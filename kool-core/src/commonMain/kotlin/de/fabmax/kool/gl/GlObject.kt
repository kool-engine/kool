package de.fabmax.kool.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.Disposable

/**
 * @author fabmax
 */
abstract class GlObject<T: GlResource> : Disposable {
    open var res: T? = null
        internal set

    open val isValid: Boolean
        get() = res?.isValid == true

    override fun dispose(ctx: KoolContext) {
        res?.delete(ctx)
        res = null
    }

}
