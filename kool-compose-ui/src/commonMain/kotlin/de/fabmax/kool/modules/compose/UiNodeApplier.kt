package de.fabmax.kool.modules.compose

import androidx.compose.runtime.AbstractApplier
import de.fabmax.kool.modules.ui2.UiNode

/**
 * Class to let compose manage [UiNode]'s children.
 */
class UiNodeApplier(root: UiNode) : AbstractApplier<UiNode>(root) {
    var changed = false

    override fun onBeginChanges() {
        super.onBeginChanges()
        changed = true
    }

    override fun insertTopDown(index: Int, instance: UiNode) {
        // Ignored, we insert bottom-up.
    }

    override fun insertBottomUp(index: Int, instance: UiNode) {
        current.mutChildren.add(index, instance)
        check(instance.parent == null) {
            "$instance must not have a parent when being inserted."
        }
        instance.parent = current
    }

    override fun remove(index: Int, count: Int) {
        current.mutChildren.remove(index, count)
    }

    override fun move(from: Int, to: Int, count: Int) {
        current.mutChildren.move(from, to, count)
    }

    override fun onClear() {
        current.mutChildren.clear()
    }
}
