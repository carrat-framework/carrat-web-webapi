package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.webidlir.types.Type

sealed class AsyncIterable : Member() {
    abstract val arguments : List<Argument>?

    data class CollectionIterable(
        val elementType : Type,
        override val arguments : List<Argument>?
    ) : AsyncIterable()

    data class MapIterable(
        val keyType : Type,
        val valueType : Type,
        override val arguments : List<Argument>?
    ) : AsyncIterable()
}