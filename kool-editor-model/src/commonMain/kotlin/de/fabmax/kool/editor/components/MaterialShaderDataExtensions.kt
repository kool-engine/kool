package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.SceneShaderData
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslPbrSplatShader
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.pipeline.DrawShader

suspend fun MaterialComponentData.createShader(sceneShaderData: SceneShaderData, modelMats: List<ModelMatrixComposition>): DrawShader {
    return shaderData.createShader(sceneShaderData, modelMats)
}

suspend fun MaterialComponentData.updateShader(shader: DrawShader?, sceneShaderData: SceneShaderData): Boolean {
    return shaderData.updateShader(shader, sceneShaderData)
}

fun MaterialComponentData.matchesShader(shader: DrawShader?): Boolean = shaderData.matchesShader(shader)

suspend fun MaterialShaderData.createShader(sceneShaderData: SceneShaderData, modelMats: List<ModelMatrixComposition>): DrawShader = when (this) {
    is PbrShaderData -> createPbrShader(sceneShaderData, modelMats)
    is BlinnPhongShaderData -> TODO()
    is UnlitShaderData -> TODO()
    is PbrSplatShaderData -> createPbrSplatShader(sceneShaderData, modelMats)
}

suspend fun MaterialShaderData.updateShader(shader: DrawShader?, sceneShaderData: SceneShaderData): Boolean = when (this) {
    is PbrShaderData -> (shader as? KslPbrShader)?.let { updatePbrShader(it, sceneShaderData) } ?: false
    is BlinnPhongShaderData -> TODO()
    is UnlitShaderData -> TODO()
    is PbrSplatShaderData -> (shader as? KslPbrSplatShader)?.let { updatePbrSplatShader(it, sceneShaderData) } ?: false
}

fun MaterialShaderData.matchesShader(shader: DrawShader?): Boolean = when (this) {
    is PbrShaderData -> matchesPbrShaderConfig(shader)
    is BlinnPhongShaderData -> TODO()
    is UnlitShaderData -> TODO()
    is PbrSplatShaderData -> matchesPbrSplatShaderConfig(shader)
}
