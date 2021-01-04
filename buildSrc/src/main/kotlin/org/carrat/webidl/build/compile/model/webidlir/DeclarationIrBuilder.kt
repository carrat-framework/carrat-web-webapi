package org.carrat.webidl.build.compile.model.webidlir

class DeclarationIrBuilder(
    val identifier: Identifier,
    val type: CitizenType,
    var inherits : String?
){
    val members : MutableList<Member> = mutableListOf()

    fun build() : Declaration {
        return type.create(identifier, if (inherits != null) Identifier(inherits!!) else null, members)
    }
}
