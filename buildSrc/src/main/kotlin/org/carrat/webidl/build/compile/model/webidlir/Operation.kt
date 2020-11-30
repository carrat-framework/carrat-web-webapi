package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.webidlir.types.Type

data class Operation(
    val special: Special?,
    val type : Type,
    val name : OperationName?,
    val arguments : List<Argument>
) : Member() {
    enum class Special(val keyword : String) {
        GETTER("getter"),
        SETTER("setter"),
        DELETER("deleter");

        companion object {
            val byKeyword = Special.values().map {it.keyword to it}.toMap()
        }
    }
}