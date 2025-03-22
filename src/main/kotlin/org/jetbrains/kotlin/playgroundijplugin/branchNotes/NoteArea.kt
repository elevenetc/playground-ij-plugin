package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextArea

class NoteArea(
    val project: Project
) : JBTextArea() {

    private var currentNote = ""

    init {
        autoscrolls = true
        addContextMenu()
    }

    fun setNote(note: String) {
        if (currentNote == note) return
        currentNote = note
        text = note
    }
}