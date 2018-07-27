package de.fabmax.kool.modules.globe

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.logE
import kotlin.math.max
import kotlin.math.min

class TileManager(val globe: Globe) {

    var maxTiles = 300

    var minZoomLvl = 3
    var maxZoomLvl = 19

    private val tiles = mutableMapOf<Long, TileMesh>()
    private val loadingTiles = mutableSetOf<Long>()
    private val removingTiles = mutableMapOf<Long, TileMesh>()

    private var center = TileName(0, 0, 1)

    fun onTileLoaded(tile: TileMesh) {
        loadingTiles -= tile.key
        removeObsoleteTiles(tile)
    }

    fun onTileDeleted(tile: TileMesh) {
        if (!tile.isRemovable) {
            logE { "removed non removable tile: ${tile.tileName}" }
        }

        loadingTiles -= tile.key
        removingTiles -= tile.key
        tiles -= tile.key
    }

    fun onPreRender(ctx: KoolContext) {
        if (loadingTiles.isEmpty() && !removingTiles.isEmpty()) {
            //logD { "all loaded" }
            // all loaded, remove obsolete
            removingTiles.values.forEach { globe.removeTile(it) }
            removingTiles.clear()

            tiles.values.forEach { it.isVisible = true }
        }
    }

    fun updateCenter(newCenter: TileName, isMoving: Boolean, ctx: KoolContext) {
        if (newCenter != center && (tiles.size < maxTiles || !isMoving)) {
            center = newCenter
            updateTiles(ctx)
        }
    }

    fun getCenterTile(): TileMesh? = getTile(center)

    fun getTile(tileName: TileName): TileMesh? = getTile(tileName.fusedKey)

    fun getTile(key: Long): TileMesh? = tiles[key]

    private fun updateTiles(ctx: KoolContext) {
        // compute list of visible tiles for current center tile
        val newTiles = computeNeededTileList()

        // remove all current tiles not present in new tile set
        removingTiles.putAll(tiles)
        for (i in newTiles.indices) {
            val key = newTiles[i]
            removingTiles -= key

            val existing = tiles[key]
            if (existing == null) {
                // add new tile
                val tile = TileMesh(globe, TileName.fromFusedKey(key), ctx)
                loadingTiles += key
                tiles[key] = tile
                globe.addTile(tile)
            } else {
                existing.isRemovable = false
            }
        }
        removingTiles.values.forEach { it ->
            it.isRemovable = true
        }

        // if there are too many tiles force-remove some
        val forceRemoveThresh = (maxTiles * 1.5).toInt()
        if (tiles.size > forceRemoveThresh) {
            //logD { "force removing tiles" }
            // queue is getting too large, remove stuff even though it might be visible
            val rmQueue = mutableListOf<TileMesh>().apply { addAll(removingTiles.values) }
            rmQueue.sortBy { m ->
                if (!m.isLoaded || !m.isCurrentlyVisible) {
                    -Double.MAX_VALUE
                } else {
                    val dx = m.tileName.lonCenter - center.lonCenter
                    val dy = m.tileName.latCenter - center.latCenter
                    -(dx*dx + dy*dy)
                }
            }
            for (i in 0..(tiles.size - forceRemoveThresh)) {
                globe.removeTile(rmQueue[i])
            }
        }
    }

    private fun computeNeededTileList(): List<Long> {
        val tileList = mutableListOf<Long>()

        val rng = 5
        var zoom = center.zoom
        var xStart = (center.x - rng + 1) and 1.inv()
        var xEnd = ((center.x + rng + 1) and 1.inv()) - 1
        var yStart = (center.y - rng + 1) and 1.inv()
        var yEnd = ((center.y + rng + 1) and 1.inv()) - 1

        addTilesWrappingX(xStart, xEnd, yStart, yEnd, zoom, tileList)

        for (i in 1..4) {
            zoom--
            if (zoom >= minZoomLvl) {
                val xStShf = xStart shr 1
                val xEdShf = (xEnd + 1) shr 1
                val yStShf = yStart shr 1
                val yEdShf = (yEnd + 1) shr 1

                xStart = (xStShf - 1) and 1.inv()
                xEnd = (xEdShf and 1.inv()) + 1
                yStart = (yStShf - 1) and 1.inv()
                yEnd = (yEdShf and 1.inv()) + 1

                addTilesWrappingX(xStart, xStShf-1, yStart, yEnd, zoom, tileList)
                addTilesWrappingX(xEdShf, xEnd, yStart, yEnd, zoom, tileList)

                addTilesWrappingX(xStShf, xEdShf-1, yStart, yStShf-1, zoom, tileList)
                addTilesWrappingX(xStShf, xEdShf-1, yEdShf, yEnd, zoom, tileList)
            } else {
                break
            }
        }
        return tileList
    }

