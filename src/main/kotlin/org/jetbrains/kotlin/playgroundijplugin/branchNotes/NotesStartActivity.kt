package org.jetbrains.kotlin.playgroundijplugin.branchNotes

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryChangeListener

class BranchNotesStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        val connection = project.messageBus.connect()
        connection.subscribe(
            GitRepository.GIT_REPO_CHANGE,
            GitRepositoryChangeListener { repository ->
                val currentBranch = repository.currentBranchName ?: return@GitRepositoryChangeListener

                val branches = repository.branches.localBranches.map {
                    val updateTime = getBranchUpdateTime(project, repository.root, it.name)
                    BranchData(it.name, updateTime ?: -1)
                }.sortedBy { it.updateTime }

                val branchService = project.getService(CurrentBranchService::class.java)

                branchService.setBranches(branches)
                branchService.setCurrentBranch(currentBranch)
            }
        )
    }
}