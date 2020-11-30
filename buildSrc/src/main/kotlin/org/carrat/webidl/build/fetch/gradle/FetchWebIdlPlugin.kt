package org.carrat.webidl.build.fetch.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class FetchWebIdlPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("fetchWebIdl", FetchWebIdlExtension::class.java)

        project.tasks.register("fetchWebIdl", FetchWebIdlTask::class.java) {
            it.getSources().set(extension.getSources())
            it.getOutput().set(extension.getOutput())
        }
    }

}