package org.carrat.webidl.build.compile.model.webidlir

sealed class Stringifier : Member() {
    data class AttributeStringifier(
        val attribute: Attribute
    ) : Stringifier()

    data class OperationStringifier(
        val operation: Operation
    ) : Stringifier()

    object EmptyStringifier : Stringifier()
}
