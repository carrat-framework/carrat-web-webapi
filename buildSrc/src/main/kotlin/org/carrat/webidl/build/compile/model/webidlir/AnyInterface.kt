package org.carrat.webidl.build.compile.model.webidlir

abstract class AnyInterface : Declaration() {
    open val inherits : Identifier? = null
    abstract val members : List<Member>
}