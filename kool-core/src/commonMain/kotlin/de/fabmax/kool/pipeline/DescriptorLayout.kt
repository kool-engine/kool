package de.fabmax.kool.pipeline

class DescriptorLayout(val descriptors: List<Descriptor>)

abstract class Descriptor(val name: String, val type: DescriptorType, val stages: Set<Stage>)

class TextureSampler(name: String, vararg stages: Stage) : Descriptor(name, DescriptorType.IMAGE_SAMPLER, stages.toSet())

enum class DescriptorType {
    IMAGE_SAMPLER,
    UNIFORM_BUFFER
}

enum class Stage {
    VERTEX_SHADER,
    GEOMETRY_SHADER,
    FRAGMENT_SHADER
}
