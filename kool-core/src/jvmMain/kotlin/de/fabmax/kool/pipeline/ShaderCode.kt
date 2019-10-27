package de.fabmax.kool.pipeline

import de.fabmax.kool.platform.vk.pipeline.ShaderStage

actual class ShaderCode(val stages: List<ShaderStage>) {
    constructor(vararg stages: ShaderStage): this(stages.asList())
}