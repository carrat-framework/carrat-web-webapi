package org.carrat.webidl.build.compile.model.webidlir

class CallbackInterface(
    override val identifier: Identifier,
    override val members: List<Member>
) : AnyInterface()