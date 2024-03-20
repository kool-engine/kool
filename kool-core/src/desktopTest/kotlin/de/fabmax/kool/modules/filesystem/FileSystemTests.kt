package de.fabmax.kool.modules.filesystem

import de.fabmax.kool.mock.Mock
import de.fabmax.kool.util.Uint8Buffer
import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

open class FileSystemTest {

    init {
        Mock.testCtx
    }

    fun WritableFileSystem.createFileSystemTest() {
        runBlocking {
            createFile("/someFile", Uint8Buffer(0))
            assertHasFile("/someFile")
            createFile("anotherFile", Uint8Buffer(0))
            assertHasFile("/anotherFile")

            createDirectory("someDir")
            assertHasDirectory("/someDir")
            createDirectory("/anotherDir")
            assertHasDirectory("/anotherDir")

            createFile("/someDir/someFileInDir", Uint8Buffer(0))
            assertHasFile("/someDir/someFileInDir")

            assertEquals(6, listAll().size)
            assertEquals(4, root.list().size)
        }
    }

    fun WritableFileSystem.deleteTest() {
        runBlocking {
            val file = createFile("/someFile", Uint8Buffer(0))
            file.delete()
            assertNotHasFile("/someFile")

            val dir = createDirectory("someDir")
            createFile("/someDir/someFileInDir", Uint8Buffer(0))
            dir.delete()
            assertNotHasDirectory("/someDir")
            assertNotHasFile("/someDir/someFileInDir")

            assertEquals(1, listAll().size)
            assertEquals(0, root.list().size)
        }
    }

    fun WritableFileSystem.moveTest() {
        runBlocking {
            createFile("/someFile", Uint8Buffer(0))
            move("/someFile", "/movedFile")
            assertNotHasFile("/someFile")
            assertHasFile("/movedFile")

            createDirectory("/someDir")
            createFile("/someDir/someFileInDir", Uint8Buffer(0))
            move("/someDir", "/movedDir")
            assertNotHasDirectory("/someDir")
            assertNotHasFile("/someDir/someFileInDir")
            assertHasDirectory("/movedDir")
            assertHasFile("/movedDir/someFileInDir")

            assertEquals(4, listAll().size)
            assertEquals(2, root.list().size)

            print()
        }
    }

    fun WritableFileSystem.noDuplicateFileTest() {
        runBlocking {
            createFile("/someFile", Uint8Buffer(0))
            createFile("/someFile", Uint8Buffer(0))
        }
    }

    fun WritableFileSystem.noDuplicateDirTest() {
        runBlocking {
            createDirectory("/someDir")
            createDirectory("/someDir/")
        }
    }

    fun FileSystem.assertHasFile(path: String) {
        assertNotNull(getFileOrNull(path))
    }

    fun FileSystem.assertHasDirectory(path: String) {
        assertNotNull(getDirectoryOrNull(path))
    }

    fun FileSystem.assertNotHasFile(path: String) {
        assertNull(getFileOrNull(path))
    }

    fun FileSystem.assertNotHasDirectory(path: String) {
        assertNull(getDirectoryOrNull(path))
    }

    val printWatcher = object : FileSystemWatcher {
        override fun onFileCreated(file: FileSystemFile) {
            println("file created: ${file.path}")
        }

        override fun onFileDeleted(file: FileSystemFile) {
            println("file deleted: ${file.path}")
        }

        override fun onFileChanged(file: FileSystemFile) {
            println("file changed: ${file.path}")
        }

        override fun onDirectoryCreated(directory: FileSystemDirectory) {
            println("directory created: ${directory.path}")
        }

        override fun onDirectoryDeleted(directory: FileSystemDirectory) {
            println("directory deleted: ${directory.path}")
        }
    }
}