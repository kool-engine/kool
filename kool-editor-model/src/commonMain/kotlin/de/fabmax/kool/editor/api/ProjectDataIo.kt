package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.data.*
import de.fabmax.kool.modules.filesystem.*
import de.fabmax.kool.util.decodeToString
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.toBuffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ProjectReader(private val srcDir: FileSystemDirectory) {

    var parserErrors = 0
        private set

    @OptIn(ExperimentalSerializationApi::class)
    private val codec: Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        prettyPrintIndent = "  "
    }

    suspend fun loadTree(): ProjectData? {
        val projMeta: ProjectMeta = srcDir.getFileOrNull("project.json")?.parseFile<ProjectMeta>() ?: return null
        val materials = srcDir.getDirectoryOrNull("materials") ?.let { loadSceneEntities(it) } ?: emptyList()
        val scenes = buildList {
            val scenesDir = srcDir.getDirectoryOrNull("scenes")
            if (scenesDir != null) {
                val metas = scenesDir.listFiles()
                    .filter { it.name.endsWith("meta.json") }
                    .mapNotNull { it.parseFile<SceneMeta>() }
                    .map { meta -> meta to scenesDir.listDirectories().find { it.name.endsWith("${meta.rootId.value}") } }

                metas.forEach { (meta, dir) ->
                    val sceneEntities = dir?.let { loadSceneEntities(it) } ?: emptyList()
                    add(SceneData(meta, sceneEntities.toMutableList()))
                }
            }
        }
        return ProjectData(projMeta, scenes.toMutableList(), materials.toMutableList())
    }

    private suspend fun loadSceneEntities(dir: FileSystemDirectory): List<GameEntityData> {
        return dir.listRecursively()
            .filter { it.name.endsWith(".json", ignoreCase = true) }
            .filterIsInstance<FileSystemFile>()
            .mapNotNull { it.parseFile<GameEntityData>() }
    }

    private suspend inline fun <reified T> FileSystemFile?.parseFile(): T? {
        if (this == null) return null
        return try {
            codec.decodeFromString<T>(readText())
        } catch (e: Exception) {
            logE { "Failed parsing ${path}: $e" }
            parserErrors++
            null
        }
    }

    companion object {
        suspend fun loadProjectData(srcDir: FileSystemDirectory): ProjectData? {
            return ProjectReader(srcDir).loadTree()
        }
    }
}

class ProjectWriter private constructor(
    private val projData: ProjectData,
    private val targetDir: WritableFileSystemDirectory
) {
    @OptIn(ExperimentalSerializationApi::class)
    private val codec: Json = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
    }

    private val projFiles = mutableSetOf<WritableFileSystemItem>()

    private suspend fun saveTree() {
        targetDir.createProjFile("project.json", codec.encodeToString(projData.meta))

        val materialDir = targetDir.createProjDir("materials")
        projData.materials.toHierarchy().forEach { it.saveEntities(materialDir) }

        val scenesDir = targetDir.createProjDir("scenes")
        projData.scenes.forEach { scene ->
            val sceneName = "${scene.meta.name.fileNameSafe()}_${scene.meta.rootId.value}"
            scenesDir.createProjFile("$sceneName-meta.json", codec.encodeToString(scene.meta))
            val sceneDir = scenesDir.createProjDir(sceneName)
            scene.entities.toHierarchy().forEach { it.saveEntities(sceneDir) }
        }
        deleteOldFiles()
    }

    private fun deleteOldFiles() {
        val oldFiles = targetDir.listRecursively().filterIsInstance<WritableFileSystemItem>().toSet() - projFiles
        oldFiles.forEach { it.delete() }
    }

    private suspend fun GameEntityDataHierarchy.saveEntities(targetDir: WritableFileSystemDirectory) {
        val fileName = entityData.fileName
        val json = codec.encodeToString(entityData)
        val file = targetDir.getOrCreateFile(fileName)
        val existing = targetDir.getFile(fileName).read().decodeToString()
        if (existing != json) {
            file.write(json.encodeToByteArray().toBuffer())
        }
        projFiles += file

        if (children.isNotEmpty()) {
            val subDir = targetDir.getOrCreateDirectory(entityData.dirName)
            projFiles += subDir
            children.forEach { it.saveEntities(subDir) }
        }
    }

    private fun WritableFileSystemDirectory.createProjDir(name: String): WritableFileSystemDirectory {
        return getOrCreateDirectory(name).also { projFiles += it }
    }

    private suspend fun WritableFileSystemDirectory.createProjFile(name: String, json: String) {
        val file = getOrCreateFile(name)
        projFiles += file
        val existing = file.read().decodeToString()
        if (existing != json) {
            file.writeText(json)
        }
    }

    private val GameEntityData.dirName: String get() = "${settings.name.fileNameSafe()}_${id.value}"
    private val GameEntityData.fileName: String get() = "$dirName.json"

    companion object {
        private val nameRegex = Regex("[^\\w\\-_]+")

        private fun String.fileNameSafe(): String = replace(nameRegex, "-")

        suspend fun saveProjectData(data: ProjectData, targetDir: WritableFileSystemDirectory) {
            ProjectWriter(data, targetDir).saveTree()
        }
    }
}
