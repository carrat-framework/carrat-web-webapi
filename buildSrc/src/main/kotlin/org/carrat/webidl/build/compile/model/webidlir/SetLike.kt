package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.webidlir.types.Type

data class SetLike(
    val elementType : Type,
    val readOnly : Boolean
) : Member()