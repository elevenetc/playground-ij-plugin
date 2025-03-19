package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.text.JTextComponent

fun JTextComponent.onFocusLost(listener: (e: FocusEvent?) -> Unit) {
    addFocusListener(object : FocusAdapter() {
        override fun focusLost(e: FocusEvent?) {
            listener(e)
        }
    })
}