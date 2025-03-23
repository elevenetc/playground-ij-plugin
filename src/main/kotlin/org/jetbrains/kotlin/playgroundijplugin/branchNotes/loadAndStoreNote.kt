package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project

fun loadProjectNote(project: Project): SideNote {
    val key = createNoteKey(project)
    return SideNote(loadNote(key, project), key)
}

fun loadBranchNote(branch: Branch, project: Project): SideNote {
    val key = createNoteKey(project, branch.name)
    return SideNote(loadNote(key, project), key)
}

fun storeProjectNote(note: String, project: Project) {
    storeNote(createNoteKey(project), note, project)
}

fun storeBranchNote(note: String, branch: Branch, project: Project) {
    storeNote(createNoteKey(project, branch.name), note, project)
}

fun storeNote(note: SideNote, project: Project) {
    storeNote(note.key, note.note, project)
}

fun storeNote(key: SideNote.Key, note: String, project: Project) {
    PropertiesComponent.getInstance(project).setValue(key.value, note)
}

private fun createNoteKey(project: Project, branch: String = ""): SideNote.Key {
    val projectPath = project.basePath ?: ""
    val projectHash = projectPath.hashCode().toString()
    return SideNote.Key("side-note-${projectHash}-$branch")
}

private fun loadNote(key: SideNote.Key, project: Project): String {
    return PropertiesComponent.getInstance(project).getValue(key.value, "")
}

//fun storeNote(branchName: String, note: String, project: Project) {
//    println("Storing note($branchName): ${note.lines().firstOrNull()}")
//    val key = createNoteKey(project, branchName)
//    PropertiesComponent.getInstance(project).setValue(key, note)
//}