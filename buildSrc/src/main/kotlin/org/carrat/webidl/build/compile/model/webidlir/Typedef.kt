package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.webidlir.types.Type

data class Typedef(
    val type : Type,
    override val identifier: Identifier
) : Declaration()