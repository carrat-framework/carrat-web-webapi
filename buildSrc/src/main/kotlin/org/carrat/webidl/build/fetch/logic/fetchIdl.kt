package org.carrat.webidl.build.fetch.logic

import io.ktor.client.*
import io.ktor.client.request.*
import org.carrat.webidl.build.fetch.model.IdlSource
import org.jsoup.Jsoup

// Based on https://github.com/microsoft/TypeScript-DOM-lib-generator/blob/master/src/idlfetcher.ts

suspend fun fetchIdl(source: IdlSource, httpClient: HttpClient): String {
    val documentString = httpClient.get<String>(source.url)
    if (source.url.endsWith(".idl")) {
        return documentString
    }
    val document = Jsoup.parse(documentString)
    val extractedIdl = extractIdl(document)
    val cssDefinitions = extractCssDefinitions(document)
    val idl = listOf(extractedIdl, cssDefinitions).filterNotNull().joinToString("\n\n")
    return idl
}