package org.carrat.webidl.build.compile.model.webidlir

class Namespace(
    override val identifier: Identifier,
    val members: List<Member>
) : Declaration()