    private fun addTilesWrappingX(xStart: Int, xEnd: Int, yStart: Int, yEnd: Int, zoom: Int, tiles: MutableList<Long>) {
        val size = 1 shl zoom
        val ys = max(0, yStart)
        val ye = min(size - 1, yEnd)

        addTiles(max(0, xStart)..min(size - 1, xEnd), ys..ye, zoom, tiles)
        if (xStart < 0 && xEnd < size - 1) {
            // wrap around 180° long
            addTiles(max(size + xStart, xEnd) until size, ys..ye, zoom, tiles)
        } else if (xStart > 0 && xEnd > size - 1) {
            // wrap around 180° long
            addTiles(0..min(xStart, xEnd - (size - 1)), ys..ye, zoom, tiles)
        }
    }

    private fun addTiles(xRng: IntRange, yRng: IntRange, zoom: Int, tiles: MutableList<Long>) {
        if (xRng.last - xRng.first > 2 && yRng.last - yRng.first > 2) {
            addTilesCircular(xRng, yRng, zoom, tiles)
        } else {
            addTilesRectRange(xRng, yRng, zoom, tiles)
        }
    }

    private fun addTilesRectRange(xRng: IntRange, yRng: IntRange, zoom: Int, tiles: MutableList<Long>) {
        for (x in xRng) {
            for (y in yRng) {
                addTile(x, y, zoom, xRng, yRng, tiles)
            }
        }
    }

    private fun addTilesCircular(xRng: IntRange, yRng: IntRange, zoom: Int, tiles: MutableList<Long>) {
        val cx = xRng.first + (xRng.last - xRng.first) / 2
        val cy = yRng.first + (yRng.last - yRng.first) / 2
        val r = max(max(cx - xRng.first, xRng.last - cx), max(cy - yRng.first, yRng.last - cy))

        for (i in 0..r) {
            for (x in cx - i..cx + i) {
                addTile(x, cy - i, zoom, xRng, yRng, tiles)
                if (i > 0) {
                    addTile(x, cy + i, zoom, xRng, yRng, tiles)
                }
            }
            if (i > 0) {
                for (y in cy - i + 1 until cy + i) {
                    addTile(cx - i, y, zoom, xRng, yRng, tiles)
                    addTile(cx + i, y, zoom, xRng, yRng, tiles)
                }
            }
        }
    }

    private fun addTile(x: Int, y: Int, zoom: Int, xRng: IntRange, yRng: IntRange, tiles: MutableList<Long>) {
        if (x in xRng && y in yRng) {
            tiles += TileName.fuesdKey(x, y, zoom)
        }
    }

    private fun removeObsoleteTiles(tile: TileMesh) {
        var makeVisible = true
        val it = removingTiles.values.iterator()
        for (mesh in it) {
            if (!mesh.isRemovable) {
                logE { "mesh is not removable!" }
            }

            if (mesh.tileName.isSubTileOf(tile.tileName)) {
                // remove tiles below tileName
                globe.removeTile(mesh)
                it.remove()


            } else if (tile.tileName.isSubTileOf(mesh.tileName)) {
                // remove tiles above tileName if all sub-tiles are loaded
                val z = mesh.tileName.zoom + 1
                val subKey1 = TileName.fuesdKey(mesh.tileName.x * 2, mesh.tileName.y * 2, z)
                val subKey2 = TileName.fuesdKey(mesh.tileName.x * 2, mesh.tileName.y * 2 + 1, z)
                val subKey3 = TileName.fuesdKey(mesh.tileName.x * 2 + 1, mesh.tileName.y * 2, z)
                val subKey4 = TileName.fuesdKey(mesh.tileName.x * 2 + 1, mesh.tileName.y * 2 + 1, z)
                if (tiles[subKey1]?.isLoaded == true && tiles[subKey2]?.isLoaded == true &&
                        tiles[subKey3]?.isLoaded == true && tiles[subKey4]?.isLoaded == true) {
                    globe.removeTile(mesh)
                    it.remove()
                    tiles[subKey1]!!.isVisible = true
                    tiles[subKey2]!!.isVisible = true
                    tiles[subKey3]!!.isVisible = true
                    tiles[subKey4]!!.isVisible = true

                } else {
                    makeVisible = false
                }
            }
        }
        tile.isVisible = makeVisible
    }
}