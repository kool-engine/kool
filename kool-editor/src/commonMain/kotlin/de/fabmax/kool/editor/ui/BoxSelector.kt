package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.components.localToGlobalF
import de.fabmax.kool.editor.components.toGlobalCoords
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.SpatialTree
import de.fabmax.kool.math.spatial.SpatialTreeTraverser
import de.fabmax.kool.math.spatial.Triangle
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Viewport
import de.fabmax.kool.util.logE
import kotlin.math.*

class BoxSelector : Composable {

    val isBoxSelect = mutableStateOf(false)

    private val startSelection = mutableSetOf<GameEntity>()
    private val selectionCache = mutableMapOf<GameEntity, MinIncludeRect>()

    override fun UiScope.compose() {
        var boxSelectStart by remember(Vec2f.ZERO)
        var boxSelectCursor by remember(Vec2f.ZERO)

        modifier
            .onPointer {
                if (it.pointer.isLeftButtonDown || it.pointer.isLeftButtonEvent) {
                    // explicitly consume pointer, to avoid camera movement during box select
                    it.pointer.consume()
                }
            }
            .onDragStart {
                if (it.pointer.isLeftButtonDown) {
                    isBoxSelect.set(true)
                    boxSelectStart = it.screenPosition
                    boxSelectCursor = it.screenPosition
                    startSelection.clear()
                    if (KeyboardInput.isShiftDown || KeyboardInput.isAltDown) {
                        startSelection += KoolEditor.instance.selectionOverlay.selection
                    }
                    // explicitly consume pointer, to avoid camera movement during box select
                    it.pointer.consume()
                } else {
                    it.reject()
                }
            }
            .onDrag {
                if (isBoxSelect.value) {
                    boxSelectCursor = it.screenPosition
                    // explicitly consume pointer, to avoid camera movement during box select
                    it.pointer.consume()
                } else {
                    it.reject()
                }
            }
            .onDragEnd {
                if (isBoxSelect.value) {
                    isBoxSelect.set(false)
                    selectionCache.clear()
                    // explicitly consume pointer, to avoid camera movement during box select
                    it.pointer.consume()
                } else {
                    it.reject()
                }
            }
            .onClick { ev ->
                KoolEditor.instance.overlayScene.doPicking(ev.pointer)
            }


        if (isBoxSelect.use()) {
            val startPos = uiNode.toLocal(boxSelectStart)
            val cursorPos = uiNode.toLocal(boxSelectCursor)
            val min = Vec2f(min(startPos.x, cursorPos.x), min(startPos.y, cursorPos.y))
            val max = Vec2f(max(startPos.x, cursorPos.x), max(startPos.y, cursorPos.y))

            updateSelection(min, max)

            val x = Dp.fromPx(min.x)
            val y = Dp.fromPx(min.y)
            val w = Dp.fromPx(max.x - min.x)
            val h = Dp.fromPx(max.y - min.y)

            Box {
                modifier
                    .margin(start = x, top = y)
                    .size(w, h)
                    .background(RoundRectBackground(colors.secondaryVariantAlpha(0.3f), sizes.smallGap))
                    .border(RoundRectBorder(colors.primary, sizes.smallGap, sizes.borderWidth * 2))
            }
        }
    }

    private fun updateSelection(min: Vec2f, max: Vec2f) {
        val editor = KoolEditor.instance
        val scene = editor.activeScene.value ?: return
        val editorCam = editor.overlayScene.camera
        val viewport = editor.overlayScene.mainRenderPass.viewport

        val camHelper = if (editorCam is PerspectiveCamera) {
            PerspectiveBoxIntersectHelper(editorCam, min, max, viewport)
        } else {
            TODO()
        }

        val boxSelection = selectionCache.values
            .asSequence()
            .filter { it.isIncludedIn(min, max) }
            .map { it.entity }
            .toMutableSet()

        var testCount = 0
        scene.orderedEntities
            .asSequence()
            .filter { it !in boxSelection && it.isSceneChild && it.isVisible }
            .forEach {
                if (camHelper.testSceneNode(it)) {
                    testCount++
                    boxSelection += it
                }
            }

        boxSelection.forEach { entity ->
            val minRect = selectionCache.getOrPut(entity) { MinIncludeRect(min, max, entity) }
            if (!minRect.isIncludedIn(min, max)) {
                selectionCache[entity] = MinIncludeRect(min, max, entity)
            }
        }

        // remove group nodes where not all children are selected
        boxSelection -= boxSelection.filter { !it.hasComponent<MeshComponent>() && it.children.any { child -> child !in boxSelection } }.toSet()
        // remove nodes from selection whose parent is in selection as well
        boxSelection -= boxSelection.filter { it.parent in boxSelection }.toSet()

        val newSelection = if (KeyboardInput.isAltDown) {
            startSelection - boxSelection
        } else {
            startSelection + boxSelection
        }
        editor.selectionOverlay.setSelection(newSelection)
    }

