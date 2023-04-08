package de.fabmax.kool.pipeline.shading

sealed class AlphaMode {
    object Blend : AlphaMode()

    object Opaque : AlphaMode()

    class Mask(val cutOff: Float) : AlphaMode() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Mask
            return cutOff == other.cutOff
        }

        override fun hashCode(): Int {
            return cutOff.hashCode()
        }
    }
}
