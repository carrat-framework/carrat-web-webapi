package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.webidlir.types.Type
import org.carrat.webidl.build.compile.model.webidlir.values.Value

class Constant(
    val type: Type,
    val identifier: Identifier,
    val value: Value
) : Member()