package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.project.Project

class NoteStorage(private val project: Project) {

    fun storeBranchNote(note: String, branch: Branch) {
        storeBranchNote(note, branch, project)
    }

    fun storeProjectNote(note: String) {
        storeProjectNote(note, project)
    }

    fun loadBranchNote(branch: Branch): SideNote {
        return loadBranchNote(branch, project)
    }

    fun loadProjectNote(): SideNote {
        return loadProjectNote(project)
    }
}