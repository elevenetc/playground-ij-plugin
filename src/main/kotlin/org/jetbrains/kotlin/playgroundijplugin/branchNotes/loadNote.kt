package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project

/**
 * Creates a unique key for storing and retrieving notes based on project path and branch name
 */
private fun createNoteKey(project: Project, branchName: String): String {
    val projectPath = project.basePath ?: ""
    // Create a hash of the project path to keep the key manageable
    val projectHash = projectPath.hashCode().toString()
    return "branch_note_${projectHash}_$branchName"
}

/**
 * Loads a note for the specified branch from local storage
 */
fun loadNote(branchName: String, project: Project): String {
    val key = createNoteKey(project, branchName)
    return PropertiesComponent.getInstance(project).getValue(key, "")
}

/**
 * Stores a note for the specified branch in local storage
 */
fun storeNote(branchName: String, note: String, project: Project) {
    val key = createNoteKey(project, branchName)
    PropertiesComponent.getInstance(project).setValue(key, note)
}