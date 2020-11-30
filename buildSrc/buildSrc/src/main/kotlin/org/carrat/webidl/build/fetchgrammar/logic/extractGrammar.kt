package org.carrat.webidl.build.fetchgrammar.logic

import com.google.common.io.Resources
import org.carrat.webidl.build.fetchgrammar.gradle.FetchWebIdlGrammarPlugin
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.nio.charset.StandardCharsets
import java.util.*

private const val TAB = "    "

fun extractGrammar(document: Document, out: Appendable, _package: String) {
    out.append("grammar WebIdl;\n\n")

    out.append(
        "@header {\n" +
                "package $_package;\n" +
                "}"
    )
    out.append("\n\n")
    out.append("document\n")
    out.append("$TAB:${TAB}definitions EOF\n")
    out.append("$TAB;")

    val grammarNode = document.selectFirst("div[data-fill-with=grammar-index] pre.grammar")

    var firstRuleSet = true
    var newRuleSet = false
    var newRule = true
    for (child in grammarNode.childNodes()) {
        if (child is Element) {
            if (child.id().isNotEmpty()) {
                if (firstRuleSet) {
                    firstRuleSet = false
                } else {
                    out.append("\n$TAB;")
                }
                out.append("\n\n")
                val nonTerminalId = child.text().toNonTerminalId()
                out.append("$nonTerminalId\n")
                newRuleSet = true
                newRule = true
                if (nonTerminalId == "attributeName") {
                    newRuleSet = false
                    out.append("$TAB:$TAB'float'")
                }
            } else {
                if (newRule) {
                    if (newRuleSet) {
                        out.append("$TAB:$TAB")
                        newRuleSet = false
                    } else {
                        out.append("\n")
                        out.append("$TAB|$TAB")
                    }
                    newRule = false
                } else {
                    out.append(" ")
                }
                when (child.tagName()) {
                    "emu-nt" -> {
                        if (child.text() == "decimal") {
                            out.append(child.text().toUpperCase(Locale.ENGLISH))
                        } else {
                            out.append(child.text().toNonTerminalId())
                        }
                    }
                    "emu-t" -> {
                        if (child.classNames().contains("regex")) {
                            out.append(child.text().toUpperCase(Locale.ENGLISH))
                        } else {
                            out.append("'${child.text()}'")
                        }
                    }
                }
            }
        }
        if (child is TextNode) {
            if (child.wholeText.contains('\n')) {
                if (child.wholeText.contains('\u03B5')) { //ε
                    out.append("\n$TAB|$TAB// \u03B5")
                }
                newRule = true
            }
        }
    }
    out.append("\n$TAB;\n\n")
    @Suppress("UnstableApiUsage")
    out.append(
        Resources.toString(
            FetchWebIdlGrammarPlugin::class.java.getResource("/org/carrat/webidl/build/fetchgrammar/regex.g4.fragment")!!,
            StandardCharsets.UTF_8
        )
    )
}

private fun String.toNonTerminalId(): String {
    if (this.isEmpty()) {
        return this
    }
    val first = this.first()
    val lower: String
    if (first.isUpperCase()) {
        lower = first.toLowerCase() + this.substring(1)
    } else {
        lower = this
    }
    val escaped: String
    if (javaKeywords.contains(lower)) {
        escaped = "widl${lower.first().toUpperCase()}${lower.substring(1)}"
    } else {
        escaped = lower
    }
    return escaped
}