    private data class MinIncludeRect(val min: Vec2f, val max: Vec2f, val entity: GameEntity) {
        fun isIncludedIn(selMin: Vec2f, selMax: Vec2f): Boolean {
            return min.x >= selMin.x && min.y >= selMin.y && max.x <= selMax.x && max.y <= selMax.y
        }
    }

    private abstract class BoxIntersectHelper(val cam: Camera, boxMin: Vec2f, boxMax: Vec2f, viewport: Viewport) {
        val boxMinX = (boxMin.x / viewport.width) * 2f - 1f
        val boxMaxX = (boxMax.x / viewport.width) * 2f - 1f
        val boxMaxY = (1f - boxMin.y / viewport.height) * 2f - 1f
        val boxMinY = (1f - boxMax.y / viewport.height) * 2f - 1f

        private val triTrav = TriTraverser()

        abstract fun testBoundingSphere(globalCenter: Vec3f, globalRadius: Float): Boolean

        fun testSceneNode(gameEntity: GameEntity): Boolean {
            if (!gameEntity.testBounds()) {
                return false
            }
            val drawNode = gameEntity.getComponent<MeshComponent>()?.sceneNode
            return when (drawNode) {
                is Mesh -> gameEntity.testMesh(drawNode)
                is Model -> drawNode.meshes.values.any { gameEntity.testMesh(it) }
                else -> true
            }
        }

        private fun GameEntity.testBounds(): Boolean {
            val mesh = getComponent<MeshComponent>()?.sceneNode
                ?: return testBoundingSphere(localToGlobalF.getTranslation(), 1f)

            val globalCenter = toGlobalCoords(MutableVec3f(mesh.bounds.center))
            val globalRadius = toGlobalCoords(MutableVec3f(mesh.bounds.size), 0f).length() * 0.5f
            return testBoundingSphere(globalCenter, globalRadius)
        }

        private fun GameEntity.testMesh(mesh: Mesh): Boolean {
            return when (val meshTest = mesh.rayTest) {
                is MeshRayTest.TriangleGeometry -> testTriMesh(mesh, meshTest)
                is MeshRayTest.LineGeometry -> testLineMesh()
                else -> false
            }
        }

        private fun GameEntity.testTriMesh(mesh: Mesh, meshTest: MeshRayTest.TriangleGeometry): Boolean {
            meshTest.triangleTree?.let { triTree ->
                triTrav.setup(this, mesh)
                triTrav.traverse(triTree)
                return triTrav.isIntersect
            }
            return false
        }

        private fun testLineMesh(): Boolean {
            logE { "line mesh selection is not yet implemented" }
            return false
        }

        private inner class TriTraverser : SpatialTreeTraverser<Triangle>() {
            private val tmpP = MutableVec3f()
            private val tmpA = MutableVec3f()
            private val tmpB = MutableVec3f()
            private val tmpC = MutableVec3f()
            private val toGlobal = MutableMat4f()

            private lateinit var gameEntity: GameEntity
            private lateinit var mesh: Mesh

            var isIntersect = false

            fun setup(gameEntity: GameEntity, mesh: Mesh) {
                this.gameEntity = gameEntity
                this.mesh = mesh
                toGlobal.set(gameEntity.localToGlobalF).mul(mesh.modelMatF)
                isIntersect = false
            }

            override fun traverseChildren(tree: SpatialTree<Triangle>, node: SpatialTree<Triangle>.Node) {
                for (child in node.children) {
                    toGlobal.transform(child.bounds.center.toMutableVec3f(tmpA))
                    toGlobal.transform(child.bounds.max.toMutableVec3f(tmpB))
                    val r = tmpA.distance(tmpB)
                    if (testBoundingSphere(tmpA, r)) {
                        traverseNode(tree, child)
                    }
                    if (isIntersect) {
                        break
                    }
                }
            }

            override fun traverseLeaf(tree: SpatialTree<Triangle>, leaf: SpatialTree<Triangle>.Node) {
                for (i in leaf.nodeRange) {
                    val tri = leaf.itemsUnbounded[i]
                    cam.project(toGlobal.transform(tmpP.set(tri.pt0)), tmpA)
                    cam.project(toGlobal.transform(tmpP.set(tri.pt1)), tmpB)
                    cam.project(toGlobal.transform(tmpP.set(tri.pt2)), tmpC)
                    if (tmpA.z < 0f || tmpB.z < 0f || tmpC.z < 0f) {
                        continue
                    }

                    isIntersect = testTriEdgesVsBoxEdges()
                    isIntersect = isIntersect || testBoxPointInsideAbc(boxMinX, boxMinY)
                    isIntersect = isIntersect || testBoxPointInsideAbc(boxMinX, boxMaxY)
                    isIntersect = isIntersect || testBoxPointInsideAbc(boxMaxX, boxMinY)
                    isIntersect = isIntersect || testBoxPointInsideAbc(boxMaxX, boxMaxY)
                    if (isIntersect) break
                }
            }

            // from: https://stackoverflow.com/questions/2049582/how-to-determine-if-a-point-is-in-a-2d-triangle
            private fun edgeSign(boxPtX: Float, boxPtY: Float, edgeA: Vec3f, edgeB: Vec3f): Float {
                return (boxPtX - edgeB.x) * (edgeA.y - edgeB.y) - (edgeA.x - edgeB.x) * (boxPtY - edgeB.y)
            }

            private fun testBoxPointInsideAbc(boxPtX: Float, boxPtY: Float): Boolean {
                val d1 = edgeSign(boxPtX, boxPtY, tmpA, tmpB)
                val d2 = edgeSign(boxPtX, boxPtY, tmpB, tmpC)
                val d3 = edgeSign(boxPtX, boxPtY, tmpC, tmpA)

                val hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0)
                val hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0)
                return !(hasNeg && hasPos)
            }

