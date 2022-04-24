package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.scene.ui.ComponentUi
import de.fabmax.kool.scene.ui.UiComponent
import de.fabmax.kool.scene.ui.UiShader
import de.fabmax.kool.util.Color
import kotlin.math.cos

open class BarUi(private val component: UiComponent) : ComponentUi {
    protected val mesh = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS)) { }
    protected val meshBuilder = MeshBuilder(mesh.geometry)

    protected val track = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS)) { }
    protected val trackBuilder = MeshBuilder(track.geometry)

    protected val tiltAng = 15f
    protected val tilt = cos((90f - tiltAng).toRad())
    protected val centerTrack = mutableListOf<Vec2f>()
    protected val centerTrackLefts = mutableListOf<Vec2f>()
    protected val centerTrackRelPos = mutableListOf<Float>()
    protected var trackScale = 1f

    protected var numIntervals = 20

    override fun updateComponentAlpha() {
        (mesh.shader as UiShader).apply { alpha = component.alpha }
        (track.shader as UiShader).apply { alpha = component.alpha }
    }

    override fun createUi(ctx: KoolContext) {
        mesh.shader = UiShader()
        component += mesh

        track.shader = UiShader()
        component += track
    }

    override fun updateUi(ctx: KoolContext) {
        component.setupBuilder(meshBuilder)

        meshBuilder.drawBorder(2f)
        meshBuilder.drawIntervals(2f)
    }

    protected open fun MeshBuilder.drawBorder(w: Float) {
        meshBuilder.color = Color.WHITE
        withTransform {
            val s = trackScale
            translate(0f, 0f, 10f)

            // left track border
            for (i in 0 until centerTrack.lastIndex) {
                line((centerTrack[i].x + centerTrackLefts[i].x) * s, (centerTrack[i].y + centerTrackLefts[i].y) * s,
                        (centerTrack[i+1].x + centerTrackLefts[i+1].x) * s, (centerTrack[i+1].y + centerTrackLefts[i+1].y) * s, w)
            }
            // right track border
            for (i in 0 until centerTrack.lastIndex) {
                line((centerTrack[i].x - centerTrackLefts[i].x) * s, (centerTrack[i].y - centerTrackLefts[i].y) * s,
                        (centerTrack[i+1].x - centerTrackLefts[i+1].x) * s, (centerTrack[i+1].y - centerTrackLefts[i+1].y) * s, w)
            }
            line((centerTrack[0].x + centerTrackLefts[0].x) * s, (centerTrack[0].y + centerTrackLefts[0].y) * s,
                    (centerTrack[0].x - centerTrackLefts[0].x) * s, (centerTrack[0].y - centerTrackLefts[0].y) * s, w)
            val n = centerTrack.lastIndex
            line((centerTrack[n].x + centerTrackLefts[n].x) * s, (centerTrack[n].y + centerTrackLefts[n].y) * s,
                    (centerTrack[n].x - centerTrackLefts[n].x) * s, (centerTrack[n].y - centerTrackLefts[n].y) * s, w)
        }
    }

    protected open fun MeshBuilder.drawIntervals(w: Float) {
        meshBuilder.color = Color.WHITE.withAlpha(0.25f)
        val pos = MutableVec2f()
        val lt = MutableVec2f()
        for (i in 1 until numIntervals) {
            getTrackPosAt(i.toFloat() / numIntervals, pos, lt)
            line(pos.x + lt.x, pos.y + lt.y, pos.x - lt.x, pos.y - lt.y, w)
        }
    }

    protected open fun MeshBuilder.fillTrack(from: Float, to: Float, colorFun: (Float) -> Color) {
        val pos = MutableVec2f()
        val lt = MutableVec2f()
        getTrackPosAt(from, pos, lt)
        val pos3 = MutableVec3f()

        color = colorFun(from)
        vertex(pos3.set(pos.x - lt.x, pos.y - lt.y, 0f), Vec3f.Z_AXIS)
        vertex(pos3.set(pos.x + lt.x, pos.y + lt.y, 0f), Vec3f.Z_AXIS)

        var i = 1
        while (from > centerTrackRelPos[i] && i < centerTrackRelPos.lastIndex) { i++ }
        while (to > centerTrackRelPos[i] && i < centerTrackRelPos.lastIndex) {
            pos.set(centerTrack[i]).scale(trackScale)
            lt.set(centerTrackLefts[i]).scale(trackScale)

            color = colorFun(centerTrackRelPos[i])
            vertex(pos3.set(pos.x - lt.x, pos.y - lt.y, 0f), Vec3f.Z_AXIS)
            val vi = vertex(pos3.set(pos.x + lt.x, pos.y + lt.y, 0f), Vec3f.Z_AXIS)
            geometry.addTriIndices(vi-3, vi-2, vi-1)
            geometry.addTriIndices(vi-2, vi-1, vi)
            i++
        }

        getTrackPosAt(to, pos, lt)
        color = colorFun(to)
        vertex(pos3.set(pos.x - lt.x, pos.y - lt.y, 0f), Vec3f.Z_AXIS)
        val vi = vertex(pos3.set(pos.x + lt.x, pos.y + lt.y, 0f), Vec3f.Z_AXIS)

        geometry.addTriIndices(vi-3, vi-2, vi-1)
        geometry.addTriIndices(vi-2, vi-1, vi)
    }

    protected fun getTrackPosAt(relPos: Float, resultPos: MutableVec2f, resultLeft: MutableVec2f) {
        var i = 1
        while (relPos > centerTrackRelPos[i] && i < centerTrackRelPos.lastIndex) {
            i++
        }
        val dp = relPos - centerTrackRelPos[i-1]
        val dt = centerTrackRelPos[i] - centerTrackRelPos[i-1]
        val w1 = (dp / dt).clamp(0f, 1f)
        val w0 = 1f - w1

        resultPos.x = centerTrack[i-1].x * w0 + centerTrack[i].x * w1
        resultPos.y = centerTrack[i-1].y * w0 + centerTrack[i].y * w1
        resultLeft.x = centerTrackLefts[i-1].x * w0 + centerTrackLefts[i].x * w1
        resultLeft.y = centerTrackLefts[i-1].y * w0 + centerTrackLefts[i].y * w1

        resultPos.scale(trackScale)
        resultLeft.scale(trackScale)
    }
}