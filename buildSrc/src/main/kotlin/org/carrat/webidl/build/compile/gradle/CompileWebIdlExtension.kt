package org.carrat.webidl.build.compile.gradle

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory

abstract class CompileWebIdlExtension {
    abstract fun getInput(): Property<String> // https://github.com/gradle/gradle/issues/10690

    abstract fun getPackage(): Property<String>

    abstract fun getIgnoreDeclarations(): ListProperty<String>

    abstract fun getCommonOutput(): Property<String> // https://github.com/gradle/gradle/issues/10690

    abstract fun getJsOutput(): Property<String> // https://github.com/gradle/gradle/issues/10690

    abstract fun getOtherOutputs(): MapProperty<String, String>
}