package org.carrat.webidl.build.compile.model.webidlir

class Interface(
    override val identifier : Identifier,
    override val inherits : Identifier?,
    override val members : List<Member>
) : AnyInterface()