            private fun testTriEdgesVsBoxEdges(): Boolean {
                val cA = computeOutCode(tmpA)
                val cB = computeOutCode(tmpB)
                val cC = computeOutCode(tmpC)
                return testEdgeVsBox(cA, cB) || testEdgeVsBox(cB, cC) || testEdgeVsBox(cC, cA)
            }

            private fun testEdgeVsBox(c1: Int, c2: Int): Boolean {
                val ored = c1 or c2
                return if (c1 == INSIDE || c2 == INSIDE || ored == LEFT_OR_RIGHT || ored == TOP_OR_BOTTOM) {
                    // guaranteed intersection
                    true
                } else if (c1 and c2 != 0) {
                    // guaranteed outside
                    false
                } else {
                    // todo: test edge vs. individual box edges
                    false
                }
            }

            private fun computeOutCode(triPt: Vec3f): Int {
                var code = INSIDE
                if (triPt.x < boxMinX) {
                    code = code or LEFT
                } else if (triPt.x > boxMaxX) {
                    code = code or RIGHT
                }
                if (triPt.y < boxMinY) {
                    code = code or BOTTOM
                } else if (triPt.y > boxMaxY) {
                    code = code or TOP
                }
                return code
            }
        }

        companion object {
            const val INSIDE = 0
            const val LEFT = 1
            const val RIGHT = 2
            const val BOTTOM = 4
            const val TOP = 8

            const val LEFT_OR_RIGHT = LEFT or RIGHT
            const val TOP_OR_BOTTOM = TOP or BOTTOM
        }
    }

    private class PerspectiveBoxIntersectHelper(cam: PerspectiveCamera, boxMin: Vec2f, boxMax: Vec2f, viewport: Viewport) :
        BoxIntersectHelper(cam, boxMin, boxMax, viewport)
    {
        private var sphereFacX = 1f
        private var sphereFacY = 1f
        private var tangX = 1f
        private var tangY = 1f

        private val tmpNodeCenter = MutableVec3f()

        init {
            val angY = cam.fovY.rad / 2f
            sphereFacY = 1f / cos(angY)
            tangY = tan(angY)

            val angX = atan(tangY * cam.aspectRatio)
            sphereFacX = 1f / cos(angX)
            tangX = tan(angX)
        }

        override fun testBoundingSphere(
            globalCenter: Vec3f,
            globalRadius: Float
        ): Boolean {
            tmpNodeCenter.set(globalCenter)
            tmpNodeCenter.subtract(cam.globalPos)

            var z = tmpNodeCenter.dot(cam.globalLookDir)
            if (z > cam.clipFar + globalRadius || z < cam.clipNear - globalRadius) {
                // node's bounding sphere is either in front of near or behind far plane
                return false
            }

            val y = tmpNodeCenter.dot(cam.globalUp)
            var d = globalRadius * sphereFacY
            z *= tangY

            if ((y + d) / z < boxMinY || (y - d) / z > boxMaxY) {
                return false
            }

            val x = tmpNodeCenter.dot(cam.globalRight)
            d = globalRadius * sphereFacX
            z *= cam.aspectRatio

            if ((x + d) / z < boxMinX || (x - d) / z > boxMaxX) {
                return false
            }
            return true
        }
    }
}