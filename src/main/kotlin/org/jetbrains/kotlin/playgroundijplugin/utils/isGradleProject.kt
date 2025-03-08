package org.jetbrains.kotlin.playgroundijplugin.utils

import com.intellij.openapi.project.Project
import java.io.File

fun isGradleProject(project: Project): Boolean {
    val projectDir = project.basePath?.let { File(it) } ?: return false
    val hasBuildGradle = File(projectDir, "build.gradle").exists() ||
            File(projectDir, "build.gradle.kts").exists()
    val hasSettingsGradle = File(projectDir, "settings.gradle").exists() ||
            File(projectDir, "settings.gradle.kts").exists()

    return hasBuildGradle || hasSettingsGradle
}
