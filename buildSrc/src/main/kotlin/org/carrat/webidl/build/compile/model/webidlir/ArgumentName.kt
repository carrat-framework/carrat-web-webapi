package org.carrat.webidl.build.compile.model.webidlir

sealed class ArgumentName {
    data class Reference(
        val identifier: Identifier
    ) : ArgumentName()

    data class Keyword(
        val keyword: ArgumentNameKeyword
    ) : ArgumentName()
}