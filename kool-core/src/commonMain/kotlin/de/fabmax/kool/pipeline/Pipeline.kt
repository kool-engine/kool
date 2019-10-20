package de.fabmax.kool.pipeline

import de.fabmax.kool.scene.Mesh

interface Pipeline {

    val inputLayout: VertexInputDescription

    val pipelineConfig: PipelineConfig

    fun bindMesh(mesh: Mesh)

}