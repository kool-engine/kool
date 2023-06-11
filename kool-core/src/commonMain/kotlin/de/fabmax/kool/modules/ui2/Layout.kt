package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext

interface Layout {
    fun measureContentSize(uiNode: UiNode, ctx: KoolContext)
    fun layoutChildren(uiNode: UiNode, ctx: KoolContext)

    companion object {
        const val LAYOUT_EPS = 0.031415924f
    }
}