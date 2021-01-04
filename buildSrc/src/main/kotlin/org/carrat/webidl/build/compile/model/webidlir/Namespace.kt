package org.carrat.webidl.build.compile.model.webidlir

class Namespace(
    override val identifier: Identifier,
    override val members: List<Member>
) : Declaration()
