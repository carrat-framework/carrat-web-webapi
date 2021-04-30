package org.carrat.webidl.build.compile.parser

import org.carrat.webidl.build.compile.model.webidlir.CitizenType
import org.carrat.webidl.build.compile.model.webidlir.WDeclaration
import org.carrat.webidl.build.compile.model.webidlir.WMember

class IrDeclarationBuilder(
    val identifier: String,
    val type: CitizenType
) {
    var inherits: String? = null
    val members: MutableList<WMember> = mutableListOf()
    val includes: MutableList<String> = mutableListOf()

    fun build(): WDeclaration {
        return type.create("", identifier, inherits, includes, members)
    }
}
