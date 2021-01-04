package org.carrat.webidl.build.compile.model.webidlir

abstract class Declaration {
    abstract val identifier : Identifier
    abstract val members : Collection<Member>
    open val inherits : Identifier? = null
}
