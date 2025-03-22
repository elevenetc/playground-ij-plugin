package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
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

        val project = currentProject

        if (project == null) {
            val panel = JBPanel<JBPanel<*>>(BorderLayout())
            val label = JLabel("No project available")
            panel.add(label, BorderLayout.CENTER)
            return panel
        } else {
            // Create a tabbed pane
            val tabbedPane = JBTabbedPane()

            // First tab - empty text area
            val emptyNoteArea = NoteArea(project)
            val projectNotes = JBPanel<JBPanel<*>>(BorderLayout())
            projectNotes.add(JBScrollPane(emptyNoteArea), BorderLayout.CENTER)
            tabbedPane.addTab("Project notes", projectNotes)

            // Second tab - current implementation with combobox and text area
            val branchNotes = JBPanel<JBPanel<*>>(BorderLayout())

            val noteArea = NoteArea(project)
            val branchesBox = ComboBox<String>()
            val refreshButton = JButton("Refresh")
            val noteAreaController = NoteAreaController(
                noteArea,
                branchesBox,
                NoteStorage(project),
                refreshButton,
                project
            )

            noteAreaController.onCreate()

            val buttonsPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
            buttonsPanel.add(refreshButton)

            val topPanel = JPanel(BorderLayout())
            topPanel.add(branchesBox, BorderLayout.CENTER)
            topPanel.add(buttonsPanel, BorderLayout.EAST)

            branchNotes.add(topPanel, BorderLayout.NORTH)
            branchNotes.add(JBScrollPane(noteArea), BorderLayout.CENTER)

            tabbedPane.addTab("Branch Notes", branchNotes)

            return tabbedPane
        }
    }
}
