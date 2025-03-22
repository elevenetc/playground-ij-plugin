package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.DocumentAdapter
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.event.DocumentEvent

class NoteAreaController(
    private val noteArea: NoteArea,
    private val changeBranchBox: ComboBox<String>,
    private val noteStorage: NoteStorage,
    private val refreshBranches: JButton,
    private val project: Project
) {
    private var branches = DefaultComboBoxModel<String>()

    fun onCreate() {

        loadAndSetBranches()
        loadAndSetSelectedBranchNode()

        changeBranchBox.addActionListener {
            unsubscribeFromChanges()
            loadAndSetSelectedBranchNode()
            subscribeToChanges()
        }

        project.getService(CurrentBranchService::class.java).addListener { branch ->
            unsubscribeFromChanges()
            branches.selectedItem = branch
            subscribeToChanges()
        }

        refreshBranches.addActionListener {
            unsubscribeFromChanges()
            loadAndSetBranches()
            subscribeToChanges()
        }
    }

    private fun subscribeToChanges() {
        if (listener != null) return
        listener = whenTextChanged { storeSelectedBranchNote() }
        noteArea.document.addDocumentListener(listener)
    }

    private fun unsubscribeFromChanges() {
        noteArea.document.removeDocumentListener(listener)
        listener = null
    }

    private fun loadAndSetBranches() {
        val loadedBranches = loadBranches(project)
        branches.removeAllElements()
        branches.addAll(loadedBranches)
        if (branches.selectedItem == null) branches.selectedItem = loadedBranches.first()
        if (changeBranchBox.model.size == 0) changeBranchBox.model = branches
    }

    private fun loadAndSetSelectedBranchNode() {
        val selectedBranch = branches.selectedItem as? String ?: return
        noteArea.setNote(noteStorage.loadNote(selectedBranch))
    }

    private fun storeSelectedBranchNote() {
        val selectedBranch = branches.selectedItem as? String ?: return
        noteStorage.storeNote(selectedBranch, noteArea.text)
    }

    var listener: DocumentAdapter? = null
}

private fun whenTextChanged(handler: () -> Unit): DocumentAdapter {
    return object : DocumentAdapter() {
        override fun textChanged(e: DocumentEvent) {
            handler.invoke()
        }
    }
}