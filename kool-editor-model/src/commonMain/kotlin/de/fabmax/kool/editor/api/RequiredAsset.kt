package de.fabmax.kool.editor.api

sealed class RequiredAsset(val path: String) {

    class Texture(path: String) : RequiredAsset(path)
    class HdriEnvironment(path: String) : RequiredAsset(path)
    class Heightmap(path: String) : RequiredAsset(path)
    class Model(path: String) : RequiredAsset(path)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RequiredAsset
        return path == other.path
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}