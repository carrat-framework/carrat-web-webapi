package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.webidlir.types.Type
import org.carrat.webidl.build.compile.model.webidlir.values.Value

data class DictionaryEntry(
    val required : Boolean,
    val type : Type,
    val identifier: Identifier,
    val defaultValue: Value?
) : Member()