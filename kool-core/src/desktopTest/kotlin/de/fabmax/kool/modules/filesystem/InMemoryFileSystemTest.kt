package de.fabmax.kool.modules.filesystem

import kotlin.test.Test

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
}