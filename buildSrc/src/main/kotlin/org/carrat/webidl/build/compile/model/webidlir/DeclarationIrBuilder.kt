package org.carrat.webidl.build.compile.model.webidlir

class DeclarationIrBuilder(
    val identifier: Identifier,
    val type: CitizenType,
    private val inherits : String?
){
    val members : MutableList<Member> = mutableListOf()

    fun build() : Declaration {
        return type.create(identifier, if (inherits != null) Identifier(inherits) else null, members)
    }
}
