package org.carrat.webidl.build.fetchgrammar.gradle

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.jsoup.Jsoup
import org.carrat.webidl.build.fetchgrammar.logic.extractGrammar
import org.gradle.api.tasks.OutputFile
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

abstract class FetchWebIdlGrammarTask : DefaultTask() {
    @Input
    abstract fun getUrl(): Property<String>

    @Input
    abstract fun getPackage(): Property<String>

    @OutputFile
    abstract fun getOutput(): Property<String> // https://github.com/gradle/gradle/issues/10690

    @TaskAction
    fun fetchGrammar() {
        val httpClient = HttpClient()
        val documentString = runBlocking { httpClient.get<String>(getUrl().get()) }
        httpClient.close()
        val document = Jsoup.parse(documentString)
        val output = Paths.get(getOutput().get())
        Files.createDirectories(output.parent)
        Files.newBufferedWriter(output, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
            .use {
                extractGrammar(document, it, getPackage().get())
            }
    }
}