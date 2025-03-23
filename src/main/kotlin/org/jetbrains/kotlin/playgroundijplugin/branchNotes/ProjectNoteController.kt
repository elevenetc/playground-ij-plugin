package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.project.Project

class ProjectNoteController(
    private val noteArea: NoteArea,
    private val noteStorage: NoteStorage,
) {
    fun onCreate() {

        noteArea.text = noteStorage.loadProjectNote().note

        noteArea.document.addDocumentListener(whenTextChanged {
            noteStorage.storeProjectNote(noteArea.text)
        })
    }
}