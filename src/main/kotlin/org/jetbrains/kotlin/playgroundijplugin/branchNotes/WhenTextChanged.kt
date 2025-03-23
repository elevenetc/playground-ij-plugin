package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.ui.DocumentAdapter
import javax.swing.event.DocumentEvent

fun whenTextChanged(handler: () -> Unit): DocumentAdapter {
    return object : DocumentAdapter() {
        override fun textChanged(e: DocumentEvent) {
            handler.invoke()
        }
    }
}