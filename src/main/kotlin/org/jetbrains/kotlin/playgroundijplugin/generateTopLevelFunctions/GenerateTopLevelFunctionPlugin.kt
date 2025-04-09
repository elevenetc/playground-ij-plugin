package org.jetbrains.kotlin.playgroundijplugin.generateTopLevelFunctions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

class GenerateTopLevelFunctionsCallsAction : AnAction("Generate Top Level Function") {

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

            // Get the text of the file
            val fileText = kotlinPsiFile.text

            // Find all top-level functions in the file
            // Match pattern: fun name<generics>(params): returnType
            // We need to ensure we're only matching top-level functions, not methods inside classes
            val fileLines = fileText.lines()
            val topLevelFunctionLines = mutableListOf<String>()

            // Simple approach: collect lines that start with "fun" and are not inside a class/object/interface block
            var insideClassOrObject = false
            var braceCount = 0

            for (line in fileLines) {
                val trimmedLine = line.trim()

                // Check if we're entering a class, object, or interface declaration
                if (trimmedLine.startsWith("class ") || trimmedLine.startsWith("object ") || trimmedLine.startsWith("interface ")) {
                    insideClassOrObject = true
                }

                // Count opening braces
                braceCount += line.count { it == '{' }

                // Count closing braces
                braceCount -= line.count { it == '}' }

                // If braceCount is 0, we're at the top level
                if (braceCount == 0) {
                    insideClassOrObject = false
                }

                // If we're not inside a class/object and the line starts with "fun", it's a top-level function
                if (!insideClassOrObject && trimmedLine.startsWith("fun ")) {
                    topLevelFunctionLines.add(line)
                }
            }

            // Now parse the top-level function lines
            val functionRegex = Regex("fun\\s+([a-zA-Z0-9_]+)\\s*(<[^>]*>)?\\s*\\(([^)]*)\\)\\s*(:)?\\s*([^{]*)?")
            val matches = topLevelFunctionLines.flatMap { functionRegex.findAll(it) }

            for (match in matches) {
                val functionName = match.groupValues[1]
                val generics = match.groupValues[2]
                val parameters = match.groupValues[3]

                // Skip the function we're generating to avoid recursion
                if (functionName == "topLevelFunctionsCalls") continue

                // Skip functions with generics
                if (generics.isNotEmpty()) continue

                // Parse parameters
                val paramList = parameters.split(",").map { it.trim() }
                var hasNonPrimitiveArgs = false
                val args = mutableListOf<String>()

                for (param in paramList) {
                    if (param.isEmpty()) continue

                    // Extract parameter type
                    val paramParts = param.split(":")
                    if (paramParts.size < 2) {
                        hasNonPrimitiveArgs = true
                        break
                    }

                    val paramType = paramParts[1].trim()

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
