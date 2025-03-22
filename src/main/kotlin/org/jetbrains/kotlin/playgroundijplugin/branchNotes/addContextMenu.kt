package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.ui.components.JBTextArea
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

fun JBTextArea.addContextMenu() {
    val popup = JPopupMenu()

    val cut = JMenuItem("Cut").apply {
        addActionListener { cut() }
    }
    val copy = JMenuItem("Copy").apply {
        addActionListener { copy() }
    }
    val paste = JMenuItem("Paste").apply {
        addActionListener { paste() }
    }
    val selectAll = JMenuItem("Select All").apply {
        addActionListener { selectAll() }
    }

    popup.add(cut)
    popup.add(copy)
    popup.add(paste)
    popup.addSeparator()
    popup.add(selectAll)

    componentPopupMenu = popup
}