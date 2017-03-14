package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.BoundingBox

/**
 * @author fabmax
 */

interface UiNode {

    var layoutSpec: LayoutSpec

    var alpha: Float

    val contentBounds: BoundingBox
    val width: Float
        get() = contentBounds.size.x
    val height: Float
        get() = contentBounds.size.y
    val depth: Float
        get() = contentBounds.size.z

    var root: UiRoot?

    fun doLayout(bounds: BoundingBox, ctx: RenderContext)

    fun applyTheme(theme: UiTheme, ctx: RenderContext)

}
