package org.carrat.webidl.build.fetchgrammar.gradle

import org.gradle.api.provider.Property

abstract class FetchWebIdlGrammarExtension {
    abstract fun getUrl(): Property<String>
    abstract fun getPackage(): Property<String>
    abstract fun getOutput(): Property<String> // https://github.com/gradle/gradle/issues/10690
}