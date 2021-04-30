package org.carrat.webidl.build.fetch.gradle

import org.gradle.api.provider.Property

abstract class FetchWebIdlExtension {
    abstract fun getSources(): Property<String> // https://github.com/gradle/gradle/issues/10690

    abstract fun getOutput(): Property<String> // https://github.com/gradle/gradle/issues/10690
}