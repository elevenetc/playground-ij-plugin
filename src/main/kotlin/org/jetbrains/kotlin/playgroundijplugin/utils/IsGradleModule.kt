package org.jetbrains.kotlin.playgroundijplugin.utils

import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.module.Module

fun isGradleModule(module: Module): Boolean {
    val projectSystemId = ProjectSystemId("GRADLE")
    return ExternalSystemApiUtil.isExternalSystemAwareModule(projectSystemId, module)
}