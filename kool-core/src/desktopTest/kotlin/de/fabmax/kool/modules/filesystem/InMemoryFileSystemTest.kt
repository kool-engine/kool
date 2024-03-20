package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.util.Uint8Buffer
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryFileSystemTest : FileSystemTest() {
    @Test
    fun createFileSystemTest() {
        InMemoryFileSystem().createFileSystemTest()
    }

    @Test
    fun deleteTest() {
        InMemoryFileSystem().deleteTest()
    }

    @Test
    fun moveTest() {
        InMemoryFileSystem().moveTest()
    }

    @Test(expected = IllegalStateException::class)
    fun noDuplicateFileTest() {
        InMemoryFileSystem().noDuplicateFileTest()
    }

    @Test(expected = IllegalStateException::class)
    fun noDuplicateDirTest() {
        InMemoryFileSystem().noDuplicateDirTest()
    }

    @Test
    fun zipTest() {
        runBlocking {
            val fs = InMemoryFileSystem()
            fs.createFile("a", Uint8Buffer(100))
            fs.createFile("b", Uint8Buffer(100))
            fs.createDirectory("d")
            fs.createFile("d/a", Uint8Buffer(100))
            fs.createFile("d/b", Uint8Buffer(100))

            val zipped = fs.toZip()
            val zipFs = zipFileSystem(zipped)
            zipFs.assertHasFile("a")
            zipFs.assertHasFile("b")
            zipFs.assertHasDirectory("d")
            zipFs.assertHasFile("d/a")
            zipFs.assertHasFile("d/b")

            assertEquals(6, zipFs.listAll().size)
            assertEquals(3, zipFs.root.list().size)
        }
    }
}