package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.webidlir.Identifier

data class TypeReference(
    val identifier: Identifier
) : Type()
