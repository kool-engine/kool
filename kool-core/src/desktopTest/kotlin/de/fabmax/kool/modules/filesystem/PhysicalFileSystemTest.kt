package de.fabmax.kool.modules.filesystem

import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class PhysicalFileSystemTest : FileSystemTest() {
    @BeforeTest
    fun createTestDir() {
        FS_PATH.createDirectories()
    }

    @OptIn(ExperimentalPathApi::class)
    @AfterTest
    fun cleanupTestDir() {
        FS_PATH.deleteRecursively()
    }

    @Test
    fun createFileSystemTest() {
        PhysicalFileSystem(FS_PATH).createFileSystemTest()
    }

    @Test
    fun deleteTest() {
        PhysicalFileSystem(FS_PATH).deleteTest()
    }

    @Test
    fun moveTest() {
        PhysicalFileSystem(FS_PATH).moveTest()
    }

    @Test(expected = IllegalStateException::class)
    fun noDuplicateFileTest() {
        PhysicalFileSystem(FS_PATH).noDuplicateFileTest()
    }

    @Test(expected = IllegalStateException::class)
    fun noDuplicateDirTest() {
        PhysicalFileSystem(FS_PATH).noDuplicateDirTest()
    }

    companion object {
        val FS_PATH = Path("fsTest")
    }
}