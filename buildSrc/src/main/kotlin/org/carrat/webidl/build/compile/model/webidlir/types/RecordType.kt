package org.carrat.webidl.build.compile.model.webidlir.types

data class RecordType(
    val left : StringType,
    val right : Type
) : Type()