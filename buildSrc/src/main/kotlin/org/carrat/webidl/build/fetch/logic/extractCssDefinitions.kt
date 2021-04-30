package org.carrat.webidl.build.fetch.logic

import org.jsoup.nodes.Document

private val cssPropSelector = listOf(
    ".propdef dfn", // CSS Fonts, SVG
    ".propdef-title", // SVG Paint Servers
    "dfn.css[data-dfn-type=property]"
).joinToString(",");

internal fun extractCssDefinitions(document: Document): String? {
    val properties = document.select(cssPropSelector).map { it.text().trim() }

    if (properties.isEmpty()) {
        return null
    }

    return "partial interface CSSStyleDeclaration {${
        properties.map { property ->
            "\n  [CEReactions] attribute [LegacyNullToEmptyString] CSSOMString ${
                property.replaceHyphensToCamelCase()
            };"
        }.joinToString("")
    }\n};"
}

private fun String.replaceHyphensToCamelCase(): String =
    this.split("[.:_\\-<>/]".toRegex())
        .mapIndexed { i, s ->
            if (i == 0) s
            else s.capitalize()
        }
        .joinToString("")