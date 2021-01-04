package org.carrat.webidl.build.compile.model.webidlir

abstract class AnyInterface : Declaration() {
    override val inherits : Identifier? = null
    abstract override val members : List<Member>
}
