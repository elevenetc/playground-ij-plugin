package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.project.Project

class NoteStorage(private val project: Project) {
    fun storeNote(branch: String, note: String) {
        storeNote(branch, note, project)
    }

    fun loadNote(branch: String): String {
        return loadNote(branch, project)
    }
}