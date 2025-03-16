package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.*

class BranchNotes : ToolWindowFactory {
    private var currentProject: Project? = null

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        currentProject = project
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(
            createToolWindowPanel(),
            null,
            false
        )
        toolWindow.contentManager.addContent(content)
    }

    private fun createToolWindowPanel(): JComponent {

        val project = currentProject

        if (project == null) {
            val panel = JBPanel<JBPanel<*>>(BorderLayout())
            val label = JLabel("No project available")
            panel.add(label, BorderLayout.CENTER)
            return panel
        } else {
            val panel = JBPanel<JBPanel<*>>(BorderLayout())

            val noteField = JBTextField("Add note...")
            val branches = loadBranches(project).toTypedArray()
            val branchesBox = ComboBox(DefaultComboBoxModel(branches))
            //val branchesBox = ComboBox<String>(0)

            // Variable to keep track of the current branch
            var currentBranch = if (branches.isNotEmpty()) branches[0] else ""

            // Load note for the initially selected branch
            if (currentBranch.isNotEmpty()) {
                noteField.text = loadNote(currentBranch, project)
            }

            // Save note when branch is changed
            branchesBox.addActionListener {
                // Save note for the previous branch
                if (currentBranch.isNotEmpty()) {
                    storeNote(currentBranch, noteField.text, project)
                }

                // Update current branch and load its note
                currentBranch = branchesBox.selectedItem as String
                noteField.text = loadNote(currentBranch, project)
            }

            // Save note when text field loses focus
            noteField.addFocusListener(object : FocusAdapter() {
                override fun focusLost(e: FocusEvent?) {
                    if (currentBranch.isNotEmpty()) {
                        storeNote(currentBranch, noteField.text, project)
                    }
                }
            })

            val refreshButton = JButton("Refresh")
            refreshButton.addActionListener {
                // Save current note before refreshing
                if (currentBranch.isNotEmpty()) {
                    storeNote(currentBranch, noteField.text, project)
                }

                val updatedBranches = loadBranches(project).toTypedArray()
                branchesBox.model = DefaultComboBoxModel(updatedBranches)

                // Update current branch
                currentBranch = if (updatedBranches.isNotEmpty()) updatedBranches[0] else ""
                if (currentBranch.isNotEmpty()) {
                    noteField.text = loadNote(currentBranch, project)
                }
            }

            val saveButton = JButton("Save")
            saveButton.addActionListener {
                if (currentBranch.isNotEmpty()) {
                    storeNote(currentBranch, noteField.text, project)
                }
            }

            val buttonsPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
            buttonsPanel.add(saveButton)
            buttonsPanel.add(refreshButton)

            val topPanel = JPanel(BorderLayout())
            topPanel.add(branchesBox, BorderLayout.CENTER)
            topPanel.add(buttonsPanel, BorderLayout.EAST)

            panel.add(topPanel, BorderLayout.NORTH)
            panel.add(noteField, BorderLayout.CENTER)

            return panel
        }
    }
}
