package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.MemberDeclaration
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

sealed class AsyncIterable : WMember() {
    abstract val arguments: List<WArgument>?

    override fun WidlContext.toIr(declaration: String): MemberDeclaration? {
        return null;//TODO
    }

    data class CollectionIterable(
        val elementType: WTypeExpression,
        override val arguments: List<WArgument>?
    ) : AsyncIterable()

    data class MapIterable(
        val keyType: WTypeExpression,
        val valueType: WTypeExpression,
        override val arguments: List<WArgument>?
    ) : AsyncIterable()
}
