package de.fabmax.kool.editor.menu

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.math.Mat3d
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene

class ObjectProperties(val editor: KoolEditor) {

    private val windowState = WindowState().apply { setWindowSize(Dp(300f), Dp(600f)) }
    private val transformProperties = TransformProperties()

    private val tmpNodePos = MutableVec3d()
    private val tmpNodeRot = MutableVec3d()
    private val tmpNodeScale = MutableVec3d()
    private val tmpNodeRotMat = Mat3d()

    init {
        transformProperties.onChangedByEditor += {
            editor.menu.sceneBrowser.selectedObject.value?.let { selectedNd ->
                transformProperties.getPosition(tmpNodePos)
                transformProperties.getRotation(tmpNodeRot)
                transformProperties.getScale(tmpNodeScale)

                val oldTransform = Mat4d().set(selectedNd.transform.matrix)
                val newTransform = Mat4d()
                    .setRotate(tmpNodeRot.x, tmpNodeRot.y, tmpNodeRot.z)
                    .scale(tmpNodeScale)
                    .setOrigin(tmpNodePos)
                EditorActions.applyAction(SetTransformAction(selectedNd, oldTransform, newTransform))
            }
        }
    }

    val windowSurface: UiSurface = Window(
        windowState,
        colors = EditorMenu.EDITOR_THEME_COLORS,
        name = "Object Properties"
    ) {
        modifier.backgroundColor(colors.background.withAlpha(0.8f))

        TitleBar()
        objectProperties()

        surface.onEachFrame {
            editor.menu.sceneBrowser.selectedObject.value?.let { selectedNd ->
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
                Text(selectedObject.name) {
                    modifier.alignY(AlignmentY.Center)
                }
            }

            // Don't forget undo / redo!
        }

        when (selectedObject) {
            is Scene -> sceneProperties(selectedObject)
            is Mesh -> meshProperties(selectedObject)
            is Camera -> cameraProperties(selectedObject)
            is Node -> nodeProperties(selectedObject)
        }
    }

    fun UiScope.sceneProperties(scene: Scene) {
        // - clear color
        // - lighting
        // - skybox
    }

    fun UiScope.meshProperties(mesh: Mesh) {
        transformEditor(transformProperties)

        // - material (simple)
    }

    fun UiScope.cameraProperties(cam: Camera) {
        // transform? (is usually determined by parent transform node)
        // perspective / ortho
        // clip near/far
        // fovy
    }

    fun UiScope.nodeProperties(node: Node) {
        transformEditor(transformProperties)
    }
}