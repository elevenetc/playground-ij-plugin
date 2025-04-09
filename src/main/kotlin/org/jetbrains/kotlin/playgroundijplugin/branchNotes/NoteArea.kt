package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorTextField
import com.intellij.ui.JBColor
import java.awt.Cursor
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.regex.Pattern

private val urlPattern = Pattern.compile(
    "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
    Pattern.CASE_INSENSITIVE
)

class NoteArea(project: Project) : EditorTextField(null, project, PlainTextFileType.INSTANCE, false, false) {

    private var currentNote = ""

    init {
        autoscrolls = true

        // Set cursor to hand when hovering over links
        addSettingsProvider { editor ->
            editor.contentComponent.cursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR)

            // Add document listener to detect URLs when text changes
            editor.document.addDocumentListener(object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    highlightUrls()
                }
            })


            editor.contentComponent.addMouseMotionListener(object : MouseAdapter() {
                override fun mouseMoved(e: MouseEvent) {
                    updateCursor(editor, e)
                }
            })

            editor.contentComponent.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
//                    val offset = editor.logicalPositionToOffset(
//                        editor.xyToLogicalPosition(java.awt.Point(e.x, e.y))
//                    )
//
//                    // Find URL at the clicked position
//                    val text = editor.document.text
//                    val matcher = urlPattern.matcher(text)
//                    while (matcher.find()) {
//                        if (offset >= matcher.start() && offset <= matcher.end()) {
//                            val url = matcher.group()
//                            BrowserUtil.browse(url)
//                            break
//                        }
//                    }
                }

                override fun mouseEntered(e: MouseEvent) {
                    //updateCursor(editor, e)
                }

                override fun mouseMoved(e: MouseEvent) {
                    //updateCursor(editor, e)
                }


            })
        }


    }

    override fun createEditor(): EditorEx {
        val e = super.createEditor()
        //e.settings.isLineNumbersShown = true
        return e
    }

    fun setNote(note: String) {
        if (currentNote == note) return
        currentNote = note
        text = note
        highlightUrls()
    }

    private fun highlightUrls() {
        val editor = editor ?: return
        val document = editor.document
        val text = document.text
        val markupModel = editor.markupModel

        // Clear existing highlights
        markupModel.removeAllHighlighters()

        // Create text attributes for URL highlighting
        val attributes = TextAttributes().apply {
            foregroundColor = JBColor.BLUE
            effectType = null
            fontType = Font.PLAIN
        }

        // Find and highlight URLs
        val matcher = urlPattern.matcher(text)
        while (matcher.find()) {
            markupModel.addRangeHighlighter(
                matcher.start(),
                matcher.end(),
                HighlighterLayer.SELECTION - 1,
                attributes,
                HighlighterTargetArea.EXACT_RANGE
            )
        }
    }
}

private fun updateCursor(editor: EditorEx, e: MouseEvent) {
    val offset = editor.logicalPositionToOffset(
        editor.xyToLogicalPosition(java.awt.Point(e.x, e.y))
    )

    // Check if cursor is over a URL
    val text = editor.document.text
    val matcher = urlPattern.matcher(text)
    var isOverUrl = false

    while (matcher.find()) {
        if (offset >= matcher.start() && offset <= matcher.end()) {
            isOverUrl = true
            break
        }
    }

    // Update cursor based on whether it's over a URL
    if (isOverUrl) {
        editor.contentComponent.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
    } else {
        editor.contentComponent.cursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR)
    }
}