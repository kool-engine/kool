package de.fabmax.kool.gl

import de.fabmax.kool.platform.RenderContext

/**
 * @author fabmax
 */
abstract class GlObject<T: GlResource> {
    open var res: T? = null
        protected set

    open val isValid: Boolean
        get() = res != null

    open fun delete(ctx: RenderContext) {
        res?.delete(ctx)
        res = null
    }

}
