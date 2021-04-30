package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.MemberDeclaration
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

sealed class Iterable : WMember() {
    override fun WidlContext.toIr(declaration: String): MemberDeclaration? {
        return null;//TODO
    }

    data class CollectionIterable(
        val elementType: WTypeExpression
    ) : Iterable()

    data class MapIterable(
        val keyType: WTypeExpression,
        val valueType: WTypeExpression
    ) : Iterable()
}
