package de.fabmax.kool.editor.ui

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.modules.ui2.*

fun UiScope.transformEditor(props: TransformProperties) = collapsapsablePanel("Transform") {
    Column(width = Grow.Std) {
        modifier
            .padding(horizontal = sizes.gap)
            .margin(bottom = sizes.smallGap)

        position(props)
        rotation(props)
        scale(props)
    }
}

private fun UiScope.position(props: TransformProperties) = xyzRow(
    label = "Position:",
    x = props.px.use(),
    y = props.py.use(),
    z = props.pz.use()
) { x, y, z ->
    props.setPosition(x, y, z)
    props.onChangedByEditor.forEach { it() }
}

private fun UiScope.rotation(props: TransformProperties) = xyzRow(
    label = "Rotation:",
    x = props.rx.use(),
    y = props.ry.use(),
    z = props.rz.use()
) { x, y, z ->
    props.setRotation(x, y, z)
    props.onChangedByEditor.forEach { it() }
}

private fun UiScope.scale(props: TransformProperties) = xyzRow(
    label = "Scale:",
    x = props.sx.use(),
    y = props.sy.use(),
    z = props.sz.use()
) { x, y, z ->
    props.setScale(x, y, z)
    props.onChangedByEditor.forEach { it() }
}

class TransformProperties {
    val px = mutableStateOf(0.0)
    val py = mutableStateOf(0.0)
    val pz = mutableStateOf(0.0)

    val rx = mutableStateOf(0.0)
    val ry = mutableStateOf(0.0)
    val rz = mutableStateOf(0.0)

    val sx = mutableStateOf(0.0)
    val sy = mutableStateOf(0.0)
    val sz = mutableStateOf(0.0)

    val onChangedByEditor = mutableListOf<() -> Unit>()

    fun getPosition(result: MutableVec3d): MutableVec3d {
        return result.set(px.value, py.value, pz.value)
    }

    fun getRotation(result: MutableVec3d): MutableVec3d {
        return result.set(rx.value, ry.value, rz.value)
    }

    fun getScale(result: MutableVec3d): MutableVec3d {
        return result.set(sx.value, sy.value, sz.value)
    }

    fun setPosition(position: Vec3d) = setPosition(position.x, position.y, position.z)

    fun setPosition(x: Double, y: Double, z: Double) {
        px.set(x)
        py.set(y)
        pz.set(z)
    }

    fun setRotation(position: Vec3d) = setRotation(position.x, position.y, position.z)

    fun setRotation(x: Double, y: Double, z: Double) {
        rx.set(x)
        ry.set(y)
        rz.set(z)
    }

    fun setScale(scale: Vec3d) = setScale(scale.x, scale.y, scale.z)

    fun setScale(x: Double, y: Double, z: Double) {
        sx.set(x)
        sy.set(y)
        sz.set(z)
    }
}
