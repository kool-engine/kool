package de.fabmax.kool.scene.ui

import de.fabmax.kool.assetTexture
import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.scene.Node
import de.fabmax.kool.shading.*
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */

open class UiPanel(name: String? = null) : Group(name), UiNode {

    override var layoutSpec = LayoutSpec()
    override val contentBounds = BoundingBox()

    override var root: UiRoot? = null
    override var parent: Node?
        get() = super.parent
        set(value) {
            super.parent = value
            if (value == null) {
                root = null
            } else if (value is UiNode) {
                root = value.root
                children.filter { it is UiNode }.forEach { (it as UiNode).root = root }
            }
        }

    protected var isUpdateNeeded = true

    protected val contentMesh: Mesh
    protected val contentMeshData = MeshData(true, true, true)
    protected val contentMeshBuilder = MeshBuilder(contentMeshData)
    protected val contentMeshItem = contentMeshData.data[0]

    val bgHelper = BlurredBackgroundHelper()

    var panelText = ""

    var font = Font.DEFAULT_FONT
        set(value) {
            field = value
            isUpdateNeeded = true
        }

    var backgroundColor = Color.WHITE
        set(value) {
            field = value
            isUpdateNeeded = true
        }

    init {
        contentMeshData.usage = GL.DYNAMIC_DRAW
        contentMesh = Mesh(contentMeshData)
        contentMesh.shader = blurShader(bgHelper) {
            colorModel = ColorModel.VERTEX_COLOR
        }.apply {
            colorMix = 0.7f
        }
        addNode(contentMesh)
    }

    override fun onLayout(bounds: BoundingBox, ctx: RenderContext) {
        if (!contentBounds.isEqual(bounds)) {
            contentBounds.set(bounds)
            isUpdateNeeded = true
        }
    }

    override fun render(ctx: RenderContext) {
        if (isUpdateNeeded) {
            update(ctx)
        }
        bgHelper.updateDistortionTexture(contentMesh, ctx)

        super.render(ctx)
    }

    protected open fun update(ctx: RenderContext) {
        isUpdateNeeded = false

        contentMeshData.clear()
        contentMeshBuilder.identity()
        contentMeshBuilder.translate(contentBounds.min)
        drawBackground(contentMeshBuilder, ctx)
    }

    protected open fun drawBackground(builder: MeshBuilder, ctx: RenderContext) {
        builder.run {
            color = backgroundColor
            rect {
                width = this@UiPanel.width
                height = this@UiPanel.height
                fullTexCoords()
            }

            /*color = Color.BLACK
            translate(pcR(50f, width), pcR(50f, height), 0f)
            text(font) {
                text = panelText
                origin.x = -font.textWidth(text) * 0.5f
                origin.y = -font.sizeUnits * 0.35f
            }*/
        }
    }

    override fun rayTest(test: RayTest) {
        val hitNode = test.hitNode
        super.rayTest(test)
        if (hitNode != test.hitNode) {
            // an element of this panel was hit!
            test.hitNode = this
            test.hitPositionLocal.subtract(bounds.min)
        }
    }
}
