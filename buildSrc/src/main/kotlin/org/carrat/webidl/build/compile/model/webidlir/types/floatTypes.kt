package org.carrat.webidl.build.compile.model.webidlir.types

data class FloatType(
    val unrestricted : Boolean,
    val baseType: FloatBaseType
) : PrimitiveType()

enum class FloatBaseType {
    FLOAT,
    DOUBLE
}