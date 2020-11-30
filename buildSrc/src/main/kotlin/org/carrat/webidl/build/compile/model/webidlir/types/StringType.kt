package org.carrat.webidl.build.compile.model.webidlir.types

sealed class StringType : Type() {
    object ByteString : StringType()
    object DOMString : StringType()
    object USVString : StringType()
}