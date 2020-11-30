package org.carrat.webidl.build.compile.model.webidlir.types

data class IntegerType(
    val unsigned : Boolean,
    val baseType: IntegerBaseType
) : PrimitiveType()

enum class IntegerBaseType {
    SHORT,
    LONG,
    LONG_LONG
}