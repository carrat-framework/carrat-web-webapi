package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.webidlir.types.Type

sealed class Iterable : Member() {
    data class CollectionIterable(
        val elementType : Type
    ) : Iterable()

    data class MapIterable(
        val keyType : Type,
        val valueType : Type
    ) : Iterable()
}