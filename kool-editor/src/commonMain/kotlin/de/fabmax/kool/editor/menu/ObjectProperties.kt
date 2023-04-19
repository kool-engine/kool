package de.fabmax.kool.editor.menu

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.NodeTransformGizmo
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.editor.model.MSceneNode
import de.fabmax.kool.math.Mat3d
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Mesh

class ObjectProperties(val editor: KoolEditor) {

    private val windowState = WindowState().apply { setWindowSize(Dp(300f), Dp(600f)) }
    private val transformProperties = TransformProperties()

    private val tmpNodePos = MutableVec3d()
    private val tmpNodeRot = MutableVec3d()
    private val tmpNodeScale = MutableVec3d()
    private val tmpNodeRotMat = Mat3d()

    private val transformGizmo = NodeTransformGizmo(editor)

    init {
        transformProperties.onChangedByEditor += {
            editor.menu.sceneBrowser.selectedObject.value?.let { selectedNd ->
                transformProperties.getPosition(tmpNodePos)
                transformProperties.getRotation(tmpNodeRot)
                transformProperties.getScale(tmpNodeScale)

                val sceneNode = selectedNd.created
                if (sceneNode != null) {
                    val oldTransform = Mat4d().set(sceneNode.transform.matrix)
                    val newTransform = Mat4d()
                        .setRotate(tmpNodeRot.x, tmpNodeRot.y, tmpNodeRot.z)
                        .scale(tmpNodeScale)
                        .setOrigin(tmpNodePos)

                    applyTransformAction(selectedNd, oldTransform, newTransform)
                }
            }
        }
        editor.editorContent += transformGizmo
    }

    val windowSurface: UiSurface = Window(
        windowState,
        colors = EditorMenu.EDITOR_THEME_COLORS,
        name = "Object Properties"
    ) {
        modifier.backgroundColor(colors.background.withAlpha(0.8f))

        // clear gizmo transform object, will be set below if transform editor is available
        transformGizmo.setTransformObject(null)

        TitleBar()
        objectProperties()

        surface.onEachFrame {
            editor.menu.sceneBrowser.selectedObject.value?.created?.let { selectedNd ->
                selectedNd.transform.getPosition(tmpNodePos)
                transformProperties.setPosition(tmpNodePos)
                selectedNd.transform.matrix.getRotation(tmpNodeRotMat)
                transformProperties.setRotation(tmpNodeRotMat.getEulerAngles(tmpNodeRot))
                selectedNd.transform.matrix.getScale(tmpNodeScale)
                transformProperties.setScale(tmpNodeScale)
            }
        }
    }

    val windowScope: WindowScope = windowSurface.windowScope!!

    fun UiScope.objectProperties() = Column(Grow.Std, Grow.Std) {
        val selectedObject = editor.menu.sceneBrowser.selectedObject.use()

        Row(width = Grow.Std, height = sizes.gap * 3f) {
            modifier
                .backgroundColor(colors.secondaryVariantAlpha(0.5f))
                .padding(horizontal = sizes.gap)

            if (selectedObject == null) {
                Text("Nothing selected") {
                    modifier.alignY(AlignmentY.Center)
                }
                return@Column
            } else {
                Text(selectedObject.nodeProperties.name) {
                    modifier.alignY(AlignmentY.Center)
                }
            }
        }

        val sceneNode = selectedObject?.created
        when (sceneNode) {
//            is Scene -> sceneProperties(sceneNode, selectedObject)
            is Mesh -> meshProperties(sceneNode, selectedObject)
//            is Camera -> cameraProperties(sceneNode, selectedObject)
//            is Node -> nodeProperties(sceneNode, selectedObject)
        }
    }

//    fun UiScope.sceneProperties(scene: Scene, nodeModel: MSceneNode<*>) {
//        // - clear color
//        // - lighting
//        // - skybox
//    }

    fun UiScope.meshProperties(mesh: Mesh, nodeModel: MSceneNode<*>) {
        transformEditor(transformProperties)
        transformGizmo.setTransformObject(nodeModel)

        // - material (simple)
    }

//    fun UiScope.cameraProperties(cam: Camera, nodeModel: MSceneNode<*>) {
//        // transform? (is usually determined by parent transform node)
//        // perspective / ortho
//        // clip near/far
//        // fovy
//    }

//    fun UiScope.nodeProperties(node: Node, nodeModel: MSceneNode<*>) {
//        //transformEditor(transformProperties)
//    }

    companion object {
        fun applyTransformAction(nodeModel: MSceneNode<*>, oldTransform: Mat4d, newTransform: Mat4d) {
            val setTransform = SetTransformAction(
                editedNodeModel = nodeModel,
                oldTransform = oldTransform,
                newTransform = newTransform
            )
            EditorActions.applyAction(setTransform)
        }
    }
}