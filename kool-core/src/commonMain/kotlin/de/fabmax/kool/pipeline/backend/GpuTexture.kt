package de.fabmax.kool.pipeline.backend

import de.fabmax.kool.util.Releasable

interface GpuTexture : Releasable {
    val width: Int
    val height: Int
    val depth: Int
}