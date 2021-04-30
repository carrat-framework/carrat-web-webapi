package org.carrat.webidl.build.compile.model.webidlir

sealed class OperationName {
    data class Reference(val identifier: String) : OperationName()

    sealed class Keyword : OperationName() {
        object Includes : Keyword()
    }
}
