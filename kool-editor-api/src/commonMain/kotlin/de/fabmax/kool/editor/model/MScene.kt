package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.api.ClassFactory
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import kotlinx.serialization.Serializable

@Serializable
class MScene(
    val commonProps: MCommonNodeProperties,
    val clearColor: MColor,
    val proceduralMeshes: List<MProceduralMesh>,
    //todo: val models: List<MModel>
) {

    fun createScene(classFactory: ClassFactory) = Scene(name = commonProps.name).apply {
        mainRenderPass.clearColor = clearColor.toColor()

        proceduralMeshes.forEach { procMeshModel ->
            val procMesh = classFactory.createProceduralMesh(procMeshModel)
            procMesh.name = procMeshModel.commonProps.name
            getParentNode(procMeshModel.commonProps.hierarchyPath).addNode(procMesh)
        }
    }

    private fun Scene.getParentNode(hierarchyPath: List<String>): Node {
        var node: Node = this
        for (i in 1 until hierarchyPath.lastIndex - 1) {
            val nextName = hierarchyPath[i]
            var nextNode = node.children.find { it.name == nextName }
            if (nextNode == null) {
                nextNode = Node(nextName)
                node.addNode(nextNode)
            }
            node = nextNode
        }
        return node
    }
}
