package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.scene.TrsTransformD

interface GizmoListener {

    fun onGizmoUpdate(transform: TrsTransformD) { }

    fun onManipulationStart(startTransform: TrsTransformD) { }

    fun onManipulationFinished(startTransform: TrsTransformD, endTransform: TrsTransformD) { }

    fun onManipulationCanceled(startTransform: TrsTransformD) { }
}