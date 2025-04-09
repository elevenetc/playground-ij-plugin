package org.jetbrains.kotlin.playgroundijplugin.generateTopLevelFunctions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.nj2k.types.typeFqName
import org.jetbrains.kotlin.psi.KtNamedFunction

class GenerateTopLevelFunctionsCallsAction : AnAction("Generate Top Level Function") {

    /**
     * Checks if a PSI element represents a top-level function.
     * A top-level function is one that is not inside a class, object, or interface.
     */
    private fun isTopLevelFunction(element: PsiElement): Boolean {
        // Get the parent elements
        var parent = element.parent
        while (parent != null) {
            val text = parent.text
            // Check if the parent is a class, object, or interface
            if (text.contains("class ") || text.contains("object ") || text.contains("interface ")) {
                return false
            }
            parent = parent.parent
        }
        return true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return

        // Check if the file is a Kotlin file
        if (!psiFile.name.endsWith(".kt")) {
            return
        }

        // Get the document
        val document = editor.document

        // Find the current module
        val module = ModuleUtil.findModuleForFile(psiFile.virtualFile, project) ?: return

        // Find all Kotlin files in the current module and its dependencies
        val globalScope = GlobalSearchScope.moduleWithDependenciesScope(module)
        val kotlinFiles = FilenameIndex.getAllFilesByExt(project, "kt", globalScope)

        // Generate function calls
        val functionCalls = StringBuilder()

        // Process each Kotlin file in the module
        for (kotlinFile in kotlinFiles) {
            val psiManager = PsiManager.getInstance(project)
            val kotlinPsiFile = psiManager.findFile(kotlinFile) ?: continue

            // Find all function declarations in the file using PSI
            val functions = PsiTreeUtil.findChildrenOfType(kotlinPsiFile, PsiElement::class.java)
                .filterIsInstance<KtNamedFunction>()

            for (function in functions) {

                // Extract function name and parameters using PSI
                val functionName = function.name ?: ""

                // Skip the function we're generating to avoid recursion
                if (functionName == "topLevelFunctionsCalls") continue

                // Check for generics
                val hasGenerics = function.typeParameters.isNotEmpty()
                if (hasGenerics) continue

                var hasNonPrimitiveArgs = false
                val args = mutableListOf<String>()
                val paramList = function.valueParameters

                for (param in paramList) {

                    val paramType = param.typeFqName()?.shortName()?.asString() ?: ""

                    // Check if parameter type is primitive
                    when {
                        paramType.contains("Int") -> args.add("-1")
                        paramType.contains("Boolean") -> args.add("false")
                        paramType.contains("Double") -> args.add("0.0")
                        paramType.contains("Float") -> args.add("0.0f")
                        paramType.contains("Long") -> args.add("0L")
                        paramType.contains("Short") -> args.add("0")
                        paramType.contains("Byte") -> args.add("0")
                        paramType.contains("Char") -> args.add("'a'")
                        paramType.contains("String") -> args.add("\"\"")
                        else -> {
                            hasNonPrimitiveArgs = true
                            break
                        }
                    }
                }

                // Skip functions with non-primitive arguments
                if (hasNonPrimitiveArgs) continue

                // Add function call
                functionCalls.append("    $functionName(${args.joinToString(", ")})\n")
            }
        }

        // Create the function text
        val functionText = if (functionCalls.isEmpty()) {
            "\n\nfun topLevelFunctionsCalls() = Unit"
        } else {
            "\n\nfun topLevelFunctionsCalls() {\n$functionCalls}"
        }

        // Write the function to the document
        WriteCommandAction.runWriteCommandAction(project) {
            // Find the appropriate position to insert the function
            // For simplicity, we'll add it at the end of the file
            val insertPosition = document.textLength
            document.insertString(insertPosition, functionText)

            // Commit the document changes
            PsiDocumentManager.getInstance(project).commitDocument(document)
        }
    }

    override fun update(e: AnActionEvent) {
        // Enable the action only for Kotlin files
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        e.presentation.isEnabledAndVisible = psiFile != null && psiFile.name.endsWith(".kt")
    }
}
