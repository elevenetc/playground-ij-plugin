package org.jetbrains.kotlin.playgroundijplugin.branchNotes

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