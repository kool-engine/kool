package de.fabmax.kool.pipeline

class UniformLayoutDescription(val bindings: List<Binding>) {
    data class Binding(val name: String, val binding: Int, val type: UniformType, val stages: Set<Stage>, val count: Int)


}

enum class UniformType {
    IMAGE_SAMPLER,
    UNIFORM_BUFFER
}

enum class Stage {
    VERTEX_SHADER,
    GEOMETRY_SHADER,
    FRAGMENT_SHADER
}
