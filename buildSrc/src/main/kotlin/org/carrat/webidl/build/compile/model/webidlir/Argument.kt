package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.webidlir.types.Type
import org.carrat.webidl.build.compile.model.webidlir.values.Value

data class Argument(
    val optional: Boolean,
    val type : Type,
    val vararg: Boolean,
    val argumentName: ArgumentName,
    val default : Value?
)