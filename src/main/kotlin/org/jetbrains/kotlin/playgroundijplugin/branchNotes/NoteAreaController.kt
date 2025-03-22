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
    project: Project
) {
    private var branches = DefaultComboBoxModel<String>()
    private val branchesService = project.getService(CurrentBranchService::class.java)

    fun onCreate() {

        //loadAndSetBranches()
        //loadAndSetSelectedBranchNode()

        changeBranchBox.addActionListener {
            unsubscribeFromChanges()
            loadAndSetSelectedBranchNode()
            subscribeToChanges()
        }

        refreshBranches.addActionListener {
            unsubscribeFromChanges()
            setBranches()
            subscribeToChanges()
        }

        branchesService.onCurrentBranchChangeListener { branch ->
            unsubscribeFromChanges()
            setBranches()
            //branches.selectedItem = branch
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

    private fun setBranches() {
        val loadedBranches = branchesService.getBranches().map { it.name }
        val currentBranch = branchesService.getCurrentBranch() ?: loadedBranches.firstOrNull()

        branches.removeAllElements()
        branches.addAll(loadedBranches)
        branches.selectedItem = currentBranch
        changeBranchBox.model = branches
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