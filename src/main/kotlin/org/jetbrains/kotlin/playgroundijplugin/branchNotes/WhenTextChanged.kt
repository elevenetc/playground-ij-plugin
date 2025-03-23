package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.editor.event.DocumentListener

fun whenTextChanged(handler: () -> Unit): DocumentListener {
    return object : DocumentListener {
        override fun documentChanged(event: com.intellij.openapi.editor.event.DocumentEvent) {
            handler.invoke()
        }
    }
}