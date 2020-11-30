package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.webidlir.types.Type

data class MapLike(
    val keyType : Type,
    val valueType : Type,
    val readOnly : Boolean
) : Member()