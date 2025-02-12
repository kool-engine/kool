package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.util.UniqueId

sealed interface StorageTexture {
    val asTexture: Texture<*>
}

fun StorageTexture1d(
    width: Int,
    props: TextureProps,
    name: String = UniqueId.nextId("StorageTexture1d")
): StorageTexture1d = StorageTexture1d(width, props, KoolSystem.requireContext().backend, name)

fun StorageTexture2d(
    width: Int,
    height: Int,
    props: TextureProps,
    name: String = UniqueId.nextId("StorageTexture2d")
): StorageTexture2d = StorageTexture2d(width, height, props, KoolSystem.requireContext().backend, name)

fun StorageTexture3d(
    width: Int,
    height: Int,
    depth: Int,
    props: TextureProps,
    name: String = UniqueId.nextId("StorageTexture3d")
):  StorageTexture3d = StorageTexture3d(width, height, depth, props, KoolSystem.requireContext().backend, name)

class StorageTexture1d(
    width: Int,
    props: TextureProps,
    private val backend: RenderBackend,
    name: String = UniqueId.nextId("StorageTexture1d")
) : Texture1d(props, name), StorageTexture {
    override val asTexture: Texture1d get() = this

    init {
        backend.initStorageTexture(this, width, 1, 1)
    }

    fun resize(width: Int) {
        backend.initStorageTexture(this, width, 1, 1)
    }
}

class StorageTexture2d(
    width: Int,
    height: Int,
    props: TextureProps,
    private val backend: RenderBackend,
    name: String
) : Texture2d(props, name), StorageTexture {
    override val asTexture: Texture2d get() = this

    init {
        backend.initStorageTexture(this, width, height, 1)
    }

    fun resize(width: Int, height: Int) {
        backend.initStorageTexture(this, width, height, 1)
    }
}

class StorageTexture3d(
    width: Int,
    height: Int,
    depth: Int,
    props: TextureProps,
    private val backend: RenderBackend,
    name: String
) : Texture3d(props, name), StorageTexture {
    override val asTexture: Texture3d get() = this

    init {
        backend.initStorageTexture(this, width, height, depth)
    }

    fun resize(width: Int, height: Int, depth: Int) {
        backend.initStorageTexture(this, width, height, depth)
    }
}