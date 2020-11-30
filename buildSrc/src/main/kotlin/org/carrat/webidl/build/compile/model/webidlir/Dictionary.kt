package org.carrat.webidl.build.compile.model.webidlir

class Dictionary(
    override val identifier : Identifier,
    val inherits : Identifier?,
    val members : List<Member>
) : Declaration()