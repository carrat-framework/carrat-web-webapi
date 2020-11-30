package org.carrat.webidl.build.fetch.gradle

import org.carrat.webidl.build.fetch.logic.fetchIdls
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.nio.file.Paths

abstract class FetchWebIdlTask : DefaultTask() {
    @InputFile
    abstract fun getSources(): Property<String> // https://github.com/gradle/gradle/issues/10690

    @OutputDirectory
    abstract fun getOutput(): Property<String> // https://github.com/gradle/gradle/issues/10690

    @TaskAction
    fun fetch() {
        fetchIdls(Paths.get(getSources().get()), Paths.get(getOutput().get()), project.logger)
    }
}