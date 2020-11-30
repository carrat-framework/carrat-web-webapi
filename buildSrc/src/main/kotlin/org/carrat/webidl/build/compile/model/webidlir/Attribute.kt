package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.webidlir.types.Type

data class Attribute(
    val readOnly : Boolean,
    val inherited : Boolean,
    val type : Type,
    val name : AttributeName
) : Member()