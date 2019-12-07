package de.fabmax.kool.pipeline.shadermodel

class ShaderModel {

    var baseMaterial = BaseMaterial.UNLIT

    var baseAlbedo = BaseAlbedo.STATIC
    val albedoCustomizer: (ShaderModel) -> String = { "" }

    val textures = mutableSetOf<String>()

}