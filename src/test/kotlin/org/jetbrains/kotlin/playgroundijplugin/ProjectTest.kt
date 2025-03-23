package org.jetbrains.kotlin.playgroundijplugin

/**
 * This test class demonstrates how to test IntelliJ Platform Project functionality.
 * It extends BasePlatformTestCase which provides the necessary test infrastructure
 * for IntelliJ plugin development.
 *
 * Key testing capabilities demonstrated:
 * 1. Project Creation - Using LightProjectDescriptor
 * 2. Project State Verification - Checking initialization and disposal states
 * 3. Project Components - Testing message bus and other project-level services
 *
 * To run these tests:
 * 1. Make sure you have proper test dependencies in build.gradle.kts
 * 2. Tests can be run directly from IDE or via gradle test task
 * 3. Use myFixture.project to access the test project instance
 */

import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.playgroundijplugin.branchNotes.Branch
import org.jetbrains.kotlin.playgroundijplugin.branchNotes.createNoteKey
import org.jetbrains.kotlin.playgroundijplugin.branchNotes.loadBranchNote
import org.jetbrains.kotlin.playgroundijplugin.branchNotes.storeNote

class ProjectTest : BasePlatformTestCase() {

    /**
     * Specifies the type of project to create for testing.
     * EMPTY_PROJECT_DESCRIPTOR creates a minimal project suitable for basic testing.
     * Other descriptors are available for specific testing scenarios.
     */
    override fun getProjectDescriptor(): LightProjectDescriptor {
        return LightProjectDescriptor.EMPTY_PROJECT_DESCRIPTOR
    }

    /**
     * Tests basic project operations and state.
     * Demonstrates how to:
     * - Access the test project instance
     * - Verify project is properly initialized
     * - Check basic project properties
     */
    fun testBasicProjectOperations() {
        // Get the test project instance
        val project: Project = myFixture.project

        // Verify project is not disposed
        assertFalse("Project should not be disposed", project.isDisposed)

        // Test project name
        assertNotNull("Project name should not be null", project.name)
        println("[DEBUG_LOG] Project name: ${project.name}")

        // Test project base path
        assertNotNull("Project base path should not be null", project.basePath)
        println("[DEBUG_LOG] Project base path: ${project.basePath}")

        // Test project is initialized
        assertTrue("Project should be initialized", project.isInitialized)
    }

    /**
     * Tests project-level components and services.
     * Demonstrates how to:
     * - Access project's message bus
     * - Verify project services are available
     * - Check project initialization state
     */
    fun testProjectComponents() {
        val project: Project = myFixture.project

        // Test project service availability
        val messageBus = project.messageBus
        assertNotNull("Message bus should be available", messageBus)

        // Test project settings
        assertTrue("Project settings should be available", project.isInitialized)
    }

    /**
     * Tests the branch notes functionality.
     * Verifies that notes can be stored and retrieved correctly for different branches.
     */
    fun testBranchNotes() {
        val project: Project = myFixture.project

        val branch1 = "test-branch-1"
        val branch2 = "test-branch-2"
        val note1 = "This is a test note for branch 1"
        val note2 = "This is a test note for branch 2"

        assertEmpty(loadBranchNote(Branch(branch1), project).note)
        assertEmpty(loadBranchNote(Branch(branch2), project).note)

        storeNote(createNoteKey(project, branch1), note1, project)
        storeNote(createNoteKey(project, branch2), note2, project)

        assertEquals(note1, loadBranchNote(Branch(branch1), project).note)
        assertEquals(note2, loadBranchNote(Branch(branch2), project).note)
    }
}
