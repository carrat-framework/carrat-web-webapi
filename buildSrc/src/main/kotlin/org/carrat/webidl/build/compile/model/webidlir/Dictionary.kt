package org.carrat.webidl.build.compile.model.webidlir

class Dictionary(
    override val identifier : Identifier,
    override val inherits : Identifier?,
    override val members : List<Member>
) : Declaration()
