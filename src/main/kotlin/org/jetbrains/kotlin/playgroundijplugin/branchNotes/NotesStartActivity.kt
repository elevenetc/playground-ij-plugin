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
                val branch = repository.currentBranchName ?: return@GitRepositoryChangeListener
                val branchService = project.getService(CurrentBranchService::class.java)
                branchService.setCurrentBranch(branch)
            }
        )
    }
}