package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.MeshLayoutInfo
import de.fabmax.kool.editor.api.SceneShaderData
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslPbrSplatShader
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.pipeline.DrawShader

suspend fun MaterialComponentData.createShader(
    meshLayoutInfo: MeshLayoutInfo,
    modelMats: List<ModelMatrixComposition>,
    sceneShaderData: SceneShaderData,
): DrawShader {
    return shaderData.createShader(meshLayoutInfo, modelMats, sceneShaderData)
}

suspend fun MaterialComponentData.updateShader(shader: DrawShader?, sceneShaderData: SceneShaderData): Boolean {
    return shaderData.updateShader(shader, sceneShaderData)
}

fun MaterialComponentData.matchesShader(shader: DrawShader?): Boolean = shaderData.matchesShader(shader)

suspend fun MaterialShaderData.createShader(meshLayoutInfo: MeshLayoutInfo, modelMats: List<ModelMatrixComposition>, sceneShaderData: SceneShaderData): DrawShader = when (this) {
    is PbrShaderData -> createPbrShader(meshLayoutInfo, modelMats, sceneShaderData)
    is BlinnPhongShaderData -> TODO()
    is UnlitShaderData -> TODO()
    is PbrSplatShaderData -> createPbrSplatShader(meshLayoutInfo, modelMats, sceneShaderData)
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
