package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.ui.EditorTextField

class NoteArea : EditorTextField() {

    private var currentNote = ""

    init {
        autoscrolls = true
    }

    fun setNote(note: String) {
        if (currentNote == note) return
        currentNote = note
        text = note
    }
}