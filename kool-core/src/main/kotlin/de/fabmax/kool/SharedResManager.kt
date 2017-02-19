package de.fabmax.kool

import de.fabmax.kool.platform.RenderContext

/**
 * @author fabmax
 */

abstract class SharedResManager<K, R> {

    protected val resources: MutableMap<K, SharedResource<R>> = mutableMapOf()

    protected fun addReference(key: K, ctx: RenderContext): R {
        var res = resources[key]
        if (res == null) {
            res = SharedResource(createResource(key, ctx))
            resources[key] = res
        }
        res.refCount++
        return res.resource
    }

    protected fun removeReference(key: K, ctx: RenderContext) {
        var res = resources[key]
        if (res != null) {
            if (--res.refCount == 0) {
                deleteResource(key, res.resource, ctx)
                resources.remove(key)
            }
        }
    }

    protected abstract fun createResource(key: K, ctx: RenderContext): R
    protected abstract fun deleteResource(key: K, res: R, ctx: RenderContext)

    protected class SharedResource<out R>(val resource: R) {
        var refCount = 0
    }
}
