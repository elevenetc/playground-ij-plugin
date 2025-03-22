package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import java.text.SimpleDateFormat
import java.util.*

fun getBranchUpdateTime(project: Project, root: VirtualFile, branchName: String): Long? {
    val handler = GitLineHandler(project, root, GitCommand.REF_LOG).apply {
        addParameters("show", branchName, "-1", "--date=iso")
        setSilent(true)
    }

    val result = Git.getInstance().runCommand(handler)
    if (result.success()) {
        val line = result.output.firstOrNull() ?: return null
        val dateRegex = Regex("\\b\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} [+-]\\d{4}\\b")
        val dateString = dateRegex.find(line)?.value ?: return null

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US)
        return try {
            format.parse(dateString)?.time
        } catch (e: Exception) {
            null
        }
    }

    return null
}