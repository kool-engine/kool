package de.fabmax.kool.pipeline.backend

import de.fabmax.kool.platform.KoolWindowJvm

interface RenderBackendJvm : RenderBackend {
    val window: KoolWindowJvm
}