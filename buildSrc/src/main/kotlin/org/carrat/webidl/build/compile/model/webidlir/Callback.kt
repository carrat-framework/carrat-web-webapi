package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.webidlir.types.Type

data class Callback(
    override val identifier: Identifier,
    val type : Type,
    val arguments : List<Argument>
) : Declaration() {
    override val members: Collection<Member>
        get() = emptySet()
}
