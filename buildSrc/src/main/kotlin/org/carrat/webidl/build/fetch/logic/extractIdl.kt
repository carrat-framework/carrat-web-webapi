package org.carrat.webidl.build.fetch.logic

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

private val idlSelector = listOf(
    "pre.idl:not(.extract):not(.example)", // bikeshed and ReSpec
    "pre.code code.idl-code", // Web Cryptography
    "pre:not(.extract) code.idl", // HTML
    "#permission-registry + pre.highlight" // Permissions
).joinToString(",");

fun extractIdl(document: Document): String? {
    val elements = document.select(idlSelector).filter { el ->
        if (el.parent() != null && el.parent().classNames().contains("example")) {
            false
        } else {
            val previous = el.previousElementSibling()
            if (previous == null) {
                true
            } else {
                !previous.classNames().contains("atrisk") && !previous.text().contains("IDL Index")
            }
        }
    }
    elements.forEach { (it as Element).selectFirst("span.idlHeader")?.remove() }
    return elements.map { trimCommonIndentation(it.text()).trim() }.joinToString("\n\n")
}

@OptIn(ExperimentalStdlibApi::class)
private fun trimCommonIndentation(text: String): String {
    val lines = text.split("\n").toMutableList()
    if (lines.first().isBlank()) {
        lines.removeFirst()
    }
    if (lines.last().isBlank()) {
        lines.removeLast()
    }
    val commonIndentation = lines.filter { it.isNotBlank() }.map(::getIndentation).min() ?: 0
    return lines.map { line -> line.substring(commonIndentation) }.joinToString("\n");
}

/** Count preceding whitespaces */
private fun getIndentation(line: String): Int {
    var count = 0
    for (ch in line) {
        if (ch != ' ') {
            break
        }
        count++
    }
    return count
}