package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MeshBuilder

interface ComponentUi {

    fun updateComponentAlpha()

    fun createUi(ctx: RenderContext)

    fun updateUi(ctx: RenderContext)

    fun removeUi(ctx: RenderContext)

    fun onRender(ctx: RenderContext)
}

open class BlankComponentUi : ComponentUi {

    // blank UI has no alpha to update...
    override fun updateComponentAlpha() { }

    // blank UI has nothing to create...
    override fun createUi(ctx: RenderContext) { }

    // blank UI has nothing to update...
    override fun updateUi(ctx: RenderContext) { }

    // blank UI has nothing to remove...
    override fun removeUi(ctx: RenderContext) { }

    // blank UI has nothing to render...
    override fun onRender(ctx: RenderContext) { }
}

open class SimpleComponentUi(val component: UiComponent) : ComponentUi {

    protected var shader: BasicShader? = null
    protected val meshData = MeshData(true, true, true)
    protected val meshBuilder = MeshBuilder(meshData)
    protected val mesh = Mesh(meshData)

    val color: ThemeOrCustomProp<Color> = ThemeOrCustomProp(Color.BLACK.withAlpha(0.5f))

    override fun updateComponentAlpha() {
        shader?.alpha = component.alpha
    }

    override fun createUi(ctx: RenderContext) {
        color.setTheme(component.root.theme.backgroundColor).apply()
        shader = createShader(ctx)
        shader?.staticColor?.set(color.prop)
        mesh.shader = shader
        component += mesh
    }

    override fun removeUi(ctx: RenderContext) {
        component -= mesh
        mesh.dispose(ctx)
    }

    override fun updateUi(ctx: RenderContext) {
        color.setTheme(component.root.theme.backgroundColor).apply()
        shader?.staticColor?.set(color.prop)

        component.setupBuilder(meshBuilder)
        meshBuilder.color = color.prop
        meshBuilder.rect {
            size.set(component.width, component.height)
            fullTexCoords()
        }
    }

    protected open fun createShader(ctx: RenderContext): BasicShader {
        return basicShader {
            lightModel = LightModel.PHONG_LIGHTING
            colorModel = ColorModel.STATIC_COLOR
            isAlpha = true
        }
    }

    override fun onRender(ctx: RenderContext) {
        // nothing to be done here...
    }
}

open class BlurredComponentUi(component: UiComponent) : SimpleComponentUi(component) {
    override fun createShader(ctx: RenderContext): BasicShader {
        return blurShader {
            lightModel = LightModel.PHONG_LIGHTING
            colorModel = ColorModel.STATIC_COLOR
            isAlpha = true
        }.apply {
            blurHelper = component.root.createBlurHelper()
        }
    }

    override fun updateUi(ctx: RenderContext) {
        super.updateUi(ctx)
        val bs = shader
        if (bs is BlurShader) {
            bs.colorMix = bs.staticColor.w
            bs.staticColor.w = 1f
        }
    }
}