package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.project.Project
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun loadBranches(project: Project?): List<String> {
    if (project == null) return emptyList()
    val projectPath = project.basePath ?: return emptyList()
    val result = mutableListOf<String>()
    result.add(project.name)

    val gitDir = File(projectPath, ".git")
    if (!gitDir.exists() || !gitDir.isDirectory) return result

    try {
        val process = ProcessBuilder("git", "branch").directory(File(projectPath)).redirectErrorStream(true).start()

        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val branches = mutableListOf<String>()
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            val branchName = line?.trim()?.replace("* ", "") ?: continue
            if (branchName.isNotEmpty()) {
                branches.add(branchName)
            }
        }

        process.waitFor()

        result.addAll(branches)

        return result
    } catch (e: Exception) {
        return result
    }
}