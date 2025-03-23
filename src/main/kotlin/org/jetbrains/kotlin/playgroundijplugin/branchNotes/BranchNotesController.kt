package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import javax.swing.DefaultComboBoxModel

class BranchNotesController(
    private val noteArea: NoteArea,
    private val changeBranchBox: ComboBox<String>,
    private val noteStorage: NoteStorage,
    project: Project
) {
    private var branches = DefaultComboBoxModel<String>()
    private val branchesService = project.getService(CurrentBranchService::class.java)
    private var onNoteChangedListener: DocumentListener? = null

    fun onCreate() {

        changeBranchBox.addActionListener {
            unsubscribeFromChanges()
            loadAndSetSelectedBranchNote()
            subscribeToChanges()
        }

        branchesService.onCurrentBranchChangeListener { _ ->
            unsubscribeFromChanges()
            onCurrentBranchChangeListener()
            subscribeToChanges()
        }
    }

    private fun subscribeToChanges() {
        if (onNoteChangedListener != null) return
        onNoteChangedListener = whenTextChanged { storeSelectedBranchNote() }
        onNoteChangedListener?.let { noteArea.document.addDocumentListener(it) }
    }

    private fun unsubscribeFromChanges() {
        onNoteChangedListener?.let { noteArea.document.removeDocumentListener(it) }
        onNoteChangedListener = null
    }

    private fun onCurrentBranchChangeListener() {
        val loadedBranches = branchesService.getBranches().map { it.name }
        val currentBranch = branchesService.getCurrentBranch() ?: loadedBranches.firstOrNull()

        branches.removeAllElements()
        branches.addAll(loadedBranches)
        branches.selectedItem = currentBranch

        changeBranchBox.model = branches

        loadAndSetSelectedBranchNote()
    }

    private fun loadAndSetSelectedBranchNote() {
        val selectedBranch = branches.selectedItem as? String ?: return
        noteArea.setNote(noteStorage.loadBranchNote(Branch(selectedBranch)).note)
    }

    private fun storeSelectedBranchNote() {
        val selectedBranch = branches.selectedItem as? String ?: return
        noteStorage.storeBranchNote(noteArea.text, Branch(selectedBranch))
    }
}

