package de.fabmax.kool

/**
 * @author fabmax
 */

abstract class SharedResManager<K, R> {

    protected val resources: MutableMap<K, SharedResource<R>> = mutableMapOf()

    protected fun addReference(key: K, ctx: KoolContext): R {
        var res = resources[key]
        if (res == null) {
            res = SharedResource(createResource(key, ctx))
            resources[key] = res
        }
        res.refCount++
        return res.resource
    }

    protected fun removeReference(key: K, ctx: KoolContext) {
        val res = resources[key]
        if (res != null) {
            if (--res.refCount == 0) {
                deleteResource(key, res.resource, ctx)
                resources.remove(key)
            }
        }
    }

    protected abstract fun createResource(key: K, ctx: KoolContext): R
    protected abstract fun deleteResource(key: K, res: R, ctx: KoolContext)

    protected class SharedResource<out R>(val resource: R) {
        var refCount = 0
    }
}
