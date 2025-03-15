package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

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
        val panel = JBPanel<JBPanel<*>>(BorderLayout())

        val textField = JBTextField("Add note...")
        val dropdownItems = loadBranches(currentProject).toTypedArray()
        val comboBox = ComboBox(DefaultComboBoxModel(dropdownItems))

        comboBox.addActionListener {
            val selectedItem = comboBox.selectedItem as String
            textField.text = selectedItem
        }

        val refreshButton = JButton("Refresh")
        refreshButton.addActionListener {
            val updatedBranches = loadBranches(currentProject).toTypedArray()
            comboBox.model = DefaultComboBoxModel(updatedBranches)
        }

        val topPanel = JPanel(BorderLayout())
        topPanel.add(comboBox, BorderLayout.CENTER)
        topPanel.add(refreshButton, BorderLayout.EAST)

        panel.add(topPanel, BorderLayout.NORTH)
        panel.add(textField, BorderLayout.CENTER)

        return panel
    }
}
