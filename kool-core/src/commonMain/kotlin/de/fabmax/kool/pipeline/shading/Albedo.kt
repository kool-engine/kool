package de.fabmax.kool.pipeline.shading

enum class Albedo {
    CUBE_MAP_ALBEDO,
    STATIC_ALBEDO,
    TEXTURE_ALBEDO,
    VERTEX_ALBEDO
}

enum class AlbedoMapMode {
    UNMODIFIED,
    MULTIPLY_BY_UNIFORM,
    MULTIPLY_BY_VERTEX
}