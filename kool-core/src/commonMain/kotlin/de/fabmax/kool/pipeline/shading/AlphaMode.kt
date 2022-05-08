package de.fabmax.kool.pipeline.shading

sealed class AlphaMode {
    class Blend : AlphaMode() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            return true
        }

        override fun hashCode() = this::class.hashCode()
    }

    class Opaque : AlphaMode() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            return true
        }

        override fun hashCode() = this::class.hashCode()
    }

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
