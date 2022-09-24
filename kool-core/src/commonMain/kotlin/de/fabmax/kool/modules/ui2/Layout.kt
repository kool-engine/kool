package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext

interface Layout {
    fun measureContentSize(uiNode: UiNode, ctx: KoolContext)
    fun layoutChildren(uiNode: UiNode, ctx: KoolContext)
}