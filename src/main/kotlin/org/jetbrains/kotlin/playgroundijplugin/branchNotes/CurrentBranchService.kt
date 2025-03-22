package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.components.Service

@Service(Service.Level.PROJECT)
class CurrentBranchService {

    private val listeners = mutableListOf<(String) -> Unit>()
    private var currentBranch: String? = null

    fun setCurrentBranch(newBranch: String) {
        if (newBranch != currentBranch) {
            currentBranch = newBranch
            listeners.forEach { it(newBranch) }
        }
    }

    fun addListener(listener: (String) -> Unit) {
        listeners.add(listener)
        currentBranch?.let { branch -> listener(branch) }

    }
}