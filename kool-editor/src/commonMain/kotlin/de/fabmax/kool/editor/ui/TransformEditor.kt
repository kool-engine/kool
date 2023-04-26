package de.fabmax.kool.editor.ui

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.MdColor

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

private fun UiScope.position(props: TransformProperties) = xyzTextFields(
    title = "Position:",
    props = props,
    x = props.px,
    y = props.py,
    z = props.pz,
    precision = 3
)

private fun UiScope.rotation(props: TransformProperties) = xyzTextFields(
    title = "Rotation:",
    props = props,
    x = props.rx,
    y = props.ry,
    z = props.rz,
    precision = 1
)

private fun UiScope.scale(props: TransformProperties) = xyzTextFields(
    title = "Scale:",
    props = props,
    x = props.sx,
    y = props.sy,
    z = props.sz,
    precision = 3
)

private fun UiScope.xyzTextFields(
    title: String,
    props: TransformProperties,
    x: MutableStateValue<Double>,
    y: MutableStateValue<Double>,
    z: MutableStateValue<Double>,
    precision: Int
) = Column(width = Grow.Std) {
    val boldTxt = sizes.boldText
    Row(height = sizes.lineHeight) {
        modifier.margin(top = sizes.smallGap)
        Text(title) {
            modifier
                .font(boldTxt)
                .alignY(AlignmentY.Center)
        }
    }
    Row(width = Grow.Std, height = sizes.lineHeight) {
        modifier.margin(start = sizes.gap)
        Text("X") {
            modifier
                .alignY(AlignmentY.Center)
                .font(boldTxt)
                .textColor(MdColor.RED tone 300)
        }
        doubleTextField(x.use(), precision, width = Grow.Std) {
            x.set(it)
            props.onChangedByEditor.forEach { it() }
        }

        Text("Y") {
            modifier
                .margin(start = sizes.gap)
                .alignY(AlignmentY.Center)
                .font(boldTxt)
                .textColor(MdColor.GREEN tone 300)
        }
        doubleTextField(y.use(), precision, width = Grow.Std) {
            y.set(it)
            props.onChangedByEditor.forEach { it() }
        }

        Text("Z") {
            modifier
                .margin(start = sizes.gap)
                .alignY(AlignmentY.Center)
                .font(boldTxt)
                .textColor(MdColor.BLUE tone 300)
        }
        doubleTextField(z.use(), precision, width = Grow.Std) {
            z.set(it)
            props.onChangedByEditor.forEach { it() }
        }
    }
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

    fun setPosition(position: Vec3d) {
        px.set(position.x)
        py.set(position.y)
        pz.set(position.z)
    }

    fun setRotation(rotation: Vec3d) {
        rx.set(rotation.x)
        ry.set(rotation.y)
        rz.set(rotation.z)
    }

    fun setScale(scale: Vec3d) {
        sx.set(scale.x)
        sy.set(scale.y)
        sz.set(scale.z)
    }
}
