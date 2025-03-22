package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.components.Service

@Service(Service.Level.PROJECT)
class CurrentBranchService {

    private val currentBranchChangeListeners = mutableListOf<(String) -> Unit>()
    private var currentBranch: String? = null
    private val branches = mutableListOf<BranchData>()

    fun setBranches(branches: List<BranchData>) {
        this.branches.clear()
        this.branches.addAll(branches)
    }

    fun getBranches(): List<BranchData> = branches
    fun getCurrentBranch(): String? = currentBranch

    fun setCurrentBranch(newBranch: String) {
        if (newBranch != currentBranch) {
            currentBranch = newBranch
            currentBranchChangeListeners.forEach { it(newBranch) }
        }
    }

    fun onCurrentBranchChangeListener(listener: (String) -> Unit) {
        currentBranchChangeListeners.add(listener)
        currentBranch?.let { branch -> listener(branch) }
    }
}