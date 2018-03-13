package de.fabmax.kool.gl

import de.fabmax.kool.KoolContext

/**
 * @author fabmax
 */
abstract class GlObject<T: GlResource> {
    open var res: T? = null
        protected set

    open val isValid: Boolean
        get() = res != null

    open fun dispose(ctx: KoolContext) {
        res?.delete(ctx)
        res = null
    }

}
