package org.carrat.webidl.build.fetch.logic

import com.charleskorn.kaml.Yaml
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import org.carrat.webidl.build.fetch.model.IdlSource
import org.gradle.api.logging.Logger
import java.io.IOException
import java.nio.channels.UnresolvedAddressException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

fun fetchIdls(input: Path, output: Path, logger: Logger) {
    val inputString = String(Files.readAllBytes(input), StandardCharsets.UTF_8)
    val idlSources: List<IdlSource> = Yaml.default.decodeFromString(ListSerializer(IdlSource.serializer()), inputString)
    val httpClient = HttpClient()

    runBlocking {
        withContext(Dispatchers.Default) {
            idlSources.asFlow().onEach {
                try {
                    flowOf(it).onEach {
                        val idl = fetchIdl(it, httpClient)
                        withContext(Dispatchers.IO) {
                            Files.createDirectories(output)
                            Files.write(output.resolve("${it.title}.widl"), idl.toByteArray(StandardCharsets.UTF_8))
                        }
                    }.retry(10) { e ->
                        if (e is IOException || e is UnresolvedAddressException) {
                            logger.info("Failed to fetch ${it.url}. Retrying.", e)
                            delay(1000)
                            true
                        } else {
                            false
                        }
                    }.collect()
                } catch (e: Exception) {
                    logger.error("Failed to fetch ${it.url}. The file will be skipped.", e)
                }
            }.collect()
        }
    }
    httpClient.close()
}