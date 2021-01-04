package org.carrat.webidl.build.compile.model.webidlir

class Enum(
    override val identifier: Identifier,
    val values : List<String>
) : Declaration() {
    override val members: Collection<Member>
        get() = emptySet()
}
