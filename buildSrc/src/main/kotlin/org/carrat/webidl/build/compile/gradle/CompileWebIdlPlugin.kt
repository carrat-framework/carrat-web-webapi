package org.carrat.webidl.build.compile.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool

class CompileWebIdlPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("compileWebIdl", CompileWebIdlExtension::class.java)

        val compileWebIdlTask = project.tasks.register("compileWebIdl", CompileWebIdlTask::class.java) {
            it.getInput().set(extension.getInput())
            it.getCommonOutput().set(extension.getCommonOutput())
            it.getJsOutput().set(extension.getJsOutput())
            it.getOtherOutputs().set(extension.getOtherOutputs().map { it.values })
            it.getPackage().set(extension.getPackage())
            it.getIgnoreDeclarations().set(extension.getIgnoreDeclarations())
        }

        project.afterEvaluate {
            addOutputDirectory(project, "commonMain", extension.getCommonOutput().get(), compileWebIdlTask, extension)
            addOutputDirectory(project, "jsMain", extension.getJsOutput().get(), compileWebIdlTask, extension)
            extension.getOtherOutputs().get().forEach {
                addOutputDirectory(project, it.key, it.value, compileWebIdlTask, extension)
            }

            project.tasks.withType(AbstractKotlinCompileTool::class.java).forEach {
                it.dependsOn(compileWebIdlTask)
            }
        }
    }

    private fun addOutputDirectory(
        project: Project,
        sourceSet: String,
        path: String,
        compileWebIdlTask: TaskProvider<*>,
        extension: CompileWebIdlExtension
    ) {
        project.extensions.configure(KotlinMultiplatformExtension::class.java) {
            val kotlin = it.sourceSets.named(sourceSet).get().kotlin
            kotlin.srcDir(path)
            kotlin.compiledBy(compileWebIdlTask) {
                val directoryProperty = project.objects.directoryProperty()
                directoryProperty.set(directoryProperty.dir(extension.getCommonOutput()))
                directoryProperty
            }
        }
    }
}