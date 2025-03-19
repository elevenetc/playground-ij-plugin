package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.observable.util.whenTextChanged
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton

class NoteAreaController(
    private val noteArea: NoteArea,
    private val changeBranchBox: ComboBox<String>,
    private val noteStorage: NoteStorage,
    private val refreshBranches: JButton,
    private val project: Project
) {
    //var selectedBranch: String = ""
    var branches = DefaultComboBoxModel<String>()

    fun onCreate() {

        loadAndSetBranches()
        loadAndSetSelectedBranchNode()

        changeBranchBox.addActionListener {
            storeSelectedBranchNote()
            loadAndSetSelectedBranchNode()
        }

        noteArea.onFocusLost { storeSelectedBranchNote() }
        noteArea.whenTextChanged { storeSelectedBranchNote() }

        refreshBranches.addActionListener {
            loadAndSetBranches()
        }
    }

    private fun loadAndSetBranches() {
        val loadedBranches = loadBranches(project)
        branches.removeAllElements()
        branches.addAll(loadedBranches)
        if (branches.selectedItem == null) {
            branches.selectedItem = loadedBranches.first()
        }
        if (changeBranchBox.model.size == 0) {
            changeBranchBox.model = branches
        }
    }

    private fun loadAndSetSelectedBranchNode() {

        noteArea.setNote(
            noteStorage.loadNote(branches.selectedItem as String)
        )
    }

    private fun storeSelectedBranchNote() {
        noteStorage.storeNote(branches.selectedItem as String, noteArea.text)
    }
}