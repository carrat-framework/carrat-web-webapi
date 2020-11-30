package org.carrat.webidl.build.compile.model.webidlir

sealed class AttributeName {
    data class Reference(
        val identifier: Identifier
    ) : AttributeName()

    sealed class Keyword : AttributeName() {
        object Async : Keyword()
        object Required : Keyword()
    }
}
