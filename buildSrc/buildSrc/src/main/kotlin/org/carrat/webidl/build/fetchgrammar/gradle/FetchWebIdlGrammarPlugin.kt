package org.carrat.webidl.build.fetchgrammar.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class FetchWebIdlGrammarPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("fetchGrammar", FetchWebIdlGrammarExtension::class.java)

        project.tasks.register("fetchGrammar", FetchWebIdlGrammarTask::class.java) {
            it.getUrl().set(extension.getUrl())
            it.getPackage().set(extension.getPackage())
            it.getOutput().set(extension.getOutput())
        }
    }

}